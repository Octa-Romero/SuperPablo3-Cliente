package com.prz.juego.entidades.enemigos;

public enum TipoEnemigo {
    BICHO_MELEE("Enemigos/BichoMelee/bichoMelee.png", 70f, 110f, 100f, 10f),
    BICHO_RANGO("Enemigos/BichoRango/bichoRango.png", 70f, 110f, 0f, 0f),
    MINIJEFE_MELEE("Enemigos/BichoMelee/bichoMelee.png", 105f, 165f, 150f, 15f),
    MINIJEFE_RANGO("Enemigos/BichoRango/bichoRango.png", 105f, 165f, 0f, 0f);

    private final String RUTA_TEXTURA;
    private final float ANCHO;
    private final float ALTO;
    private final float ANCHO_ATAQUE;
    private final float ALTO_ATAQUE;

    TipoEnemigo(String rutaTextura, float ancho, float alto, float anchoAtaque, float altoAtaque) {
        RUTA_TEXTURA = rutaTextura;
        ANCHO = ancho;
        ALTO = alto;
        ANCHO_ATAQUE = anchoAtaque;
        ALTO_ATAQUE = altoAtaque;
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

    public float getAnchoAtaque() {
        return ANCHO_ATAQUE;
    }

    public float getAltoAtaque() {
        return ALTO_ATAQUE;
    }
}
