package com.sectorlimit.dukeburger.powerup;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public abstract class Powerup {

	protected Vector2 m_position;
	protected int m_id;
	protected boolean m_consumed;
	protected float m_elapsedAnimationTime;
	protected Animation<TextureRegion> m_animation;

	public Powerup(Vector2 position, Animation<TextureRegion> animation) {
		this(-1, position, animation);
	}

	public Powerup(int id, Vector2 position, Animation<TextureRegion> animation) {
		m_id = id;
		m_position = position;
		m_animation = animation;
	}

	public boolean hasID() {
		return m_id > 0;
	}

	public int getID() {
		return m_id;
	}

	public boolean isOneTimeUse() {
		return true;
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

	public boolean isRotationFixed() {
		return false;
	}

	public boolean isConsumed() {
		return m_consumed;
	}

	public void consume() {
		m_consumed = true;
	}

	public void render(SpriteBatch spriteBatch) {
		float deltaTime = Gdx.graphics.getDeltaTime();

		m_elapsedAnimationTime += deltaTime;

		if(m_elapsedAnimationTime >= m_animation.getAnimationDuration()) {
			m_elapsedAnimationTime = m_elapsedAnimationTime % m_animation.getAnimationDuration();
		}

		Vector2 renderOrigin = new Vector2(getOriginPosition()).sub(new Vector2(getSize()).scl(0.5f));
		TextureRegion currentAnimationTextureRegion = m_animation.getKeyFrame(m_elapsedAnimationTime, true);
		spriteBatch.draw(currentAnimationTextureRegion, renderOrigin.x, renderOrigin.y);
	}

}
