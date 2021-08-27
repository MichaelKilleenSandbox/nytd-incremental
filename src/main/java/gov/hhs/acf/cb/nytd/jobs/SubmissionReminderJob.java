package gov.hhs.acf.cb.nytd.jobs;

import gov.hhs.acf.cb.nytd.service.TransmissionServiceP3;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class SubmissionReminderJob extends QuartzJobBean {
	
	/*public SubmissionReminderJob() {}*/
	
	private TransmissionServiceP3 transmissionServiceP3;
	
	public void setTransmissionServiceP3(TransmissionServiceP3 transmissionServiceP3) {
		this.transmissionServiceP3 = transmissionServiceP3;
	}
	
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {		
		transmissionServiceP3.submissionReminder();		
	}

}
