package com.sectorlimit.dukeburger.factory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.sectorlimit.dukeburger.enemy.Enemy;
import com.sectorlimit.dukeburger.projectile.Fireball;

public class ProjectileFactory {

	private World m_world;

	private Texture m_fireballSheetTexture;
	private Animation<TextureRegion> m_fireballAnimation;

	private final static int NUMBER_OF_FIREBALL_FRAMES = 4;

	public ProjectileFactory(World world) {
		m_world = world;

		m_fireballSheetTexture = new Texture(Gdx.files.internal("sprites/fireball.png"));

		TextureRegion[][] fireballTextureRegions = TextureRegion.split(m_fireballSheetTexture, m_fireballSheetTexture.getWidth() / NUMBER_OF_FIREBALL_FRAMES, m_fireballSheetTexture.getHeight());
		TextureRegion[] fireballFrames = new TextureRegion[NUMBER_OF_FIREBALL_FRAMES];

		for (int i = 0; i < NUMBER_OF_FIREBALL_FRAMES; i++) {
			fireballFrames[i] = fireballTextureRegions[0][i];
		}

		m_fireballAnimation = new Animation<TextureRegion>(0.15f, fireballFrames);
	}

	public Fireball createFireball(Enemy source, Vector2 position, Vector2 direction) {
		Fireball fireball = new Fireball(source, m_fireballAnimation);
		fireball.assignPhysics(m_world, position, direction);
		return fireball;
	}

	public void dispose() {
		m_fireballSheetTexture.dispose();
	}

}
