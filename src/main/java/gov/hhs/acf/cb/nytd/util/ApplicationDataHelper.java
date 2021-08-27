/**
 * Filename: ApplicationDataHelper.java
 * 
 * Copyright 2010, ICF International
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
package gov.hhs.acf.cb.nytd.util;

import gov.hhs.acf.cb.nytd.models.*;
import gov.hhs.acf.cb.nytd.models.helper.ExtSecondaryUserRole;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Helper class for consistent access to data shared across application
 * 
 * @author Doug Sjoquist (15670)
 */
public class ApplicationDataHelper
{

	private Map<String, Object> application;

	public ApplicationDataHelper(Map<String, Object> application)
	{
		this.application = application;
	}

	private <T extends Serializable> ArrayList<T> getList(String appkey, Class<T> tClass)
	{
		return (ArrayList<T>) application.get(appkey);
	}

	private <T extends Serializable> LinkedHashMap<T, T> getMap(String appkey, Class<T> tClass)
	{
		return (LinkedHashMap<T, T>) application.get(appkey);
	}

	private <T extends Serializable> T put(String appkey, T o)
	{
		application.put(appkey, o);
		return o;
	}

	public ArrayList<PrimaryUserRole> getPrimaryUserRoleWithSysAdminList()
	{
		return getList(Constants.APPKEY_PRIMARY_USER_ROLE_WITH_SYS_ADMIN_LIST, PrimaryUserRole.class);
	}

	public void setPrimaryUserRoleWithSysAdminList(ArrayList<PrimaryUserRole> list)
	{
		put(Constants.APPKEY_PRIMARY_USER_ROLE_WITH_SYS_ADMIN_LIST, list);
	}

	public ArrayList<PrimaryUserRole> getPrimaryUserRoleNoSysAdminList()
	{
		return getList(Constants.APPKEY_PRIMARY_USER_ROLE_NO_SYS_ADMIN_LIST, PrimaryUserRole.class);
	}

	public void setPrimaryUserRoleNoSysAdminList(ArrayList<PrimaryUserRole> list)
	{
		put(Constants.APPKEY_PRIMARY_USER_ROLE_NO_SYS_ADMIN_LIST, list);
	}

	public ArrayList<ReportingPeriod> getReportingPeriodList()
	{
		return getList(Constants.APPKEY_REPORTING_PERIOD_LIST, ReportingPeriod.class);
	}

	public void setReportingPeriodList(ArrayList<ReportingPeriod> list)
	{
		put(Constants.APPKEY_REPORTING_PERIOD_LIST, list);
	}

	public ArrayList<TransmissionType> getTransmissionTypeList()
	{
		return getList(Constants.APPKEY_FILE_TYPE_LIST, TransmissionType.class);
	}

	public void setTransmissionTypeList(ArrayList<TransmissionType> list)
	{
		put(Constants.APPKEY_FILE_TYPE_LIST, list);
	}
	public ArrayList<TransmissionType> getTransmissionTypeListFed()
	{
		return getList(Constants.APPKEY_FILE_TYPE_LIST_FED, TransmissionType.class);
	}

	public void setTransmissionTypeListFed(ArrayList<TransmissionType> list)
	{
		put(Constants.APPKEY_FILE_TYPE_LIST_FED, list);
	}

	public ArrayList<Lookup> getComplianceStatusList()
	{
		return getList(Constants.APPKEY_COMPLIANCE_STATUS_LIST, Lookup.class);
	}

	public void setComplianceStatusList(ArrayList<Lookup> list)
	{
		put(Constants.APPKEY_COMPLIANCE_STATUS_LIST, list);
	}
	
	public ArrayList<Lookup> getSamplingRequestStatusList()
	{
		return getList(Constants.APPKEY_SAMPLE_REQUEST_STATUS_LIST, Lookup.class);
	}

	public void setSamplingRequestStatusList(ArrayList<Lookup> list)
	{
		put(Constants.APPKEY_SAMPLE_REQUEST_STATUS_LIST, list);
	}

	public ArrayList<State> getStateList()
	{
		return getList(Constants.APPKEY_STATE_LIST, State.class);
	}

	public void setStateList(ArrayList<State> list)
	{
		put(Constants.APPKEY_STATE_LIST, list);
	}

	public LinkedHashMap<String, String> getViewResultsList()
	{
		return getMap(Constants.APPKEY_VIEW_RESULTS_LIST, String.class);
	}

	public void setViewResultsList(LinkedHashMap<String, String> map)
	{
		put(Constants.APPKEY_VIEW_RESULTS_LIST, map);
	}

	public ArrayList<Element> getElementNumberDropDown()
	{
		return getList(Constants.APPKEY_ELEMENT_NUMBER_DROP_DOWN, Element.class);
	}

	public void setElementNumberDropDown(ArrayList<Element> list)
	{
		put(Constants.APPKEY_ELEMENT_NUMBER_DROP_DOWN, list);
	}

	public ArrayList<Region> getRegionList()
	{
		return getList(Constants.APPKEY_REGION_LIST, Region.class);
	}

	public void setRegionList(ArrayList<Region> list)
	{
		put(Constants.APPKEY_REGION_LIST, list);
	}

	public ArrayList<SecondaryUserRole> getUserSecondaryRoleList()
	{
		return getList(Constants.APPKEY_USER_SECONDARY_ROLE_LIST, SecondaryUserRole.class);
	}

	public void setUserSecondaryRoleList(ArrayList<SecondaryUserRole> list)
	{
		put(Constants.APPKEY_USER_SECONDARY_ROLE_LIST, list);
		setExtUserSecondaryRoleList(list);
	}
	
	private void setExtUserSecondaryRoleList(ArrayList<SecondaryUserRole> list)
	{
		ArrayList extList = new ArrayList<ExtSecondaryUserRole>();
		for (SecondaryUserRole sr : list) {
			extList.add(new ExtSecondaryUserRole(sr));
		}
		put(Constants.APPKEY_EXT_USER_SECONDARY_ROLE_LIST, extList);
	}
	
	public ArrayList<ExtSecondaryUserRole> getExUserSecondaryRoleList()
	{
		return getList(Constants.APPKEY_EXT_USER_SECONDARY_ROLE_LIST, ExtSecondaryUserRole.class);
	}

}