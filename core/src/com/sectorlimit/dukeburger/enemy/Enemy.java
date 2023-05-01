package com.sectorlimit.dukeburger.enemy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.sectorlimit.dukeburger.CollisionCategories;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;

public abstract class Enemy {

	protected boolean m_facingLeft;
	protected boolean m_alive;
	protected boolean m_wasAlive;
	protected boolean m_destroyed;
	protected boolean m_pickedUp;
	protected boolean m_tossed;

	protected Body m_body;

	public Enemy() {
		if(shouldRandomizeInitialDirection()) {
			m_facingLeft = (byte) (Math.random() * 2.0) == 0;
		}
		else {
			m_facingLeft = false;
		}

		m_alive = true;
		m_wasAlive = true;
		m_destroyed = false;
		m_pickedUp = false;
		m_tossed = false;
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

	public void assignPhysics(World world, Vector2 position) {
		BodyDef bodyDefinition = new BodyDef();
		bodyDefinition.type = BodyType.DynamicBody;
		bodyDefinition.position.set(position);
		bodyDefinition.fixedRotation = true;
		m_body = world.createBody(bodyDefinition);
		m_body.setUserData(this);
		PolygonShape polygonCollisionShape = new PolygonShape();
		polygonCollisionShape.setAsBox(getSize().x / 2.0f, getSize().y / 2.0f);
		FixtureDef fixtureDefinition = new FixtureDef();
		fixtureDefinition.shape = polygonCollisionShape;
		fixtureDefinition.density = 0.5f;
		fixtureDefinition.friction = 0.0f;
		fixtureDefinition.restitution = 0.1f;
		Fixture collisionFixture = m_body.createFixture(fixtureDefinition);
		Filter collisionFilter = new Filter();
		collisionFilter.categoryBits = CollisionCategories.ENEMY;
		collisionFilter.maskBits = CollisionCategories.GROUND | CollisionCategories.ENEMY_BOUNDARY | CollisionCategories.OBJECT;
		collisionFixture.setFilterData(collisionFilter);
		FixtureDef sensorFixtureDefinition = new FixtureDef();
		sensorFixtureDefinition.shape = polygonCollisionShape;
		sensorFixtureDefinition.isSensor = true;
		Fixture sensorCollisionFixture = m_body.createFixture(sensorFixtureDefinition);
		Filter sensorCollisionFilter = new Filter();
		sensorCollisionFilter.categoryBits = CollisionCategories.ENEMY_SENSOR;
		sensorCollisionFilter.maskBits = CollisionCategories.DUKE;
		sensorCollisionFixture.setFilterData(sensorCollisionFilter);
		polygonCollisionShape.dispose();
	}

	public boolean isFacingLeft() {
		return m_facingLeft;
	}

	public boolean isPickupable() {
		return false;
	}

	public boolean isPickedUp() {
		return m_pickedUp;
	}

	public boolean isTossed() {
		return m_tossed;
	}

	public boolean pickup() {
		if(!isPickupable()) {
			return false;
		}

		m_pickedUp = true;
		m_body.setActive(false);

		return true;
	}

	public boolean toss(boolean tossLeft) {
		if(!isPickedUp()) {
			return false;
		}

		m_pickedUp = false;
		m_tossed = true;
		Fixture firstFixture = m_body.getFixtureList().first();
		Filter disabledFilter = new Filter();
		disabledFilter.maskBits = 0x0000;
		firstFixture.setFilterData(disabledFilter);
		firstFixture.setDensity(0.5f);
		firstFixture.setRestitution(0.1f);
		m_body.setAngularVelocity((float) ((Math.random() * 20.0) - 10.0));
		m_body.setLinearVelocity(new Vector2((tossLeft ? -1.0f : 1.0f) * 85.0f, 60.0f));
		m_body.setActive(true);

		return true;
	}

	public boolean shouldRandomizeInitialDirection() {
		return true;
	}

	public boolean isAlive() {
		return m_alive;
	}

	public boolean isActive() {
		return true;
	}

	public boolean isDestroyed() {
		return m_destroyed;
	}

	public void destroy() {
		if(m_alive) {
			kill();
		}

		m_destroyed = true;
	}

	public abstract void attack();

	public void kill() {
		m_alive = false;
	}

	public void cleanup(World world) {
		world.destroyBody(m_body);
	}

	public void update() {
		if(!m_alive) {
			if(m_wasAlive) {
				Fixture firstFixture = m_body.getFixtureList().first();
				Filter disabledFilter = new Filter();
				disabledFilter.maskBits = 0x0000;
				firstFixture.setFilterData(disabledFilter);
				firstFixture.setDensity(0.5f);
				firstFixture.setRestitution(0.1f);
				m_body.setType(BodyType.DynamicBody);
				m_body.setAngularVelocity((float) ((Math.random() * 20.0) - 10.0));
				m_body.setLinearVelocity(new Vector2((((int) (Math.random() * 2.0) == 0) ? -1.0f : 1.0f) * 65.0f, (float) (Math.random() * 15.0 + 25.0)));
				m_body.setActive(true);
			}
		}

		if(m_body.getPosition().y + getSize().y < 0.0f) {
			destroy();
		}

		m_wasAlive = m_alive;
	}

	public abstract void render(SpriteBatch spriteBatch);

}
