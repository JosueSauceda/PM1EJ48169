package com.example.ejercicio4.tablas;

public class Transacciones {
    public static final String NameDatabase = "PM1-4";

    public static final String TbImagenes = "imagenes";

    public static final String id = "id";
    public static final String img = "img";


    public static final String CTBImagenes = "CREATE TABLE imagenes (id INTEGER PRIMARY KEY AUTOINCREMENT,"+
            "img BLOB)";

    public static final String GetImagenes = "SELECT * FROM " + Transacciones.TbImagenes;

    public static final String DPImagenes = "DROP TABLE IF EXIST imagenes";
}
