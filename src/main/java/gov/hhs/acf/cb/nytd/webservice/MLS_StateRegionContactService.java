

/**
 * MLS_StateRegionContactService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

    package gov.hhs.acf.cb.nytd.webservice;

    /*
     *  MLS_StateRegionContactService java interface
     */

import java.io.IOException;

public interface MLS_StateRegionContactService {
          

        /**
          * Auto generated method signature
          * 
                    * @param stateRegionContactInfo0
                
         */

         
                     public StateRegionContactInfoResponseE stateRegionContactInfo(

                        StateRegionContactInfoE stateRegionContactInfo0)
                        throws java.rmi.RemoteException, IOException
                     ;

        
         /**
            * Auto generated method signature for Asynchronous Invocations
            * 
                * @param stateRegionContactInfo0
            
          */
        public void startstateRegionContactInfo(

            StateRegionContactInfoE stateRegionContactInfo0,

            final MLS_StateRegionContactServiceCallbackHandler callback)

            throws java.rmi.RemoteException;

     

        
       //
       }
    