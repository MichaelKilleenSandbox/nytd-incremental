/**
 * Filename: NYTDSystem.java
 * 
 *  Copyright 2009, ICF International
 *  Created: Sep 16, 2009
 *  Author: 15178
 *
 *  COPYRIGHT STATUS: This work, authored by ICF International employees, was funded in whole or in part 
 *  under U.S. Government contract, and is, therefore, subject to the following license: The Government is 
 *  granted for itself and others acting on its behalf a paid-up, nonexclusive, irrevocable worldwide 
 *  license in this work to reproduce, prepare derivative works, distribute copies to the public, and perform 
 *  publicly and display publicly, by or on behalf of the Government. All other rights are reserved by the 
 *  copyright owner.
 */
package gov.hhs.acf.cb.nytd.models;

/**
 * @author 15178
 *
 */
public class NYTDSystem implements Sender
{
		private String name = "NYTD Federal System";
		private String signature = "NYTD Federal System Admin";
		private String beforSignatureWord = "Regards";
		private String messageAddressedTo = "Dear NYTD User,";

		/**
		 * @return the name
		 */
		public String getName()
		{
			return name;
		}

		/**
		 * @param name the name to set
		 */
		public void setName(String name)
		{
			this.name = name;
		}

		/**
		 * @return the signature
		 */
		public String getSignature()
		{
			return signature;
		}

		/**
		 * @param signature the signature to set
		 */
		public void setSignature(String signature)
		{
			this.signature = signature;
		}

		/**
		 * @return the beforSignatureWord
		 */
		public String getBeforSignatureWord()
		{
			return beforSignatureWord;
		}

		/**
		 * @param beforSignatureWord the beforSignatureWord to set
		 */
		public void setBeforSignatureWord(String beforSignatureWord)
		{
			this.beforSignatureWord = beforSignatureWord;
		}

		/**
		 * @return the messageAddressedTo
		 */
		public String getMessageAddressedTo()
		{
			return messageAddressedTo;
		}

		/**
		 * @param messageAddressedTo the messageAddressedTo to set
		 */
		public void setMessageAddressedTo(String messageAddressedTo)
		{
			this.messageAddressedTo = messageAddressedTo;
		}
		
}
