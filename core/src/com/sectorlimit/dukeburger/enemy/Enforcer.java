package com.sectorlimit.dukeburger.enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.sectorlimit.dukeburger.CollisionCategories;
import com.sectorlimit.dukeburger.ProjectileSystem;

public class Enforcer extends Enemy {

	private ProjectileSystem m_projectileSystem;
	private float m_fireballCooldownTimeElapsed;

	private float m_elapsedAnimationTime;
	private Animation<TextureRegion> m_enforcerAnimation;
	private Texture m_enforcerDeadTexture;

	private static final float FIREBALL_COOLDOWN = 2.0f;
	private static final Vector2 ENFORCER_SIZE = new Vector2(22, 22);

	public Enforcer(ProjectileSystem projectileSystem, Animation<TextureRegion> enforcerAnimation, Texture enforcerDeadTexture) {
		m_fireballCooldownTimeElapsed = 0.0f;
		m_projectileSystem = projectileSystem;

		m_elapsedAnimationTime = 0.0f;
		m_enforcerAnimation = enforcerAnimation;
		m_enforcerDeadTexture = enforcerDeadTexture;
	}

	public Vector2 getSize() {
		return ENFORCER_SIZE;
	}

    @Override
    public boolean shouldRandomizeInitialDirection() {
        return false;
    }

	public void assignPhysics(World world, Vector2 position) {
		BodyDef bodyDefinition = new BodyDef();
		bodyDefinition.type = BodyType.StaticBody;
		bodyDefinition.position.set(position);
		m_body = world.createBody(bodyDefinition);
		m_body.setUserData(this);
		PolygonShape polygonCollisionShape = new PolygonShape();
		polygonCollisionShape.setAsBox(getSize().x / 2.0f, getSize().y / 2.0f);
		FixtureDef sensorFixtureDefinition = new FixtureDef();
		sensorFixtureDefinition.shape = polygonCollisionShape;
		sensorFixtureDefinition.isSensor = true;
		Fixture sensorCollisionFixture = m_body.createFixture(sensorFixtureDefinition);
		Filter sensorCollisionFilter = new Filter();
		sensorCollisionFilter.categoryBits = CollisionCategories.ENEMY_SENSOR;
		sensorCollisionFilter.maskBits = CollisionCategories.OBJECT | CollisionCategories.DUKE;
		sensorCollisionFixture.setFilterData(sensorCollisionFilter);
		polygonCollisionShape.dispose();
	}

	@Override
	public void attack() { }

	public void shootFireball() {
		m_projectileSystem.spawnFireball(this, new Vector2(getOriginPosition()).add(new Vector2(0.0f, getSize().y * 0.5f)), new Vector2(m_facingLeft ? -1.0f : 1.0f, 0.1f));
	}

	@Override
	public void kill() {
		super.kill();
	}

	public void render(SpriteBatch spriteBatch) {
		update();

		float deltaTime = Gdx.graphics.getDeltaTime();

		if(isAlive()) {
			if(!m_body.isActive()) {
				m_body.setActive(true);
			}

			m_fireballCooldownTimeElapsed += deltaTime;

			if(m_fireballCooldownTimeElapsed >= FIREBALL_COOLDOWN) {
				m_fireballCooldownTimeElapsed = 0.0f;

				shootFireball();
			}
		}

		Texture currentTexture = null;
		TextureRegion currentTextureRegion = null;
	
		if(m_alive) {
			m_elapsedAnimationTime += deltaTime;
			
			if(m_elapsedAnimationTime >= m_enforcerAnimation.getAnimationDuration()) {
				m_elapsedAnimationTime = m_elapsedAnimationTime % m_enforcerAnimation.getAnimationDuration();
			}

			currentTextureRegion = m_enforcerAnimation.getKeyFrame(m_elapsedAnimationTime, true);
		}
		else {
			currentTexture = m_enforcerDeadTexture;
		}

		Vector2 renderOrigin = new Vector2(getOriginPosition()).sub(new Vector2(getSize()).scl(0.5f));

		if(currentTexture != null) {
			spriteBatch.draw(currentTexture, renderOrigin.x, renderOrigin.y, getSize().x / 2, getSize().y / 2, currentTexture.getWidth(), currentTexture.getHeight(), 1.0f, 1.0f, (float) Math.toDegrees(m_body.getAngle()), 0, 0, currentTexture.getWidth(), currentTexture.getHeight(), !m_facingLeft, m_pickedUp);
		}
		else if(currentTextureRegion != null) {
			if(!m_facingLeft) {
				currentTextureRegion.flip(true, false);
			}
	
			spriteBatch.draw(currentTextureRegion, renderOrigin.x, renderOrigin.y);
	
			if(!m_facingLeft) {
				currentTextureRegion.flip(true, false);
			}
		}
	}

}
