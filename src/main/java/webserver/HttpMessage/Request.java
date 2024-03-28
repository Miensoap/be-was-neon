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

    public Request(String startLine) {
        this.startLine = new RequestStartLine(startLine);
    }

    public Request header(MessageHeader messageHeader) {
        this.header = messageHeader;
        return this;
    }

    public Request body(MessageBody body) {
        this.body = body;
        return this;
    }

    public String getRequestQuery(String key) {
        return startLine.getQuery().get(key);
    }

    public String getHeaderValue(String key) {
        return header.getHeaderFields().get(key);
    }

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

    public String toString() {
        StringJoiner sj = new StringJoiner(CRLF);
        sj.add(startLine.toString())
                .add(header.toString());
        if(body!=null) sj.add(body.toString());

        return sj.toString();
    }
}
