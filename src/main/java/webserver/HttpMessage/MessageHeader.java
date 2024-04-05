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

    /**
     * HTTP 메시지에 Body가 존재하는지 여부를 반환
     * @return
     */
    public boolean hasContent() {
        return headerFields.containsKey(CONTENT_TYPE) && headerFields.containsKey(CONTENT_LEN);
    }

    /**
     * 헤더 생성에 사용하는 Builder
     */
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

    /**
     * 헤더 필드가 저장된 불변 Map을 반환
     * @return
     */
    public Map<String, String> getHeaderFields() {
        return Collections.unmodifiableMap(headerFields);
    }

    /**
     * 헤더 필드를 추가
     * @param key
     * @param value
     */
    public void addHeaderField(String key, String value) {
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

    /**
     * 쿠키의 길이 , 이름을 전달받아 생성해 헤더에 추가
     * @param length 길이
     * @param cookieName 이름
     * @return 생성한 쿠키 값
     */
    public String addCookie(int length, String cookieName) {
        String newCookie = makeCookie(length);
        ZonedDateTime dateTime = ZonedDateTime.now().plus(1, ChronoUnit.MINUTES);
        String formattedDateTime = dateTime.format(DateTimeFORMAT);

        String value = cookieName + "=" + newCookie + VALUE_DELIM + "Path=/" + VALUE_DELIM + "Expires=" + formattedDateTime;

        try {
            addHeaderField("Set-Cookie", value);
        }catch (IllegalArgumentException secondPut){
            headerFields.replace("Set-Cookie", value);
        }

        return newCookie;
    }

    /**
     * 지정한 길이의 무작위 문자열 쿠키 값 생성
     * @param length 길이
     * @return 쿠키 값
     */
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
