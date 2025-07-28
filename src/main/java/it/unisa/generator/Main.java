package it.unisa.generator;

import java.io.File;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length < 8 || !args[1].equals("-host")) {
            System.err.println("Usage: java -jar test2benchmark.jar <input.json> -host <llm_full_url> " +
                    "[-mdl <model>] [-tmp <temperature>] -src <testSourceRoot> -bin <testClassRoot> -jmh <benchmarkOutputRoot>");
            System.err.println("Example: java -jar test2benchmark.jar input.json -host https://xxxxxxxx/v1/chat/completions " +
                    "-mdl codellama-13b-instruct -tmp 0.4 -src module/src/test/java -bin module/build/classes/java/test -jmh module/src/jmh/java");
            System.exit(1);
        }

        String jsonPath = args[0];
        String hostUrl = args[2];
        String model = "codellama-13b-instruct";
        double temperature = 0.4;
        String testSourceRoot = null;
        String testClassRoot = null;
        String benchmarkOutputRoot = null;

        for (int i = 3; i < args.length; i++) {
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
            System.out.println("Processing file: " + filePath);
            String testContent = PromptBuilder.buildJUnitPrompt(filePath, methods);
            System.out.println("Generated prompt: " + testContent);
            String testCode = LLMClient.generate(testContent);
            System.out.println("Generated test code:\n" + testCode);
            File testFile = TestFileWriter.writeJUnitTest(filePath, testCode);
            System.out.println("Test file written: " + testFile.getAbsolutePath());
            Ju2JmhInvoker.convert(testFile, testSourceRoot, testClassRoot, benchmarkOutputRoot);
        }

        System.out.println("DONE.");
    }
}
