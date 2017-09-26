package es.gmbdesign.invoiceme.utiles;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;

public class PropertyUtil {

    private static Properties properties = null;
    private static Logger logger = Logger.getLogger(PropertyUtil.class);
    private static String pathProperties = "./init.properties";
    private static long lastLoaded;
    private static long refreshPeriod;

    static {
        initializeProperties();
    }

    public static String getProperty(String key) {
        return getProperty(key, null);
    }

    public static String getProperty(String key, String defaultValue) {
        long now = System.currentTimeMillis();
        if (now > refreshPeriod + lastLoaded) {
            logger.debug("recargando Properties...");
            initializeProperties();
        }
        return getPropertyValue(key, defaultValue);
    }

    public static String getPropertyValue(String key, String defaultValue) {
        String value = properties.getProperty(key, defaultValue);
        //logger.trace("Property " + key + "=" + value);
        return value;
    }

    private static void initializeProperties() {
        Properties tmpProperties = getProperties();
        if (tmpProperties == null) {
            logger.fatal("Properties no han podido ser cargadas");
        } else {
            properties = tmpProperties;
            lastLoaded = System.currentTimeMillis();
            try {
                refreshPeriod = Long.parseLong(getPropertyValue("properties_refresh_period", null));
            } catch (NumberFormatException e) {
                logger.error("Wrong value for the properties_refresh_period, it must be a long");
                refreshPeriod = 30 * 60 * 1000;
            }
        }
    }

    private static Properties getProperties() {
        Properties tmpProperties = null;
        String fileURL = findURLFile();
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(fileURL);
            if (inputStream != null) {
                BufferedReader reader = null;
                reader = new BufferedReader(new InputStreamReader(inputStream, "UTF8"));
                tmpProperties = loadProperties(reader);
            }
        } catch (FileNotFoundException e) {
            logger.error("No se ha encontrado el archivo properties: " + pathProperties + ", " + e.getMessage());
        } catch (UnsupportedEncodingException e) {
            logger.error("Erro leyendo el archivo: " + pathProperties + " --> " + e.getMessage(), e);
        }
        return tmpProperties;
    }

    private static String findURLFile() {
        String fileURL = null;
        URL urlResource = PropertyUtil.class.getClassLoader().getResource(pathProperties);
        if (urlResource != null) {
            fileURL = urlResource.getFile();
        }
        if (fileURL == null) {
            fileURL = PropertyUtil.class.getResource(pathProperties).getFile();
        }
        return fileURL;
    }

    private static Properties loadProperties(BufferedReader reader) {
        Properties tmp = new Properties();
        try {
            tmp.load(reader);
            StringWriter sw = new StringWriter();
            tmp.list(new PrintWriter(sw));
        } catch (IOException e) {
            logger.error("No se pueden cargar propiedades desde el archivo " + pathProperties, e);
        }
        return tmp;
    }

    public static void setPathProperties(String pathProperties) {
        PropertyUtil.pathProperties = pathProperties;
        initializeProperties();
    }

    public static void reset() {
        setPathProperties("./init.properties");
    }
}
