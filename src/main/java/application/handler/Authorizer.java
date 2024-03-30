package application.handler;

import webserver.HttpMessage.MessageHeader;
import webserver.HttpMessage.Request;
import webserver.HttpMessage.Response;
import webserver.HttpMessage.ResponseStartLine;

import java.util.Optional;

import static webserver.HttpMessage.constants.WebServerConst.HTTP_VERSION;
import static webserver.HttpMessage.constants.WebServerConst.LOCATION;
import static webserver.HttpMessage.constants.eums.ResponseStatus.FOUND;

public interface Authorizer {

    /**
     * HTTP 요청 쿠키에서 sid 값을 추출
     * @param request
     * @return
     */
    default String getSid(Request request) {
        Optional<String> sessionUserName = request.getCookie("sid");
        try {
            return sessionUserName.map(s -> s.split("=")[1]).orElse(null);
        }catch (ArrayIndexOutOfBoundsException noSid){
            return null;
        }
    }

    /**
     * 로그인 페이지로 리다이렉트하는 응답을 반환
     * @return
     */
    default Response redirectToLogin(){
        ResponseStartLine startLine = new ResponseStartLine(HTTP_VERSION, FOUND);
        MessageHeader responseHeader = MessageHeader.builder()
                .field(LOCATION, "/login").build();
        return new Response(startLine).header(responseHeader);
    }
}
