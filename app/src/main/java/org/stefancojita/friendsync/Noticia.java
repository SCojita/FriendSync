package org.stefancojita.friendsync;

public class Noticia {

    private String aliasCreador;
    private String nombreEvento;
    private String fechaLugar;

    public Noticia(String aliasCreador, String nombreEvento, String fechaLugar) {
        this.aliasCreador = aliasCreador;
        this.nombreEvento = nombreEvento;
        this.fechaLugar = fechaLugar;
    }

    public String getAliasCreador() { return aliasCreador; }
    public String getNombreEvento() { return nombreEvento; }
    public String getFechaLugar() { return fechaLugar; }

}

