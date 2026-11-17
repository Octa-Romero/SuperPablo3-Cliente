package com.prz.juego.entidades.jugadores;

public enum Personajes {
    PABLO(
        50f, 100f, 10d,
        100f, 100f, -5f, -45f, 0f,
        "Personajes/Pablo/pablo_idle.png",
        "Hud/pablo_hud.png",
        "Personajes/Pablo/pablo.png",
        1080, 1079,
        new int[]{0},
        new int[]{1, 2, 3, 2},
        new int[]{4},
        new int[]{5},
        new int[]{6},
        0.12f),
    WALTER(
        50f, 80f, 5d,
        96f, 80f, -5f, -42f, 0f,
        "Personajes/Walter/walter_idle.png",
        "Hud/walter_hud.png",
        "Personajes/Walter/walter.png",
        540, 450,
        new int[]{0},
        new int[]{1, 2, 3, 4, 5, 3},
        new int[]{2},
        new int[]{1},
        new int[]{6},
        0.12f);

    private final float ANCHO_CAJA;
    private final float ALTO_CAJA;
    private final double VIDA;
    private final float ANCHO_SPRITE;
    private final float ALTO_SPRITE;
    private final float OFFSET_DERECHA;
    private final float OFFSET_IZQUIERDA;
    private final float OFFSET_Y;
    private final String RUTA_IDLE;
    private final String RUTA_HUD;
    private final String RUTA_SHEET;
    private final int ANCHO_FRAME;
    private final int ALTO_FRAME;
    private final int[] REPOSO;
    private final int[] CAMINAR;
    private final int[] SALTO;
    private final int[] CAIDA;
    private final int[] ATAQUE;
    private final float DURACION_FRAME;

    Personajes(
        float anchoCaja,
        float altoCaja,
        double vida,
        float anchoSprite,
        float altoSprite,
        float offsetDerecha,
        float offsetIzquierda,
        float offsetY,
        String rutaIdle,
        String rutaHud,
        String rutaSheet,
        int anchoFrame,
        int altoFrame,
        int[] reposo,
        int[] caminar,
        int[] salto,
        int[] caida,
        int[] ataque,
        float duracionFrame) {
        ANCHO_CAJA = anchoCaja;
        ALTO_CAJA = altoCaja;
        VIDA = vida;
        ANCHO_SPRITE = anchoSprite;
        ALTO_SPRITE = altoSprite;
        OFFSET_DERECHA = offsetDerecha;
        OFFSET_IZQUIERDA = offsetIzquierda;
        OFFSET_Y = offsetY;
        RUTA_IDLE = rutaIdle;
        RUTA_HUD = rutaHud;
        RUTA_SHEET = rutaSheet;
        ANCHO_FRAME = anchoFrame;
        ALTO_FRAME = altoFrame;
        REPOSO = reposo;
        CAMINAR = caminar;
        SALTO = salto;
        CAIDA = caida;
        ATAQUE = ataque;
        DURACION_FRAME = duracionFrame;
    }

    public float getAnchoCaja() {
        return ANCHO_CAJA;
    }

    public float getAltoCaja() {
        return ALTO_CAJA;
    }

    public double getVida() {
        return VIDA;
    }

    public float getAnchoSprite() {
        return ANCHO_SPRITE;
    }

    public float getAltoSprite() {
        return ALTO_SPRITE;
    }

    public float getOffsetDerecha() {
        return OFFSET_DERECHA;
    }

    public float getOffsetIzquierda() {
        return OFFSET_IZQUIERDA;
    }

    public float getOffsetY() {
        return OFFSET_Y;
    }

    public String getRutaIdle() {
        return RUTA_IDLE;
    }

    public String getRutaHud() {
        return RUTA_HUD;
    }

    public String getRutaSheet() {
        return RUTA_SHEET;
    }

    public int getAnchoFrame() {
        return ANCHO_FRAME;
    }

    public int getAltoFrame() {
        return ALTO_FRAME;
    }

    public int[] getReposo() {
        return REPOSO;
    }

    public int[] getCaminar() {
        return CAMINAR;
    }

    public int[] getSalto() {
        return SALTO;
    }

    public int[] getCaida() {
        return CAIDA;
    }

    public int[] getAtaque() {
        return ATAQUE;
    }

    public float getDuracionFrame() {
        return DURACION_FRAME;
    }
}
