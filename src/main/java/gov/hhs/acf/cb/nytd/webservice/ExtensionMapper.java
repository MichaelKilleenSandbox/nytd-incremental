/**
 * ExtensionMapper.java
 * 
 * This file was auto-generated from WSDL by the Apache Axis2 version: 1.6.2
 * Built on : Apr 17, 2012 (05:34:40 IST)
 */

package gov.hhs.acf.cb.nytd.webservice;

/**
 * ExtensionMapper class
 */
@SuppressWarnings( { "unchecked", "unused" })
public class ExtensionMapper
{

	public static Object getTypeObject(String namespaceURI, String typeName,
                                       javax.xml.stream.XMLStreamReader reader) throws Exception
	{

		if ("http://MLS.clearinghouse.caliber.com/".equals(namespaceURI)
				&& "stateRegionContactInfoResponse".equals(typeName))
		{

			return StateRegionContactInfoResponse.Factory.parse(reader);

		}

		if ("http://MLS.clearinghouse.caliber.com/".equals(namespaceURI)
				&& "stateRegionContactInfo".equals(typeName))
		{

			return StateRegionContactInfo.Factory.parse(reader);

		}

		throw new org.apache.axis2.databinding.ADBException("Unsupported type " + namespaceURI + " " + typeName);
	}

}
