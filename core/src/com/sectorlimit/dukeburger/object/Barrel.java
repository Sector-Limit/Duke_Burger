package com.sectorlimit.dukeburger.object;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class Barrel extends PickupItem {

	private static final Vector2 BARREL_SIZE = new Vector2(16, 16);

	public Barrel(Texture texture) {
		super(texture);
	}

	public Vector2 getSize() {
		return BARREL_SIZE;
	}

}
