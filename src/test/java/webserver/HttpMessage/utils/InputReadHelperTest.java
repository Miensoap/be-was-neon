package webserver.HttpMessage.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static webserver.HttpMessage.utils.InputReadHelper.biReadLine;

class InputReadHelperTest {

    @DisplayName("바이트 InputStream을 CR 이나 LF 를 만나기 전까지 읽어 문자열을 반환한다")
    @Test
    void readLine() throws IOException {
        // given
        byte[] bytes = "abcdefg\r\n".getBytes();
        BufferedInputStream bi = new BufferedInputStream(new ByteArrayInputStream(bytes));

        // when
        String readString = biReadLine(bi);

        //then
        assertThat(readString).isEqualTo("abcdefg");
    }
}