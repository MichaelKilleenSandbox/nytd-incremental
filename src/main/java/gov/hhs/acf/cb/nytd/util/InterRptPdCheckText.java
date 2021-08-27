package gov.hhs.acf.cb.nytd.util;


public class InterRptPdCheckText
{
	public static final String GENERIC =
		"Youth record number %s specifies a value of %s for %s, but this same "
	 + "youth record in report period %s specified the "
	 + "value %s for this element.";
	
	public static final String PRE_TO_POSTBUFFER_BASELINE_DUPLICATE =
		"The baseline survey information of youth record number %s "
	 + "already appears in reporting period %s and should not appear "
	 + "in this subsequent/corrected file.";
	
	public static final String MISSING_POSTBUFFER =
		"The birthday of youth record number %s occurred near "
	 + "the end of %s reporting period, and survey information "
	 + "was not previously included. Survey information should "
	 + "most likely appear in this reporting period but does not.";
	
	public static final String UNTIMELY_BUFFER =
		"The baseline survey information of youth record number %s "
	 + "appears in this reporting period, but the youth did not "
	 + "turn 17 during the previous report period %s. Baseline "
	 + "survey information for buffer-case youth must be reported "
	 + "the period directly following the one in which they turn "
	 + "17.";
}
