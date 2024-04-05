package webserver.HttpHandler;

import webserver.HttpMessage.*;
import webserver.HttpMessage.constants.eums.FileType;
import webserver.HttpMessage.constants.eums.ResponseStatus;

import static webserver.HttpMessage.constants.WebServerConst.HTTP_VERSION;

public class ErrorHandler implements Handler {

    private ResponseStartLine startLine;
    private MessageHeader responseHeader;
    private MessageBody responseBody;

    /**
     * 지정한 HTTP 상태 코드에 해당하는 plain text 응답을 반환
     * @param status 상태 코드
     * @return
     */
    public Response getErrorResponse(ResponseStatus status) {
        startLine = new ResponseStartLine(HTTP_VERSION , status);
        responseBody = new MessageBody(status.getMessage() , FileType.TXT);
        responseHeader = writeContentResponseHeader(responseBody);

        return new Response(startLine).header(responseHeader).body(responseBody);
    }
}
