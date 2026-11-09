package com.prz.juego.sistemas;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;

public class Colisiones {

	private final ArrayList<Rectangle> colisiones;

	public Colisiones(TiledMap tiledMap) {
		colisiones = new ArrayList<>();
		cargarColisiones(tiledMap);
	}

	private void cargarColisiones(TiledMap tiledMap) {
		for (MapObject object : tiledMap.getLayers().get("colisiones").getObjects()) {
			if (object instanceof RectangleMapObject) {
				Rectangle rect = ((RectangleMapObject) object).getRectangle();
				colisiones.add(rect);
			}
		}
	}

	public void dibujarHitboxes(ShapeRenderer shapeRenderer) {
		shapeRenderer.setColor(Color.BLUE);
		for (Rectangle colision : colisiones) {
			shapeRenderer.rect(colision.x, colision.y, colision.width, colision.height);
		}
	}
}
