package com.sectorlimit.dukeburger;

import com.badlogic.gdx.Application;
import java.util.Arrays;
import java.util.Vector;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import com.sectorlimit.dukeburger.Jukebox.Track;

public class DukeBurger extends ApplicationAdapter implements DukeListener {

	private World m_world;
	private float m_physicsTimeAccumulator;
	private Box2DDebugRenderer m_debugRenderer;

	private Texture m_citySkyTexture;
	private Texture m_skyTexture;
	private Texture m_titleScreenSheetTexture;
	private Animation<TextureRegion> m_titleScreenAnimation;
	private Animation<Texture> m_introAnimation;
	private TiledMap m_map;
	private OrthogonalTiledMapRenderer m_mapRenderer;

	private boolean m_showTitleScreen;
	private float m_elapsedTitleScreenAnimationTime;
	private boolean m_showIntro;
	private float m_elapsedIntroAnimationTime;
	private Stage m_uiStage;
	private Stage m_gameStage;
	private boolean m_debugCameraEnabled;
	private OrthographicCamera m_camera;
	private Vector2 m_cameraOffset;
	private SpriteBatch m_spriteBatch;
	private Duke m_duke;
	private int m_lives;
	private int m_coins;
	private int m_currentLevelNumber;
	private String m_currentLevelFileName;

	private Jukebox m_jukebox;

	public static final Vector2 VIEWPORT_SIZE = new Vector2(320.0f, 180.0f);
	private static final float PHYSICS_TIME_STEMP = 1 / 60.f;
	private static final int PHYSICS_VELOCITY_ITERATIONS = 6;
	private static final int PHYSICS_POSITION_ITERATIONS = 2;
	private static final int NUMBER_OF_TITLE_SCREEN_FRAMES = 2;
	private static final int NUMBER_OF_MISSIONS = 4;
	private static final float CAMERA_FOLLOW_VERTICAL_OFFSET_PERCENTAGE = 0.5f;
	private static final float CAMERA_SPEED = 4.0f;
	private static final boolean DEBUG_CAMERA_ENABLED = false;
	private static final boolean PHYSICS_DEBUGGING_ENABLED = false;

	private class AnimationFrameData {

		public Texture texture;
		public float delay;

		public AnimationFrameData(Texture t, float d) {
			texture = t;
			delay = d;
		}

	}

	@Override
	public void create() {
		Gdx.graphics.setWindowedMode(1280, 720);

		m_showTitleScreen = true;
		m_elapsedTitleScreenAnimationTime = 0.0f;
		m_showIntro = false;
		m_elapsedIntroAnimationTime = 0.0f;
		m_currentLevelNumber = 1;
		m_lives = Duke.MAX_LIVES;
		m_coins = 0;

		m_camera = new OrthographicCamera(VIEWPORT_SIZE.x, VIEWPORT_SIZE.y);
		m_debugCameraEnabled = DEBUG_CAMERA_ENABLED;
		m_uiStage = new Stage(new StretchViewport(VIEWPORT_SIZE.x, VIEWPORT_SIZE.y));
		m_gameStage = new Stage(new StretchViewport(VIEWPORT_SIZE.x, VIEWPORT_SIZE.y));
		m_gameStage.getViewport().setCamera(m_camera);

		m_spriteBatch = new SpriteBatch();
		m_citySkyTexture = new Texture(Gdx.files.internal("sprites/city_bg.png"));

		m_jukebox = new Jukebox();

		m_introAnimation = createIntroAnimation();

		if(Gdx.app.getType() == Application.ApplicationType.Desktop) {
			m_jukebox.play(Jukebox.Track.PixelDuke);
		}

		m_titleScreenSheetTexture = new Texture(Gdx.files.internal("ui/duke_burger_menu_animation.png"));

		TextureRegion[][] titleScreenTextureRegion = TextureRegion.split(m_titleScreenSheetTexture, m_titleScreenSheetTexture.getWidth() / NUMBER_OF_TITLE_SCREEN_FRAMES, m_titleScreenSheetTexture.getHeight());
		TextureRegion[] titleScreenFrames = new TextureRegion[NUMBER_OF_TITLE_SCREEN_FRAMES];

		for (int i = 0; i < NUMBER_OF_TITLE_SCREEN_FRAMES; i++) {
			titleScreenFrames[i] = titleScreenTextureRegion[0][i];
		}

		m_titleScreenAnimation = new Animation<TextureRegion>(0.3f, titleScreenFrames);

		if(m_currentLevelFileName != null) {
			startNewGame(m_currentLevelFileName);
		}
	}

	public void nextLevel() {
		m_lives = m_duke.getLives();
		m_coins = m_duke.getCoins();

		stopGame();

		if(m_currentLevelNumber <= 0 || m_currentLevelNumber >= NUMBER_OF_MISSIONS) {
			m_showTitleScreen = true;
			return;
		}

		m_currentLevelNumber++;

		startNewGame(m_currentLevelNumber);
	}

	public void startBrandNewGame() {
		m_coins = 0;
		m_lives = Duke.MAX_LIVES;
		m_currentLevelNumber = 1;

		startNewGame(1);
	}

	public void startNewGame(int levelNumber) {
		if(levelNumber < 1 || levelNumber > NUMBER_OF_MISSIONS) {
			return;
		}

		m_currentLevelNumber = levelNumber;

		startNewGame("mission_" + m_currentLevelNumber + ".tmx", m_lives, m_coins);
	}

	public void startNewGame(String levelFileName) {
		startNewGame(levelFileName, Duke.MAX_LIVES, 0);
	}

	public void startNewGame(String levelFileName, int lives, int coins) {
		stopMusic();

		m_currentLevelFileName = levelFileName;
		m_showTitleScreen = false;
		m_elapsedTitleScreenAnimationTime = 0.0f;
		m_showIntro = false;
		m_elapsedIntroAnimationTime = 0.0f;
		m_physicsTimeAccumulator = 0.0f;

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
				collisionFilter.maskBits = CollisionCategories.GROUND | CollisionCategories.DUKE | CollisionCategories.DUKE_FEET_SENSOR | CollisionCategories.DUKE_SIDE_SENSOR | CollisionCategories.OBJECT | CollisionCategories.BURGER | CollisionCategories.ENEMY | CollisionCategories.ENEMY_SIDE_SENSOR;
				collisionFixture.setFilterData(collisionFilter);
			}
		}

		MapLayer enemyBoundaryCollisionMapLayer = mapLayers.get("enemy_collision");

		if(enemyBoundaryCollisionMapLayer != null) {
			MapObjects enemyBoundaryCollisionObjects = enemyBoundaryCollisionMapLayer.getObjects();

			if(enemyBoundaryCollisionObjects.getCount() != 0) {
				for(int i = 0; i < enemyBoundaryCollisionObjects.getCount(); i++) {
					MapObject enemyBoundaryCollisionObject = enemyBoundaryCollisionObjects.get(i);

					if(enemyBoundaryCollisionObject instanceof PolygonMapObject) {
						PolygonMapObject enemyBoundaryPolygonCollisionObject = (PolygonMapObject) enemyBoundaryCollisionObject;
						Polygon enemyBoundaryPolygonCollision = enemyBoundaryPolygonCollisionObject.getPolygon();
						float[] enemyBoundaryPolygonCollisionVertices = enemyBoundaryPolygonCollision.getVertices();

						if(enemyBoundaryPolygonCollisionVertices.length < 3 || enemyBoundaryPolygonCollisionVertices.length > 8) {
							System.err.println("Map has enemy boundary polygon collision with invalid number of vertices: " + enemyBoundaryPolygonCollisionVertices.length + ". Expected between 3 and 8 vertices.");
							continue;
						}

						BodyDef enemyBoundaryBodyDefinition = new BodyDef();
						enemyBoundaryBodyDefinition.position.set(new Vector2(enemyBoundaryPolygonCollision.getX(), enemyBoundaryPolygonCollision.getY()));
						Body enemyBoundaryCollisionObjectBody = m_world.createBody(enemyBoundaryBodyDefinition);
						enemyBoundaryCollisionObjectBody.setUserData("enemy_boundary");
						PolygonShape enemyBoundaryCollisionObjectPolygonShape = new PolygonShape();
						enemyBoundaryCollisionObjectPolygonShape.set(enemyBoundaryPolygonCollisionVertices);
						Fixture collisionFixture = enemyBoundaryCollisionObjectBody.createFixture(enemyBoundaryCollisionObjectPolygonShape, 0.0f);
						enemyBoundaryCollisionObjectPolygonShape.dispose();
						Filter enemyBoundaryCollisionFixture = new Filter();
						enemyBoundaryCollisionFixture.categoryBits = CollisionCategories.ENEMY_BOUNDARY;
						enemyBoundaryCollisionFixture.maskBits = CollisionCategories.ENEMY | CollisionCategories.ENEMY_SIDE_SENSOR;
						collisionFixture.setFilterData(enemyBoundaryCollisionFixture);
					}
				}
			}
		}

		MapLayer finishCollisionMapLayer = mapLayers.get("finish_collision");

		if(finishCollisionMapLayer != null) {
			MapObjects finishCollisionObjects = finishCollisionMapLayer.getObjects();

			if(finishCollisionObjects.getCount() != 0) {
				for(int i = 0; i < finishCollisionObjects.getCount(); i++) {
					MapObject finishCollisionObject = finishCollisionObjects.get(i);

					if(finishCollisionObject instanceof PolygonMapObject) {
						PolygonMapObject finishPolygonCollisionObject = (PolygonMapObject) finishCollisionObject;
						Polygon finishPolygonCollision = finishPolygonCollisionObject.getPolygon();
						float[] finishPolygonCollisionVertices = finishPolygonCollision.getVertices();

						if(finishPolygonCollisionVertices.length < 3 || finishPolygonCollisionVertices.length > 8) {
							System.err.println("Map has finish polygon collision with invalid number of vertices: " + finishPolygonCollisionVertices.length + ". Expected between 3 and 8 vertices.");
							continue;
						}

						BodyDef finishBodyDefinition = new BodyDef();
						finishBodyDefinition.position.set(new Vector2(finishPolygonCollision.getX(), finishPolygonCollision.getY()));
						Body finishCollisionObjectBody = m_world.createBody(finishBodyDefinition);
						finishCollisionObjectBody.setUserData("finish");
						PolygonShape finishCollisionObjectPolygonShape = new PolygonShape();
						finishCollisionObjectPolygonShape.set(finishPolygonCollisionVertices);
						Fixture collisionFixture = finishCollisionObjectBody.createFixture(finishCollisionObjectPolygonShape, 0.0f);
						collisionFixture.setSensor(true);
						finishCollisionObjectPolygonShape.dispose();
						Filter finishCollisionFixture = new Filter();
						finishCollisionFixture.categoryBits = CollisionCategories.GROUND;
						finishCollisionFixture.maskBits = CollisionCategories.DUKE;
						collisionFixture.setFilterData(finishCollisionFixture);
					}
				}
			}
			else {
				System.err.println("Map 'finish_collision' layer has no objects.");
			}
		}
		else {
			System.err.println("Map is missing 'finish_collision' layer.");
		}

		MapLayer deathBoundaryCollisionMapLayer = mapLayers.get("death_collision");

		if(deathBoundaryCollisionMapLayer != null) {
			MapObjects deathBoundaryCollisionObjects = deathBoundaryCollisionMapLayer.getObjects();

			if(deathBoundaryCollisionObjects.getCount() != 0) {
				for(int i = 0; i < deathBoundaryCollisionObjects.getCount(); i++) {
					MapObject deathBoundaryCollisionObject = deathBoundaryCollisionObjects.get(i);

					if(deathBoundaryCollisionObject instanceof PolygonMapObject) {
						PolygonMapObject deathBoundaryPolygonCollisionObject = (PolygonMapObject) deathBoundaryCollisionObject;
						Polygon deathBoundaryPolygonCollision = deathBoundaryPolygonCollisionObject.getPolygon();
						float[] deathBoundaryPolygonCollisionVertices = deathBoundaryPolygonCollision.getVertices();

						if(deathBoundaryPolygonCollisionVertices.length < 3 || deathBoundaryPolygonCollisionVertices.length > 8) {
							System.err.println("Map has death boundary polygon collision with invalid number of vertices: " + deathBoundaryPolygonCollisionVertices.length + ". Expected between 3 and 8 vertices.");
							continue;
						}

						BodyDef deathBoundaryBodyDefinition = new BodyDef();
						deathBoundaryBodyDefinition.position.set(new Vector2(deathBoundaryPolygonCollision.getX(), deathBoundaryPolygonCollision.getY()));
						Body deathBoundaryCollisionObjectBody = m_world.createBody(deathBoundaryBodyDefinition);
						deathBoundaryCollisionObjectBody.setUserData("death");
						PolygonShape deathBoundaryCollisionObjectPolygonShape = new PolygonShape();
						deathBoundaryCollisionObjectPolygonShape.set(deathBoundaryPolygonCollisionVertices);
						Fixture deathCollisionFixture = deathBoundaryCollisionObjectBody.createFixture(deathBoundaryCollisionObjectPolygonShape, 0.0f);
						deathCollisionFixture.setSensor(true);
						deathBoundaryCollisionObjectPolygonShape.dispose();
						Filter deathBoundarydeathBoundaryCollisionFixture = new Filter();
						deathBoundarydeathBoundaryCollisionFixture.categoryBits = CollisionCategories.DEATH;
						deathBoundarydeathBoundaryCollisionFixture.maskBits = CollisionCategories.DUKE;
						deathCollisionFixture.setFilterData(deathBoundarydeathBoundaryCollisionFixture);
					}
				}
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

					if(!m_jukebox.play(musicType)) {
						System.err.println("Invalid music type: '" + musicType + "'.");
					}
				}
			}
		}
	}

	public void stopMusic() {
		m_jukebox.stop();
	}

	public void stopGame() {
		stopMusic();

		m_mapRenderer = null;
		m_map = null;
		m_debugRenderer = null;
		m_world = null;
		m_duke = null;
		m_skyTexture = null;
	}

	@Override
	public void onKilled() {
		stopMusic();
	}

	@Override
	public void onDead() {
		m_lives = m_duke.getLives();
		m_coins = m_duke.getCoins();

		stopGame();
		startNewGame(m_currentLevelFileName, m_lives, m_coins);
	}

	@Override
	public void onGameOver() {
		m_showTitleScreen = true;
		m_elapsedTitleScreenAnimationTime = 0.0f;

		stopGame();

		m_jukebox.play(Track.PixelDuke);
	}

	@Override
	public void onLevelCompleted() {
		stopMusic();
	}

	@Override
	public void onLevelEnded() {
		nextLevel();
	}

	@Override
	public void onLevelWarpRequested(int levelNumber) {
		if(levelNumber <= 0 || levelNumber > NUMBER_OF_MISSIONS) {
			return;
		}

		m_lives = m_duke.getLives();
		m_coins = m_duke.getCoins();

		stopGame();

		m_currentLevelNumber = levelNumber;

		startNewGame(m_currentLevelNumber);
	}

	@Override
	public void onTestLevelWarpRequested(int levelNumber) {
		if(levelNumber <= 0 || levelNumber > NUMBER_OF_MISSIONS) {
			return;
		}

		m_lives = m_duke.getLives();
		m_coins = m_duke.getCoins();

		stopGame();

		m_currentLevelNumber = 1;

		startNewGame("test_level" + (levelNumber == 1 ? "" : "_" + levelNumber) + ".tmx", m_lives, m_coins);
	}

	@Override
	public void onDebugCameraEnableRequested() {
		m_debugCameraEnabled = true;
	}

	@Override
	public void resize (int width, int height) {
		m_gameStage.getViewport().update(width, height, true);
	}

	@Override
	public void render() {
		ScreenUtils.clear(0, 0, 0, 1);

		float deltaTime = Gdx.graphics.getDeltaTime();

		if(m_showTitleScreen) {
			m_elapsedTitleScreenAnimationTime += deltaTime;

			if(m_elapsedTitleScreenAnimationTime >= m_titleScreenAnimation.getAnimationDuration()) {
				m_elapsedTitleScreenAnimationTime = m_elapsedTitleScreenAnimationTime % m_titleScreenAnimation.getAnimationDuration();
			}

			m_spriteBatch.begin();

			m_uiStage.getViewport().apply();
			m_uiStage.draw();

			TextureRegion titleScreenFrameTextureRegion = m_titleScreenAnimation.getKeyFrame(m_elapsedTitleScreenAnimationTime);
			m_spriteBatch.draw(titleScreenFrameTextureRegion, 0.0f, 0.0f, 0.0f, 0.0f, titleScreenFrameTextureRegion.getRegionWidth(), titleScreenFrameTextureRegion.getRegionHeight(), 1.0f, 1.0f, 0.0f);

			m_spriteBatch.end();

			if(Gdx.input.isKeyPressed(Keys.ANY_KEY)) {
				m_showTitleScreen = false;
				m_elapsedTitleScreenAnimationTime = 0.0f;

				m_showIntro = true;
				m_elapsedIntroAnimationTime = 0.0f;

				if(!m_jukebox.isPlaying()) {
					m_jukebox.play(Track.PixelDuke);
				}
			}

			return;
		}

		if(m_showIntro) {
			if(m_introAnimation != null) {
				m_elapsedIntroAnimationTime += deltaTime;

				m_spriteBatch.begin();

				m_uiStage.getViewport().apply();
				m_uiStage.draw();

				Texture introFrameTexture = m_introAnimation.getKeyFrame(m_elapsedIntroAnimationTime);
				m_spriteBatch.draw(introFrameTexture, 0.0f, 0.0f, 0.0f, 0.0f, introFrameTexture.getWidth(), introFrameTexture.getHeight(), 1.0f, 1.0f, 0.0f, 0, 0, introFrameTexture.getWidth(), introFrameTexture.getHeight(), false, false);

				m_spriteBatch.end();

				if((Gdx.input.isKeyPressed(Keys.ESCAPE) || Gdx.input.isKeyPressed(Keys.BUTTON_START) || Gdx.input.isKeyPressed(Keys.BUTTON_SELECT) || Gdx.input.isKeyPressed(Keys.BUTTON_A)) || m_elapsedIntroAnimationTime >= m_introAnimation.getAnimationDuration()) {
					m_showIntro = false;
					m_elapsedIntroAnimationTime = 0.0f;

					startBrandNewGame();
				}

				return;
			}
			else {
				m_showIntro = false;

				startBrandNewGame();
			}
		}

		if(m_duke == null) {
			return;
		}

		if(m_debugCameraEnabled) {
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

		m_physicsTimeAccumulator += deltaTime;

		while(m_physicsTimeAccumulator >= PHYSICS_TIME_STEMP) {
			m_world.step(PHYSICS_TIME_STEMP, PHYSICS_VELOCITY_ITERATIONS, PHYSICS_POSITION_ITERATIONS);
			m_physicsTimeAccumulator -= PHYSICS_TIME_STEMP;
		}

		Vector2 newCameraPosition = new Vector2(m_duke.getCenterPosition().x, VIEWPORT_SIZE.y / 2.0f).add(m_cameraOffset);

		if(m_duke.getCenterPosition().y > VIEWPORT_SIZE.y * CAMERA_FOLLOW_VERTICAL_OFFSET_PERCENTAGE) {
			newCameraPosition.add(new Vector2(0.0f, m_duke.getCenterPosition().y - VIEWPORT_SIZE.y * CAMERA_FOLLOW_VERTICAL_OFFSET_PERCENTAGE));
		}

		m_camera.position.set(newCameraPosition.x, newCameraPosition.y, 0.0f);
		m_camera.update();

		if(m_skyTexture != null) {
			m_spriteBatch.begin();

			m_uiStage.getViewport().apply();
			m_uiStage.draw();

			m_spriteBatch.draw(m_skyTexture, 0.0f, 0.0f, 0.0f, 0.0f, m_skyTexture.getWidth(), m_skyTexture.getHeight(), 1.0f, 1.0f, 0.0f, 0, 0, m_skyTexture.getWidth(), m_skyTexture.getHeight(), false, false);

			m_spriteBatch.end();
		}

		m_spriteBatch.begin();

		m_gameStage.getViewport().apply();
		m_gameStage.draw();

		m_mapRenderer.setView(m_camera);
		m_mapRenderer.render();

		m_duke.render(m_spriteBatch);

		m_spriteBatch.end();

		if(m_duke == null) {
			return;
		}

		m_spriteBatch.begin();

		m_uiStage.getViewport().apply();
		m_uiStage.draw();

		m_duke.renderHUD(m_spriteBatch);

		m_spriteBatch.end();

		if(m_debugRenderer != null) {
			m_debugRenderer.render(m_world, m_gameStage.getCamera().combined);
		}
	}

	private Animation<Texture> createIntroAnimation() {
		float fadeDuration = 200.0f;
		Texture blankFrame = new Texture(Gdx.files.internal("ui/intro/intro00.png"));

		Vector<AnimationFrameData> animationFrameData = new Vector<AnimationFrameData>(Arrays.asList(
			new AnimationFrameData(blankFrame, 1000.0f),
			new AnimationFrameData(new Texture(Gdx.files.internal("ui/intro/intro01.png")), fadeDuration),
			new AnimationFrameData(new Texture(Gdx.files.internal("ui/intro/intro02.png")), 3000.0f),
			new AnimationFrameData(new Texture(Gdx.files.internal("ui/intro/intro03.png")), 200.0f),
			new AnimationFrameData(blankFrame, fadeDuration),
			new AnimationFrameData(new Texture(Gdx.files.internal("ui/intro/intro04.png")), fadeDuration),
			new AnimationFrameData(new Texture(Gdx.files.internal("ui/intro/intro05.png")), 3000.0f),
			new AnimationFrameData(new Texture(Gdx.files.internal("ui/intro/intro06.png")), 3000.0f),
			new AnimationFrameData(new Texture(Gdx.files.internal("ui/intro/intro07.png")), fadeDuration),
			new AnimationFrameData(blankFrame, fadeDuration),
			new AnimationFrameData(new Texture(Gdx.files.internal("ui/intro/intro08.png")), fadeDuration),
			new AnimationFrameData(new Texture(Gdx.files.internal("ui/intro/intro09.png")), 1500.0f),
			new AnimationFrameData(new Texture(Gdx.files.internal("ui/intro/intro10.png")), 200.0f),
			new AnimationFrameData(blankFrame, fadeDuration),
			new AnimationFrameData(new Texture(Gdx.files.internal("ui/intro/intro11.png")), fadeDuration),
			new AnimationFrameData(new Texture(Gdx.files.internal("ui/intro/intro12.png")), 3000.0f),
			new AnimationFrameData(new Texture(Gdx.files.internal("ui/intro/intro14.png")), 3000.0f),
			new AnimationFrameData(new Texture(Gdx.files.internal("ui/intro/intro15.png")), 500.0f),
			new AnimationFrameData(new Texture(Gdx.files.internal("ui/intro/intro16.png")), 500.0f),
			new AnimationFrameData(new Texture(Gdx.files.internal("ui/intro/intro17.png")), 500.0f),
			new AnimationFrameData(new Texture(Gdx.files.internal("ui/intro/intro18.png")), 500.0f),
			new AnimationFrameData(new Texture(Gdx.files.internal("ui/intro/intro19.png")), 1500.0f),
			new AnimationFrameData(new Texture(Gdx.files.internal("ui/intro/intro20.png")), fadeDuration),
			new AnimationFrameData(blankFrame, fadeDuration),
			new AnimationFrameData(new Texture(Gdx.files.internal("ui/intro/intro21.png")), fadeDuration),
			new AnimationFrameData(new Texture(Gdx.files.internal("ui/intro/intro22.png")), 4000.0f),
			new AnimationFrameData(new Texture(Gdx.files.internal("ui/intro/intro23.png")), 4000.0f),
			new AnimationFrameData(new Texture(Gdx.files.internal("ui/intro/intro24.png")), 4000.0f),
			new AnimationFrameData(new Texture(Gdx.files.internal("ui/intro/intro25.png")), 4000.0f),
			new AnimationFrameData(new Texture(Gdx.files.internal("ui/intro/intro26.png")), 4000.0f),
			new AnimationFrameData(new Texture(Gdx.files.internal("ui/intro/intro27.png")), 2000.0f)
		));

		int totalFrameCount = 0;

		for(int i = 0; i < animationFrameData.size(); i++) {
			totalFrameCount += (int) (animationFrameData.elementAt(i).delay / 100.0f);
		}

		int currentFrameIndex = 0;
		Texture[] animationFrames = new Texture[totalFrameCount];

		for(int i = 0; i < animationFrameData.size(); i++) {
			AnimationFrameData data = animationFrameData.elementAt(i);
			int frameCount = (int) (data.delay / 100.0f);

			for(int j = 0; j < frameCount; j++) {
				animationFrames[currentFrameIndex++] = data.texture;
			}
		}

		return new Animation<Texture>(0.1f, animationFrames);
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
