package com.sectorlimit.dukeburger.object;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.sectorlimit.dukeburger.CollisionCategories;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public abstract class DynamicObject {

	protected boolean m_destroyed;

	protected Texture m_texture;

	protected Body m_body;

	public DynamicObject(Texture texture) {
		m_texture = texture;
	}

	public Vector2 getOriginPosition() {
		return m_body != null ? m_body.getPosition() : new Vector2(0.0f, 0.0f);
	}

	public Vector2 getCenterPosition() {
		return new Vector2(getOriginPosition()).add(new Vector2(getSize()).scl(0.5f));
	}

	public abstract Vector2 getSize();

	public void destroy() {
		m_destroyed = true;
	}

	public boolean isDestroyed() {
		return m_destroyed;
	}

	public void assignPhysics(World world, Vector2 position) {
		BodyDef bodyDefinition = new BodyDef();
		bodyDefinition.type = BodyType.DynamicBody;
		bodyDefinition.position.set(position);
		m_body = world.createBody(bodyDefinition);
		m_body.setUserData(this);
		PolygonShape polygonCollisionShape = new PolygonShape();
		polygonCollisionShape.setAsBox(getSize().x / 2.0f, getSize().y / 2.0f);
		FixtureDef fixtureDefinition = new FixtureDef();
		fixtureDefinition.shape = polygonCollisionShape;
		fixtureDefinition.density = 0.5f;
		fixtureDefinition.friction = 0.75f;
		fixtureDefinition.restitution = 0.1f;
		Fixture collisionFixture = m_body.createFixture(fixtureDefinition);
		polygonCollisionShape.dispose();
		Filter collisionFilter = new Filter();
		collisionFilter.categoryBits = CollisionCategories.OBJECT;
		collisionFilter.maskBits = CollisionCategories.GROUND;
		collisionFixture.setFilterData(collisionFilter);
	}

	public void cleanup(World world) {
		world.destroyBody(m_body);
	}

	public void update() {
		if(m_body.getPosition().y + getSize().y < 0.0f) {
			destroy();
		}
	}

	public void render(SpriteBatch spriteBatch) {
		update();

		Vector2 renderOrigin = new Vector2(getOriginPosition()).sub(new Vector2(getSize()).scl(0.5f));
		spriteBatch.draw(m_texture, renderOrigin.x, renderOrigin.y, getSize().x / 2, getSize().y / 2, m_texture.getWidth(), m_texture.getHeight(), 1.0f, 1.0f, (float) Math.toDegrees(m_body.getAngle()), 0, 0, m_texture.getWidth(), m_texture.getHeight(), false, false);
	}

}
