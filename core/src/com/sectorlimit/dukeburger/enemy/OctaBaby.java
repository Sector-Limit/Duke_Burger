package com.sectorlimit.dukeburger.enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class OctaBaby extends Enemy {

	private boolean m_squished;

	private Texture m_octaBabySquishedTexture;
	private Animation<TextureRegion> m_octaBabyWalkAnimation;
	private float m_elapsedAnimationTime;

	private static final Vector2 OCTA_BABY_SIZE = new Vector2(16, 16);

	public OctaBaby(Vector2 position, Animation<TextureRegion> octaBabyWalkAnimation, Texture octaBabySquishedTexture) {
		m_squished = false;

		m_octaBabySquishedTexture = octaBabySquishedTexture;
		m_octaBabyWalkAnimation = octaBabyWalkAnimation;
	}

	public Vector2 getSize() {
		return OCTA_BABY_SIZE;
	}

	public boolean isSquished() {
		return m_squished;
	}

	public boolean squish() {
		if(m_squished) {
			return false;
		}

		m_squished = true;

		return true;
	}

	public void render(SpriteBatch spriteBatch) {
		if(!isAlive()) {
			// TODO: add intermediary dying state
			return;
		}

		float deltaTime = Gdx.graphics.getDeltaTime();
		Texture currentTexture = null;
		TextureRegion currentTextureRegion = null;
	
		if(m_squished) {
			currentTexture = m_octaBabySquishedTexture;
		}
		else {
			m_elapsedAnimationTime += deltaTime;
			
			if(m_elapsedAnimationTime >= m_octaBabyWalkAnimation.getAnimationDuration()) {
				m_elapsedAnimationTime = m_elapsedAnimationTime % m_octaBabyWalkAnimation.getAnimationDuration();
			}

			currentTextureRegion = m_octaBabyWalkAnimation.getKeyFrame(m_elapsedAnimationTime, true);
		}

		Vector2 renderOrigin = new Vector2(getOriginPosition()).sub(new Vector2(getSize()).scl(0.5f));

		if(currentTexture != null) {
			spriteBatch.draw(currentTexture, renderOrigin.x, renderOrigin.y, getSize().x / 2, getSize().y / 2, currentTexture.getWidth(), currentTexture.getHeight(), 1.0f, 1.0f, 0.0f, 0, 0, currentTexture.getWidth(), currentTexture.getHeight(), m_facingLeft, false);
		}
		else if(currentTextureRegion != null) {
			if(m_facingLeft) {
				currentTextureRegion.flip(true, false);
			}
	
			spriteBatch.draw(currentTextureRegion, renderOrigin.x, renderOrigin.y);
	
			if(m_facingLeft) {
				currentTextureRegion.flip(true, false);
			}
		}
	}

}
