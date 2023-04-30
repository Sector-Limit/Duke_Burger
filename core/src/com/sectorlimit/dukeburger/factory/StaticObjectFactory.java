package com.sectorlimit.dukeburger.factory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.sectorlimit.dukeburger.object.Restaurant;

public class StaticObjectFactory {

	private Texture m_restaurantSheetTexture;
	private Animation<TextureRegion> m_restaurantAnimation;

	private static final int NUMBER_OF_RESTAURANT_FRAMES = 2;

	public StaticObjectFactory() {
		m_restaurantSheetTexture = new Texture(Gdx.files.internal("sprites/duke_rest.png"));

		TextureRegion[][] restaurantTextureRegion = TextureRegion.split(m_restaurantSheetTexture, m_restaurantSheetTexture.getWidth() / NUMBER_OF_RESTAURANT_FRAMES, m_restaurantSheetTexture.getHeight());
		TextureRegion[] restaurantFrames = new TextureRegion[NUMBER_OF_RESTAURANT_FRAMES];

		for (int i = 0; i < NUMBER_OF_RESTAURANT_FRAMES; i++) {
			restaurantFrames[i] = restaurantTextureRegion[0][i];
		}

		m_restaurantAnimation = new Animation<TextureRegion>(1.0f, restaurantFrames);
	}

	public Restaurant createRestaurant(Vector2 position) {
		return new Restaurant(position, m_restaurantAnimation);
	}

	public void dispose() {
		m_restaurantSheetTexture.dispose();
	}

}
