package gov.hhs.acf.cb.nytd.actions.transmission;

import gov.hhs.acf.cb.nytd.actions.PaginatedSearch;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by IntelliJ IDEA.
 * User: 13873
 * Date: Mar 30, 2010
 */
public class TransmissionSearch extends PaginatedSearch
{
	// Transmission search criteria
	@Getter @Setter private String stateName;
	@Getter @Setter private String fileType;
	@Getter @Setter private String fileNumber;
	@Getter @Setter private String complianceStatus;
	@Getter @Setter private String reportingPeriod;
	@Getter @Setter private String transmissionStartDate;
	@Getter @Setter private String transmissionEndDate;
	@Getter @Setter private boolean viewSubmissionsOnly;
	@Getter @Setter private boolean viewActiveSubmissionsOnly;

	public TransmissionSearch()
	{
		super();
		loadDefaults();
	}

	public void reset()
	{
		super.reset();
		loadDefaults();
	}

	private void loadDefaults()
	{
		setStateName("All");
		setFileType("All");
		setFileNumber(null);
		setComplianceStatus("All");
		setReportingPeriod("All");
		setTransmissionStartDate(null);
		setTransmissionEndDate(null);
		setViewSubmissionsOnly(false);
		setViewActiveSubmissionsOnly(false);
	}
}
