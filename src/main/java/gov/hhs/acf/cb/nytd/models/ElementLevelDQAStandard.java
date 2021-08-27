package gov.hhs.acf.cb.nytd.models;

import java.util.Calendar;

// Generated May 20, 2009 10:16:43 AM by Hibernate Tools 3.2.4.GA

/*
 * DataQualityAdvStandard generated by hbm2java
 */
public class ElementLevelDQAStandard extends PersistentObject
{
	private String name;
	private Integer dataQualityAdvStandard;
	private String conditionalDescription;
	private String dataQualityAdvNotation;
    // associations
	private AllowedValue allowedValue;

    // collections
  /*  private Set<ElementLevelAdvisory> elementLeveladvisories =
            new HashSet<ElementLevelAdvisory>();*/

	public ElementLevelDQAStandard()
	{
	}

	public ElementLevelDQAStandard(Long elementLevelDQAStandardId)
	{
		this.id = elementLevelDQAStandardId;
	}

	public ElementLevelDQAStandard(Long dataQualityAdvStandardId, String name,
			Calendar createdDate, String createdBy, Calendar updateDate, String updateBy, String description)
	{
		this.id = dataQualityAdvStandardId;
		// this.element = element;
		this.name = name;
		this.createdDate = createdDate;
		this.createdBy = createdBy;
		this.updateDate = updateDate;
		this.updateBy = updateBy;
		this.description = description;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @return the dataQualityAdvStandard
	 */
	public Integer getDataQualityAdvStandard()
	{
		return dataQualityAdvStandard;
	}

	/**
	 * @param dataQualityAdvStandard the dataQualityAdvStandard to set
	 */
	public void setDataQualityAdvStandard(Integer dataQualityAdvStandard)
	{
		this.dataQualityAdvStandard = dataQualityAdvStandard;
	}

	/**
	 * @return the allowedValue
	 */
	public AllowedValue getAllowedValue()
	{
		return allowedValue;
	}

	/**
	 * @param allowedValue the allowedValue to set
	 */
	public void setAllowedValue(AllowedValue allowedValue)
	{
		this.allowedValue = allowedValue;
	}

	/**
	 * @return the elementLeveladvisories
	 */
	/*public Set<ElementLevelAdvisory> getElementLeveladvisories()
	{
		return elementLeveladvisories;
	}*/

	/**
	 * @param elementLeveladvisories the elementLeveladvisories to set
	 */
/*	public void setElementLeveladvisories(Set<ElementLevelAdvisory> elementLeveladvisories)
	{
		this.elementLeveladvisories = elementLeveladvisories;
	}*/

	/**
	 * @return the conditionalDescription
	 */
	public String getConditionalDescription()
	{
		return conditionalDescription;
	}

	/**
	 * @param conditionalDescription the conditionalDescription to set
	 */
	public void setConditionalDescription(String conditionalDescription)
	{
		this.conditionalDescription = conditionalDescription;
	}

	/**
	 * @return the dataQualityAdvNotation
	 */
	public String getDataQualityAdvNotation()
	{
		return dataQualityAdvNotation;
	}

	/**
	 * @param dataQualityAdvNotation the dataQualityAdvNotation to set
	 */
	public void setDataQualityAdvNotation(String dataQualityAdvNotation)
	{
		this.dataQualityAdvNotation = dataQualityAdvNotation;
	}
	
}
