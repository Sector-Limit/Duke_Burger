package com.sectorlimit.dukeburger.factory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.sectorlimit.dukeburger.object.Door;
import com.sectorlimit.dukeburger.object.FinishText;
import com.sectorlimit.dukeburger.object.Lava;
import com.sectorlimit.dukeburger.object.PigCop;
import com.sectorlimit.dukeburger.object.Restaurant;

public class StaticObjectFactory {

	private Texture m_restaurantSheetTexture;
	private Texture m_lavaSheetTexture;
	private Texture m_doorSheetTexture;
	private Texture[] m_finishTextTextures;
	private Texture[] m_pigCopTextures;
	private Animation<TextureRegion> m_restaurantAnimation;
	private Animation<TextureRegion> m_lavaAnimation;
	private TextureRegion m_doorClosedTextureRegion;
	private TextureRegion m_doorOpenTextureRegion;

	private World m_world;

	private Sound m_doorSound;

	private static final int NUMBER_OF_RESTAURANT_FRAMES = 2;
	private static final int NUMBER_OF_LAVA_FRAMES = 16;
	private static final int NUMBER_OF_DOOR_FRAMES = 2;
	private static final int NUMBER_OF_PIGCOPS = 2;
	private static final int FINISH_TEXT_COUNT = 2;

	public StaticObjectFactory(World world) {
		m_world = world;

		m_restaurantSheetTexture = new Texture(Gdx.files.internal("sprites/duke_rest.png"));

		TextureRegion[][] restaurantTextureRegion = TextureRegion.split(m_restaurantSheetTexture, m_restaurantSheetTexture.getWidth() / NUMBER_OF_RESTAURANT_FRAMES, m_restaurantSheetTexture.getHeight());
		TextureRegion[] restaurantFrames = new TextureRegion[NUMBER_OF_RESTAURANT_FRAMES];

		for (int i = 0; i < NUMBER_OF_RESTAURANT_FRAMES; i++) {
			restaurantFrames[i] = restaurantTextureRegion[0][i];
		}

		m_restaurantAnimation = new Animation<TextureRegion>(1.0f, restaurantFrames);

		m_lavaSheetTexture = new Texture(Gdx.files.internal("sprites/lava_animation.png"));

		TextureRegion[][] lavaTextureRegion = TextureRegion.split(m_lavaSheetTexture, m_lavaSheetTexture.getWidth() / NUMBER_OF_LAVA_FRAMES, m_lavaSheetTexture.getHeight());
		TextureRegion[] lavaFrames = new TextureRegion[NUMBER_OF_LAVA_FRAMES];

		for (int i = 0; i < NUMBER_OF_LAVA_FRAMES; i++) {
			lavaFrames[i] = lavaTextureRegion[0][i];
		}

		m_lavaAnimation = new Animation<TextureRegion>(0.07f, lavaFrames);

		m_doorSheetTexture = new Texture(Gdx.files.internal("sprites/door.png"));

		TextureRegion[][] doorTextureRegions = TextureRegion.split(m_doorSheetTexture, m_doorSheetTexture.getWidth() / NUMBER_OF_DOOR_FRAMES, m_doorSheetTexture.getHeight());

		m_doorClosedTextureRegion = doorTextureRegions[0][0];
		m_doorOpenTextureRegion = doorTextureRegions[0][1];

		m_finishTextTextures = new Texture[FINISH_TEXT_COUNT];

		for(int i = 0; i < 2; i++) {
			m_finishTextTextures[i] = new Texture(Gdx.files.internal("sprites/finish_text_" + (i + 1) + ".png"));
		}

		m_pigCopTextures = new Texture[NUMBER_OF_PIGCOPS];

		for(int i = 0; i < 2; i++) {
			m_pigCopTextures[i] = new Texture(Gdx.files.internal("sprites/pigcop_" + (i + 1) + ".png"));
		}

		m_doorSound = Gdx.audio.newSound(Gdx.files.internal("sounds/DoorOpen.wav"));
	}

	public Restaurant createRestaurant(Vector2 position) {
		return new Restaurant(position, m_restaurantAnimation);
	}

	public Lava createLava(Vector2 position) {
		return new Lava(position, m_lavaAnimation);
	}

	public Door createDoor(Vector2 position) {
		Door door = new Door(position, m_doorClosedTextureRegion, m_doorOpenTextureRegion, m_doorSound);
		door.assignPhysics(m_world, position);
		return door;
	}

	public FinishText createFinishText(int type, Vector2 position) {
		if(type < 1 || type > FINISH_TEXT_COUNT) {
			return null;
		}

		return new FinishText(position, m_finishTextTextures[type - 1]);
	}

	public PigCop createPigCop(int type, Vector2 position) {
		if(type < 1 || type > NUMBER_OF_PIGCOPS) {
			return null;
		}

		return new PigCop(type, position, m_pigCopTextures[type - 1]);
	}

	public void dispose() {
		m_restaurantSheetTexture.dispose();
	}

}
