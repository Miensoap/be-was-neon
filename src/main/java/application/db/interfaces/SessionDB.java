package application.db.interfaces;

import application.model.Session;

import java.util.Optional;

public interface SessionDB {
    Optional<String> getSession(String sessionId);

    void addSession(Session session);

    void removeSession(String sessionId);

    int getSize();

    void clear();
}
