package beautifier;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;

@Getter
@Setter
@NoArgsConstructor
public class Config {

    String userName;
    String password;

    String serverUrl;
    String serverName;
    String serviceName;

    String pathToOcExe;

    boolean keepScanning;

    int numberOfMessagesToTake;

    boolean showLineNumbers;

    public static Config load_config_from_file(String pathToJson) throws Exception {

        Config config = new Config();

        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(pathToJson)) {

            JSONObject o = (JSONObject) jsonParser.parse(reader);
            config.setPathToOcExe((String) o.get("pathToOCexe"));

            JSONObject network = (JSONObject) o.get("network");
            config.setServiceName((String) network.get("serviceName"));
            config.setUserName((String) network.get("userName"));
            config.setPassword((String) network.get("password"));
            config.setServerName((String) network.get("serverName"));
            config.setServerUrl((String) network.get("serverUrl"));

            config.setKeepScanning(try_get_boolean(o.get("keepScanning")));
            config.setNumberOfMessagesToTake(try_get_int(o.get("numberOfMessagesToTake")));
            config.setShowLineNumbers(try_get_boolean(o.get("showLineNumbers")));
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
