package com.sectorlimit.dukeburger.enemy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public abstract class Enemy {

	protected boolean m_facingLeft;
	protected boolean m_alive;

	protected Body m_body;

	public Enemy() {
		if(shouldRandomizeInitialDirection()) {
			m_facingLeft = (byte) (Math.random() * 2.0) == 0;
		}
		else {
			m_facingLeft = false;
		}

		m_alive = true;
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
		m_body.createFixture(fixtureDefinition);
		polygonCollisionShape.dispose();
	}

	public boolean isFacingLeft() {
		return m_facingLeft;
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

	public abstract void attack();

	public void kill() {
		m_alive = false;
	}

	public abstract void render(SpriteBatch spriteBatch);

}
