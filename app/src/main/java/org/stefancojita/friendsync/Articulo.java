package org.stefancojita.friendsync;

public class Articulo {

    // Declaración de atributos.
    private String titulo;
    private String descripcion;
    private int idArticulo;

    // Declaración de constructor.
    public Articulo(String titulo, String descripcion, int idArticulo) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.idArticulo = idArticulo;
    }

    // Métodos de acceso (getters).
    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getIdArticulo() {
        return idArticulo;
    }
}
