package gov.hhs.acf.cb.nytd.models.helper;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: 13873
 * Date: Jun 8, 2010
 */
public class VwNote implements Serializable
{
	private String noteId;
	private String recordNumber;
	private String elementName;
	private String elementDescription;
	private String datumValue;
	private String noteText;

	public String getNoteId()
	{
		return noteId;
	}

	public void setNoteId(String noteId)
	{
		this.noteId = noteId;
	}

	public String getRecordNumber()
	{
		return recordNumber;
	}

	public void setRecordNumber(String recordNumber)
	{
		this.recordNumber = recordNumber;
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

	public String getDatumValue()
	{
		return datumValue;
	}

	public void setDatumValue(String datumValue)
	{
		this.datumValue = datumValue;
	}

	public String getNoteText()
	{
		return noteText;
	}

	public void setNoteText(String noteText)
	{
		this.noteText = noteText;
	}
}
