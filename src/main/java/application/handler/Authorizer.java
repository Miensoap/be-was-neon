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

    default String getSid(Request request) {
        Optional<String> sessionUserName = request.getCookie("sid");
        try {
            return sessionUserName.map(s -> s.split("=")[1]).orElse(null);
        }catch (ArrayIndexOutOfBoundsException noSid){
            return null;
        }
    }

    default Response redirectToLogin(){
        ResponseStartLine startLine = new ResponseStartLine(HTTP_VERSION, FOUND);
        MessageHeader responseHeader = MessageHeader.builder()
                .field(LOCATION, "/login").build();
        return new Response(startLine).header(responseHeader);
    }
}
