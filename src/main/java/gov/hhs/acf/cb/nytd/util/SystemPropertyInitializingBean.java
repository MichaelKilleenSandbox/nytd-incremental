/**
 * Filename: SystemPropertyInitializingBean.java
 * 
 *  Created: Jan 12, 2010
 *  Author: adam
 */
package gov.hhs.acf.cb.nytd.util;

import org.springframework.beans.factory.InitializingBean;

import java.util.Map;


/**
 * Bean for automatically initializing System
 * properties from within a Spring context.
 * 
 * This is a modified version of the class found on:
 * http://rolfje.wordpress.com/2008/07/23/spring-systempropertyinitilizingbean/
 * 
 * @author Adam Russell
 */
public class SystemPropertyInitializingBean implements InitializingBean
{
	/** Properties to be set */
	private Map<String, String> systemProperties;

	/** Sets the system properties */
	public void afterPropertiesSet() throws Exception
	{
		if (systemProperties == null || systemProperties.isEmpty())
		{
			// No properties to initialize
			return;
		}
		
		for (String key : systemProperties.keySet())
		{
			String value = systemProperties.get(key);
			System.setProperty(key, value);
		}
	}

	public void setSystemProperties(Map<String, String> systemProperties)
	{
		this.systemProperties = systemProperties;
	}
}
