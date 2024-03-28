package application.db.memoryDB;

import application.db.interfaces.SessionDB;
import application.model.Session;
import application.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class MemSessionDB implements SessionDB {
    private Map<String, String> sessions = new HashMap<>();
    private final Logger log = LoggerFactory.getLogger(MemSessionDB.class);

    public String getSession(String sessionId){
        return sessions.get(sessionId);
    }

    public int getSize(){
        return sessions.size();
    }

    public void addSession(Session session) {
        sessions.put(session.getSessionId(), session.getUserId());
        log.debug("New Session : " + session.getSessionId());
    }

    public void removeSession(String sessionId){
        if(sessions.remove(sessionId )!= null) log.info("Removing log-out Session : " + sessionId);
    }

    public void clear(){
        sessions = new HashMap<>();
    }

}

