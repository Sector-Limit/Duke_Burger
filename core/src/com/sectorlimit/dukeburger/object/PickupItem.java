package com.sectorlimit.dukeburger.object;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;

public abstract class PickupItem {

	protected Body m_body;
	protected Fixture m_fixture;

	protected Texture m_texture;

	public PickupItem(Vector2 position, Texture texture) {
		m_texture = texture;
	}

	public Vector2 getOriginPosition() {
		return m_body != null ? m_body.getPosition() : new Vector2(0.0f, 0.0f);
	}

	public Vector2 getCenterPosition() {
		return new Vector2(getOriginPosition()).add(new Vector2(getSize()).scl(0.5f));
	}

	public abstract Vector2 getSize();

	public void setPhysicsProperties(Body body, Fixture fixture) {
		m_body = body;
		m_fixture = fixture;
		
		m_body.setActive(false);
	}

	public void setPosition(Vector2 position) {
		m_body.setTransform(position, 0);
	}

	public void pickup() {
		m_body.setActive(false);
	}

	public void drop(Vector2 velocity) {
		m_body.setLinearVelocity(0.0f, 0.0f);
		m_body.setAngularVelocity(0.0f);
		m_body.setActive(true);
		m_body.setLinearVelocity(velocity);
	}

	public void render(SpriteBatch spriteBatch) {
		Vector2 renderOrigin = new Vector2(getOriginPosition()).sub(new Vector2(getSize()).scl(0.5f));
		spriteBatch.draw(m_texture, renderOrigin.x, renderOrigin.y, 0.0f, 0.0f, m_texture.getWidth(), m_texture.getHeight(), 1.0f, 1.0f, (float) Math.toDegrees(m_body.getAngle()), 0, 0, m_texture.getWidth(), m_texture.getHeight(), false, false);
	}

}
