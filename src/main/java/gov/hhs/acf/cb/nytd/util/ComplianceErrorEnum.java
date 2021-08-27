package gov.hhs.acf.cb.nytd.util;

/**
 * 
 * Created per Bug 15527.
 * <p>
 * The enums in this class map to the error-type Strings returned by {@link gov.hhs.acf.cb.nytd.service.ComplianceService#getErrorTypes() ComplianceService.getErrorTypes()}.
 * <p>
 * The enumeration is incomplete.  Types may be added as needed.
 * 
 * @author 20833
 *
 */
public enum ComplianceErrorEnum
{

	TIMELY_DATA("Timely Data"), ERROR_FREE_INFORMATION("Error Free Information");

	private String errorName;

	ComplianceErrorEnum(String errorName)
	{
		this.errorName = errorName;
	}

	/**
	 * @return String representation of the Compliance Error
	 */
	public String getErrorName()
	{
		return errorName;
	}

}
