package com.sectorlimit.dukeburger.factory;

import java.util.Arrays;
import java.util.Vector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.sectorlimit.dukeburger.object.Explosion;

public class ExplosionFactory {

	private Animation<TextureRegion> m_explosionAnimation;
	private Texture m_explosionSpriteSheet;

	private Vector<Sound> m_explosionSounds;

	private static final int EXPLOSION_SPRITE_SHEET_ROWS = 1;
	private static final int EXPLOSION_SPRITE_SHEET_COLUMNS = 6;

	public ExplosionFactory() {
		m_explosionSpriteSheet = new Texture(Gdx.files.internal("sprites/exp.png"));
		TextureRegion[][] explosionTextureRegion = TextureRegion.split(m_explosionSpriteSheet, m_explosionSpriteSheet.getWidth() / EXPLOSION_SPRITE_SHEET_COLUMNS, m_explosionSpriteSheet.getHeight() / EXPLOSION_SPRITE_SHEET_ROWS);
		TextureRegion[] explosionFrames = new TextureRegion[EXPLOSION_SPRITE_SHEET_ROWS * EXPLOSION_SPRITE_SHEET_COLUMNS];
		int explosionFrameIndex = 0;

		for (int i = 0; i < EXPLOSION_SPRITE_SHEET_ROWS; i++) {
			for (int j = 0; j < EXPLOSION_SPRITE_SHEET_COLUMNS; j++) {
				explosionFrames[explosionFrameIndex++] = explosionTextureRegion[i][j];
			}
		}

		m_explosionAnimation = new Animation<TextureRegion>(0.07f, explosionFrames);

		m_explosionSounds = new Vector<Sound>(Arrays.asList(
			Gdx.audio.newSound(Gdx.files.internal("sounds/Explosion.wav")),
			Gdx.audio.newSound(Gdx.files.internal("sounds/Explosion02.wav"))
		));
	}

	public Explosion createExplosion(Vector2 position) {
		m_explosionSounds.elementAt((int) (Math.random() * 2.0)).play(0.6f);

		Explosion explosion = new Explosion(position, m_explosionAnimation);
		return explosion;
	}

	public void dispose() {
		m_explosionSpriteSheet.dispose();
	}

}
