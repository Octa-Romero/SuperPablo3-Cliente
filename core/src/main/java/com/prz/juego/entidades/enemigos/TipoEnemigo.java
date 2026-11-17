package com.prz.juego.entidades.enemigos;

public enum TipoEnemigo {
    BICHO_MELEE("Enemigos/BichoMelee/bichoMelee.png", 70f, 110f),
    BICHO_RANGO("Enemigos/BichoRango/bichoRango.png", 70f, 110f);

    private final String RUTA_TEXTURA;
    private final float ANCHO;
    private final float ALTO;

    TipoEnemigo(String rutaTextura, float ancho, float alto) {
        RUTA_TEXTURA = rutaTextura;
        ANCHO = ancho;
        ALTO = alto;
    }

    public String getRutaTextura() {
        return RUTA_TEXTURA;
    }

    public float getAncho() {
        return ANCHO;
    }

    public float getAlto() {
        return ALTO;
    }
}
