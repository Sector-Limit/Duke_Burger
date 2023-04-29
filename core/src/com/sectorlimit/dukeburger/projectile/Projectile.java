package com.sectorlimit.dukeburger.projectile;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public abstract class Projectile {

	protected Vector2 m_position;
	protected Texture m_texture;

	public Projectile(Vector2 position, Texture texture) {
		m_position = position;
		m_texture = texture;
	}
	
	public Vector2 getPosition() {
		return m_position;
	}
	
	public void setPosition(Vector2 position) {
		m_position = position;
	}
	
	public void render(SpriteBatch spriteBatch) {
		spriteBatch.draw(m_texture, m_position.x, m_position.y, 0.0f, 0.0f, m_texture.getWidth(), m_texture.getHeight(), 1.0f, 1.0f, 0.0f, 0, 0, m_texture.getWidth(), m_texture.getHeight(), false, false);
	}

}
