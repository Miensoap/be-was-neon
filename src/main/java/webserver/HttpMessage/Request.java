package webserver.HttpMessage;

import java.util.Arrays;
import java.util.Optional;
import java.util.StringJoiner;

import static webserver.HttpMessage.constants.WebServerConst.CRLF;
import static webserver.HttpMessage.constants.WebServerConst.VALUE_DELIM;

public class Request {
    private final RequestStartLine startLine;
    private MessageHeader header;
    private MessageBody body;

    /**
     * StartLine 문자열을 전달받아 저장
     * @param startLine 문자열
     */
    public Request(String startLine) {
        this.startLine = new RequestStartLine(startLine);
    }

    /**
     * Set Header
     * @param messageHeader
     * @return this
     */
    public Request header(MessageHeader messageHeader) {
        this.header = messageHeader;
        return this;
    }

    /**
     * Set Body
     * @param body
     * @return this
     */
    public Request body(MessageBody body) {
        this.body = body;
        return this;
    }

    /**
     * key에 해당하는 URL 쿼리 값을 반환
     * @param key URL Query Key
     * @return
     */
    public String getRequestQuery(String key) {
        return startLine.getQuery().get(key);
    }

    /**
     * key에 해당하는 헤더 필드의 값을 반환
     * @param key Header Field Key
     * @return
     */
    public String getHeaderValue(String key) {
        return header.getHeaderFields().get(key);
    }

    /**
     * 쿠키 네임에 해당하는 쿠키 값을 반환
     * @param name Cookie name
     * @return
     */
    public Optional<String> getCookie(String name){
        String[] cookie = getHeaderValue("Cookie").split(VALUE_DELIM);
        return Arrays.stream(cookie).filter(s->s.startsWith(name + "=")).findAny();
    }

    public RequestStartLine getStartLine() {
        return startLine;
    }

    public MessageHeader getHeader() {
        return header;
    }

    public MessageBody getBody() {
        return body;
    }

    /**
     * HTTP 요청 원본 형태 문자열로 반환
     * @return
     */
    public String toString() {
        StringJoiner sj = new StringJoiner(CRLF);
        sj.add(startLine.toString())
                .add(header.toString());
        if(body!=null) sj.add(body.toString());

        return sj.toString();
    }
}
