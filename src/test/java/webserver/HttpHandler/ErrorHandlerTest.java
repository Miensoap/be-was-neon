package webserver.HttpHandler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import webserver.HttpMessage.Response;
import webserver.HttpMessage.constants.eums.ResponseStatus;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static webserver.HttpMessage.constants.eums.ResponseStatus.NotFound;

class ErrorHandlerTest {
    ErrorHandler errorHandler = new ErrorHandler();

    @DisplayName("지정한 상태 코드에 해당하는 오류 메시지 응답을 반환한다")
    @Test
    void test() {
        // given
        // when
        Response response = errorHandler.getErrorResponse(NotFound);

        //then
        assertThat(response.getStartLine().getStatus()).isEqualTo(NotFound);
        assertThat(new String(response.getBody())).isEqualTo(NotFound.getMessage());
    }
}