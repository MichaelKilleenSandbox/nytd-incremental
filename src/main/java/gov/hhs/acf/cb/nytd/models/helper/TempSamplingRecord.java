package gov.hhs.acf.cb.nytd.models.helper;

import gov.hhs.acf.cb.nytd.models.PersistentObject;
import lombok.Getter;
import lombok.Setter;


@SuppressWarnings("serial")
public class TempSamplingRecord extends PersistentObject
{
	@Getter @Setter private Long samplingRequestId;
	@Getter @Setter private String recordNumber;
	
	
	public TempSamplingRecord()
	{
	}

	/*
	 * 
	 */
	public TempSamplingRecord(Long id)
	{
		this.id = id;
	}

	public TempSamplingRecord(Long id ,Long samplingRequestId,String recordNumber)
	{
		this.id = id;
		this.samplingRequestId = samplingRequestId;
		this.recordNumber = recordNumber;
	}

}
