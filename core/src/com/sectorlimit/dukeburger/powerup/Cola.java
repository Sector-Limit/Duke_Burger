package com.sectorlimit.dukeburger.powerup;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Cola extends Powerup {

	private static final Vector2 COLA_SIZE = new Vector2(16, 16);

	public Cola(Vector2 position, Animation<TextureRegion> animation) {
		super(position, animation);
	}

	public Vector2 getSize() {
		return COLA_SIZE;
	}

}
