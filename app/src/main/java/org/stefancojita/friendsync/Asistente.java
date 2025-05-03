package org.stefancojita.friendsync;

public class Asistente {

    // Declaración de atributos.
    private String id;
    private String alias;
    private String email;

    // Declaración de constructor por defecto (necesario para Firebase).
    public Asistente() {}

    // Declaración de constructor.
    public Asistente(String id, String alias, String email) {
        this.id = id;
        this.alias = alias;
        this.email = email;
    }

    // Métodos de acceso (getters).
    public String getId() { return id; }
    public String getAlias() { return alias; }
    public String getEmail() { return email; }

}