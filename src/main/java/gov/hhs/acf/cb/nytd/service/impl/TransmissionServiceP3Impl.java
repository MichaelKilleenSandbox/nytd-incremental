package gov.hhs.acf.cb.nytd.service.impl;

import gov.hhs.acf.cb.nytd.actions.PaginatedSearch;
import gov.hhs.acf.cb.nytd.actions.compliance.ComplianceSearch;
import gov.hhs.acf.cb.nytd.actions.report.CohortSearch;
import gov.hhs.acf.cb.nytd.actions.report.OutcomesReportSearch;
import gov.hhs.acf.cb.nytd.actions.systemadmin.ExtendedDueDateSearch;
import gov.hhs.acf.cb.nytd.actions.transmission.TransmissionSearch;
import gov.hhs.acf.cb.nytd.dao.TransmissionDAO;
import gov.hhs.acf.cb.nytd.dao.impl.ExtendedDueDateDaoImpl;
import gov.hhs.acf.cb.nytd.models.*;
import gov.hhs.acf.cb.nytd.models.helper.FailedTransmissionDetail;
import gov.hhs.acf.cb.nytd.models.helper.VwExtendedDueDate;
import gov.hhs.acf.cb.nytd.models.helper.VwNote;
import gov.hhs.acf.cb.nytd.models.helper.VwTransmissionStatus;
import gov.hhs.acf.cb.nytd.service.*;
import gov.hhs.acf.cb.nytd.util.CommonFunctions;
import gov.hhs.acf.cb.nytd.util.Constants;
import gov.hhs.acf.cb.nytd.util.DateUtil;
import gov.hhs.acf.cb.nytd.util.UserRoleEnum;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.CalendarType;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Created by IntelliJ IDEA. User: 13873 Date: Jun 1, 2010
 */
//TODO: SonearQube - this class needs A LOT OF refactoring, run sonarqube report to see details.
public class TransmissionServiceP3Impl extends BaseServiceImpl implements TransmissionServiceP3 {
    private static final String FILE_TYPE_EXEPTION_STATUS = "Exited Not Expected File Category";

    private File activeSubmissionDir;
    private File inactiveSubmissionDir;
    private File nottoprocessDir;
    private File unprocessedDir;
    private File deletedDir;
    private File transmissionDir;
    private File toprocessDir;

    // services
    @Getter
    @Setter
    private LookupService lookupService;
    @Getter
    @Setter
    private MessageService messageService;

    //DAOs
    @Getter
    @Setter
    private TransmissionDAO transmissionDAO;
    @Getter
    @Setter
    private ExtendedDueDateService extendedDueDateService;

    public void setActiveSubmissionDir(String activeSubmissionDirURL) throws URISyntaxException {
        this.activeSubmissionDir = new File(new URI(activeSubmissionDirURL));
    }

    public void setInactiveSubmissionDir(String inactiveSubmissionDirURL) throws URISyntaxException {
        this.inactiveSubmissionDir = new File(new URI(inactiveSubmissionDirURL));
    }

    public void setTransmissionDir(String transmissionDirURL) throws URISyntaxException {
        this.transmissionDir = new File(new URI(transmissionDirURL));
    }

    public void setNottoprocessDir(String nottoprocessDirURL) throws URISyntaxException {
        this.nottoprocessDir = new File(new URI(nottoprocessDirURL));
    }

    public void setDeletedDir(String deletedDirURL) throws URISyntaxException {
        this.deletedDir = new File(new URI(deletedDirURL));
    }

    /**
     * @see TransmissionServiceP3#getToprocessDir()
     */
    @Override
    public File getToprocessDir() {
        return this.toprocessDir;
    }

    /**
     * Set toprocess directory
     *
     * @param toprocessDirURL as String
     * @throws URISyntaxException
     */
    public void setToprocessDir(String toprocessDirURL) throws URISyntaxException {
        this.toprocessDir = new File(new URI(toprocessDirURL));
    }

    /**
     * @see TransmissionServiceP3#getToprocessDir()
     */
    @Override
    public File getUnprocessedDir() {
        return this.unprocessedDir;
    }

    public void setUnprocessedDir(String unprocessedDirURL) throws URISyntaxException {
        this.unprocessedDir = new File(new URI(unprocessedDirURL));
    }

    @Override
    public void deleteTransmission(Long transmissionId, SiteUser siteUser) {
        Transmission trans = getHibernateSession().get(Transmission.class, transmissionId);
        deleteTransmission(trans, siteUser);
    }

    @Override
    public void deleteTransmission(Transmission transmission, SiteUser siteUser) {
        saveTransmissionDeleteLog(transmission, siteUser);
        getHibernateSession().delete(transmission);
    }

    @Override
    public Integer getCountBaselineYouth(Long transmissionId) {
        Query qry = getSessionFactory().getCurrentSession().getNamedQuery("countBaselineYouth");
        qry.setCacheable(true);
        qry.setParameter("transmissionId", transmissionId);
        Long result = (Long) qry.uniqueResult();

        return result.intValue();
    }

    @Override
    public Integer getCountFollowupYouth(Long transmissionId) {
        Query qry = getSessionFactory().getCurrentSession().getNamedQuery("countFollowupYouth");
        qry.setCacheable(true);
        qry.setParameter("transmissionId", transmissionId);
        Long result = (Long) qry.uniqueResult();

        return result.intValue();
    }

    @Override
    public Integer getCountServedYouth(Long transmissionId) {
        Query qry = getSessionFactory().getCurrentSession().getNamedQuery("countServedYouth");
        qry.setCacheable(true);
        qry.setParameter("transmissionId", transmissionId);
        Long result = (Long) qry.uniqueResult();

        return result.intValue();
    }

    @Override
    public Integer getCountTotalRecords(Long transmissionId) {
        Query qry = getSessionFactory().getCurrentSession().getNamedQuery("countTotalRecords");
        qry.setCacheable(true);
        qry.setParameter("transmissionId", transmissionId);
        Long result = (Long) qry.uniqueResult();

        return result.intValue();
    }

    public ReportingPeriod getCurrentReportingPeriod() {

        Query qry = getSessionFactory().getCurrentSession().getNamedQuery("getCurrentReportingPeriod");

        Object result = qry.uniqueResult();
        if (result != null) {
            return (ReportingPeriod) result;
        }

        return null;
    }

    /**
     * @see TransmissionServiceP3#getCurrentReportingPeriodForState(Long, String, Long)
     */
    @Override
    public ReportingPeriod getCurrentReportingPeriodForState(Long stateId, String fileType, Long reportingPeriodId) {
        // initialize default due date and extended due date
        Calendar defaultDueDate = new GregorianCalendar();
        Calendar dueDateForState = new GregorianCalendar();
        // use search model to get extended due date
        ExtendedDueDateSearch search = new ExtendedDueDateSearch();
        search.setState(String.valueOf(stateId));
        search.setReportingPeriod(String.valueOf(reportingPeriodId));
        // obtain list of extended due date view model
        List<VwExtendedDueDate> extendedDueDateList = extendedDueDateService.getExtendedDueDateData(search);
        for (VwExtendedDueDate extendedDueDate : extendedDueDateList) {
            if (extendedDueDate.getTransmissionTypeName().equals(fileType)) {
                defaultDueDate = extendedDueDate.getSubmissionDate();
                dueDateForState = extendedDueDate.getExtendedDueDate();
            }
        }
        // calculate days difference using ChronoUnit api
        long daysDifference = ChronoUnit.DAYS.between(
                defaultDueDate.getTime().toInstant(), dueDateForState.getTime().toInstant());
        log.info("daysDifference: " + daysDifference);
        // now construct named query
        Session session = null;
        SessionFactory sessionFactory = getSessionFactory();
        if (sessionFactory != null) {
            session = sessionFactory.getCurrentSession();
        } else {
            throw new IllegalStateException("session factory is null in getAllUserListByEmail()");
        }
        // current corrected period is a previous period of the current regular period need to
        // subtract days in current regular period on top of days difference on extended due date.
        String queryName = "getCurrentReportingPeriodForState";
        if (fileType.equals("Corrected")) {
            queryName = "getCurrentCorrectedReportingPeriodForState";
        }
        // adding default 45 days from endReportingDate
        Query qry = session.getNamedQuery(queryName);
        qry.setParameter("daysDiff", 45 + daysDifference);
        Object result = qry.uniqueResult();
        if (result != null) {
            return (ReportingPeriod) result;
        }

        return null;
    }

    @Override
    public TransmissionSearch getDashboardTransmissions(TransmissionSearch search) {
        // JPA 2.0
        Session dbSession = getSessionFactory().getCurrentSession();
        CriteriaBuilder criteriaBuilder = dbSession.getCriteriaBuilder();
        CriteriaQuery<VwTransmissionStatus> criteriaQuery = criteriaBuilder.createQuery(VwTransmissionStatus.class);
        Root<VwTransmissionStatus> root = criteriaQuery.from(VwTransmissionStatus.class);
        UserRoleEnum role = UserRoleEnum.getRole(search.getUser().getPrimaryUserRole().getName());

        // criteria based on primary user role
        switch (role) {
            case ADMIN:
            case FEDERAL:
                //JPA 2.0
                criteriaQuery.where(criteriaBuilder.lessThan(root.get("submittedDate"), Calendar.getInstance()));
                criteriaQuery.orderBy(criteriaBuilder.desc(root.get("submittedDate")));
                break;
            case REGIONAL:
                //JPA 2.0
                //Constructing list of parameters
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(criteriaBuilder.isNotNull(root.get("region")));
                predicates.add(criteriaBuilder.equal(root.get("region"), search.getUser().getRegion().getRegion()));
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("submittedDate"), Calendar.getInstance()));
                criteriaQuery.where(predicates.toArray(new Predicate[]{}));
                criteriaQuery.orderBy(criteriaBuilder.desc(root.get("submittedDate")));
                break;
            case STATE:
                //JPA 2.0
                criteriaQuery.where(criteriaBuilder.equal(root.get("state"), search.getUser().getState().getStateName()),
                        criteriaBuilder.notEqual(root.get("processingStatus"), FILE_TYPE_EXEPTION_STATUS));
                criteriaQuery.orderBy(criteriaBuilder.desc(root.get("fileReceivedDate")));
                break;
        }
        // dashboard results are limited to 5
        int maxResults = 5;
        //New JPA 2.0
        criteriaQuery.select(root);
        TypedQuery<VwTransmissionStatus> q = dbSession.createQuery(criteriaQuery);
        q.setFirstResult(0);
        q.setMaxResults(maxResults);

        //New JPA 2.0
        List<VwTransmissionStatus> vwTransmissionStatusList = q.getResultList();
        for (VwTransmissionStatus vwTransmissionStatus : vwTransmissionStatusList) {
            if (vwTransmissionStatus.getSubmittedDate() != null) {
                Calendar calSubmitted = vwTransmissionStatus.getSubmittedDate();
                vwTransmissionStatus.setStrSubmittedDate(DateUtil.formatDateAndTimezone(DateFormat.LONG, calSubmitted));
            }
            if (vwTransmissionStatus.getFileReceivedDate() != null) {
                Calendar calFileReceived = vwTransmissionStatus.getFileReceivedDate();
                vwTransmissionStatus.setStrFileReceivedDate(DateUtil.formatDateAndTimezone(DateFormat.LONG, calFileReceived));
            }
        }
        search.setPageResults(vwTransmissionStatusList);

        return search;
    }

    @Override
    public TransmissionSearch getDatumNotes(TransmissionSearch search) {
        Query qry = getSessionFactory().getCurrentSession().getNamedQuery("getDatumNotes");
        qry.setParameter("transmissionId", search.getTransmissionId());

        // return all records with datum notes
        List<VwNote> results = qry.list();

        // sort the results. we handle the sorting
        // here because the named query (native sql
        // optimized for performance) cannot accept
        // an order by clause dynamically
        final String sortColumn = search.getSortColumn();
        final PaginatedSearch.SortDirection sortDirection = search.getSortDirection();
        if (sortColumn != null) {
            results.sort(new NoteComparator(search));
            if (sortDirection == PaginatedSearch.SortDirection.DESC) {
                Collections.reverse(results);
            }
        }

        search.setPageResults(results);
        return search;
    }

    @Override
    public TransmissionSearch getElementNotes(TransmissionSearch search) {
        CriteriaBuilder criteriaBuilder = getSessionFactory().getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<ElementNote> criteriaQuery = criteriaBuilder.createQuery(ElementNote.class);
        Root<ElementNote> root = criteriaQuery.from(ElementNote.class);
        criteriaQuery.where(
                criteriaBuilder.equal(root.get("transmission").get("id"), search.getTransmissionId())
        );

        // add sort
        if (search.getSortColumn() != null) {
            if (search.getSortDirection() == PaginatedSearch.SortDirection.ASC) {
                criteriaQuery.orderBy(criteriaBuilder.asc(root.get(search.getSortColumn())));
            } else if (search.getSortDirection() == PaginatedSearch.SortDirection.DESC) {
                criteriaQuery.orderBy(criteriaBuilder.desc(root.get(search.getSortColumn())));
            }
        } else {
            // default sort is element id
            search.setSortColumn("id");
            search.setSortDirection(ComplianceSearch.SortDirection.ASC);
            criteriaQuery.orderBy(criteriaBuilder.asc(root.get("transmission").get("id")));
        }

        criteriaQuery.select(root);
        TypedQuery<ElementNote> q = getSessionFactory().getCurrentSession().createQuery(criteriaQuery);
        List results = q.getResultList();

        if (results != null) {
            search.setPageResults(results);
        }

        return search;
    }

    public TransmissionSearch getRecordNotes(TransmissionSearch search) {
        Query qry = getSessionFactory().getCurrentSession().getNamedQuery("getRecordNotes");
        qry.setParameter("transmissionId", search.getTransmissionId());

        // return all transmission records with notes
        List<TransmissionRecord> results = qry.list();

        // marshall record data to note objects
        List<VwNote> notes = new ArrayList<>();
        for (TransmissionRecord rec : results) {
            VwNote note = new VwNote();
            note.setRecordNumber(rec.getRecordNumber());
            note.setNoteText(rec.getNotes());
            notes.add(note);
        }

        // sort the results. we handle the sorting
        // here because the named query (native sql
        // optimized for performance) cannot accept
        // an order by clause dynamically
        final String sortColumn = search.getSortColumn();
        final PaginatedSearch.SortDirection sortDirection = search.getSortDirection();
        if (sortColumn == null) {

            search.setSortColumn("recordNumber");
            search.setSortDirection(PaginatedSearch.SortDirection.DESC);
        }
        if (sortColumn != null) {
            notes.sort(new NoteComparator(search));
            if (sortDirection == PaginatedSearch.SortDirection.DESC) {
                Collections.reverse(results);
            }
        }

        if (results != null) {
            search.setPageResults(notes);
        }
        return search;
    }

    @Override
    public Transmission getSubmission(State state, ReportingPeriod period) {
        Query qry = getHibernateSession().getNamedQuery("getSubmission");
        qry.setParameter("reportingPeriodId", period.getId());
        qry.setParameter("stateId", state.getId());

        Object result = qry.uniqueResult();
        if (result != null) {
            return (Transmission) result;
        }

        return null;
    }

    public Calendar getSubmissionDeadline(ReportingPeriod period, Long transmissionTypeId) {
        Calendar endDate = lookupService.getSubmissionDeadline(period, transmissionTypeId);
        Calendar deadline = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
        deadline.setTime(endDate.getTime());

        // default grace period is 45 days from the reporting period end date
        // if date falls on weekend move forward to Monday
        int dayOfWeek = deadline.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.SATURDAY) {
            deadline.add(Calendar.DATE, 2);
        } else if (dayOfWeek == Calendar.SUNDAY) {
            deadline.add(Calendar.DATE, 1);
        }
        // Adding a day since day has not yet passed
        deadline.add(Calendar.DATE, 1);
        return deadline;
    }

    public Calendar getSubmissionDeadline(ReportingPeriod period) {
        Calendar endDate = period.getEndReportingDate();
        Calendar deadline = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
        deadline.setTime(endDate.getTime());

        // default grace period is 45 days from the reporting period end date
        int gracePeriod = 45;
        deadline.add(Calendar.DATE, gracePeriod);

        // if date falls on weekend move forward to Monday
        int dayOfWeek = deadline.get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek) {
            case Calendar.SATURDAY:
                deadline.add(Calendar.DATE, 2);
                break;
            case Calendar.SUNDAY:
                deadline.add(Calendar.DATE, 1);
                break;
        }
        // Adding a day since day has not yet passed
        deadline.add(Calendar.DATE, 1);
        return deadline;
    }

    @Override
    public Transmission getTransmission(Long transmissionId) {
        Query qry = getSessionFactory().getCurrentSession().getNamedQuery("getTransmission");
        qry.setParameter("transmissionId", transmissionId);
        Object result = qry.uniqueResult();

        if (result != null) {
            return (Transmission) result;
        }

        return null;
    }

    @Override
    public List<Long> getTransmissionIds() {
        Query qry = getHibernateSession().getNamedQuery("getTransmissionIds");
        return qry.list();
    }

    @Override
    public Long getTransmissionIdOfInactiveStatus(Long stateId, Long reportingPeriodId) {
        Query qry = getHibernateSession().getNamedQuery("getTransmissionIdOfInactiveStatus");
        qry.setParameter("stateId", stateId);
        qry.setParameter("reportingPeriodId", reportingPeriodId);
        Long result = (Long) qry.uniqueResult();
        return result;
    }

    @Override
    public Long getStateId(String stateName) {
        Query qry = getHibernateSession().getNamedQuery("getStateId");
        Long result = (Long) qry.uniqueResult();
        return result;
    }

    @Override
    public String getElementNameById(Integer elementId) {
        Query qry = getHibernateSession().getNamedQuery("getElementNameById");
        qry.setParameter("elementId", elementId.longValue());
        String result = (String) qry.uniqueResult();
        return result;
    }

    @Override
    public List<TransmissionNote> getTransmissionNotes(Long transmissionId) {
        Query qry = getSessionFactory().getCurrentSession().getNamedQuery("getTransmissionNotes");
        qry.setParameter("transmissionId", transmissionId);

        List results = qry.list();
        if (results != null) {
            return (List<TransmissionNote>) results;
        }

        return null;
    }

    @Override
    public TransmissionRecord getYouthRecord(Long transmissionId, String recordNumber, Long datumId) {
        Datum datum = null;
        CriteriaBuilder criteriaBuilder = getSessionFactory().getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<Datum> criteriaQuery = criteriaBuilder.createQuery(Datum.class);
        Root<Datum> root = criteriaQuery.from(Datum.class);
        criteriaQuery.where(
                criteriaBuilder.equal(root.get("id"), datumId)
        );
        criteriaQuery.select(root);
        TypedQuery<Datum> q = getSessionFactory().getCurrentSession().createQuery(criteriaQuery);
        datum = q.getSingleResult();

        Query qry = getSessionFactory().getCurrentSession().getNamedQuery("getYouthRecord");
        qry.setParameter("transmissionId", transmissionId);
        qry.setParameter("recordNumber", recordNumber);
        qry.setParameter("transmissionRecordId", datum.getTransmissionRecord().getId());

        Object result = qry.uniqueResult();
        if (result != null) {
            return (TransmissionRecord) result;
        }

        return null;
    }

    @Override
    public boolean isAuthorized(SiteUser user, Long transmissionId) {
        // admins can view everything
        if (user.getPrimaryUserRole().getName().equals(Constants.SYSTEMADMIN)) {
            return true;
        }

        // fed users can view all transmissions
        if (user.getPrimaryUserRole().getName().equals(Constants.CBUSER)) {
            return true;
        }

        // load the transmission
        Transmission trans = getHibernateSession().get(Transmission.class, transmissionId);

        // authorize regional users
        if (user.getPrimaryUserRole().getName().equals(Constants.REGIONALUSER)) {
            return user.getRegion().getId().equals(trans.getState().getRegion().getId());
        }

        // authorize state users
        if (user.getPrimaryUserRole().getName().equals(Constants.STATEUSER)) {
            return user.getState().getId().equals(trans.getState().getId());
        }

        // default is deny access
        return false;
    }

    @Override
    public TransmissionSearch searchTransmissions(TransmissionSearch search, SiteUser user) {
        // create detahced criteria object to build the query
        DetachedCriteria criteria = DetachedCriteria.forClass(VwTransmissionStatus.class);

        // add only processing status is not a file type exception
        criteria.add(Restrictions.ne("processingStatus", FILE_TYPE_EXEPTION_STATUS));

        // add state name to criteria
        String stateName = search.getStateName();
        if (stateName != null && !stateName.isEmpty()) {
            String[] stateNames = stateName.split(";");
            if (stateNames.length > 0 && !(stateName.trim()).equalsIgnoreCase("All")) {
                criteria.add(Restrictions.in("state", stateNames));
            }
        }

        // add compliance status to criteria
        String complianceStatus = search.getComplianceStatus();
        if (complianceStatus != null
                && (!complianceStatus.isEmpty() && (!(complianceStatus.trim()).equalsIgnoreCase("All")))) {
            criteria.add(Restrictions.eq("complianceStatus", complianceStatus.trim()));
        }

        // add reporting period to the criteria
        String reportingPeriod = search.getReportingPeriod();
        if (reportingPeriod != null && !reportingPeriod.isEmpty()
                && (!(reportingPeriod.trim()).equalsIgnoreCase("All"))) {
            criteria.add(Restrictions.eq("reportingPeriod", reportingPeriod));
        }

        // checking for federal users and adding criteria to show only submission
        if (user != null && !user.getPrivileges().contains("canViewTransmissions")) {
            criteria.add(Restrictions.isNotNull("submissionStatus"));
            criteria.add(Restrictions.isNotNull("submittedDate"));

            // checking for federal users and adding criteria to show only
            // submission
            if (user != null && user.getPrivileges().contains("canViewFederalActiveSubmissions")
                    && search.isViewActiveSubmissionsOnly()) {
                criteria.add(Restrictions.disjunction().add(Restrictions.eq("submissionStatus", "Active")).add(
                        Restrictions.eq("submissionStatus", "active")));
            }

        }
        // NG fixing the bug no. 15479
        if (user != null && user.getPrivileges().contains("canViewTransmissions")
                && search.isViewSubmissionsOnly()) {
            criteria.add(Restrictions.isNotNull("submissionStatus"));
            criteria.add(Restrictions.isNotNull("submittedDate"));
        }
        // Adding option for States to see only active files
        else if (user != null && user.getPrivileges().contains("canViewTransmissions")
                && search.isViewActiveSubmissionsOnly()) {
            criteria.add(Restrictions.isNotNull("submissionStatus"));
            criteria.add(Restrictions.isNotNull("submittedDate"));
            criteria.add(Restrictions.disjunction().add(Restrictions.eq("submissionStatus", "Active")).add(
                    Restrictions.eq("submissionStatus", "active")));
        }
        // add check for file type
        String fileType = search.getFileType();
        if (fileType != null && !fileType.isEmpty() && (!(fileType.trim()).equalsIgnoreCase("All"))) {
            criteria.add(Restrictions.eq("transmissionType", fileType));
        }
        String fileNumber = search.getFileNumber();
        if (fileNumber != null && !fileNumber.isEmpty()) {
            criteria.add(Restrictions.eq("transmissionId", Long.parseLong(fileNumber)));
        }
        // add date range check for tranmissions or submissions.
        // viewSubmissionsOnly
        // is set in action code when user type is Federal or Regional
        // the date ranges are checked against the submittedDate column
        // for submissions. For tranmissions that are not submitted we
        // check the fileReceivedDate column
        String dateProperty = search.isViewSubmissionsOnly() ? "submittedDate" : "fileReceivedDate";
        String formStartDate = search.getTransmissionStartDate();
        String formEndDate = search.getTransmissionEndDate();
        if (formStartDate != null && !formStartDate.isEmpty()) {
            String dateRestriction;
            Calendar startDate = DateUtil.toCalendar(formStartDate);
            dateRestriction = String.format("trunc({alias}.%s) >= trunc(?)", dateProperty);
            criteria.add(Restrictions.sqlRestriction(dateRestriction, startDate, CalendarType.INSTANCE));
        }
        if (formEndDate != null && !formEndDate.isEmpty()) {
            String dateRestriction;
            Calendar endDate = DateUtil.toCalendar(formEndDate);
            dateRestriction = String.format("trunc({alias}.%s) <= trunc(?)", dateProperty);
            criteria.add(Restrictions.sqlRestriction(dateRestriction, endDate, CalendarType.INSTANCE));
        }

        // execute count query to return row count
        criteria.setProjection(Projections.rowCount());
        Criteria countCriteria = criteria.getExecutableCriteria(getSessionFactory().getCurrentSession());
        Long lRowCount = (Long) countCriteria.uniqueResult();
        search.setRowCount(lRowCount.intValue());
        criteria.setResultTransformer(Criteria.ROOT_ENTITY);
        criteria.setProjection(null);

        // add user sort
        if (search.getSortColumn() != null) {
            switch (search.getSortDirection()) {
                case ASC:
                    criteria.addOrder(Order.asc(search.getSortColumn()));
                    break;
                case DESC:
                    criteria.addOrder(Order.desc(search.getSortColumn()));
                    break;
            }
        } else {
            // default sort order
            criteria.addOrder(Order.desc("fileReceivedDate"));
            search.setSortColumn("fileReceivedDate");
            search.setSortDirection(PaginatedSearch.SortDirection.DESC);

        }

        // execute result query. limit results if page size > 0
        Criteria resultsCriteria = criteria.getExecutableCriteria(getSessionFactory().getCurrentSession());
        ExtendedDueDateDaoImpl.getPages(resultsCriteria, search.getPageSize(), search.getPage());

        List<VwTransmissionStatus> vwTransmissionStatusList = resultsCriteria.list();
        for (VwTransmissionStatus vwTransmissionStatus : vwTransmissionStatusList) {
            if (vwTransmissionStatus.getSubmittedDate() != null) {
                Calendar calSubmitted = vwTransmissionStatus.getSubmittedDate();
                vwTransmissionStatus.setStrSubmittedDate(DateUtil.formatDateAndTimezone(DateFormat.LONG, calSubmitted));
            }
            if (vwTransmissionStatus.getFileReceivedDate() != null) {
                Calendar calFileReceived = vwTransmissionStatus.getFileReceivedDate();
                vwTransmissionStatus.setStrFileReceivedDate(DateUtil.formatDateAndTimezone(DateFormat.LONG, calFileReceived));
            }
        }

        search.setPageResults(vwTransmissionStatusList);

        return search;
    }

    @Override
    public void submissionReminder() {
        ReportingPeriod currentReportingPeriod = getCurrentReportingPeriod();
        long days = getdifferenceOfDays(currentReportingPeriod);

        Query qry = getHibernateSession().getNamedQuery("submissionReminder");
        qry.setParameter("reportingPeriod", getCurrentReportingPeriod());

        List results = qry.list();
        if (results != null) {
            List<SiteUser> msgRecipients = (List<SiteUser>) results;

            // message parameters
            Map<String, Object> msgParams = new HashMap<>();
            msgParams.put("reportingPeriod", currentReportingPeriod.getName());
            msgParams.put("daysRemaining", String.valueOf(days));
            msgParams.put("cbEmailAddress", Constants.CBEMAILADDRESS);

            // send submission reminder message
            sendMessage(msgRecipients, MessageService.SUBMISSION_REMINDER, msgParams);

        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Transmission submitTransmission(Transmission transmission, SiteUser stateUser, Boolean ignoreWarning)
            throws SubmissionException {
        Calendar deadline = null;
        Calendar today = Calendar.getInstance();
        Long transmissionTypeId = transmission.getTransmissionType().getId();
        /*
         * // add 48 hours to deadline to account for saturday deadline //
         * transmission will not be late until midnight monday if
         * (deadline.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
         * deadline.add(Calendar.HOUR, 48); } // add 24 hours to deadline to
         * account for sunday deadline // transmission will not be late until
         * midnight monday else if (deadline.get(Calendar.DAY_OF_WEEK) ==
         * Calendar.SUNDAY) { deadline.add(Calendar.HOUR, 24); }
         */
        if (transmissionTypeId == 2L || transmissionTypeId == 3L) {
            deadline = getSubmissionDeadline(transmission.getReportingPeriod(), transmission
                    .getTransmissionType().getId());
            if (!ignoreWarning && transmission.getTransmissionType().getName().equals("Regular")
                    && today.after(deadline)) {
                SubmissionException se = new SubmissionException();
                se.setTransmission(transmission);
                throw se;
            }
        }
        // call the pl/sql function to submit the transmission
        Query qry = getSessionFactory().getCurrentSession().getNamedQuery("submitTransmission");
        qry.setParameter("transmissionId", transmission.getId());
        qry.setParameter("userId", stateUser.getId());
        qry.setParameter("createdDate", today);
        List<Transmission> results = qry.list();

        // determine the inactive submission returned by the query, if any
        Transmission oldSubmission = null;
        for (Transmission curr : results) {
            /*
             * Note: for some reason, submissionStatus is currently null for the
             * active submission by the stored procedure. Inactive submission
             * status is correct.
             */
            if (curr.getSubmissionStatus() != null
                    && curr.getSubmissionStatus().equalsIgnoreCase(Constants.TRANSMISSION_INACTIVE_SUBMISSION)) {
                oldSubmission = curr;
            }
        }

        // refresh the transmission after calling the function
        getHibernateSession().refresh(transmission);

        moveSubmittedDataFileOnFilesystem(transmission, oldSubmission);

        // create message parameters
        Map<String, Object> msgParams = new HashMap<>();
        msgParams.put("transmissionId", String.valueOf(transmission.getId()));
        msgParams.put("userName", stateUser.getUserName());
        msgParams.put("stateName", stateUser.getState().getStateName());
        msgParams.put("fileName", transmission.getFileName());
        msgParams.put("reportingPeriod", transmission.getReportingPeriod().getName());
        msgParams.put("dateTime", DateUtil.formatDateAndTimezone(DateFormat.LONG, transmission
                .getSubmittedDate()));
        msgParams.put("cbEmailAddress", Constants.CBEMAILADDRESS);
        msgParams.put("fileType", transmission.getTransmissionType().getName());
        msgParams.put("complianceStatus", transmission.getComplianceStatus());
        msgParams.put("systemGeneratedPenalty", transmission.getPotentialPenalty());
        msgParams.put("receivedDate", DateUtil.formatDateAndTimezone(DateFormat.LONG, transmission.getFileReceivedDate()));
        msgParams.put("fileProcessedDate", DateUtil.formatDateAndTimezone(DateFormat.LONG, transmission.getCreatedDate()));
        /** TO DO add total number of records in file **/
        int totalCount = this.getCountTotalRecords(transmission.getId());
        int totalServedYouth = this.getCountServedYouth(transmission.getId());
        int totalBaselineYouth = this.getCountBaselineYouth(transmission.getId());
        int totalFollowupYouth = this.getCountFollowupYouth(transmission.getId());
        msgParams.put("totalRecordsInFile", totalCount + " (Served:" + totalServedYouth + " Baseline:" + totalBaselineYouth + " Follow-Up:" + totalFollowupYouth + ")");

        List<SiteUser> msgRecipients = new ArrayList<>();

        // Submission Receipt is sent only to state users
        // adding state usrs
        msgRecipients.addAll(lookupService.getStateUsers(stateUser.getState()));

        List<SiteUser> stUser = new ArrayList<>();
        SiteUser user = null;
        Message systemMsg = null;
        Iterator<SiteUser> userItr = msgRecipients.iterator();
        while (userItr.hasNext()) {
            user = userItr.next();
            msgParams.remove("firstName");
            msgParams.remove("lastName");
            msgParams.put("firstName", user.getFirstName());
            msgParams.put("lastName", user.getLastName());
            systemMsg = messageService.createSystemMessage(MessageService.SUBMISSION_RECEIPT, msgParams);
            stUser.clear();
            stUser.add(user);
            // add users as recipients to message
            // and save message to database
            if (log.isDebugEnabled()) {
                log.debug("Sending submission receipt to: " + user);
            }
            messageService.sendRequiredSystemMessage(systemMsg, stUser);

        }

        /*
         * SUBMISSION NOTIFICATION is sent only to Federal, Regional and System
         * Admin users.
         */
        // adding System Admin
        msgRecipients.addAll(lookupService.getSystemAdminUsers());
        // adding Regional Users - Regional users
        List<SiteUser> regionUsers = lookupService.getRegionUsers(transmission.getState().getRegion(), transmission.getState());
        msgRecipients.addAll(regionUsers);
        // adding Federal Users - CB Central office users
        msgRecipients.addAll(lookupService.getFederalUsers());
        userItr = msgRecipients.iterator();
        while (userItr.hasNext()) {
            user = userItr.next();
            msgParams.remove("firstName");
            msgParams.remove("lastName");
            msgParams.put("firstName", user.getFirstName());
            msgParams.put("lastName", user.getLastName());
            systemMsg = messageService.createSystemMessage(MessageService.SUBMISSION_NOTIFICATION, msgParams);
            stUser.clear();
            stUser.add(user);
            // add users as recipients to message
            // and save message to database
            messageService.sendSystemMessage(systemMsg, stUser);

        }

        return transmission;
    }

    @Override
    public ReportingPeriod getReportingPeriod(Long reportingPeriodId) {
        Session session = getSessionFactory().getCurrentSession();
        Query query = session.getNamedQuery("getReportingPeriod");
        query.setParameter("reportPeriodId", reportingPeriodId);
        ReportingPeriod result = (ReportingPeriod) query.uniqueResult();
        return result;
    }

    @Override
    public State getState(Long stateId) {
        Session session = getSessionFactory().getCurrentSession();
        Query query = session.getNamedQuery("getState");
        query.setParameter("stateId", stateId);
        State result = (State) query.uniqueResult();
        return result;
    }

    /**
     * @see TransmissionServiceP3#updateTransmission(T)
     */
    public void updateTransmission(Transmission transmission) {
        transmissionDAO.updateTransmission(transmission);
    }

    @Override
    public List<FailedTransmissionDetail> getFailedTransmissionsReport(Map<String, String> searchCriteria) {
        log.debug("getFailedTransmissionsReport");
        int reportPeriodId = 0;
        int stateId = 0;
        int transmissionTypeId = 0;
        Calendar startDate = null;
        Calendar endDate = null;
        int sortOrder = Integer.parseInt(searchCriteria.get("SORTORDER"));
        int sortColumn = Integer.parseInt(searchCriteria.get("SORTCOLUMN"));
        String sortDirection = null;
        if (sortOrder == 0)
            sortDirection = "ASC";
        else
            sortDirection = "DESC";
        String exception = null;
        String failedTransmissionQuery = "Select   "
                + "Tr.Transmissionid As Transmissionid, St.stateName As State, Tr.Filereceiveddate, "
                + "Tt.Name As Filetype, Tr.Filename,  Pd.Name As Reason, "
                + "Tr.DataFileReportDateValue, Tr.DataFileStateValue, Tr.DataFileTransmissionTypeValue, "
                + "Pd.Problemdescriptionid, Tr.ProcessingException " + " From Transmission Tr "
                + "left outer Join Reportingperiod Rp On Tr.Reportingperiodid = Rp.Reportingperiodid "
                + "left outer Join Transmissiontype Tt On Tr.Transmissiontypeid = Tt.Transmissiontypeid "
                + "Inner Join Noncompliance Nc On Tr.Transmissionid = Nc.Transmissionid "
                + "Inner Join Error Er On Nc.Noncomplianceid = Er.Noncomplianceid "
                + "Inner Join Problemdescription Pd On Er.Problemdescriptionid = Pd.Problemdescriptionid "
                + "left outer Join State St On Tr.Stateid = St.Stateid " + "Where ";
        if (searchCriteria.get("REPORTPERIOD") != null && !searchCriteria.get("REPORTPERIOD").isEmpty()) {
            failedTransmissionQuery = failedTransmissionQuery + "Rp.Reportingperiodid = :RP And ";
            reportPeriodId = Integer.parseInt(searchCriteria.get("REPORTPERIOD"));
        }

        if (searchCriteria.get("STATE") != null && !searchCriteria.get("STATE").isEmpty()) {
            failedTransmissionQuery = failedTransmissionQuery + "St.Stateid = :STT And ";
            stateId = Integer.parseInt(searchCriteria.get("STATE"));
        }

        if (searchCriteria.get("STARTDATE") != null && !searchCriteria.get("STARTDATE").isEmpty()) {
            startDate = DateUtil.toCalendar(searchCriteria.get("STARTDATE"));
            failedTransmissionQuery = failedTransmissionQuery + "Tr.fileReceiveddate >= :SD And ";

        }

        if (searchCriteria.get("ENDDATE") != null && !searchCriteria.get("ENDDATE").isEmpty()) {
            endDate = DateUtil.toCalendar(searchCriteria.get("ENDDATE"));
            failedTransmissionQuery = failedTransmissionQuery + "Tr.fileReceiveddate <= :ED And ";

        }

        if (searchCriteria.get("FILETYPE") != null && !searchCriteria.get("FILETYPE").isEmpty()) {

            failedTransmissionQuery = failedTransmissionQuery + "Tt.Transmissiontypeid = :TT And ";
            transmissionTypeId = Integer.parseInt(searchCriteria.get("FILETYPE"));

        }

        failedTransmissionQuery = failedTransmissionQuery
                + "Nc.Compliancetype = 12  And Er.Compliancecategoryid In (1,2,10,11)" + "Order By ";

        switch (sortColumn) {
            case 1: {
                failedTransmissionQuery = failedTransmissionQuery + "St.Abbreviation " + sortDirection
                        + ", Tr.Filereceiveddate Desc";
                break;
            }
            case 2: {
                failedTransmissionQuery = failedTransmissionQuery + "Tt.Name " + sortDirection
                        + ", Tr.Filereceiveddate Desc";
                break;

            }
            case 3: {
                failedTransmissionQuery = failedTransmissionQuery + "Tr.Filename " + sortDirection
                        + ", Tr.Filereceiveddate Desc";
                break;
            }
            case 4: {
                failedTransmissionQuery = failedTransmissionQuery + "Pd.Name " + sortDirection
                        + ", Tr.Filereceiveddate Desc";
                break;
            }
            default: {
                failedTransmissionQuery = failedTransmissionQuery + "St.Abbreviation " + sortDirection
                        + ", Tr.Filereceiveddate Desc";
                break;
            }
        }

        List failedTransmissionsList = new ArrayList();
        List<FailedTransmissionDetail> results = new ArrayList<>();
        FailedTransmissionDetail failedTransmissionDetail = null;
        NativeQuery query = getSessionFactory().getCurrentSession().createNativeQuery(failedTransmissionQuery);
        if (reportPeriodId != 0)
            query.setParameter("RP", reportPeriodId);
        if (stateId != 0)
            query.setParameter("STT", stateId);
        if (startDate != null)
            query.setParameter("SD", startDate);
        if (endDate != null)
            query.setParameter("ED", endDate);
        if (transmissionTypeId != 0)
            query.setParameter("TT", transmissionTypeId);
        failedTransmissionsList = query.list();
        Iterator itr = failedTransmissionsList.iterator();
        if (itr != null) {
            while (itr.hasNext()) {
                failedTransmissionDetail = new FailedTransmissionDetail();
                Object[] detail = (Object[]) itr.next();
                // setting transmission id
                failedTransmissionDetail.setTrasmissionId(detail[0] != null ? detail[0].toString() : null);
                // setting state abbreviation
                failedTransmissionDetail.setStateAbbr(detail[1] != null ? detail[1].toString() : null);
                // setting file received date
                failedTransmissionDetail.setFileReceivedDate(detail[2] != null ? detail[2].toString() : null);
                // setting file type
                failedTransmissionDetail.setFileType(detail[3] != null ? detail[3].toString() : null);
                // setting file name
                failedTransmissionDetail.setFileName(detail[4] != null ? detail[4].toString() : null);
                // setting problem description
                if (detail[9] != null && Integer.parseInt(detail[9].toString()) == 80)
                    failedTransmissionDetail
                            .setReason("Properly formatted values are required for all Elements as defined in Technical Bulletin");
                else
                    failedTransmissionDetail.setReason(detail[5] != null ? detail[5].toString() : null);
                // setting report period mentioned in the xml file
                failedTransmissionDetail.setDataFileReportDateValue(detail[6] != null ? detail[6].toString()
                        : null);
                // setting state name mentioned in the xml file
                failedTransmissionDetail.setDataFileStateValue(detail[7] != null ? detail[7].toString() : null);
                // setting transmission Type mentioned in the xml file
                failedTransmissionDetail.setDataFileTransmissionTypeValue(detail[8] != null ? detail[8]
                        .toString() : null);
                failedTransmissionDetail.formatErrorMessage();
                // setting transmission processing exception
                exception = detail[10] != null ? detail[10].toString() : null;
                if (exception != null && exception.contains(":")) {
                    exception = exception.substring(exception.indexOf(':') + 1);
                }
                failedTransmissionDetail.setProcessingException(exception);
                results.add(failedTransmissionDetail);
            }
        } else
            results = null;

        return results;
    }

    public RecordToExport getBaselineCohortRecord(CohortSearch cohortSearch) {
        RecordToExport recordToExport = null;
        Session session = getSessionFactory().getCurrentSession();

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<RecordToExport> criteriaQuery = criteriaBuilder.createQuery(RecordToExport.class);
        Root<RecordToExport> root = criteriaQuery.from(RecordToExport.class);
        criteriaQuery.where(
                criteriaBuilder.equal(root.get("id"), cohortSearch.getR2EId())
        );
        criteriaQuery.select(root);
        TypedQuery<RecordToExport> q = session.createQuery(criteriaQuery);
        recordToExport = q.getSingleResult();

        return recordToExport;
    }

    public RecordToExport getCohortSet19Record(CohortSearch cohortSearch) {
        RecordToExport recordToExport = null;
        Session session = getSessionFactory().getCurrentSession();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<RecordToExport> criteriaQuery = criteriaBuilder.createQuery(RecordToExport.class);
        Root<RecordToExport> root = criteriaQuery.from(RecordToExport.class);
        Integer stateId = cohortSearch.getSelectedState();
        criteriaQuery.where(
                criteriaBuilder.equal(root.get("stateId"), stateId.longValue()),
                criteriaBuilder.equal(root.get("e3RecordNumber"), cohortSearch.getRecordNumber()),
                criteriaBuilder.and(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("reportingPeriod")),
                                cohortSearch.getSelectedReportPeriodName().toLowerCase()
                        )
                )
        );
        criteriaQuery.select(root);
        TypedQuery<RecordToExport> q = session.createQuery(criteriaQuery);
        recordToExport = q.getSingleResult();

        return recordToExport;
    }

    @Override
    public RecordToExport getOutcomesUniverseNotReportedRecord(
            OutcomesReportSearch search) {
        RecordToExport recordToExport = null;
        Session session = getSessionFactory().getCurrentSession();
        Integer stateId = search.getStateId().intValue();
        Object[] rpNameArray = new Object[]{search.getBaselineRPName(), search.getPostBufferRPName()};
        List<Object> rpNameList = Arrays.asList(rpNameArray);
        Object[] opArray = new Object[]{"Baseline", "Post-buffer"};
        List<Object> opList = Arrays.asList(opArray);
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<RecordToExport> criteriaQuery = criteriaBuilder.createQuery(RecordToExport.class);
        Root<RecordToExport> root = criteriaQuery.from(RecordToExport.class);

        if (search.getOutcomeAge() == 19) {
            criteriaQuery.where(
                    criteriaBuilder.equal(root.get("stateId"), stateId.longValue()),
                    criteriaBuilder.equal(root.get("e3RecordNumber"), search.getRecordNumber()),
                    root.get("reportingPeriod").in(rpNameList),
                    root.get("outcomePopulation").in(opList)
            );
            criteriaQuery.select(root);
            TypedQuery<RecordToExport> q = session.createQuery(criteriaQuery);
            try {
                recordToExport = q.getSingleResult();
            } catch (Exception e) {
                log.debug(e.getMessage());
                recordToExport = null;
            }
        }
        if (search.getOutcomeAge() == 21) {
            if (search.getFollowup19RPName() != null) {
                criteriaQuery.where(
                        criteriaBuilder.equal(root.get("stateId"), stateId.longValue()),
                        criteriaBuilder.equal(root.get("e3RecordNumber"), search.getRecordNumber()),
                        criteriaBuilder.equal(root.get("reportingPeriod"), search.getFollowup19RPName())
                );
                criteriaQuery.select(root);
                TypedQuery<RecordToExport> q = session.createQuery(criteriaQuery);
                try {
                    recordToExport = q.getSingleResult();
                } catch (Exception e) {
                    log.debug(e.getMessage());
                    recordToExport = null;
                }

                if (recordToExport == null) {
                    StringBuilder followup19RPName = new StringBuilder(search.getFollowup19RPName());
                    search.setFollowup19RPName(null);
                    recordToExport = this.getOutcomesUniverseNotReportedRecord(search);
                    search.setFollowup19RPName(followup19RPName.toString());
                }
            } else {
                criteriaQuery.where(
                        criteriaBuilder.equal(root.get("stateId"), stateId.longValue()),
                        criteriaBuilder.equal(root.get("e3RecordNumber"), search.getRecordNumber()),
                        root.get("reportingPeriod").in(rpNameList),
                        root.get("outcomePopulation").in(opList)
                );
                criteriaQuery.select(root);
                TypedQuery<RecordToExport> q = session.createQuery(criteriaQuery);
                try {
                    recordToExport = q.getSingleResult();
                } catch (Exception e) {
                    log.debug(e.getMessage());
                    recordToExport = null;
                }
            }
        }

        return recordToExport;
    }

    public String getDueDateofTransmission(Long transmissionId) {
        StringBuilder queryStr = new StringBuilder().append("select to_char(dd.submissiondate,'MM/dd/YYYY') from duedate dd inner join ")
                .append(" reportingperiod rp on dd.reportingperiodid = rp.reportingperiodid ")
                .append(" inner join  transmission tr using (transmissiontypeid) ")
                .append(" where  rp.reportingperiodid = tr.reportingperiodid and tr.transmissionid = :transmissionId");

        Query query = getSessionFactory().getCurrentSession().createSQLQuery(queryStr.toString());
        query.setParameter("transmissionId", transmissionId);
        String dueDateStr = (String) query.uniqueResult();

        return dueDateStr;
    }

    public boolean isCohortSampled(Long transmissionid) {
        boolean isSampled = false;

        Query qry = getSessionFactory().getCurrentSession().getNamedQuery("isCohortSampled");
        qry.setParameter("transmissionId", transmissionid);
        Object count = qry.uniqueResult();
        if (count != null && Long.valueOf(String.valueOf(count)) > 0) isSampled = true;

        return isSampled;
    }

    public String isWithinRegularReportPeriod(Long transmissionid) {

        Query qry = getSessionFactory().getCurrentSession().getNamedQuery("isWithinRegularReportPeriod");
        qry.setParameter("transmissionId", transmissionid);
        qry.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        Integer iswithinrp = 0;

        Map results = (Map) qry.uniqueResult();
        iswithinrp = Integer.valueOf(results.get("iswithinrp").toString());
        return iswithinrp != 1 ? results.get("duedatestr").toString() : null;
    }

    public Float getPenaltyAmtForInactiveRegularFile(Long correctedFileTransmissionid, Long stateid, Long reportingperiodid) {
        Float penaltyAmt = 0.0f;

        Query qry = getSessionFactory().getCurrentSession().getNamedQuery("getPenaltyAmtForInactiveRegularFile");
        qry.setParameter("CORRECTEDFILETRANSID", correctedFileTransmissionid);
        qry.setParameter("STATEID", stateid);
        qry.setParameter("REPORTINGPERIODID", reportingperiodid);
        penaltyAmt = (Float) qry.uniqueResult();

        return penaltyAmt;
    }

    public boolean isTransmissionActive(String transmissionId) {
        return StringUtils.equalsIgnoreCase(Constants.TRANSMISSION_ACTIVE_SUBMISSION,
                transmissionDAO.getSubmissionStatus(Long.valueOf(transmissionId)));
    }

    public String getTransmissionType(String transmissionId) {
        return transmissionDAO.getTransmissionType(Long.valueOf(transmissionId));
    }

    public boolean isCompliant(String transmissionId) {
        return StringUtils.equalsIgnoreCase(Constants.COMPLIANT,
                transmissionDAO.getComplianceStatus(Long.valueOf(transmissionId)));
    }

    @Override
    public boolean stateHasActiveTransmission(State state, Long reportingPeriod) {
        List<Transmission> activeTransmissions = transmissionDAO.getActiveSubmittedTransmissions(state, reportingPeriod);

        return !activeTransmissions.isEmpty();
    }

    @Override
    public boolean stateHasActiveTransmission(State state) {
        //List<Transmission> activeTransmissions = transmissionDAO.getActiveSubmittedTransmissions(state);

        return false;
    }

    private long getdifferenceOfDays(ReportingPeriod currentReportingPeriod) {
        Calendar submissionDeadline = getSubmissionDeadline(currentReportingPeriod);
        Calendar today = Calendar.getInstance();
        long delta = submissionDeadline.getTimeInMillis() - today.getTimeInMillis();
        long millisPerDay = 1000L * 60L * 60L * 24L;
        long days = delta / millisPerDay;
        return days;
    }

    private String moveDeletedDataFileOnFilesystem(Transmission transmission) {
        String description = "";
        assert (transmission != null);

        try {
            CommonFunctions.mkdir(inactiveSubmissionDir);
            CommonFunctions.mkdir(transmissionDir);
            CommonFunctions.mkdir(unprocessedDir);
            CommonFunctions.mkdir(nottoprocessDir);
            CommonFunctions.mkdir(deletedDir);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            e.printStackTrace();
            return "create directory exception";
        }

        File deletedFile = new File(deletedDir, transmission.getFileName());
        File processedFile = new File(transmissionDir, transmission.getFileName());
        File unprocessedFile = new File(unprocessedDir, transmission.getFileName());
        File inactiveFile = new File(inactiveSubmissionDir, transmission.getFileName());
        File nottoprocessFile = new File(nottoprocessDir, transmission.getFileName());

        // move the processed data file to the deleted directory in case it exists
        if (processedFile.exists()) {
            try {
                FileUtils.moveFileToDirectory(processedFile, deletedDir, false);
                description = String.format("During deletion, file was found in the directory (processed) and moved to the deleted folder (%s):", deletedDir.getAbsolutePath());
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        } else if (unprocessedFile.exists()) {
            try {
                FileUtils.moveFileToDirectory(unprocessedFile, deletedDir, false);
                description = String.format("During deletion, file was found in the directory (unprocessed) and moved to the deleted folder (%s):", deletedDir.getAbsolutePath());
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        } else if (inactiveFile.exists()) {
            try {
                FileUtils.moveFileToDirectory(inactiveFile, deletedDir, false);
                description = String.format("During deletion, file was found in the directory (inactiveFilesSubmitted) and moved to the deleted folder (%s):", deletedDir.getAbsolutePath());
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        } else if (nottoprocessFile.exists()) {
            try {
                FileUtils.moveFileToDirectory(nottoprocessFile, deletedDir, false);
                description = String.format("During deletion, file was found in the directory (nottoprocess) and moved to the deleted folder (%s):", deletedDir.getAbsolutePath());
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        } else {
            description = String.format("During deletion, file was not found in any directory (processed, unprocessed,inactiveFilesSubmitted) to move to the deleted folder (%s):", deletedDir.getAbsolutePath());
        }

        return description;
    }

    private void moveSubmittedDataFileOnFilesystem(Transmission newSubmission, Transmission oldSubmission) {
        assert (newSubmission != null);

        try {
            CommonFunctions.mkdir(activeSubmissionDir);
            CommonFunctions.mkdir(inactiveSubmissionDir);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return;
        }

        File newSubmissionFile = new File(transmissionDir, newSubmission.getFileName());
        File oldSubmissionFile;

        // move the current active transmission, if any, to the inactive
        // submission directory
        if (oldSubmission != null) {
            oldSubmissionFile = new File(activeSubmissionDir, oldSubmission.getFileName());
            if (!oldSubmissionFile.exists()) {
                log.error(String.format(
                        "During submission, file for newly inactive submission %d (%s) not found at %s.",
                        oldSubmission.getId(), oldSubmission.getFileName(), activeSubmissionDir.getAbsolutePath()));
                String currDesc = newSubmission.getDescription();
                if (currDesc != null && !currDesc.isEmpty()) {
                    currDesc = currDesc + "; ";
                } else {
                    currDesc = "";
                }
                oldSubmission.setDescription(String.format(
                        "%sDuring submission, newly inactive file not moved from %s to %s", currDesc,
                        activeSubmissionDir.getAbsolutePath(), inactiveSubmissionDir.getAbsolutePath()));
                getSessionFactory().getCurrentSession().save(oldSubmission);
            }
            try {
                FileUtils.moveFileToDirectory(oldSubmissionFile, inactiveSubmissionDir, false);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }

        // move the data file to the active submission directory
        if (!newSubmissionFile.exists()) {
            log.error(String.format("During submission, file %d (%s) not found at %s.", newSubmission.getId(),
                    newSubmission.getFileName(), transmissionDir.getAbsolutePath()));
            String currDesc = newSubmission.getDescription();
            if (currDesc != null && !currDesc.isEmpty()) {
                currDesc = currDesc + "; ";
            } else {
                currDesc = "";
            }
            newSubmission.setDescription(String.format("%sDuring submission, file not moved from %s to %s",
                    currDesc, transmissionDir.getAbsolutePath(), activeSubmissionDir.getAbsolutePath()));
            getSessionFactory().getCurrentSession().save(newSubmission);
        }
        try {
            FileUtils.moveFileToDirectory(newSubmissionFile, activeSubmissionDir, false);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void saveTransmissionDeleteLog(Transmission transmission, SiteUser siteUser) {
        TransmissionDeleteLog transmissionDeleteLog = new TransmissionDeleteLog();
        transmissionDeleteLog.setTransmissionId(transmission.getId());
        transmissionDeleteLog.setTransmissionType(transmission.getTransmissionType());
        transmissionDeleteLog.setComplianceStatus(transmission.getComplianceStatus());
        transmissionDeleteLog.setFileName(transmission.getFileName());
        transmissionDeleteLog.setFileReceivedDate(transmission.getFileReceivedDate());
        transmissionDeleteLog.setFileSize(transmission.getFileSize());
        transmissionDeleteLog.setState(transmission.getState());
        transmissionDeleteLog.setRecordCount(transmission.getRecordsCnt());
        transmissionDeleteLog.setReportingPeriod(transmission.getReportingPeriod());
        transmissionDeleteLog.setDeletedDate(Calendar.getInstance());
        transmissionDeleteLog.setSiteUser(siteUser);
        transmissionDeleteLog.setDeletedBy(siteUser.getFirstName() + " " + siteUser.getLastName());
        transmissionDeleteLog.setRecordCount(transmission.getRecordsCnt());
        transmissionDeleteLog.setDescription(moveDeletedDataFileOnFilesystem(transmission));
        getHibernateSession().save(transmissionDeleteLog);
    }

    private void sendMessage(List<SiteUser> users, String msg, Map<String, Object> msgParams) {
        List<SiteUser> stateUser = new ArrayList<>();
        SiteUser user = null;
        Message systemMsg = null;
        for (SiteUser siteUser : users) {
            user = siteUser;
            msgParams.remove("firstName");
            msgParams.remove("lastName");
            msgParams.put("firstName", user.getFirstName());
            msgParams.put("lastName", user.getLastName());
            systemMsg = messageService.createSystemMessage(msg, msgParams);
            stateUser.clear();
            stateUser.add(user);
            // add users as recipients to message
            // and save message to database
            messageService.sendSystemMessage(systemMsg, stateUser);
        }

    }

    private class NoteComparator implements Comparator<VwNote> {
        private final TransmissionSearch search;

        public NoteComparator(TransmissionSearch search) {
            this.search = search;
        }

        @Override
        public int compare(VwNote o1, VwNote o2) {
            try {
                // use reflection to compare the string values
                // of the specified sort column. because the fields
                // are marked private need to call setAccessible
                Class clazz = VwNote.class;
                Field o1Field = clazz.getDeclaredField(search.getSortColumn());
                o1Field.setAccessible(true);
                Object o1Value = o1Field.get(o1);

                Field o2Field = clazz.getDeclaredField(search.getSortColumn());
                o2Field.setAccessible(true);
                Object o2Value = o2Field.get(o2);

                if (o1Value == null && o2Value == null) {
                    return 0;
                }
                if (o1Value == null) {
                    return -1;
                }
                if (o2Value == null) {
                    return 1;
                }
                return ((String) o1Value).compareTo((String) o2Value);
            } catch (Exception e) {
                // return equality on exceptions (should never occur)
                e.printStackTrace(System.out);
                return 0;
            }
        }
    }


}
