package gov.hhs.acf.cb.nytd.util;

import gov.hhs.acf.cb.nytd.service.ContactDTO;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class ContactUtil {

    public static String getFormattedName(String prefix, String firstName,
                                          String middleInitial, String lastName, String suffix) {
        StringBuilder name = new StringBuilder();
        if (StringUtils.isNotBlank(prefix)) {
            name.append(prefix + " ");
        }

        name.append(firstName + " ");

        if (StringUtils.isNotBlank(middleInitial)) {
            name.append(middleInitial + " ");
        }

        name.append(lastName);

        if (StringUtils.isNotBlank(suffix)) {
            name.append(", " + suffix);
        }

        return name.toString();
    }

    public static String getFormattedName(ContactDTO contactDTO) {
        return getFormattedName(contactDTO.getPrefix(), contactDTO.getFirstname(), contactDTO.getMiddleinitial(),
                contactDTO.getLastname(), contactDTO.getSuffix());
    }

    public static String getFormattedName(Map<String, String> contactInfoMap, String prefix) {
        return getFormattedName(
                contactInfoMap.getOrDefault(prefix + Constants.MLS_QNAME_PREFIX, StringUtils.EMPTY),
                contactInfoMap.get(prefix + Constants.MLS_QNAME_FIRSTNAME),
                contactInfoMap.getOrDefault(prefix + Constants.MLS_QNAME_MIDDLEINITIAL, StringUtils.EMPTY),
                contactInfoMap.get(prefix + Constants.MLS_QNAME_LASTNAME),
                contactInfoMap.getOrDefault(prefix + Constants.MLS_QNAME_SUFFIX, StringUtils.EMPTY));
    }
}
