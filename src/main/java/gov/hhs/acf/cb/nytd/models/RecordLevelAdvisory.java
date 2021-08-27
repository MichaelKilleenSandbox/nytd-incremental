package gov.hhs.acf.cb.nytd.models;

public class RecordLevelAdvisory extends DataQualityAdvisory
{
	private ProblemDescription problemDescription;
	private Datum datum;
	private Datum refDatum;
	/**
	 * @return the problemDescription
	 */
	public ProblemDescription getProblemDescription()
	{
		return problemDescription;
	}
	/**
	 * @param problemDescription the problemDescription to set
	 */
	public void setProblemDescription(ProblemDescription problemDescription)
	{
		this.problemDescription = problemDescription;
	}
	/**
	 * @return the datum
	 */
	public Datum getDatum()
	{
		return datum;
	}
	/**
	 * @param datum the datum to set
	 */
	public void setDatum(Datum datum)
	{
		this.datum = datum;
	}
	/**
	 * @return the refDatum
	 */
	public Datum getRefDatum()
	{
		return refDatum;
	}
	/**
	 * @param refDatum the refDatum to set
	 */
	public void setRefDatum(Datum refDatum)
	{
		this.refDatum = refDatum;
	}
}
