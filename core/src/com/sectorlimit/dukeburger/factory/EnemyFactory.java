package com.sectorlimit.dukeburger.factory;

import java.util.Vector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.sectorlimit.dukeburger.ProjectileSystem;
import com.sectorlimit.dukeburger.enemy.Enforcer;
import com.sectorlimit.dukeburger.enemy.Octa;
import com.sectorlimit.dukeburger.enemy.OctaBaby;
import com.sectorlimit.dukeburger.enemy.OctaBaby.Type;

public class EnemyFactory {

	private Texture m_octaBabySquishedTextures[];
	private Texture m_octaBabyWalkSheetTextures[];
	private Vector<Animation<TextureRegion>> m_octaBabyWalkAnimations;
	private Texture m_octaSheetTexture;
	private Texture m_octaDeadTexture;
	private TextureRegion m_octaRisingTextureRegion;
	private TextureRegion m_octaFallingTextureRegion;
	private Texture m_enforcerDeadTexture;
	private Texture m_enforcerSheetTexture;
	private Animation<TextureRegion> m_enforcerAnimation;

	private ProjectileSystem m_projectileSystem;
	private World m_world;

	private static final int NUMBER_OF_OCTA_BABY_WALK_FRAMES = 2;
	private static final int NUMBER_OF_OCTA_SPRITES = 2;
	private static final int NUMBER_OF_ENFORCER_FRAMES = 2;

	public EnemyFactory(ProjectileSystem projectileSystem, World world) {
		m_world = world;
		m_projectileSystem = projectileSystem;

		m_octaBabyWalkSheetTextures = new Texture[] {
			new Texture(Gdx.files.internal("sprites/octababy_walk.png")),
			new Texture(Gdx.files.internal("sprites/octababy_blue_walk.png")),
		};

		m_octaBabySquishedTextures = new Texture[] {
			new Texture(Gdx.files.internal("sprites/octababy_flat.png")),
			new Texture(Gdx.files.internal("sprites/octababy_blue_flat.png"))
		};

		m_octaBabyWalkAnimations = new Vector<Animation<TextureRegion>>();

		for(Type type : OctaBaby.Type.values()) {
			TextureRegion[][] octaBabyWalkTextureRegion = TextureRegion.split(m_octaBabyWalkSheetTextures[type.ordinal()], m_octaBabyWalkSheetTextures[type.ordinal()].getWidth() / NUMBER_OF_OCTA_BABY_WALK_FRAMES, m_octaBabyWalkSheetTextures[type.ordinal()].getHeight());
			TextureRegion[] octaBabyWalkFrames = new TextureRegion[NUMBER_OF_OCTA_BABY_WALK_FRAMES];

			for(int i = 0; i < NUMBER_OF_OCTA_BABY_WALK_FRAMES; i++) {
				octaBabyWalkFrames[i] = octaBabyWalkTextureRegion[0][i];
			}

			m_octaBabyWalkAnimations.add(new Animation<TextureRegion>(0.2f, octaBabyWalkFrames));
		}

		m_octaDeadTexture = new Texture(Gdx.files.internal("sprites/octa_object.png"));
		m_octaSheetTexture = new Texture(Gdx.files.internal("sprites/octa.png"));

		TextureRegion[][] octaTextureRegion = TextureRegion.split(m_octaSheetTexture, m_octaSheetTexture.getWidth() / NUMBER_OF_OCTA_SPRITES, m_octaSheetTexture.getHeight());
		m_octaRisingTextureRegion = octaTextureRegion[0][0];
		m_octaFallingTextureRegion = octaTextureRegion[0][1];

		m_enforcerDeadTexture = new Texture(Gdx.files.internal("sprites/enforcer_object.png"));
		m_enforcerSheetTexture = new Texture(Gdx.files.internal("sprites/enforcer.png"));

		TextureRegion[][] enforcerTextureRegions = TextureRegion.split(m_enforcerSheetTexture, m_enforcerSheetTexture.getWidth() / NUMBER_OF_ENFORCER_FRAMES, m_enforcerSheetTexture.getHeight());
		TextureRegion[] enforcerFrames = new TextureRegion[NUMBER_OF_ENFORCER_FRAMES];

		for (int i = 0; i < NUMBER_OF_ENFORCER_FRAMES; i++) {
			enforcerFrames[i] = enforcerTextureRegions[0][i];
		}

		m_enforcerAnimation = new Animation<TextureRegion>(0.5f, enforcerFrames);
	}

	public OctaBaby createOctaBaby(OctaBaby.Type type, Vector2 position) {
		OctaBaby octaBaby = new OctaBaby(type, m_octaBabyWalkAnimations.elementAt(type.ordinal()), m_octaBabySquishedTextures[type.ordinal()]);
		octaBaby.assignPhysics(m_world, position);
		return octaBaby;
	}

	public Octa createOcta(Vector2 position) {
		Octa octa = new Octa(m_octaRisingTextureRegion, m_octaFallingTextureRegion, m_octaDeadTexture);
		octa.assignPhysics(m_world, position);
		return octa;
	}

	public Enforcer createEnforcer(Vector2 position) {
		Enforcer enforcer = new Enforcer(m_projectileSystem, m_enforcerAnimation, m_enforcerDeadTexture);
		enforcer.assignPhysics(m_world, position);
		return enforcer;
	}

	public void dispose() {
		for(Type type : OctaBaby.Type.values()) {
			m_octaBabySquishedTextures[type.ordinal()].dispose();
			m_octaBabyWalkSheetTextures[type.ordinal()].dispose();
		}
	}

}
