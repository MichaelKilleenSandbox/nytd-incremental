package gov.hhs.acf.cb.nytd.service.impl;

import gov.hhs.acf.cb.nytd.actions.PaginatedSearch.SortDirection;
import gov.hhs.acf.cb.nytd.actions.message.MessageSearch;
import gov.hhs.acf.cb.nytd.dao.impl.ExtendedDueDateDaoImpl;
import gov.hhs.acf.cb.nytd.models.*;
import gov.hhs.acf.cb.nytd.service.MessageService;
import gov.hhs.acf.cb.nytd.util.DateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.hibernate.query.Query;
import org.hibernate.type.CalendarType;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.text.DateFormat;
import java.util.*;

/**
 * Message service implementation class.
 * User: 13873
 * Date: Jun 16, 2010
 *
 * @see MessageService
 */
//TODO: SonearQube - many fixed and some high risk items to break code (i.e.) query needs to move to DAO etc.
// need to be refactoring, run sonarqube report to see details.
public class MessageServiceImpl extends BaseServiceImpl implements MessageService {
    private static final String CREATED_DATE = "createdDate";
    // Spring mail sender used to send email notifications
    JavaMailSender mailSender;
    // template email message
    SimpleMailMessage mailTemplate;

    /**
     * @see MessageService#createSystemMessage(String, Map<String, Object>)
     */
    public Message createSystemMessage(String type, Map<String, Object> namedParams) {
        // create the system message
        Message msg = new Message();
        prepareSystemMessage(msg);
        SystemGeneratedMessage msgTemplate = getMessageTemplate(type);

        String msgSubject = msgTemplate.getSubject();
        String msgBody = msgTemplate.getSystemMessageBody();
        if (namedParams != null) {
            msgSubject = msgTemplate.formatText(msgSubject, namedParams);
            msgBody = msgTemplate.formatText(msgBody, namedParams);
        }

        msg.setSubject(msgSubject);
        msg.setMessageBody(msgBody);
        msg.setDescription(msgTemplate.getDescription());

        return msg;
    }

    /**
     * @see MessageService#prepareSystemMessage(Message)
     */
    public Message prepareSystemMessage(Message systemMsg) {
        NYTDSystem nytdSystem = new NYTDSystem();
        systemMsg.setMessageFrom(nytdSystem.getName());
        systemMsg.setSignature(nytdSystem.getSignature());
        systemMsg.setBeforeSignatureWord(nytdSystem.getBeforSignatureWord());

        return systemMsg;
    }

    /**
     * @see MessageService#search(MessageSearch)
     */
    //TODO: there is no replacement for setResultTransformer() until Hibernate 6.0
    //http://wiki.openbravo.com/wiki/Hibernate_5.3_Migration_Guide#org.hibernate.query.Query.setResultTransformer.28.29
    //TODO: SonarQube - Refactor this method to reduce its Cognitive Complexity from 19 to the 15 allowed.
    @SuppressWarnings("deprecation")
    public MessageSearch search(MessageSearch search) {
        log.info("TODO: No replacement of setResultTransformer() available until Hibernate 6.0");
        // create the criteria object
        DetachedCriteria criteria = DetachedCriteria.forClass(Message.class);
        // restrict messages to currently logged-in user
        DetachedCriteria userCriteria = criteria.createCriteria("recipients");
        userCriteria.add(Restrictions.eq("id", search.getUser().getId()));

        // check for matching text in the message subject and body
        if (search.getText() != null && !search.getText().isEmpty()) {
            Disjunction disjunction = Restrictions.disjunction();
            criteria.add(disjunction);
            for (String token : search.getText().split(" ")) {
                disjunction.add(Restrictions.like("subject", "%" + token + "%").ignoreCase());
                disjunction.add(Restrictions.like("messageBody", "%" + token + "%").ignoreCase());
            }
        }

        // constrain messages to a specific date range
        String formStartDate = search.getMessageCreatedStartDate();
        String formEndDate = search.getMessageCreatedEndDate();
        if (formStartDate != null && !formStartDate.isEmpty()) {
            String startDateRestriction;
            Calendar startDate = DateUtil.toCalendar(formStartDate);
            startDateRestriction = String.format("trunc({alias}.%s) >= trunc(?)", CREATED_DATE);
            criteria.add(Restrictions.sqlRestriction(startDateRestriction, startDate, CalendarType.INSTANCE));
        }
        if (formEndDate != null && !formEndDate.isEmpty()) {
            String endDateRestriction;
            Calendar endDate = DateUtil.toCalendar(formEndDate);
            endDateRestriction = String.format("trunc({alias}.%s) <= trunc(?)", CREATED_DATE);
            criteria.add(Restrictions.sqlRestriction(endDateRestriction, endDate, CalendarType.INSTANCE));
        }

        // execute count query to return row count
        criteria.setProjection(Projections.rowCount());
        Criteria countCriteria = criteria.getExecutableCriteria(getSessionFactory().getCurrentSession());
        Long lRowCount = (Long) countCriteria.uniqueResult();
        search.setRowCount(lRowCount.intValue());
        criteria.setProjection(null);
        criteria.setResultTransformer(Criteria.ROOT_ENTITY);

        // add user sort (default sort is message created date)
        if (search.getSortColumn() != null) {
            switch (search.getSortDirection()) {
                case ASC:
                    criteria.addOrder(Order.asc(search.getSortColumn()));
                    break;
                case DESC:
                    criteria.addOrder(Order.desc(search.getSortColumn()));
                    break;
                default:
            }
        } else {
            criteria.addOrder(Order.desc(CREATED_DATE));
            search.setSortColumn(CREATED_DATE);
            search.setSortDirection(SortDirection.DESC);
        }

        // execute result query. limit results if page size > 0
        Criteria resultsCriteria = criteria.getExecutableCriteria(getSessionFactory().getCurrentSession());
        ExtendedDueDateDaoImpl.getPages(resultsCriteria, search.getPageSize(), search.getPage());

        // add transient message properties
        List<Message> messages = resultsCriteria.list();
        for (Message message : messages) {
            if (message.getCreatedDate() != null) {
                Calendar cal = message.getCreatedDate();
                message.setMessageCreatedDate(DateUtil.formatDateAndTimezone(DateFormat.LONG, cal));
            }
            prepareSystemMessage(message);
        }

        // return results
        search.setPageResults(messages);
        return search;
    }

    /**
     * @see MessageService#sendEmailNotification(Message List<String>)
     */
    @Override
    public void sendEmailNotification(Message systemMsg, List<String> emailAddresses) {
        boolean isHtml = true;
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper messageBuilder = new MimeMessageHelper(mimeMessage);
            for (String to : emailAddresses) {
                messageBuilder.addTo(new InternetAddress(to));
            }
            messageBuilder.setFrom(mailTemplate.getFrom());
            messageBuilder.setSubject(systemMsg.getSubject());
            StringBuilder messageText = new StringBuilder();
            messageText.append(systemMsg.getMessageBody());
            messageText.append("<br><br>");
            messageText.append(systemMsg.getBeforeSignatureWord()).append(",");
            messageText.append("<br>");
            messageText.append(systemMsg.getSignature());
            messageBuilder.setText(messageText.toString(), isHtml);

            mailSender.send(messageBuilder.getMimeMessage());

        }
        catch (MessagingException e) {
            log.error("Messaging Exception in sendEmailNotification: " + e.getMessage(), e);
        }
        catch (MailException e) {
            log.error("Mail Exception in sendEmailNotification: " + e.getMessage(), e);
        }
    }

    /**
     * @see MessageService#sendSystemMessage(Message SiteUser)
     */
    @Override
    public void sendSystemMessage(Message msg, SiteUser recipients) {
        List<SiteUser> siteUserList = new ArrayList<>();
        siteUserList.add(recipients);
        sendSystemMessage(msg,siteUserList);
    }

    /**
     * @see MessageService#sendSystemMessage(Message List<SiteUser>)
     */
    @Override
    public void sendSystemMessage(Message msg, List<SiteUser> recipients) {
        Session session = getSessionFactory().getCurrentSession();
        // save messsage
        msg.setCreatedDate(new GregorianCalendar());
        session.saveOrUpdate(msg);

        // create list of email addresses of users requesting email notification
        // while saving each recipient to the database
        List<String> emailAddresses = new ArrayList<>();
        for (SiteUser user : recipients) {
            MessageRecipient recipient = new MessageRecipient();
            recipient.setSiteUser(user);
            recipient.setMessage(msg);
            recipient.setUpdateDate(Calendar.getInstance());
            session.saveOrUpdate(recipient);

            // add user to email address list if email notification requested
            if (!user.isDeleted() && user.isReceiveEmailNotifications() && user.getEmailAddress() != null) {
                emailAddresses.add(user.getEmailAddress());
            }
        }

        // send e-mail notification to users requesting it
        if (!emailAddresses.isEmpty()) {
            sendEmailNotification(msg, emailAddresses);
        }
    }


    /**
     * @see MessageService#sendSystemMessage(Message List<SiteUser>)
     */
    @Override
    public void sendRequiredSystemMessage(Message msg, List<SiteUser> recipients) {
        Session session = getSessionFactory().getCurrentSession();
        msg.setCreatedDate(new GregorianCalendar());
        session.saveOrUpdate(msg);

        recipients.stream()
                .filter(Objects::nonNull)
                .filter(u -> !u.isDeleted())
                .filter(u -> u.getEmailAddress() != null)
                .forEach(u -> saveAndSendEmail(u, msg));

    }

    private void saveAndSendEmail(SiteUser user, Message msg) {
        Session session = getSessionFactory().getCurrentSession();
        MessageRecipient recipient = new MessageRecipient();
        recipient.setSiteUser(user);
        recipient.setMessage(msg);
        session.saveOrUpdate(recipient);

        sendEmailNotification(msg, Collections.singletonList(user.getEmailAddress()));
    }

    /**
     * @see MessageService#sendMessage(Message List<SiteUser>)
     */
    public Message sendMessage(Message msg, List<SiteUser> recipients) {
        Session session = getSessionFactory().getCurrentSession();
        // save messsage
        msg.setCreatedDate(new GregorianCalendar());
        session.saveOrUpdate(msg);

        // create list of email addresses of users requesting email notification
        // while saving each recipient to the database
        List<String> emailAddresses = new ArrayList<>();
        for (SiteUser user : recipients) {
            MessageRecipient recipient = new MessageRecipient();
            recipient.setSiteUser(user);
            recipient.setMessage(msg);
            session.saveOrUpdate(recipient);

            // add user to email address list if email notification requested
            if (user.isReceiveEmailNotifications() && user.getEmailAddress() != null && !(user.isDeleted())) {
                emailAddresses.add(user.getEmailAddress());
            }
        }

        // send e-mail notification to users requesting it
        if (!emailAddresses.isEmpty()) {
            sendEmailNotification(msg, emailAddresses);
        }

        return msg;
    }

    /**
     * @see MessageService#getMessage(Long)
     */
    @Override
    public Message getMessage(Long messageId) {
        Session session = getSessionFactory().getCurrentSession();
        Query query;
        String queryString;
        Message result;

        queryString = "select message " + "from Message as message " + "where message.id = :messageId";
        query = session.createQuery(queryString).setParameter("messageId", messageId);
        result = (Message) query.uniqueResult();

        if (result.getCreatedDate() != null) {
            Calendar cal = result.getCreatedDate();
            result.setMessageCreatedDate(DateUtil.formatDateAndTimezone(DateFormat.LONG, cal));
        }

        return result;
    }

    /**
     * Get message template based on type.
     *
     * @param type as String
     * @return SystemGeneratedMessage object
     */
    private SystemGeneratedMessage getMessageTemplate(String type) {
        // prepare named query
        Query qry = getSessionFactory().getCurrentSession().getNamedQuery("getSystemGeneratedMessage");
        qry.setParameter("type", type);

        // load message template
        SystemGeneratedMessage msgTemplate = (SystemGeneratedMessage) qry.uniqueResult();

        return msgTemplate;
    }

    /**
     * @return the mailSender
     */
    public JavaMailSender getMailSender() {
        return mailSender;
    }

    /**
     * @param mailSender the mailSender to set
     */
    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * @return the mailTemplate
     */
    public SimpleMailMessage getMailTemplate() {
        return mailTemplate;
    }

    /**
     * @param mailTemplate the mailTemplate to set
     */
    public void setMailTemplate(SimpleMailMessage mailTemplate) {
        this.mailTemplate = mailTemplate;
    }
}