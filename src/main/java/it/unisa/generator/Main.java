package it.unisa.generator;

import java.io.File;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length < 4 || !args[1].equals("-host")) {
            System.err.println("Usage: java -jar test2benchmark.jar <input.json> -host <llm_url> [-mdl <model>] [-tmp <temperature>] <ju2jmh.jar>");
            System.exit(1);
        }

        String jsonPath = args[0];
        String hostUrl = args[2];
        String ju2jmhPath = args[args.length - 1];

        String mdl = "codellama-13b-instruct";
        double temperature = 0.4;

        for (int i = 3; i < args.length - 1; i++) {
            switch (args[i]) {
                case "-mdl":
                    mdl = args[++i];
                    break;
                case "-tmp":
                    temperature = Double.parseDouble(args[++i]);
                    break;
            }
        }

        LLMClient.configure(hostUrl, mdl, temperature);

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
