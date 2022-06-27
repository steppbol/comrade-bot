package com.balashenka.comrade.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.balashenka.comrade.service.LocaleService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Log4j2
@Component
public final class MessageUtil {
    public static final String NEW_LINE_HTML_SYMBOL = "</br>";

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{(\\w+)}");
    private static final String PLACEHOLDER_FORMAT = "${%s}";

    private static final Pattern URL_PATTERN = Pattern.compile("^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$");

    private final LocaleService localeService;
    private final ObjectMapper objectMapper;

    @Autowired
    public MessageUtil(LocaleService localeService) {
        this.localeService = localeService;
        this.objectMapper = new ObjectMapper();
    }

    public List<Map<String, ?>> getAttachment(String path, String... arguments) {
        List<Map<String, ?>> attachment = null;

        try {
            var content = Files.readString(Path.of(path));
            content = fillTextContent(content, arguments);

            Map<String, Object> converted = objectMapper.readValue(content, new TypeReference<>() {
            });

            attachment = List.of(converted);
        } catch (IOException e) {
            log.error("Error while converting attachment file: " + path, e);
        }

        return attachment;
    }

    public String getText(String path, String... arguments) {
        return fillTextContent(localeService.getText(path), arguments);
    }

    public boolean isUrl(String text) {
        boolean found = false;
        if (text != null && !text.isBlank()) {
            var matcher = URL_PATTERN.matcher(text);

            if (matcher.find()) {
                found = true;
            }
        }

        return found;
    }

    public String fillTextContent(String text, @NonNull String... arguments) {
        if (arguments.length > 0) {
            Map<String, Object> values = new HashMap<>();
            for (var i = 0; i < arguments.length; i++) {
                values.put(String.valueOf(i), arguments[i]);
            }
            text = format(text, values);
        }

        return text;
    }

    private String format(String source, Map<String, Object> values) {
        var formatter = new StringBuilder(source);
        var matcher = PLACEHOLDER_PATTERN.matcher(source);

        List<Object> foundValues = new ArrayList<>();
        while (matcher.find()) {
            var key = matcher.group(1);
            var formatKey = String.format(PLACEHOLDER_FORMAT, key);
            var index = formatter.indexOf(formatKey);

            if (index != -1) {
                formatter.replace(index, index + formatKey.length(), "%s");
                foundValues.add(values.get(key));
            }
        }

        return String.format(formatter.toString(), foundValues.toArray());
    }
}
