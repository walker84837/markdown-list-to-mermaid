package com.hhoa;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class MermaidValidator {
    /** Validate mermaid code, it needs to install @mermaid-js/mermaid-cli by system node npm. */
    public static void validate(String mermaidCode) throws IOException {
        Path input = Files.createTempFile("input", ".mmd");
        Path output = Files.createTempFile("output", ".svg");
        Files.write(input, mermaidCode.getBytes());
        ProcessBuilder mmdc =
                new ProcessBuilder("mmdc", "-i", input.toString(), "-o", output.toString());
        Map<String, String> environment = mmdc.environment();
        Map<String, String> getenv = System.getenv();
        environment.putAll(getenv);
        Process start = mmdc.start();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
        start.getErrorStream().transferTo(bufferedOutputStream);
        bufferedOutputStream.flush();
        byteArrayOutputStream.flush();
        if (byteArrayOutputStream.size() > 0) {
            throw new IOException(byteArrayOutputStream.toString());
        }
    }
}
