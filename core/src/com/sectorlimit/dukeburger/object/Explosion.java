package com.sectorlimit.dukeburger.object;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.sectorlimit.dukeburger.CollisionCategories;

public class Explosion {

	private Body m_body;

	private Vector2 m_position;
	private Animation<TextureRegion> m_explosionAnimation;
	private float m_animationTimeElapsed;

	private Vector2 EXPLOSION_SIZE = new Vector2(50.0f, 50.0f);
	private float EXPLOSION_IMPACT_RADIUS = EXPLOSION_SIZE.x * 0.4f;

	public Explosion(Vector2 position, Animation<TextureRegion> explosionAnimation) {
		m_position = position;
		m_explosionAnimation = explosionAnimation;
		m_animationTimeElapsed = 0.0f;
	}

	public Vector2 getOriginPosition() {
		return m_position;
	}

	public Vector2 getSize() {
		return EXPLOSION_SIZE;
	}

	public float getImpactRadius() {
		return EXPLOSION_IMPACT_RADIUS;
	}

	public boolean isExpired() {
		return m_animationTimeElapsed > m_explosionAnimation.getAnimationDuration();
	}

	public void assignPhysics(World world, Vector2 position) {
		BodyDef bodyDefinition = new BodyDef();
		bodyDefinition.type = BodyType.StaticBody;
		bodyDefinition.position.set(position);
		m_body = world.createBody(bodyDefinition);
		m_body.setUserData(this);
		CircleShape circleCollisionShape = new CircleShape();
		circleCollisionShape.setRadius(EXPLOSION_IMPACT_RADIUS);
		FixtureDef sensorFixtureDefinition = new FixtureDef();
		sensorFixtureDefinition.shape = circleCollisionShape;
		sensorFixtureDefinition.isSensor = true;
		Fixture sensorCollisionFixture = m_body.createFixture(sensorFixtureDefinition);
		Filter sensorCollisionFilter = new Filter();
		sensorCollisionFilter.categoryBits = CollisionCategories.EXPLOSION;
		sensorCollisionFilter.maskBits = CollisionCategories.ENEMY_SENSOR;
		sensorCollisionFixture.setFilterData(sensorCollisionFilter);
		circleCollisionShape.dispose();
	}

	public void cleanup(World world) {
		world.destroyBody(m_body);
	}

	public void render(SpriteBatch spriteBatch) {
		if(!isExpired()) {
			Vector2 renderOrigin = new Vector2(getOriginPosition()).sub(new Vector2(getSize()).scl(0.5f));
			spriteBatch.draw(m_explosionAnimation.getKeyFrame(m_animationTimeElapsed, true), renderOrigin.x, renderOrigin.y);

			m_animationTimeElapsed += Gdx.graphics.getDeltaTime();
		}
	}

}
