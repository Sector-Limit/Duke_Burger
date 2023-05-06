package com.sectorlimit.dukeburger.object;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.sectorlimit.dukeburger.CollisionCategories;

public class Burger extends PickupItem {

	private static final Vector2 BURGER_SIZE = new Vector2(16, 13);

	public Burger(Texture texture, Sound impactSound) {
		super(texture, null, impactSound);
	}

	@Override
	public Vector2 getSize() {
		return BURGER_SIZE;
	}

	@Override
	public boolean shouldTossHorizontal() {
		return false;
	}

	@Override
	public short getCollisionCategory() {
		return CollisionCategories.BURGER;
	}

	@Override
	public short getCollisionMask() {
		return CollisionCategories.GROUND | CollisionCategories.DOOR;
	}

	@Override
	public float getFriction() {
		return 0.9f;
	}

	@Override
	public boolean isDestructible() {
		return false;
	}

	@Override
	public void toss(boolean tossLeft) {
		toss(tossLeft, new Vector2(140.0f, 160.0f));
	}

}
