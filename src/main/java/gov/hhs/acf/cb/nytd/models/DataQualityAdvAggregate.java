package gov.hhs.acf.cb.nytd.models;

/**
 * Created by IntelliJ IDEA.
 * User: 13873
 * Date: May 27, 2010
 */
public class DataQualityAdvAggregate extends AggregateValue {
    private DataQualityAdvStandard dataQualityAdvStandard;
    private Transmission transmission;
    private String datumValue;

    public DataQualityAdvStandard getDataQualityAdvStandard() {
        return dataQualityAdvStandard;
    }

    public void setDataQualityAdvStandard(DataQualityAdvStandard dataQualityAdvStandard) {
        this.dataQualityAdvStandard = dataQualityAdvStandard;
    }

    public Transmission getTransmission() {
        return transmission;
    }

    public void setTransmission(Transmission transmission) {
        this.transmission = transmission;
    }

    public String getDatumValue() {
        return datumValue;
    }

    public void setDatumValue(String datumValue) {
        this.datumValue = datumValue;
    }
}
