package com.sectorlimit.dukeburger.object;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Restaurant extends StaticObject {

	private static final Vector2 RESTAURANT_SIZE = new Vector2(86.0f, 73.0f);

	public Restaurant(Vector2 position, Animation<TextureRegion> animation) {
		super(position, animation);
	}

	public Vector2 getSize() {
		return RESTAURANT_SIZE;
	}

}
