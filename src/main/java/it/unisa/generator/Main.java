package it.unisa.generator;

import java.io.File;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length < 10 || !args[1].equals("-host")) {
            System.err.println("Usage: java -jar test2benchmark.jar <input.json> -host <llm_url> " +
                    "[-mdl <model>] [-tmp <temperature>] " +
                    "-src <testSourceRoot> -bin <testClassRoot> -jmh <benchmarkOutputRoot> <ju2jmh.jar>");
            System.exit(1);
        }

        String jsonPath = args[0];
        String hostUrl = args[2];
        String model = "codellama-13b-instruct";
        double temperature = 0.4;
        String testSourceRoot = null;
        String testClassRoot = null;
        String benchmarkOutputRoot = null;
        String ju2jmhPath = args[args.length - 1]; // ultimo parametro

        // parsing opzionale e obbligatorio
        for (int i = 3; i < args.length - 1; i++) {
            switch (args[i]) {
                case "-mdl":
                    model = args[++i];
                    break;
                case "-tmp":
                    temperature = Double.parseDouble(args[++i]);
                    break;
                case "-src":
                    testSourceRoot = args[++i];
                    break;
                case "-bin":
                    testClassRoot = args[++i];
                    break;
                case "-jmh":
                    benchmarkOutputRoot = args[++i];
                    break;
                default:
                    System.err.println("Unknown argument: " + args[i]);
                    System.exit(1);
            }
        }

        if (testSourceRoot == null || testClassRoot == null || benchmarkOutputRoot == null) {
            System.err.println("Missing required arguments: -src, -bin, -jmh");
            System.exit(1);
        }

        LLMClient.configure(hostUrl, model, temperature);

        Map<String, List<String>> input = JsonInputParser.parse(jsonPath);
        for (String filePath : input.keySet()) {
            List<String> methods = input.get(filePath);
            String testContent = PromptBuilder.buildJUnitPrompt(filePath, methods);
            String testCode = LLMClient.generate(testContent);
            File testFile = TestFileWriter.writeJUnitTest(filePath, testCode);
            Ju2JmhInvoker.convert(testFile, ju2jmhPath, testSourceRoot, testClassRoot, benchmarkOutputRoot);
        }

        System.out.println("DONE.");
    }
}
