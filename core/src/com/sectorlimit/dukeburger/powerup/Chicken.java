package com.sectorlimit.dukeburger.powerup;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Chicken extends Powerup {

	private static final Vector2 CHICKEN_SIZE = new Vector2(16, 18);

	public Chicken(Vector2 position, Animation<TextureRegion> animation) {
		super(position, animation);
	}

	public Vector2 getSize() {
		return CHICKEN_SIZE;
	}

}
