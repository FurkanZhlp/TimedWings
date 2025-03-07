package me.furkanzhlp.timedwings.message;

import java.util.HashMap;
import java.util.Map;

public class MessagePlaceholder {
    private final Map<String, String> placeholders = new HashMap<>();
    public MessagePlaceholder add(String key, String value) {
        placeholders.put(key, value);
        return this;
    }

    public MessagePlaceholder addAll(Map<String, String> placeholders) {
        this.placeholders.putAll(placeholders);
        return this;
    }

    public String replace(String message) {
        String result = message;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            result = result.replace("%" + entry.getKey() + "%", entry.getValue());
        }
        return result;
    }

    public Map<String, String> getPlaceholders() {
        return placeholders;
    }
}
