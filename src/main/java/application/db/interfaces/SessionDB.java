package application.db.interfaces;

import application.model.Session;

public interface SessionDB {
    String getSession(String sessionId);

    void addSession(Session session);

    void removeSession(String sessionId);

    int getSize();

    void clear();
}
