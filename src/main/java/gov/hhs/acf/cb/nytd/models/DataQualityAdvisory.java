package gov.hhs.acf.cb.nytd.models;

/**
 * @author 23839
 *
 */
public  class DataQualityAdvisory extends PersistentObject
{
	public static final String ELEMENT_ADVISORY ="ELEMENTLEVEL";
	public static final String RECORD_ADVISORY = "RECORDLEVEL";
	
	protected Long transmissionId;

	/**
	 * @return the transmissionId
	 */
	public Long getTransmissionId()
	{
		return transmissionId;
	}

	/**
	 * @param transmissionId the transmissionId to set
	 */
	public void setTransmissionId(Long transmissionId)
	{
		this.transmissionId = transmissionId;
	}

}
