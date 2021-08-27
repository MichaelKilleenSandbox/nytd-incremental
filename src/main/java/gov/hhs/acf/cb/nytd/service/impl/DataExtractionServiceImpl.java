/**
 * Filename: DataExtractionServiceImpl.java
 *
 *  Copyright 2009, ICF International
 *  Created: Aug 12, 2009
 *  Author: 18816
 *
 *  COPYRIGHT STATUS: This work, authored by ICF International employees, was funded in whole or in part
 *  under U.S. Government contract, and is, therefore, subject to the following license: The Government is
 *  granted for itself and others acting on its behalf a paid-up, nonexclusive, irrevocable worldwide
 *  license in this work to reproduce, prepare derivative works, distribute copies to the public, and perform
 *  publicly and display publicly, by or on behalf of the Government. All other rights are reserved by the
 *  copyright owner.
 */
package gov.hhs.acf.cb.nytd.service.impl;

import gov.hhs.acf.cb.nytd.actions.report.ComplianceStandardReportHelper;
import gov.hhs.acf.cb.nytd.actions.report.ComplianceStandardsReport;
import gov.hhs.acf.cb.nytd.actions.report.SubmissionStatisticsMetaData;
import gov.hhs.acf.cb.nytd.actions.report.SubmissionStatisticsReportHelper;
import gov.hhs.acf.cb.nytd.models.*;
import gov.hhs.acf.cb.nytd.models.helper.DataTable;
import gov.hhs.acf.cb.nytd.models.helper.TableDatumBean;
import gov.hhs.acf.cb.nytd.models.helper.TableFieldBean;
import gov.hhs.acf.cb.nytd.service.DataExtractionService;
import gov.hhs.acf.cb.nytd.service.PopulateSearchCriteriaService;
import gov.hhs.acf.cb.nytd.util.CommonFunctions;
import gov.hhs.acf.cb.nytd.util.Constants;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.*;
import java.math.BigDecimal;
import java.net.URI;
import java.util.*;


/**
 * Implementation of the DataExtractionService.
 *
 * @author Adam Russell (18816)
 * @see DataExtractionService
 */
@Transactional
public class DataExtractionServiceImpl extends BaseServiceImpl implements DataExtractionService
{
	// These attributes all act as cache. Once they are initialized/populated,
	// they do not change. They do not save state.

	private final Map<String, String> fileTypes;
	private final Map<String, String> generalFields;
	private final Map<String, String> fields;
	private final Map<String, String> demographics;
	private final Map<String, String> characteristics;
	private final Map<String, String> services;
	private final Map<String, String> outcomes;
	/// identifier of baseline population in db
	private  Long baselinePopId;
	private   Collection<Long> bufferPopIds;
	private  Long undeterminedPopId;
	
	@Getter @Setter public SessionFactory asessionFactory;
	@Getter @Setter public ArrayList<Element> elementsList;

	private String exportLocation;
	
	@Setter private PopulateSearchCriteriaService populateSearchCriteriaService;

	/**
	 * Construct a DataExtractionServiceImpl object, initializing appropriate attributes.
	 */
/*	public DataExtractionServiceImpl(BaseDAO baseDAO)
	{
		super();
*/		
	//public DataExtractionServiceImpl(SessionFactory sessionFactory)
	public DataExtractionServiceImpl()
	{
		super();
		String curr = null;
		fileTypes = new LinkedHashMap<String, String>(5);
		generalFields = new LinkedHashMap<String, String>(1);
		demographics = new LinkedHashMap<String, String>(Constants.NUM_DEMOGRAPHIC_FIELDS);
		characteristics = new LinkedHashMap<String, String>(Constants.NUM_CHARACTERISTIC_FIELDS);
		services = new LinkedHashMap<String, String>(Constants.NUM_SERVICE_FIELDS);
		outcomes = new LinkedHashMap<String, String>(Constants.NUM_OUTCOME_FIELDS);
		bufferPopIds = new HashSet<Long>();
		fields = new LinkedHashMap<String, String>(Constants.NUM_ELEM_FIELDS);
		
	// initialize fileTypes
		fileTypes.put(Constants.SAV_KEY, Constants.SAV_LABEL);
		fileTypes.put(Constants.CSV_KEY, Constants.CSV_LABEL);
		fileTypes.put(Constants.XLS_KEY, Constants.XLS_LABEL);
		fileTypes.put(Constants.HTM_KEY, Constants.HTM_LABEL);
		fileTypes.put(Constants.TXT_KEY, Constants.TXT_LABEL);
		
		// initialize generalFields
			generalFields.put("1", "File_Number");
			generalFields.put("2", "Served");
			generalFields.put("3", "Baseline");
			generalFields.put("4", "Follow-up 19");
			generalFields.put("5", "Follow-up 21");
			generalFields.put("6", "PreBuffer");
			generalFields.put("7", "PostBuffer");
			generalFields.put("8", "Cohort_FY");
			generalFields.put("9", "In_Sample");
		
		/*// initialize demographics
		for (int i = 0; i < Constants.NUM_DEMOGRAPHIC_FIELDS; ++i)
				{
					curr = String.valueOf(Constants.DEMOGRAPHIC_FIELDS[i]);
					demographics.put(curr, getElementLabel(curr));
				}
			
			// initialize characteristics	
			for (int i = 0; i < Constants.NUM_CHARACTERISTIC_FIELDS; ++i)
			{
				curr = String.valueOf(Constants.CHARACTERISTIC_FIELDS[i]);
				characteristics.put(curr, getElementLabel(curr));
			}
			
			// initialize services
		for (int i = 0; i < Constants.NUM_SERVICE_FIELDS; ++i)
			{
				curr = String.valueOf(Constants.SERVICE_FIELDS[i]);
				services.put(curr, getElementLabel(curr));
			}

			// initialize outcomes
			for (int i = 0; i < Constants.NUM_OUTCOME_FIELDS; ++i)
			{
				curr = String.valueOf(Constants.OUTCOME_FIELDS[i]);
				outcomes.put(curr, getElementLabel(curr));
			}*/
	}
	
	public int dataCount(DataTable dataTable, Collection<Long> reportingPeriods,
			Collection<Long> states, Collection<Long> populations,
	         Collection<Long> cohorts)
	{
		List<String> reportingPeriodList = this.getReportingPeriods(reportingPeriods);
		List<String> stateList = this.getStates(states);
		String recordQuery = null;
		StringBuffer recordQueryBuf = null;
		List<RecordToExport> records = null;

		List<String> populationList = this.getPopulationList(populations);
		
		recordQueryBuf = new StringBuffer()
		.append("select count(recordToExportDE)")
		.append("from RecordToExport as recordToExportDE, ")
		.append("State as state ")
		.append("where  recordToExportDE.state = state.abbreviation and ")
		.append("state.stateName in (:stateList) ")
		.append("and recordToExportDE.reportingPeriod in (:reportingPeriodList) ");
		if(cohorts!=null && cohorts.size()>0)
			recordQueryBuf.append("and recordToExportDE.cohortId in (:cohortList) ");	
		if(populationList.size() >0)
		{
			Iterator itr = populations.iterator();
			boolean selectedOthers = false;
			while(itr.hasNext())
			{
				
				if( itr.next().toString().equals("7"))
				{
					selectedOthers = true;
					break;
				}
				
			}
			if(selectedOthers)
			{
				recordQueryBuf.append("and (recordToExportDE.servedPopulation in (:populationList) ")
				.append("or recordToExportDE.outcomePopulation in (:populationList)")
				.append("or (recordToExportDE.servedPopulation is null ")
				.append("and recordToExportDE.outcomePopulation is null))");
			}
		else
			{
				recordQueryBuf.append("and (recordToExportDE.servedPopulation in (:populationList) ")
				.append("or recordToExportDE.outcomePopulation in (:populationList))");
			}
		}
		else if(populationList.size() == 0 && populations.size() == 1)
		{
			recordQueryBuf.append("and (recordToExportDE.servedPopulation is null ")
			.append("and recordToExportDE.outcomePopulation is null)");
		}
		
		/*recordQuery = "select count(recordToExportDE) "
         + "from RecordToExport as recordToExportDE, " 
         +	"State as state "
         + "where  recordToExportDE.state = state.abbreviation and " 
         + "state.stateName in (:stateList) "
         + "and recordToExportDE.reportingPeriod in (:reportingPeriodList) "
         + "and (recordToExportDE.servedPopulation in (:populationList) "
         + "or recordToExportDE.outcomePopulation in (:populationList))";*/
		
		recordQuery = recordQueryBuf.toString();


		   Object obj = null;  
		   Query query = getSessionFactory().getCurrentSession().createQuery(recordQuery)
		          .setParameterList("stateList", stateList, new StringType())
		          .setParameterList("reportingPeriodList", reportingPeriodList, new StringType());
		   if(cohorts!=null && cohorts.size()>0)
			   query.setParameterList("cohortList", cohorts, new LongType());
			if(populationList.size() >0)
		          query.setParameterList("populationList", populationList, new StringType());
		        obj =   query.uniqueResult();
		   
		   return Integer.parseInt(obj.toString());
	}
        
        /*
        *@see DataExtractionService#dataCountForPenaltyLetters(DataTable, Collection<Long>, Collection<Long>)
        */
        @Override
        public int dataCountForPenaltyLetters(DataTable dataTable, Collection<Long> reportingPeriods, Collection<Long> states) {
		List<String> reportingPeriodList = this.getReportingPeriods(reportingPeriods);
		List<String> stateList = this.getStates(states);
		String recordQuery = null;
		StringBuffer recordQueryBuf = null;
		List<RecordToExport> records = null;
		
		recordQueryBuf = new StringBuffer()
		.append("select count(recordToExportDE)")
		.append("from RecordToExport as recordToExportDE, ")
		.append("State as state ")
		.append("where  recordToExportDE.state = state.abbreviation and ")
		.append("state.stateName in (:stateList) ")
		.append("and recordToExportDE.reportingPeriod in (:reportingPeriodList) ");
		
		recordQuery = recordQueryBuf.toString();

		   Object obj = null;  
		   Query query = getSessionFactory().getCurrentSession().createQuery(recordQuery)
		          .setParameterList("stateList", stateList, new StringType())
		          .setParameterList("reportingPeriodList", reportingPeriodList, new StringType());
		        obj =   query.uniqueResult();
		   
		   return Integer.parseInt(obj.toString());
	}
	
	/// @see DataExtractionService#compileData(DataTable, Collection, Collection, Collection)
	@SuppressWarnings("unchecked")
	@Override
	public DataTable compileData(DataTable dataTable, Collection<Long> reportingPeriods,
			Collection<Long> states, Collection<Long> populations,Collection<Long> cohorts,int pageNumber,
         int pageSize)
	{
		List<String> reportingPeriodList = this.getReportingPeriods(reportingPeriods);
		List<String> stateList = this.getStates(states);
		String recordQuery = null;
		StringBuffer recordQueryBuf = null;
		List<RecordToExport> records = null;

		List<String> populationList = this.getPopulationList(populations);
		


		/*
		undeterminedPopClause = "";
		if (populationIds.remove(Long.valueOf(undeterminedPopId.longValue())))
		{
			undeterminedPopClause = " or (outcomePop is null and servedPop is null) ";
		}

		searchRecordQuery = String.format(searchRecordQuery, stateQuery,
		                                  reportPeriodQuery, undeterminedPopClause);
		*/

		/*recordQuery = "select recordToExport "
            + "from RecordToExport as recordToExport, " 
            +	"State as state "
            + "where  recordToExport.state = state.abbreviation and " 
            + "state.stateName in (:stateList) "
            + "and recordToExport.reportingPeriod in (:reportingPeriodList) "
            + "and (recordToExport.servedPopulation in (:populationList) "
            + "or recordToExport.outcomePopulation in (:populationList))";*/
		
		recordQueryBuf = new StringBuffer()
		.append("select distinct recordToExportDE ")
		.append("from RecordToExport as recordToExportDE, ")
		.append("State as state ")
		.append("where  recordToExportDE.state = state.abbreviation and ")
		.append("state.stateName in (:stateList) ")
		.append("and recordToExportDE.reportingPeriod in (:reportingPeriodList) ");
		if(cohorts!=null  & cohorts.size() > 0)
			recordQueryBuf.append("and recordToExportDE.cohortId in (:cohortList) ");
		if(populationList.size()>0)
		{
			
			Iterator itr = populations.iterator();
			boolean selectedOthers = false;
			while(itr.hasNext())
			{
				
				if( itr.next().toString().equals("7"))
				{
					selectedOthers = true;
					break;
				}
				
			}
			if(selectedOthers)
				{
					recordQueryBuf.append("and (recordToExportDE.servedPopulation in (:populationList) ")
					.append("or recordToExportDE.outcomePopulation in (:populationList)")
					.append("or (recordToExportDE.servedPopulation is null ")
					.append("and recordToExportDE.outcomePopulation is null))");
				}
			else
				{
					recordQueryBuf.append("and (recordToExportDE.servedPopulation in (:populationList) ")
					.append("or recordToExportDE.outcomePopulation in (:populationList))");
				}
		}
		else if(populationList.size() == 0 && populations.size() == 1)
		{
			recordQueryBuf.append("and (recordToExportDE.servedPopulation is null ")
			.append("and recordToExportDE.outcomePopulation is null)");
		}
		
	/*	recordQuery = "select recordToExportDE "
         + "from RecordToExport as recordToExportDE, " 
         +	"State as state "
         + "where  recordToExportDE.state = state.abbreviation and " 
         + "state.stateName in (:stateList) "
         + "and recordToExportDE.reportingPeriod in (:reportingPeriodList) "
         + "and (recordToExportDE.servedPopulation in (:populationList) "
         + "or recordToExportDE.outcomePopulation in (:populationList))";*/
		
		recordQuery = recordQueryBuf.toString();
		
		
	/*	recordQuery = "select recordToExport "
         + "from RecordToExport as recordToExport inner join  " 
         +	"State as state  on recordToExport.state = state.abbreviation "
         +	"left outer join ReportingPeriod as RP on recordToExport.reportingPeriod = RP.Name "
         + "where  recordToExport.state = state.abbreviation and " 
         + "state.id in (:stateList) "
         + "and RP.id in (:reportingPeriodList) "
         + "and (recordToExport.servedPopulation in (:populationList) "
         + "or recordToExport.outcomePopulation in (:populationList))";*/

		/*Criteria crit = getSessionFactory().getCurrentSession().createCriteria(RecordToExport.class)
		.setFetchMode("transmissionRecord", FetchMode.JOIN)
		.setFetchMode("transmission", FetchMode.JOIN)
		.setFetchMode("stateobj", FetchMode.JOIN);
		crit.createCriteria("transmissionRecord").setCacheable(false);
		crit.createCriteria("transmission");
		crit.createCriteria("stateobj").add(Restrictions.in("abbreviation", stateList));
		crit.add(Restrictions.in("reportingPeriod", reportingPeriodList));	
		crit.add( Restrictions.or(Restrictions.in("servedPopulation", populationList), Restrictions.in("outcomePopulation", populationList)));
*/		
		
		
		records = null;
		Query query = getSessionFactory().getCurrentSession().createQuery(recordQuery)
		          .setParameterList("stateList", stateList, new StringType())
		          .setParameterList("reportingPeriodList", reportingPeriodList, new StringType());
		if(cohorts!=null  & cohorts.size() > 0)
				query.setParameterList("cohortList", cohorts, new LongType());
		if(populationList.size() >0)         
			query.setParameterList("populationList", populationList, new StringType());
		records = query.setFirstResult((pageNumber * pageSize))
					 .setMaxResults(pageSize)
					 .setCacheable(false)
					 .list();
		//records = crit.list();
		
		// Parse the data and add it to the data table.
		int i = 0;
	//	System.out.println("size of records list:"+ records.size());
		if(records.size() >0)
		{
			for (RecordToExport record : records)
			{
				for (TableFieldBean field : dataTable.getFields())
				{
					TableDatumBean tableDatumBean;
					String datumValue = "";
					Map.Entry<String, Boolean> elementInfo;
					String elementName;
					Boolean isNote;
	
					elementInfo = extractElementInfoFromFieldName(field.getName());
	
					if (elementInfo != null) // field corresponds to an element
					{
						elementName = elementInfo.getKey();
						isNote = elementInfo.getValue();
	
						if (!isNote)
						{
							datumValue = record.getElementValue(elementName);
						}
						else
						{
							datumValue = record.getElementNote(elementName);
						}
					}
					else // field doesn't correspond to an element (general fields)
					{
						if (field.getLabel().equalsIgnoreCase(getGeneralFields().get("1"))) // File Number
						{
						//	datumValue = String.valueOf(record.getTransmission().getId());
							datumValue = String.valueOf(record.getTransId());
						}
						else if(field.getLabel().equalsIgnoreCase(getGeneralFields().get("2"))) //Served 
						{
							datumValue = record.getServedPopulation() != null ? Constants.STR_1: Constants.STR_0;
						}
						else if(field.getLabel().equalsIgnoreCase(getGeneralFields().get("3"))) //Baseline 
						{
							datumValue = "Baseline".equalsIgnoreCase(record.getOutcomePopulation()) ? Constants.STR_1: Constants.STR_0;
							//datumValue = String.valueOf(record.getOutcomePopulations().getBaseline());
						}
						else if(field.getLabel().equalsIgnoreCase(getGeneralFields().get("4"))) //Follow-up 19 
						{
							datumValue = "Follow-up 19".equalsIgnoreCase(record.getOutcomePopulation()) ? Constants.STR_1: Constants.STR_0;
							//datumValue = String.valueOf(record.getOutcomePopulations().getFollowUp19());
						}
						else if(field.getLabel().equalsIgnoreCase(getGeneralFields().get("5"))) //Follow-up 21 
						{
							datumValue = "Follow-up 21".equalsIgnoreCase(record.getOutcomePopulation()) ? Constants.STR_1: Constants.STR_0;
							//datumValue = String.valueOf(record.getOutcomePopulations().getFollowUp21());
						}
						else if(field.getLabel().equalsIgnoreCase(getGeneralFields().get("6"))) //pre-buffer 
						{
							datumValue = "Pre-buffer".equalsIgnoreCase(record.getOutcomePopulation()) ? Constants.STR_1: Constants.STR_0;
						}
						else if(field.getLabel().equalsIgnoreCase(getGeneralFields().get("7"))) //post-buffer 
						{
							datumValue = "Post-buffer".equalsIgnoreCase(record.getOutcomePopulation()) ? Constants.STR_1: Constants.STR_0;
						}
						else if(field.getLabel().equalsIgnoreCase(getGeneralFields().get("8"))) //post-buffer 
						{
							datumValue = record.getInCohort() == 1L ? Constants.STR_1: Constants.STR_0;
						}
						else if(field.getLabel().equalsIgnoreCase(getGeneralFields().get("9"))) //post-buffer 
						{
							datumValue = record.getInSample() == 1L ? Constants.STR_1: Constants.STR_0;
						}
						else
						{
							assert(false); // We should never reach an unknown field.
							continue;
						}
					}
	
					// Now that all the hard work is done, actually add it.
					tableDatumBean = new TableDatumBean(field.getLabel(), new Integer(i), datumValue);
					dataTable.add(tableDatumBean);
				}
				++i;
			}
		}
		else
		{
			for (TableFieldBean field : dataTable.getFields())
			{
				TableDatumBean tableDatumBean = new TableDatumBean(field.getLabel(), new Integer(i), "");
				dataTable.add(tableDatumBean);
			}
			
		}
//		System.out.println("******************************************count of records : "+i);
		return dataTable;
	}

	/// @see DataExtractionService#getSPSSFile(DataTable)
	public InputStream getSPSSFile(final DataTable dataTable,boolean startOfRecs, boolean endOfRecs)
	{
		// Create stream that will contain the file.
		PipedInputStream in = new PipedInputStream();
		final PipedOutputStream out;
		try
		{
			out = new PipedOutputStream(in);
		}
		catch (IOException e)
		{
			log.error(e.getMessage(), e);
			return in;
		}

		// Have to create a thread because Java doesn't want PipedInputStream
		// and PipedOutputStream being operated on in the same thread.
		new Thread(new SPSSWriterThread(dataTable,out,startOfRecs, endOfRecs)).start();

		return in;
	}

	/// @see DataExtractionService#getRSTFile(DataTable)
	public InputStream getRSTFile(final DataTable dataTable,boolean startofRecs, boolean endofRecs)
	{
		// Create stream that will contain the file.
		PipedInputStream in = new PipedInputStream();
		final PipedOutputStream out;
		try
		{
			out = new PipedOutputStream(in);
		}
		catch (IOException e)
		{
			log.error(e.getMessage(), e);
			return in;
		}

		// Have to create a thread because Java doesn't want PipedInputStream
		// and PipedOutputStream being operated on in the same thread.
		new Thread(
			new Runnable()
			{
				public void run()
				{
					BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
					final int rowCount = dataTable.getRowCount();
					final int fieldCount = dataTable.getFields().size();
					List<Integer> fieldWidths = new ArrayList<Integer>(fieldCount);
					int i, j;

					// To facilitate writing of RST grid tables.
					class MyGridTableWriter
					{
						void writeGridLine(Iterable<Integer> fieldWidths, String horizLineChar, BufferedWriter writer)
						{
							int i, j;

							i = 0;
							for (Integer fieldWidth : fieldWidths)
							{
								Boolean isLast = Boolean.valueOf(false);
								if (i == (fieldCount - 1))
								{
									isLast = Boolean.valueOf(true);
								}

								try
								{
									writer.write("+");
								}
								catch (IOException e)
								{
									log.error(e.getMessage(), e);
								}

								for (j = 0; j < (fieldWidth.intValue() + 2); ++j)
								{
									try
									{
										writer.write(horizLineChar);
									}
									catch (IOException e)
									{
										log.error(e.getMessage(), e);
									}
								}

								if (isLast.booleanValue())
								{
									try
									{
										writer.write("+");
									}
									catch (IOException e)
									{
										log.error(e.getMessage(), e);
									}
								}

								++i;
							}
							try
							{
								writer.newLine();
							}
							catch (IOException e)
							{
								log.error(e.getMessage(), e);
							}
						}

						void writeGridLine(Iterable<Integer> fieldWidths, BufferedWriter writer)
						{
							writeGridLine(fieldWidths, "-", writer);
						}

						void writeHeaderGridLine(Iterable<Integer> fieldWidths, BufferedWriter writer)
						{
							writeGridLine(fieldWidths, "=", writer);
						}

						void writeValue(String content, Integer fieldWidth, Boolean isLast, Writer writer)
						{
							String formatString = "%-" + fieldWidth.intValue() + "s";

							try
							{
								writer.write("| ");
							}
							catch (IOException e)
							{
								log.error(e.getMessage(), e);
							}

							if (content == null)
							{
								content = "";
							}

							try
							{
								writer.write(String.format(formatString, content));
							}
							catch (IOException e)
							{
								log.error(e.getMessage(), e);
							}

							try
							{
								writer.write(" ");
							}
							catch (IOException e)
							{
								log.error(e.getMessage(), e);
							}

							if (isLast.booleanValue())
							{
								try
								{
									writer.write("|");
								}
								catch (IOException e)
								{
									log.error(e.getMessage(), e);
								}
							}
						}
					}
					MyGridTableWriter myWriter = new MyGridTableWriter();

					// Initialize field widths to 0.
					for(i = 0; i < fieldCount; ++i)
					{
						fieldWidths.add(Integer.valueOf(0));
					}

					// Calculate width of fields against maximums.
					i = 0;
					for (TableFieldBean field : dataTable.getFields())
					{
						Integer currMax = fieldWidths.get(i);
						Integer currWidth;
						try
						{
							currWidth = Integer.valueOf(field.label.length());
						}
						catch (NullPointerException e)
						{
							currWidth = 0;
						}

						if (currWidth > currMax)
						{
							fieldWidths.set(i, currWidth);
						}

						++i;
					}

					// Calculate width of data values against maximums.
					for(j = 0; j < rowCount; ++j)
					{
						Iterable<TableDatumBean> row = dataTable.getRow(j);

						i = 0;
						for (TableDatumBean datum : row)
						{
							Integer currMax = fieldWidths.get(i);
							Integer currWidth;
							try
							{
								currWidth = Integer.valueOf(datum.value.trim().length());
							}
							catch (NullPointerException e)
							{
								currWidth = 0;
							}

							if (currWidth > currMax)
							{
								fieldWidths.set(i, currWidth);
							}

							++i;
						}
					}

					// Write first grid line above fields/headers.
					myWriter.writeGridLine(fieldWidths, writer);

					// Write the fields as a header line.
					i = 0;
					for (TableFieldBean field : dataTable.getFields())
					{
						String content = field.label;
						Integer fieldWidth = fieldWidths.get(i);
						Boolean isLast = Boolean.valueOf(false);

						if (i == (fieldCount - 1))
						{
							isLast = Boolean.valueOf(true);
						}

						myWriter.writeValue(content, fieldWidth, isLast, writer);

						++i;
					}
					try
					{
						writer.newLine();
					}
					catch (IOException e)
					{
						log.error(e.getMessage(), e);
					}

					// Write the line separating the header line from the data.
					myWriter.writeHeaderGridLine(fieldWidths, writer);

					// Write the data values.
					for(i = 0; i < rowCount; ++i)
					{
						Iterable<TableDatumBean> row = dataTable.getRow(i);

						j = 0;
						for (TableDatumBean datum : row)
						{
							String content = datum.value;
							if (content != null)
							{
								content = content.trim();
							}
							Integer fieldWidth = fieldWidths.get(j);
							Boolean isLast = Boolean.valueOf(false);

							if (j == (fieldCount - 1))
							{
								isLast = Boolean.valueOf(true);
							}

							myWriter.writeValue(content, fieldWidth, isLast, writer);

							++j;
						}
						try
						{
							writer.newLine();
						}
						catch (IOException e)
						{
							log.error(e.getMessage(), e);
						}

						// Write a grid line.
						myWriter.writeGridLine(fieldWidths, writer);
					}

					// Clean up the writer.
					if (writer != null)
					{
						try
						{
							writer.flush();
							writer.close();
						}
						catch (IOException e)
						{
							log.error(e.getMessage(), e);
						}
					}
				}
			}
		).start();

		return in;
	}

	/// @see DataExtractionService#getCSVFile(DataTable)
	public InputStream getCSVFile(final DataTable dataTable,boolean startOfRecs, boolean endOfRecs)
	{
		// Create stream that will contain the file.
		PipedInputStream in = new PipedInputStream();
		final PipedOutputStream out;
		try
		{
			out = new PipedOutputStream(in);
		}
		catch (IOException e)
		{
			log.error(e.getMessage(), e);
			return in;
		}

		// Have to create a thread because Java doesn't want PipedInputStream
		// and PipedOutputStream being operated on in the same thread.
		new Thread( new CVSWriterThread(dataTable,out,startOfRecs, endOfRecs)).start();

		return in;
	}

 	/**
	 * @see DataExtractionService#getXLSFile(DataTable)
	 */
	public InputStream getXLSFile(final DataTable dataTable,boolean startOfRecs, boolean endOfRecs)
	{
		// Create stream that will contain the file.
		PipedInputStream in = new PipedInputStream();
		final PipedOutputStream out;
		try
		{
			out = new PipedOutputStream(in);
		}
		catch (IOException e)
		{
			log.error(e.getMessage(), e);
			return in;
		}
		
		// Have to create a thread because Java doesn't want PipedInputStream
		// and PipedOutputStream being operated on in the same thread.
		new Thread(new XLSWriterThread(dataTable,out,startOfRecs, endOfRecs)).start();

		return in;
	}
	
	/**
	 * @see DataExtractionService#getHTMFile(DataTable)
	 */
	public InputStream getHTMFile(final DataTable dataTable,boolean startOfRecs, boolean endOfRecs)
	{
		// Create stream that will contain the file.
		PipedInputStream in = new PipedInputStream();
		final PipedOutputStream out;
		try
		{
			out = new PipedOutputStream(in);
		}
		catch (IOException e)
		{
			log.error(e.getMessage(), e);
			return in;
		}

		// Have to create a thread because Java doesn't want PipedInputStream
		// and PipedOutputStream being operated on in the same thread.
		new Thread(new HTMLWriterThread(dataTable,out,startOfRecs, endOfRecs)).start();

		return in;
	}

	
	/// @see DataExtractionService#getFileTypes()
	public Map<String, String> getFileTypes()
	{
		return fileTypes;
	}

	/// @see gov.hhs.acf.cb.nytd.service.DataExtractionService#getReportingPeriods(SiteUser)
	@Override
	public Map<String, String> getReportingPeriods(SiteUser siteUser)
	{
		return populateSearchCriteriaService.getReportPeriodSelectMapForUser(siteUser);
	}

	/// @see gov.hhs.acf.cb.nytd.service.DataExtractionService#getStates(SiteUser)
	@Override
	public Map<String, String> getStates(SiteUser siteUser)
	{
		return populateSearchCriteriaService.getStateSelectMapForUser(siteUser);
	}

	/// @see gov.hhs.acf.cb.nytd.service.DataExtractionService#getPopulations()
	@Override
	public Map<String, String> getPopulations()
	{
		
		Map<String, String> populations = new LinkedHashMap<String, String>();
	
		Session session = getSessionFactory().getCurrentSession();
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Population> criteriaQuery = criteriaBuilder.createQuery(Population.class);
		Root<Population> root = criteriaQuery.from(Population.class);
		criteriaQuery.select(root);
		TypedQuery<Population> q = session.createQuery(criteriaQuery);			
		List<Population> queryResults = (List<Population>) q.getResultList();

		for (Population queryResult : queryResults)
		{
			Long popId = queryResult.getId();
			populations.put(String.valueOf(popId), queryResult.getName());
		}

		populations.put(String.valueOf(Long.valueOf(getUndeterminedPopId())), "Other");

		return populations;
	}

	public Map<String, String> getGeneralFields()
	{
		return generalFields;
	}

	/**
	 * Gets a map of all the element number and descriptions.
	 *
	 * @return map of element numbers (keys) and descriptions (values)
	 */
	public Map<String, String> getFields()
	{
			List<Element> elements = null;
			// initialize fields
		//	elements = asessionFactory.getCurrentSession().createCriteria(Element.class).list();
			elements = getElementsList();
			if(elements == null)
			{
				CriteriaBuilder criteriaBuilder = asessionFactory.getCurrentSession().getCriteriaBuilder();
				CriteriaQuery<Element> criteriaQuery = criteriaBuilder.createQuery(Element.class);
				Root<Element> root = criteriaQuery.from(Element.class);
				criteriaQuery.select(root);
				TypedQuery<Element> q = asessionFactory.getCurrentSession().createQuery(criteriaQuery);	
				elements = q.getResultList();
			}
			for (Element element : elements)
			{
				if(fields.get(element.getName()) == null)
					fields.put(element.getName(), element.getDescription());
			}

		assert(fields.size() == Constants.NUM_ELEM_FIELDS);
		return fields;
	}

	/// @see gov.hhs.acf.cb.nytd.service.DataExtractionService#getDemographics()
	@Override
	public Map<String, String> getDemographics()
	{
		String curr = null;
		// initialize demographics
		for (int i = 0; i < Constants.NUM_DEMOGRAPHIC_FIELDS; ++i)
				{
					curr = String.valueOf(Constants.DEMOGRAPHIC_FIELDS[i]);
					if(demographics.get(curr) == null)
						demographics.put(curr, getElementLabel(curr));
				}
		assert(demographics.size() == Constants.NUM_DEMOGRAPHIC_FIELDS);
		return demographics;
	}

	/// @see gov.hhs.acf.cb.nytd.service.DataExtractionService#getCharacteristics()
	@Override
	public Map<String, String> getCharacteristics()
	{
		String curr = null;
		
			
			// initialize characteristics	
			for (int i = 0; i < Constants.NUM_CHARACTERISTIC_FIELDS; ++i)
			{
				curr = String.valueOf(Constants.CHARACTERISTIC_FIELDS[i]);
				if(characteristics.get(curr) == null)
					characteristics.put(curr, getElementLabel(curr));
			}
		assert(characteristics.size() == Constants.NUM_CHARACTERISTIC_FIELDS);
		return characteristics;
	}

	/// @see gov.hhs.acf.cb.nytd.service.DataExtractionService#getServices()
	@Override
	public Map<String, String> getServices()
	{
		String curr = null;
				
			// initialize services
		for (int i = 0; i < Constants.NUM_SERVICE_FIELDS; ++i)
			{
				curr = String.valueOf(Constants.SERVICE_FIELDS[i]);
				if(services.get(curr) == null)
					services.put(curr, getElementLabel(curr));
			}

		
		assert(services.size() == Constants.NUM_SERVICE_FIELDS);
		return services;
	}

	/// @see gov.hhs.acf.cb.nytd.service.DataExtractionService#getOutcomes()
	@Override
	public Map<String, String> getOutcomes()
	{
		String curr = null;
		// initialize outcomes
			for (int i = 0; i < Constants.NUM_OUTCOME_FIELDS; ++i)
			{
				curr = String.valueOf(Constants.OUTCOME_FIELDS[i]);
				if(outcomes.get(curr) == null)
					outcomes.put(curr, getElementLabel(curr));
			}
		
		assert(outcomes.size() == Constants.NUM_OUTCOME_FIELDS);
		return outcomes;
	}

	/// @see gov.hhs.acf.cb.nytd.service.DataExtractionService#getElementLabel(String)
	@Override
	public String getElementLabel(String elementNumber)
	{
		String label = getFields().get(elementNumber);
		label += " (" + String.valueOf(elementNumber) + ")";
		return label;
	}

	/// @see gov.hhs.acf.cb.nytd.service.DataExtractionService#getNoteLabel(String)
	@Override
	public String getNoteLabel(String elementNumber)
	{
		String label = getElementLabel(elementNumber);
		label += " Note";
		return label;
	}

	/// @see gov.hhs.acf.cb.nytd.service.DataExtractionService#getShortElementName(String)
	@Override
	public String getShortElementName(String elementNumber)
	{
		String name = String.valueOf(elementNumber);
		name = "E" + name;
		return name;
	}

	/// @see gov.hhs.acf.cb.nytd.service.DataExtractionService#getShortNoteName(String)
	@Override
	public String getShortNoteName(String elementNumber)
	{
		String name = getShortElementName(elementNumber);
		name += "Note";
		return name;
	}

	/// @see DataExtractionService#getShortGeneralFieldName(String)
	@Override
	public String getShortGeneralFieldName(String generalFieldKey)
	{
		String name = getGeneralFields().get(generalFieldKey);
		if (name.length() > 8 &&  name.contains("up"))
		{
			name = name.substring(0, 6) + name.substring(name.length() - 2, name.length());
		}
		return name;
	}

	/// @see DataExtractionService#getFullGeneralFieldLabel(String)
	@Override
	public String getFullGeneralFieldName(String generalFieldKey)
	{
		String label = getGeneralFields().get(generalFieldKey);
		return label;
	}

	/**
	 * Gets the database ID of the baseline population.
	 *
	 * Initializes this value if it hasn't already been done.
	 *
	 * @return database ID of the baseline population
	 */
	/*
	@SuppressWarnings("unused")
	private Long getBaselinePopId()
	{
		Population targetPopulation = null;
		// initialize baselinePopId
		targetPopulation = (Population) asessionFactory.getCurrentSession().createCriteria(Population.class)
		.add(Restrictions.ilike("name", "Baseline"))
		.uniqueResult();

		//criteria =  DetachedCriteria.forClass(Population.class)
		//	.add(Restrictions.ilike("name", "Baseline"));
		//targetPopulation = getBaseDAO().findUnique(Population.class, criteria);
		
		baselinePopId = targetPopulation.getId();
		
		return this.baselinePopId;
	}
	*/

	/**
	 * Gets the database IDs of the buffer baseline populations.
	 *
	 * Initializes this collection if it hasn't already been done.
	 *
	 * @return database IDs of the buffer baseline populations
	 */
	/*
	@SuppressWarnings("unused")
	private Collection<Long> getBufferPopIds()
	{
	// initialize bufferPopIds
		Population targetPopulation = null;
		targetPopulation = (Population)asessionFactory.getCurrentSession().createCriteria(Population.class)
			.add(Restrictions.ilike("name", "Pre-buffer")).uniqueResult();
		bufferPopIds.add(targetPopulation.getId()); // pre-buffer
		targetPopulation = (Population)  asessionFactory.getCurrentSession().createCriteria(Population.class)
			.add(Restrictions.ilike("name", "Post-buffer")).uniqueResult();
		bufferPopIds.add(targetPopulation.getId()); // post-buffer
		return this.bufferPopIds;
	}
	*/

	/**
	 * Gets the ID/key of the "Other" population.
	 *
	 * This represents records that have no outcomes or served population.
	 * Initializes this value if it hasn't already been done.
	 *
	 * @return database ID of the baseline population
	 */
	private Long getUndeterminedPopId()
	{
		
		List<Population> populations = null;
		Long maxPopId = 0l;
		
		Session session = getSessionFactory().getCurrentSession();
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Population> criteriaQuery = criteriaBuilder.createQuery(Population.class);
		Root<Population> root = criteriaQuery.from(Population.class);
		criteriaQuery.select(root);
		TypedQuery<Population> q = session.createQuery(criteriaQuery);	
		populations = (List<Population>)q.getResultList();
		
		maxPopId = Long.valueOf(0);
		for (Population population : populations)
		{
			Long popId = population.getId();
			if (popId.compareTo(maxPopId) > 0)
			{
				maxPopId = popId;
			}
		}
		undeterminedPopId = Long.valueOf(maxPopId.intValue() + 1);
		
		return this.undeterminedPopId;
	}

	/**
	 * Determine an element number and whether or not a field is for a note given a field name.
	 *
	 * @param fieldName short name of a field as found in a TableDatumBean
	 * @return element number and whether or not the field is for a note
	 */
	private Map.Entry<String, Boolean> extractElementInfoFromFieldName(String fieldName)
	{
		if (fieldName.startsWith("E"))
		{
			String k;
			Boolean v;

			if (fieldName.endsWith("Note"))
			{
				k = fieldName.substring(1, fieldName.indexOf("Note"));
				v = Boolean.valueOf(true);
			}
			else
			{
				k = fieldName.substring(1);
				v = Boolean.valueOf(false);
			}

			return new AbstractMap.SimpleImmutableEntry<String, Boolean>(k, v);
		}
		return null;
	}

	/**
	 * Return a list of ExportMetadata objects representing previously submitted exports.
	 *
	 * @return List<Exportmetadata>
	 */
	public List<ExportMetadata> getPreviousDataExports() {
		List<ExportMetadata> exportList = new LinkedList<ExportMetadata>();
        List<ExportMetadata> ignoreList = new LinkedList<ExportMetadata>();
		try
		{	
			Session session = getSessionFactory().getCurrentSession();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<ExportMetadata> criteriaQuery = criteriaBuilder.createQuery(ExportMetadata.class);
			Root<ExportMetadata> root = criteriaQuery.from(ExportMetadata.class);
			criteriaQuery.orderBy(criteriaBuilder.desc(root.get("createdDate")));
			criteriaQuery.select(root);
			TypedQuery<ExportMetadata> q = session.createQuery(criteriaQuery);	
			exportList = (List<ExportMetadata>)q.getResultList();
			
			Iterator<ExportMetadata> iter = exportList.iterator();
			while (iter.hasNext()) {
				ExportMetadata em = iter.next();
				String fullFilename = exportLocation + em.getFileName();
				try {
					File fileToDownload = new File(new URI(fullFilename));
					if (!fileToDownload.exists()){
						File tempFile = new File(new URI(fullFilename + ".tmp"));
						if (tempFile.exists()) {
							em.setStatus(Constants.EXPORT_IN_PROCESS);
						}
						else {
							em.setStatus(Constants.EXPORT_DNE);
                            ignoreList.add(em);
						}
					}
					else em.setStatus(Constants.EXPORT_COMPLETE);
				}catch(Exception fileException) {
					fileException.printStackTrace();
				}
			}
            // remove metadata if file does not exist
            exportList.removeAll(ignoreList);
		}
		catch (Exception e)
		{
			log.error(" Error in the getPreviousDataExports method" + e);
			e.printStackTrace();
		}
		return exportList;
	}

	/**
	 * Saves export metadata to the database.
	 *
	 * @param exportCriteria map containing export criteria
	 */
	public void writeExportMetadata(Map exportCriteria) {
		
		GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("America/New_York"), Locale.US);

		String fileName = (String)exportCriteria.get("fileName");
		String fileType = (String)exportCriteria.get("fileType");

		Collection<String> generalFields = (Collection<String>)exportCriteria.get("generalFields");
		Collection<String> demographics = (Collection<String>)exportCriteria.get("demographics");
		Collection<String> characteristics = (Collection<String>)exportCriteria.get("characteristics");
		Collection<String> services = (Collection<String>)exportCriteria.get("services");
		Collection<String> outcomes = (Collection<String>)exportCriteria.get("outcomes");
		Collection<String> demographicsNotes = (Collection<String>)exportCriteria.get("demographicsNotes");
		Collection<String> characteristicsNotes = (Collection<String>)exportCriteria.get("characteristicsNotes");
		Collection<String> servicesNotes = (Collection<String>)exportCriteria.get("servicesNotes");
		Collection<String> outcomesNotes = (Collection<String>)exportCriteria.get("outcomesNotes");

		Collection<String> reportingPeriods = (Collection<String>)exportCriteria.get("reportingPeriods");
		Collection<String> states = (Collection<String>)exportCriteria.get("states");
		Collection<String> populations = (Collection<String>)exportCriteria.get("populations");
		SiteUser siteUser = (SiteUser) exportCriteria.get("siteUser");

		ExportMetadata metadata = new ExportMetadata();
		metadata.setCreatedDate(calendar);
		metadata.setFileName(fileName);
		metadata.setFileType(fileTypes.get(fileType));
		metadata.setDemographics(convertExportMetaDataToString(demographics,13,"field"));
		metadata.setCharacteristics(convertExportMetaDataToString(characteristics,9,"field"));
		metadata.setIndependentLivingServices(convertExportMetaDataToString(services,14,"field"));
		metadata.setYouthOutcomeSurveys(convertExportMetaDataToString(outcomes,22,"field"));
		metadata.setDemographicNotes(convertExportMetaDataToString(demographicsNotes,11,"note"));
		metadata.setCharacteristicNotes(convertExportMetaDataToString(characteristicsNotes,9,"note"));
		metadata.setIndependentLivingServiceNotes(convertExportMetaDataToString(servicesNotes,14,"note"));
		metadata.setYouthOutcomeSurveyNotes(convertExportMetaDataToString(outcomesNotes,22,"note"));
		metadata.setUserName(siteUser.getUserName());
		List<String> reportPeriodList;
		try
		{
			reportPeriodList = getReportingPeriods(CommonFunctions
					.convertCollectionOfStringsToLongs(reportingPeriods));
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
			e.printStackTrace();
			return;
		}
		StringBuffer rpb = new StringBuffer();
		Iterator<String> rpIter = reportPeriodList.iterator();
		while (rpIter.hasNext()) {
			rpb.append(rpIter.next()).append(", ");
		}
		if (rpb.length() > 0) metadata.setReportingPeriods(rpb.substring(0, rpb.length()-2));

		List<String> stateAbbrList;
		try
		{
			stateAbbrList = getStates(CommonFunctions
					.convertCollectionOfStringsToLongs(states));
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
			e.printStackTrace();
			return;
		}
		StringBuffer ssb = new StringBuffer();
		if (stateAbbrList.size() == 52) {
			metadata.setStates(Constants.EXPORT_ALL);
		}
		else {
			Iterator<String> stateIter = stateAbbrList.iterator();
			while (stateIter.hasNext()) {
				ssb.append(stateIter.next()).append(", ");
			}

			if (ssb.length() > 0)
				metadata.setStates(ssb.substring(0, ssb.length()-2));
			else
				metadata.setStates(Constants.EXPORT_NONE);
		}

		try {
			metadata.setPopulations(listToString(getPopulationList((CommonFunctions
					.convertCollectionOfStringsToLongs(populations)))));
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
			return;
		}
		// metadata.setTransmissionIds(getTransmissionIds(getStates(states), getReportingPeriods(reportingPeriods)));
		//getBaseDAO().saveOrUpdate(metadata);
		getSessionFactory().getCurrentSession().saveOrUpdate(metadata);

	}

	/**
	 * Returns a String representation of the Collection<String> export parameter.
	 *
	 * @param exportCriteriaCollection the Collection<String> export parameter
	 * @param maxSelection the int maximum number of elements of the export parameter.
	 * @param type the String type of the export parameter (field or note).
	 * @return String representation of the passed in export parameter
	 */
	private String convertExportMetaDataToString(Collection<String> exportCriteriaCollection, int maxSelection, String type) {
		if (exportCriteriaCollection.size() == maxSelection) {
			return Constants.EXPORT_ALL;
		}
		else {
			StringBuffer sb = new StringBuffer();
			Iterator<String> iter = exportCriteriaCollection.iterator();
			while (iter.hasNext()) {
				String i = iter.next();
				if (type.equals("field"))
					sb.append(getElementLabel(i)).append(", ");
				if (type.equals("note"))
					sb.append(getNoteLabel(i)).append(", ");
			}
			if (sb.length() > 0)
				return sb.substring(0, sb.length()-2);
			else
				return Constants.EXPORT_NONE;
		}
	}

	/**
	 * Returns a list of state names based on the numerical value of a state
	 *
	 * @param stateIds the Collection<Long> of states
	 * @return List<String> of state names
	 */
	@SuppressWarnings("unchecked")
	private List<String> getStates(Collection<Long> stateIds) {
		List<String> stateAbbreviations;
		String stateQuery;

		stateQuery = "select state.stateName from State as state where state.id in (:stateIds) ";

		stateAbbreviations = getSessionFactory().getCurrentSession()
								.createQuery(stateQuery)
								.setParameterList("stateIds", stateIds, new LongType())
								.list();

		return stateAbbreviations;
	}

	
	/**
	 * Returns a list of reporting periods based on the numerical value of a reporting period
	 *
	 * @param periodIntegers the Collection<Long> of reporting periods
	 * @return List<String> of reporting periods
	 */
	@SuppressWarnings("unchecked")
	private List<String> getReportingPeriods(Collection<Long> periodIds) {
		List<String> reportingPeriods;
		String reportPeriodQuery;

		reportPeriodQuery = "select reportPeriod.name from ReportingPeriod as reportPeriod where reportPeriod.id in (:reportPdIds) ";

		reportingPeriods = getSessionFactory().getCurrentSession()
								.createQuery(reportPeriodQuery)
								.setParameterList("reportPdIds", periodIds, new LongType())
								.list();

		return reportingPeriods;
	}


	
	/**
	 * Returns a String list of populations based on the numerical value of a population
	 *
	 * @param populationIds the Collection<Long> of populations
	 * @return List<String> of populations
	 */
	@SuppressWarnings("unchecked")
	private List<String> getPopulationList(Collection<Long> populationIds) {
		List<String> populations = null;
		String populationQuery;

		populationQuery = "select population.name from Population as population where population.id in (:populationIds) ";

		populations = getSessionFactory().getCurrentSession()
								.createQuery(populationQuery)
								.setParameterList("populationIds", populationIds, new LongType())
								.list();

		return populations;
	}

	/**
	 * Returns a comma-separated string representation of a list of strings
	 *
	 * @param list the List<String>
	 * @return String
	 */
	private String listToString(List<String> list) {
		StringBuffer sb = new StringBuffer();
		Iterator<String> iter = list.iterator();
		while (iter.hasNext()) {
			sb.append(iter.next()).append(", ");
		}

		if (sb.length() > 0)
			return sb.substring(0, sb.length()-2);
		else
			return null;
	}

	/**
	 * Deletes an ExportMetadata object based on the data export file name
	 *
	 * @param fileName the file name of the data export
	 */
	public void deleteExportMetadata(String fileName) {
		/*DetachedCriteria criteria = getBaseDAO().createCriteria(ExportMetadata.class);
		criteria.add(Restrictions.eq("fileName", fileName));*/

		CriteriaBuilder criteriaBuilder = getSessionFactory().getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<ExportMetadata> criteriaQuery = criteriaBuilder.createQuery(ExportMetadata.class);
		Root<ExportMetadata> root = criteriaQuery.from(ExportMetadata.class);
		criteriaQuery.where(
				criteriaBuilder.equal(root.get("fileName"), fileName)
		);
		criteriaQuery.select(root);
		TypedQuery<ExportMetadata> q = getSessionFactory().getCurrentSession().createQuery(criteriaQuery);	
		List<ExportMetadata> metadataList = q.getResultList();
		
		//getBaseDAO().find(ExportMetadata.class, criteria);
		Iterator<ExportMetadata> iter = metadataList.iterator();
		while (iter.hasNext()) {
			//getBaseDAO().delete(iter.next());
			getSessionFactory().getCurrentSession().delete(iter.next());
		}
	}

	/**
	 * @return the exportLocation
	 */
	public String getExportLocation() {
		return exportLocation;
	}

	/**
	 * @param exportLocation the exportLocation to set
	 */
	public void setExportLocation(String exportLocation) {
		this.exportLocation = exportLocation;
	}
	public SubmissionStatisticsReportHelper submissionStatisticsReport(Long reportPeriod, String sortBy, String sortOrder, boolean activeSubmissionFlag)
	{		
		
		List<?> submissionStatsReportList;
		List<SubmissionStatisticsReportHelper> results = new LinkedList<SubmissionStatisticsReportHelper>();
		if(sortBy == null)
		{
			sortBy = "stateName";
		}
		if(sortOrder == null)
		{
			sortOrder = "asc";
		}
		String submittedStatusStr = "";
		String submittedWhereClause = "";
		if(activeSubmissionFlag)
		{
			submittedStatusStr = " where Lower(Submissionstatus) = 'active' and Submitteddate is not null ";
			submittedWhereClause = "  where submissionstatus is not null ";
		}
		String reportQuery2 = "select s.statename, NVL(t.counts,0) numberOfTransmissions,NVL(t.Submitteddate, '--') Submitteddate,"
				+ " Case When Submitteddate is not null and Submitteddate <> '--' Then 'Yes' Else 'No' End submittedTheFile, "
				+ " Case When Submitteddate Is Not Null and Submitteddate <> '--' Then To_Char(t.transmissionId) Else '--' End fileNumber,  "
				+ " NVL(t.Timelyerrcnt, '--') onTime"
				+ " from state s "
				+ " left join ( "
				+ " select a.stateID, NVL(a.counts,0) counts, NVL(b.Submitteddate, '--') Submitteddate, b.transmissionId, NVL(b.Timelyerrcnt, '--') Timelyerrcnt,a.Submissionstatus "
				+ " from "
				+ " (select stateId, ReportingPeriodId, count(transmissionId) counts,Submissionstatus "
				+ " from transmission "
				+   submittedStatusStr 
				+ " group by stateId, ReportingPeriodId,Submissionstatus)a "				
				+ " left join"
				+ " (select stateid, ReportingPeriodId, transmissionId,Submissionstatus,"
				+ " NVL(TO_CHAR(FROM_TZ(CAST(Submitteddate AS TIMESTAMP), 'America/Chicago') AT TIME ZONE 'America/New_York', 'dd/mm/yyyy hh24:mi:ss'), '--') Submitteddate, "
				+ " Case When Timelyerrcnt=0 Then 'Yes' Else 'No' End Timelyerrcnt "
				+ " from transmission "
				+ " where Lower(Submissionstatus) = 'active' "
				+ " order by Submitteddate)b "
				+ " on a.stateId = b.stateId "
				+ " and a.ReportingPeriodId = b.ReportingPeriodId "
				+ " where a.ReportingPeriodId = " + reportPeriod
				+ " )t "
				+ " on s.stateId = t.stateId "
				+ submittedWhereClause
				+ " order by " + sortBy + " "+ sortOrder; 
		
		
	/*	String reportQuery2 = "select s.statename, NVL(t.counts,0) numberOfTransmissions,NVL(t.Submitteddate, '--') Submitteddate,"
			+ " Case When Submitteddate is not null and Submitteddate <> '--' Then 'Yes' Else 'No' End submittedTheFile, "
			+ " Case When Submitteddate Is Not Null and Submitteddate <> '--' Then To_Char(t.transmissionId) Else '--' End fileNumber,  "
			+ " NVL(t.Timelyerrcnt, '--') onTime"
			+ " from state s "
			+ " left join ( "
			+ " select a.stateID, NVL(a.counts,0) counts, NVL(b.Submitteddate, '--') Submitteddate, b.transmissionId, NVL(b.Timelyerrcnt, '--') Timelyerrcnt "
			+ " from "
			+ " (select stateId, ReportingPeriodId, count(transmissionId) counts "
			+ " from transmission "
			+ " group by stateId, ReportingPeriodId)a "
			+ " left join"
			+ " (select stateid, ReportingPeriodId, transmissionId,"
			+ " NVL(TO_CHAR(FROM_TZ(CAST(Submitteddate AS TIMESTAMP), 'America/Chicago') AT TIME ZONE 'America/New_York', 'dd/mm/yyyy hh24:mi:ss'), '--') Submitteddate, "
			+ " Case When Timelyerrcnt=0 Then 'Yes' Else 'No' End Timelyerrcnt "
			+ " from transmission "
			+ " where Lower(Submissionstatus) = 'active' "
			+ " order by Submitteddate)b "
			+ " on a.stateId = b.stateId "
			+ " and a.ReportingPeriodId = b.ReportingPeriodId "
			+ " where a.ReportingPeriodId = " + reportPeriod
			+ " )t "
			+ " on s.stateId = t.stateId "
			+ " order by " + sortBy + " "+ sortOrder;*/
		

		
		submissionStatsReportList = getSessionFactory().getCurrentSession().createSQLQuery(reportQuery2).list();
			//getBaseDAO().sqlqueryCallback(reportQuery2);		
		System.out.println("got the list submissionStatsReportList" + submissionStatsReportList.size());
		int totalStatesSubmitted = 0;
		int totalStatesTransmitted = 0;
		int totalTransmissionAcrossStates = 0;
		int totalSubmissionAcrossStates = 0;
		int counter = 0;
		Iterator<?> submissionStatsIt = submissionStatsReportList.iterator();
		while(submissionStatsIt.hasNext())
		{
			counter++;
			SubmissionStatisticsReportHelper subStatsHelper = new SubmissionStatisticsReportHelper();
			Object[] submissionStats =  (Object[])submissionStatsIt.next();
			subStatsHelper.setStateName(submissionStats[0] != null ? submissionStats[0].toString(): null);
   		subStatsHelper.setNumberOfTransmissions(submissionStats[1] != null ?(BigDecimal) submissionStats[1]:null);  
   		if(((BigDecimal)submissionStats[1]).intValue() > 0)
   		{
   			totalStatesTransmitted++;
   			totalTransmissionAcrossStates +=((BigDecimal)submissionStats[1]).intValue();
   		}     	
			subStatsHelper.setSubmittedDate(submissionStats[2] != null  ? (submissionStats[2].toString()):null);
			if(submissionStats[2] != null && !submissionStats[2].equals("--"))
			{
				totalStatesSubmitted++;
				totalSubmissionAcrossStates++;
			}
			subStatsHelper.setSubmittedTheFile(submissionStats[3] != null  ? (submissionStats[3].toString()):null);
			subStatsHelper.setFileNumber(submissionStats[4] != null ? submissionStats[4].toString(): null);
			subStatsHelper.setOnTime(submissionStats[5] != null ? submissionStats[5].toString(): null);	
			results.add(subStatsHelper);
		}
		ReportingPeriod reportingPeriodObj = (ReportingPeriod) getSessionFactory().getCurrentSession()
															.createQuery(" from ReportingPeriod reportingPeriod where reportingPeriod.id = " + reportPeriod).uniqueResult();
			//getBaseDAO().hqlQueryObject(" from ReportingPeriod reportingPeriod where reportingPeriod.id = " + reportPeriod);
		SubmissionStatisticsMetaData submissionStatsMetaData = new SubmissionStatisticsMetaData();
		if(reportingPeriodObj != null)
      {
      	submissionStatsMetaData.setSelectedReportingPeriodName(reportingPeriodObj.getName());
      }
		System.out.println("got the list submissionStatsReportList" + submissionStatsReportList.size());		
		submissionStatsMetaData.setTotalStatesSubmitted(totalStatesSubmitted);
		submissionStatsMetaData.setTotalStatesTransmitted(totalStatesTransmitted);
		submissionStatsMetaData.setTotalSubmissionAcrossStates(totalSubmissionAcrossStates);
		submissionStatsMetaData.setTotalTransmissionAcrossStates(totalTransmissionAcrossStates);
		
		SubmissionStatisticsReportHelper resultsWithMetaData = new SubmissionStatisticsReportHelper();
		resultsWithMetaData.setResultList(results);
		resultsWithMetaData.setSubmissionStatisticsMetaData(submissionStatsMetaData);
		return resultsWithMetaData;

	}
   public List<ComplianceStandardsReport> complianceStandardsReport(List<String> selectedStates, List<String> selectedReportingPeriods, String groupBy)
   {
   	List<ComplianceStandardReportHelper> helperObjs = new ArrayList<ComplianceStandardReportHelper>();
   	List<?> returnedResult;
   	List<ComplianceStandardsReport> reportObjs = new ArrayList<ComplianceStandardsReport>();
   	String selectedStatesStr = "";
   	String selectedRPStr = "";
   	int fileFormatCount = 0;
   	int timelyCount = 0;
   	int fileSubmissionErrorFreeCount = 0;
   	int dataStandardsErrorFreeCount = 0;
   	int outcomeUniverseCount = 0;
   	int participationInCareCount = 0;
   	int participationDischargedCount = 0;
   
   	int enforcedFileFormatCount = 0;
   	int enforcedTimelyCount = 0;
   	int enforcedFileSubmissionErrorFreeCount = 0;
   	int enforcedDataStandardsErrorFreeCount = 0;
   	int enforcedOutcomeUniverseCount = 0;
   	int enforcedParticipationInCareCount = 0;
   	int enforcedParticipationDischargedCount = 0;
   	boolean changeTable = false;
   
   	if(selectedStates.size() > 0 )
   	{   		
   		selectedStatesStr = arrayToString(selectedStates.toArray(),",");
   	}

   	if(selectedReportingPeriods.size() > 0)
   	{
   		selectedRPStr = arrayToString(selectedReportingPeriods.toArray(),",");
   	}
   	
   	String groupbyStr = "By State";
   	if(groupBy.equalsIgnoreCase("By State"))
   	{
   		groupbyStr = "stateName";
   	}
   	if(groupBy.equalsIgnoreCase("By Report Period"))
   	{
   		groupbyStr = "reportPeriodName";
   	}
		String sqlString = "Select stateName, reportPeriodName,fileName,potentialPenalty,"
		+ " Max ( Case When Compliancecategoryid In (2,11) Then Cnt End) As fileFormat,"
		+ " Max ( Case When Compliancecategoryid = 1 Then Cnt End) As timelyData,"		
		+ " Max ( Case When Compliancecategoryid = 3 Then Cnt End) As dataStandardsErrorFree,"
		+ " Max ( Case When Compliancecategoryid = 10 Then Cnt End) As fileSubmissionErrorFree, "
		+ " Max ( Case When Compliancecategoryid = 4 Then Cnt End) As outcomeUniverse, "
		+ " Max ( Case When Compliancecategoryid = 5 Then Cnt End) As participationInCare,"
		+ " Max ( Case When Compliancecategoryid = 6 Then cnt End) as participationDischarged,"
		+ " Max ( Case When Compliancecategoryid in (7,8,9) Then cnt End) as recordLevel"
		+ " From ("		
			+ " select tr.transmissionid as fileName,tr.stateid,tr.potentialPenalty,rs.name as reportPeriodName," 
			+ " st.stateName,Count(1) as cnt," 
			+ " Cc.Name as complianceCategoryName,cc.compliancecategoryid as complianceCategoryId," 
			+	"cc.standardpenaltyvalue"
			+ " from transmission tr left join state st on tr.stateid =  st.stateid "
			+ " left join reportingPeriod rs on tr.reportingperiodid = rs.reportingperiodid "
			+ " left Join Noncompliance nc On nc.Transmissionid = tr.Transmissionid "
		   + " left Join Lookup Lu On nc.Compliancetype = Lu.Lookupid "
		   + " left Join Error Er On Er.Noncomplianceid = Nc.Noncomplianceid "
		   + " left Join Compliancecategory Cc On Er.Compliancecategoryid = Cc.Compliancecategoryid "
		   + " where st.statename in("+selectedStatesStr+") and rs.name in ("+selectedRPStr+") " 
		   + " and tr.submissionStatus='Active'"	
		   + " GROUP BY rs.name,St.Statename,tr.stateid,tr.Transmissionid, Cc.Name, cc.compliancecategoryid,"
		 	+ " tr.potentialPenalty,cc.standardpenaltyvalue"
		 + " ) GROUP BY stateName,reportPeriodName,fileName,potentialPenalty"
		 + " order by "+groupbyStr+"";
		
		//System.out.println("the str" + sqlString);
		returnedResult = getSessionFactory().getCurrentSession().createSQLQuery(sqlString).list();
		int listSize = returnedResult.size();
		Iterator<?> compliaceStandardsIt = returnedResult.iterator();
      int counter = 0;  
      int loopCount = 0;
      String rowArangementBy = "";
		while(compliaceStandardsIt.hasNext())
		{
			ComplianceStandardsReport complianceStandardsReport;
			ComplianceStandardReportHelper compliaceStReportHelper = new ComplianceStandardReportHelper();
			//System.out.println("the counter is:" + counter + "listSize is:" +listSize);
			Object[] complianceObj =  (Object[])compliaceStandardsIt.next();
			Map<String, BigDecimal> conterMap = new HashMap<String, BigDecimal>();
			if(groupbyStr.equals("stateName") && counter == 0 )
			{
				rowArangementBy = complianceObj[0].toString();				
			}
			if(groupbyStr.equals("reportPeriodName") && counter ==0 )
			{
				rowArangementBy = complianceObj[1].toString();			
			}
			System.out.println("the rowArangementBy first time" + rowArangementBy);
			if((groupbyStr.equals("stateName") && !rowArangementBy.equalsIgnoreCase(complianceObj[0].toString())) || 
				(groupbyStr.equals("reportPeriodName") && !rowArangementBy.equalsIgnoreCase(complianceObj[1].toString()))	)
			{
				
				 complianceStandardsReport = new ComplianceStandardsReport();
				 //System.out.println("rowArangementBy" + rowArangementBy);
   	   	         complianceStandardsReport = addToComplianceStandardsReport(rowArangementBy,complianceStandardsReport,timelyCount,fileFormatCount,
   	   			 fileSubmissionErrorFreeCount,dataStandardsErrorFreeCount,outcomeUniverseCount,participationInCareCount,participationDischargedCount
   	   			 ,enforcedFileFormatCount,enforcedTimelyCount,enforcedFileSubmissionErrorFreeCount,enforcedDataStandardsErrorFreeCount,enforcedOutcomeUniverseCount
   	   			 ,enforcedParticipationInCareCount, enforcedParticipationDischargedCount);
   	   	         
   	    
				 complianceStandardsReport.setResultList(helperObjs);	
				 reportObjs.add(complianceStandardsReport);
				 helperObjs = new ArrayList<ComplianceStandardReportHelper>();
				 counter = 0;	
				 if(groupbyStr.equals("stateName"))
				 {
				  rowArangementBy = complianceObj[0].toString();	
				 }
				 if(groupbyStr.equals("reportPeriodName"))
				 {
				  rowArangementBy = complianceObj[1].toString();	
				 }
				 
			    fileFormatCount = 0;		  
			    timelyCount = 0;		   
			    fileSubmissionErrorFreeCount = 0;		 
			    dataStandardsErrorFreeCount = 0;		
			    outcomeUniverseCount = 0;		
			    participationInCareCount = 0;		 
			    participationDischargedCount = 0;
				timelyCount = 0;
				
			    enforcedFileFormatCount = 0;
			    enforcedTimelyCount = 0;
			    enforcedFileSubmissionErrorFreeCount = 0;
			    enforcedDataStandardsErrorFreeCount = 0;
			   	enforcedOutcomeUniverseCount = 0;
			   	enforcedParticipationInCareCount = 0;
			   	enforcedParticipationDischargedCount = 0;
			}

			compliaceStReportHelper.setStateName(complianceObj[0] != null ? complianceObj[0].toString(): null);        
			compliaceStReportHelper.setReportingPeriod(complianceObj[1] != null ? complianceObj[1].toString(): null);
			compliaceStReportHelper.setFileNumber(complianceObj[2] != null ? complianceObj[2].toString(): null);
			compliaceStReportHelper.setPotentialPenalty(complianceObj[3] != null ? complianceObj[3].toString(): null);
		
         if(complianceObj[4] != null)
         {
         	fileFormatCount++;
         	conterMap.put("fileFormat", ((BigDecimal)complianceObj[4]));         	
         }
			compliaceStReportHelper.setFileFormat(complianceObj[4] != null ? "2.5%": "--");
			
         if(complianceObj[5] != null)
         {
         	timelyCount++;         	
         	conterMap.put("timely", ((BigDecimal)complianceObj[5]));
         }			
			compliaceStReportHelper.setTimelyData(complianceObj[5] != null ? "2.5%": "--");
         if(complianceObj[6] != null)
         {
         	dataStandardsErrorFreeCount++;         	
         	conterMap.put("dataStandardsErrorFree",((BigDecimal)complianceObj[6]));
         }
			compliaceStReportHelper.setDataStandardErrorFree(complianceObj[6] != null ? "1.25%": "--");
         if(complianceObj[7] != null)
         {
         	fileSubmissionErrorFreeCount++;         
         	conterMap.put("fileSubmissionErrorFree", ((BigDecimal)complianceObj[7]));
         }
			compliaceStReportHelper.setFileSubmissionErrorFree(complianceObj[7] != null ? "2.5%": "--");
         if(complianceObj[8] != null)
         {
         	outcomeUniverseCount++;         	
         	conterMap.put("outcomeUniverse", ((BigDecimal)complianceObj[8]));
         }
			compliaceStReportHelper.setOutcomeUniverse(complianceObj[8] != null ? "1.25%": "--");
         if(complianceObj[9] != null)
         {
         	participationInCareCount++;         
         	conterMap.put("participationInCare", ((BigDecimal)complianceObj[9]));
         }
			compliaceStReportHelper.setParticipationInCare(complianceObj[9] != null ? "0.5%": "--");
         if(complianceObj[10] != null)
         {
         	participationDischargedCount++;         
         	conterMap.put("participationDischarged", ((BigDecimal)complianceObj[10]));
         }
			compliaceStReportHelper.setParticipationDischarged(complianceObj[10] != null ? "0.5%": "--");
			compliaceStReportHelper = calculateEnforcedPenalty(compliaceStReportHelper,conterMap);
			
			if(compliaceStReportHelper.getEnforcedTimelyCount() == 1)
			{
				enforcedTimelyCount++;
			}
			if(compliaceStReportHelper.getEnforcedFileFormatCount() == 1)
			{
				enforcedFileFormatCount++;
			}
			if(compliaceStReportHelper.getEnforcedFileSubmissionErrorFreeCount() == 1)
			{
				enforcedFileSubmissionErrorFreeCount++;
			}
			if(compliaceStReportHelper.getEnforcedDataStandardsErrorFreeCount() == 1)
			{
				enforcedDataStandardsErrorFreeCount++;
			}
			if(compliaceStReportHelper.getEnforcedOutcomeUniverseCount() == 1)
			{
				enforcedOutcomeUniverseCount++;
			}
			if(compliaceStReportHelper.getEnforcedParticipationInCareCount() == 1)
			{
				enforcedParticipationInCareCount++;
			}
			if(compliaceStReportHelper.getEnforcedParticipationDischargedCount() == 1)
			{
				enforcedParticipationDischargedCount++;
			}
			helperObjs.add(compliaceStReportHelper);
		

        counter++; 
        loopCount++;       
        if(loopCount == listSize &&  helperObjs.size() > 0)
        {      	  
      	          	 
      	   	 System.out.println("came in the last one and name is not equal");
      	   	 ComplianceStandardsReport complianceStandardsReport2 = new ComplianceStandardsReport();
      	   	 complianceStandardsReport2 = addToComplianceStandardsReport(rowArangementBy,complianceStandardsReport2,timelyCount,fileFormatCount,
      	   	 fileSubmissionErrorFreeCount,dataStandardsErrorFreeCount,outcomeUniverseCount,participationInCareCount,participationDischargedCount
      	     ,enforcedFileFormatCount,enforcedTimelyCount,enforcedFileSubmissionErrorFreeCount,enforcedDataStandardsErrorFreeCount,enforcedOutcomeUniverseCount
	   		 ,enforcedParticipationInCareCount, enforcedParticipationDischargedCount);
      	   	 
      	  
   			 complianceStandardsReport2.setResultList(helperObjs);   				
   			 reportObjs.add(complianceStandardsReport2);   				
   			    counter = 0;
   			    fileFormatCount = 0;		  
   			    timelyCount = 0;		   
   			    fileSubmissionErrorFreeCount = 0;		 
   			    dataStandardsErrorFreeCount = 0;		
   			    outcomeUniverseCount = 0;		
   			    participationInCareCount = 0;		 
   			    participationDischargedCount = 0;
   				 timelyCount = 0;    
   				 
 			    enforcedFileFormatCount = 0;
			    enforcedTimelyCount = 0;
			    enforcedFileSubmissionErrorFreeCount = 0;
			    enforcedDataStandardsErrorFreeCount = 0;
			   	enforcedOutcomeUniverseCount = 0;
			   	enforcedParticipationInCareCount = 0;
			   	enforcedParticipationDischargedCount = 0;

         }
        
        //System.out.println("reportObjs size" + reportObjs.size());
/*			System.out.println("fileFormatCount" +fileFormatCount);
			System.out.println("timelyCount" +timelyCount);
			System.out.println("fileSubmissionErrorFreeCount" +fileSubmissionErrorFreeCount);
			System.out.println("dataStandardsErrorFreeCount" + dataStandardsErrorFreeCount);
			
			System.out.println("outcomeUniverseCount" +outcomeUniverseCount);
			System.out.println("participationInCareCount" +participationInCareCount);
			System.out.println("participationDischargedCount" +participationDischargedCount);*/
        
        
		}
		return reportObjs;
     	
   }
   private ComplianceStandardsReport addToComplianceStandardsReport(String name,ComplianceStandardsReport csr, int timelyCount,
   		int fileFormatCount, int fileSubmissionErrorFreeCount, int dataStandardsErrorFreeCount, int outcomeUniverseCount, 
   		int participationInCareCount, int participationDischargedCount, int enforcedTimelyCount, int enforcedFileFormatCount,
   		int enforcedFileSubmissionErrorFreeCount, int enforcedDataStandardsErrorFreeCount, int enforcedOutcomeUniverseCount,
   		int enforcedParticipationInCareCount, int enforcedParticipationDischargedCount )
   {  
	   
  	  csr.setName(name);
   	  csr.setTimelyCount(timelyCount);
   	  csr.setFileFormatCount(fileFormatCount);
   	  csr.setFileSubmissionErrorFreeCount(fileSubmissionErrorFreeCount);
   	  csr.setDataStandardsErrorFreeCount(dataStandardsErrorFreeCount);
   	  csr.setOutcomeUniverseCount(outcomeUniverseCount);
   	  csr.setParticipationInCareCount(participationInCareCount);
   	  csr.setParticipationDischargedCount(participationDischargedCount);
      
   	  csr.setEnforcedTimelyCount(enforcedTimelyCount);
   	  csr.setEnforcedFileFormatCount(enforcedFileFormatCount);
   	  csr.setEnforcedFileSubmissionErrorFreeCount(enforcedFileSubmissionErrorFreeCount);
   	  csr.setEnforcedDataStandardsErrorFreeCount(enforcedDataStandardsErrorFreeCount);
   	  csr.setEnforcedOutcomeUniverseCount(enforcedOutcomeUniverseCount);
   	  csr.setEnforcedParticipationInCareCount(enforcedParticipationInCareCount);
   	  csr.setEnforcedParticipationDischargedCount(enforcedParticipationDischargedCount);
   	return csr;
   }
   private static String arrayToString(Object[] a, String separator) {
      StringBuffer result = new StringBuffer();
      if (a.length > 0) {
          result.append("'" +a[0].toString()+ "'");
          for (int i=1; i< a.length; i++) {
              result.append(separator);
              result.append("'" + a[i].toString() +"'");
          }
      }
      return result.toString();
  }
   private static boolean isArray(final Object obj) {
	     if (obj != null)
	        return obj.getClass().isArray();
	     return false;
	  } 
 
   private ComplianceStandardReportHelper calculateEnforcedPenalty(ComplianceStandardReportHelper csrh, Map<String, BigDecimal> enforcedPenaltyCounterMpa)
   {
   	   boolean enforcedPenaltyFlag = false; 
   	   boolean dataStandardsErrorFreeFlag = false;  
   	   boolean outcomesUniverseFlag = false;  
   	   boolean participationInCareFlag = false;
   	 
   	    if(Double.parseDouble(csrh.getPotentialPenalty()) == 0)
   	    {
   	   	 csrh.setEnforcedPenalty("0%");   	   
   	   	 csrh.setEnforcedPenaltyReason("Compliant");   	
   	    }
   	   
   	    else
   	    {
   	   
   		if(enforcedPenaltyCounterMpa.get("timely") != null && (enforcedPenaltyCounterMpa.get("timely")).intValue() > 0)
   		{
   			csrh.setEnforcedPenalty("2.5%");   		   
   			csrh.setEnforcedPenaltyReason("State has failed to submit a data file on time.");
   			csrh.setEnforcedTimelyCount(1);
   			enforcedPenaltyFlag = true; 
   		}
   	
   		
   		if(enforcedPenaltyCounterMpa.get("fileFormat") != null && (enforcedPenaltyCounterMpa.get("fileFormat")).intValue() > 0 && !enforcedPenaltyFlag)
   		{
   			csrh.setEnforcedPenalty("2.5%");
   			csrh.setEnforcedPenaltyReason("State has failed ACF's file format specifications.");
   			enforcedPenaltyFlag = true;
   			csrh.setEnforcedFileFormatCount(1);
   		    
   		}
   	
   	
   		if(enforcedPenaltyCounterMpa.get("fileSubmissionErrorFree") != null && (enforcedPenaltyCounterMpa.get("fileSubmissionErrorFree")).intValue() > 0 && !enforcedPenaltyFlag)
   		{
   			csrh.setEnforcedPenalty("2.5%");
   			csrh.setEnforcedPenaltyReason("State has failed the file submission standards - error-free information.");
   			enforcedPenaltyFlag = true;
   			csrh.setEnforcedFileSubmissionErrorFreeCount(1);
   		}
   		
   		if(enforcedPenaltyCounterMpa.get("dataStandardsErrorFree") != null && (enforcedPenaltyCounterMpa.get("dataStandardsErrorFree")).intValue() > 0 && !enforcedPenaltyFlag)
   		{
   			csrh.setEnforcedPenalty("1.25%");
   			csrh.setEnforcedPenaltyReason("State has failed the data standards - error-free information.");
   			//enforcedPenaltyFlag = true;
   			dataStandardsErrorFreeFlag = true;
   			csrh.setEnforcedDataStandardsErrorFreeCount(1);
   			
   		}	
   		
   		if(enforcedPenaltyCounterMpa.get("outcomeUniverse") != null && (enforcedPenaltyCounterMpa.get("outcomeUniverse")).intValue() > 0 )
   		{
   			outcomesUniverseFlag = true;
   			csrh.setEnforcedOutcomeUniverseCount(1);
   			if(dataStandardsErrorFreeFlag)
   			{
   			 csrh.setEnforcedPenalty("2.5%");
   			 csrh.setEnforcedPenaltyReason("State has failed the data standards for error free and outcomes universe.");
   			} 
   			else
   			{
   				if(!enforcedPenaltyFlag)
   				{
   				 csrh.setEnforcedPenalty("1.25%");
   				 csrh.setEnforcedPenaltyReason("State has failed the data standards for outcomes universe.");
   				}
   			}   
   		
   		}
 
     		if(enforcedPenaltyCounterMpa.get("participationInCare") != null && (enforcedPenaltyCounterMpa.get("participationInCare")).intValue() > 0 && !outcomesUniverseFlag)
      		{
      			participationInCareFlag = true;
      			csrh.setEnforcedParticipationInCareCount(1);
      			if(dataStandardsErrorFreeFlag)
      			{
      				csrh.setEnforcedPenalty("1.75%");
      				csrh.setEnforcedPenaltyReason("State has failed the data standards for error free and foster care youth participation.");
      			}
      			else
      			{
      				if(!enforcedPenaltyFlag)
      				{
         			 csrh.setEnforcedPenalty("0.5%");
         			 csrh.setEnforcedPenaltyReason("State has failed the data standards for foster care youth participation.");  
      				}
      			}
      		}
      		
      		if(enforcedPenaltyCounterMpa.get("participationDischarged") != null && (enforcedPenaltyCounterMpa.get("participationDischarged")).intValue() > 0 && !outcomesUniverseFlag)
      		{
      			csrh.setEnforcedParticipationDischargedCount(1);
      			if(dataStandardsErrorFreeFlag && participationInCareFlag)
      			{
      				csrh.setEnforcedPenalty("2.25%");
      				csrh.setEnforcedPenaltyReason("State has failed the data standards for error free, foster care youth participation and discharged youth participation.");
      			}
      			else if (dataStandardsErrorFreeFlag && !participationInCareFlag)
      			{
      				csrh.setEnforcedPenalty("1.75%");
      				csrh.setEnforcedPenaltyReason("State has failed the data standards for error free discharged youth participation.");
      			}
      			else
      			{
      				if(!enforcedPenaltyFlag)
      				{
         			 csrh.setEnforcedPenalty("0.5%");
         			 csrh.setEnforcedPenaltyReason("State has failed the data standards for foster care youth participation.");  
      				}
      			}
      		}
   	    }
    		
   	return csrh;		   	
   }//End of calculateEnforcedPenalty

@Override
public Map<String, String> getCohorts() {
	List<Cohort> cohorts = null;
	Session session = getSessionFactory().getCurrentSession();
	CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
	CriteriaQuery<Cohort> criteriaQuery = criteriaBuilder.createQuery(Cohort.class);
	Root<Cohort> root = criteriaQuery.from(Cohort.class);
	criteriaQuery.select(root);
	TypedQuery<Cohort> q = session.createQuery(criteriaQuery);	
	cohorts = (List<Cohort>)q.getResultList();
	
	Map<String,String> cohortMap =   new HashMap<String, String>();
	for(Cohort ch :cohorts)
	{
		cohortMap.put(ch.getId().toString(), ch.getDerivedName());
	}
	
	return cohortMap;
}
   
   
}
