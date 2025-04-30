package com.hhoa;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * TestMermaidConverter
 *
 * @author xianxing
 * @since 2025/4/28
 */
public class TestMermaidConverter {
    @Test
    void testConvert() throws IOException {
        URL resource = TestMermaidConverter.class.getResource("/list_example.md");
        MermaidConverter mermaidConverter =
                new MermaidConverter(
                        false, Files.readString(new File(resource.getFile()).toPath()));
        String convert = mermaidConverter.convert();
        Assertions.assertDoesNotThrow(() -> MermaidValidator.validate(convert));
    }
}
