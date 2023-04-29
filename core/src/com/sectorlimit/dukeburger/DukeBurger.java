package com.sectorlimit.dukeburger;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class DukeBurger extends ApplicationAdapter {

	private GameStage m_gameStage;
	private SpriteBatch m_spriteBatch;
	private Duke m_duke;

	@Override
	public void create() {
		Gdx.graphics.setWindowedMode(1280, 720);
		m_gameStage = new GameStage();
		m_spriteBatch = new SpriteBatch();
		m_duke = new Duke();
	}

	@Override
	public void resize (int width, int height) {
		m_gameStage.resize(width, height);
	}

	@Override
	public void render() {
		ScreenUtils.clear(0, 0, 0, 1);
		m_spriteBatch.begin();
		m_gameStage.getViewport().apply();
		m_gameStage.draw();
		m_duke.render(m_spriteBatch);
		m_spriteBatch.end();
	}

	@Override
	public void dispose() {
		m_spriteBatch.dispose();
		m_duke.dispose();
	}

}
