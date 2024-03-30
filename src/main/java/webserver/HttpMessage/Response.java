package webserver.HttpMessage;

import java.util.StringJoiner;

import static webserver.HttpMessage.constants.WebServerConst.CRLF;

public class Response {
    private final ResponseStartLine startLine;
    private MessageHeader header;
    private MessageBody body;

    public Response(ResponseStartLine startLine) {
        this.startLine = startLine;
    }

    public Response header(MessageHeader messageHeader) {
        this.header = messageHeader;
        return this;
    }

    public Response body(MessageBody body) {
        this.body = body;
        return this;
    }

    public ResponseStartLine getStartLine() {
        return startLine;
    }

    public MessageHeader getHeader() {
        return header;
    }

    /**
     * 메시지 바디 데이터 byte 배열을 반환
     * @return
     */
    public byte[] getBody() {
        if (body == null) return null;
        return body.getBody();
    }

    /**
     * 메시지 바디 객체를 반환
     * @return
     */
    public MessageBody messageBody() {
        return this.body;
    }

    /**
     * StartLine + Header 를 HTTP 메시지 형식에 맞는 문자열로 반환
     * Body는 따로 Byte 배열 사용
     * @return
     */
    public String toString() {
        StringJoiner sj = new StringJoiner(CRLF);
        sj.add(startLine.toString())
                .add(header.toString() + CRLF);
        return sj.toString();
    }

    /**
     * 헤더에 필드를 추가
     * @param key
     * @param value
     * @return
     */
    public Response addHeaderField(String key, String value) {
        this.header.headerFields.put(key , value);
        return this;
    }
}
