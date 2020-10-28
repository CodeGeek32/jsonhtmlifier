package beautifier;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;

public class Config {

    public String user_name;
    public String password;

    public String server_url;
    public String server_name;
    public String service_name;

    public String path_to_ocexe;

    public boolean keep_scanning;

    public int numberOfMessagesToTake;

    public boolean showLineNumbers;

    public Config() {
        showLineNumbers = false;
        numberOfMessagesToTake = 0;
    }

    public static Config load_config_from_file(String path_to_json) throws Exception {

        Config config = new Config();

        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(path_to_json)) {

            JSONObject o = (JSONObject) jsonParser.parse(reader);
            config.path_to_ocexe = (String) o.get("pathToOCexe");

            JSONObject network = (JSONObject) o.get("network");
            config.service_name = (String) network.get("serviceName");
            config.user_name = (String) network.get("userName");
            config.password = (String) network.get("password");
            config.server_name = (String) network.get("serverName");
            config.server_url = (String) network.get("serverUrl");

            config.keep_scanning = try_get_boolean(o.get("keepScanning"));
            config.numberOfMessagesToTake = try_get_int(o.get("numberOfMessagesToTake"));
            config.showLineNumbers = try_get_boolean(o.get("showLineNumbers"));
        } catch (ParseException e) {
            System.out.println(e.getStackTrace());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
            e.printStackTrace();
        }

        return config;
    }

    private static boolean try_get_boolean(Object object) throws Exception {

        if (object.getClass() == boolean.class)
            return (boolean) object;
        else if (object.getClass() == String.class) {
            String value = ((String) object).toLowerCase();
            int hashCode = value.hashCode();
            if (hashCode == "yes".hashCode() || hashCode == "no".hashCode()) return hashCode == "yes".hashCode();
            if (hashCode == "true".hashCode() || hashCode == "false".hashCode()) return hashCode == "true".hashCode();
        }

        throw new Exception("Object is of unrecognised type: " + object.getClass());
    }

    private static int try_get_int(Object object) throws Exception {

        if (object.getClass() == int.class)
            return (int) object;
        else if (object.getClass() == long.class)
            return (int) (long) object;
        else if (object.getClass() == Long.class)
            return (int) (long) object;
        else if (object.getClass() == String.class)
            return Integer.parseInt((String) object);

        throw new Exception("Object is of unrecognised type: " + object.getClass());
    }

}
