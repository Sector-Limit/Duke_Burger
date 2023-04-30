package com.sectorlimit.dukeburger.factory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.sectorlimit.dukeburger.enemy.OctaBaby;

public class EnemyFactory {

	private Texture m_octaBabySquishedTexture;
	private Texture m_octaBabyWalkSheetTexture;
	private Animation<TextureRegion> m_octaBabyWalkAnimation;

	private static final int NUMBER_OF_OCTA_BABY_WALK_FRAMES = 2;

	public EnemyFactory() {
		m_octaBabyWalkSheetTexture = new Texture(Gdx.files.internal("sprites/octababy_walk.png"));
		m_octaBabySquishedTexture = new Texture(Gdx.files.internal("sprites/octababy_flat.png"));

		TextureRegion[][] octaBabyWalkTextureRegion = TextureRegion.split(m_octaBabyWalkSheetTexture, m_octaBabyWalkSheetTexture.getWidth() / NUMBER_OF_OCTA_BABY_WALK_FRAMES, m_octaBabyWalkSheetTexture.getHeight());
		TextureRegion[] octaBabyWalkFrames = new TextureRegion[NUMBER_OF_OCTA_BABY_WALK_FRAMES];

		for (int i = 0; i < NUMBER_OF_OCTA_BABY_WALK_FRAMES; i++) {
			octaBabyWalkFrames[i] = octaBabyWalkTextureRegion[0][i];
		}

		m_octaBabyWalkAnimation = new Animation<TextureRegion>(0.2f, octaBabyWalkFrames);
	}

	public OctaBaby createOctaBaby(Vector2 position) {
		return new OctaBaby(position, m_octaBabyWalkAnimation, m_octaBabySquishedTexture);
	}

	public void dispose() {
		m_octaBabySquishedTexture.dispose();
		m_octaBabyWalkSheetTexture.dispose();
	}

}
