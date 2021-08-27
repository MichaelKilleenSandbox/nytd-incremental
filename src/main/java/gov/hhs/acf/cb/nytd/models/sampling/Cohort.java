package gov.hhs.acf.cb.nytd.models.sampling;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Cohort implements Serializable {
    
    private static final long serialVersionUID = 467154015423700925L;

    private String name;
    private CohortSamplingStatus samplingStatus;
    private Date date;
    private List<String> comments;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSamplingStatus(CohortSamplingStatus samplingStatus) {
        this.samplingStatus = samplingStatus;
    }

    public CohortSamplingStatus getSamplingStatus() {
        return samplingStatus;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public List<String> getComments() {
        return comments;
    }

}
