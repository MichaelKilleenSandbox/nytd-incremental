package gov.hhs.acf.cb.nytd.actions.compliance;

import gov.hhs.acf.cb.nytd.actions.PaginatedSearch;
import gov.hhs.acf.cb.nytd.models.ComplianceCategory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: 13873
 * Date: Apr 20, 2010
 */
public class ComplianceSearch extends PaginatedSearch
{
	private List<String> recordNumbers;
	private String elementNumber;
	private String elementName;
	private String complianceType;
	private Long datumId;
	private List<ComplianceCategory> nonPenaltyCategories;

	public ComplianceSearch()
	{
		super();
		loadDefaults();
	}

	public List<String> getRecordNumbers()
	{
		return recordNumbers;
	}

	public void setRecordNumbers(List<String> recordNumbers)
	{
		this.recordNumbers = recordNumbers;
	}

	public String getElementNumber()
	{
		return elementNumber;
	}

	public void setElementNumber(String elementNumber)
	{
		this.elementNumber = elementNumber;
	}

	public String getElementName()
	{
		return elementName;
	}

	public void setElementName(String elementName)
	{
		this.elementName = elementName;
	}

	public String getComplianceType()
	{
		return complianceType;
	}

	public void setComplianceType(String complianceType)
	{
		this.complianceType = complianceType;
	}

	public List<ComplianceCategory> getNonPenaltyCategories()
	{
		return nonPenaltyCategories;
	}

	public void setNonPenaltyCategories(List<ComplianceCategory> nonPenaltyCategories)
	{
		this.nonPenaltyCategories = nonPenaltyCategories;
	}

	public void reset()
	{
		super.reset();
		loadDefaults();
	}

	private void loadDefaults()
	{
		setRecordNumbers(new ArrayList<String>());
		setElementNumber("All");
		setElementName("");
		setComplianceType("All");
		setNonPenaltyCategories(new ArrayList());
	}

	/**
	 * @return the datumId
	 */
	public Long getDatumId()
	{
		return datumId;
	}

	/**
	 * @param datumId the datumId to set
	 */
	public void setDatumId(Long datumId)
	{
		this.datumId = datumId;
	}
}
