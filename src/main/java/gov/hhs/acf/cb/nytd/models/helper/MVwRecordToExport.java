/**
 * Filename: MVwRecordToExport.java
 * 
 *  Copyright 2009, ICF International
 *  Created: Dec 4, 2009
 *  Author: adam
 *
 *  COPYRIGHT STATUS: This work, authored by ICF International employees, was funded in whole or in part 
 *  under U.S. Government contract, and is, therefore, subject to the following license: The Government is 
 *  granted for itself and others acting on its behalf a paid-up, nonexclusive, irrevocable worldwide 
 *  license in this work to reproduce, prepare derivative works, distribute copies to the public, and perform 
 *  publicly and display publicly, by or on behalf of the Government. All other rights are reserved by the 
 *  copyright owner.
 */
package gov.hhs.acf.cb.nytd.models.helper;

import gov.hhs.acf.cb.nytd.models.Transmission;
import gov.hhs.acf.cb.nytd.models.TransmissionRecord;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;


/**
 * Represents data contained in active/current submissions.
 * 
 * The database only contains objects of this type if they are related to 
 * active/current submissions. In case it is desired to have an instance of this class
 * related to a record *not* related to an active/current submission, one must be
 * manually initialized. The setElementAndValue method was created to facilitate
 * this process.
 * 
 * Primarily used by the Data Export module.
 * 
 * @author Adam Russell (18816)
 */
@Entity
@Table(name = "MVWRECORDTOEXPORT")
public class MVwRecordToExport implements Serializable
{
	@Id
	@Column(name="TRANSMISSIONRECORDID")
	@Getter @Setter
	private Long id;
	
	@OneToOne(cascade=CascadeType.ALL)
	@PrimaryKeyJoinColumn
	@JoinColumn(name="TRANSMISSIONRECORDID")
	@Getter @Setter
	private TransmissionRecord transmissionRecord;
	
	@ManyToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name="TRANSMISSIONID")
	@Getter @Setter
	private Transmission transmission;
	
	@Column(name="REPORTINGPERIOD") @Getter @Setter private String reportingPeriod;
	
	@Column(name="E1_STATE", length=4) @Getter @Setter private String e1State;
	@Column(name="FEDERALFILEID", length=552) @Getter @Setter private String federalFileId;
	@Column(name="E3_RECORD_NUMBER") @Getter @Setter private String e3RecordNumber;
	@Column(name="RECORD_NOTE") @Getter @Setter private String recordNote;
	@Column(name="E2") @Getter @Setter private String e2;
	@Column(name="E4") @Getter @Setter private String e4;
	@Column(name="E4NOTES") @Getter @Setter private String e4Notes;
	@Column(name="E5") @Getter @Setter private String e5;
	@Column(name="E5NOTES") @Getter @Setter private String e5Notes;
	@Column(name="E6") @Getter @Setter private String e6;
	@Column(name="E6NOTES") @Getter @Setter private String e6Notes;
	@Column(name="E7") @Getter @Setter private String e7;
	@Column(name="E7NOTES") @Getter @Setter private String e7Notes;
	@Column(name="E8") @Getter @Setter private String e8;
	@Column(name="E8NOTES") @Getter @Setter private String e8Notes;
	@Column(name="E9") @Getter @Setter private String e9;
	@Column(name="E9NOTES") @Getter @Setter private String e9Notes;
	@Column(name="E10") @Getter @Setter private String e10;
	@Column(name="E10NOTES") @Getter @Setter private String e10Notes;
	@Column(name="E11") @Getter @Setter private String e11;
	@Column(name="E11NOTES") @Getter @Setter private String e11Notes;
	@Column(name="E12") @Getter @Setter private String e12;
	@Column(name="E12NOTES") @Getter @Setter private String e12Notes;
	@Column(name="E13") @Getter @Setter private String e13;
	@Column(name="E13NOTES") @Getter @Setter private String e13Notes;
	@Column(name="E14") @Getter @Setter private String e14;
	@Column(name="E14NOTES") @Getter @Setter private String e14Notes;
	@Column(name="E15") @Getter @Setter private String e15;
	@Column(name="E15NOTES") @Getter @Setter private String e15Notes;
	@Column(name="E16") @Getter @Setter private String e16;
	@Column(name="E16NOTES") @Getter @Setter private String e16Notes;
	@Column(name="E17") @Getter @Setter private String e17;
	@Column(name="E17NOTES") @Getter @Setter private String e17Notes;
	@Column(name="E18") @Getter @Setter private String e18;
	@Column(name="E18NOTES") @Getter @Setter private String e18Notes;
	@Column(name="E19") @Getter @Setter private String e19;
	@Column(name="E19NOTES") @Getter @Setter private String e19Notes;
	@Column(name="E20") @Getter @Setter private String e20;
	@Column(name="E20NOTES") @Getter @Setter private String e20Notes;
	@Column(name="E21") @Getter @Setter private String e21;
	@Column(name="E21NOTES") @Getter @Setter private String e21Notes;
	@Column(name="E22") @Getter @Setter private String e22;
	@Column(name="E22NOTES") @Getter @Setter private String e22Notes;
	@Column(name="E23") @Getter @Setter private String e23;
	@Column(name="E23NOTES") @Getter @Setter private String e23Notes;
	@Column(name="E24") @Getter @Setter private String e24;
	@Column(name="E24NOTES") @Getter @Setter private String e24Notes;
	@Column(name="E25") @Getter @Setter private String e25;
	@Column(name="E25NOTES") @Getter @Setter private String e25Notes;
	@Column(name="E26") @Getter @Setter private String e26;
	@Column(name="E26NOTES") @Getter @Setter private String e26Notes;
	@Column(name="E27") @Getter @Setter private String e27;
	@Column(name="E27NOTES") @Getter @Setter private String e27Notes;
	@Column(name="E28") @Getter @Setter private String e28;
	@Column(name="E28NOTES") @Getter @Setter private String e28Notes;
	@Column(name="E29") @Getter @Setter private String e29;
	@Column(name="E29NOTES") @Getter @Setter private String e29Notes;
	@Column(name="E30") @Getter @Setter private String e30;
	@Column(name="E30NOTES") @Getter @Setter private String e30Notes;
	@Column(name="E31") @Getter @Setter private String e31;
	@Column(name="E31NOTES") @Getter @Setter private String e31Notes;
	@Column(name="E32") @Getter @Setter private String e32;
	@Column(name="E32NOTES") @Getter @Setter private String e32Notes;
	@Column(name="E33") @Getter @Setter private String e33;
	@Column(name="E33NOTES") @Getter @Setter private String e33Notes;
	@Column(name="E34") @Getter @Setter private String e34;
	@Column(name="E34NOTES") @Getter @Setter private String e34Notes;
	@Column(name="E35") @Getter @Setter private String e35;
	@Column(name="E35NOTES") @Getter @Setter private String e35Notes;
	@Column(name="E36") @Getter @Setter private String e36;
	@Column(name="E36NOTES") @Getter @Setter private String e36Notes;
	@Column(name="E37") @Getter @Setter private String e37;
	@Column(name="E37NOTES") @Getter @Setter private String e37Notes;
	@Column(name="E38") @Getter @Setter private String e38;
	@Column(name="E38NOTES") @Getter @Setter private String e38Notes;
	@Column(name="E39") @Getter @Setter private String e39;
	@Column(name="E39NOTES") @Getter @Setter private String e39Notes;
	@Column(name="E40") @Getter @Setter private String e40;
	@Column(name="E40NOTES") @Getter @Setter private String e40Notes;
	@Column(name="E41") @Getter @Setter private String e41;
	@Column(name="E41NOTES") @Getter @Setter private String e41Notes;
	@Column(name="E42") @Getter @Setter private String e42;
	@Column(name="E42NOTES") @Getter @Setter private String e42Notes;
	@Column(name="E43") @Getter @Setter private String e43;
	@Column(name="E43NOTES") @Getter @Setter private String e43Notes;
	@Column(name="E44") @Getter @Setter private String e44;
	@Column(name="E44NOTES") @Getter @Setter private String e44Notes;
	@Column(name="E45") @Getter @Setter private String e45;
	@Column(name="E45NOTES") @Getter @Setter private String e45Notes;
	@Column(name="E46") @Getter @Setter private String e46;
	@Column(name="E46NOTES") @Getter @Setter private String e46Notes;
	@Column(name="E47") @Getter @Setter private String e47;
	@Column(name="E47NOTES") @Getter @Setter private String e47Notes;
	@Column(name="E48") @Getter @Setter private String e48;
	@Column(name="E48NOTES") @Getter @Setter private String e48Notes;
	@Column(name="E49") @Getter @Setter private String e49;
	@Column(name="E49NOTES") @Getter @Setter private String e49Notes;
	@Column(name="E50") @Getter @Setter private String e50;
	@Column(name="E50NOTES") @Getter @Setter private String e50Notes;
	@Column(name="E51") @Getter @Setter private String e51;
	@Column(name="E51NOTES") @Getter @Setter private String e51Notes;
	@Column(name="E52") @Getter @Setter private String e52;
	@Column(name="E52NOTES") @Getter @Setter private String e52Notes;
	@Column(name="E53") @Getter @Setter private String e53;
	@Column(name="E53NOTES") @Getter @Setter private String e53Notes;
	@Column(name="E54") @Getter @Setter private String e54;
	@Column(name="E54NOTES") @Getter @Setter private String e54Notes;
	@Column(name="E55") @Getter @Setter private String e55;
	@Column(name="E55NOTES") @Getter @Setter private String e55Notes;
	@Column(name="E56") @Getter @Setter private String e56;
	@Column(name="E56NOTES") @Getter @Setter private String e56Notes;
	@Column(name="E57") @Getter @Setter private String e57;
	@Column(name="E57NOTES") @Getter @Setter private String e57Notes;
	@Column(name="E58") @Getter @Setter private String e58;
	@Column(name="E58NOTES") @Getter @Setter private String e58Notes;
	
	public MVwRecordToExport()
	{
	}
	
	public MVwRecordToExport(TransmissionRecord transmissionRecord, Transmission transmission,
			String reportingPeriod, String e1State, String federalFileId, String e3RecordNumber,
			String recordNote, String e2, String e4, String e4Notes, String e5, String e5Notes, String e6,
			String e6Notes, String e7, String e7Notes, String e8, String e8Notes, String e9, String e9Notes,
			String e10, String e10Notes, String e11, String e11Notes, String e12, String e12Notes, String e13,
			String e13Notes, String e14, String e14Notes, String e15, String e15Notes, String e16,
			String e16Notes, String e17, String e17Notes, String e18, String e18Notes, String e19,
			String e19Notes, String e20, String e20Notes, String e21, String e21Notes, String e22,
			String e22Notes, String e23, String e23Notes, String e24, String e24Notes, String e25,
			String e25Notes, String e26, String e26Notes, String e27, String e27Notes, String e28,
			String e28Notes, String e29, String e29Notes, String e30, String e30Notes, String e31,
			String e31Notes, String e32, String e32Notes, String e33, String e33Notes, String e34,
			String e34Notes, String e35, String e35Notes, String e36, String e36Notes, String e37,
			String e37Notes, String e38, String e38Notes, String e39, String e39Notes, String e40,
			String e40Notes, String e41, String e41Notes, String e42, String e42Notes, String e43,
			String e43Notes, String e44, String e44Notes, String e45, String e45Notes, String e46,
			String e46Notes, String e47, String e47Notes, String e48, String e48Notes, String e49,
			String e49Notes, String e50, String e50Notes, String e51, String e51Notes, String e52,
			String e52Notes, String e53, String e53Notes, String e54, String e54Notes, String e55,
			String e55Notes, String e56, String e56Notes, String e57, String e57Notes, String e58,
			String e58Notes)
	{
		this.transmissionRecord = transmissionRecord;
		this.transmission = transmission;
		this.reportingPeriod = reportingPeriod;
		this.e1State = e1State;
		this.federalFileId = federalFileId;
		this.e3RecordNumber = e3RecordNumber;
		this.recordNote = recordNote;
		this.e2 = e2;
		this.e4 = e4;
		this.e4Notes = e4Notes;
		this.e5 = e5;
		this.e5Notes = e5Notes;
		this.e6 = e6;
		this.e6Notes = e6Notes;
		this.e7 = e7;
		this.e7Notes = e7Notes;
		this.e8 = e8;
		this.e8Notes = e8Notes;
		this.e9 = e9;
		this.e9Notes = e9Notes;
		this.e10 = e10;
		this.e10Notes = e10Notes;
		this.e11 = e11;
		this.e11Notes = e11Notes;
		this.e12 = e12;
		this.e12Notes = e12Notes;
		this.e13 = e13;
		this.e13Notes = e13Notes;
		this.e14 = e14;
		this.e14Notes = e14Notes;
		this.e15 = e15;
		this.e15Notes = e15Notes;
		this.e16 = e16;
		this.e16Notes = e16Notes;
		this.e17 = e17;
		this.e17Notes = e17Notes;
		this.e18 = e18;
		this.e18Notes = e18Notes;
		this.e19 = e19;
		this.e19Notes = e19Notes;
		this.e20 = e20;
		this.e20Notes = e20Notes;
		this.e21 = e21;
		this.e21Notes = e21Notes;
		this.e22 = e22;
		this.e22Notes = e22Notes;
		this.e23 = e23;
		this.e23Notes = e23Notes;
		this.e24 = e24;
		this.e24Notes = e24Notes;
		this.e25 = e25;
		this.e25Notes = e25Notes;
		this.e26 = e26;
		this.e26Notes = e26Notes;
		this.e27 = e27;
		this.e27Notes = e27Notes;
		this.e28 = e28;
		this.e28Notes = e28Notes;
		this.e29 = e29;
		this.e29Notes = e29Notes;
		this.e30 = e30;
		this.e30Notes = e30Notes;
		this.e31 = e31;
		this.e31Notes = e31Notes;
		this.e32 = e32;
		this.e32Notes = e32Notes;
		this.e33 = e33;
		this.e33Notes = e33Notes;
		this.e34 = e34;
		this.e34Notes = e34Notes;
		this.e35 = e35;
		this.e35Notes = e35Notes;
		this.e36 = e36;
		this.e36Notes = e36Notes;
		this.e37 = e37;
		this.e37Notes = e37Notes;
		this.e38 = e38;
		this.e38Notes = e38Notes;
		this.e39 = e39;
		this.e39Notes = e39Notes;
		this.e40 = e40;
		this.e40Notes = e40Notes;
		this.e41 = e41;
		this.e41Notes = e41Notes;
		this.e42 = e42;
		this.e42Notes = e42Notes;
		this.e43 = e43;
		this.e43Notes = e43Notes;
		this.e44 = e44;
		this.e44Notes = e44Notes;
		this.e45 = e45;
		this.e45Notes = e45Notes;
		this.e46 = e46;
		this.e46Notes = e46Notes;
		this.e47 = e47;
		this.e47Notes = e47Notes;
		this.e48 = e48;
		this.e48Notes = e48Notes;
		this.e49 = e49;
		this.e49Notes = e49Notes;
		this.e50 = e50;
		this.e50Notes = e50Notes;
		this.e51 = e51;
		this.e51Notes = e51Notes;
		this.e52 = e52;
		this.e52Notes = e52Notes;
		this.e53 = e53;
		this.e53Notes = e53Notes;
		this.e54 = e54;
		this.e54Notes = e54Notes;
		this.e55 = e55;
		this.e55Notes = e55Notes;
		this.e56 = e56;
		this.e56Notes = e56Notes;
		this.e57 = e57;
		this.e57Notes = e57Notes;
		this.e58 = e58;
		this.e58Notes = e58Notes;
	}

	/**
	 * Gets the value of a given element in this record.
	 * 
	 * @param elementName name of the element for which to return the value
	 * @return the value of the element in the record
	 */
	public String getElementValue(String elementName)
	{
		assert(elementName != null);
		String result = "";
		Integer elementSort = Integer.valueOf(elementName);
		
		if (elementSort.compareTo(29) < 0)
		{
			if (elementSort.compareTo(15) < 0)
			{
				if (elementSort.compareTo(8) < 0)
				{
					if (elementSort.compareTo(4) < 0)
					{
						if (elementSort.compareTo(2) < 0)
						{
							result = this.getE1State();
						}
						else if (elementSort.compareTo(2) > 0)
						{
							result = this.getE3RecordNumber();
						}
						else
						{
							result = this.getE2();
						}
					}
					else if (elementSort.compareTo(4) > 0)
					{
						if (elementSort.compareTo(6) < 0)
						{
							result = this.getE5();
						}
						if (elementSort.compareTo(6) > 0)
						{
							result = this.getE7();
						}
						else
						{
							result = this.getE6();
						}
					}
					else
					{
						result = this.getE4();
					}
				}
				else if (elementSort.compareTo(8) > 0)
				{
					if (elementSort.compareTo(12) < 0)
					{
						if (elementSort.compareTo(10) < 0)
						{
							result = this.getE9();
						}
						else if (elementSort.compareTo(10) > 0)
						{
							result = this.getE11();
						}
						else
						{
							result = this.getE10();
						}
					}
					else if (elementSort.compareTo(12) > 0)
					{
						if (elementSort.compareTo(13) > 0)
						{
							result = this.getE14();
						}
						else
						{
							result = this.getE13();
						}
					}
					else
					{
						result = this.getE12();
					}
				}
				else
				{
					result = this.getE8();
				}
			}
			else if (elementSort.compareTo(15) > 0)
			{
				if (elementSort.compareTo(23) < 0)
				{
					if (elementSort.compareTo(19) < 0)
					{
						if (elementSort.compareTo(17) < 0)
						{
							result = this.getE16();
						}
						else if (elementSort.compareTo(17) > 0)
						{
							result = this.getE18();
						}
						else
						{
							result = this.getE17();
						}
					}
					else if (elementSort.compareTo(19) > 0)
					{
						if (elementSort.compareTo(21) < 0)
						{
							result = this.getE20();
						}
						else if (elementSort.compareTo(21) > 0)
						{
							result = this.getE22();
						}
						else
						{
							result = this.getE21();
						}
					}
					else
					{
						result = this.getE19();
					}
				}
				else if (elementSort.compareTo(23) > 0)
				{
					if (elementSort.compareTo(27) < 0)
					{
						if (elementSort.compareTo(25) < 0)
						{
							result = this.getE24();
						}
						else if (elementSort.compareTo(25) > 0)
						{
							result = this.getE26();
						}
						else
						{
							result = this.getE25();
						}
					}
					else if (elementSort.compareTo(27) > 0)
					{
						if (elementSort.compareTo(28) > 0)
						{
							result = this.getE29();
						}
						else
						{
							result = this.getE28();
						}
					}
					else
					{
						result = this.getE27();
					}
				}
				else
				{
					result = this.getE23();
				}
			}
			else
			{
				result = this.getE15();
			}
		}
		else if (elementSort.compareTo(29) > 0)
		{
			if (elementSort.compareTo(44) < 0)
			{
				if (elementSort.compareTo(37) < 0)
				{
					if (elementSort.compareTo(33) < 0)
					{
						if (elementSort.compareTo(31) < 0)
						{
							result = this.getE30();
						}
						else if (elementSort.compareTo(31) > 0)
						{
							result = this.getE32();
						}
						else
						{
							result = this.getE31();
						}
					}
					else if (elementSort.compareTo(33) > 0)
					{
						if (elementSort.compareTo(35) < 0)
						{
							result = this.getE34();
						}
						if (elementSort.compareTo(35) > 0)
						{
							result = this.getE36();
						}
						else
						{
							result = this.getE35();
						}
					}
					else
					{
						result = this.getE33();
					}
				}
				else if (elementSort.compareTo(37) > 0)
				{
					if (elementSort.compareTo(41) < 0)
					{
						if (elementSort.compareTo(39) < 0)
						{
							result = this.getE38();
						}
						else if (elementSort.compareTo(39) > 0)
						{
							result = this.getE40();
						}
						else
						{
							result = this.getE39();
						}
					}
					else if (elementSort.compareTo(41) > 0)
					{
						if (elementSort.compareTo(42) > 0)
						{
							result = this.getE43();
						}
						else
						{
							result = this.getE42();
						}
					}
					else
					{
						result = this.getE41();
					}
				}
				else
				{
					result = this.getE37();
				}
			}
			else if (elementSort.compareTo(44) > 0)
			{
				if (elementSort.compareTo(52) < 0)
				{
					if (elementSort.compareTo(48) < 0)
					{
						if (elementSort.compareTo(46) < 0)
						{
							result = this.getE45();
						}
						else if (elementSort.compareTo(46) > 0)
						{
							result = this.getE47();
						}
						else
						{
							result = this.getE46();
						}
					}
					else if (elementSort.compareTo(48) > 0)
					{
						if (elementSort.compareTo(50) < 0)
						{
							result = this.getE49();
						}
						else if (elementSort.compareTo(50) > 0)
						{
							result = this.getE51();
						}
						else
						{
							result = this.getE50();
						}
					}
					else
					{
						result = this.getE48();
					}
				}
				else if (elementSort.compareTo(52) > 0)
				{
					if (elementSort.compareTo(56) < 0)
					{
						if (elementSort.compareTo(54) < 0)
						{
							result = this.getE53();
						}
						else if (elementSort.compareTo(54) > 0)
						{
							result = this.getE55();
						}
						else
						{
							result = this.getE54();
						}
					}
					else if (elementSort.compareTo(56) > 0)
					{
						if (elementSort.compareTo(57) > 0)
						{
							result = this.getE58();
						}
						else
						{
							result = this.getE57();
						}
					}
					else
					{
						result = this.getE56();
					}
				}
				else
				{
					result = this.getE52();
				}
			}
			else
			{
				result = this.getE44();
			}
		}
		else
		{
			result = this.getE29();
		}
		
		return result;
	}
	
	/**
	 * Gets a note about a given element in this record.
	 * 
	 * @param elementName name of the element for which to return the note
	 * @return a note about the element in the record
	 */
	public String getElementNote(String elementName)
	{
		assert(elementName != null);
		String result = "";
		Integer elementSort = Integer.valueOf(elementName);
		
		if (elementSort.compareTo(29) < 0)
		{
			if (elementSort.compareTo(15) < 0)
			{
				if (elementSort.compareTo(8) < 0)
				{
					if (elementSort.compareTo(4) < 0)
					{
						// notes don't exist for elements 1, 2, or 3 in MVwRecordToExport
						assert(!elementName.equals("1"));
						assert(!elementName.equals("2"));
						assert(!elementName.equals("3"));
					}
					else if (elementSort.compareTo(4) > 0)
					{
						if (elementSort.compareTo(6) < 0)
						{
							result = this.getE5Notes();
						}
						if (elementSort.compareTo(6) > 0)
						{
							result = this.getE7Notes();
						}
						else
						{
							result = this.getE6Notes();
						}
					}
					else
					{
						result = this.getE4Notes();
					}
				}
				else if (elementSort.compareTo(8) > 0)
				{
					if (elementSort.compareTo(12) < 0)
					{
						if (elementSort.compareTo(10) < 0)
						{
							result = this.getE9Notes();
						}
						else if (elementSort.compareTo(10) > 0)
						{
							result = this.getE11Notes();
						}
						else
						{
							result = this.getE10Notes();
						}
					}
					else if (elementSort.compareTo(12) > 0)
					{
						if (elementSort.compareTo(13) > 0)
						{
							result = this.getE14Notes();
						}
						else
						{
							result = this.getE13Notes();
						}
					}
					else
					{
						result = this.getE12Notes();
					}
				}
				else
				{
					result = this.getE8Notes();
				}
			}
			else if (elementSort.compareTo(15) > 0)
			{
				if (elementSort.compareTo(23) < 0)
				{
					if (elementSort.compareTo(19) < 0)
					{
						if (elementSort.compareTo(17) < 0)
						{
							result = this.getE16Notes();
						}
						else if (elementSort.compareTo(17) > 0)
						{
							result = this.getE18Notes();
						}
						else
						{
							result = this.getE17Notes();
						}
					}
					else if (elementSort.compareTo(19) > 0)
					{
						if (elementSort.compareTo(21) < 0)
						{
							result = this.getE20Notes();
						}
						else if (elementSort.compareTo(21) > 0)
						{
							result = this.getE22Notes();
						}
						else
						{
							result = this.getE21Notes();
						}
					}
					else
					{
						result = this.getE19Notes();
					}
				}
				else if (elementSort.compareTo(23) > 0)
				{
					if (elementSort.compareTo(27) < 0)
					{
						if (elementSort.compareTo(25) < 0)
						{
							result = this.getE24Notes();
						}
						else if (elementSort.compareTo(25) > 0)
						{
							result = this.getE26Notes();
						}
						else
						{
							result = this.getE25Notes();
						}
					}
					else if (elementSort.compareTo(27) > 0)
					{
						if (elementSort.compareTo(28) > 0)
						{
							result = this.getE29Notes();
						}
						else
						{
							result = this.getE28Notes();
						}
					}
					else
					{
						result = this.getE27Notes();
					}
				}
				else
				{
					result = this.getE23Notes();
				}
			}
			else
			{
				result = this.getE15Notes();
			}
		}
		else if (elementSort.compareTo(29) > 0)
		{
			if (elementSort.compareTo(44) < 0)
			{
				if (elementSort.compareTo(37) < 0)
				{
					if (elementSort.compareTo(33) < 0)
					{
						if (elementSort.compareTo(31) < 0)
						{
							result = this.getE30Notes();
						}
						else if (elementSort.compareTo(31) > 0)
						{
							result = this.getE32Notes();
						}
						else
						{
							result = this.getE31Notes();
						}
					}
					else if (elementSort.compareTo(33) > 0)
					{
						if (elementSort.compareTo(35) < 0)
						{
							result = this.getE34Notes();
						}
						if (elementSort.compareTo(35) > 0)
						{
							result = this.getE36Notes();
						}
						else
						{
							result = this.getE35Notes();
						}
					}
					else
					{
						result = this.getE33Notes();
					}
				}
				else if (elementSort.compareTo(37) > 0)
				{
					if (elementSort.compareTo(41) < 0)
					{
						if (elementSort.compareTo(39) < 0)
						{
							result = this.getE38Notes();
						}
						else if (elementSort.compareTo(39) > 0)
						{
							result = this.getE40Notes();
						}
						else
						{
							result = this.getE39Notes();
						}
					}
					else if (elementSort.compareTo(41) > 0)
					{
						if (elementSort.compareTo(42) > 0)
						{
							result = this.getE43Notes();
						}
						else
						{
							result = this.getE42Notes();
						}
					}
					else
					{
						result = this.getE41Notes();
					}
				}
				else
				{
					result = this.getE37Notes();
				}
			}
			else if (elementSort.compareTo(44) > 0)
			{
				if (elementSort.compareTo(52) < 0)
				{
					if (elementSort.compareTo(48) < 0)
					{
						if (elementSort.compareTo(46) < 0)
						{
							result = this.getE45Notes();
						}
						else if (elementSort.compareTo(46) > 0)
						{
							result = this.getE47Notes();
						}
						else
						{
							result = this.getE46Notes();
						}
					}
					else if (elementSort.compareTo(48) > 0)
					{
						if (elementSort.compareTo(50) < 0)
						{
							result = this.getE49Notes();
						}
						else if (elementSort.compareTo(50) > 0)
						{
							result = this.getE51Notes();
						}
						else
						{
							result = this.getE50Notes();
						}
					}
					else
					{
						result = this.getE48Notes();
					}
				}
				else if (elementSort.compareTo(52) > 0)
				{
					if (elementSort.compareTo(56) < 0)
					{
						if (elementSort.compareTo(54) < 0)
						{
							result = this.getE53Notes();
						}
						else if (elementSort.compareTo(54) > 0)
						{
							result = this.getE55Notes();
						}
						else
						{
							result = this.getE54Notes();
						}
					}
					else if (elementSort.compareTo(56) > 0)
					{
						if (elementSort.compareTo(57) > 0)
						{
							result = this.getE58Notes();
						}
						else
						{
							result = this.getE57Notes();
						}
					}
					else
					{
						result = this.getE56Notes();
					}
				}
				else
				{
					result = this.getE52Notes();
				}
			}
			else
			{
				result = this.getE44Notes();
			}
		}
		else
		{
			result = this.getE29Notes();
		}
		
		return result;
	}
	
	/**
	 * Sets both the value and note for a given element in this record
	 * 
	 * @param elementName name of the element for which to set the value and note
	 * @param value value of the element in the record
	 * @param note note about the element in the record
	 */
	public void setElementValueAndNote(String elementName, String value, String note)
	{
		assert(elementName != null);
		Integer elementSort = Integer.valueOf(elementName);
		
		if (elementSort.compareTo(29) < 0)
		{
			if (elementSort.compareTo(15) < 0)
			{
				if (elementSort.compareTo(8) < 0)
				{
					if (elementSort.compareTo(4) < 0)
					{
						if (elementSort.compareTo(2) < 0)
						{
							if (value != null)
							{
								this.setE1State(value);
							}
						}
						else if (elementSort.compareTo(2) > 0)
						{
							if (value != null)
							{
								this.setE3RecordNumber(value);
							}
						}
						else
						{
							if (value != null)
							{
								this.setE2(value);
							}
						}
					}
					else if (elementSort.compareTo(4) > 0)
					{
						if (elementSort.compareTo(6) < 0)
						{
							if (value != null)
							{
								this.setE5(value);
							}
							if (note != null)
							{
								this.setE5Notes(value);
							}
						}
						if (elementSort.compareTo(6) > 0)
						{
							if (value != null)
							{
								this.setE7(value);
							}
							if (note != null)
							{
								this.setE7Notes(value);
							}
						}
						else
						{
							if (value != null)
							{
								this.setE6(value);
							}
							if (note != null)
							{
								this.setE6Notes(value);
							}
						}
					}
					else
					{
						if (value != null)
						{
							this.setE4(value);
						}
						if (note != null)
						{
							this.setE4Notes(value);
						}
					}
				}
				else if (elementSort.compareTo(8) > 0)
				{
					if (elementSort.compareTo(12) < 0)
					{
						if (elementSort.compareTo(10) < 0)
						{
							if (value != null)
							{
								this.setE9(value);
							}
							if (note != null)
							{
								this.setE9Notes(value);
							}
						}
						else if (elementSort.compareTo(10) > 0)
						{
							if (value != null)
							{
								this.setE11(value);
							}
							if (note != null)
							{
								this.setE11Notes(value);
							}
						}
						else
						{
							if (value != null)
							{
								this.setE10(value);
							}
							if (note != null)
							{
								this.setE10Notes(value);
							}
						}
					}
					else if (elementSort.compareTo(12) > 0)
					{
						if (elementSort.compareTo(13) > 0)
						{
							if (value != null)
							{
								this.setE14(value);
							}
							if (note != null)
							{
								this.setE14Notes(value);
							}
						}
						else
						{
							if (value != null)
							{
								this.setE13(value);
							}
							if (note != null)
							{
								this.setE13Notes(value);
							}
						}
					}
					else
					{
						if (value != null)
						{
							this.setE12(value);
						}
						if (note != null)
						{
							this.setE12Notes(value);
						}
					}
				}
				else
				{
					if (value != null)
					{
						this.setE8(value);
					}
					if (note != null)
					{
						this.setE8Notes(value);
					}
				}
			}
			else if (elementSort.compareTo(15) > 0)
			{
				if (elementSort.compareTo(23) < 0)
				{
					if (elementSort.compareTo(19) < 0)
					{
						if (elementSort.compareTo(17) < 0)
						{
							if (value != null)
							{
								this.setE16(value);
							}
							if (note != null)
							{
								this.setE16Notes(value);
							}
						}
						else if (elementSort.compareTo(17) > 0)
						{
							if (value != null)
							{
								this.setE18(value);
							}
							if (note != null)
							{
								this.setE18Notes(value);
							}
						}
						else
						{
							if (value != null)
							{
								this.setE17(value);
							}
							if (note != null)
							{
								this.setE17Notes(value);
							}
						}
					}
					else if (elementSort.compareTo(19) > 0)
					{
						if (elementSort.compareTo(21) < 0)
						{
							if (value != null)
							{
								this.setE20(value);
							}
							if (note != null)
							{
								this.setE20Notes(value);
							}
						}
						else if (elementSort.compareTo(21) > 0)
						{
							if (value != null)
							{
								this.setE22(value);
							}
							if (note != null)
							{
								this.setE22Notes(value);
							}
						}
						else
						{
							if (value != null)
							{
								this.setE21(value);
							}
							if (note != null)
							{
								this.setE21Notes(value);
							}
						}
					}
					else
					{
						if (value != null)
						{
							this.setE19(value);
						}
						if (note != null)
						{
							this.setE19Notes(value);
						}
					}
				}
				else if (elementSort.compareTo(23) > 0)
				{
					if (elementSort.compareTo(27) < 0)
					{
						if (elementSort.compareTo(25) < 0)
						{
							if (value != null)
							{
								this.setE24(value);
							}
							if (note != null)
							{
								this.setE24Notes(value);
							}
						}
						else if (elementSort.compareTo(25) > 0)
						{
							if (value != null)
							{
								this.setE26(value);
							}
							if (note != null)
							{
								this.setE26Notes(value);
							}
						}
						else
						{
							if (value != null)
							{
								this.setE25(value);
							}
							if (note != null)
							{
								this.setE25Notes(value);
							}
						}
					}
					else if (elementSort.compareTo(27) > 0)
					{
						if (elementSort.compareTo(28) > 0)
						{
							if (value != null)
							{
								this.setE29(value);
							}
							if (note != null)
							{
								this.setE29Notes(value);
							}
						}
						else
						{
							if (value != null)
							{
								this.setE28(value);
							}
							if (note != null)
							{
								this.setE28Notes(value);
							}
						}
					}
					else
					{
						if (value != null)
						{
							this.setE27(value);
						}
						if (note != null)
						{
							this.setE27Notes(value);
						}
					}
				}
				else
				{
					if (value != null)
					{
						this.setE23(value);
					}
					if (note != null)
					{
						this.setE23Notes(value);
					}
				}
			}
			else
			{
				if (value != null)
				{
					this.setE15(value);
				}
				if (note != null)
				{
					this.setE15Notes(value);
				}
			}
		}
		else if (elementSort.compareTo(29) > 0)
		{
			if (elementSort.compareTo(44) < 0)
			{
				if (elementSort.compareTo(37) < 0)
				{
					if (elementSort.compareTo(33) < 0)
					{
						if (elementSort.compareTo(31) < 0)
						{
							if (value != null)
							{
								this.setE30(value);
							}
							if (note != null)
							{
								this.setE30Notes(value);
							}
						}
						else if (elementSort.compareTo(31) > 0)
						{
							if (value != null)
							{
								this.setE32(value);
							}
							if (note != null)
							{
								this.setE32Notes(value);
							}
						}
						else
						{
							if (value != null)
							{
								this.setE31(value);
							}
							if (note != null)
							{
								this.setE31Notes(value);
							}
						}
					}
					else if (elementSort.compareTo(33) > 0)
					{
						if (elementSort.compareTo(35) < 0)
						{
							if (value != null)
							{
								this.setE34(value);
							}
							if (note != null)
							{
								this.setE34Notes(value);
							}
						}
						if (elementSort.compareTo(35) > 0)
						{
							if (value != null)
							{
								this.setE36(value);
							}
							if (note != null)
							{
								this.setE36Notes(value);
							}
						}
						else
						{
							if (value != null)
							{
								this.setE35(value);
							}
							if (note != null)
							{
								this.setE35Notes(value);
							}
						}
					}
					else
					{
						if (value != null)
						{
							this.setE33(value);
						}
						if (note != null)
						{
							this.setE33Notes(value);
						}
					}
				}
				else if (elementSort.compareTo(37) > 0)
				{
					if (elementSort.compareTo(41) < 0)
					{
						if (elementSort.compareTo(39) < 0)
						{
							if (value != null)
							{
								this.setE38(value);
							}
							if (note != null)
							{
								this.setE38Notes(value);
							}
						}
						else if (elementSort.compareTo(39) > 0)
						{
							if (value != null)
							{
								this.setE40(value);
							}
							if (note != null)
							{
								this.setE40Notes(value);
							}
						}
						else
						{
							if (value != null)
							{
								this.setE39(value);
							}
							if (note != null)
							{
								this.setE39Notes(value);
							}
						}
					}
					else if (elementSort.compareTo(41) > 0)
					{
						if (elementSort.compareTo(42) > 0)
						{
							if (value != null)
							{
								this.setE43(value);
							}
							if (note != null)
							{
								this.setE43Notes(value);
							}
						}
						else
						{
							if (value != null)
							{
								this.setE42(value);
							}
							if (note != null)
							{
								this.setE42Notes(value);
							}
						}
					}
					else
					{
						if (value != null)
						{
							this.setE41(value);
						}
						if (note != null)
						{
							this.setE41Notes(value);
						}
					}
				}
				else
				{
					if (value != null)
					{
						this.setE37(value);
					}
					if (note != null)
					{
						this.setE37Notes(value);
					}
				}
			}
			else if (elementSort.compareTo(44) > 0)
			{
				if (elementSort.compareTo(52) < 0)
				{
					if (elementSort.compareTo(48) < 0)
					{
						if (elementSort.compareTo(46) < 0)
						{
							if (value != null)
							{
								this.setE45(value);
							}
							if (note != null)
							{
								this.setE45Notes(value);
							}
						}
						else if (elementSort.compareTo(46) > 0)
						{
							if (value != null)
							{
								this.setE47(value);
							}
							if (note != null)
							{
								this.setE47Notes(value);
							}
						}
						else
						{
							if (value != null)
							{
								this.setE46(value);
							}
							if (note != null)
							{
								this.setE46Notes(value);
							}
						}
					}
					else if (elementSort.compareTo(48) > 0)
					{
						if (elementSort.compareTo(50) < 0)
						{
							if (value != null)
							{
								this.setE49(value);
							}
							if (note != null)
							{
								this.setE49Notes(value);
							}
						}
						else if (elementSort.compareTo(50) > 0)
						{
							if (value != null)
							{
								this.setE51(value);
							}
							if (note != null)
							{
								this.setE51Notes(value);
							}
						}
						else
						{
							if (value != null)
							{
								this.setE50(value);
							}
							if (note != null)
							{
								this.setE50Notes(value);
							}
						}
					}
					else
					{
						if (value != null)
						{
							this.setE48(value);
						}
						if (note != null)
						{
							this.setE48Notes(value);
						}
					}
				}
				else if (elementSort.compareTo(52) > 0)
				{
					if (elementSort.compareTo(56) < 0)
					{
						if (elementSort.compareTo(54) < 0)
						{
							if (value != null)
							{
								this.setE53(value);
							}
							if (note != null)
							{
								this.setE53Notes(value);
							}
						}
						else if (elementSort.compareTo(54) > 0)
						{
							if (value != null)
							{
								this.setE55(value);
							}
							if (note != null)
							{
								this.setE55Notes(value);
							}
						}
						else
						{
							if (value != null)
							{
								this.setE54(value);
							}
							if (note != null)
							{
								this.setE54Notes(value);
							}
						}
					}
					else if (elementSort.compareTo(56) > 0)
					{
						if (elementSort.compareTo(57) > 0)
						{
							if (value != null)
							{
								this.setE58(value);
							}
							if (note != null)
							{
								this.setE58Notes(value);
							}
						}
						else
						{
							if (value != null)
							{
								this.setE57(value);
							}
							if (note != null)
							{
								this.setE57Notes(value);
							}
						}
					}
					else
					{
						if (value != null)
						{
							this.setE56(value);
						}
						if (note != null)
						{
							this.setE56Notes(value);
						}
					}
				}
				else
				{
					if (value != null)
					{
						this.setE52(value);
					}
					if (note != null)
					{
						this.setE52Notes(value);
					}
				}
			}
			else
			{
				if (value != null)
				{
					this.setE44(value);
				}
				if (note != null)
				{
					this.setE44Notes(value);
				}
			}
		}
		else
		{
			if (value != null)
			{
				this.setE29(value);
			}
			if (note != null)
			{
				this.setE29Notes(value);
			}
		}
	}
}
