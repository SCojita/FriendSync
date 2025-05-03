package org.stefancojita.friendsync;

public class Noticia {

    // Declaración de atributos.
    private String autor;
    private String titulo;
    private String detalle;
    private String eventoId;

    // Declaración de constructor.
    public Noticia(String autor, String titulo, String detalle, String eventoId) {
        this.autor = autor;
        this.titulo = titulo;
        this.detalle = detalle;
        this.eventoId = eventoId;
    }

    // Métodos de acceso (getters).
    public String getEventoId() {
        return eventoId;
    }

    public String getAliasCreador() {
        return autor;
    }

    public String getNombreEvento() {
        return titulo;
    }

    public String getFechaLugar() {
        return detalle;
    }

}


