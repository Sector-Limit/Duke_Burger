package com.sectorlimit.dukeburger.factory;

import java.util.Arrays;
import java.util.Vector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.sectorlimit.dukeburger.powerup.Chicken;
import com.sectorlimit.dukeburger.powerup.Coin;
import com.sectorlimit.dukeburger.powerup.Cola;

public class PowerupsFactory {

	private Texture m_colaSpriteSheetTexture;
	private Texture m_chickenSpriteSheetTexture;
	private Texture m_coinSpriteSheetTexture;
	private Animation<TextureRegion> m_colaAnimation;
	private Animation<TextureRegion> m_chickenAnimation;
	private Animation<TextureRegion> m_coinAnimation;

	private Vector<Sound> m_colaConsumeSounds;
	private Sound m_chickenConsumeSound;
	private Sound m_coinConsumeSound;

	private static final int NUMBER_OF_COLA_FRAMES = 8;
	private static final int NUMBER_OF_CHICKEN_FRAMES = 2;
	private static final int NUMBER_OF_COIN_FRAMES = 20;

	public PowerupsFactory() {
		m_colaSpriteSheetTexture = new Texture(Gdx.files.internal("sprites/cola.png"));
		m_chickenSpriteSheetTexture = new Texture(Gdx.files.internal("sprites/chicken.png"));
		m_coinSpriteSheetTexture = new Texture(Gdx.files.internal("sprites/coin_animation.png"));

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

		TextureRegion[][] coinTextureRegion = TextureRegion.split(m_coinSpriteSheetTexture, m_coinSpriteSheetTexture.getWidth() / NUMBER_OF_COIN_FRAMES, m_coinSpriteSheetTexture.getHeight());
		TextureRegion[] coinFrames = new TextureRegion[NUMBER_OF_COIN_FRAMES];

		for (int i = 0; i < NUMBER_OF_COIN_FRAMES; i++) {
			coinFrames[i] = coinTextureRegion[0][i];
		}

		m_coinAnimation = new Animation<TextureRegion>(0.125f, coinFrames);

		m_colaConsumeSounds = new Vector<Sound>(Arrays.asList(
				Gdx.audio.newSound(Gdx.files.internal("sounds/Cola.wav")),
				Gdx.audio.newSound(Gdx.files.internal("sounds/Cola02.wav"))
		));

		m_chickenConsumeSound = Gdx.audio.newSound(Gdx.files.internal("sounds/Chicken.wav"));
		m_coinConsumeSound = Gdx.audio.newSound(Gdx.files.internal("sounds/Coin.wav"));
	}

	public Cola createCola(Vector2 position) {
		return new Cola(position, m_colaAnimation, m_colaConsumeSounds);
	}

	public Chicken createChicken(Vector2 position) {
		return new Chicken(position, m_chickenAnimation, m_chickenConsumeSound);
	}

	public Coin createCoin(Vector2 position) {
		return new Coin(position, m_coinAnimation, m_coinConsumeSound);
	}

	public void dispose() {
		m_colaSpriteSheetTexture.dispose();
		m_chickenSpriteSheetTexture.dispose();
		m_coinSpriteSheetTexture.dispose();

		for(Sound sound : m_colaConsumeSounds) {
			sound.dispose();
		}

		m_chickenConsumeSound.dispose();
		m_coinConsumeSound.dispose();
	}

}
