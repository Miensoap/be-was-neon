package webserver.HttpHandler.Mapping;

import webserver.HttpHandler.ErrorHandler;
import webserver.HttpHandler.Handler;
import application.handler.LoginHandler;
import webserver.HttpHandler.ResourceHandler;
import application.handler.UserHandler;
import webserver.HttpMessage.Request;
import webserver.HttpMessage.RequestStartLine;
import webserver.HttpMessage.Response;
import webserver.HttpMessage.constants.eums.ResponseStatus;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

import static webserver.HttpMessage.constants.WebServerConst.*;

public class MappingMatcher {

    /**
     * WAS 에서 사용할 Handler 리스트
     */
    private final List<Handler> handlers = new ArrayList<>();
    private final ResourceHandler resourceHandler = new ResourceHandler();
    private final ErrorHandler errorHandler = new ErrorHandler();

    public MappingMatcher(List<Handler> appHandlers) {
        // application handlers
        this.handlers.addAll(appHandlers);
    }

    /**
     * HTTP 요청에 알맞는 핸들러를 찾아 응답을 반환한다
     * @param request HTTP 요청
     * @return HTTP 응답
     * @throws Exception 요청 처리중 발생한 예외
     */
    public Response getResponse(Request request) throws Exception {
        RequestStartLine startLine = request.getStartLine();

        final String httpMethod = startLine.getMethod();
        final String path = startLine.getUri().split(QUERY_START)[0];

        if (httpMethod.equals(GET)) {
            return handleRequest(request, path, this::matchGetMapping);
        }
        if (httpMethod.equals(POST)) {
            return handleRequest(request, path, this::matchPostMapping);
        } else {
            return errorHandler.getErrorResponse(ResponseStatus.MethodNotAllowed);
        }
    }

    /**
     * @param request HTTP 요청
     * @param uri 요청 URI
     * @param matchMapping 어노테이션 확인 메서드
     * @return HTTP 응답
     */
    private Response handleRequest(Request request , String uri, BiPredicate<Method, String> matchMapping) throws Exception {
        for(Handler handler : handlers) {
            for (Method method : handler.getClass().getDeclaredMethods()) {
                if (matchMapping.test(method, uri)) {
                    return (Response) method.invoke(handler, request);
                }
            }
        }
        // default : get Resource
        return new ResourceHandler().responseGet(request);
    }

    /**
     * @param method HTTP Method
     * @param uri 요청 URI
     * @return 요청과 일치하는 핸들러인지 여부
     */
    private boolean matchGetMapping(Method method, String uri) {
        if (method.isAnnotationPresent(GetMapping.class)) {
            String path = method.getAnnotation(GetMapping.class).path();
            return path.equals(uri);
        }
        return false;
    }

    private boolean matchPostMapping(Method method, String uri) {
        if (method.isAnnotationPresent(PostMapping.class)) {
            String path = method.getAnnotation(PostMapping.class).path();
            return path.equals(uri);
        }
        return false;
    }
}
