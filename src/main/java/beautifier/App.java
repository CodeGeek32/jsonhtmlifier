package beautifier;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javafx.util.Pair;

public class App {

    private String PodName = "";
    private Config Config;

    public static void main(String[] args) throws Exception {
        // TODO: move cli from windows-installed into internal directory( copy'n'paste? research )
        // TODO: serve page on a localhost server

        App app = new App();

        String current_directory = System.getProperty("user.dir") + "\\";
//        String config_name = "config_credithistory_reactive_service_develop2.json";

        String config_name = "config_common.json";

        String enforce_service_name = "okr-reactive-service";
//        String enforce_service_name = "front-adapter";

//        String enforce_service_name = "income-service";
//          String enforce_service_name = "customer-service";
//        String enforce_service_name = "customercheck-service";
//        String enforce_service_name = "loanrequest-service";

//        String enforce_service_name = "income-service";

//        String enforce_service_name = "credithistory-reactive-service";
//        String enforce_server_name = "test3";
//        String enforce_server_name = "release3";
//        String enforce_server_name = "release2";


//        String enforce_server_name = "develop4";
        String enforce_server_name = "develop3";
//        String enforce_server_name = "develop4";

        app.Config = app.Config.load_config_from_file(current_directory + config_name);
        if (!IsNullOrEmpty(enforce_server_name))
            app.Config.setServerName(enforce_server_name);
        if (!IsNullOrEmpty(enforce_service_name))
            app.Config.setServiceName(enforce_service_name);

        if (!app.login(app.Config))
            throw new Exception("Could not login");

        Pair<Boolean, String> podName = App.tryGetPod(app.Config, 10);
        if (podName.getKey())
            app.PodName = podName.getValue();

        String path_to_output = current_directory + "output\\" + app.Config.getServiceName() + "_log_beautiful";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

        do {
            List<String> input_strings = null;
            try {
                input_strings = app.fetch_log(app.Config, app.PodName);
            } catch (Exception e) {
                System.out.println("Trying to get pod name");
                podName = App.tryGetPod(app.Config, 10);
                if (podName.getKey())
                    app.PodName = podName.getValue();
                continue;
            }

            List<Message> messages = app.messagify(input_strings);

            List<Message> filtered = new ArrayList<>();
            int start_index = Math.max(messages.size() - app.Config.getNumberOfMessagesToTake(), 0);
            for (int i = start_index; i < messages.size(); i++)
                filtered.add(messages.get(i));

            app.extractJsons(filtered);

//          writeToFile(makeHtmlFromMessagesCollapsible(messages), path_to_output + "_collapsible.html");
//            writeToFile(makeHtmlFromMessages(messages), path_to_output + "_plain.html");
            app.writeToFile(app.makeHtmlFromMessagesTabled(filtered), path_to_output + "_plain_tabled.html");

            LocalDateTime now = LocalDateTime.now();

            System.out.printf("%s, html generated at: " + dtf.format(now) + ", number of entries shown:%d\n", app.PodName, filtered.size());
            Thread.sleep(50);
        } while (app.Config.isKeepScanning());
    }

    private boolean login(Config config) throws Exception {

        List<String> params = new ArrayList<>();

        params.add(config.getPathToOcExe());
        params.add("login");
        params.add("-u");
        params.add(config.getUserName());
        params.add("-p");
        params.add(config.getPassword());
        params.add("-n");
        params.add(config.getServerName());
        params.add(config.getServerUrl());

        String[] par = new String[params.size()];

        List<String> login_output = executeCommand(params.toArray(par));

        for (String line : login_output) {
//            System.out.println(line);
            if (line.contains("Login successful")) return true;
        }
        return false;
    }

    private static String get_pod(Config config) throws Exception {
        // check, whether our pod exists and is running
        List<String> pods = executeCommand(new String[]{config.getPathToOcExe(), "get", "pods"});
        //        Stream<String> filtered = pods.stream().filter(pod -> pod.contains(config.service_name));
        List<String> filtered = new ArrayList<String>();
        for (String pod : pods)
            if (pod.contains(config.getServiceName()))
                filtered.add(pod);

        if (filtered.size() == 0) {
            String output = "";
            for (String pod : pods)
                output += pod + "\n";
            throw new Exception("No pod with given name found, have you specified a correct pod name? All pods:\n" + output);
        }

        boolean is_any_alive = false;
        for (String pod : filtered)
            if (pod.contains("Running"))
                is_any_alive = true;

        if (!is_any_alive) {
            String output = "";
            for (String pod : (String[]) filtered.toArray())
                output += pod + "\n";

            throw new Exception("Chosen pod is broken. List of found pods: " + output);
        }
        // filtered.forEach( string -> output_string += string + "\n");

        // extract correct full name
        for (String pod : filtered) {
            if (pod.contains("Running")) {
                int first_space = pod.indexOf(' ');
                return pod.substring(0, first_space);
            }
        }
//        String clean_pod_name = filtered.filter(pod -> pod.contains("Running"))
//                .findAny()
//                .get();

        String output = "";
        for (String pod : pods)
            output += pod + "\n";

        throw new Exception("Something went horribly wrong:" + output);
    }

    private static Pair<Boolean, String> tryGetPod(Config config, int numberOfEfforts) throws Exception {

        do {
            String podName = null;
            try {
                podName = get_pod(config);
            } catch (Exception e) {
            }

            if (!IsNullOrEmpty(podName))
                return new Pair<Boolean, String>(true, podName);

            Thread.sleep(400);
        } while (numberOfEfforts-- > 0);

        return new Pair<Boolean, String>(false, null);
    }

    private List<String> fetch_log(Config config, String pod_name) throws Exception {
        return executeCommand(new String[]{config.pathToOcExe, "logs", pod_name});
    }

    private static List<String> executeCommand(String[] params) throws Exception {
        List<String> output = new ArrayList<>();
        final Process p = Runtime.getRuntime().exec(params);
        Thread thread = new Thread() {
            public void run() {
                String line;
                BufferedReader input =
                        new BufferedReader
                                (new InputStreamReader(p.getInputStream()));
                try {
                    while ((line = input.readLine()) != null) {
                        output.add(line);
                    }
                    input.close();
                } catch (Exception e) {
                    System.out.println("Something happened" + e);
                }
            }
        };
        thread.start();
        int result = p.waitFor();
        thread.join();

        if (result != 0)
            throw new Exception("Process failed with status: " + result);

        p.destroy();

        return output;
    }

    /**
     * Open and read a file, and return the lines in the file as a list
     * of Strings.
     */
    private List<String> readFromFile(String filename) {
        List<String> records = new ArrayList<String>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine()) != null) {
                records.add(line);
            }
            reader.close();
            return records;
        } catch (Exception e) {
            System.err.format("Exception occurred trying to read '%s'.", filename);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Open and write to a file a collection of strings
     */
    private void writeToFile(List<String> strings, String fileName) {
        try {
            FileWriter writer = new FileWriter(fileName);
            for (String line : strings) {
                writer.write(line);
            }
            writer.close();
        } catch (Exception e) {
            System.err.format("Exception occurred trying to read '%s'.", fileName);
            e.printStackTrace();
        }
    }

    private List<Message> messagify(List<String> rawFile) {

        List<Message> messages = new ArrayList<Message>();
        if (rawFile.size() == 0)
            return messages;

        //"2020-09-07 07:00:39.689""
        String date_regex = new String("^\\d\\d\\d\\d-\\d\\d-\\d\\d .*");

        Message current_message = new Message(rawFile.get(0), 1);
        for (int i = 1; i < rawFile.size(); i++) {
            String line = rawFile.get(i);

            // we have a new message here
            if (line.matches(date_regex)) {
                // message has ended here
                messages.add(current_message);

                // we start a new message
                current_message = new Message(line, i + 1);
            } else {
                current_message.body.add(line);
            }
        }
        messages.add(current_message);

        return messages;
    }

    private void extractJsons(List<Message> messages) {
        for (Message message : messages) {
            String header = message.header;
            if (header.contains("Получена задача")) continue;

            int beginning, ending;
            beginning = header.indexOf('{');
            ending = header.lastIndexOf('}');

            if (beginning == -1 || ending == -1) continue;
            if (beginning >= ending) continue;

            // checks, whether we are dealing with an array
            if (beginning - 1 >= 0 && header.charAt(beginning - 1) == '[' &&
                    ending + 1 < header.length() && header.charAt(ending + 1) == ']') {
                beginning--;
                ending++;
            }

            message.json = header.substring(beginning, ending + 1);
            message.header = header.substring(0, beginning) + header.substring(ending + 1);
        }
    }

    private List<String> makeHtml(List<String> raw) {
        List<String> html = new ArrayList<>();
        html.add("<html>");
        html.add("<body>");

        html.add("<style>");
        html.add(".blockIndicator {");
        html.add("color: #333;");
        html.add("-webkit-font-smoothing: antialiased;");
        html.add("font-family: x-locale-heading-primary, zillaslab, Palatino, \"Palatino Linotype\", x-locale-heading-secondary, serif;");
        html.add("font-size: 1rem;");
//        html.add("background: #fff3d4;");
        html.add("}");
        html.add("</style>");

        for (String line : raw) {
            html.add("<div class=\"blockIndicator\">");
            html.add("<p>" + line + "</p>");
            html.add("</div>");
        }
        html.add("</body>");
        html.add("</html>");
        return html;
    }

    private List<String> makeHtmlFromMessages(List<Message> messages) {
        List<String> html = new ArrayList<>();
        html.add("<html>");
        html.add("<body>");

        html.add("<style>");
        html.add(".blockIndicator {");
        html.add("color: #333;");
        html.add("-webkit-font-smoothing: antialiased;");
        html.add("font-family: x-locale-heading-primary, zillaslab, Palatino, \"Palatino Linotype\", x-locale-heading-secondary, serif;");
        html.add("font-size: 1rem;");
//        html.add("background: #fff3d4;");
        html.add("}");
        html.add("</style>");

        for (Message message : messages) {
            html.add("<div class=\"blockIndicator\">");
            html.add("<p>" + message.header + "</p>");
            if (message.body.size() > 0) {
                html.add("<pre>");
                for (String line : message.body) {
                    html.add(line);
                    html.add("<br>");
                }
                html.add("</pre>");
            }
            html.add("</div>");
        }

        html.add("</body>");
        html.add("</html>");
        return html;
    }

    private List<String> makeHtmlFromMessagesTabled(List<Message> messages) {
        List<String> html = new ArrayList<>();
        html.add("<html>");

        html.add(
                "<head>" +
                        "<meta charset=\"utf-8\" />" +
                        "<link rel=\"stylesheet\" href=\"./resources/style.css\">" +
                        "</head>");

        html.add("<body>");

        html.add("<div id = \"root\" class = \"root\">");
        html.add("</div>");

        // by some reason, loading a style from file does not work as intended,
        // tho I have same style below

//        html.add("<style>");
//        appendContent(html, ".\\resources_for_html_generation\\style.js");
////        html.add("</style>");

        html.add("<style>");
        html.add("table {\n" +
                "table-layout: fixed;" +
                "}");
//        html.add("table, td {\n" +
//                "}");

        html.add("td {");
        html.add("text-align: left;");
        html.add("vertical-align: top;");
        html.add("color: #333;");
        html.add("-webkit-font-smoothing: antialiased;");
        html.add("font-family: x-locale-heading-primary, zillaslab, Palatino, \"Palatino Linotype\", x-locale-heading-secondary, serif;");
        html.add("font-size: 1rem;");
        html.add("word-wrap:break-word;");
//        html.add("white-space:normal;");
        html.add("}");

//        html.add("td div {");
//        html.add("margin: 0px;");
//        html.add("color: #333;");
//        html.add("-webkit-font-smoothing: antialiased;");
//        html.add("font-family: x-locale-heading-primary, zillaslab, Palatino, \"Palatino Linotype\", x-locale-heading-secondary, serif;");
//        html.add("font-size: 1rem;");
//        html.add("word-wrap:break-word;");
////        html.add("white-space:normal;");
//        html.add("}");

        html.add(
                "button { \n" +
                        "-webkit-touch-callout: none;\n" +
                        "-webkit-user-select: none;\n" +
                        "-khtml-user-select: none;\n" +
                        "-moz-user-select: none;\n" +
                        "-ms-user-select: none;\n" +
                        "user-select: none;\n" +
                        "}\n" +
                        "button:before {\n" +
                        "content: attr(data-unselectable);\n" +
                        "}");

        html.add("</style>");

        html.add("<table width='100%'>");
        for (Message message : messages) {
            html.add("<tr>");
            if (Config.isShowLineNumbers())
                html.add("<td text-align: left; width='35';>" + message.index + "</td>");
            html.add("<td>" + message.header);
            if (message.containsJson()) {
                html.add("<br>");
                html.add("<button id=\"" + message.index + "\" onClick=\"prettify()\">prettify</button>");
                html.add("<div id=\"json " + message.index + "\">");
                html.add(message.json);
                html.add("</div>");
            }
            if (message.body.size() > 0) {
//                html.add("<pre>");
                for (String line : message.body) {
                    html.add(line);
                    html.add("<br>");
                }
//                html.add("</pre>");
            }
            html.add("</td");
            html.add("</tr>");
            html.add("<tr height=\"15px\";><td></td></tr>");//adding an empty line of text
        }
        html.add("</table>");

        html.add("<script type=\"text/javascript\" src=\"./resources/collapsible.js\"></script>");
        html.add("<script type=\"text/javascript\" src=\"./resources/convert.js\"></script>");
        html.add("<script type=\"text/javascript\" src=\"./resources/prettify_handler.js\"></script>");
        html.add("<script type=\"text/javascript\" src=\"./resources/update_file_content.js\"></script>");

        html.add("</body>");
        html.add("</html>");
        return html;
    }

    private List<String> makeHtmlFromMessagesCollapsible(List<Message> messages) {
        List<String> html = new ArrayList<>();
        html.add("<html>");

        appendContent(html, "C:\\Users\\alexander.ruchkov\\IdeaProjects\\beautyfier\\resources_for_html_generation\\header.txt");

        html.add("<body>");

        html.add("<style>");
        html.add(".blockIndicator {");
        html.add("color: #333;");
        html.add("-webkit-font-smoothing: antialiased;");
        html.add("font-family: x-locale-heading-primary, zillaslab, Palatino, \"Palatino Linotype\", x-locale-heading-secondary, serif;");
        html.add("font-size: 1rem;");
        html.add("}");
        html.add("</style>");

        for (Message message : messages) {
            if (message.body.size() == 0) {
                html.add("<div class=\"blockIndicator\">");
                html.add("<p>" + message.header + "</p>");
                html.add("</div>");
            } else {
                html.add("<button class=\"collapsible\">" + message.header + "</button>");
                html.add("<pre class=\"content\">");
                for (String line : message.body) {
                    html.add(line);
                    html.add("<br>");
                }
                html.add("</pre>");
                html.add("</div>");
            }
        }

        appendContent(html, "C:\\Users\\alexander.ruchkov\\IdeaProjects\\beautyfier\\resources_for_html_generation\\script.txt");

        html.add("</body>");
        html.add("</html>");
        return html;
    }

    private List<String> appendContent(List<String> document, String path_to_content) {

        List<String> content = readFromFile(path_to_content);
        for (String line : content) {
            document.add(line);
        }

        return document;
    }

    public static boolean IsNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }
}

