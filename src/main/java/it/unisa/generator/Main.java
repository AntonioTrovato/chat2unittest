package it.unisa.generator;

import java.io.File;
import java.util.Map;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length < 4 || !args[2].equals("-llm")) {
            System.err.println("Usage: java -jar test2benchmark.jar <input.json> <ju2jmh.jar> -llm <llm_url>");
            System.exit(1);
        }

        String jsonPath = args[0];
        String ju2jmhPath = args[1];
        String llmUrl = args[3];

        LLMClient.setEndpoint(llmUrl); // Imposta endpoint dinamico

        Map<String, List<String>> input = JsonInputParser.parse(jsonPath);
        for (String filePath : input.keySet()) {
            List<String> methods = input.get(filePath);
            String testContent = PromptBuilder.buildJUnitPrompt(filePath, methods);
            String testCode = LLMClient.generate(testContent);
            File testFile = TestFileWriter.writeJUnitTest(filePath, testCode);
            Ju2JmhInvoker.convert(testFile, ju2jmhPath);
        }

        System.out.println("DONE.");
    }
}
