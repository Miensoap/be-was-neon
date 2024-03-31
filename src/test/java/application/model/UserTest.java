package application.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserTest {

    @DisplayName("올바른 비밀번호인지 확인할 수 있다")
    @Test
    void verifyPassword() {
        // given
        User user = new User("test", "1234", "테스트", "test@dsa.com");

        // when
        String correctPassword = "1234";
        String wrongPassword = "4321";

        //then
        assertThat(user.isCorrectPassword(correctPassword)).isTrue();
        assertThat(user.isCorrectPassword(wrongPassword)).isFalse();
    }
}
