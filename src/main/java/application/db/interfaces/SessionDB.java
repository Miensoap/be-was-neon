package application.db.interfaces;

import application.model.Session;

import java.util.Optional;

public interface SessionDB {

    /**
     * DB에 저장된 세션을 id로 조회해 반환
     * @param sessionId sid
     * @return
     */
    Optional<String> getSession(String sessionId);

    /**
     * DB에 세션을 저장
     * @param session
     */
    void addSession(Session session);

    /**
     * DB에서 세션을 삭제
     * @param sessionId
     */
    void removeSession(String sessionId);

    /**
     * DB의 저장된 세션의 수를 반환
     * @return
     */
    int getSize();

    /**
     * DB에서 모든 세션을 삭제
     */
    void clear();
}
