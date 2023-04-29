package com.sectorlimit.dukeburger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Duke {

	private Vector2 m_position;
	private Vector2 m_velocity;
	private Vector2 m_acceleration;
	private boolean m_facingLeft;
	private boolean m_walking;
	private boolean m_jumping;
	private boolean m_holdingItem;
	private float m_walkDuration;

	private Texture m_idleTexture;
	private Texture m_idleHoldTexture;
	private Texture m_jumpTexture;
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
	private static final float WALK_ANIMATION_SPEED = 0.1f;

	public Duke() {
		m_position = new Vector2(100.0f, 0.0f);
		m_velocity = new Vector2(0.0f, 0.0f);
		m_acceleration = new Vector2(0.0f, 0.0f);
		m_facingLeft = false;
		m_walking = false;
		m_jumping = false;
		m_holdingItem = false;
		m_walkDuration = 0.0f;

		m_idleTexture = new Texture(Gdx.files.internal("sprites/duke_idle.png"));
		m_idleHoldTexture = new Texture(Gdx.files.internal("sprites/duke_holds_idle.png"));
		m_jumpTexture = new Texture(Gdx.files.internal("sprites/duke_jump.png"));
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
			m_velocity.x *= 0.9f;
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

		if(m_position.x + DUKE_SIZE.x > SCREEN_SIZE.x) {
			m_position.x = SCREEN_SIZE.x;
		}

		if(m_position.y + DUKE_SIZE.y > SCREEN_SIZE.y) {
			m_position.y = SCREEN_SIZE.y;
		}

		Texture currentTexture = null;
		TextureRegion currentTextureRegion = null;

		if(m_jumping) {
			currentTexture = m_jumpTexture;
		}
		else if(m_walking) {
			if(m_holdingItem) {
				currentTextureRegion = m_walkHoldAnimation.getKeyFrame(m_walkDuration, true);
			}
			else {
				currentTextureRegion = m_walkAnimation.getKeyFrame(m_walkDuration, true);
			}
		}
		else {
			if(m_holdingItem) {
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
		m_idleTexture.dispose();
		m_idleHoldTexture.dispose();
		m_jumpTexture.dispose();
		m_walkSpriteSheetTexture.dispose();
		m_walkHoldSpriteSheetTexture.dispose();
	}

}
