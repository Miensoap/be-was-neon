package webserver.Mapping;

import application.db.interfaces.SessionDB;
import application.db.interfaces.UserDB;
import application.db.memoryDB.MemSessionDB;
import application.db.memoryDB.MemUserDB;
import application.handler.LoginHandler;
import application.handler.UserHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import webserver.HttpHandler.Mapping.MappingMatcher;
import webserver.HttpMessage.Request;
import webserver.HttpMessage.Response;
import webserver.TestUtils;
import webserver.HttpMessage.constants.eums.ResponseStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class MappingMatcherTest {
    final UserDB userDB = new MemUserDB();
    final SessionDB sessionDB = new MemSessionDB();
    final MappingMatcher matcher = new MappingMatcher(List.of(new UserHandler(userDB) , new LoginHandler(userDB , sessionDB)));
    @Test
    @DisplayName("resource GET 요청이 들어오면 resourceHandler 의 메서드를 실행해 Response 를 반환한다")
    void getResourceResponse() throws Exception {
        // given
        Request request = TestUtils.getIndexRequest;

        //when
        Response response = matcher.getResponse(request);

        //then
        assertThat(response.getStartLine().getStatus()).isEqualTo(ResponseStatus.OK);
        assertThat(response.getHeader().getHeaderFields().containsKey("Content-Length")).isTrue();
    }

    @Test
    @DisplayName("create POST 요청이 들어오면 userHandler 의 메서드를 실행해 Response 를 반환한다")
    void getCreateResponse() throws Exception {
        // given
        Request request = TestUtils.createUserRequest;

        //when
        Response response = matcher.getResponse(request);

        //then
        assertThat(response.getStartLine().getStatus()).isEqualTo(ResponseStatus.FOUND);
        assertThat(response.getHeader().getHeaderFields().containsKey("Location")).isTrue();
    }

    @Test
    @DisplayName("등록되지 않은 메서드의 요청이 들어오면 405 응답을 보낸다")
    void getInvalidMethodResponse() throws Exception {
        // given
        Request request = new Request("PUT / HTTP/1.1");

        //when
        Response response = matcher.getResponse(request);

        //then
        assertThat(response.getStartLine().toString()).contains("405 Method Not Allowed");
    }
}