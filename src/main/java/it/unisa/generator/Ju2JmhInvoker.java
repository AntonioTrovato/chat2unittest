package it.unisa.generator;

import java.io.*;
import java.nio.file.*;

public class Ju2JmhInvoker {

    public static void convert(File testFile,
                               String testSourceRoot,
                               String testClassRoot,
                               String benchmarkOutputRoot) throws Exception {

        // Estrai converter-all.jar dalle risorse nella temp dir
        Path tmpDir = Files.createTempDirectory("ju2jmh");
        Path ju2jmhJar = tmpDir.resolve("converter-all.jar");

        try (InputStream in = Ju2JmhInvoker.class.getResourceAsStream("/ju2jmh/converter-all.jar")) {
            if (in == null) {
                throw new FileNotFoundException("converter-all.jar non trovato nel jar!");
            }
            Files.copy(in, ju2jmhJar, StandardCopyOption.REPLACE_EXISTING);
        }

        // Calcola il Fully Qualified Name della classe di test
        String fqn = computeFQN(testFile, testSourceRoot);

        // Lancia ju2jmh
        ProcessBuilder pb = new ProcessBuilder(
                "java", "-jar", ju2jmhJar.toAbsolutePath().toString(),
                testSourceRoot,
                testClassRoot,
                benchmarkOutputRoot,
                fqn
        );

        pb.inheritIO();
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
