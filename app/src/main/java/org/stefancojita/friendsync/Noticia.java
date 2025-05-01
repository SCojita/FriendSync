package org.stefancojita.friendsync;

public class Noticia {

    private String autor;
    private String titulo;
    private String detalle;
    private String eventoId;

    public Noticia(String autor, String titulo, String detalle, String eventoId) {
        this.autor = autor;
        this.titulo = titulo;
        this.detalle = detalle;
        this.eventoId = eventoId;
    }

    // Getters.
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


