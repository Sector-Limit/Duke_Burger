package com.sectorlimit.dukeburger.object;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public abstract class StaticObject {

	protected Vector2 m_position;

	protected float m_elapsedAnimationTime;
	protected Texture m_texture;
	protected Animation<TextureRegion> m_animation;

	public StaticObject(Vector2 position) {
		m_position = position;
	}

	public StaticObject(Vector2 position, Texture texture) {
		m_position = position;
		m_texture = texture;
	}

	public StaticObject(Vector2 position, Animation<TextureRegion> animation) {
		m_position = position;
		m_animation = animation;
	}

	public Vector2 getOriginPosition() {
		return m_position;
	}

	public Vector2 getCenterPosition() {
		return new Vector2(getOriginPosition()).add(new Vector2(getSize()).scl(0.5f));
	}

	public abstract Vector2 getSize();

	public void setPosition(Vector2 position) {
		m_position = position;
	}

	public void render(SpriteBatch spriteBatch) {
		Vector2 renderOrigin = new Vector2(getOriginPosition()).sub(new Vector2(getSize()).scl(0.5f));

		if(m_texture != null) {
			spriteBatch.draw(m_texture, renderOrigin.x, renderOrigin.y, 0.0f, 0.0f, m_texture.getWidth(), m_texture.getHeight(), 1.0f, 1.0f, 0.0f, 0, 0, m_texture.getWidth(), m_texture.getHeight(), false, false);			
		}

		if(m_animation != null) {
			float deltaTime = Gdx.graphics.getDeltaTime();
	
			m_elapsedAnimationTime += deltaTime;
	
			if(m_elapsedAnimationTime >= m_animation.getAnimationDuration()) {
				m_elapsedAnimationTime = m_elapsedAnimationTime % m_animation.getAnimationDuration();
			}
	
			TextureRegion currentAnimationTextureRegion = m_animation.getKeyFrame(m_elapsedAnimationTime, true);
			spriteBatch.draw(currentAnimationTextureRegion, renderOrigin.x, renderOrigin.y);
		}
	}

}
