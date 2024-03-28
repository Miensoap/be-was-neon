package webserver.HttpMessage;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static webserver.HttpMessage.constants.WebServerConst.*;

public class MessageHeader {
    Map<String, String> headerFields;

    private MessageHeader(Map<String, String> headerFields) {
        this.headerFields = headerFields;
    }

    public static HeaderBuilder builder() {
        return new HeaderBuilder();
    }

    public boolean hasContent() {
        return headerFields.containsKey(CONTENT_TYPE) && headerFields.containsKey(CONTENT_LEN);
    }

    public static class HeaderBuilder {
        private final Map<String, String> headerFields = new HashMap<>();

        public HeaderBuilder field(String key, String value) {
            headerFields.put(key, value);
            return this;
        }

        public MessageHeader build() {
            return new MessageHeader(headerFields);
        }
    }

    public Map<String, String> getHeaderFields() {
        return Collections.unmodifiableMap(headerFields);
    }

    private void addHeaderField(String key, String value) {
        headerFields.put(key, value);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        headerFields.keySet().forEach(key -> {
            sb.append(key)
                    .append(HEADER_DELIM)
                    .append(headerFields.get(key))
                    .append(CRLF);
        });
        return sb.toString();
    }

    public String addCookie(int length, String cookieName) {
        String newCookie = makeCookie(length);
        ZonedDateTime dateTime = ZonedDateTime.now().plus(1, ChronoUnit.MINUTES);
        String formattedDateTime = dateTime.format(DateTimeFORMAT);

        addHeaderField("Set-Cookie", cookieName + "=" + newCookie + VALUE_DELIM +
                "Path=/" + VALUE_DELIM
                + "Expires=" + formattedDateTime);

        return newCookie;
    }

    private String makeCookie(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        String newCookie;

        while (sb.length() < length) {
            int index = random.nextInt(CHRACTERS.length());
            char randomChar = CHRACTERS.charAt(index);
            sb.append(randomChar);
        }
        newCookie = sb.toString();

        return newCookie;
    }
}
