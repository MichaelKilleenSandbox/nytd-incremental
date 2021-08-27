package gov.hhs.acf.cb.nytd.models;

import lombok.Getter;
import lombok.Setter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/*
 * ExtendedDueDate model
 */
public class ExtendedDueDate  extends PersistentObject {
	
    private Calendar extendedDueDateCal;	
    @Getter @Setter private Long stateId;
    @Getter @Setter private Long dueDateId;	
    @Getter @Setter private String reason;
    @Getter @Setter private DueDate dueDate;
    @Getter @Setter private State state;
    @Getter @Setter private String strExtendedDueDate;
    @Getter @Setter private Character deleteFlag;

    /*
    * No argument constructor
    */
    public ExtendedDueDate(){}

    /*
    * Constructor taking extended due date id
    */
    public ExtendedDueDate(Long extendedDueDateId) {
        this.id = extendedDueDateId;
    }

    /*
    * Getter for extendedDueDateCal
    */
    public Calendar getExtendedDueDateCal() {
        return this.extendedDueDateCal;
    }

    /*
    * Setter for extendedDueDateCal
    */
    public void setExtendedDueDateCal(Calendar extendedDueDateCal) {
        this.extendedDueDateCal = extendedDueDateCal;
        DateFormat dateFormat = new SimpleDateFormat("MMMMM dd, yyyy");
        setStrExtendedDueDate(dateFormat.format(extendedDueDateCal.getTime()));
    }
}
