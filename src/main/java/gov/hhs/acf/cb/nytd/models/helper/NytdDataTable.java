package gov.hhs.acf.cb.nytd.models.helper;

import gov.hhs.acf.cb.nytd.service.DataExtractionService;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.Map;


/**
 * Adds NYTD-specific population methods to DataTable.
 * Can be used instead of addField(Integer, String, String)
 * @author Adam Russell (18816)
 */
public class NytdDataTable extends DataTable
{
	@Getter @Setter private DataExtractionService dataExtractionService;
//	@Getter @Setter private Map<String, String> nytdFields;
	
	public NytdDataTable(DataExtractionService dataExtractionService, Map<String, String> nytdFields)
	{
		super();
		this.dataExtractionService = dataExtractionService;
	//	this.setNytdFields(nytdFields);
	}

	/**
	 * Adds all of the general/non-element fields specified by the collection of numeric keys to the field set.
	 *
	 * @param c collection containing general field keys
	 * @return true if the field set changed as a result of the call 
	 */
	public boolean addGeneralFields(Collection<? extends String> c)
	{
		boolean result = false;
		for (String generalFieldKey : c)
		{
			Integer sortKey = Integer.valueOf(0 - dataExtractionService.getGeneralFields().size()
			                                  + Integer.valueOf(generalFieldKey).intValue());
			String fieldName = dataExtractionService.getShortGeneralFieldName(generalFieldKey);
			String fieldLabel = dataExtractionService.getFullGeneralFieldName(generalFieldKey);
			result = (addField(sortKey, fieldName, fieldLabel) || result);
		}
		return result;
	}
	
	/**
	 * Adds all of the element fields specified by the collection of element numbers to the field set.
	 * 
	 * @param c collection containing element numbers
	 * @return true if the field set changed as a result of the call 
	 */
	public boolean addElementFields(Collection<? extends String> c)
	{
		boolean result = false;
		for (String elementNumber : c)
		{
			String fieldName = dataExtractionService.getShortElementName(elementNumber);
			//String fieldLabel = dataExtractionService.getElementLabel(nytdFields, elementNumber);
			String fieldLabel = dataExtractionService.getElementLabel(elementNumber);
			result = (addField(Integer.valueOf(elementNumber), fieldName, fieldLabel) || result);
		}
		return result;
	}
	
	/**
	 * Adds all of the data-level note fields specified by the collection of element numbers to the field set.
	 * 
	 * @param c collection containing element numbers
	 * @return true if the field set changed as a result of the call 
	 */
	public boolean addNoteFields(Collection<? extends String> c)
	{
		boolean result = false;
		for (String elementNumber : c)
		{
			String fieldName = dataExtractionService.getShortNoteName(elementNumber);
		//	String fieldLabel = dataExtractionService.getNoteLabel(nytdFields, elementNumber);
			String fieldLabel = dataExtractionService.getNoteLabel(elementNumber);
			result = (addField(Integer.valueOf(elementNumber), fieldName, fieldLabel) || result);
		}
		return result;
	}
}
