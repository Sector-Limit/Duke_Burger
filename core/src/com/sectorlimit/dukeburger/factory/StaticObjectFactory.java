package com.sectorlimit.dukeburger.factory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.sectorlimit.dukeburger.object.Door;
import com.sectorlimit.dukeburger.object.Restaurant;

public class StaticObjectFactory {

	private Texture m_restaurantSheetTexture;
	private Texture m_doorSheetTexture;
	private Animation<TextureRegion> m_restaurantAnimation;
	private TextureRegion m_doorClosedTextureRegion;
	private TextureRegion m_doorOpenTextureRegion;

	private static final int NUMBER_OF_RESTAURANT_FRAMES = 2;
	private static final int NUMBER_OF_DOOR_FRAMES = 2;

	public StaticObjectFactory() {
		m_restaurantSheetTexture = new Texture(Gdx.files.internal("sprites/duke_rest.png"));

		TextureRegion[][] restaurantTextureRegion = TextureRegion.split(m_restaurantSheetTexture, m_restaurantSheetTexture.getWidth() / NUMBER_OF_RESTAURANT_FRAMES, m_restaurantSheetTexture.getHeight());
		TextureRegion[] restaurantFrames = new TextureRegion[NUMBER_OF_RESTAURANT_FRAMES];

		for (int i = 0; i < NUMBER_OF_RESTAURANT_FRAMES; i++) {
			restaurantFrames[i] = restaurantTextureRegion[0][i];
		}

		m_restaurantAnimation = new Animation<TextureRegion>(1.0f, restaurantFrames);

		m_doorSheetTexture = new Texture(Gdx.files.internal("sprites/door.png"));

		TextureRegion[][] doorTextureRegions = TextureRegion.split(m_doorSheetTexture, m_doorSheetTexture.getWidth() / NUMBER_OF_DOOR_FRAMES, m_doorSheetTexture.getHeight());

		m_doorClosedTextureRegion = doorTextureRegions[0][0];
		m_doorOpenTextureRegion = doorTextureRegions[0][1];
	}

	public Restaurant createRestaurant(Vector2 position) {
		return new Restaurant(position, m_restaurantAnimation);
	}

	public Door createDoor(Vector2 position) {
		return new Door(position, m_doorClosedTextureRegion, m_doorOpenTextureRegion);
	}

	public void dispose() {
		m_restaurantSheetTexture.dispose();
	}

}
