package it.unisa.generator;

import java.net.http.*;
import java.net.URI;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpRequest.BodyPublishers;

public class LLMClient {
    private static String endpoint = "http://localhost:1234/v1/chat/completions";
    private static String model = "codellama-13b-instruct";
    private static double temperature = 0.4;

    public static void configure(String hostUrl, String mdl, double tmp) {
        endpoint = hostUrl.endsWith("/") ? hostUrl + "v1/chat/completions" : hostUrl + "/v1/chat/completions";
        model = mdl;
        temperature = tmp;
    }

    public static String generate(String prompt) throws Exception {
        String payload = """
        {
          "model": "%s",
          "messages": [
            {"role": "user", "content": "%s"}
          ],
          "temperature": %.2f
        }
        """.formatted(model, prompt.replace("\"", "\\\""), temperature);

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
