package com.hhoa;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * TestMermaidValidator
 *
 * @author xianxing
 * @since 2025/4/30
 */
public class TestMermaidValidator {
    @Test
    void testValidSuccess() {
        String mermaidCode =
                """
            graph LR
            A --> B
            B --> C
            """;
        Assertions.assertDoesNotThrow(() -> MermaidValidator.validate(mermaidCode));
    }

    @Test
    void testValidFail() {
        String mermaidCode =
                """
            graph LR
            A --> B
            B -->
            """;
        Assertions.assertThrows(
                Exception.class,
                () -> {
                    MermaidValidator.validate(mermaidCode);
                });
    }
}
