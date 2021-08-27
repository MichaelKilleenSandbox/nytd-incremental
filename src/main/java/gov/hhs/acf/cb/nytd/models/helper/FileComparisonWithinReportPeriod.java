package gov.hhs.acf.cb.nytd.models.helper;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.HashSet;


/**
 * A cross-file comparison within a report period.
 * 
 * More specifically, comparisons of records between two NYTD data files
 * categorized as follows:
 * 
 * Unmatched, Without Error
 * Unmatched, With Error
 * Unmatched, Total
 * Matched, Unchanged, Without Error
 * Matched, Unchanged, With Error
 * Matched, Unchanged, Total
 * Matched, Changed, Without Error
 * Matched, Changed, With Error
 * Matched, Changed, Total
 * Matched, Total, Without Error
 * Matched, Total, With Error
 * Matched, Total, Total
 * Total, Without Error
 * Total, With Error
 * Total, Total
 * 
 * "Unmatched" means record numbers that appear in one data file but not in the other.
 * "Changed" means that the information contained in a record differs from one data file to the next.
 * 
 * @author Adam Russell (18816)
 */
public class FileComparisonWithinReportPeriod
{
	protected Logger log = Logger.getLogger(getClass());
	
	@Getter private Collection<String> file1RecordsUnmatchedWithoutError = new HashSet<String>();
	@Getter private Collection<String> file2RecordsUnmatchedWithoutError = new HashSet<String>();
	@Getter private Collection<String> file1RecordsUnmatchedWithError = new HashSet<String>();
	@Getter private Collection<String> file2RecordsUnmatchedWithError = new HashSet<String>();
	@Getter private Collection<String> recordsMatchedUnchangedWithoutError = new HashSet<String>();
	@Getter private Collection<String> recordsMatchedUnchangedWithError = new HashSet<String>();
	@Getter private Collection<String> file1RecordsMatchedChangedWithoutError = new HashSet<String>();
	@Getter private Collection<String> file2RecordsMatchedChangedWithoutError = new HashSet<String>();
	@Getter private Collection<String> file1RecordsMatchedChangedWithError = new HashSet<String>();
	@Getter private Collection<String> file2RecordsMatchedChangedWithError = new HashSet<String>();
	
	@Getter @Setter private Long file1Id;
	@Getter @Setter private Long file2Id;
	
	/**
	 * Compliance of first data file.
	 */
	@Getter @Setter private Boolean file1Compliant;
	
	/**
	 * Compliance of second data file.
	 */
	@Getter @Setter private Boolean file2Compliant;
	
	public enum File { ONE, TWO, N_A };
	public enum Matched { UNMATCHED, MATCHED, TOTAL };
	public enum Changed { UNCHANGED, CHANGED, TOTAL, N_A };
	public enum InError { WITHOUT_ERROR, WITH_ERROR, TOTAL };
	
	/**
	 * Get the difference of record numbers in the first and second data files
	 * for given the appropriate categorizations.
	 * 
	 * @param matched whether records to be compared are matched
	 * @param changed whether records to be compared changed
	 * @param inError whether records to be compared are in error
	 * @return difference of record numbers in first and second data files
	 */
	public String getDelta(Matched matched, Changed changed, InError inError)
	{
		String result;
		int delta;
		
		if (matched == Matched.UNMATCHED && inError == InError.WITHOUT_ERROR)
		{
			delta = getFile2RecordsUnmatchedWithoutError().size() - getFile1RecordsUnmatchedWithoutError().size();
		}
		else if (matched == Matched.UNMATCHED && inError == InError.WITH_ERROR)
		{
			delta = getFile2RecordsUnmatchedWithError().size() - getFile1RecordsUnmatchedWithError().size();
		}
		else if (matched == Matched.UNMATCHED && inError == InError.TOTAL)
		{
			delta = getFile2RecordsUnmatchedTotal().size() - getFile1RecordsUnmatchedTotal().size();
		}
		else if (matched == Matched.MATCHED && changed == Changed.CHANGED && inError == InError.WITHOUT_ERROR)
		{
			delta = getFile2RecordsMatchedChangedWithoutError().size() - getFile1RecordsMatchedChangedWithoutError().size();
		}
		else if (matched == Matched.MATCHED && changed == Changed.CHANGED && inError == InError.WITH_ERROR)
		{
			delta = getFile2RecordsMatchedChangedWithError().size() - getFile1RecordsMatchedChangedWithError().size();
		}
		else if (matched == Matched.MATCHED && changed == Changed.TOTAL && inError == InError.WITHOUT_ERROR)
		{
			delta = getFile2RecordsMatchedTotalWithoutError().size() - getFile1RecordsMatchedTotalWithoutError().size();
		}
		else if (matched == Matched.MATCHED && changed == Changed.TOTAL && inError == InError.WITH_ERROR)
		{
			delta = getFile2RecordsMatchedTotalWithError().size() - getFile1RecordsMatchedTotalWithError().size();
		}
		else if (matched == Matched.TOTAL && inError == InError.WITHOUT_ERROR)
		{
			delta = getFile2RecordsTotalWithoutError().size() - getFile1RecordsTotalWithoutError().size();
		}
		else if (matched == Matched.TOTAL && inError == InError.WITH_ERROR)
		{
			delta = getFile2RecordsTotalWithError().size() - getFile1RecordsTotalWithError().size();
		}
		else if (matched == Matched.TOTAL && inError == InError.TOTAL)
		{
			delta = getFile2RecordsTotal().size() - getFile1RecordsTotal().size();
		}
		else
		{
			delta = 0; // matched, unchanged; matched, changed, total; matched, total, total
		}
		
		result = String.valueOf(delta);
		if (delta >= 0)
		{
			result = String.format("%s%s", "+", result);
		}
		return result;
	}
	
	/**
	 * Adds a record to the appropriate collection of record numbers.
	 *
	 * @param recordNumber the record number to be added
	 * @param firstFile whether record to be added corresponds to first file rather than second; null if N/A
	 * @param matched whether record to be added has a match in the other transmission
	 * @param changed whether record to be added changed between transmissions; null if N/A
	 * @param inError whether record to be added is in error
	 */
	public void addRecordNumber(String recordNumber, Boolean firstFile, Boolean matched, Boolean changed, Boolean inError)
	{
		if (!matched && !inError && firstFile)
		{
			addFile1RecordUnmatchedWithoutError(recordNumber);
		}
		else if (!matched && !inError && !firstFile)
		{
			addFile2RecordUnmatchedWithoutError(recordNumber);
		}
		else if (!matched && inError && firstFile)
		{
			addFile1RecordUnmatchedWithError(recordNumber);
		}
		else if (!matched && inError && !firstFile)
		{
			addFile2RecordUnmatchedWithError(recordNumber);
		}
		else if (matched && !changed && !inError)
		{
			addRecordMatchedUnchangedWithoutError(recordNumber);
		}
		else if (matched && !changed && inError)
		{
			addRecordMatchedUnchangedWithError(recordNumber);
		}
		else if (matched && changed && !inError && firstFile)
		{
			addFile1RecordMatchedChangedWithoutError(recordNumber);
		}
		else if (matched && changed && !inError && !firstFile)
		{
			addFile2RecordMatchedChangedWithoutError(recordNumber);
		}
		else if (matched && changed && inError && firstFile)
		{
			addFile1RecordMatchedChangedWithError(recordNumber);
		}
		else if (matched && changed && inError && !firstFile)
		{
			addFile2RecordMatchedChangedWithError(recordNumber);
		}
	}
	
	/**
	 * Adds a record to the appropriate collection of record numbers.
	 *
	 * @param recordNumber the record number to be added
	 * @param file which transmitted file the record corresponds to
	 * @param matched whether record to be added has a match in the other transmission
	 * @param changed whether record to be added changed between transmissions
	 * @param inError whether record to be added is in error
	 */
	public void addRecordNumber(String recordNumber, File file, Matched matched, Changed changed, InError inError)
	{
		assert(matched != Matched.TOTAL && changed != Changed.TOTAL && inError != InError.TOTAL);
		
		Boolean isFirstFile = null;
		Boolean isMatched = false;
		Boolean isChanged = null;
		Boolean isInError = false;
		
		if (file == File.ONE)
		{
			isFirstFile = Boolean.valueOf(true);
		}
		else if (file == File.TWO)
		{
			isFirstFile = Boolean.valueOf(false);
		}
		if (matched == Matched.MATCHED)
		{
			isMatched = Boolean.valueOf(true);
		}
		if (changed == Changed.UNCHANGED)
		{
			isChanged = Boolean.valueOf(false);
		}
		else if (changed == Changed.CHANGED)
		{
			isChanged = Boolean.valueOf(true);
		}
		if (inError == InError.WITH_ERROR)
		{
			isInError = Boolean.valueOf(true);
		}
		
		addRecordNumber(recordNumber, isFirstFile, isMatched, isChanged, isInError);
	}
	
	public Collection<String> getFile1RecordsUnmatchedTotal()
	{
		return CollectionUtils.union(getFile1RecordsUnmatchedWithoutError(), getFile1RecordsUnmatchedWithError());
	}
	
	public Collection<String> getFile2RecordsUnmatchedTotal()
	{
		return CollectionUtils.union(getFile2RecordsUnmatchedWithoutError(), getFile2RecordsUnmatchedWithError());
	}
	
	public Collection<String> getRecordsMatchedUnchangedTotal()
	{
		return CollectionUtils.union(getRecordsMatchedUnchangedWithoutError(), getRecordsMatchedUnchangedWithError());
	}
	
	public Collection<String> getRecordsMatchedChangedTotal()
	{
		// assumes that file 1 records are the same as file 2 records, here
		return CollectionUtils.union(getFile1RecordsMatchedChangedWithoutError(), getFile1RecordsMatchedChangedWithError());
	}
	
	public Collection<String> getFile1RecordsMatchedTotalWithoutError()
	{
		return CollectionUtils.union(getRecordsMatchedUnchangedWithoutError(), getFile1RecordsMatchedChangedWithoutError());
	}
	
	public Collection<String> getFile2RecordsMatchedTotalWithoutError()
	{
		return CollectionUtils.union(getRecordsMatchedUnchangedWithoutError(), getFile2RecordsMatchedChangedWithoutError());
	}
	
	public Collection<String> getFile1RecordsMatchedTotalWithError()
	{
		return CollectionUtils.union(getRecordsMatchedUnchangedWithError(), getFile1RecordsMatchedChangedWithError());
	}
	
	public Collection<String> getFile2RecordsMatchedTotalWithError()
	{
		return CollectionUtils.union(getRecordsMatchedUnchangedWithError(), getFile2RecordsMatchedChangedWithError());
	}
	
	public Collection<String> getRecordsMatchedTotal()
	{
		// assumes that file 1 records are the same as file 2 records, here
		return CollectionUtils.union(getFile1RecordsMatchedTotalWithoutError(), getFile1RecordsMatchedTotalWithError());
	}
	
	public Collection<String> getFile1RecordsTotalWithoutError()
	{
		return CollectionUtils.union(getFile1RecordsUnmatchedWithoutError(), getFile1RecordsMatchedTotalWithoutError());
	}
	
	public Collection<String> getFile2RecordsTotalWithoutError()
	{
		return CollectionUtils.union(getFile2RecordsUnmatchedWithoutError(), getFile2RecordsMatchedTotalWithoutError());
	}
	
	public Collection<String> getFile1RecordsTotalWithError()
	{
		return CollectionUtils.union(getFile1RecordsUnmatchedWithError(), getFile1RecordsMatchedTotalWithError());
	}
	
	public Collection<String> getFile2RecordsTotalWithError()
	{
		return CollectionUtils.union(getFile2RecordsUnmatchedWithError(), getFile2RecordsMatchedTotalWithError());
	}
	
	public Collection<String> getFile1RecordsTotal()
	{
		return CollectionUtils.union(getFile1RecordsTotalWithoutError(), getFile1RecordsTotalWithError());
	}
	
	public Collection<String> getFile2RecordsTotal()
	{
		return CollectionUtils.union(getFile2RecordsTotalWithoutError(), getFile2RecordsTotalWithError());
	}
	
	public void addFile1RecordUnmatchedWithoutError(String recordNumber)
	{
		file1RecordsUnmatchedWithoutError.add(recordNumber);
	}
	
	public void addFile2RecordUnmatchedWithoutError(String recordNumber)
	{
		file2RecordsUnmatchedWithoutError.add(recordNumber);
	}
	
	public void addFile1RecordUnmatchedWithError(String recordNumber)
	{
		file1RecordsUnmatchedWithError.add(recordNumber);
	}
	
	public void addFile2RecordUnmatchedWithError(String recordNumber)
	{
		file2RecordsUnmatchedWithError.add(recordNumber);
	}
	
	public void addRecordMatchedUnchangedWithoutError(String recordNumber)
	{
		recordsMatchedUnchangedWithoutError.add(recordNumber);
	}
	
	public void addRecordMatchedUnchangedWithError(String recordNumber)
	{
		recordsMatchedUnchangedWithError.add(recordNumber);
	}
	
	public void addFile1RecordMatchedChangedWithoutError(String recordNumber)
	{
		file1RecordsMatchedChangedWithoutError.add(recordNumber);
	}
	
	public void addFile2RecordMatchedChangedWithoutError(String recordNumber)
	{
		file2RecordsMatchedChangedWithoutError.add(recordNumber);
	}
	
	public void addFile1RecordMatchedChangedWithError(String recordNumber)
	{
		file1RecordsMatchedChangedWithError.add(recordNumber);
	}
	
	public void addFile2RecordMatchedChangedWithError(String recordNumber)
	{
		file2RecordsMatchedChangedWithError.add(recordNumber);
	}
}
