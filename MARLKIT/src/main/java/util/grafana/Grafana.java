package util.grafana;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import util.Pair;


public class Grafana {
    private final String url = "http://localhost:3000";
    private final String token;
    private final Map<String, String> HEAD;
    private final String CSV_PATH;
    private final String CSV_URL;
    private String PNG_OUT = null;

    /**
     * Constructor for the Grafana class with the CSV file path.
     * Grafana is a class that interacts with a Grafana instance though their API to create dashboards and render PNG images from CSV data.
     * If no PNG_OUT is provided, the PNG will not be saved.
     * @param CSV_PATH The path to the CSV file to use as a data source
     * @throws IOException If an input/output error occurs
     * @throws URISyntaxException If the URL is malformed
     */
    public Grafana(String CSV_PATH) throws IOException, URISyntaxException {
        this.token = "Bearer " + getOrCreateToken();
        this.HEAD = createHead();
        this.CSV_PATH = CSV_PATH;
        this.CSV_URL = Paths.get(CSV_PATH).toAbsolutePath().toString().replace("\\", "/");
    }

    /**
     * Constructor for the Grafana class with the CSV file path and the output path for the generated PNG image.
     * Grafana is a class that interacts with a Grafana instance though their API to create dashboards and render PNG images from CSV data.
     * If no PNG_OUT is provided, the PNG will not be saved.
     * @param CSV_PATH The path to the CSV file to use as a data source
     * @param PNG_OUT The output path for the generated PNG image
     * @throws IOException If an input/output error occurs
     * @throws URISyntaxException If the URL is malformed
     */
    public Grafana(String CSV_PATH, String PNG_OUT) throws IOException, URISyntaxException {
        this(CSV_PATH);
        this.PNG_OUT = PNG_OUT;
    }

    /**
     * Reads the headers of a CSV file
     * @param csvPath The path to the CSV file
     * @return A list of strings representing the CSV headers
     * @throws IOException If an input/output error occurs
     */
    public List<String> getHeaders(String csvPath) throws IOException {
        List<String> headers = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(csvPath))) {
            String line = reader.readLine();
            if (line != null) {
                headers = Arrays.asList(line.split(","));
            }
        }
        return headers;
    }

    public String getUrl() {
        return url;
    }

    /**
     * Retrieves the UID of a data source by its name from grafana.
     * @param name The name of the data source to retrieve
     * @return The UID of the data source
     * @throws IOException In case of an I/O error during the request
     * @throws URISyntaxException If the URL is not well-formed
     */
    private String getDatasource(String name) throws IOException, URISyntaxException {
        String endpoint = url + "/api/datasources/name/" + name;
        HttpURLConnection conn = (HttpURLConnection) new URI(endpoint).toURL().openConnection();
        for (Map.Entry<String, String> entry : HEAD.entrySet()) {
            conn.setRequestProperty(entry.getKey(), entry.getValue());
        }
        conn.setRequestMethod("GET");
        raiseStatus(conn.getResponseCode());
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map responseMap = objectMapper.readValue(conn.getInputStream(), Map.class);
            String uid = (String) responseMap.get("uid");
            if (uid == null) {
                throw new IOException("UID non trouvé dans la réponse");
            }
            return uid;
        } catch (IOException e) {
            throw new IOException("Erreur lors de la lecture de la réponse JSON", e);
        } finally {
            conn.disconnect();
        }
    }


    /**
     * Create a new data source in Grafana or return the UID of an existing one.
     * @param name The name of the data source
     * @return The UID of the data source
     * @throws IOException In case of an I/O error during the request
     * @throws URISyntaxException If the URL is not well-formed
     */
    public String getOrCreateDatasource(String name) throws IOException, URISyntaxException {
        String endpoint = url + "/api/datasources";
        HttpURLConnection conn = (HttpURLConnection) new URI(endpoint).toURL().openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        for (Map.Entry<String, String> entry : HEAD.entrySet()) {
            conn.setRequestProperty(entry.getKey(), entry.getValue());
        }
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("name", name);
        jsonMap.put("type", "marcusolsson-csv-datasource");
        jsonMap.put("access", "proxy");
        jsonMap.put("url", CSV_URL);
        jsonMap.put("basicAuth", false);

        Map<String, Object> jsonData = new HashMap<>();
        jsonData.put("delimiter", ",");
        jsonData.put("header", true);
        jsonData.put("storage", "local");
        jsonMap.put("jsonData", jsonData);
        String jsonInputString = new ObjectMapper().writeValueAsString(jsonMap);
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        int code = conn.getResponseCode();
        if (code == 409) {
            return getDatasource(name);
        }
        raiseStatus(code);
        String uid;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map responseMap = objectMapper.readValue(conn.getInputStream(), Map.class);
            uid = (String) ((Map<String, Object>) responseMap.get("datasource")).get("uid");
        } catch (IOException e) {
            throw new IOException("Erreur lors de la lecture de la réponse JSON", e);
        } finally {
            conn.disconnect();
        }
        if (uid == null) {
            throw new IOException("UID non trouvé dans la réponse");
        }
        return uid;
    }

    /**
     * Creates a new dashboard in Grafana with the specified data source.
     * @param ds_uid The UID of the data source to use for the dashboard
     * @return A pair containing the dashboard UID and slug
     */
    public Pair<String, String> createDashboard(String ds_uid) throws IOException, URISyntaxException {
        List<String> headers = getHeaders(CSV_PATH);
        if (headers.isEmpty()) {
            throw new IllegalArgumentException("Invalid CSV file: no headers found (might be wrong delimiter)");
        }

        ArrayList<Map<String, String>> columns = new ArrayList<>();
        for (String header : headers) {
            Map<String, String> column = new HashMap<>();
            column.put("selector", header);
            column.put("type", "number");
            columns.add(column);
        }
        int w = 12, h = 9;
        List<Map<String, Object>> panels = new ArrayList<>();

        for (int idx = 1; idx < headers.size(); idx++) {
            String col = headers.get(idx);

            List<Map<String, Object>> transformations = new ArrayList<>();

            Map<String, Object> convertFieldType = new HashMap<>();
            convertFieldType.put("id", "convertFieldType");

            Map<String, Object> options = new HashMap<>();
            List<Map<String, String>> conversions = new ArrayList<>();

            for (String field : headers) {
                Map<String, String> conversion = new HashMap<>();
                conversion.put("targetField", field);
                conversion.put("destinationType", "number");
                conversions.add(conversion);
            }

            options.put("conversions", conversions);
            options.put("fields", new HashMap<>());
            convertFieldType.put("options", options);

            transformations.add(convertFieldType);

            for (String field : headers) {
                if (field.equals("Episode")) continue;

                Map<String, Object> calculateField = new HashMap<>();
                calculateField.put("id", "calculateField");

                Map<String, Object> calcOptions = new HashMap<>();
                calcOptions.put("mode", "windowFunctions");

                Map<String, Object> reduce = new HashMap<>();
                reduce.put("include", new ArrayList<>(headers));
                reduce.put("reducer", "mean");
                calcOptions.put("reduce", reduce);

                Map<String, Object> binary = new HashMap<>();
                Map<String, String> left = new HashMap<>();
                left.put("fixed", "");
                Map<String, String> right = new HashMap<>();
                right.put("fixed", "");
                binary.put("left", left);
                binary.put("right", right);
                calcOptions.put("binary", binary);

                Map<String, Object> cumulative = new HashMap<>();
                cumulative.put("field", field);
                cumulative.put("reducer", "mean");
                calcOptions.put("cumulative", cumulative);

                calcOptions.put("replaceFields", false);

                Map<String, Object> window = new HashMap<>();
                window.put("reducer", "mean");
                window.put("windowAlignment", "centered");
                window.put("windowSizeMode", "percentage");
                window.put("windowSize", 0.1);
                window.put("field", field);
                calcOptions.put("window", window);

                calculateField.put("options", calcOptions);
                transformations.add(calculateField);

            }

            Map<String, Object> panel = new HashMap<>();
            panel.put("type", "trend");
            panel.put("title", col);

            Map<String, Object> gridPos = new HashMap<>();
            gridPos.put("x", (idx - 1) % 2 * w);
            gridPos.put("y", ((idx - 1) / 2) * h);
            gridPos.put("w", w);
            gridPos.put("h", h);
            panel.put("gridPos", gridPos);

            List<Map<String, Object>> targets = new ArrayList<>();
            Map<String, Object> target = new HashMap<>();
            target.put("refId", "A");

            Map<String, Object> datasource = new HashMap<>();
            datasource.put("type", "marcusolsson-csv-datasource");
            datasource.put("uid", ds_uid);
            target.put("datasource", datasource);

            target.put("format", "table");

            Map<String, Object> csv = new HashMap<>();
            csv.put("file", CSV_URL);
            csv.put("columns", columns);
            target.put("csv", csv);

            target.put("delimiter", ",");
            target.put("decimalSeparator", ".");
            target.put("header", true);
            target.put("ignoreUnknown", false);
            target.put("skipRows", 0);

            List<Map<String, String>> schema = new ArrayList<>();
            Map<String, String> schemaItem = new HashMap<>();
            schemaItem.put("name", "");
            schemaItem.put("type", "string");
            schema.add(schemaItem);
            target.put("schema", schema);

            targets.add(target);
            panel.put("targets", targets);

            panel.put("datasource", datasource);

            panel.put("transformations", transformations);

            Map<String, Object> fieldConfig = new HashMap<>();
            Map<String, Object> defaults = new HashMap<>();
            Map<String, Object> custom = new HashMap<>();
            custom.put("drawStyle", "line");
            custom.put("lineWidth", 1);
            custom.put("fillOpacity", 0);
            custom.put("showPoints", "never");
            custom.put("showTrendLine", "linear");
            defaults.put("custom", custom);
            fieldConfig.put("defaults", defaults);
            panel.put("fieldConfig", fieldConfig);

            panels.add(panel);
        }

        Map<String, Object> dashBody = new HashMap<>();
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("title", "Épisodes dynamiques");
        dashboard.put("schemaVersion", 41);

        List<Map<String, Object>> firstPanel = new ArrayList<>();
        if (!panels.isEmpty()) {
            firstPanel.add(panels.getFirst());
        }
        dashboard.put("panels", firstPanel);

        dashBody.put("dashboard", dashboard);
        dashBody.put("overwrite", false);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonDashboard = objectMapper.writeValueAsString(dashBody);

        URL urlObj = new URI(url + "/api/dashboards/db").toURL();
        HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        for (Map.Entry<String, String> entry : HEAD.entrySet()) {
            conn.setRequestProperty(entry.getKey(), entry.getValue());
        }

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonDashboard.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        raiseStatus(conn.getResponseCode());

        Map responseMap = objectMapper.readValue(conn.getInputStream(), Map.class);
        String uid = (String) responseMap.get("uid");
        String slug = (String) responseMap.get("slug");

        if (uid == null || slug == null) {
            throw new IOException("UID ou slug non trouvé dans la réponse");
        }

        return new Pair<>(uid, slug);

    }

    /**
     * Generates and saves a PNG image of a Grafana dashboard panel.
     *
     * @param uid      The unique identifier of the dashboard
     * @param slug     The slug of the dashboard
     * @param panelId  The panel identifier (default: 1)
     * @param outPath  The output file path (default: PNG_OUT)
     * @throws IOException If an input/output error occurs
     * @throws URISyntaxException If the URL is malformed
    */
    public void renderPNG(String uid, String slug, int panelId, String outPath) throws IOException, URISyntaxException {
        String outputPath = (outPath != null) ? outPath : PNG_OUT;
        if (outputPath == null) {
            throw new IllegalArgumentException("Le chemin de sortie ne peut pas être null");
        }
        String renderUrl = url + "/render/d-solo/" + uid + "/" + slug
                + "?orgId=1&panelId=" + panelId + "&width=1000&height=500"
                + "&theme=light&from=now-6h&to=now";

        URL urlObj = new URI(renderUrl).toURL();
        HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
        conn.setRequestMethod("GET");

        for (Map.Entry<String, String> entry : HEAD.entrySet()) {
            conn.setRequestProperty(entry.getKey(), entry.getValue());
        }

        raiseStatus(conn.getResponseCode());

        try (InputStream in = conn.getInputStream();
            FileOutputStream out = new FileOutputStream(outputPath)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        } finally {
            conn.disconnect();
        }
    }

    /**
     * Generates and saves a PNG image with the default value for outPath.
     *
     * @param uid      The unique identifier of the dashboard
     * @param slug     The slug of the dashboard
     * @throws IOException If an input/output error occurs
     * @throws URISyntaxException If the URL is malformed
     */
    public void renderPNG(String uid, String slug) throws IOException, URISyntaxException {
        renderPNG(uid, slug, 1, null);
    }

    /**
     * Generates and saves a PNG image with the default value for outPath.
     *
     * @param uid      The unique identifier of the dashboard
     * @param slug     The slug of the dashboard
     * @param panelId  The panel identifier
     * @throws IOException If an input/output error occurs
     * @throws URISyntaxException If the URL is malformed
    */
    public void renderPNG(String uid, String slug, int panelId) throws IOException, URISyntaxException {
        renderPNG(uid, slug, panelId, null);
    }

    /**
     * Retrieves or creates a Grafana service account token.
     * If the token file does not exist, it generates a new token and saves it to the file.
     * If the token file exists, it reads the token from the file.
     *
     * @return The Grafana service account token
     * @throws IOException If an input/output error occurs
     * @throws URISyntaxException If the URL is malformed
     */
    public String getOrCreateToken() throws IOException, URISyntaxException {
        Path tokenPath = Paths.get("grafana_token.txt");
        String token;
        if (!Files.exists(tokenPath)) {
            token = generateServiceAccountToken();
            Files.write(tokenPath, token.getBytes());
        } else {
            token = new String(Files.readAllBytes(tokenPath)).trim();
        }
        return token;
    }

    /**
     * Creates the headers for the HTTP requests to Grafana.
     * The headers include the authorization token and content type.
     *
     * @return A map containing the headers
     */
    private Map<String, String> createHead() {
        Map<String, String> head = new HashMap<>();
        head.put("Authorization", token);
        head.put("Content-Type", "application/json");
        return head;
    }

    /**
     * Generates a service account token by creating a service account and then creating a token for that account.
     *
     * @return The generated service account token
     * @throws IOException If an input/output error occurs
     * @throws URISyntaxException If the URL is malformed
     */
    private String generateServiceAccountToken() throws IOException, URISyntaxException {
        String saUid = null;
        saUid = createServiceAccount();
        return createServiceAccountToken(saUid);
    }

    /**
     * Creates the authorization header for HTTP requests to Grafana.
     * The header is created using basic authentication with a predefined username and password.
     *
     * @return A map containing the authorization header
     */
    private Map<String, String> getAuthHeader() {
        String user = "admin";
        String password = "admin";
        String auth = user + ":" + password;
        String encodedAuth = java.util.Base64.getEncoder().encodeToString(auth.getBytes());
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic " + encodedAuth);
        headers.put("Content-Type", "application/json");
        return headers;
    }

    /**
     * Creates a service account in Grafana.
     * The service account is created with the name "script" and the role "Admin".
     *
     * @return The UID of the created service account
     * @throws IOException If an input/output error occurs
     * @throws URISyntaxException If the URL is malformed
     */
    private String createServiceAccount() throws IOException, URISyntaxException {

        ObjectMapper urlConnMapper = new ObjectMapper();
        Map<String, String> saData = new HashMap<>();
        saData.put("name", "script");
        saData.put("role", "Admin");
        String jsonInputString = urlConnMapper.writeValueAsString(saData);

        HttpURLConnection conn = getHttpURLConnection(jsonInputString, url + "/api/serviceaccounts");

        raiseStatus(conn.getResponseCode());


        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map responseMap = objectMapper.readValue(conn.getInputStream(), Map.class);
            Map dataSource = (Map) responseMap.get("serviceAccount");
            String uid = (String) dataSource.get("uid");
            if (uid == null) {
                throw new IOException("UID non trouvé dans la réponse");
            }
            return uid;
        } catch (IOException e) {
            throw new IOException("Erreur lors de la lecture de la réponse JSON", e);
        } finally {
            conn.disconnect();
        }
    }

    /**
     * Creates a service account token for the specified service account UID.
     * The token is created with a lifetime of 1 year (31536000 seconds).
     *
     * @param saUid The UID of the service account
     * @return The generated service account token
     * @throws IOException If an input/output error occurs
     * @throws URISyntaxException If the URL is malformed
     */
    private String createServiceAccountToken(String saUid) throws IOException, URISyntaxException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> tokenRequest = new HashMap<>();
        tokenRequest.put("name", "script-token");
        tokenRequest.put("secondsToLive", 31536000);
        String jsonInputString = objectMapper.writeValueAsString(tokenRequest);
        HttpURLConnection conn = getHttpURLConnection(
                jsonInputString, url + "/api/serviceaccounts/" + saUid + "/tokens");

        raiseStatus(conn.getResponseCode());


        ObjectMapper responseMapper = new ObjectMapper();
        try {
            Map responseMap = responseMapper.readValue(conn.getInputStream(), Map.class);
            if (responseMap == null || responseMap.isEmpty()) {
                throw new IOException("La réponse est vide ou invalide");
            }
            String token = (String) responseMap.get("key");
            if (token == null) {
                throw new IOException("Token non trouvé dans la réponse");
            }
            return token;
        } catch (IOException e) {
            throw new IOException("Erreur lors de la lecture de la réponse JSON", e);
        } finally {
            conn.disconnect();
        }
    }

    /**
     * Creates an HTTP connection with the specified JSON input string and URL.
     * The connection is configured to send a POST request with the appropriate headers.
     *
     * @param jsonInputString The JSON input string to send in the request body
     * @param url The URL to connect to
     * @return An HttpURLConnection object representing the connection
     * @throws IOException If an input/output error occurs
     * @throws URISyntaxException If the URL is malformed
     */
    private HttpURLConnection getHttpURLConnection(String jsonInputString, String url) throws IOException, URISyntaxException {
        Map<String, String> headers = getAuthHeader();
        URL urlObj = new URI(url).toURL();
        HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            conn.setRequestProperty(entry.getKey(), entry.getValue());
        }

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        return conn;
    }

    /**
     * Raises an IOException if the HTTP response code is not 200 (OK) or 201 (Created).
     *
     * @param code The HTTP response code
     * @throws IOException If the response code indicates an error
     */
    private void raiseStatus(int code) throws IOException {
        if (code != 200 && code != 201) {
            throw new IOException("Erreur HTTP: " + code);
        }
    }

    /**
     * Sort of unit test
     * TODO en faire de vrais unit test
     * @param args args
     */
    public static void main(String[] args) {
        try {
            Grafana grafana = new Grafana("logs/log_1750967264.csv", "log_1750967264");
            String dsUid = grafana.getOrCreateDatasource("MyDataSource");
            Pair<String, String> dashboard = grafana.createDashboard(dsUid);
            grafana.renderPNG(dashboard.getFirst(), dashboard.getSecond());
            System.out.println("Dashboard created and PNG rendered successfully.");
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
