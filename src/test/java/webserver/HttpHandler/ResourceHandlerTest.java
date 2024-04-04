package webserver.HttpHandler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import webserver.HttpMessage.Request;
import webserver.HttpMessage.Response;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

public class ResourceHandlerTest {
    private static final ResourceHandler RESOURCE_HANDLER = new ResourceHandler();

    @ParameterizedTest
    @CsvSource({"GET /index.html HTTP/1.1", "GET /registration/index.html HTTP/1.1"})
    @DisplayName("요청 url path로 특정할 수 있는 리소스를 응답한다")
    void getResourceRequest(String startLine) {

        Request request = new Request(startLine);
        Response response = RESOURCE_HANDLER.getResource(request);
        String body = new String(response.getBody());
        Map<String, String> headerFields = response.getHeader().getHeaderFields();

        assertThat(headerFields.get("Content-Type")).isEqualTo("text/html; charset=utf-8");
        assertThat(body.startsWith("<!DOCTYPE html>")).isTrue();
    }

    @ParameterizedTest
    @CsvSource({"GET / HTTP/1.1", "GET /registration HTTP/1.1"})
    @DisplayName("url path로 특정할 수 있는 리소스가 없다면 , 해당 경로의 index.html 을 요청한 것으로 간주한다")
    void getPathRequest(String startLine) {
        Request request = new Request(startLine);
        Response response = RESOURCE_HANDLER.getResource(request);
        String body = new String(response.getBody());
        Map<String, String> headerFields = response.getHeader().getHeaderFields();

        assertThat(headerFields.get("Content-Type")).isEqualTo("text/html; charset=utf-8");
        assertThat(body.startsWith("<!DOCTYPE html>")).isTrue();
    }
}
