package com.sectorlimit.dukeburger;

import java.util.Vector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.sectorlimit.dukeburger.enemy.Enemy;
import com.sectorlimit.dukeburger.enemy.Enforcer;
import com.sectorlimit.dukeburger.enemy.OctaBaby;
import com.sectorlimit.dukeburger.enemy.OctaBaby.Type;
import com.sectorlimit.dukeburger.factory.EnemyFactory;
import com.sectorlimit.dukeburger.factory.ExplosionFactory;
import com.sectorlimit.dukeburger.factory.PickupItemFactory;
import com.sectorlimit.dukeburger.factory.PowerupsFactory;
import com.sectorlimit.dukeburger.factory.StaticObjectFactory;
import com.sectorlimit.dukeburger.object.Barrel;
import com.sectorlimit.dukeburger.object.Box;
import com.sectorlimit.dukeburger.object.Burger;
import com.sectorlimit.dukeburger.object.Door;
import com.sectorlimit.dukeburger.object.Explosion;
import com.sectorlimit.dukeburger.object.PickupItem;
import com.sectorlimit.dukeburger.object.PigCop;
import com.sectorlimit.dukeburger.object.StaticObject;
import com.sectorlimit.dukeburger.powerup.Chicken;
import com.sectorlimit.dukeburger.powerup.Coin;
import com.sectorlimit.dukeburger.powerup.Cola;
import com.sectorlimit.dukeburger.powerup.Powerup;
import com.sectorlimit.dukeburger.projectile.Projectile;

public class Duke implements ContactListener, HUDDataProvider {

	private Vector2 m_acceleration;
	private boolean m_facingLeft;
	private boolean m_grounded;
	private boolean m_walking;
	private boolean m_jumping;
	private float m_jumpTimeElapsed;
	private float m_walkDuration;
	private boolean m_underAttack;
	private boolean m_recentlyAttacked;
	private float m_attackCooldownTimeElapsed;
	private boolean m_alive;
	private boolean m_wasAlive;
	private int m_health;
	private int m_lives;
	private int m_coins;
	private boolean m_levelCompleted;
	private boolean m_levelEnded;
	private float m_levelCompletedTimeElapsed;

	private World m_world;
	private Body m_body;

	private HUD m_hud;
	private ExplosionFactory m_explosionFactory;
	private EnemyFactory m_enemyFactory;
	private PowerupsFactory m_powerupsFactory;
	private PickupItemFactory m_pickupItemFactory;
	private ProjectileSystem m_projectileSystem;
	private StaticObjectFactory m_staticObjectFactory;

	private boolean m_tossingSomething;
	private boolean m_pickupItemButtonPressed;
	private PickupItem m_pickupItem;
	private Enemy m_pickupEnemy;
	private Door m_door;
	private PigCop m_pigCop;
	private Vector<Powerup> m_powerups;
	private Vector<PickupItem> m_pickupItems;
	private Vector<Enemy> m_enemies;
	private Vector<Explosion> m_explosions;
	private Vector<StaticObject> m_staticObjects;

	private Texture m_idleTexture;
	private Texture m_idleHoldTexture;
	private Texture m_jumpTexture;
	private Texture m_jumpHoldTexture;
	private Texture m_tossItemTexture;
	private Texture m_walkSpriteSheetTexture;
	private Texture m_walkHoldSpriteSheetTexture;
	private Animation<TextureRegion> m_walkAnimation;
	private Animation<TextureRegion> m_walkHoldAnimation;

	private Sound m_hitSound;
	private Sound m_squishSound;
	private Sound m_pickupSound;
	private Sound m_tossSound;
	private Sound m_extraLifeSound;
	private Sound m_deathSound;
	private Sound m_winSound;

	private DukeListener m_listener;

	public static final int MAX_HEALTH = 3;
	public static final int MAX_LIVES = 3;
	private static final Vector2 DUKE_SIZE = new Vector2(16, 16);
	private static final float ACCELERATION = 150.0f;
	private static final float JUMP_VELOCITY = 220.0f;
	private static final float JUMP_DURATION = 0.3f;
	private static final float JUMP_HOLDING_DURATION = 0.125f;
	private static final float TOSS_VELOCITY = 75.0f;
	private static final float MAX_HORIZONTAL_VELOCITY = 80.0f;
	private static final int NUMBER_OF_WALKING_FRAMES = 4;
	private static final float WALK_ANIMATION_SPEED = 0.07f;
	private static final float ATTACK_COOLDOWN = 3.0f;
	private static final float RECENTLY_ATTACKED_DURATION = 1.0f;
	private static final float DOOR_OPEN_DISTANCE = DUKE_SIZE.y + (16.0f * 1.5f);
	private static final float LEVEL_COMPLETED_DELAY = 3.0f;

	public Duke(World world, TiledMap map) {
		this(world, map, MAX_LIVES, 0);
	}

	public Duke(World world, TiledMap map, int lives, int coins) {
		m_world = world;
		m_world.setContactListener(this);

		m_underAttack = false;
		m_recentlyAttacked = false;
		m_attackCooldownTimeElapsed = 0.0f;
		m_alive = true;
		m_wasAlive = true;
		m_health = MAX_HEALTH;
		m_lives = lives;
		m_coins = coins;
		m_levelCompleted = false;
		m_levelEnded = false;
		m_levelCompletedTimeElapsed = 0.0f;
		m_hud = new HUD(this);
		m_pickupItemFactory = new PickupItemFactory(m_world);
		m_projectileSystem = new ProjectileSystem(m_world);
		m_enemyFactory = new EnemyFactory(m_projectileSystem, m_world);
		m_powerupsFactory = new PowerupsFactory();
		m_explosionFactory = new ExplosionFactory();
		m_staticObjectFactory = new StaticObjectFactory(m_world);

		m_grounded = false;
		m_tossingSomething = false;
		m_pickupItemButtonPressed = false;
		m_pickupItems = new Vector<PickupItem>();
		m_powerups = new Vector<Powerup>();
		m_enemies = new Vector<Enemy>();
		m_explosions = new Vector<Explosion>();
		m_staticObjects = new Vector<StaticObject>();

		MapLayers mapLayers = map.getLayers();
		MapObjects mapObjects = mapLayers.get("objects").getObjects();

		for(int i = 0; i < mapObjects.getCount(); i++) {
			MapObject mapObject = mapObjects.get(i);
			TextureMapObject textureMapObject = (TextureMapObject) mapObject;
			TextureRegion textureMapObjectTextureRegion = textureMapObject.getTextureRegion();
			Vector2 objectPosition = new Vector2(textureMapObject.getX() + (textureMapObjectTextureRegion.getRegionWidth() / 2.0f), textureMapObject.getY() + (textureMapObjectTextureRegion.getRegionHeight() / 2.0f));

			if(mapObject.getName() == null) {
				System.err.println("Map object is missing name.");
			}
			else if(mapObject.getName().equalsIgnoreCase("wooden_box")) {
				m_pickupItems.add(m_pickupItemFactory.createBox(objectPosition));
			}
			else if(mapObject.getName().equalsIgnoreCase("barrel")) {
				m_pickupItems.add(m_pickupItemFactory.createBarrel(objectPosition));
			}
			else if(mapObject.getName().equalsIgnoreCase("burger")) {
				m_pickupItems.add(m_pickupItemFactory.createBurger(objectPosition));
			}
			else if(mapObject.getName().equalsIgnoreCase("cola")) {
				m_powerups.add(m_powerupsFactory.createCola(objectPosition));
			}
			else if(mapObject.getName().equalsIgnoreCase("chicken")) {
				m_powerups.add(m_powerupsFactory.createChicken(objectPosition));
			}
			else if(mapObject.getName().equalsIgnoreCase("octababy")) {
				m_enemies.add(m_enemyFactory.createOctaBaby(Type.Green, objectPosition));
			}
			else if(mapObject.getName().equalsIgnoreCase("octababy_blue")) {
				m_enemies.add(m_enemyFactory.createOctaBaby(Type.Blue, objectPosition));
			}
			else if(mapObject.getName().equalsIgnoreCase("octa")) {
				m_enemies.add(m_enemyFactory.createOcta(objectPosition));
			}
			else if(mapObject.getName().equalsIgnoreCase("enforcer")) {
				m_enemies.add(m_enemyFactory.createEnforcer(objectPosition));
			}
			else if(mapObject.getName().equalsIgnoreCase("duke_rest") || mapObject.getName().equalsIgnoreCase("duke_burger")) {
				m_staticObjects.add(m_staticObjectFactory.createRestaurant(objectPosition));
			}
			else if(mapObject.getName().equalsIgnoreCase("door")) {
				m_door = m_staticObjectFactory.createDoor(objectPosition);
				m_staticObjects.add(m_door);
			}
			else if(mapObject.getName().equalsIgnoreCase("lava")) {
				m_staticObjects.add(m_staticObjectFactory.createLava(objectPosition));
			}
			else if(mapObject.getName().equalsIgnoreCase("finish_text_1")) {
				m_staticObjects.add(m_staticObjectFactory.createFinishText(1, objectPosition));
			}
			else if(mapObject.getName().equalsIgnoreCase("finish_text_2")) {
				m_staticObjects.add(m_staticObjectFactory.createFinishText(2, objectPosition));
			}
			else if(mapObject.getName().equalsIgnoreCase("pigcop_1") || mapObject.getName().equalsIgnoreCase("pigcop_finish")) {
				m_pigCop = m_staticObjectFactory.createPigCop(1, objectPosition);
				m_staticObjects.add(m_pigCop);
			}
			else if(mapObject.getName().equalsIgnoreCase("pigcop_2")) {
				m_staticObjects.add(m_staticObjectFactory.createPigCop(2, objectPosition));
			}
			else if(mapObject.getName().equalsIgnoreCase("coin")) {
				m_powerups.add(m_powerupsFactory.createCoin(objectPosition));
			}
			else if(!mapObject.getName().equalsIgnoreCase("player_start")){
				System.err.println("Unexpected object name: " + mapObject.getName());
			}
		}

		if(m_door == null) {
			System.err.println("Map is missing 'door' object.");
		}

		Vector2 spawnPosition = new Vector2(0.0f, 0.0f);
		MapObject dukeMapObject = mapObjects.get("player_start");

		if(dukeMapObject != null) {
			TextureMapObject textureDukeMapObject = (TextureMapObject) dukeMapObject;
			spawnPosition = new Vector2(textureDukeMapObject.getX(), textureDukeMapObject.getY()).add(new Vector2(getSize()).scl(0.5f));
		}
		else {
			System.err.println("Missing player spawn position objece with name: 'player_start'.");
		}

		BodyDef bodyDefinition = new BodyDef();
		bodyDefinition.type = BodyType.DynamicBody;
		bodyDefinition.position.set(spawnPosition);
		bodyDefinition.fixedRotation = true;
		m_body = world.createBody(bodyDefinition);
		m_body.setUserData(this);
		PolygonShape polygonCollisionShape = new PolygonShape();
		polygonCollisionShape.setAsBox(getSize().x / 2.0f, getSize().y / 2.0f);

		FixtureDef fixtureDefinition = new FixtureDef();
		fixtureDefinition.shape = polygonCollisionShape;
		fixtureDefinition.density = 0.5f;
		fixtureDefinition.friction = 0.2f;
		fixtureDefinition.restitution = 0.1f;
		Fixture collisionFixture = m_body.createFixture(fixtureDefinition);
		polygonCollisionShape.dispose();
		Filter collisionFilter = new Filter();
		collisionFilter.categoryBits = CollisionCategories.DUKE;
		collisionFilter.maskBits = CollisionCategories.GROUND | CollisionCategories.OBJECT | CollisionCategories.ENEMY_SENSOR | CollisionCategories.DOOR | CollisionCategories.PROJECTILE;
		collisionFixture.setFilterData(collisionFilter);

		m_acceleration = new Vector2(0.0f, 0.0f);
		m_facingLeft = false;
		m_walking = false;
		m_jumping = false;
		m_jumpTimeElapsed = 0.0f;
		m_walkDuration = 0.0f;

		m_idleTexture = new Texture(Gdx.files.internal("sprites/duke_idle.png"));
		m_idleHoldTexture = new Texture(Gdx.files.internal("sprites/duke_holds_idle.png"));
		m_jumpTexture = new Texture(Gdx.files.internal("sprites/duke_jump.png"));
		m_jumpHoldTexture = new Texture(Gdx.files.internal("sprites/duke_holds_jump.png"));
		m_tossItemTexture = new Texture(Gdx.files.internal("sprites/duke_toss.png"));
		m_walkSpriteSheetTexture = new Texture(Gdx.files.internal("sprites/duke_walk.png"));
		m_walkHoldSpriteSheetTexture = new Texture(Gdx.files.internal("sprites/duke_holds_walk.png"));

		TextureRegion[][] walkTextureRegion = TextureRegion.split(m_walkSpriteSheetTexture, m_walkSpriteSheetTexture.getWidth() / NUMBER_OF_WALKING_FRAMES, m_walkSpriteSheetTexture.getHeight());
		TextureRegion[] walkFrames = new TextureRegion[NUMBER_OF_WALKING_FRAMES];

		for (int i = 0; i < NUMBER_OF_WALKING_FRAMES; i++) {
			walkFrames[i] = walkTextureRegion[0][i];
		}

		m_walkAnimation = new Animation<TextureRegion>(WALK_ANIMATION_SPEED, walkFrames);

		TextureRegion[][] walkHoldTextureRegion = TextureRegion.split(m_walkHoldSpriteSheetTexture, m_walkHoldSpriteSheetTexture.getWidth() / NUMBER_OF_WALKING_FRAMES, m_walkHoldSpriteSheetTexture.getHeight());
		TextureRegion[] walkHoldFrames = new TextureRegion[NUMBER_OF_WALKING_FRAMES];

		for (int i = 0; i < NUMBER_OF_WALKING_FRAMES; i++) {
			walkHoldFrames[i] = walkHoldTextureRegion[0][i];
		}

		m_walkHoldAnimation = new Animation<TextureRegion>(WALK_ANIMATION_SPEED, walkHoldFrames);

		m_hitSound = Gdx.audio.newSound(Gdx.files.internal("sounds/Squish.wav"));
		m_squishSound = Gdx.audio.newSound(Gdx.files.internal("sounds/GetHit.wav"));
		m_pickupSound = Gdx.audio.newSound(Gdx.files.internal("sounds/PickUp.wav"));
		m_tossSound = Gdx.audio.newSound(Gdx.files.internal("sounds/Toss.wav"));
		m_extraLifeSound = Gdx.audio.newSound(Gdx.files.internal("sounds/Chicken.wav"));
		m_deathSound = Gdx.audio.newSound(Gdx.files.internal("sounds/DeathSound.wav"));
		m_winSound = Gdx.audio.newSound(Gdx.files.internal("sounds/WinSound.wav"));
	}

	public int getHealth() {
		return m_health;
	}

	public int getLives() {
		return m_lives;
	}

	public int getCoins() {
		return m_coins;
	}

	public boolean addHealth() {
		if(m_health >= MAX_HEALTH) {
			return false;
		}

		m_health++;

		return true;
	}

	public void removeHealth() {
		m_health--;

		if(m_health <= 0) {
			kill();
		}
	}

	public void addLife() {
		m_lives++;
	}

	public void removeLife() {
		if(m_lives <= 0) {
			return;
		}

		m_lives--;
	}

	public void addCoin() {
		m_coins++;

		if(m_coins >= 100) {
			m_coins -= 100;

			m_extraLifeSound.play();

			addLife();
		}
	}

	public Vector2 getOriginPosition() {
		return m_body.getPosition();
	}

	public Vector2 getCenterPosition() {
		return new Vector2(getOriginPosition()).add(new Vector2(getSize()).scl(0.5f));
	}

	public Vector2 getSize() {
		return DUKE_SIZE;
	}

	public boolean isHoldingSomething() {
		return m_pickupItem != null || m_pickupEnemy != null;
	}

	public boolean isHoldingBurger() {
		return m_pickupItem instanceof Burger;
	}

	public void pickupItem(PickupItem pickupItem) {
		if(isHoldingSomething()) {
			return;
		}

		m_pickupItem = pickupItem;
		m_pickupItem.pickup();

		m_pickupSound.play();
	}

	public void tossItem() {
		if(m_pickupItem == null) {
			return;
		}
		else if(m_pickupItem instanceof Burger && m_door.isOpen()) {
			return;
		}

		m_body.setLinearVelocity(new Vector2(m_body.getLinearVelocity()).add(new Vector2(0.0f, TOSS_VELOCITY)));
		m_acceleration.x = 0;
		m_tossingSomething = true;
		m_pickupItem.toss(m_facingLeft);
		m_pickupItem = null;

		m_tossSound.play();
	}

	public void dropItem() {
		if(m_pickupItem == null) {
			return;
		}

		m_pickupItem.drop(m_body.getLinearVelocity());
		m_pickupItem = null;
	}

	public void pickupEnemy(Enemy enemy) {
		if(isHoldingSomething() || !enemy.isPickupable()) {
			return;
		}

		m_pickupEnemy = enemy;
		m_pickupEnemy.pickup();

		m_pickupSound.play();
	}

	public void tossEnemy() {
		if(m_pickupEnemy == null) {
			return;
		}

		m_body.setLinearVelocity(new Vector2(m_body.getLinearVelocity()).add(new Vector2(0.0f, TOSS_VELOCITY)));
		m_acceleration.x = 0;
		m_tossingSomething = true;
		m_pickupEnemy.toss(m_facingLeft);
		m_pickupEnemy = null;

		m_tossSound.play();
	}

	public void kill() {
		removeLife();

		m_alive = false;

		m_deathSound.play(0.15f);
	}

	public boolean completeLevel() {
		if(m_levelCompleted) {
			return false;
		}
		
		m_levelCompleted = true;
		m_levelEnded = false;
		m_levelCompletedTimeElapsed = 0.0f;

		if(m_pigCop != null) {
			m_pigCop.setVisible(true);
		}

		m_winSound.play(0.1f);

		return true;
	}

	public boolean onAttackedBy(Enemy enemy) {
		if(m_underAttack) {
			return false;
		}

		m_underAttack = true;
		m_attackCooldownTimeElapsed = 0.0f;

		removeHealth();

		m_hitSound.play();

		enemy.attack();

		return true;
	}

	public void setListener(DukeListener listener) {
		m_listener = listener;
	}

	public void render(SpriteBatch spriteBatch) {
		float deltaTime = Gdx.graphics.getDeltaTime();

		if(m_levelCompleted) {
			if(m_levelCompletedTimeElapsed == 0.0f) {
				m_listener.onLevelCompleted();
			}

			if(!m_levelEnded) {
				m_levelCompletedTimeElapsed += deltaTime;
	
				if(m_levelCompletedTimeElapsed >= LEVEL_COMPLETED_DELAY) {
					m_levelEnded = true;

					m_listener.onLevelEnded();
				}
			}
		}

		if(!m_alive) {
			if(m_wasAlive) {
				m_wasAlive = false;
				m_listener.onKilled();
			}

			return;
		}

		if(m_underAttack) {
			m_attackCooldownTimeElapsed += deltaTime;
			
			m_recentlyAttacked = m_attackCooldownTimeElapsed < RECENTLY_ATTACKED_DURATION;

			if(m_attackCooldownTimeElapsed > ATTACK_COOLDOWN) {
				m_underAttack = false;
				m_recentlyAttacked = false;
				m_attackCooldownTimeElapsed = 0.0f;
			}
		}

		m_walking = false;

		if(Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT)) {
			m_facingLeft = true;
			m_walking = true;
			m_acceleration.x = -ACCELERATION;
		}
		else if(Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.RIGHT)) {
			m_facingLeft = false;
			m_walking = true;
			m_acceleration.x = ACCELERATION;
		}
		else {
			m_acceleration.x = 0;
		}

		Vector2 newVelocity = new Vector2(m_body.getLinearVelocity());
		
		if((Gdx.input.isKeyPressed(Keys.SPACE) || Gdx.input.isKeyPressed(Keys.Z)) && !m_jumping && !m_tossingSomething && m_grounded) {
			m_jumping = true;
			m_jumpTimeElapsed = 0.0f;

			newVelocity.add(new Vector2(0.0f, JUMP_VELOCITY));
			m_acceleration.x = 0;
		}

		if(m_jumping) {
			m_jumpTimeElapsed += deltaTime;
	
			if(m_jumpTimeElapsed < (isHoldingSomething() ? JUMP_HOLDING_DURATION : JUMP_DURATION)) {
				newVelocity.add(new Vector2(0.0f, JUMP_VELOCITY));
			}
		}

		if(m_walking) {
			m_walkDuration += deltaTime;

			if(m_walkDuration >= m_walkAnimation.getAnimationDuration()) {
				m_walkDuration = m_walkDuration % m_walkAnimation.getAnimationDuration();
			}
		}

		m_acceleration.scl(deltaTime);
		newVelocity.add(m_acceleration);

		if(m_acceleration.x == 0.0f) {
			newVelocity.x *= 0.8f;
		}

		if(newVelocity.x > MAX_HORIZONTAL_VELOCITY) {
			newVelocity.x = MAX_HORIZONTAL_VELOCITY;
		}
		else if(newVelocity.x < -MAX_HORIZONTAL_VELOCITY) {
			newVelocity.x = -MAX_HORIZONTAL_VELOCITY;
		}

		m_body.setLinearVelocity(newVelocity);

		if(m_body.getPosition().y + getSize().y < 0.0f) {
			kill();
		}

		if(Gdx.input.isKeyPressed(Keys.E) || Gdx.input.isKeyPressed(Keys.F) || Gdx.input.isKeyPressed(Keys.X)) {
			if(!m_pickupItemButtonPressed) {
				m_pickupItemButtonPressed = true;

				if(!isHoldingSomething()) {
					for(Enemy enemy : m_enemies) {
						if(enemy.isPickupable() && getOriginPosition().dst(enemy.getOriginPosition()) <= (getSize().x / 2.0f) + (enemy.getSize().x / 2.0f)) {
							pickupEnemy(enemy);
							break;
						}
					}

					if(!isHoldingSomething()) {
						for(PickupItem pickupItem : m_pickupItems) {
							if(getOriginPosition().dst(pickupItem.getOriginPosition()) <= (getSize().x / 2.0f) + pickupItem.getSize().x) {
								pickupItem(pickupItem);
								break;
							}
						}
					}
				}
				else {
					if(m_pickupItem != null) {
						tossItem();
					}
					else if(m_pickupEnemy != null) {
						tossEnemy();
					}
				}
			}
		}
		else {
			m_pickupItemButtonPressed = false;
		}

		float pickupObjectVerticalOffset = -1.0f;

		if(m_pickupItem != null) {
			if(m_pickupItem instanceof Burger) {
				pickupObjectVerticalOffset = -2.0f;
			}

			m_pickupItem.setPosition(getOriginPosition().add(new Vector2(1.0f, getSize().y + pickupObjectVerticalOffset)));
		}
		else if(m_pickupEnemy != null) {
			m_pickupEnemy.setPosition(getOriginPosition().add(new Vector2(1.0f, getSize().y + pickupObjectVerticalOffset)));
		}

		for(StaticObject staticObject : m_staticObjects) {
			staticObject.render(spriteBatch);
		}

		Vector<PickupItem> pickupItemsToRemove = new Vector<PickupItem>();

		for(PickupItem pickupItem : m_pickupItems) {
			if(pickupItem.isDestroyed()) {
				pickupItemsToRemove.add(pickupItem);
				continue;
			}

			pickupItem.render(spriteBatch);
		}

		for(PickupItem pickupItem : pickupItemsToRemove) {
			if(pickupItem instanceof Burger) {
				kill();
			}

			pickupItem.cleanup(m_world);
			m_pickupItems.remove(pickupItem);
		}

		Vector<Powerup> powerupsToRemove = new Vector<Powerup>();

		for(Powerup powerup : m_powerups) {
			if(powerup.isConsumed()) {
				continue;
			}

			if(getCenterPosition().dst(powerup.getCenterPosition()) <= getSize().x) {
				boolean consume = true;

				if(powerup instanceof Cola) {
					if(!addHealth()) {
						consume = false;
					}
				}
				else if(powerup instanceof Chicken) {
					addLife();			
				}
				else if(powerup instanceof Coin) {
					addCoin();
				}

				if(consume) {
					powerup.consume();
					powerupsToRemove.add(powerup);

					continue;
				}
			}

			powerup.render(spriteBatch);
		}

		for(Powerup powerup : powerupsToRemove) {
			m_powerups.remove(powerup);
		}

		Vector<Enemy> enemiesToRemove = new Vector<Enemy>();

		for(Enemy enemy : m_enemies) {
			if(enemy.isDestroyed()) {
				enemiesToRemove.add(enemy);
				continue;
			}

			if(enemy instanceof Enforcer) {
				Enforcer enforcer = (Enforcer) enemy;
				enforcer.setFacingLeft(enforcer.getOriginPosition().x > getOriginPosition().x);
			}

			enemy.render(spriteBatch);
		}

		for(Enemy enemy : enemiesToRemove) {
			enemy.cleanup(m_world);
			m_enemies.remove(enemy);
		}

		Vector<Explosion> explosionsToRemove = new Vector<Explosion>();

		for(Explosion explosion : m_explosions) {
			if(explosion.isExpired()) {
				explosionsToRemove.add(explosion);
			}
			else {
				explosion.render(spriteBatch);
			}
		}

		for(Explosion explosion : explosionsToRemove) {
			m_explosions.remove(explosion);
		}

		Texture currentTexture = null;
		TextureRegion currentTextureRegion = null;

		if(m_tossingSomething) {
			currentTexture = m_tossItemTexture;
		}
		else if(m_jumping) {
			if(isHoldingSomething()) {
				currentTexture = m_jumpHoldTexture;
			}
			else {
				currentTexture = m_jumpTexture;
			}
		}
		else if(m_walking) {
			if(isHoldingSomething()) {
				currentTextureRegion = m_walkHoldAnimation.getKeyFrame(m_walkDuration, true);
			}
			else {
				currentTextureRegion = m_walkAnimation.getKeyFrame(m_walkDuration, true);
			}
		}
		else {
			if(isHoldingSomething()) {
				currentTexture = m_idleHoldTexture;
			}
			else {
				currentTexture = m_idleTexture;
			}
		}

		if(m_pickupItem instanceof Burger) {
			if(getOriginPosition().dst(m_door.getOriginPosition()) <= DOOR_OPEN_DISTANCE) {
				m_door.open();
			}
			else {
				m_door.close();
			}
		}
		else {
			m_door.close();
		}

		Vector2 renderOrigin = new Vector2(getOriginPosition()).sub(new Vector2(getSize()).scl(0.5f));

		if(currentTexture != null) {
			spriteBatch.draw(currentTexture, renderOrigin.x, renderOrigin.y, 0.0f, 0.0f, currentTexture.getWidth(), currentTexture.getHeight(), 1.0f, 1.0f, 0.0f, 0, 0, currentTexture.getWidth(), currentTexture.getHeight(), m_facingLeft, false);
		}
		else if(currentTextureRegion != null) {
			if(m_facingLeft) {
				currentTextureRegion.flip(true, false);
			}

			spriteBatch.draw(currentTextureRegion, renderOrigin.x, renderOrigin.y);

			if(m_facingLeft) {
				currentTextureRegion.flip(true, false);
			}
		}

		m_projectileSystem.render(spriteBatch);

		m_grounded = false;
	}

	public void renderHUD(SpriteBatch spriteBatch) {
		m_hud.render(spriteBatch);
	}

	@Override
	public void beginContact(Contact contact) {
		Object contactObjectA = contact.getFixtureA().getBody().getUserData();
		Object contactObjectB = contact.getFixtureB().getBody().getUserData();
		Fixture contactFixture = null;
		Object contactObject = null;
		boolean isPlayerContact = false;

		if(contactObjectA instanceof Duke) {
			contactFixture = contact.getFixtureB();
			isPlayerContact = true;
		}
		else if(contactObjectB instanceof Duke) {
			contactFixture = contact.getFixtureA();
			isPlayerContact = true;
		}

		if(contactFixture != null) {
			contactObject = contactFixture.getBody().getUserData();
		}

		if(isPlayerContact) {
			if(contactObject == null) {
				m_jumping = false;
				m_tossingSomething = false;
				m_grounded = true;
			}
			else if(contactObject instanceof PickupItem) {
				m_jumping = false;
				m_tossingSomething = false;
				m_grounded = true;
			}
			else if(contactObject instanceof Enemy) {
				Enemy enemy = (Enemy) contactObject;

				if(enemy instanceof OctaBaby) {
					OctaBaby octaBaby = (OctaBaby) enemy;

					if(m_jumping && !m_recentlyAttacked && contactFixture.isSensor()) {
						m_squishSound.play();
						octaBaby.squish();
					}
					else if(!octaBaby.isSquished()) {
						onAttackedBy(enemy);
					}
				}
				else {
					onAttackedBy(enemy);
				}
			}
			else if(contactObject instanceof Projectile) {
				Projectile projectile = (Projectile) contactObject;

				onAttackedBy(projectile.getSource());

				projectile.destroy();
			}
			else if(contactObject instanceof String) {
				String colliderName = (String) contactObject;

				if(colliderName.equalsIgnoreCase("finish")) {
					completeLevel();
				}
			}
		}
		else {
			Projectile contactProjectile = null;
			Object projectileTarget = null;

			if(contactObjectA instanceof Projectile) {
				contactProjectile = (Projectile) contactObjectA;
				projectileTarget = contactObjectB;
			}
			else if(contactObjectB instanceof Projectile) {
				contactProjectile = (Projectile) contactObjectB;
				projectileTarget = contactObjectA;
			}

			if(contactProjectile != null) {
				if(projectileTarget == null) {
					contactProjectile.destroy();
				}
			}
			else {
				PickupItem tossedPickupItem = null;
				Object otherContactObject = null;
	
				if(contactObjectA instanceof PickupItem && ((PickupItem) contactObjectA).isTossed()) {
					tossedPickupItem = (PickupItem) contactObjectA;
					otherContactObject = contactObjectB;
				}
				else if(contactObjectB instanceof PickupItem && ((PickupItem) contactObjectB).isTossed()) {
					tossedPickupItem = (PickupItem) contactObjectB;
					otherContactObject = contactObjectA;
				}
	
				if(tossedPickupItem != null) {
					if(tossedPickupItem instanceof Barrel) {
						m_explosions.add(m_explosionFactory.createExplosion(new Vector2(tossedPickupItem.getOriginPosition())));
					}
	
					tossedPickupItem.onImpact();
					tossedPickupItem.destroy();
	
					if(otherContactObject instanceof Enemy) {
						Enemy enemy = (Enemy) otherContactObject;
						enemy.kill();
					}
					else if(tossedPickupItem instanceof Box) {
						int randomNumber = (int) (Math.random() * 100.0);
						Vector2 newItemPosition = new Vector2(tossedPickupItem.getOriginPosition());
	
						if(randomNumber < 10) {
							m_powerups.add(m_powerupsFactory.createChicken(newItemPosition));
						}
						else if(randomNumber < 30) {
							m_powerups.add(m_powerupsFactory.createCola(newItemPosition));
						}
					}
				}
			}
		}
	}

	@Override
	public void endContact(Contact contact) { }

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) { }

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) { }

	public void dispose() {
		m_pickupItemFactory.dispose();
		m_projectileSystem.dispose();
		m_explosionFactory.dispose();

		m_idleTexture.dispose();
		m_idleHoldTexture.dispose();
		m_jumpTexture.dispose();
		m_walkSpriteSheetTexture.dispose();
		m_walkHoldSpriteSheetTexture.dispose();
	}

}
