package com.sectorlimit.dukeburger;

import java.util.Vector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
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
import com.badlogic.gdx.physics.box2d.World;
import com.sectorlimit.dukeburger.factory.ExplosionFactory;
import com.sectorlimit.dukeburger.factory.PickupItemFactory;
import com.sectorlimit.dukeburger.factory.PowerupsFactory;
import com.sectorlimit.dukeburger.factory.ProjectileFactory;
import com.sectorlimit.dukeburger.object.PickupItem;
import com.sectorlimit.dukeburger.powerup.Powerup;

public class Duke {

	private Vector2 m_position;
	private Vector2 m_velocity;
	private Vector2 m_acceleration;
	private boolean m_facingLeft;
	private boolean m_walking;
	private boolean m_jumping;
	private float m_walkDuration;

	private World m_world;

	private ExplosionFactory m_explosionFactory;
	private PowerupsFactory m_powerupsFactory;
	private PickupItemFactory m_pickupItemFactory;
	private ProjectileFactory m_projectileFactory;

	private boolean m_pickupItemButtonPressed;
	private PickupItem m_pickupItem;
	private Vector<Powerup> m_powerups;
	private Vector<PickupItem> m_pickupItems;

	private Texture m_idleTexture;
	private Texture m_idleHoldTexture;
	private Texture m_jumpTexture;
	private Texture m_jumpHoldTexture;
	private Texture m_walkSpriteSheetTexture;
	private Texture m_walkHoldSpriteSheetTexture;
	private Animation<TextureRegion> m_walkAnimation;
	private Animation<TextureRegion> m_walkHoldAnimation;

	private static final Vector2 SCREEN_SIZE = new Vector2(320, 180);
	private static final Vector2 DUKE_SIZE = new Vector2(16, 16);
	private static final float ACCELERATION = 150.0f;
	private static final float JUMP_VELOCITY = 90.0f;
	private static final float GRAVITY = 180.0f;
	private static final float MAX_VELOCITY = 80.0f;
	private static final int NUMBER_OF_WALKING_FRAMES = 4;
	private static final float WALK_ANIMATION_SPEED = 0.07f;

	public Duke(World world, TiledMap map) {
		m_world = world;

		m_pickupItemFactory = new PickupItemFactory(m_world);
		m_powerupsFactory = new PowerupsFactory();
		m_projectileFactory = new ProjectileFactory();
		m_explosionFactory = new ExplosionFactory();

		m_pickupItemButtonPressed = false;
		m_pickupItems = new Vector<PickupItem>();
		m_powerups = new Vector<Powerup>();

		MapLayers mapLayers = map.getLayers();
		MapObjects mapObjects = mapLayers.get("objects").getObjects();

		for(int i = 0; i < mapObjects.getCount(); i++) {
			MapObject mapObject = mapObjects.get(i);
			TextureMapObject textureMapObject = (TextureMapObject) mapObject;
			TextureRegion textureMapObjectTextureRegion = textureMapObject.getTextureRegion();
			Vector2 objectPosition = new Vector2(textureMapObject.getX() + (textureMapObjectTextureRegion.getRegionWidth() / 2.0f), textureMapObject.getY() + (textureMapObjectTextureRegion.getRegionHeight() / 2.0f));

			if(mapObject.getName().equalsIgnoreCase("wooden_box")) {
				m_pickupItems.add(m_pickupItemFactory.createBox(objectPosition));
			}
			else if(mapObject.getName().equalsIgnoreCase("barrel")) {
				m_pickupItems.add(m_pickupItemFactory.createBarrel(objectPosition));
			}
			else if(mapObject.getName().equalsIgnoreCase("cola")) {
				m_powerups.add(m_powerupsFactory.createCola(objectPosition));
			}
			else if(mapObject.getName().equalsIgnoreCase("chicken")) {
				m_powerups.add(m_powerupsFactory.createChicken(objectPosition));
			}
		}

		MapObject dukeMapObject = mapObjects.get("player_start");
		TextureMapObject textureDukeMapObject = (TextureMapObject) dukeMapObject;
		m_position = new Vector2(textureDukeMapObject.getX(), textureDukeMapObject.getY());

		m_velocity = new Vector2(0.0f, 0.0f);
		m_acceleration = new Vector2(0.0f, 0.0f);
		m_facingLeft = false;
		m_walking = false;
		m_jumping = false;
		m_walkDuration = 0.0f;

		m_idleTexture = new Texture(Gdx.files.internal("sprites/duke_idle.png"));
		m_idleHoldTexture = new Texture(Gdx.files.internal("sprites/duke_holds_idle.png"));
		m_jumpTexture = new Texture(Gdx.files.internal("sprites/duke_jump.png"));
		m_jumpHoldTexture = new Texture(Gdx.files.internal("sprites/duke_holds_jump.png"));
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
	}

	public Vector2 getPosition() {
		return m_position;
	}

	public Vector2 getCenterPosition() {
		return new Vector2(m_position).add(new Vector2(getSize()).scl(0.5f));
	}

	public Vector2 getSize() {
		return DUKE_SIZE;
	}

	public void pickupItem(PickupItem pickupItem) {
		if(m_pickupItem != null) {
			return;
		}

		m_pickupItem = pickupItem;

		m_pickupItem.pickup();
	}

	public void throwItem() {
		if(m_pickupItem == null) {
			return;
		}

		m_pickupItem.toss(m_facingLeft);
		m_pickupItem = null;
	}

	public void dropItem() {
		if(m_pickupItem == null) {
			return;
		}

		m_pickupItem.drop(m_velocity);
		m_pickupItem = null;
	}

	public void render(SpriteBatch spriteBatch) {
		float deltaTime = Gdx.graphics.getDeltaTime();

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

		boolean wasJumping = m_jumping;
		
		if(Gdx.input.isKeyPressed(Keys.SPACE) && !m_jumping) {
			m_jumping = true;
			m_velocity.y = JUMP_VELOCITY;
			m_acceleration.x = 0;
		}

		if(m_walking) {
			m_walkDuration += deltaTime;

			if(m_walkDuration >= m_walkAnimation.getAnimationDuration()) {
				m_walkDuration = m_walkDuration % m_walkAnimation.getAnimationDuration();
			}
		}

		m_acceleration.y = -GRAVITY;
		m_acceleration.scl(deltaTime);
		m_velocity.add(m_acceleration);

		if(m_acceleration.x == 0.0f) {
			m_velocity.x *= 0.8f;
		}

		if(m_velocity.x > MAX_VELOCITY) {
			m_velocity.x = MAX_VELOCITY;
		}
		else if(m_velocity.x < -MAX_VELOCITY) {
			m_velocity.x = -MAX_VELOCITY;
		}

		Vector2 scaledVelocity = new Vector2(m_velocity.x, m_velocity.y).scl(deltaTime);

		m_position.add(scaledVelocity);

		if(m_position.x < 0.0f) {
			m_position.x = 0.0f;
		}

		if(m_position.y < 0.0f) {
			m_position.y = 0.0f;

			if(wasJumping) {
				m_jumping = false;
			}
		}

		if(m_position.x + getSize().x > SCREEN_SIZE.x) {
			m_position.x = SCREEN_SIZE.x - getSize().x;
		}

		if(m_position.y + getSize().y > SCREEN_SIZE.y) {
			m_position.y = SCREEN_SIZE.y - getSize().y;
		}

		if(Gdx.input.isKeyPressed(Keys.E) || Gdx.input.isKeyPressed(Keys.F)) {
			if(!m_pickupItemButtonPressed) {
				m_pickupItemButtonPressed = true;

				if(m_pickupItem == null) {
					for(PickupItem pickupItem : m_pickupItems) {
						if(getCenterPosition().dst(pickupItem.getCenterPosition()) <= getSize().x) {
							m_pickupItem = pickupItem;
							break;
						}
					}
				}
				else {
					throwItem();
				}
			}
		}
		else {
			m_pickupItemButtonPressed = false;
		}

		if(Gdx.input.isKeyPressed(Keys.G)) {
			dropItem();
		}

		if(m_pickupItem != null) {
			m_pickupItem.setPosition(new Vector2(m_position).add(new Vector2(getSize()).scl(0.5f)).add(new Vector2(0.0f, getSize().y - 1)));
		}

		for(PickupItem pickupItem : m_pickupItems) {
			pickupItem.render(spriteBatch);
		}

		for(Powerup powerup : m_powerups) {
			if(powerup.isConsumed()) {
				continue;
			}

			if(getCenterPosition().dst(powerup.getCenterPosition()) <= getSize().x) {
				powerup.consume();
				// TODO: apply powerup effect
				continue;
			}

			powerup.render(spriteBatch);
		}

		Texture currentTexture = null;
		TextureRegion currentTextureRegion = null;

		if(m_jumping) {
			if(m_pickupItem != null) {
				currentTexture = m_jumpHoldTexture;
			}
			else {
				currentTexture = m_jumpTexture;
			}
		}
		else if(m_walking) {
			if(m_pickupItem != null) {
				currentTextureRegion = m_walkHoldAnimation.getKeyFrame(m_walkDuration, true);
			}
			else {
				currentTextureRegion = m_walkAnimation.getKeyFrame(m_walkDuration, true);
			}
		}
		else {
			if(m_pickupItem != null) {
				currentTexture = m_idleHoldTexture;
			}
			else {
				currentTexture = m_idleTexture;
			}
		}

		if(currentTexture != null) {
			spriteBatch.draw(currentTexture, m_position.x, m_position.y, 0.0f, 0.0f, currentTexture.getWidth(), currentTexture.getHeight(), 1.0f, 1.0f, 0.0f, 0, 0, currentTexture.getWidth(), currentTexture.getHeight(), m_facingLeft, false);
		}
		else if(currentTextureRegion != null) {
			if(m_facingLeft) {
				currentTextureRegion.flip(true, false);
			}

			spriteBatch.draw(currentTextureRegion, m_position.x, m_position.y);

			if(m_facingLeft) {
				currentTextureRegion.flip(true, false);
			}
		}
	}

	public void dispose() {
		m_pickupItemFactory.dispose();
		m_projectileFactory.dispose();
		m_explosionFactory.dispose();

		m_idleTexture.dispose();
		m_idleHoldTexture.dispose();
		m_jumpTexture.dispose();
		m_walkSpriteSheetTexture.dispose();
		m_walkHoldSpriteSheetTexture.dispose();
	}

}
