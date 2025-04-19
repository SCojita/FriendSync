package org.stefancojita.friendsync;

public class Evento {

    private String titulo;
    private String fecha;
    private String lugar;

    public Evento() {} // Requerido por Firestore

    public Evento(String titulo, String fecha, String lugar) {
        this.titulo = titulo;
        this.fecha = fecha;
        this.lugar = lugar;
    }

    public String getTitulo() { return titulo; }
    public String getFecha() { return fecha; }
    public String getLugar() { return lugar; }

}

