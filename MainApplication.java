import model.IndexInfo;
import java.util.List;
import java.util.ArrayList;
import java.util.*;
import java.io.*;
import java.net.*;
import java.time.LocalDate;


public class MainApplication {

    /**
     * Main method to start the application. Allows the user to select input mode (file or API),
     * retrieves index data accordingly, and outputs analytical summaries.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<IndexInfo> data = new ArrayList<>();
        try {
            System.out.println("Enter mode (file/api):");
            String mode = scanner.nextLine();

            if (mode.equalsIgnoreCase("file")) {
                System.out.println("Enter filename (e.g., example-in.json): ");
                String fileName = scanner.nextLine().trim();
                data = getDataFromFile(fileName);

            } else if (mode.equalsIgnoreCase("api")) {
                System.out.println("Enter endpoint: ");
                String endpoint = scanner.nextLine();

                LocalDate today = LocalDate.now();
                LocalDate yesterday = today.minusDays(1);

                String year = String.valueOf(yesterday.getYear());
                String month = String.format("%02d", yesterday.getMonthValue());
                String day = String.format("%02d", yesterday.getDayOfMonth());

                String apiUrl = buildApiUrl(endpoint, year, month, day);
                data = getDataFromServer(apiUrl);
                
            } else {
                System.out.println("Invalid input mode.");
                scanner.close();
                return;
            }

            printLargestIndexes(data);
            printMostShards(data);
            printLeastBalanced(data);

        } catch (Exception e) {
            System.err.println("Error reading data: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        scanner.close();
    }

    /**
     * Reads index information from a given JSON file.
     *
     * @param path path to the input JSON file
     * @return list of parsed IndexInfo objects
     * @throws IOException if file reading fails
     */
    public static List<IndexInfo> getDataFromFile(String path) throws IOException {
        Reader reader = new FileReader(path);
        return parseJson(reader);
    }

    /**
     * Retrieves index information from a specified API endpoint.
     *
     * @param urlInput fully constructed API URL
     * @return list of parsed IndexInfo objects
     * @throws IOException if the HTTP request or stream handling fails
     */
    public static List<IndexInfo> getDataFromServer(String urlInput) throws IOException {
        URI uri = URI.create(urlInput);
        URL url = uri.toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        return parseJson(new InputStreamReader(conn.getInputStream()));
    }

    /**
     * Parses a JSON response from a character stream into a list of IndexInfo.
     *
     * @param reader character stream from file or API
     * @return list of parsed IndexInfo objects
     * @throws IOException if reading the stream fails
     */
    private static List<IndexInfo> parseJson(Reader reader) throws IOException {
        BufferedReader br = new BufferedReader(reader);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        return parseJson(sb.toString());
    }

    /**
     * Parses a raw JSON string into a list of IndexInfo objects.
     * Custom parsing logic designed for simple JSON arrays without dependencies.
     *
     * @param json JSON-formatted string
     * @return list of IndexInfo instances
     */
    private static List<IndexInfo> parseJson(String json) {
        List<IndexInfo> list = new ArrayList<>();
        json = json.trim().substring(1, json.length() - 1);
        String[] objects = json.split("},\\s*\\{");

        for (String obj : objects) {
            obj = obj.replaceAll("[\\{\\}]", "");
            String[] pairs = obj.split(",");

            String index = "";
            long size = 0;
            int shards = 0;

            for (String pair : pairs) {
                String[] kv = pair.split(":");
                if (kv.length != 2) continue;

                String key = kv[0].trim().replace("\"", "");
                String value = kv[1].trim().replace("\"", "");

                switch (key) {
                    case "index":
                        index = value;
                        break;
                    case "pri.store.size":
                        size = Long.parseLong(value);
                        break;
                    case "pri":
                        shards = Integer.parseInt(value);
                        break;
                }
            }

            list.add(new IndexInfo(index, size, shards));
        }
        return list;
    }

    /**
     * Prints the top 5 indexes with the largest storage size.
     *
     * @param list list of IndexInfo objects to evaluate
     */
    public static void printLargestIndexes(List<IndexInfo> list) {
        System.out.println("\nPrinting largest indexes by storage size");
        list.stream()
            .sorted((current, next) -> Long.compare(next.getSizeBytes(), current.getSizeBytes()))
            .limit(5)
            .forEach(index -> System.out.printf("Index: %s\nSize: %.2f GB\n", index.getName(), index.getSizeInGB()));
    }

    /**
     * Prints the top 5 indexes with the highest number of shards.
     *
     * @param list list of IndexInfo objects to evaluate
     */
    public static void printMostShards(List<IndexInfo> list) {
        System.out.println("\nPrinting largest indexes by shard count");
        list.stream()
            .sorted((current, next) -> Integer.compare(next.getShards(), current.getShards()))
            .limit(5)
            .forEach(index -> System.out.printf("Index: %s\nShards: %d\n", index.getName(), index.getShards()));
    }

    /**
     * Prints the top 5 least balanced indexes based on storage-to-shard ratio.
     * Also prints the recommended shard count for each index
     *
     * @param list list of IndexInfo objects to evaluate
     */
    public static void printLeastBalanced(List<IndexInfo> list) {
        System.out.println("\nPrinting least balanced indexes");
        list.stream()
            .sorted((current, next) -> Double.compare(next.getBalanceRatio(), current.getBalanceRatio()))
            .limit(5)
            .forEach(index -> {
                System.out.printf("Index: %s\n", index.getName());
                System.out.printf("Size: %.2f GB\n", index.getSizeInGB());
                System.out.printf("Shards: %d\n", index.getShards());
                System.out.printf("Balance Ratio: %d\n", (int) index.getBalanceRatio());
                System.out.printf("Recommended shard count is %d\n", index.getRecommendedShardCount());
            });
    }

    /**
     * Constructs the API URL dynamically based on the specified date and endpoint.
     *
     * @param endpoint API server endpoint
     * @param year     year value as string
     * @param month    month value as string (2-digit format)
     * @param day      day value as string (2-digit format)
     * @return a fully formatted API URL string
     */
    public static String buildApiUrl(String endpoint, String year, String month, String day) {
        return String.format("https://%s/_cat/indices/*%s*%s*%s?v&h=index,pri.store.size,pri&format=json&bytes=b",
                endpoint, year, month, day);
    }
}
