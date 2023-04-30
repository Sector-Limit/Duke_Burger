package com.sectorlimit.dukeburger.factory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.sectorlimit.dukeburger.powerup.Chicken;
import com.sectorlimit.dukeburger.powerup.Cola;

public class PowerupsFactory {

	private Texture m_colaSpriteSheetTexture;
	private Texture m_chickenSpriteSheetTexture;
	private Animation<TextureRegion> m_colaAnimation;
	private Animation<TextureRegion> m_chickenAnimation;

	private static final int NUMBER_OF_COLA_FRAMES = 8;
	private static final int NUMBER_OF_CHICKEN_FRAMES = 2;

	public PowerupsFactory() {
		m_colaSpriteSheetTexture = new Texture(Gdx.files.internal("sprites/cola.png"));
		m_chickenSpriteSheetTexture = new Texture(Gdx.files.internal("sprites/chicken.png"));

		TextureRegion[][] colaTextureRegion = TextureRegion.split(m_colaSpriteSheetTexture, m_colaSpriteSheetTexture.getWidth() / NUMBER_OF_COLA_FRAMES, m_colaSpriteSheetTexture.getHeight());
		TextureRegion[] colaFrames = new TextureRegion[NUMBER_OF_COLA_FRAMES];

		for (int i = 0; i < NUMBER_OF_COLA_FRAMES; i++) {
			colaFrames[i] = colaTextureRegion[0][i];
		}

		m_colaAnimation = new Animation<TextureRegion>(0.05f, colaFrames);

		TextureRegion[][] chickenTextureRegion = TextureRegion.split(m_chickenSpriteSheetTexture, m_chickenSpriteSheetTexture.getWidth() / NUMBER_OF_CHICKEN_FRAMES, m_chickenSpriteSheetTexture.getHeight());
		TextureRegion[] chickenFrames = new TextureRegion[NUMBER_OF_CHICKEN_FRAMES];

		for (int i = 0; i < NUMBER_OF_CHICKEN_FRAMES; i++) {
			chickenFrames[i] = chickenTextureRegion[0][i];
		}

		m_chickenAnimation = new Animation<TextureRegion>(0.225f, chickenFrames);
	}

	public Cola createCola(Vector2 position) {
		return new Cola(position, m_colaAnimation);
	}

	public Chicken createChicken(Vector2 position) {
		return new Chicken(position, m_chickenAnimation);
	}

	public void dispose() {
		m_colaSpriteSheetTexture.dispose();
		m_chickenSpriteSheetTexture.dispose();
	}

}
