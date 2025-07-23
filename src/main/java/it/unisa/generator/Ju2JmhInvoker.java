package it.unisa.generator;

import java.io.*;

public class Ju2JmhInvoker {
    public static void convert(File testFile, String ju2jmhJarPath) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(
                "java", "-jar", ju2jmhJarPath, testFile.getAbsolutePath()
        );
        pb.inheritIO();
        Process p = pb.start();
        int exit = p.waitFor();
        if (exit != 0) throw new RuntimeException("ju2jmh failed for " + testFile);
    }
}
