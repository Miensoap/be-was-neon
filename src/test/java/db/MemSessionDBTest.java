package db;

import application.db.interfaces.SessionDB;
import application.db.memoryDB.MemSessionDB;
import application.model.Session;
import application.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class MemSessionDBTest {
    final SessionDB sessionDB = new MemSessionDB();
    User user;
    @BeforeEach
    void createUser(){
        user = new User("test", "test", "test", "test@naver.com");
    }

    @Test
    @DisplayName("쿠키와 유저 정보로 새 세션을 만들고 , 쿠키 정보로 로그인한 유저를 조회할 수 있다")
    void addSession() {
        sessionDB.addSession(new Session("1234", user.getUserId()));

        assertThat(sessionDB.getSession("1234")).isEqualTo(user.getUserId());
    }

    @Test
    @DisplayName("쿠키 정보로 세션을 삭제할 수 있다")
    void removeSession(){
        sessionDB.addSession(new Session("12345", user.getUserId()));
        sessionDB.removeSession("12345");

        assertThat(sessionDB.getSession("12345")).isEqualTo(null);
    }
}
