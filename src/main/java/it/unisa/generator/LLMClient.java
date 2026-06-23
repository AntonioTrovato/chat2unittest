package it.unisa.generator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.http.*;
import java.net.URI;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpRequest.BodyPublishers;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LLMClient {
    // Support for api keys
    private static String apiKey = System.getenv("OPENAI_API_KEY");

    private static String endpoint = "http://localhost:1234/v1/chat/completions";
    private static String model = "codellama-13b-instruct";
    private static double temperature = 0.4;

    public static void configure(String hostUrl, String mdl, double tmp) {
        endpoint = hostUrl;
        model = mdl;
        temperature = tmp;
    }

    public static String generate(String prompt) throws Exception {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", model);
        body.put("temperature", temperature);

        List<Map<String, String>> messages = List.of(Map.of(
                "role", "user",
                "content", prompt
        ));
        body.put("messages", messages);

        ObjectMapper mapper = new ObjectMapper();
        String jsonPayload = mapper.writeValueAsString(body);

        /*System.out.println("------ HTTP REQUEST DEBUG ------");
        System.out.println("POST " + endpoint);
        System.out.println("Headers: Content-Type=application/json");
        System.out.println("Payload:\n" + jsonPayload);
        System.out.println("------ END HTTP REQUEST --------");*/

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json");

        if (endpoint.contains("openai.com")) {
            if (apiKey == null || apiKey.isBlank()) {
                throw new RuntimeException("OPENAI_API_KEY environment variable not set");
            }
            builder.header("Authorization", "Bearer " + apiKey);
        }

        HttpRequest req = builder
                .POST(BodyPublishers.ofString(jsonPayload))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> res = client.send(req, BodyHandlers.ofString());
        return extractCompletion(res.body());
    }

    private static String extractCompletion(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);
            String raw = root.path("choices").get(0).path("message").path("content").asText();
            return cleanCode(raw);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse LLM response: " + json, e);
        }
    }

    private static String cleanCode(String raw) {
        if (raw == null) return "";

        // Se c'è un blocco ```java, prendiamo SOLO quello
        int start = raw.indexOf("```");
        if (start != -1) {
            int end = raw.lastIndexOf("```");
            if (end > start) {
                raw = raw.substring(start + 3, end);
                raw = raw.replaceFirst("^java\\s*", "");
            }
        }

        // Fallback: prendiamo da 'package' in poi
        int pkg = raw.indexOf("package ");
        if (pkg != -1) {
            raw = raw.substring(pkg);
        }

        return raw.trim();
    }

}
