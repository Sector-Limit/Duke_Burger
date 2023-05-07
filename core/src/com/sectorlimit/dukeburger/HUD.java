package com.sectorlimit.dukeburger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class HUD {

	private TextRenderer m_textRenderer;
	private HUDDataProvider m_dataProvider;

	private Texture m_hudTexture;
	private Texture m_fullHeartTexture;
	private Texture m_emptyHeartTexture;

	private static final float HEART_SPACING = 5.0f;

	public HUD(HUDDataProvider dataProvider, TextRenderer textRenderer) {
		m_dataProvider = dataProvider;
		m_textRenderer = textRenderer;

		m_hudTexture = new Texture(Gdx.files.internal("sprites/ui_bar.png"));
		m_fullHeartTexture = new Texture(Gdx.files.internal("sprites/heart.png"));
		m_emptyHeartTexture = new Texture(Gdx.files.internal("sprites/heart_grey.png"));
	}

	public void render(SpriteBatch spriteBatch) {
		Vector2 hudPosition = new Vector2(0.0f, (DukeBurger.VIEWPORT_SIZE.y - m_hudTexture.getHeight()));
		spriteBatch.draw(m_hudTexture, hudPosition.x, hudPosition.y, 0.0f, 0.0f, m_hudTexture.getWidth(), m_hudTexture.getHeight(), 1.0f, 1.0f, 0.0f, 0, 0, m_hudTexture.getWidth(), m_hudTexture.getHeight(), false, false);

		float verticalTextPosition =  (m_hudTexture.getHeight() * 0.35f);
		Vector2 heartPosition = new Vector2(hudPosition).add(new Vector2(63.0f, verticalTextPosition));

		for(int i = 0; i < Duke.MAX_HEALTH; i++) {
			Texture heartTexture = m_dataProvider.getHealth() >= i + 1 ? m_fullHeartTexture : m_emptyHeartTexture;
			spriteBatch.draw(heartTexture, heartPosition.x + ((heartTexture.getWidth() + HEART_SPACING) * i), heartPosition.y, 0.0f, 0.0f, heartTexture.getWidth(), heartTexture.getHeight(), 1.0f, 1.0f, 0.0f, 0, 0, heartTexture.getWidth(), heartTexture.getHeight(), false, false);
		}

		Vector2 livesPosition = new Vector2(hudPosition).add(new Vector2(157.0f, verticalTextPosition));
		m_textRenderer.renderNumber(spriteBatch, m_dataProvider.getLives(), livesPosition);

		Vector2 coinsPosition = new Vector2(hudPosition).add(new Vector2(297.0f, verticalTextPosition));
		m_textRenderer.renderNumber(spriteBatch, m_dataProvider.getCoins(), coinsPosition, 2);
	}

}
