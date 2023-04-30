package com.sectorlimit.dukeburger.enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public abstract class BasicEnemy extends Enemy {

	private Vector2 m_previousPosition;

	private static final float HORIZONTAL_VELOCITY = 2500.0f;

	public BasicEnemy() {
		m_previousPosition = null;
	}

	public void attack() {
		m_facingLeft = !m_facingLeft;
	}

	public void update() {
		float deltaTime = Gdx.graphics.getDeltaTime();

		if(m_previousPosition != null) {
			if(m_previousPosition.epsilonEquals(m_body.getPosition(), 0.01f)) {
				m_facingLeft = !m_facingLeft;
			}
		}

		m_body.setLinearVelocity(new Vector2((m_facingLeft ? -1.0f : 1.0f) * HORIZONTAL_VELOCITY, 0.0f).scl(deltaTime));

		m_previousPosition = new Vector2(m_body.getPosition());
	}
	
}
