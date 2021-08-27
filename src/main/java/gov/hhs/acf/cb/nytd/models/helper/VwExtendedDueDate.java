package gov.hhs.acf.cb.nytd.models.helper;

import gov.hhs.acf.cb.nytd.models.PersistentObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Calendar;

/*
 * VwExtendedDueDate model
 */
@SuppressWarnings("serial")
@ToString
public class VwExtendedDueDate extends PersistentObject {

    private boolean enableEdit;
    @Getter @Setter private Long dueDateId;
    @Getter @Setter private Calendar submissionDate;
    @Getter @Setter private Long eddStateId;
    @Getter @Setter private String eddStateName;
    @Getter @Setter private String transmissionTypeName;
    @Getter @Setter private Long reportingPeriodId;
    @Getter @Setter private String reportingPeriodName;
    @Getter @Setter private Calendar startReportingDate;
    @Getter @Setter private Calendar endReportingDate;
    @Getter @Setter private Long outComeAge;
    @Getter @Setter private Long extendedDueDateId;
    @Getter @Setter private Calendar extendedDueDate;
    @Getter @Setter private Calendar extendedCreateDate;
    @Getter @Setter private String extendedCreatedBy;
    @Getter @Setter private Calendar extendedUpdateDate;
    @Getter @Setter private String extendedUpdateBy;
    @Getter @Setter private String reason;
    @Getter @Setter private Character deleteFlag;

    /*
    * No argument constructor
    */
    public VwExtendedDueDate(){}

    /*
    * Constructor taking vwExtendedDueDateId
    */
    public VwExtendedDueDate(Long vwExtendedDueDateId) {
        this.id = vwExtendedDueDateId;
    }

    /*
    * A method to check if the report period 
    * should be enable or not for editing
    */
    public boolean isEnableEdit() {
        enableEdit = false;
        Integer yearFromReportingPeriod = Integer.parseInt(this.getReportingPeriodName().substring(0,4));
        LocalDate currentDate = LocalDate.now();
        Integer currentYear = currentDate.getYear();
        if(yearFromReportingPeriod >= currentYear) {
            enableEdit = true;
        }
        return enableEdit;
    }
    
    /*
    * Setter for enableEdit
    */
    public void setEnableEdit(boolean enableEdit) {
            this.enableEdit = enableEdit;
    }
}
