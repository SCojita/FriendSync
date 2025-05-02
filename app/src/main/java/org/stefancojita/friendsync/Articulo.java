package org.stefancojita.friendsync;

public class Articulo {

    private String titulo;
    private String descripcion;
    private int idArticulo;

    public Articulo(String titulo, String descripcion, int idArticulo) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.idArticulo = idArticulo;
    }

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
