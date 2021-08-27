package gov.hhs.acf.cb.nytd.actions;

import gov.hhs.acf.cb.nytd.models.PersistentObject;
import gov.hhs.acf.cb.nytd.models.SiteUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: 13873
 * Date: Apr 20, 2010
 */
public class PaginatedSearch
{
	public static enum SortDirection
	{
		ASC, DESC
	}

	// current page. paging starts at 0
	private int page = 0;

	// results from database for the current page
	private List pageResults;

	// optional comparator used to externally sort
	// the search results
	Comparator<? extends PersistentObject> sorter;

	// count of all records returned by search
	private int rowCount = 0;

	// maximum number of page links to show user. default is 5
	private int maxLinkedPages = 5;

	// page size (number of records per page). default is 25
	private int pageSize = 25;

	// sort association path (optional)
	// necessary to support Criteria API
	private String sortAssociation;

	// sort column
	private String sortColumn;

	// sort direction. default is ascending
	private SortDirection sortDirection = SortDirection.ASC;

	// transmissionId
	private Long transmissionId;

	// user performing the search
	SiteUser user;
	
	//non compliacne page name
	private String nonCompliancePageName;

	public boolean isFirstPage()
	{
		return getPage() == 0;
	}

	public boolean isLastPage()
	{
		return getPage() == getPageCount() - 1;
	}

	public int getFirstLinkedPage()
	{
		int offset = getPage() / getMaxLinkedPages();
		return offset * getMaxLinkedPages();
	}

	public int getLastLinkedPage()
	{
		return Math.min(getFirstLinkedPage() + getMaxLinkedPages() - 1, getPageCount() - 1);
	}

	public List<Integer> getLinkedPages()
	{
		List<Integer> pageLinks = new ArrayList<Integer>();
		for (int i = getFirstLinkedPage(); i <= getLastLinkedPage(); i++)
		{
			pageLinks.add(i);
		}

		return pageLinks;
	}

	public int getMaxLinkedPages()
	{
		return this.maxLinkedPages;
	}

	public void setMaxLinkedPages(int maxLinkedPages)
	{
		this.maxLinkedPages = maxLinkedPages;
	}

	public int getPage()
	{
		return page;
	}

	public void setPage(int page)
	{
		this.page = page;
	}

	public List getPageResults()
	{
		return this.pageResults;
	}

	public void setPageResults(List results)
	{
		this.pageResults = results;

		if (sorter != null)
		{
			Collections.sort(pageResults);
		}
	}

	public int getPageSize()
	{
		return this.pageSize;
	}

	public void setPageSize(int pageSize)
	{
		this.pageSize = pageSize;
	}

	public int getPageCount()
	{
		float nrOfPages = 0.0f;
		if (getPageSize() > 0)
			nrOfPages = (float) getRowCount() / getPageSize();
		return (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages);
	}

	public int getRowCount()
	{
		return this.rowCount;
	}

	public void setRowCount(int rowCount)
	{
		this.rowCount = rowCount;
	}

	public String getSortAssociation()
	{
		return sortAssociation;
	}

	public void setSortAssociation(String sortAssociation)
	{
		this.sortAssociation = sortAssociation;
	}

	public String getSortColumn()
	{
		return sortColumn;
	}

	public void setSortColumn(String sortColumn)
	{
		this.sortColumn = sortColumn;
	}

	public SortDirection getSortDirection()
	{
		return sortDirection;
	}

	public void setSortDirection(SortDirection sortDirection)
	{
		this.sortDirection = sortDirection;
	}

	public Comparator<? extends PersistentObject> getSorter()
	{
		return sorter;
	}

	public void setSorter(Comparator<? extends PersistentObject> sorter)
	{
		this.sorter = sorter;
	}

	public SiteUser getUser()
	{
		return user;
	}

	public void setUser(SiteUser user)
	{
		this.user = user;
	}

	public Long getTransmissionId()
	{
		return transmissionId;
	}

	public void setTransmissionId(Long transmissionId)
	{
		this.transmissionId = transmissionId;
	}

	public void reset()
	{
		setPage(0);
		setPageSize(25);
		setRowCount(0);
		setSortColumn(null);
		setSortDirection(SortDirection.ASC);
		setNonCompliancePageName(null);
	}

	/**
	 * @return the nonCompliancePageName
	 */
	public String getNonCompliancePageName()
	{
		return nonCompliancePageName;
	}

	/**
	 * @param nonCompliancePageName the nonCompliancePageName to set
	 */
	public void setNonCompliancePageName(String nonCompliancePageName)
	{
		this.nonCompliancePageName = nonCompliancePageName;
	}
}
