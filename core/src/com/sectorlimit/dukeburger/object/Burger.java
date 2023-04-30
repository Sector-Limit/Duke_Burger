package com.sectorlimit.dukeburger.object;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class Burger extends PickupItem {

	private static final Vector2 BURGER_SIZE = new Vector2(16, 13);

	public Burger(Texture texture) {
		super(texture);
	}

	public Vector2 getSize() {
		return BURGER_SIZE;
	}

	@Override
	public boolean isDestructible() {
		return false;
	}

}
