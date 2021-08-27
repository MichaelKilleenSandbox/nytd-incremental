package gov.hhs.acf.cb.nytd.models.helper;

import gov.hhs.acf.cb.nytd.models.PersistentObject;
import lombok.Getter;
import lombok.Setter;

import java.util.Calendar;


@SuppressWarnings("serial")
public class VwSamplingRequest extends PersistentObject
{
	@Getter @Setter private Long samplingRequestId;
	@Getter @Setter private Long cohortStatusId;
	@Getter @Setter private String cohortName;
	@Getter @Setter private Long cohortId;
	@Getter @Setter private String stateName;
	@Getter @Setter private Long stateId;
	@Getter @Setter private String samplingMethod;
	@Getter @Setter private String requestStatus;
	@Getter @Setter private Long requestStatusId;
	@Getter @Setter private Long recordsCount;
	@Getter @Setter private Calendar createdDate;
	@Getter @Setter private String importSummary;
	@Getter @Setter private Long importStatusId;
	
	public VwSamplingRequest()
	{
	}

	/*
	 * 
	 */
	public VwSamplingRequest(Long samplingRequestId)
	{
		this.samplingRequestId = samplingRequestId;
	}

	public VwSamplingRequest(Long samplingRequestId,Long cohortStatusId,String cohortName,Long cohortId,String stateName,Long stateId,String samplingMethod,
			String requestStatus,Long requestStatusId,Long recordsCount,Calendar createdDate,Long importStatusId,String importSummary)
	{
		this.samplingRequestId = samplingRequestId;
		this.cohortStatusId = cohortStatusId;
		this.cohortName = cohortName;
		this.cohortId = cohortId;
		this.stateName = stateName;
		this.stateId = stateId;
		this.samplingMethod = samplingMethod;
		this.requestStatus = requestStatus;
		this.requestStatusId = requestStatusId;
		this.recordsCount = recordsCount;
		this.createdDate = createdDate;
		this.importStatusId = importStatusId;
		this.importSummary = importSummary;
	}

	}
