package it.unisa.generator;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class PromptBuilder {
    public static String buildJUnitPrompt(String filePath, List<String> methods) throws Exception {
        String className = filePath.substring(filePath.lastIndexOf('/') + 1).replace(".java", "");
        String code = Files.readString(Paths.get(filePath));

        StringBuilder sb = new StringBuilder();
        sb.append("Generate a JUnit 4.11 test class called ").append(className).append("Test for the following Java class:\n\n");
        sb.append(code).append("\n\n");
        sb.append("Test these methods: ").append(String.join(", ", methods)).append(". ");
        return sb.toString();
    }
}

