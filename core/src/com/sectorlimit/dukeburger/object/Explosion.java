package com.sectorlimit.dukeburger.object;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Explosion {

	private Vector2 m_position;
	private Animation<TextureRegion> m_explosionAnimation;
	private float m_stateTime;

	public Explosion(Animation<TextureRegion> explosionAnimation) {
		m_explosionAnimation = explosionAnimation;
		m_position = new Vector2(100, 100);
	}

	public Vector2 getPosition() {
		return m_position;
	}

	public void render(SpriteBatch spriteBatch) {
		m_stateTime += Gdx.graphics.getDeltaTime();

		if(m_stateTime <= m_explosionAnimation.getAnimationDuration()) {
			spriteBatch.draw(m_explosionAnimation.getKeyFrame(m_stateTime, true), m_position.x, m_position.y);
		}
	}

}
