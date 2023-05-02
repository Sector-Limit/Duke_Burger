package com.sectorlimit.dukeburger.projectile;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.sectorlimit.dukeburger.enemy.Enemy;

public class Fireball extends Projectile {

	private static final float FIREBALL_SPEED = 200.0f;
	private static final Vector2 FIREBALL_SIZE = new Vector2(14.0f, 14.0f);
	private static final float FIREBALL_RADIUS = 4.0f;

	public Fireball(Enemy source, Animation<TextureRegion> fireballAnimation) {
		super(source, fireballAnimation);
	}

	public Vector2 getSize() {
		return FIREBALL_SIZE;
	}

	public float getRadius() {
		return FIREBALL_RADIUS;
	}

	public float getSpeed() {
		return FIREBALL_SPEED;
	}

}
