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

        System.out.println("------ HTTP REQUEST DEBUG ------");
        System.out.println("POST " + endpoint);
        System.out.println("Headers: Content-Type=application/json");
        System.out.println("Payload:\n" + jsonPayload);
        System.out.println("------ END HTTP REQUEST --------");

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
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

        String cleaned = raw.trim();

        // Rimuove blocchi markdown ```java ... ``` o simili
        if (cleaned.startsWith("```")) {
            cleaned = cleaned.replaceFirst("^```[a-zA-Z]*\\s*", ""); // inizio blocco
            cleaned = cleaned.replaceFirst("\\s*```\\s*$", "");       // fine blocco
        }

        // Rimuove eventuali backtick singoli "`"
        if (cleaned.startsWith("`") && cleaned.endsWith("`")) {
            cleaned = cleaned.substring(1, cleaned.length() - 1);
        }

        return cleaned.trim();
    }

}
