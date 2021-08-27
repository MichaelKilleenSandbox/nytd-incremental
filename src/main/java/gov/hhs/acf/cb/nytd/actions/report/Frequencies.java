package gov.hhs.acf.cb.nytd.actions.report;

import com.opensymphony.xwork2.ActionSupport;
import gov.hhs.acf.cb.nytd.models.helper.Frequency;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.SessionAware;

import java.util.List;
import java.util.Map;


/**
 * Action to display frequency report.
 * 
 * @author Adam Russell (18816)
 */
public class Frequencies extends ActionSupport implements SessionAware
{
	protected Logger log = Logger.getLogger(getClass());
	
	@Getter @Setter List<Frequency> frequencies;
	@Getter @Setter private Boolean byState;
	@Getter @Setter private Boolean byReportPeriod;
	@Getter @Setter private Boolean byElement;
	
	@Getter @Setter private Map<String, Object> session;
	
	public final String execute()
	{
		setFrequencies((List<Frequency>) getSession().get("frequencies"));
		if (getFrequencies() == null)
		{
			return INPUT;
		}
		
		if (byState && !byReportPeriod && !byElement)
		{
			return "by state";
		}
		else if (!byState && byReportPeriod && !byElement)
		{
			return "by report period";
		}
		else if (!byState && !byReportPeriod && byElement)
		{
			return "by element";
		}
		else if (byState && byReportPeriod && !byElement)
		{
			return "by state & report period";
		}
		else if (byState && !byReportPeriod && byElement)
		{
			return "by state & element";
		}
		else if (!byState && byReportPeriod && byElement)
		{
			return "by report period & element";
		}
		else if (byState && byReportPeriod && byElement)
		{
			return "by state & report period & element";
		}
		
		return SUCCESS;
	}
}
