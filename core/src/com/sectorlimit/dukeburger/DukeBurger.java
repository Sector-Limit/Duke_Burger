package com.sectorlimit.dukeburger;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class DukeBurger extends ApplicationAdapter implements DukeListener {

	private World m_world;
	private Box2DDebugRenderer m_debugRenderer;

	private Texture m_citySkyTexture;
	private Texture m_skyTexture;
	private TiledMap m_map;
	private OrthogonalTiledMapRenderer m_mapRenderer;

	private Stage m_gameStage;
	private OrthographicCamera m_camera;
	private Vector2 m_cameraOffset;
	private SpriteBatch m_spriteBatch;
	private Duke m_duke;
	private int m_lives;
	private int m_coins;
	private String m_currentLevelFileName;

	private Sound m_themeMusic;
	private Sound m_cityMusic;
	private Sound m_subwayMusic;

	public static final Vector2 VIEWPORT_SIZE = new Vector2(320.0f, 180.0f);
	private static final float CAMERA_FOLLOW_VERTICAL_OFFSET_PERCENTAGE = 0.5f;
	private static final float CAMERA_SPEED = 4.0f;
	private static final boolean DEBUG_CAMERA_ENABLED = false;
	private static final boolean PHYSICS_DEBUGGING_ENABLED = false;
	private static final boolean MUSIC_ENABLED = true;
	private static final float MUSIC_VOLUME = 0.35f;

	@Override
	public void create() {
		Gdx.graphics.setWindowedMode(1280, 720);

		m_lives = Duke.MAX_LIVES;
		m_coins = 0;

		m_camera = new OrthographicCamera(VIEWPORT_SIZE.x, VIEWPORT_SIZE.y);
		m_gameStage = new Stage(new StretchViewport(VIEWPORT_SIZE.x, VIEWPORT_SIZE.y));
		m_gameStage.getViewport().setCamera(m_camera);

		m_spriteBatch = new SpriteBatch();
		m_citySkyTexture = new Texture(Gdx.files.internal("sprites/city_bg.png"));

		m_currentLevelFileName = "mission_1.tmx";

		if(MUSIC_ENABLED) {
			m_themeMusic = Gdx.audio.newSound(Gdx.files.internal("music/pixelduke.mp3"));
			m_cityMusic = Gdx.audio.newSound(Gdx.files.internal("music/city.mp3"));
			m_subwayMusic = Gdx.audio.newSound(Gdx.files.internal("music/subway.mp3"));

			m_themeMusic.loop(MUSIC_VOLUME);
		}

		startNewGame(m_currentLevelFileName);
	}

	public void startNewGame(String levelFileName) {
		startNewGame(levelFileName, Duke.MAX_LIVES, 0);
	}

	public void startNewGame(String levelFileName, int lives, int coins) {
		stopMusic();

		m_world = new World(new Vector2(0, -220), true);

		if(PHYSICS_DEBUGGING_ENABLED) {
			m_debugRenderer = new Box2DDebugRenderer();
		}

		m_cameraOffset = new Vector2(0.0f, 0.0f);

		m_map = new TmxMapLoader().load("maps/" + levelFileName);
		m_mapRenderer = new OrthogonalTiledMapRenderer(m_map);

		MapLayers mapLayers = m_map.getLayers();
		MapLayer collisionMapLayer = mapLayers.get("collision");
		MapObjects collisionObjects = collisionMapLayer.getObjects();

		for(int i = 0; i < collisionObjects.getCount(); i++) {
			MapObject collisionObject = collisionObjects.get(i);

			if(collisionObject instanceof PolygonMapObject) {
				PolygonMapObject polygonCollisionObject = (PolygonMapObject) collisionObject;
				Polygon polygonCollision = polygonCollisionObject.getPolygon();
				float[] polygonCollisionVertices = polygonCollision.getVertices();

				if(polygonCollisionVertices.length < 3 || polygonCollisionVertices.length > 8) {
					System.err.println("Map has polygon collision with invalid number of vertices: " + polygonCollisionVertices.length + ". Expected between 3 and 8 vertices.");
					continue;
				}
				
				BodyDef groundTileBodyDefinition = new BodyDef();
				groundTileBodyDefinition.position.set(new Vector2(polygonCollision.getX(), polygonCollision.getY()));
				Body collisionObjectBody = m_world.createBody(groundTileBodyDefinition);
				PolygonShape collisionObjectPolygonShape = new PolygonShape();
				collisionObjectPolygonShape.set(polygonCollisionVertices);
				Fixture collisionFixture = collisionObjectBody.createFixture(collisionObjectPolygonShape, 0.0f);
				collisionObjectPolygonShape.dispose();
				Filter collisionFilter = new Filter();
				collisionFilter.categoryBits = CollisionCategories.GROUND;
				collisionFilter.maskBits = CollisionCategories.GROUND | CollisionCategories.DUKE | CollisionCategories.OBJECT | CollisionCategories.BURGER | CollisionCategories.ENEMY;
				collisionFixture.setFilterData(collisionFilter);
			}
		}

		m_duke = new Duke(m_world, m_map, lives, coins);
		m_duke.setListener(this);

		MapProperties mapProperties = m_map.getProperties();

		m_skyTexture = null;

		if(mapProperties != null) {
			if(mapProperties.containsKey("background")) {
				Object backgroundTypeObject = mapProperties.get("background");
	
				if(backgroundTypeObject instanceof String) {
					String backgroundType = (String) backgroundTypeObject;
	
					if(backgroundType.equalsIgnoreCase("city")) {
						m_skyTexture = m_citySkyTexture;
					}
					else {
						System.err.println("Invalid background type: '" + backgroundType + "'.");
					}
				}
			}

			if(mapProperties.containsKey("music")) {
				Object musicTypeObject = mapProperties.get("music");
				
				if(musicTypeObject instanceof String) {
					String musicType = (String) musicTypeObject;
					Sound music = null;
	
					if(musicType.equalsIgnoreCase("city")) {
						music = m_cityMusic;
					}
					else if(musicType.equalsIgnoreCase("subway")) {
						music = m_subwayMusic;
					}
					else {
						System.err.println("Invalid music type: '" + musicType + "'.");
					}

					if(music != null && MUSIC_ENABLED) {
						music.loop(MUSIC_VOLUME);
					}
				}
			}
		}
	}

	public void stopMusic() {
		if(!MUSIC_ENABLED) {
			return;
		}

		m_themeMusic.stop();
		m_cityMusic.stop();
		m_subwayMusic.stop();
	}

	public void stopGame() {
		m_mapRenderer.dispose();
		m_map.dispose();
		
		if(m_debugRenderer != null) {
			m_debugRenderer.dispose();
		}

		m_world.dispose();
		m_duke.dispose();

		m_mapRenderer = null;
		m_map = null;
		m_debugRenderer = null;
		m_world = null;
		m_duke = null;
		m_skyTexture = null;
	}

	public void onKilled() {
		m_lives = m_duke.getLives();
		m_coins = m_duke.getCoins();

		stopGame();
		startNewGame(m_currentLevelFileName, m_lives, m_coins);
	}

	@Override
	public void resize (int width, int height) {
		m_gameStage.getViewport().update(width, height, true);
	}

	@Override
	public void render() {
		if(m_duke == null) {
			return;
		}

		if(DEBUG_CAMERA_ENABLED) {
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
		}

		Vector2 newCameraPosition = new Vector2(m_duke.getCenterPosition().x, VIEWPORT_SIZE.y / 2.0f).add(m_cameraOffset);

		if(m_duke.getCenterPosition().y > VIEWPORT_SIZE.y * CAMERA_FOLLOW_VERTICAL_OFFSET_PERCENTAGE) {
			newCameraPosition.add(new Vector2(0.0f, m_duke.getCenterPosition().y - VIEWPORT_SIZE.y * CAMERA_FOLLOW_VERTICAL_OFFSET_PERCENTAGE));
		}

		m_camera.position.set(newCameraPosition.x, newCameraPosition.y, 0.0f);

		m_world.step(1 / 60f, 6, 2);
		ScreenUtils.clear(0, 0, 0, 1);

		m_camera.update();

		if(m_skyTexture != null) {
			m_spriteBatch.begin();
	
			m_spriteBatch.draw(m_skyTexture, 0.0f, 0.0f, 0.0f, 0.0f, m_skyTexture.getWidth(), m_skyTexture.getHeight(), 4.0f, 4.0f, 0.0f, 0, 0, m_skyTexture.getWidth(), m_skyTexture.getHeight(), false, false);
	
			m_spriteBatch.end();
		}

		m_spriteBatch.begin();

		m_gameStage.getViewport().apply();
		m_gameStage.draw();

		m_mapRenderer.setView(m_camera);
		m_mapRenderer.render();

		m_duke.render(m_spriteBatch);

		m_spriteBatch.end();

		m_spriteBatch.begin();

		m_duke.renderHUD(m_spriteBatch);

		m_spriteBatch.end();

		if(m_debugRenderer != null) {
			m_debugRenderer.render(m_world, m_gameStage.getCamera().combined);
		}
	}

	@Override
	public void dispose() {
		if(m_duke != null) {
			m_mapRenderer.dispose();
			m_map.dispose();

			if(m_debugRenderer != null) {
				m_debugRenderer.dispose();
			}

			m_world.dispose();
			m_duke.dispose();
		}

		m_skyTexture = null;
		m_citySkyTexture.dispose();
		m_spriteBatch.dispose();
		m_gameStage.dispose();
	}

}
