package gov.hhs.acf.cb.nytd.util;

import java.io.IOException;
import java.util.Properties;

public class PropertiesUtil {

    private static Properties properties;

    public static String getProperty(String name) throws IOException {
        if (properties == null) {
            properties = new Properties();
            properties.load(PropertiesUtil.class.getResourceAsStream(Constants.PROPERTIES_FILE_PATH));
        }
        return properties.getProperty(name);
    }
}
