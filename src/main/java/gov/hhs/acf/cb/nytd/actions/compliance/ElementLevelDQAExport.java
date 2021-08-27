package gov.hhs.acf.cb.nytd.actions.compliance;

import com.opensymphony.xwork2.ActionSupport;
import gov.hhs.acf.cb.nytd.actions.ExportableTable;
import gov.hhs.acf.cb.nytd.models.ElementLevelAdvisory;
import gov.hhs.acf.cb.nytd.service.DataExtractionService;

import java.util.HashMap;
import java.util.Map;

/**
 * User: 23839
 * Date: Jan 12, 2011
 */
public class ElementLevelDQAExport extends ExportableTable<ElementLevelAdvisory>
{
	public ElementLevelDQAExport(ActionSupport action, DataExtractionService dataExtractionService)
	{
		super(action, dataExtractionService);
	}

	protected void addColumns()
	{
		addColumn("Element Number/Name", new ValueProvider<ElementLevelAdvisory>()
		{
			public String getValue(final ElementLevelAdvisory elementLevelAdvisory)
			{
				return elementLevelAdvisory.getElementLevelDQAStandard().getAllowedValue().getElement().getName()
						+" - "+elementLevelAdvisory.getElementLevelDQAStandard().getAllowedValue().getElement()
						.getDescription();
			}
		});
/*		addColumn("Element Name", new ExportableTable.ValueProvider<ElementLevelAdvisory>()
		{
			public String getValue(final ElementLevelAdvisory elementLevelAdvisory)
			{
				return elementLevelAdvisory.getElementLevelDQAStandard().getAllowedValue().getElement()
						.getDescription();
			}
		});*/
		addColumn("Value (Subject Population)", new ValueProvider<ElementLevelAdvisory>()
		{
			public String getValue(final ElementLevelAdvisory elementLevelAdvisory)
			{
				Map<String, Object> namedParams = new HashMap<String, Object>();
				namedParams.put("datumValue", elementLevelAdvisory.getElementLevelDatumValue());
				return elementLevelAdvisory.formatText(elementLevelAdvisory.getElementLevelDQAStandard()
						.getDescription() != null ? elementLevelAdvisory.getElementLevelDQAStandard()
								.getDescription() : elementLevelAdvisory.getElementLevelDQAStandard()
								.getConditionalDescription(), namedParams);
			}
		});
		addColumn("Value Rate", new ValueProvider<ElementLevelAdvisory>()
		{
			public String getValue(final ElementLevelAdvisory elementLevelAdvisory)
			{
				return elementLevelAdvisory.getPercentValue().toString();
			}
		});
		addColumn("Advisory Threshold", new ValueProvider<ElementLevelAdvisory>()
		{
			public String getValue(final ElementLevelAdvisory elementLevelAdvisory)
			{
				return elementLevelAdvisory.getElementLevelDQAStandard().getDataQualityAdvNotation() + elementLevelAdvisory.getElementLevelDQAStandard().getDataQualityAdvStandard().toString();
			}
		});
	}
}
