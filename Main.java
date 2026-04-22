import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Main {

    public static void main(String[] args) {

        // Validate CLI input: GitHub username must be provided
        if (args.length == 0) {
            System.out.println("Usage: github-activity <username>");
            return;
        }

        String username = args[0];

        try {
            // Fetch recent public events for the given user
            String json = fetchEvents(username);

            // Handle API failure or invalid username
            if (json == null) {
                System.out.println("User not found or API error.");
                return;
            }

            // Parse API response and display formatted output
            parseAndDisplay(json);

        } catch (Exception e) {
            // Catch-all for unexpected runtime issues
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Calls GitHub Events API and returns raw JSON response.
     *
     * @param username GitHub username
     * @return JSON string if successful, null otherwise
     */
    public static String fetchEvents(String username) throws Exception {

        String urlString = "https://api.github.com/users/" + username + "/events";
        URL url = new URL(urlString);

        // Establish HTTP connection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        int status = conn.getResponseCode();

        // Return null for non-success responses
        if (status != 200) {
            return null;
        }

        // Read response stream into a single JSON string
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream())
        );

        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();

        return response.toString();
    }

    /**
     * Parses raw JSON response and prints user-friendly activity logs.
     * Note: Uses manual string parsing (no external JSON libraries).
     *
     * @param json Raw JSON string from GitHub API
     */
    public static void parseAndDisplay(String json) {

        // Handle empty activity response
        if (json.equals("[]")) {
            System.out.println("No recent activity found for this user.");
            return;
        }

        // Split JSON into individual event blocks (simplified parsing approach)
        String[] events = json.split("\\},\\{");

        for (String event : events) {

            // Extract and format event timestamp
            String rawDate = extract(event, "\"created_at\":\"", "\"");
            String formattedDate = formatDate(rawDate);

            // Handle Push events (code commits)
            if (event.contains("\"type\":\"PushEvent\"")) {
                String repo = extract(event, "\"name\":\"", "\"");
                String commits = extract(event, "\"size\":", ",");

                System.out.println("- [" + formattedDate + "] Pushed " + commits + " commits to " + repo);
            }

            // Handle Issue creation events
            else if (event.contains("\"type\":\"IssuesEvent\"")) {
                String repo = extract(event, "\"name\":\"", "\"");

                System.out.println("- [" + formattedDate + "] Opened an issue in " + repo);
            }

            // Handle repository star events
            else if (event.contains("\"type\":\"WatchEvent\"")) {
                String repo = extract(event, "\"name\":\"", "\"");

                System.out.println("- [" + formattedDate + "] Starred " + repo);
            }

            // Fallback for unhandled event types
            else {
                System.out.println("- [" + formattedDate + "] Other activity detected");
            }
        }
    }

    /**
     * Extracts a substring between two markers.
     * Used for lightweight JSON parsing.
     *
     * @param text  Source string
     * @param start Start delimiter
     * @param end   End delimiter
     * @return Extracted value or "unknown" if parsing fails
     */
    private static String extract(String text, String start, String end) {
        try {
            int i = text.indexOf(start) + start.length();
            int j = text.indexOf(end, i);
            return text.substring(i, j);
        } catch (Exception e) {
            return "unknown";
        }
    }

    /**
     * Converts ISO-8601 timestamp (UTC) to human-readable format in IST.
     *
     * @param isoDate Raw timestamp from API
     * @return Formatted date string
     */
    private static String formatDate(String isoDate) {
        try {
            ZonedDateTime zdt = ZonedDateTime.parse(isoDate);

            // Convert UTC → Asia/Kolkata timezone
            ZonedDateTime istTime = zdt.withZoneSameInstant(ZoneId.of("Asia/Kolkata"));

            return istTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));

        } catch (Exception e) {
            return isoDate;
        }
    }
}