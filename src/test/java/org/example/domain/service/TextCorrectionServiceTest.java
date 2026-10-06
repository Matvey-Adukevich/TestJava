package org.example.domain.service;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import static org.junit.jupiter.api.Assertions.*;

public class TextCorrectionServiceTest {

    @Test
    public void splitText_TextWithinLimit_ReturnsSingleChunk() {
        TextCorrectionService service = new TextCorrectionService(WebClient.builder());
        String text = "test";
        var chunks = service.splitText(text);
        assertEquals(1, chunks.size());
        assertEquals("test", chunks.get(0));
    }

    @Test
    public void splitText_TextExceedsLimit_ReturnsMultipleChunks() {
        TextCorrectionService service = new TextCorrectionService(WebClient.builder());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 15000; i++) {
            sb.append("a");
        }
        String text = sb.toString();
        var chunks = service.splitText(text);
        assertTrue(chunks.size() > 1);
    }

    @Test
    public void buildOptions_TextContainsDigits_ReturnsDigitsOption() {
        TextCorrectionService service = new TextCorrectionService(WebClient.builder());
        String text = "test123";
        int options = service.buildOptions(text);
        assertTrue((options & 2) != 0);
    }

    @Test
    public void buildOptions_TextContainsUrl_ReturnsUrlsOption() {
        TextCorrectionService service = new TextCorrectionService(WebClient.builder());
        String text = "test https://example.com";
        int options = service.buildOptions(text);
        assertTrue((options & 4) != 0);
    }

    @Test
    public void buildOptions_TextWithoutDigitsOrUrls_ReturnsZero() {
        TextCorrectionService service = new TextCorrectionService(WebClient.builder());
        String text = "test text";
        int options = service.buildOptions(text);
        assertEquals(0, options);
    }
}
