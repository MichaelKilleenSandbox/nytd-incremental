/**
 * Filename: SubmissionMapper.java
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

import gov.hhs.acf.cb.nytd.models.Transmission;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 15178
 * 
 */
public class SubmissionMapper implements RowMapper
{
	private final List<Transmission> submissionList = new ArrayList<Transmission>();

	/*
	 * To set the values of submission object from the cursor returned by the
	 * transmission submission stored procedure
	 * 
	 * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet,
	 * int)
	 */
	@SuppressWarnings("null")
	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException
	{
		// TODO Auto-generated method stub
		Transmission submission = new Transmission();
		submission.setId(new Long(rs.getLong("TRANSMISSIONID")));
		submission.setCreatedBy(new String(rs.getString("CREATEDBY")));
		submissionList.add(submission);

		return submissionList;
	}

	/**
	 * 
	 * @return
	 */
	public List<Transmission> getSubmissionList()
	{
		return submissionList;
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
