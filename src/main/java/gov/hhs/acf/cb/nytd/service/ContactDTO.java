package gov.hhs.acf.cb.nytd.service;

import gov.hhs.acf.cb.nytd.util.ContactUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class ContactDTO {
    @Getter @Setter private String id;
    @Getter @Setter private String firstname;
    @Getter @Setter private String middleinitial;
    @Getter @Setter private String lastname;
    @Getter @Setter private String prefix;
    @Getter @Setter private String suffix;
    @Getter @Setter private String agency;
    @Getter @Setter private String division;
    @Getter @Setter private String title;
    @Getter @Setter private String phone;
    @Getter @Setter private String fax;
    @Getter @Setter private String email;
    @Getter @Setter private String address1;
    @Getter @Setter private String address2;
    @Getter @Setter private String city;
    @Getter @Setter private String state;
    @Getter @Setter private String zipcode;
    @Getter @Setter private String country;

    public String getFormattedName() {
        return ContactUtil.getFormattedName(this);
    }


}
