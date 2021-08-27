/**
 * Filename: TransmissionMapper.java
 * 
 * Copyright 2009, ICF International Created: Jul 20, 2009 Author: 15178
 * 
 * COPYRIGHT STATUS: This work, authored by ICF International employees, was
 * funded in whole or in part under U.S. Government contract, and is, therefore,
 * subject to the following license: The Government is granted for itself and
 * others acting on its behalf a paid-up, nonexclusive, irrevocable worldwide
 * license in this work to reproduce, prepare derivative works, distribute
 * copies to the public, and perform publicly and display publicly, by or on
 * behalf of the Government. All other rights are reserved by the copyright
 * owner.
 */
package gov.hhs.acf.cb.nytd.dao.helper;

import gov.hhs.acf.cb.nytd.models.*;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author 15178
 * 
 */
public class TransmissionMapper implements RowMapper
{
	private final List<Transmission> transmissionList = new ArrayList<Transmission>();

	/*
	 * To set the values of transmission object from the cursor returned by the
	 * stored procedure
	 * 
	 * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet,
	 * int)
	 */
	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException
	{
		// TODO Auto-generated method stub

		System.out.println("In Transmission Mapper");
		Transmission transmission = new Transmission();
		transmission.setId(new Long(rs.getLong("transmissionId")));
		ReportingPeriod reportingPeriod = new ReportingPeriod();
		reportingPeriod.setId(rs.getLong("REPORTINGPERIODID"));
		transmission.setReportingPeriod(reportingPeriod);
		TransmissionType transmissionType = new TransmissionType();
		transmissionType.setId(rs.getLong("TRANSMISSIONTYPEID"));
		transmission.setTransmissionType(transmissionType);
		transmission.setFileId(rs.getString("FILEID"));
		transmission.setFileGenerationDate(rs.getString("FILEGENERATIONDATE"));
		transmission.setDescription(rs.getString("DESCRIPTION"));
		transmission.setProcessingStatus(rs.getString("PROCESSINGSTATUS"));
		transmission.setSubmissionStatus(rs.getString("SUBMISSIONSTATUS"));
		//setting all the dates
		Calendar cal = Calendar.getInstance();
		Date submittedDate = rs.getDate("SUBMITTEDDATE");
		if (submittedDate != null) {
			cal.setTime(submittedDate);
			transmission.setSubmittedDate(cal);
		}
		Date fileReceivedDate = rs.getDate("FILERECEIVEDDATE");
		if (fileReceivedDate != null) {
			cal.setTime(fileReceivedDate);
			transmission.setFileReceivedDate(cal);
		}
		Date createdDate = rs.getDate("CREATEDDATE");
		if (createdDate != null) {
			cal.setTime(createdDate);
			transmission.setCreatedDate(cal);
		}
		SiteUser siteUser = new SiteUser();
		siteUser.setId(rs.getLong("SITEUSERID"));
		transmission.setSiteUser(siteUser);
		transmission.setFileName(rs.getString("FILENAME"));
		transmission.setFileSize(rs.getInt("FILESIZE"));
		State state = new State();
		state.setId(rs.getLong("STATEID"));
		transmission.setState(state);
		transmission.setCreatedBy(rs.getString("CREATEDBY"));
		


		// Printing for testing purposes
		/*
		 * System.out.println("transmissionId:" + transmission.getId());
		 * System.out.println("DataFileId:" + transmission.getDataFile().getId());
		 * System.out.println("Federal FileId:" +
		 * transmission.getFederalFileId());
		 * System.out.println("transmissionType:" +
		 * transmission.getTransmissionType().getName());
		 * System.out.println("description:" + transmission.getDescription());
		 */
		transmissionList.add(transmission);

		return transmissionList;
	}

	/**
	 * 
	 * @return
	 */
	public List<Transmission> getTransmissionList()
	{
		return transmissionList;
	}

	/**
	 * Checks for null values and returns empty string if it finds null values.
	 * 
	 * @param val
	 * @return String
	 */
	public String getString(String val)
	{
		if (val == null)
		{
			return "";
		}
		return val;
	}
}
