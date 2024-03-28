package webserver.HttpHandler;

import webserver.HttpMessage.MessageBody;
import webserver.HttpMessage.MessageHeader;
import webserver.HttpMessage.Request;

import java.util.Optional;


public interface Handler {

    default MessageHeader writeContentResponseHeader(MessageBody responseBody) {
        return MessageHeader.builder()
                .field("Content-Type", responseBody.getContentType().getMimeType())
                .field("Content-Length", responseBody.getContentLength() + "")
                .build();
    }

    default boolean verifySession(Request request) {
        try {
            if(getSid(request) != null){
                return true;
            }
        } catch (NullPointerException | ArrayIndexOutOfBoundsException noCookieSid) {
            return false;
        }
        return false;
    }

    default String getSid(Request request) {
        Optional<String> sessionUserName = request.getCookie("sid");
        return sessionUserName.map(s -> s.split("=")[1]).orElse(null);
    }
}
