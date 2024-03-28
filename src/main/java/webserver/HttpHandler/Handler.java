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
}
