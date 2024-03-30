package webserver.HttpHandler;

import webserver.HttpMessage.*;

import java.util.Optional;

import static webserver.HttpMessage.constants.WebServerConst.HTTP_VERSION;
import static webserver.HttpMessage.constants.WebServerConst.LOCATION;
import static webserver.HttpMessage.constants.eums.ResponseStatus.FOUND;


public interface Handler {

    /**
     * HTTP 응답 메시지 Body 에 대한 Content 정보를 담은 헤더를 반환
     * @param responseBody Body
     * @return Header
     */
    default MessageHeader writeContentResponseHeader(MessageBody responseBody) {
        return MessageHeader.builder()
                .field("Content-Type", responseBody.getContentType().getMimeType())
                .field("Content-Length", responseBody.getContentLength() + "")
                .build();
    }

    /**
     * 지정한 Path로 리다이렉트 하는 응답을 반환
     * @param path url path
     * @return
     */
    default Response redirectTo(String path){
        ResponseStartLine startLine = new ResponseStartLine(HTTP_VERSION, FOUND);
        MessageHeader responseHeader = MessageHeader.builder()
                .field(LOCATION, path).build();
        return new Response(startLine).header(responseHeader);
    }
}
