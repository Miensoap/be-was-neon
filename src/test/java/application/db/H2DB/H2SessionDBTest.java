package application.db.H2DB;

import application.model.Session;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class H2SessionDBTest {

    H2SessionDB sessionDB = new H2SessionDB();

    @AfterEach
    void rollback() throws SQLException {
        // commit - rollback 사용해 서로 다른 테스트 / 원본 DB에 영향을 주지 않도록
        sessionDB.rollback();
    }

    H2SessionDBTest() throws SQLException {
    }

    @DisplayName("세션 객체로 DB에 세션을 저장할 수 있다")
    @Test
    void addSession() throws SQLException {
        // given
        Session session = new Session("abcd" , "tester");

        // when
        sessionDB.addSession(session);
        sessionDB.commit();

        // then
        assertThat(sessionDB.getSize()).isEqualTo(1);
    }

    @DisplayName("세션 ID로 저장된 세션을 조회할 수 있다")
    @Test
    void getSession() throws SQLException {
        // given
        Session session = new Session("abcd" , "tester");
        sessionDB.addSession(session);
        sessionDB.commit();

        // when
        Optional<String> userId = sessionDB.getSession("abcd");

        //then
        assertThat(userId).isPresent();
        assertThat(userId.get()).isEqualTo("tester");
    }

    @DisplayName("세션 ID로 저장된 세션을 삭제할 수 있다")
    @Test
    void removeSession() throws SQLException {
        // given
        Session session = new Session("abcd" , "tester");
        sessionDB.addSession(session);
        sessionDB.commit();

        // when
        sessionDB.removeSession("abcd");

        //then
        assertThat(sessionDB.getSize()).isEqualTo(0);
    }

    @DisplayName("저장된 세션의 수를 조회할 수 있다")
    @Test
    void getSize(){
        //given
        Session session1 = new Session("abcde", "tester");
        Session session2 = new Session("abcdef", "tester");
        Session session3 = new Session("abcdefg", "tester");

        // when
        sessionDB.addSession(session1);
        sessionDB.addSession(session2);
        sessionDB.addSession(session3);

        // then
        assertThat(sessionDB.getSize()).isEqualTo(3);
    }
}