/**
 * Filename: CompareFilesAcrossReportPeriods.java
 * 
 *  Copyright 2009, ICF International
 *  Created: Dec 8, 2009
 *  Author: adam
 *
 *  COPYRIGHT STATUS: This work, authored by ICF International employees, was funded in whole or in part 
 *  under U.S. Government contract, and is, therefore, subject to the following license: The Government is 
 *  granted for itself and others acting on its behalf a paid-up, nonexclusive, irrevocable worldwide 
 *  license in this work to reproduce, prepare derivative works, distribute copies to the public, and perform 
 *  publicly and display publicly, by or on behalf of the Government. All other rights are reserved by the 
 *  copyright owner.
 */
package gov.hhs.acf.cb.nytd.actions.report;

import com.opensymphony.xwork2.ActionSupport;
import gov.hhs.acf.cb.nytd.actions.ExportableTable;
import gov.hhs.acf.cb.nytd.models.helper.FileAdvisoryAcrossReportPeriods;
import gov.hhs.acf.cb.nytd.models.helper.FileAdvisoryAcrossReportPeriodsComparator;
import gov.hhs.acf.cb.nytd.models.helper.FileComparisonAcrossReportPeriods;
import gov.hhs.acf.cb.nytd.service.DataExtractionService;
import gov.hhs.acf.cb.nytd.util.DateUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.struts2.interceptor.ParameterAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;


/**
 * Compares a transmitted data file to submissions in previous report periods.
 * 
 * Takes File Number and compiles advisories per record in the form of
 * Record Number, Report Period, File Number, Element,
 * and Problem Description.
 * 
 * @author Adam Russell (18816)
 */
public class CompareFilesAcrossReportPeriods extends ActionSupport implements ServletResponseAware, SessionAware, ParameterAware
{
	@Getter @Setter private DataExtractionService dataExtractionService;
	@Getter @Setter private String transmission;
	@Getter @Setter private String recordNumber;
	@Getter @Setter private String reportPeriodName;
	@Getter @Setter private String stateName;
	@Getter @Setter private String currentSort;
	@Getter @Setter private FileComparisonAcrossReportPeriods fileComparisonAcrossReportPeriods;
	@Getter @Setter private HttpServletResponse servletResponse;
	@Getter @Setter private Map<String, Object> session;
	@Getter @Setter private Map<String, String[]> parameters;

	/**
	 * Executes action.
	 * 
	 * @return Action.SUCCESS on success, Action.INPUT if targetTransmission wasn't selected by user
	 */
	public final String execute()
	{
		if (transmission == null || transmission.isEmpty())
		{
			return INPUT;
		}
		
		fetchFileComparisonAcrossReportPeriodsFromSession();
		
		if (getFileComparisonAcrossReportPeriods() == null)
		{
			return INPUT;
		}
		
		return SUCCESS;
	}

	public final String exportTable()
	{
		if (transmission == null || transmission.isEmpty())
		{
			return INPUT;
		}

		fetchFileComparisonAcrossReportPeriodsFromSession();

		if (getFileComparisonAcrossReportPeriods() == null)
		{
			return INPUT;
		}

		ExportableTable<FileAdvisoryAcrossReportPeriods> dataTable = new ExportableTable<FileAdvisoryAcrossReportPeriods>(
				this, dataExtractionService)
		{
			protected void addColumns()
			{
				addColumn("Record Number", new ValueProvider<FileAdvisoryAcrossReportPeriods>()
				{
					public String getValue(final FileAdvisoryAcrossReportPeriods item)
					{
						return item.getId().getRecordNumber();
					}
				});
				addColumn("Report Period", new ValueProvider<FileAdvisoryAcrossReportPeriods>()
				{
					public String getValue(final FileAdvisoryAcrossReportPeriods item)
					{
						return item.getId().getReportPeriodName();
					}
				});
				addColumn("Federal File ID", new ValueProvider<FileAdvisoryAcrossReportPeriods>()
				{
					public String getValue(final FileAdvisoryAcrossReportPeriods item)
					{
						return item.getId().getTransmissionId();
					}
				});
				addColumn("Problem Description",
						new ValueProvider<FileAdvisoryAcrossReportPeriods>()
						{
							public String getValue(final FileAdvisoryAcrossReportPeriods item)
							{
								return item.getProblemDescription();
							}
						});
			}
		};

		return dataTable.export(servletResponse, fileComparisonAcrossReportPeriods,
				"crossFileComparisonAcrossReportPeriods_" + DateUtil.getCurrentDate("yyyyMMdd'T'HHmm") + ".csv");
	}
	
	private void fetchFileComparisonAcrossReportPeriodsFromSession()
	{
		String sort = getParameters().containsKey("sort") ? getParameters().get("sort")[0] : null;
		
		if (sort == null || sort.equalsIgnoreCase("byRecordNumber"))
		{
			setFileComparisonAcrossReportPeriods((FileComparisonAcrossReportPeriods) getSession().get(
					"interRptPdCheck"));
			setCurrentSort("recordNumber");
		}
		else if (sort.equalsIgnoreCase("byElement"))
		{
			setFileComparisonAcrossReportPeriods(new FileComparisonAcrossReportPeriods(
					FileAdvisoryAcrossReportPeriodsComparator.Priority.ELEMENT));
			getFileComparisonAcrossReportPeriods().addAll(
					(FileComparisonAcrossReportPeriods) getSession().get("interRptPdCheck"));
			setCurrentSort("element");
		}
		else if (sort.equalsIgnoreCase("byReportPeriod"))
		{
			setFileComparisonAcrossReportPeriods(new FileComparisonAcrossReportPeriods(
					FileAdvisoryAcrossReportPeriodsComparator.Priority.REPORT_PERIOD));
			getFileComparisonAcrossReportPeriods().addAll(
					(FileComparisonAcrossReportPeriods) getSession().get("interRptPdCheck"));
			setCurrentSort("reportPeriod");
		}
		else if (sort.equalsIgnoreCase("byFileNumber"))
		{
			setFileComparisonAcrossReportPeriods(new FileComparisonAcrossReportPeriods(
					FileAdvisoryAcrossReportPeriodsComparator.Priority.FILE_NUMBER));
			getFileComparisonAcrossReportPeriods().addAll(
					(FileComparisonAcrossReportPeriods) getSession().get("interRptPdCheck"));
			setCurrentSort("fileNumber");
		}
		else if (sort.equalsIgnoreCase("byDescription"))
		{
			setFileComparisonAcrossReportPeriods(new FileComparisonAcrossReportPeriods(
					FileAdvisoryAcrossReportPeriodsComparator.Priority.DESCRIPTION));
			getFileComparisonAcrossReportPeriods().addAll(
					(FileComparisonAcrossReportPeriods) getSession().get("interRptPdCheck"));
			setCurrentSort("description");
		}
		else if (sort == null || sort.equalsIgnoreCase("byRecordNumberDesc"))
		{
			setFileComparisonAcrossReportPeriods(new FileComparisonAcrossReportPeriods(
					FileAdvisoryAcrossReportPeriodsComparator.Priority.RECORD_NUMBER_DESC));
			getFileComparisonAcrossReportPeriods().addAll(
					(FileComparisonAcrossReportPeriods) getSession().get("interRptPdCheck"));
			setCurrentSort("recordNumberDesc");
		}
		else if (sort.equalsIgnoreCase("byElementDesc"))
		{
			setFileComparisonAcrossReportPeriods(new FileComparisonAcrossReportPeriods(
					FileAdvisoryAcrossReportPeriodsComparator.Priority.ELEMENT_DESC));
			getFileComparisonAcrossReportPeriods().addAll(
					(FileComparisonAcrossReportPeriods) getSession().get("interRptPdCheck"));
			setCurrentSort("elementDesc");
		}
		else if (sort.equalsIgnoreCase("byReportPeriodDesc"))
		{
			setFileComparisonAcrossReportPeriods(new FileComparisonAcrossReportPeriods(
					FileAdvisoryAcrossReportPeriodsComparator.Priority.REPORT_PERIOD_DESC));
			getFileComparisonAcrossReportPeriods().addAll(
					(FileComparisonAcrossReportPeriods) getSession().get("interRptPdCheck"));
			setCurrentSort("reportPeriodDesc");
		}
		else if (sort.equalsIgnoreCase("byFileNumberDesc"))
		{
			setFileComparisonAcrossReportPeriods(new FileComparisonAcrossReportPeriods(
					FileAdvisoryAcrossReportPeriodsComparator.Priority.FILE_NUMBER_DESC));
			getFileComparisonAcrossReportPeriods().addAll(
					(FileComparisonAcrossReportPeriods) getSession().get("interRptPdCheck"));
			setCurrentSort("fileNumberDesc");
		}
		else if (sort.equalsIgnoreCase("byDescriptionDesc"))
		{
			setFileComparisonAcrossReportPeriods(new FileComparisonAcrossReportPeriods(
					FileAdvisoryAcrossReportPeriodsComparator.Priority.DESCRIPTION_DESC));
			getFileComparisonAcrossReportPeriods().addAll(
					(FileComparisonAcrossReportPeriods) getSession().get("interRptPdCheck"));
			setCurrentSort("descriptionDesc");
		}
	}
}
