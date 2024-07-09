import Classes.RequestStatus;
import Classes.RestPath;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RestWrapper {
    private final HttpClient httpClient;

    public RestWrapper() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public RequestStatus sendPost(RestPath path, Object payload) {
        try {
            String jsonInputString = payload.toString(); // Assumes the payload has a proper toString implementation.

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080" + path.getPath()))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonInputString))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return new RequestStatus(true, response.body());
            } else {
                return new RequestStatus(false, "POST request failed with code: " + response.statusCode());
            }
        } catch (Exception e) {
            return new RequestStatus(false, "POST request failed with exception: " + e.getMessage());
        }
    }

    public RequestStatus sendGet(RestPath path) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080" + path.getPath()))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return new RequestStatus(true, response.body());
            } else {
                return new RequestStatus(false, "GET request failed with code: " + response.statusCode());
            }
        } catch (Exception e) {
            return new RequestStatus(false, "GET request failed with exception: " + e.getMessage());
        }
    }
}
