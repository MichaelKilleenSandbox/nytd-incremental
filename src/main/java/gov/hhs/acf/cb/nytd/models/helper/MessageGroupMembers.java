/**
 * Filename: MessageGroupMembers.java
 * 
 * Copyright 2009, ICF International
 * Created: Sep 30, 2009
 * Author: 16939
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
package gov.hhs.acf.cb.nytd.models.helper;

import java.util.List;

/**
 * @author 16939
 * 
 */
public class MessageGroupMembers
{
	private String groupId;
	private String groupName;
	private List<Member> groupMembers;
	private String groupLabel;

	/**
	 * @return the groupId
	 */
	public String getGroupId()
	{
		return groupId;
	}

	/**
	 * @param groupId
	 *           the groupId to set
	 */
	public void setGroupId(String groupId)
	{
		this.groupId = groupId;
	}

	/**
	 * @return the groupName
	 */
	public String getGroupName()
	{
		return groupName;
	}

	/**
	 * @param groupName
	 *           the groupName to set
	 */
	public void setGroupName(String groupName)
	{
		this.groupName = groupName;
	}

	/**
	 * @return the groupMembers
	 */
	public List<Member> getGroupMembers()
	{
		return groupMembers;
	}

	/**
	 * @param groupMembers
	 *           the groupMembers to set
	 */
	public void setGroupMembers(List<Member> groupMembers)
	{
		this.groupMembers = groupMembers;
	}

	/**
	 * @return the groupLabel
	 */
	public String getGroupLabel()
	{
		return groupLabel;
	}

	/**
	 * @param groupLabel
	 *           the groupLabel to set
	 */
	public void setGroupLabel(String groupLabel)
	{
		this.groupLabel = groupLabel;
	}
}
