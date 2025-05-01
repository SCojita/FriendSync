package org.stefancojita.friendsync;

public class Evento {

    private String titulo;
    private String fecha;
    private String hora;
    private String lugar;

    public Evento() {}

    public Evento(String titulo, String fecha, String hora, String lugar) {
        this.titulo = titulo;
        this.fecha = fecha;
        this.hora = hora;
        this.lugar = lugar;
    }

    public String getTitulo() { return titulo; }
    public String getFecha() { return fecha; }
    public String getHora() {
        return hora;
    }
    public String getLugar() { return lugar; }


}

