package it.unisa.generator;

import java.util.List;

public class PromptBuilder {
    public static String buildJUnitPrompt(String filePath, List<String> methods) {
        String className = filePath.substring(filePath.lastIndexOf('/') + 1).replace(".java", "");
        StringBuilder sb = new StringBuilder();
        sb.append("Generate a JUnit 4.11 test class for Java class ").append(className).append(". ");
        sb.append("Test these methods: ").append(String.join(", ", methods)).append(". ");
        sb.append("Use meaningful variable names and no mocks.");
        return sb.toString();
    }
}

