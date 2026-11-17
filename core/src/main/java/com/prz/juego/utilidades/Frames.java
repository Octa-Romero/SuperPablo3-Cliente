package com.prz.juego.utilidades;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.prz.juego.recursos.GestorRecursos;

public class Frames {

    public static TextureRegion[] dividir(String ruta, int cantidad) {
        Texture textura = GestorRecursos.obtenerTextura(ruta);
        return TextureRegion.split(textura, textura.getWidth() / cantidad, textura.getHeight())[0];
    }

    public static Animation<TextureRegion> animacion(String ruta, int cantidad, float duracion) {
        Animation<TextureRegion> animacion = new Animation<>(duracion, dividir(ruta, cantidad));
        animacion.setPlayMode(Animation.PlayMode.LOOP);
        return animacion;
    }
}
