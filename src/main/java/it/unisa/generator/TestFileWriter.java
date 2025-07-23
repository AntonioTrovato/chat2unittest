package it.unisa.generator;

import java.io.*;
import java.nio.file.*;

public class TestFileWriter {
    public static File writeJUnitTest(String sourceFilePath, String content) throws IOException {
        String testPath = sourceFilePath
                .replace("/main/", "/test/")
                .replace(".java", "Test.java");
        Path path = Paths.get(testPath);
        Files.createDirectories(path.getParent());
        Files.writeString(path, content);
        return path.toFile();
    }
}
