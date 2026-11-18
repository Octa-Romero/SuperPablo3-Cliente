package com.prz.juego.entidades.enemigos;

public enum Enemigos {
    CULTISTA(
        "Enemigos/cultista_idle.png", 200f, 240f, -50f, -50f, -25f,
        "Enemigos/cultista.png", 200, 200,
        new int[]{0,1,2,3,4}, new int[]{5,6,7,8,9,10}, null, null, new int[]{12,13,14,15}, new int[]{11}, 0.2f),
    MINIJEFE_CULTISTA(
        "Enemigos/cultista_idle.png", 200f*1.5f, 240f*1.5f, -50f*1.5f, -50f*1.5f, -25f*1.5f,
        "Enemigos/cultista.png", 200, 200,
        new int[]{0,1,2,3,4}, new int[]{5,6,7,8,9,10}, null, null, new int[]{12,13,14,15}, new int[]{11}, 0.2f),
    DEMONIO(
        "Enemigos/demonio_idle.png", 400f, 270f, -150f, -150f, 0f,
        "Enemigos/demonio.png", 288, 160,
        new int[]{0,1,2,3,4,5}, new int[]{6,7,8,9,10,11,12,13,14,15,16,17,18}, null, null, new int[]{27,28,29,30,31,32}, new int[]{19,20,21,22,23,24,25,26}, 0.1f),
    MINIJEFE_DEMONIO(
        "Enemigos/demonio_idle.png", 400f*1.5f, 270f*1.5f, -150f*1.5f, -150f*1.5f, 0f*1.5f,
        "Enemigos/demonio.png", 288, 160,
        new int[]{0,1,2,3,4,5}, new int[]{6,7,8,9,10,11,12,13,14,15,16,17,18}, null, null, new int[]{27,28,29,30,31,32}, new int[]{19,20,21,22,23,24,25,26}, 0.1f),
    ARQUERO(
        "Enemigos/arquero_idle.png", 110f, 150f, -20f, -40f, 0f,
            "Enemigos/arquero.png", 64, 64,
            new int[]{0,1,2,3,4}, new int[]{22,23,24,25,26,27,28,29}, null, null, new int[]{19,20,21}, new int[]{11,12,13,14,15,16,17,18}, 0.12f),
    MINIJEFE_ARQUERO(
        "Enemigos/arquero_idle.png", 110f*1.5f, 150f*1.5f, -20f*1.5f, -40f*1.5f, 0f*1.5f,
            "Enemigos/arquero.png", 64, 64,
            new int[]{0,1,2,3,4}, new int[]{22,23,24,25,26,27,28,29}, null, null, new int[]{19,20,21}, new int[]{11,12,13,14,15,16,17,18}, 0.12f);


    private final String RUTA_TEXTURA;
    private final float ANCHO_SPRITE;
    private final float ALTO_SPRITE;
    private final float OFFSET_DERECHA;
    private final float OFFSET_IZQUIERDA;
    private final float OFFSET_Y;
    private final String RUTA_SHEET;
    private final int ANCHO_FRAME;
    private final int ALTO_FRAME;
    private final int[] REPOSO;
    private final int[] CAMINAR;
    private final int[] SALTO;
    private final int[] CAIDA;
    private final int[] ATAQUE;
    private final int[] PREPARAR;
    private final float DURACION_FRAME;

    Enemigos(
        String rutaTextura,
        float anchoSprite,
        float altoSprite,
        float offsetDerecha,
        float offsetIzquierda,
        float offsetY,
        String rutaSheet,
        int anchoFrame,
        int altoFrame,
        int[] reposo,
        int[] caminar,
        int[] salto,
        int[] caida,
        int[] ataque,
        int[] preparar,
        float duracionFrame) {
        RUTA_TEXTURA = rutaTextura;
        ANCHO_SPRITE = anchoSprite;
        ALTO_SPRITE = altoSprite;
        OFFSET_DERECHA = offsetDerecha;
        OFFSET_IZQUIERDA = offsetIzquierda;
        OFFSET_Y = offsetY;
        RUTA_SHEET = rutaSheet;
        ANCHO_FRAME = anchoFrame;
        ALTO_FRAME = altoFrame;
        REPOSO = reposo;
        CAMINAR = caminar;
        SALTO = salto;
        CAIDA = caida;
        ATAQUE = ataque;
        PREPARAR = preparar;
        DURACION_FRAME = duracionFrame;
    }

    public boolean tieneSpritesheet() {
        return ANCHO_FRAME > 0 && ALTO_FRAME > 0;
    }

    public String getRutaTextura() {
        return RUTA_TEXTURA;
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

    public int[] getPreparar() {
        return PREPARAR;
    }

    public float getDuracionFrame() {
        return DURACION_FRAME;
    }
}
