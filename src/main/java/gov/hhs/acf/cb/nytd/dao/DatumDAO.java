package gov.hhs.acf.cb.nytd.dao;

import gov.hhs.acf.cb.nytd.models.Datum;

import java.util.List;


/**
 * Datum access
 */
public interface DatumDAO
{
	List<Datum> getDataForRecord(final Long transmissionRecordId);
}
