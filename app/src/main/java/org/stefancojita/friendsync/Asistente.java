package org.stefancojita.friendsync;

public class Asistente {

    private String id;
    private String alias;
    private String email;

    public Asistente() {}

    public Asistente(String id, String alias, String email) {
        this.id = id;
        this.alias = alias;
        this.email = email;
    }

    public String getId() { return id; }
    public String getAlias() { return alias; }
    public String getEmail() { return email; }

}


