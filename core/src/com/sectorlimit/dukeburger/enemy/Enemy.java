package com.sectorlimit.dukeburger.enemy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public abstract class Enemy {

	protected Vector2 m_position;
	protected boolean m_facingLeft;
	protected boolean m_alive;

	public Enemy(Vector2 position) {
		m_position = position;
		m_facingLeft = false;
		m_alive = true;
	}

	public Vector2 getOriginPosition() {
		return m_position;
	}

	public Vector2 getCenterPosition() {
		return new Vector2(getOriginPosition()).add(new Vector2(getSize()).scl(0.5f));
	}

	public abstract Vector2 getSize();

	public boolean isFacingLeft() {
		return m_facingLeft;
	}

	public boolean isAlive() {
		return m_alive;
	}

	public void kill() {
		m_alive = false;
	}

	public abstract void render(SpriteBatch spriteBatch);

}
