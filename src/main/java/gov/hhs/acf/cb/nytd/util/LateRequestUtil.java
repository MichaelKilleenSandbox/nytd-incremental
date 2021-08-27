package gov.hhs.acf.cb.nytd.util;

import com.opensymphony.xwork2.TextProvider;
import org.apache.log4j.Logger;

import java.util.Calendar;

/**
 * A utility for comparing the current date to month a day properties in a resource bundle
 * 
 * @author rees.byars
 * 
 */
public class LateRequestUtil {

    private static final Logger LOG = Logger.getLogger(LateRequestUtil.class);
    
    /**
     * late.request.deadline.month
     */
    public static final String DEADLINE_MONTH_KEY = "late.request.deadline.month";
    
    /**
     * late.request.deadline.day
     */
    public static final String DEADLINE_DAY_KEY = "late.request.deadline.day";
    
    /**
     * early.request.start.month
     */
    public static final String EARLY_MONTH_KEY = "early.request.start.month";
    
    /**
     * early.request.start.day
     */
    
    public static final String EARLY_DAY_KEY = "early.request.start.day";

    /**
     * Given a {@link TextProvider}, retrieves the properties indicated by
     * {@link #DEADLINE_DAY_KEY} and {@link #DEADLINE_MONTH_KEY}
     * and compares the month and day properties to the current month and day.  
     * 
     * @param textProvider
     * @return <code>true</code> if the current day comes after the day indicated by the properties
     */
    public static boolean isLateRequest(TextProvider textProvider, int reportYear17) {

        int deadlineMonth = -1;
        int deadlineDay = 0;

        try {

            deadlineMonth = deadlineMonth + Integer.parseInt(textProvider.getText(DEADLINE_MONTH_KEY));

            if (LOG.isDebugEnabled()) {
                LOG.debug("Retrieved Deadline Month with value:  " + deadlineMonth);
            }

        } catch (Exception e) {

            LOG.error("Could not retrieve Sampling Request Deadline Month value from any property file, please verify the " + DEADLINE_MONTH_KEY
                    + " property is set in a resource bundle on the classpath of the action class.  See stacktrace:  \n"
                    + e.getStackTrace());

        }

        try {

            deadlineDay = Integer.parseInt(textProvider.getText(DEADLINE_DAY_KEY));

            if (LOG.isDebugEnabled()) {
                LOG.debug("Retrieved Deadline Day with value:  " + deadlineDay);
            }

        } catch (Exception ee) {
            
            LOG.error("Could not retrieve Sampling Request Deadline Day value from any property file, please verify the " + DEADLINE_DAY_KEY
                    + " property is set in a resource bundle on the classpath of the action class.  See stacktrace:  \n"
                    + ee.getStackTrace());

        }

        Calendar calendar = Calendar.getInstance();
        
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentYear = calendar.get(Calendar.YEAR);

        return  (currentYear > (reportYear17 +1)) || ((currentMonth > deadlineMonth) && (currentYear == (reportYear17 +1))) || (currentDay > deadlineDay && currentMonth == deadlineMonth && (currentYear == (reportYear17 +1)));
    }
    
    
    public static boolean isEarlyRequest(TextProvider textProvider, int reportYear17) {
    	
    	int earlyMonth = 0;
        int earlyDay = 0;

        try {

        	earlyMonth = earlyMonth + Integer.parseInt(textProvider.getText(EARLY_MONTH_KEY));

            if (LOG.isDebugEnabled()) {
                LOG.debug("Retrieved Earliest Sampling Start Month with value:  " + earlyMonth);
            }

        } catch (Exception e) {

            LOG.error("Could not retrieve Sampling Request Start Month value from any property file, please verify the " + EARLY_MONTH_KEY
                    + " property is set in a resource bundle on the classpath of the action class.  See stacktrace:  \n"
                    + e.getStackTrace());

        }

        try {

        	earlyDay = Integer.parseInt(textProvider.getText(EARLY_DAY_KEY));

            if (LOG.isDebugEnabled()) {
                LOG.debug("Retrieved Earliest Sampling Start Day with value:  " + earlyDay);
            }

        } catch (Exception ee) {
            
            LOG.error("Could not retrieve Sampling Request Start Day value from any property file, please verify the " + DEADLINE_DAY_KEY
                    + " property is set in a resource bundle on the classpath of the action class.  See stacktrace:  \n"
                    + ee.getStackTrace());

        }

        Calendar calendar = Calendar.getInstance();
        
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentYear = calendar.get(Calendar.YEAR);

        return  (currentYear < (reportYear17 +1)) || (((currentMonth +1) < earlyMonth) && (currentYear == (reportYear17 +1)));
    	
    }

}
