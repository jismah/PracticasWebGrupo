package org.jismah.entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Session {
    @Id
    private String sessionId;
    private String username;
    public Session() {}

    public Session(String sessionId, String username) {
        this.username = username;
        this.sessionId = sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
