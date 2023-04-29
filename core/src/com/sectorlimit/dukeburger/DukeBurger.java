package com.sectorlimit.dukeburger;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;

public class DukeBurger extends ApplicationAdapter {

	private World m_world;
	private Body m_groundBody;
	private Box2DDebugRenderer m_debugRenderer;

	private GameStage m_gameStage;
	private SpriteBatch m_spriteBatch;
	private Duke m_duke;

	@Override
	public void create() {
		Gdx.graphics.setWindowedMode(1280, 720);
		m_world = new World(new Vector2(0, -180), true);
		m_debugRenderer = new Box2DDebugRenderer();
		m_gameStage = new GameStage();
		m_spriteBatch = new SpriteBatch();
		m_duke = new Duke(m_world);

		BodyDef groundBodyDefinition = new BodyDef();
		groundBodyDefinition.position.set(new Vector2(0.0f, -1.0f));
		m_groundBody = m_world.createBody(groundBodyDefinition);
		PolygonShape groundBox = new PolygonShape();
		groundBox.setAsBox(m_gameStage.getCamera().viewportWidth, 1.0f);
		m_groundBody.createFixture(groundBox, 0.0f);
		groundBox.dispose();
	}

	@Override
	public void resize (int width, int height) {
		m_gameStage.resize(width, height);
	}

	@Override
	public void render() {
		m_world.step(1 / 60f, 6, 2);
		ScreenUtils.clear(0, 0, 0, 1);

		m_spriteBatch.begin();

		m_gameStage.getViewport().apply();
		m_gameStage.draw();
		m_duke.render(m_spriteBatch);

		m_spriteBatch.end();

		m_debugRenderer.render(m_world, m_gameStage.getCamera().combined);
	}

	@Override
	public void dispose() {
		m_spriteBatch.dispose();
		m_duke.dispose();
	}

}
