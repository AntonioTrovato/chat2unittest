package it.unisa.generator;

import java.net.http.*;
import java.net.URI;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpRequest.BodyPublishers;

public class LLMClient {
    private static String endpoint = "http://localhost:1234/v1/chat/completions"; // default fallback

    public static void setEndpoint(String baseUrl) {
        endpoint = baseUrl.endsWith("/") ? baseUrl + "v1/chat/completions" : baseUrl + "/v1/chat/completions";
    }

    public static String generate(String prompt) throws Exception {
        String payload = """
        {
          "model": "codellama-13b-instruct",
          "messages": [
            {"role": "user", "content": "%s"}
          ],
          "temperature": 0.4
        }
        """.formatted(prompt.replace("\"", "\\\""));

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(payload))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> res = client.send(req, BodyHandlers.ofString());
        return extractCompletion(res.body());
    }

    private static String extractCompletion(String json) {
        int start = json.indexOf("\"content\":\"") + 10;
        int end = json.indexOf("\"", start);
        if (start == -1 || end == -1) {
            throw new RuntimeException("Failed to parse LLM response: " + json);
        }
        return json.substring(start, end).replace("\\n", "\n").replace("\\\"", "\"");
    }
}
