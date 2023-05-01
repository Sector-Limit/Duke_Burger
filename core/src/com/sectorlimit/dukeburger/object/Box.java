package com.sectorlimit.dukeburger.object;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class Box extends PickupItem {

	private static final Vector2 BOX_SIZE = new Vector2(16, 16);

	public Box(Texture texture, Sound destroySound) {
		super(texture, destroySound);
	}

	public Vector2 getSize() {
		return BOX_SIZE;
	}

	public boolean isRotationFixed() {
		return true;
	}

}
