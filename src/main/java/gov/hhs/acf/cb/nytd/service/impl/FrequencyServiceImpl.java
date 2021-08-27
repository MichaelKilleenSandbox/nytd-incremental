package gov.hhs.acf.cb.nytd.service.impl;

import gov.hhs.acf.cb.nytd.dao.ElementDAO;
import gov.hhs.acf.cb.nytd.dao.FrequencyDAO;
import gov.hhs.acf.cb.nytd.models.helper.Frequency;
import gov.hhs.acf.cb.nytd.service.FrequencyService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * Implements FrequencyService
 * @author Adam Russell (18816)
 * @see FrequencyService
 */
@Transactional
public class FrequencyServiceImpl extends BaseServiceImpl implements FrequencyService
{
	private final FrequencyDAO frequencyDAO;
	private final ElementDAO elementDAO;
	
	public FrequencyServiceImpl(FrequencyDAO frequencyDAO, ElementDAO elementDAO)
	{
		super();
		
		this.frequencyDAO = frequencyDAO;
		this.elementDAO = elementDAO;
	}
	
	/**
	 * @author Adam Russel (18816)
	 * @see FrequencyService#getFrequencies(Collection, Collection, Collection, Boolean, Boolean, Boolean)
	 */
	@Override
	public List<Frequency> getFrequencies(Collection<String> states, Collection<String> reportPeriods,
			Collection<String> elements, Boolean byState, Boolean byReportPeriod, Boolean byElement)
	{
		return frequencyDAO.getFrequencies(states, reportPeriods, elements,
		                                   byState, byReportPeriod, byElement);
	}
	
	/**
	 * @author Adam Russell (18816)
	 * @see FrequencyService#getElementSelectMap()
	 */
	@Override
	public Map<String, String> getElementSelectMap()
	{
		return elementDAO.getElementSelectMapForFrequencies();
	}
}
