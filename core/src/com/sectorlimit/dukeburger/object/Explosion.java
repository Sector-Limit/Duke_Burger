package com.sectorlimit.dukeburger.object;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Explosion {

	private Vector2 m_position;
	private Animation<TextureRegion> m_explosionAnimation;
	private float m_animationTimeElapsed;

	private Vector2 EXPLOSION_SIZE = new Vector2(50.0f, 50.0f);
	private float EXPLOSION_IMPACT_RADIUS = EXPLOSION_SIZE.x * 0.4f;

	public Explosion(Vector2 position, Animation<TextureRegion> explosionAnimation) {
		m_position = position;
		m_explosionAnimation = explosionAnimation;
		m_animationTimeElapsed = 0.0f;
	}

	public float getCollisionRadius() {
		return EXPLOSION_IMPACT_RADIUS;
	}

	public Vector2 getOriginPosition() {
		return m_position;
	}

	public Vector2 getSize() {
		return EXPLOSION_SIZE;
	}

	public float getImpactRadius() {
		return EXPLOSION_IMPACT_RADIUS;
	}

	public boolean isExpired() {
		return m_animationTimeElapsed > m_explosionAnimation.getAnimationDuration();
	}

	public void render(SpriteBatch spriteBatch) {
		if(!isExpired()) {
			Vector2 renderOrigin = new Vector2(getOriginPosition()).sub(new Vector2(getSize()).scl(0.5f));
			spriteBatch.draw(m_explosionAnimation.getKeyFrame(m_animationTimeElapsed, true), renderOrigin.x, renderOrigin.y);

			m_animationTimeElapsed += Gdx.graphics.getDeltaTime();
		}
	}

}
