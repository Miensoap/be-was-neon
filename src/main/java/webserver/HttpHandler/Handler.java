package webserver.HttpHandler;

import webserver.HttpMessage.*;

import java.util.Optional;

import static webserver.HttpMessage.constants.WebServerConst.HTTP_VERSION;
import static webserver.HttpMessage.constants.WebServerConst.LOCATION;
import static webserver.HttpMessage.constants.eums.ResponseStatus.FOUND;


public interface Handler {

    default MessageHeader writeContentResponseHeader(MessageBody responseBody) {
        return MessageHeader.builder()
                .field("Content-Type", responseBody.getContentType().getMimeType())
                .field("Content-Length", responseBody.getContentLength() + "")
                .build();
    }

    default Response redirectTo(String path){
        ResponseStartLine startLine = new ResponseStartLine(HTTP_VERSION, FOUND);
        MessageHeader responseHeader = MessageHeader.builder()
                .field(LOCATION, path).build();
        return new Response(startLine).header(responseHeader);
    }
}
