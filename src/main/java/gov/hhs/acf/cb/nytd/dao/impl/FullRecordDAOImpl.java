package gov.hhs.acf.cb.nytd.dao.impl;

import gov.hhs.acf.cb.nytd.dao.FullRecordDAO;
import gov.hhs.acf.cb.nytd.models.*;
import gov.hhs.acf.cb.nytd.models.helper.DataToExportComparator;
import org.hibernate.Session;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;


@Transactional
public class FullRecordDAOImpl extends HibernateDaoSupport implements FullRecordDAO
{
	/**
	 * @author Adam Russell (18816)
	 * @see FullRecordDAO#getFullRecord(TransmissionRecord)
	 */
	@Override
	public RecordToExport getFullRecord(TransmissionRecord transmissionRecord)
	{
		if (transmissionRecord == null)
		{
			return null;
		}
		
		Session session = getSessionFactory().getCurrentSession();
		List<RecordToExport> fullRecords;
		RecordToExport fullRecord;
		String query;
		
		query = "select fullRecord  "
		      + "from RecordToExport as fullRecord "
		      + "inner join fullRecord.transmissionRecord as transmissionRecord "
		      + "where transmissionRecord.id = %d ";
		query = String.format(query, transmissionRecord.getId());
		
		fullRecords = session.createQuery(query).list();
		
		assert(fullRecords.size() == 1);
		fullRecord = fullRecords.get(0);
		
		return fullRecord;
	}
	
	/**
	 * @author Adam Russell (18816)
	 * @see FullRecordDAO#getFullRecordsForTransmission(Transmission)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<RecordToExport> getFullRecordsForTransmission(Transmission transmission)
	{
		assert(transmission != null);
		
		Session session = getSessionFactory().getCurrentSession();
		List<RecordToExport> fullRecords;
		List<Datum> data;
		String query;
		List<Element> elements;
		
		// get all the NYTD elements
		query = "select element "
		      + "from Element as element "
		      + "order by element.sort asc ";
		elements = session.createQuery(query).list();
		
		// Check the entries already in RecordToExport first, as the transmission
		// may, in fact, be an active submission.
		query = "select fullRecord "
		      + "from RecordToExport as fullRecord "
		      + "inner join fullRecord.transmission as transmission "
		      + "where transmission.id = %d ";
		query = String.format(query, transmission.getId());
		fullRecords = session.createQuery(query).list();
		
		// If not already in persisted as RecordToExport objects,
		// fabricate some objects (not to be persisted!) from all the datum objects
		// in a transmission
		if (fullRecords.isEmpty())
		{
			query = "select datum "
			      + "from Datum as datum "
			      + "join datum.transmissionRecord as record "
			      + "join record.transmission as transmission "
			      + "join datum.element as element "
			      + "where transmission.id = %d "
			      + "order by record.id asc, element.sort asc ";
			query = String.format(query, transmission.getId());
			data = session.createQuery(query).list();
			
			for (TransmissionRecord transmissionRecord : transmission.getTransmissionRecords())
			{
				RecordToExport fullRecord = new RecordToExport();
				fullRecord.setId(transmissionRecord.getId());
			//	fullRecord.setTransmissionRecord(transmissionRecord);
			//	fullRecord.setTransmission(transmissionRecord.getTransmission());
				fullRecord.setReportingPeriod(transmissionRecord.getTransmission().getReportingPeriod().getName());
				fullRecord.setFederalFileId(transmissionRecord.getTransmission().getFederalFileId());
				fullRecord.setRecordNote(transmissionRecord.getNotes());
				fullRecord.setOutcomePopulation(null);
				if (transmissionRecord.getOutcomePopulation() != null)
				{
					fullRecord.setOutcomePopulation(transmissionRecord.getOutcomePopulation().getName());
				}
				fullRecord.setServedPopulation(null);
				if (transmissionRecord.getServedPopulation() != null)
				{
					fullRecord.setServedPopulation(transmissionRecord.getServedPopulation().getName());
				}
				
				for (Element element : elements)
				{
					int datumIndex;
					String elementName = element.getName();
					Datum datum = new Datum();
					datum.setTransmissionRecord(transmissionRecord);
					datum.setElement(element);
					datumIndex = Collections.binarySearch(data, datum, new DataToExportComparator());
					datum = data.get(datumIndex);
					
					fullRecord.setElementValueAndNote(elementName, datum.getValue(), datum.getNote());
				}
				
				fullRecords.add(fullRecord);
			}
		}
		
		return fullRecords;
	}
}
