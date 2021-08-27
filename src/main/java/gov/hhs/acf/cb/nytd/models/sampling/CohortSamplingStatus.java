package gov.hhs.acf.cb.nytd.models.sampling;

public enum CohortSamplingStatus {
    
    NOT_REQUESTED("Not Requested"), 
    SUBMITTED("Submitted"), 
    COMMENTS_PROVIDED("Comments Provided"), 
    REJECTED("Rejected"), 
    APPROVED("Approved"), 
    SAMPLE_DRAWN("Sample Drawn");
    
    private String prettyPrint;
    
    private CohortSamplingStatus(String prettyPrint) {
        this.prettyPrint = prettyPrint;
    }
    
    public boolean isRequested() {
        return !this.equals(NOT_REQUESTED);
    }
    
    @Override
    public String toString() {
        return this.prettyPrint;
    }

}
