package org.example.domain.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class TextCorrectionService {
    private static final Logger logger = LoggerFactory.getLogger(TextCorrectionService.class);
    private static final int MAX_TEXT_LENGTH = 10000;
    private static final String YANDEX_SPELLER_URL = "https://speller.yandex.net/services/spellservice.json/checkTexts";
    private static final Pattern DIGIT_PATTERN = Pattern.compile("\\d");
    private static final Pattern URL_PATTERN = Pattern.compile("https?://[\\w.-]+\\.[a-z]{2,}[^\\s]*", Pattern.CASE_INSENSITIVE);

    private final WebClient webClient;

    public TextCorrectionService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String correctText(String text, String language) {
        try {
            List<String> chunks = splitText(text);
            StringBuilder correctedText = new StringBuilder();

            for (String chunk : chunks) {
                String correctedChunk = correctChunk(chunk, language);
                correctedText.append(correctedChunk);
            }

            return correctedText.toString();
        } catch (Exception e) {
            logger.error("Error correcting text", e);
            throw new RuntimeException("Failed to correct text: " + e.getMessage(), e);
        }
    }

    List<String> splitText(String text) {
        List<String> chunks = new ArrayList<>();
        int start = 0;

        while (start < text.length()) {
            int end = Math.min(start + MAX_TEXT_LENGTH, text.length());
            chunks.add(text.substring(start, end));
            start = end;
        }

        return chunks;
    }

    private String correctChunk(String chunk, String language) {
        int options = buildOptions(chunk);
        String langCode = "RU".equals(language) ? "ru" : "en";

        YandexSpellerRequest request = new YandexSpellerRequest(chunk, langCode, options);

        try {
            List<List<YandexSpellerResponse>> responses = webClient.post()
                    .uri(YANDEX_SPELLER_URL)
                    .bodyValue(List.of(request))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<List<YandexSpellerResponse>>>() {})
                    .block();

            if (responses == null || responses.isEmpty()) {
                return chunk;
            }

            return applyCorrections(chunk, responses.get(0));
        } catch (Exception e) {
            logger.error("Error calling Yandex Speller API", e);
            throw new RuntimeException("Failed to call Yandex Speller API: " + e.getMessage(), e);
        }
    }

    int buildOptions(String text) {
        int options = 0;

        if (DIGIT_PATTERN.matcher(text).find()) {
            options |= 2;
        }

        if (URL_PATTERN.matcher(text).find()) {
            options |= 4;
        }

        return options;
    }

    private String applyCorrections(String text, List<YandexSpellerResponse> corrections) {
        if (corrections == null || corrections.isEmpty()) {
            return text;
        }

        StringBuilder result = new StringBuilder(text);
        int offset = 0;

        for (YandexSpellerResponse correction : corrections) {
            int pos = correction.pos + offset;
            if (pos >= 0 && pos < result.length()) {
                result.replace(pos, pos + correction.len, correction.s.get(0));
                offset += correction.s.get(0).length() - correction.len;
            }
        }

        return result.toString();
    }

    private static class YandexSpellerRequest {
        private final String text;
        private final String lang;
        private final int options;

        @JsonCreator
        public YandexSpellerRequest(
                @JsonProperty("text") String text,
                @JsonProperty("lang") String lang,
                @JsonProperty("options") int options) {
            this.text = text;
            this.lang = lang;
            this.options = options;
        }

        public String getText() {
            return text;
        }

        public String getLang() {
            return lang;
        }

        public int getOptions() {
            return options;
        }
    }

    private static class YandexSpellerResponse {
        private int pos;
        private int len;
        private List<String> s;

        @JsonCreator
        public YandexSpellerResponse(
                @JsonProperty("pos") int pos,
                @JsonProperty("len") int len,
                @JsonProperty("s") List<String> s) {
            this.pos = pos;
            this.len = len;
            this.s = s;
        }

        public int getPos() {
            return pos;
        }

        public int getLen() {
            return len;
        }

        public List<String> getS() {
            return s;
        }
    }
}
