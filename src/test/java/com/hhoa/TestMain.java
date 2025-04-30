package com.hhoa;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * TestMain
 *
 * @author xianxing
 * @since 2025/4/30
 */
public class TestMain {
    @Test
    void testConvert() throws IOException {
        URL resource = TestMermaidConverter.class.getResource("/list_example.md");
        Path temp = Files.createTempFile("temp", ".mermaid");
        Main.main(new String[] {"-i", resource.getPath(), "-o", temp.toString()});
        Assertions.assertDoesNotThrow(() -> MermaidValidator.validate(Files.readString(temp)));
    }
}
