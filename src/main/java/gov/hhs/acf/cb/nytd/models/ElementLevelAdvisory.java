package gov.hhs.acf.cb.nytd.models;

import gov.hhs.acf.cb.nytd.util.DateUtil;

import java.math.BigDecimal;

public class ElementLevelAdvisory extends DataQualityAdvisory
{
	private Integer countValue;
	private BigDecimal percentValue;
	private String elementLevelDatumValue;
	private ElementLevelDQAStandard elementLevelDQAStandard;
	
	/**
	 * @return the countValue
	 */
	public Integer getCountValue()
	{
		return countValue;
	}
	/**
	 * @param countValue the countValue to set
	 */
	public void setCountValue(Integer countValue)
	{
		this.countValue = countValue;
	}
	/**
	 * @return the percentValue
	 */
	public BigDecimal getPercentValue()
	{
		return percentValue;
	}
	/**
	 * @param percentValue the percentValue to set
	 */
	public void setPercentValue(BigDecimal percentValue)
	{
		this.percentValue = percentValue;
	}
	/**
	 * @return the elementLevelDatumValue
	 */
	public String getElementLevelDatumValue()
	{
		return elementLevelDatumValue;
	}
	/**
	 * @param elementLevelDatumValue the elementLevelDatumValue to set
	 */
	public void setElementLevelDatumValue(String elementLevelDatumValue)
	{
		this.elementLevelDatumValue = elementLevelDatumValue;
	}
	/**
	 * @return the elementLevelDQAStandard
	 */
	public ElementLevelDQAStandard getElementLevelDQAStandard()
	{
		return elementLevelDQAStandard;
	}
	/**
	 * @param elementLevelDQAStandard the elementLevelDQAStandard to set
	 */
	public void setElementLevelDQAStandard(ElementLevelDQAStandard elementLevelDQAStandard)
	{
		this.elementLevelDQAStandard = elementLevelDQAStandard;
	}
	public String toYYYYMMDD(String ddMMMyy)
	{
		return DateUtil.toYYYYMMDD(ddMMMyy);
	}
	
}
