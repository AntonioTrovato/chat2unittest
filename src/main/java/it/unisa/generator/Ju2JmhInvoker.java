package it.unisa.generator;

import java.io.File;

public class Ju2JmhInvoker {
    public static void convert(File testFile, String ju2jmhJarPath,
                               String testSourceRoot, String testClassRoot, String benchmarkOutputRoot) throws Exception {

        // Get the fully qualified name (FQN) of the test class
        String fqn = computeFQN(testFile, testSourceRoot);

        ProcessBuilder pb = new ProcessBuilder(
                "java", "-jar", ju2jmhJarPath,
                testSourceRoot,
                testClassRoot,
                benchmarkOutputRoot,
                fqn
        );

        pb.inheritIO(); // Show the output
        Process process = pb.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("ju2jmh conversion failed for class: " + fqn);
        }
    }

    private static String computeFQN(File file, String testSourceRoot) {
        String absPath = file.getAbsolutePath();
        String root = new File(testSourceRoot).getAbsolutePath();

        if (!absPath.startsWith(root)) {
            throw new IllegalArgumentException("File not under testSourceRoot");
        }

        String relativePath = absPath.substring(root.length() + 1);
        return relativePath
                .replace(File.separatorChar, '.')
                .replaceAll("\\.java$", "");
    }
}
