package org.stefancojita.friendsync;

public class Evento {

    // Declaración de atributos.
    private String titulo;
    private String fecha;
    private String hora;
    private String lugar;

    // Declaración de constructor por defecto (lo necesita Firebase).
    public Evento() {}

    // Declaración de constructor.
    public Evento(String titulo, String fecha, String hora, String lugar) {
        this.titulo = titulo;
        this.fecha = fecha;
        this.hora = hora;
        this.lugar = lugar;
    }

    // Métodos de acceso (getters).
    public String getTitulo() { return titulo; }
    public String getFecha() { return fecha; }
    public String getHora() {
        return hora;
    }
    public String getLugar() { return lugar; }

}

