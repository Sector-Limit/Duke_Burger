package com.sectorlimit.dukeburger.projectile;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class BurgerProjectile extends Projectile {

	private Type m_type;

	public enum Type {
		Cheese,
		Lettuce,
		Tomato
	}

	public BurgerProjectile(Vector2 position, Type type, Texture texture) {
		super(position, texture);

		m_type = type;
	}

	public Type getType() {
		return m_type;
	}

}
