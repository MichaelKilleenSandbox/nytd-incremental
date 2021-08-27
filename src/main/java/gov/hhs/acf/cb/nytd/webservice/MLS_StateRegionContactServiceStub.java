/**
 * MLS_StateRegionContactServiceStub.java
 * 
 * This file was auto-generated from WSDL by the Apache Axis2 version: 1.6.2
 * Built on : Apr 17, 2012 (05:33:49 IST)
 */
package gov.hhs.acf.cb.nytd.webservice;

import gov.hhs.acf.cb.nytd.util.Constants;
import gov.hhs.acf.cb.nytd.util.PropertiesUtil;
import org.apache.axis2.context.NamedValue;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Collections;

/*
 * MLS_StateRegionContactServiceStub java implementation
 */

public class MLS_StateRegionContactServiceStub extends org.apache.axis2.client.Stub implements
		MLS_StateRegionContactService
{
	protected Logger log = Logger.getLogger(getClass());

	protected org.apache.axis2.description.AxisOperation[] _operations;

	// hashmaps to keep the fault mapping
	private java.util.HashMap faultExceptionNameMap = new java.util.HashMap();
	private java.util.HashMap faultExceptionClassNameMap = new java.util.HashMap();
	private java.util.HashMap faultMessageMap = new java.util.HashMap();

	private static int counter = 0;

	private static synchronized String getUniqueSuffix()
	{
		// reset the counter if it is greater than 99999
		if (counter > 99999)
		{
			counter = 0;
		}
		counter = counter + 1;
		return Long.toString(System.currentTimeMillis()) + "_" + counter;
	}

	private void populateAxisService() throws org.apache.axis2.AxisFault
	{

		// creating the Service with a unique name
		_service = new org.apache.axis2.description.AxisService("MLS_StateRegionContactService"
				+ getUniqueSuffix());
		addAnonymousOperations();

		// creating the operations
		org.apache.axis2.description.AxisOperation __operation;

		_operations = new org.apache.axis2.description.AxisOperation[1];

		__operation = new org.apache.axis2.description.OutInAxisOperation();

		__operation.setName(new javax.xml.namespace.QName("http://MLS.clearinghouse.caliber.com/",
				"stateRegionContactInfo"));
		_service.addOperation(__operation);

		_operations[0] = __operation;

	}

	// populates the faults
	private void populateFaults()
	{

	}

	/**
	 *Constructor that takes in a configContext
	 */

	public MLS_StateRegionContactServiceStub(
			org.apache.axis2.context.ConfigurationContext configurationContext, String targetEndpoint)
			throws org.apache.axis2.AxisFault
	{
		this(configurationContext, targetEndpoint, false);
	}

	/**
	 * Constructor that takes in a configContext and useseperate listner
	 */
	public MLS_StateRegionContactServiceStub(
			org.apache.axis2.context.ConfigurationContext configurationContext, String targetEndpoint,
			boolean useSeparateListener) throws org.apache.axis2.AxisFault
	{
		// To populate AxisService
		populateAxisService();
		populateFaults();

		_serviceClient = new org.apache.axis2.client.ServiceClient(configurationContext, _service);

		_serviceClient.getOptions().setTo(new org.apache.axis2.addressing.EndpointReference(targetEndpoint));
		_serviceClient.getOptions().setUseSeparateListener(useSeparateListener);

	}

	/**
	 * Default Constructor
	 */
	public MLS_StateRegionContactServiceStub(org.apache.axis2.context.ConfigurationContext configurationContext)
			throws org.apache.axis2.AxisFault
	{

		this(configurationContext,
				"http://ffxchxgfdev.systems.hosting.icfi.com:18080/mls-ws/MLS_StateRegionContactService");

	}

	/**
	 * Default Constructor
	 */
	public MLS_StateRegionContactServiceStub() throws org.apache.axis2.AxisFault
	{

		this("http://ffxchxgfdev.systems.hosting.icfi.com:18080/mls-ws/MLS_StateRegionContactService");

	}

	/**
	 * Constructor taking the target endpoint
	 */
	public MLS_StateRegionContactServiceStub(String targetEndpoint)
			throws org.apache.axis2.AxisFault
	{
		this(null, targetEndpoint);
	}

	/**
	 * Auto generated method signature
	 *
	 * @see MLS_StateRegionContactService#stateRegionContactInfo
	 * @param stateRegionContactInfo2
	 */

	public StateRegionContactInfoResponseE stateRegionContactInfo(

	StateRegionContactInfoE stateRegionContactInfo2)

	throws java.rmi.RemoteException, IOException

	{
		org.apache.axis2.context.MessageContext _messageContext = null;
		try
		{
			org.apache.axis2.client.OperationClient _operationClient = _serviceClient
					.createClient(_operations[0].getName());
			_operationClient
					.getOptions()
					.setAction(
							"http://MLS.clearinghouse.caliber.com/MLS_StateRegionContactService/stateRegionContactInfoRequest");
			_operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

			addPropertyToOperationClient(_operationClient,
					org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");

			// create a message context
			_messageContext = new org.apache.axis2.context.MessageContext();

			// create SOAP envelope with that payload
			org.apache.axiom.soap.SOAPEnvelope env = null;

			env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
					stateRegionContactInfo2, optimizeContent(new javax.xml.namespace.QName(
							"http://MLS.clearinghouse.caliber.com/", "stateRegionContactInfo")),
					new javax.xml.namespace.QName("http://MLS.clearinghouse.caliber.com/",
							"stateRegionContactInfo"));

			NamedValue header = new NamedValue(Constants.TOKEN_HEADER_NAME,
					PropertiesUtil.getProperty(Constants.MLS_TOKEN) );

			_serviceClient.getOptions().setProperty(
					org.apache.axis2.transport.http.HTTPConstants.HTTP_HEADERS, Collections.singletonList(header));

			// adding SOAP soap_headers
			_serviceClient.addHeadersToEnvelope(env);

			// set the message context with that soap envelope
			_messageContext.setEnvelope(env);

			// add the message contxt to the operation client
			_operationClient.addMessageContext(_messageContext);

			// execute the operation client
			_operationClient.execute(true);

			org.apache.axis2.context.MessageContext _returnMessageContext = _operationClient
					.getMessageContext(org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
			org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();

			Object object = fromOM(_returnEnv.getBody().getFirstElement(),
					StateRegionContactInfoResponseE.class,
					getEnvelopeNamespaces(_returnEnv));

			return (StateRegionContactInfoResponseE) object;

		}
		catch (org.apache.axis2.AxisFault f)
		{

			org.apache.axiom.om.OMElement faultElt = f.getDetail();
			if (faultElt != null)
			{
				if (faultExceptionNameMap.containsKey(new org.apache.axis2.client.FaultMapKey(
						faultElt.getQName(), "stateRegionContactInfo")))
				{
					// make the fault by reflection
					try
					{
						String exceptionClassName = (String) faultExceptionClassNameMap
								.get(new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),
										"stateRegionContactInfo"));
						Class exceptionClass = Class.forName(exceptionClassName);
						java.lang.reflect.Constructor constructor = exceptionClass.getConstructor(String.class);
						Exception ex = (Exception) constructor.newInstance(f.getMessage());
						// message class
						String messageClassName = (String) faultMessageMap
								.get(new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),
										"stateRegionContactInfo"));
						Class messageClass = Class.forName(messageClassName);
						Object messageObject = fromOM(faultElt, messageClass, null);
						java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
								new Class[] { messageClass });
						m.invoke(ex, new Object[] { messageObject });

						throw new java.rmi.RemoteException(ex.getMessage(), ex);
					}
					catch (ClassCastException e)
					{
						// we cannot intantiate the class - throw the original Axis
						// fault
						throw f;
					}
					catch (ClassNotFoundException e)
					{
						// we cannot intantiate the class - throw the original Axis
						// fault
						throw f;
					}
					catch (NoSuchMethodException e)
					{
						// we cannot intantiate the class - throw the original Axis
						// fault
						throw f;
					}
					catch (java.lang.reflect.InvocationTargetException e)
					{
						// we cannot intantiate the class - throw the original Axis
						// fault
						throw f;
					}
					catch (IllegalAccessException e)
					{
						// we cannot intantiate the class - throw the original Axis
						// fault
						throw f;
					}
					catch (InstantiationException e)
					{
						// we cannot instantiate the class - throw the original Axis
						// fault
						throw f;
					}
				}
				else
				{
					throw f;
				}
			}
			else
			{
				throw f;
			}
		}
		finally
		{
			if (_messageContext.getTransportOut() != null)
			{
				_messageContext.getTransportOut().getSender().cleanup(_messageContext);
			}
		}
	}

	/**
	 * Auto generated method signature for Asynchronous Invocations
	 *
	 * @see MLS_StateRegionContactService#startstateRegionContactInfo
	 * @param stateRegionContactInfo2
	 */
	public void startstateRegionContactInfo(

	StateRegionContactInfoE stateRegionContactInfo2,

	final MLS_StateRegionContactServiceCallbackHandler callback)

	throws java.rmi.RemoteException
	{

		org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[0]
				.getName());
		_operationClient
				.getOptions()
				.setAction(
						"http://MLS.clearinghouse.caliber.com/MLS_StateRegionContactService/stateRegionContactInfoRequest");
		_operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

		addPropertyToOperationClient(_operationClient,
				org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");

		// create SOAP envelope with that payload
		org.apache.axiom.soap.SOAPEnvelope env = null;
		final org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();

		// Style is Doc.

		env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()),
				stateRegionContactInfo2, optimizeContent(new javax.xml.namespace.QName(
						"http://MLS.clearinghouse.caliber.com/", "stateRegionContactInfo")),
				new javax.xml.namespace.QName("http://MLS.clearinghouse.caliber.com/", "stateRegionContactInfo"));

		// adding SOAP soap_headers
		_serviceClient.addHeadersToEnvelope(env);
		// create message context with that soap envelope
		_messageContext.setEnvelope(env);

		// add the message context to the operation client
		_operationClient.addMessageContext(_messageContext);

		_operationClient.setCallback(new org.apache.axis2.client.async.AxisCallback()
		{
			public void onMessage(org.apache.axis2.context.MessageContext resultContext)
			{
				try
				{
					org.apache.axiom.soap.SOAPEnvelope resultEnv = resultContext.getEnvelope();

					Object object = fromOM(resultEnv.getBody().getFirstElement(),
							StateRegionContactInfoResponseE.class,
							getEnvelopeNamespaces(resultEnv));
					callback
							.receiveResultstateRegionContactInfo((StateRegionContactInfoResponseE) object);

				}
				catch (org.apache.axis2.AxisFault e)
				{
					callback.receiveErrorstateRegionContactInfo(e);
				}
			}

			public void onError(Exception error)
			{
				if (error instanceof org.apache.axis2.AxisFault)
				{
					org.apache.axis2.AxisFault f = (org.apache.axis2.AxisFault) error;
					org.apache.axiom.om.OMElement faultElt = f.getDetail();
					if (faultElt != null)
					{
						if (faultExceptionNameMap.containsKey(new org.apache.axis2.client.FaultMapKey(faultElt
								.getQName(), "stateRegionContactInfo")))
						{
							// make the fault by reflection
							try
							{
								String exceptionClassName = (String) faultExceptionClassNameMap
										.get(new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),
												"stateRegionContactInfo"));
								Class exceptionClass = Class.forName(exceptionClassName);
								java.lang.reflect.Constructor constructor = exceptionClass
										.getConstructor(String.class);
								Exception ex = (Exception) constructor
										.newInstance(f.getMessage());
								// message class
								String messageClassName = (String) faultMessageMap
										.get(new org.apache.axis2.client.FaultMapKey(faultElt.getQName(),
												"stateRegionContactInfo"));
								Class messageClass = Class.forName(messageClassName);
								Object messageObject = fromOM(faultElt, messageClass, null);
								java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
										new Class[] { messageClass });
								m.invoke(ex, new Object[] { messageObject });

								callback.receiveErrorstateRegionContactInfo(new java.rmi.RemoteException(ex
										.getMessage(), ex));
							}
							catch (ClassCastException e)
							{
								// we cannot intantiate the class - throw the original
								// Axis fault
								callback.receiveErrorstateRegionContactInfo(f);
							}
							catch (ClassNotFoundException e)
							{
								// we cannot intantiate the class - throw the original
								// Axis fault
								callback.receiveErrorstateRegionContactInfo(f);
							}
							catch (NoSuchMethodException e)
							{
								// we cannot intantiate the class - throw the original
								// Axis fault
								callback.receiveErrorstateRegionContactInfo(f);
							}
							catch (java.lang.reflect.InvocationTargetException e)
							{
								// we cannot intantiate the class - throw the original
								// Axis fault
								callback.receiveErrorstateRegionContactInfo(f);
							}
							catch (IllegalAccessException e)
							{
								// we cannot intantiate the class - throw the original
								// Axis fault
								callback.receiveErrorstateRegionContactInfo(f);
							}
							catch (InstantiationException e)
							{
								// we cannot intantiate the class - throw the original
								// Axis fault
								callback.receiveErrorstateRegionContactInfo(f);
							}
							catch (org.apache.axis2.AxisFault e)
							{
								// we cannot intantiate the class - throw the original
								// Axis fault
								callback.receiveErrorstateRegionContactInfo(f);
							}
						}
						else
						{
							callback.receiveErrorstateRegionContactInfo(f);
						}
					}
					else
					{
						callback.receiveErrorstateRegionContactInfo(f);
					}
				}
				else
				{
					callback.receiveErrorstateRegionContactInfo(error);
				}
			}

			public void onFault(org.apache.axis2.context.MessageContext faultContext)
			{
				org.apache.axis2.AxisFault fault = org.apache.axis2.util.Utils
						.getInboundFaultFromMessageContext(faultContext);
				onError(fault);
			}

			public void onComplete()
			{
				try
				{
					_messageContext.getTransportOut().getSender().cleanup(_messageContext);
				}
				catch (org.apache.axis2.AxisFault axisFault)
				{
					callback.receiveErrorstateRegionContactInfo(axisFault);
				}
			}
		});

		org.apache.axis2.util.CallbackReceiver _callbackReceiver = null;
		if (_operations[0].getMessageReceiver() == null
				&& _operationClient.getOptions().isUseSeparateListener())
		{
			_callbackReceiver = new org.apache.axis2.util.CallbackReceiver();
			_operations[0].setMessageReceiver(_callbackReceiver);
		}

		// execute the operation client
		_operationClient.execute(false);

	}

	/**
	 * A utility method that copies the namepaces from the SOAPEnvelope
	 */
	private java.util.Map getEnvelopeNamespaces(org.apache.axiom.soap.SOAPEnvelope env)
	{
		java.util.Map returnMap = new java.util.HashMap();
		java.util.Iterator namespaceIterator = env.getAllDeclaredNamespaces();
		while (namespaceIterator.hasNext())
		{
			org.apache.axiom.om.OMNamespace ns = (org.apache.axiom.om.OMNamespace) namespaceIterator.next();
			returnMap.put(ns.getPrefix(), ns.getNamespaceURI());
		}
		return returnMap;
	}

	private javax.xml.namespace.QName[] opNameArray = null;

	private boolean optimizeContent(javax.xml.namespace.QName opName)
	{

		if (opNameArray == null)
		{
			return false;
		}
		for (int i = 0; i < opNameArray.length; i++)
		{
			if (opName.equals(opNameArray[i]))
			{
				return true;
			}
		}
		return false;
	}

	// http://ffxchxgfdev.systems.hosting.icfi.com:18080/mls-ws/MLS_StateRegionContactService
	private org.apache.axiom.om.OMElement toOM(StateRegionContactInfoE param,
			boolean optimizeContent) throws org.apache.axis2.AxisFault
	{

		try
		{
			return param.getOMElement(StateRegionContactInfoE.MY_QNAME,
					org.apache.axiom.om.OMAbstractFactory.getOMFactory());
		}
		catch (org.apache.axis2.databinding.ADBException e)
		{
			throw org.apache.axis2.AxisFault.makeFault(e);
		}

	}

	private org.apache.axiom.om.OMElement toOM(
			StateRegionContactInfoResponseE param, boolean optimizeContent)
			throws org.apache.axis2.AxisFault
	{

		try
		{
			return param.getOMElement(StateRegionContactInfoResponseE.MY_QNAME,
					org.apache.axiom.om.OMAbstractFactory.getOMFactory());
		}
		catch (org.apache.axis2.databinding.ADBException e)
		{
			throw org.apache.axis2.AxisFault.makeFault(e);
		}

	}

	private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory,
			StateRegionContactInfoE param, boolean optimizeContent,
			javax.xml.namespace.QName methodQName) throws org.apache.axis2.AxisFault
	{

		try
		{

			org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
			emptyEnvelope.getBody().addChild(
					param.getOMElement(StateRegionContactInfoE.MY_QNAME, factory));
			return emptyEnvelope;
		}
		catch (org.apache.axis2.databinding.ADBException e)
		{
			throw org.apache.axis2.AxisFault.makeFault(e);
		}

	}

	/* methods to provide back word compatibility */

	/**
	 * get the default envelope
	 */
	private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory)
	{
		return factory.getDefaultEnvelope();
	}

	private Object fromOM(org.apache.axiom.om.OMElement param, Class type,
			java.util.Map extraNamespaces) throws org.apache.axis2.AxisFault
	{

		try
		{

			if (StateRegionContactInfoE.class.equals(type))
			{

				return StateRegionContactInfoE.Factory.parse(param
						.getXMLStreamReaderWithoutCaching());

			}

			if (StateRegionContactInfoResponseE.class.equals(type))
			{

				return StateRegionContactInfoResponseE.Factory.parse(param
						.getXMLStreamReaderWithoutCaching());

			}

		}
		catch (Exception e)
		{
			throw org.apache.axis2.AxisFault.makeFault(e);
		}
		return null;
	}

}
