package com.sectorlimit.dukeburger;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class DukeBurger extends ApplicationAdapter {

	private World m_world;
	private Body m_groundBody;
	private Box2DDebugRenderer m_debugRenderer;

	private Texture m_skyTexture;
	private TiledMap m_map;
	private OrthogonalTiledMapRenderer m_mapRenderer;

	private Stage m_gameStage;
	private OrthographicCamera m_camera;
	private Vector2 m_cameraOffset;
	private SpriteBatch m_spriteBatch;
	private Duke m_duke;

	private static final Vector2 VIEWPORT_SIZE = new Vector2(320.0f, 180.0f);
	private static final float CAMERA_SPEED = 4.0f;

	@Override
	public void create() {
		Gdx.graphics.setWindowedMode(1280, 720);
		m_world = new World(new Vector2(0, -180), true);
		m_debugRenderer = new Box2DDebugRenderer();
		m_gameStage = new Stage(new StretchViewport(VIEWPORT_SIZE.x, VIEWPORT_SIZE.y));
		m_camera = new OrthographicCamera(VIEWPORT_SIZE.x, VIEWPORT_SIZE.y);
		m_cameraOffset = new Vector2(0.0f, 0.0f);
		m_gameStage.getViewport().setCamera(m_camera);
		m_spriteBatch = new SpriteBatch();

		m_skyTexture = new Texture(Gdx.files.internal("sprites/city_bg.png"));
		m_map = new TmxMapLoader().load("maps/test_level.tmx");
		m_mapRenderer = new OrthogonalTiledMapRenderer(m_map);
		m_mapRenderer.setView(m_camera);

		MapLayers mapLayers = m_map.getLayers();
		TiledMapTileLayer groundMapLayer = (TiledMapTileLayer) mapLayers.get("ground");

		for(int i = 0; i < groundMapLayer.getTileWidth(); i++) {
			for(int j = 0; j < groundMapLayer.getTileHeight(); j++) {
				Cell groundCell = groundMapLayer.getCell(i, j);

				if(groundCell == null) {
					continue;
				}

				TiledMapTile groundTile = groundCell.getTile();
				TextureRegion groundTileTextureRegion = groundTile.getTextureRegion();
				BodyDef groundTileBodyDefinition = new BodyDef();
				groundTileBodyDefinition.position.set(new Vector2((i * groundTileTextureRegion.getRegionWidth()) + (groundTileTextureRegion.getRegionWidth() / 2.0f), (j * groundTileTextureRegion.getRegionHeight()) + (groundTileTextureRegion.getRegionHeight() / 2.0f)));
				Body groundTileBody = m_world.createBody(groundTileBodyDefinition);
				PolygonShape groundTileBox = new PolygonShape();
				groundTileBox.setAsBox(groundTileTextureRegion.getRegionWidth() / 2.0f, groundTileTextureRegion.getRegionHeight() / 2.0f);
				groundTileBody.createFixture(groundTileBox, 0.0f);
				groundTileBox.dispose();
			}
		}

		m_duke = new Duke(m_world, m_map);

		BodyDef groundBodyDefinition = new BodyDef();
		groundBodyDefinition.position.set(new Vector2(0.0f, -1.0f));
		m_groundBody = m_world.createBody(groundBodyDefinition);
		PolygonShape groundBox = new PolygonShape();
		groundBox.setAsBox(m_gameStage.getCamera().viewportWidth * 2.0f, 1.0f);
		m_groundBody.createFixture(groundBox, 0.0f);
		groundBox.dispose();
	}

	@Override
	public void resize (int width, int height) {
		m_gameStage.getViewport().update(width, height, true);
	}

	@Override
	public void render() {
		if(Gdx.input.isKeyPressed(Keys.NUMPAD_4)) {
			m_cameraOffset.add(-CAMERA_SPEED, 0);
		}

		if(Gdx.input.isKeyPressed(Keys.NUMPAD_6)) {
			m_cameraOffset.add(CAMERA_SPEED, 0);
			
		}

		if(Gdx.input.isKeyPressed(Keys.NUMPAD_8)) {
			m_cameraOffset.add(0, CAMERA_SPEED);
			
		}

		if(Gdx.input.isKeyPressed(Keys.NUMPAD_2)) {
			m_cameraOffset.add(0, -CAMERA_SPEED);
		}

		if(Gdx.input.isKeyPressed(Keys.NUMPAD_5)) {
			m_cameraOffset.set(0.0f, 0.0f);
		}

		Vector2 newCameraPosition = new Vector2(m_duke.getCenterPosition().x, VIEWPORT_SIZE.y / 2.0f).add(m_cameraOffset);
		m_camera.position.set(newCameraPosition.x, newCameraPosition.y, 0.0f);
		
		m_world.step(1 / 60f, 6, 2);
		ScreenUtils.clear(0, 0, 0, 1);

		m_camera.update();

		m_spriteBatch.begin();

		m_spriteBatch.draw(m_skyTexture, 0.0f, 0.0f, 0.0f, 0.0f, m_skyTexture.getWidth(), m_skyTexture.getHeight(), 4.0f, 4.0f, 0.0f, 0, 0, m_skyTexture.getWidth(), m_skyTexture.getHeight(), false, false);

		m_spriteBatch.end();

		m_spriteBatch.begin();

		m_gameStage.getViewport().apply();
		m_gameStage.draw();

		m_mapRenderer.setView(m_camera);
		m_mapRenderer.render();

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
