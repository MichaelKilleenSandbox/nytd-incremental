package gov.hhs.acf.cb.nytd.models;

/**
 * Filename: SystemGeneratedMessage.java
 * 
 *  Copyright 2009, ICF International
 *  Created: Oct 7, 2009
 *  Author: 16939
 *
 *  COPYRIGHT STATUS: This work, authored by ICF International employees, was funded in whole or in part 
 *  under U.S. Government contract, and is, therefore, subject to the following license: The Government is 
 *  granted for itself and others acting on its behalf a paid-up, nonexclusive, irrevocable worldwide 
 *  license in this work to reproduce, prepare derivative works, distribute copies to the public, and perform 
 *  publicly and display publicly, by or on behalf of the Government. All other rights are reserved by the 
 *  copyright owner.
 */

/**
 * @author 16939
 *
 */
public class SystemGeneratedMessage extends PersistentObject {
 private String systemMessageBody;
 private String subject;
 private Long duration;
/**
 * @return the systemMessageBody
 */
public String getSystemMessageBody()
{
	return systemMessageBody;
}
/**
 * @param systemMessageBody the systemMessageBody to set
 */
public void setSystemMessageBody(String systemMessageBody)
{
	this.systemMessageBody = systemMessageBody;
}
/**
 * @return the subject
 */
public String getSubject()
{
	return subject;
}
/**
 * @param subject the subject to set
 */
public void setSubject(String subject)
{
	this.subject = subject;
}
/**
 * @return the duration
 */
public Long getDuration()
{
	return duration;
}
/**
 * @param duration the duration to set
 */
public void setDuration(Long duration)
{
	this.duration = duration;
}
 
}
