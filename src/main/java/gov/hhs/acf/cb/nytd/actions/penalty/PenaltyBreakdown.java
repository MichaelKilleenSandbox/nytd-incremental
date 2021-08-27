package gov.hhs.acf.cb.nytd.actions.penalty;

import com.opensymphony.xwork2.ActionSupport;
import gov.hhs.acf.cb.nytd.actions.ExportableTable;
import gov.hhs.acf.cb.nytd.models.*;
import gov.hhs.acf.cb.nytd.util.Constants;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by IntelliJ IDEA. User: 13873 Date: May 21, 2010
 */
public class PenaltyBreakdown extends ExportableTable<PenaltyBreakdown.Row> implements Comparator<NytdError>
{
	public class Row
	{
		private String supercategoryName;
		private String categoryName;
		private String elementName;
		private String elementDescription;
		private String errorMessage;
		private String penaltyValue;

		public Row()
		{
			// set default value to empty string
			// null values are not allowed for export
			supercategoryName = "";
			categoryName = "";
			elementName = "";
			elementDescription = "";
			errorMessage = "";
			penaltyValue = "";
		}

		public String getSupercategoryName()
		{
			return supercategoryName;
		}

		public void setSupercategoryName(String supercategoryName)
		{
			this.supercategoryName = supercategoryName;
		}

		public String getCategoryName()
		{
			return categoryName;
		}

		public void setCategoryName(String categoryName)
		{
			this.categoryName = categoryName;
		}

		public String getElementName()
		{
			return elementName;
		}

		public void setElementName(String elementName)
		{
			this.elementName = elementName;
		}

		public String getElementDescription()
		{
			return elementDescription;
		}

		public void setElementDescription(String elementDescription)
		{
			this.elementDescription = elementDescription;
		}

		public String getErrorMessage()
		{
			return errorMessage;
		}

		public void setErrorMessage(String errorMessage)
		{
			this.errorMessage = errorMessage;
		}

		public String getPenaltyValue()
		{
			return penaltyValue;
		}

		public void setPenaltyValue(String penaltyValue)
		{
			this.penaltyValue = penaltyValue;
		}
	}

	// data penalties are sliced by compliance category
	Map<ComplianceCategory, Double> dataPenalties;

	// penalty errors (aggregate and transmission errors)
	private List<NytdError> fileSubmissionErrors;
	private List<NytdError> dataErrors;

	// error counts for each super category
	private Map<ComplianceCategory, Integer> fileSubmissionErrorCounts;
	private Map<ComplianceCategory, Integer> dataErrorCounts;

	private Map<String, String> fileSubmissionDesc;
	private Map<String, String> dataStandardDesc;
	
	private Map<String, String> fileSubmissionDesc2;
	private Map<String, String> dataStandardDesc2;

	private List<String> fileSubmissionComplianceStdPenaltyDesc;

	private List<String> dataStandardComplianceStdPenaltyDesc;

	/**
	 * @return the fileSubmissionDesc
	 */
	public Map<String, String> getFileSubmissionDesc()
	{
		return fileSubmissionDesc;
	}

	/**
	 * @param fileSubmissionDesc
	 *           the fileSubmissionDesc to set
	 */
	public void setFileSubmissionDesc(Map<String, String> fileSubmissionDesc)
	{
		this.fileSubmissionDesc = fileSubmissionDesc;
	}

	/**
	 * @return the dataStandardDesc
	 */
	public Map<String, String> getDataStandardDesc()
	{
		return dataStandardDesc;
	}

	/**
	 * @param dataStandardDesc
	 *           the dataStandardDesc to set
	 */
	public void setDataStandardDesc(Map<String, String> dataStandardDesc)
	{
		this.dataStandardDesc = dataStandardDesc;
	}

	/**
	 * @return the fileSubmissionComplianceStdPenaltyDesc
	 */
	public List<String> getFileSubmissionComplianceStdPenaltyDesc()
	{
		return fileSubmissionComplianceStdPenaltyDesc;
	}

	/**
	 * @param fileSubmissionComplianceStdPenaltyDesc
	 *           the fileSubmissionComplianceStdPenaltyDesc to set
	 */
	public void setFileSubmissionComplianceStdPenaltyDesc(List<String> fileSubmissionComplianceStdPenaltyDesc)
	{
		this.fileSubmissionComplianceStdPenaltyDesc = fileSubmissionComplianceStdPenaltyDesc;
	}

	/**
	 * @return the dataStandardComplianceStdPenaltyDesc
	 */
	public List<String> getDataStandardComplianceStdPenaltyDesc()
	{
		return dataStandardComplianceStdPenaltyDesc;
	}

	/**
	 * @param dataStandardComplianceStdPenaltyDesc
	 *           the dataStandardComplianceStdPenaltyDesc to set
	 */
	public void setDataStandardComplianceStdPenaltyDesc(List<String> dataStandardComplianceStdPenaltyDesc)
	{
		this.dataStandardComplianceStdPenaltyDesc = dataStandardComplianceStdPenaltyDesc;
	}

	public PenaltyBreakdown()
	{
		super();
	}

	@Override
	protected void addColumns()
	{
		addColumn("Standards", new ValueProvider<Row>()
		{
			public String getValue(Row row)
			{
				return row.getSupercategoryName();
			}
		});
		addColumn("Non-Compliance Category", new ValueProvider<Row>()
		{
			public String getValue(Row row)
			{
				return row.getCategoryName();
			}
		});
		addColumn("Element Number", new ValueProvider<Row>()
		{
			public String getValue(Row row)
			{
				return row.getElementName();
			}
		});
		addColumn("Element Name", new ValueProvider<Row>()
		{
			public String getValue(Row row)
			{
				return row.getElementDescription();
			}
		});
		addColumn("Error Description", new ValueProvider<Row>()
		{
			public String getValue(Row row)
			{
				return row.getErrorMessage();
			}
		});
		addColumn("System-generated Potential Penalty", new ValueProvider<Row>()
		{
			public String getValue(Row row)
			{
				return row.getPenaltyValue();
			}
		});
	}

	public Double calcDataPenalty()
	{
		Double penalty = 0.0;
		Double outcomesUniversePenalty = 0.0;
		Double outcomesFCPartPenalty = 0.0;
		Double outcomesDCPartPenalty = 0.0;
		for (Double value : dataPenalties.values())
		{
			penalty += value;
		}

		for(ComplianceCategory complianceCategory : dataPenalties.keySet())
		{
			if(complianceCategory.getName().equalsIgnoreCase("Outcomes Universe"))
			{
				outcomesUniversePenalty =  dataPenalties.get(complianceCategory);
			}
			if(complianceCategory.getName().equalsIgnoreCase("Outcomes Participation - Foster Care Youth"))
			{
				outcomesFCPartPenalty =  dataPenalties.get(complianceCategory);
			}
			if(complianceCategory.getName().equalsIgnoreCase("Outcomes Participation - Discharged Youth"))
			{
				outcomesDCPartPenalty =  dataPenalties.get(complianceCategory);
			}
		}

		if(outcomesUniversePenalty > 0.0)
		{
			penalty = penalty - (outcomesFCPartPenalty + outcomesDCPartPenalty);
		}
		return penalty;
	}

	public Double calcFileSubmissionPenalty()
	{
		for (Integer errorCount : fileSubmissionErrorCounts.values())
		{
			if (errorCount > 0)
			{
				return 2.5;
			}
		}

		return 0.0;
	}

	public Double calcTotalPenalty()
	{
		Double penalty = calcDataPenalty() + calcFileSubmissionPenalty();

		if (penalty > 2.5)
		{
			penalty = 2.5;
		}

		return penalty;
	}

	public String export(ActionSupport action, HttpServletResponse response, String exportFileName)
	{

		setAction(action);
		return super.export(response, getRowData(), exportFileName);
	}

	public Map<ComplianceCategory, Double> getDataPenalties()
	{
		return dataPenalties;
	}

	public void setDataPenalties(Map<ComplianceCategory, Double> dataPenalties)
	{
		this.dataPenalties = dataPenalties;
	}

	public List<NytdError> getFileSubmissionErrors()
	{
		return fileSubmissionErrors;
	}

	public void setFileSubmissionErrors(List<NytdError> fileSubmissionErrors)
	{
		this.fileSubmissionErrors = fileSubmissionErrors;
		Collections.sort(this.fileSubmissionErrors, this);
	}

	public List<NytdError> getDataErrors()
	{
		return dataErrors;
	}

	public void setDataErrors(List<NytdError> dataErrors)
	{
		this.dataErrors = dataErrors;
		Collections.sort(this.dataErrors, this);
	}

	public Map<ComplianceCategory, Integer> getFileSubmissionErrorCounts()
	{
		return fileSubmissionErrorCounts;
	}

	public void setFileSubmissionErrorCounts(Map<ComplianceCategory, Integer> fileSubmissionErrorCounts)
	{
		this.fileSubmissionErrorCounts = fileSubmissionErrorCounts;
	}

	public Map<ComplianceCategory, Integer> getDataErrorCounts()
	{
		return dataErrorCounts;
	}

	public void setDataErrorCounts(Map<ComplianceCategory, Integer> dataErrorCounts)
	{
		this.dataErrorCounts = dataErrorCounts;
	}

	public int compare(NytdError o1, NytdError o2)
	{
		String o1CategoryName = o1.getComplianceCategory().getName();
		String o2CategoryName = o2.getComplianceCategory().getName();

		// errors are considered equal if they are in the same compliance
		// category.
		if (o1CategoryName.equalsIgnoreCase(o2CategoryName))
		{

			if (o1CategoryName.equalsIgnoreCase(Constants.ERROR_FREE_INFO))
			{
				Long o1ElementId = o1.getNonCompliance().getDataAggregate().getElement().getId();
				Long o2ElementId = o2.getNonCompliance().getDataAggregate().getElement().getId();

				if (o1ElementId > o2ElementId)
				{
					return 1;
				}
				else if (o1ElementId < o2ElementId)
				{
					return -1;
				}
			}

			return 0;
		}

		// sort order depends on the compliance supercategory
		if (o1.getComplianceCategory().getComplianceSuperCategory().getName().equalsIgnoreCase(
				Constants.FILE_SUBMISSION_STANDARDS))
		{
			return compareFileSubmissionErrors(o1CategoryName, o2CategoryName);
		}
		else
		{
			return compareFileSubmissionErrors(o1CategoryName, o2CategoryName);
		}
	}

	/**
	 * Sorts file submission standards errors for display on the penalty
	 * breakdown page.
	 *
	 * @param o1
	 * @param o2
	 * @return
	 */
	private int compareFileSubmissionErrors(String o1, String o2)
	{
		// timely data errors are displayed first for file submission standards
		if (o1.equalsIgnoreCase(Constants.TIMELY_DATA))
		{
			return 1;
		}

		// file format errors are displayed second
		if (o1.equalsIgnoreCase(Constants.FILE_FORMAT) && !o2.equalsIgnoreCase(Constants.TIMELY_DATA))
		{
			return 1;
		}

		// error-free errors are displayed last
		return -1;
	}

	private int compareDataStandardsErrors(String o1, String o2)
	{
		// aggregate data errors are displayed first
		if (o1.equalsIgnoreCase(Constants.ERROR_FREE_INFO))
		{
			return 1;
		}

		// outcomes universe errors are displayed second
		if (o1.equalsIgnoreCase(Constants.UNIVERSE) && !o2.equalsIgnoreCase(Constants.ERROR_FREE_INFO))
		{
			return 1;
		}

		// foster care participation errors are next
		if (o1.equalsIgnoreCase(Constants.FOSTER_CARE_PARTICIPATION)
				&& !o2.equalsIgnoreCase(Constants.ERROR_FREE_INFO) && !o2.equalsIgnoreCase(Constants.UNIVERSE))
		{
			return 1;
		}

		// discharged youth participation errors are displayed last
		return -1;
	}

	private List<Row> getRowData()
	{
		// initialize the collection of rows used by export addColumns method.
		// special care must be taken to ensure the csv output matches the
		// html table displayed to the user in penaltyBreakdown.jsp
		boolean firstRow;
		String currentCategory = "";
		Row row;
		List<Row> rows = new ArrayList<Row>();

		// add the file submission errors
		firstRow = true;
		for (NytdError error : fileSubmissionErrors)
		{
			row = new Row();
			if (error.getComplianceCategory().getId() != 2)
			{
				if (error.getNonCompliance().getDataAggregate() != null)
				{
					row.setElementName(error.getNonCompliance().getDataAggregate().getElement().getName());
					row.setElementDescription(error.getNonCompliance().getDataAggregate().getElement()
							.getDescription());
				}
			}
			row.setErrorMessage(error.formatErrorMessage());

			if (firstRow)
			{
				row.setSupercategoryName(error.getComplianceCategory().getComplianceSuperCategory().getName());
				row.setPenaltyValue(calcFileSubmissionPenalty().toString());
				firstRow = false;
			}
			// if
			// (!currentCategory.equalsIgnoreCase(error.getComplianceCategory().getName()))
			// {
			row.setCategoryName(error.getComplianceCategory().getName());
			currentCategory = error.getComplianceCategory().getName();
			// }

			rows.add(row);
		}

		// add data standards errors
		firstRow = true;
		currentCategory = "";
		for (NytdError error : dataErrors)
		{
			row = new Row();
			row.setElementName(error.getNonCompliance().getDataAggregate().getElement().getName());
			row.setElementDescription(error.getNonCompliance().getDataAggregate().getElement().getDescription());
			row.setErrorMessage(error.formatErrorMessage());

			if (firstRow)
			{
				row.setSupercategoryName(error.getComplianceCategory().getComplianceSuperCategory().getName());
				firstRow = false;
			}
			if (!currentCategory.equalsIgnoreCase(error.getComplianceCategory().getName()))
			{
				row.setCategoryName(error.getComplianceCategory().getName());
				row.setPenaltyValue(dataPenalties.get(error.getComplianceCategory()).toString());
				currentCategory = error.getComplianceCategory().getName();
			}

			rows.add(row);
		}

		// add a row for the total data standards penalty and total system penalty
		row = new Row();
		row.setSupercategoryName("Total Data Standards Penalty");
		row.setPenaltyValue(calcDataPenalty().toString());
		rows.add(row);

		row = new Row();
		row.setSupercategoryName("Total System-generated Potential Penalty");
		row.setPenaltyValue(calcTotalPenalty().toString());
		rows.add(row);

		return rows;
	}

	public void addMissingCategories(List<ComplianceCategory> fileSubmissionCategories,
			List<ComplianceCategory> dataCategories)
	{
		boolean fssErrorFreeCategoryExists = false;
		boolean timelyDataCategoryExists = false;
		boolean fileFormatCategoryExists = false;
		boolean dsErrorFreeCategoryExists = false;
		boolean outcomesUniverseCategoryExists = false;
		boolean dischargedParticipationCategoryExists = false;
		boolean fosterYouthParticipationCategoryExists = false;
		for (NytdError error : fileSubmissionErrors)
		{
			ComplianceCategory category = error.getComplianceCategory();
			String categoryName = category.getName();
			assert (category.getComplianceSuperCategory() != null);
			String superCategoryName = category.getComplianceSuperCategory().getName();

			if (categoryName.equalsIgnoreCase(Constants.ERROR_FREE_INFO)
					&& superCategoryName.equalsIgnoreCase(Constants.FILE_SUBMISSION_STANDARDS))
			{
				fssErrorFreeCategoryExists = true;
			}
			else if (categoryName.equalsIgnoreCase(Constants.TIMELY_DATA))
			{
				timelyDataCategoryExists = true;
			}
			else if (categoryName.equalsIgnoreCase(Constants.FILE_FORMAT))
			{
				fileFormatCategoryExists = true;
			}
		}
		for (NytdError error : dataErrors)
		{
			ComplianceCategory category = error.getComplianceCategory();
			String categoryName = category.getName();
			assert (category.getComplianceSuperCategory() != null);
			String superCategoryName = category.getComplianceSuperCategory().getName();

			if (categoryName.equalsIgnoreCase(Constants.ERROR_FREE_INFO)
					&& superCategoryName.equalsIgnoreCase(Constants.DATA_STANDARDS))
			{
				dsErrorFreeCategoryExists = true;
			}
			else if (categoryName.equalsIgnoreCase(Constants.UNIVERSE))
			{
				outcomesUniverseCategoryExists = true;
			}
			else if (categoryName.equalsIgnoreCase(Constants.FOSTER_CARE_PARTICIPATION))
			{
				fosterYouthParticipationCategoryExists = true;
			}
			else if (categoryName.equalsIgnoreCase(Constants.DISCHARGED_PARTICIPATION))
			{
				dischargedParticipationCategoryExists = true;
			}
		}
		ComplianceCategory timelyDataCategory = null;
		ComplianceCategory fileFormatCategory = null;
		ComplianceCategory fssErrorFreeCategory = null;
		ComplianceCategory dsErrorFreeCategory = null;
		ComplianceCategory outcomesUniverseCategory = null;
		ComplianceCategory fosterYouthParticipationCategory = null;
		ComplianceCategory dischargedParticipationCategory = null;
		for (ComplianceCategory category : fileSubmissionCategories)
		{
			if (category.getName().equalsIgnoreCase(Constants.TIMELY_DATA))
			{
				timelyDataCategory = category;
			}
			else if (category.getName().equalsIgnoreCase(Constants.FILE_FORMAT))
			{
				fileFormatCategory = category;
			}
			else if (category.getName().equalsIgnoreCase(Constants.ERROR_FREE_INFO))
			{
				fssErrorFreeCategory = category;
			}
		}
		for (ComplianceCategory category : dataCategories)
		{
			if (category.getName().equalsIgnoreCase(Constants.ERROR_FREE_INFO))
			{
				dsErrorFreeCategory = category;
			}
			else if (category.getName().equalsIgnoreCase(Constants.UNIVERSE))
			{
				outcomesUniverseCategory = category;
			}
			else if (category.getName().equalsIgnoreCase(Constants.FOSTER_CARE_PARTICIPATION))
			{
				fosterYouthParticipationCategory = category;
			}
			else if (category.getName().equalsIgnoreCase(Constants.DISCHARGED_PARTICIPATION))
			{
				dischargedParticipationCategory = category;
			}
		}
		ProblemDescription blankProblemDescription = new ProblemDescription();
		blankProblemDescription.setName("N/A");
		Element blankElement = new Element();
		blankElement.setName("N/A");
		blankElement.setDescription("N/A");
		DataAggregate blankAggregate = new DataAggregate();
		blankAggregate.setElement(blankElement);
		blankAggregate.setCountValue(0);
		NonCompliance blankNonCompliance = new NonCompliance();
		blankNonCompliance.setDataAggregate(blankAggregate);
		blankAggregate.setNonCompliance(blankNonCompliance);
		if (!fssErrorFreeCategoryExists)
		{
			NytdError toAdd = new NytdError();
			toAdd.setComplianceCategory(fssErrorFreeCategory);
			toAdd.setProblemDescription(blankProblemDescription);
			toAdd.setNonCompliance(blankNonCompliance);
			fileSubmissionErrors.add(toAdd);
		}
		if (!timelyDataCategoryExists)
		{
			NytdError toAdd = new NytdError();
			toAdd.setComplianceCategory(timelyDataCategory);
			toAdd.setProblemDescription(blankProblemDescription);
			toAdd.setNonCompliance(blankNonCompliance);
			fileSubmissionErrors.add(toAdd);
		}
		if (!fileFormatCategoryExists)
		{
			NytdError toAdd = new NytdError();
			toAdd.setComplianceCategory(fileFormatCategory);
			toAdd.setProblemDescription(blankProblemDescription);
			toAdd.setNonCompliance(blankNonCompliance);
			fileSubmissionErrors.add(toAdd);
		}
		if (!dsErrorFreeCategoryExists)
		{
			NytdError toAdd = new NytdError();
			toAdd.setComplianceCategory(dsErrorFreeCategory);
			toAdd.setProblemDescription(blankProblemDescription);
			toAdd.setNonCompliance(blankNonCompliance);
			dataErrors.add(toAdd);
		}
		if (!outcomesUniverseCategoryExists)
		{
			NytdError toAdd = new NytdError();
			toAdd.setComplianceCategory(outcomesUniverseCategory);
			toAdd.setProblemDescription(blankProblemDescription);
			toAdd.setNonCompliance(blankNonCompliance);
			dataErrors.add(toAdd);
		}
		if (!fosterYouthParticipationCategoryExists)
		{
			NytdError toAdd = new NytdError();
			toAdd.setComplianceCategory(fosterYouthParticipationCategory);
			toAdd.setProblemDescription(blankProblemDescription);
			toAdd.setNonCompliance(blankNonCompliance);
			dataErrors.add(toAdd);
		}
		if (!dischargedParticipationCategoryExists)
		{
			NytdError toAdd = new NytdError();
			toAdd.setComplianceCategory(dischargedParticipationCategory);
			toAdd.setProblemDescription(blankProblemDescription);
			toAdd.setNonCompliance(blankNonCompliance);
			dataErrors.add(toAdd);
		}
	}

	/**
	 * @return the fileSubmissionDesc2
	 */
	public Map<String, String> getFileSubmissionDesc2() {
		return fileSubmissionDesc2;
	}

	/**
	 * @param fileSubmissionDesc2 the fileSubmissionDesc2 to set
	 */
	public void setFileSubmissionDesc2(Map<String, String> fileSubmissionDesc2) {
		this.fileSubmissionDesc2 = fileSubmissionDesc2;
	}

	/**
	 * @return the dataStandardDesc2
	 */
	public Map<String, String> getDataStandardDesc2() {
		return dataStandardDesc2;
	}

	/**
	 * @param dataStandardDesc2 the dataStandardDesc2 to set
	 */
	public void setDataStandardDesc2(Map<String, String> dataStandardDesc2) {
		this.dataStandardDesc2 = dataStandardDesc2;
	}
}
