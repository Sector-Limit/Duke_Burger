package com.sectorlimit.dukeburger.projectile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.sectorlimit.dukeburger.CollisionCategories;
import com.sectorlimit.dukeburger.enemy.Enemy;

public abstract class Projectile {

	protected Enemy m_source;
	protected Vector2 m_position;
	protected Vector2 m_velocity;
	protected boolean m_destroyed;

	protected Texture m_texture;
	protected Animation<TextureRegion> m_animation;
	protected float m_elapsedAnimationTime;

	protected Body m_body;

	public Projectile(Enemy source, Texture texture) {
		m_source = source;
		m_destroyed = false;

		m_texture = texture;
	}

	public Projectile(Enemy source, Animation<TextureRegion> animation) {
		m_source = source;
		m_destroyed = false;

		m_elapsedAnimationTime = 0.0f;
		m_animation = animation;
	}

	public void assignPhysics(World world, Vector2 position, Vector2 direction) {
		BodyDef bodyDefinition = new BodyDef();
		bodyDefinition.type = BodyType.DynamicBody;
		bodyDefinition.position.set(position);
		m_body = world.createBody(bodyDefinition);
		m_body.setUserData(this);
		m_body.setLinearVelocity(new Vector2(direction).nor().scl(getSpeed()));
		CircleShape collisionCircleShape = new CircleShape();
		collisionCircleShape.setRadius(getRadius());
		FixtureDef sensorFixtureDefinition = new FixtureDef();
		sensorFixtureDefinition.shape = collisionCircleShape;
		sensorFixtureDefinition.isSensor = true;
		Fixture sensorCollisionFixture = m_body.createFixture(sensorFixtureDefinition);
		Filter sensorCollisionFilter = new Filter();
		sensorCollisionFilter.categoryBits = CollisionCategories.PROJECTILE;
		sensorCollisionFilter.maskBits = CollisionCategories.GROUND | CollisionCategories.DUKE;
		sensorCollisionFixture.setFilterData(sensorCollisionFilter);
		collisionCircleShape.dispose();
	}

	public void cleanup(World world) {
		world.destroyBody(m_body);
	}

	public Enemy getSource() {
		return m_source;
	}

	public Vector2 getOriginPosition() {
		return m_body != null ? m_body.getPosition() : new Vector2(0.0f, 0.0f);
	}

	public Vector2 getCenterPosition() {
		return new Vector2(getOriginPosition()).add(new Vector2(getSize()).scl(0.5f));
	}

	public void setPosition(Vector2 position) {
		m_body.setTransform(position, 0);
	}

	public abstract Vector2 getSize();

	public abstract float getRadius();

	public abstract float getSpeed();

	public boolean isDestroyed() {
		return m_destroyed;
	}

	public boolean destroy() {
		if(m_destroyed) {
			return false;
		}

		m_destroyed = true;
		
		return true;
	}

	public void render(SpriteBatch spriteBatch) {
		if(m_destroyed) {
			return;
		}

		float deltaTime = Gdx.graphics.getDeltaTime();

		Texture currentTexture = m_texture;
		TextureRegion currentTextureRegion = null;
	
		if(m_animation != null) {
			m_elapsedAnimationTime += deltaTime;
			
			if(m_elapsedAnimationTime >= m_animation.getAnimationDuration()) {
				m_elapsedAnimationTime = m_elapsedAnimationTime % m_animation.getAnimationDuration();
			}

			currentTextureRegion = m_animation.getKeyFrame(m_elapsedAnimationTime, true);
		}

		Vector2 renderOrigin = new Vector2(getOriginPosition()).sub(new Vector2(getSize()).scl(0.5f));

		if(currentTexture != null) {
			spriteBatch.draw(currentTexture, renderOrigin.x, renderOrigin.y, getSize().x / 2, getSize().y / 2, currentTexture.getWidth(), currentTexture.getHeight(), 1.0f, 1.0f, (float) Math.toDegrees(m_body.getAngle()), 0, 0, currentTexture.getWidth(), currentTexture.getHeight(), false, false);
		}
		else if(currentTextureRegion != null) {
			spriteBatch.draw(currentTextureRegion, renderOrigin.x, renderOrigin.y);
		}
	}
}
