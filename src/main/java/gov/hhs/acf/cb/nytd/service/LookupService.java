package gov.hhs.acf.cb.nytd.service;

import gov.hhs.acf.cb.nytd.models.Region;
import gov.hhs.acf.cb.nytd.models.ReportingPeriod;
import gov.hhs.acf.cb.nytd.models.SiteUser;
import gov.hhs.acf.cb.nytd.models.State;

import java.util.Calendar;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: 13873
 * Date: Jun 6, 2010
 */
public interface LookupService extends BaseService
{
	List<ReportingPeriod> getReportingPeriods();

	List<SiteUser> getFederalUsers();

	List<State> getRegionStates(Region region);

	List<SiteUser> getRegionUsers(Region region, State state);

	List<State> getStates();

	Region getStateRegion(State state);

	List<SiteUser> getStateUsers(State state);

	List<SiteUser> getSystemAdminUsers();

	List<State> getUserStates(SiteUser user);
	
	Calendar getSubmissionDeadline(ReportingPeriod period,Long transmissionType);
	
}
