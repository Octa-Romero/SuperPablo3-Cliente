package com.prz.juego.utilidades;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public enum Musica {
    MENU("Musicas/menu.MP3"),
    HUB("Musicas/hub.MP3"),
    NIVEL1("Musicas/nivel1.MP3"),
    NIVEL2("Musicas/nivel2.MP3"),
    NIVEL3("Musicas/nivel3.MP3"),
    NIVEL4("Musicas/nivel4.MP3"),
    NIVEL5("Musicas/nivel5.MP3"),
    NIVEL6("Musicas/nivel6.MP3"),
    NIVEL7("Musicas/nivel7.MP3"),
    CREDITOS("Musicas/creditos.MP3");

    private final String NOMBRE_ARCHIVO;
    private Music music;
    private static Musica actual;
    private static float volumenGlobal = 0.5f;
    private static boolean audioActivo = true;

    private Musica(String NOMBRE_ARCHIVO) {
        this.NOMBRE_ARCHIVO = NOMBRE_ARCHIVO;
    }

    public synchronized void sonar() {
        if (!audioActivo) return;
        try {
            // Volver si ya está sonando la misma música o si es null
            if (actual == this && music != null && music.isPlaying()) {
                return;
            }
            parar();
            // Cargar si todavía no fue cargada
            if (music == null) {
                music = Gdx.audio.newMusic(Gdx.files.internal(NOMBRE_ARCHIVO));
            }
            music.setLooping(true);
            music.setVolume(volumenGlobal);
            music.play();
            actual = this;
        } catch (Throwable t) {
            // El sistema de audio puede estar disposed o corrompido (BufferOverflowException, etc.)
            audioActivo = false;
        }
    }

    public static synchronized void parar() {
        if (!audioActivo) return;
        try {
            if (actual != null && actual.music != null) {
                actual.music.stop();
                actual = null;
            }
        } catch (Throwable t) {
            // El sistema de audio puede estar disposed
            audioActivo = false;
        }
    }

    public static synchronized void setVolumen(float volumen) {
        volumenGlobal = Math.max(0f, Math.min(1f, volumen));
        if (!audioActivo) return;
        try {
            if (actual != null && actual.music != null) {
                actual.music.setVolume(volumenGlobal);
            }
        } catch (Throwable t) {
            // El sistema de audio puede estar disposed
            audioActivo = false;
        }
    }

    public static float getVolumen() {
        return volumenGlobal;
    }

    public static synchronized void reproducirPorNombre(String nombre) {
        if (!audioActivo) return;
        for (Musica musica : values()) {
            if (musica.name().equals(nombre)) {
                musica.sonar();
                return;
            }
        }
    }

    public static synchronized void dispose() {
        audioActivo = false;
        try {
            for (Musica musica : values()) {
                if (musica.music != null) {
                    musica.music.dispose();
                    musica.music = null;
                }
            }
            actual = null;
        } catch (Throwable t) {
            // El sistema de audio puede estar ya disposed
        }
    }
}
