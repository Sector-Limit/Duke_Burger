package com.sectorlimit.dukeburger.object;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Lava extends StaticObject {

	private static final Vector2 LAVA_SIZE = new Vector2(16.0f, 32.0f);

	public Lava(Vector2 position, Animation<TextureRegion> animation) {
		super(position, animation);
	}

	public Vector2 getSize() {
		return LAVA_SIZE;
	}

}
