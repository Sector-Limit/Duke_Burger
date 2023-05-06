package com.sectorlimit.dukeburger.enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
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

public class Octa extends Enemy {

	private Vector2 m_startingPosition;
	private float m_verticalRiseOffset;
	private boolean m_rising;

	private TextureRegion m_octaRisingTextureRegion;
	private TextureRegion m_octaFallingTextureRegion;
	private Texture m_octaDeadTexture;

	private static final Vector2 OCTA_SIZE = new Vector2(27, 22);
	private static final float MAX_OCTA_RISE_DISTANCE = 5.0f * 16.0f;
	private static float OCTA_RISE_VELOCITY = 50.0f;
	private static float OCTA_FALL_VELOCITY = -30.0f;

	public Octa(TextureRegion octaRisingTextureRegion, TextureRegion octaFallingTextureRegion, Texture octaDeadTexture) {
		m_verticalRiseOffset = 0.0f;
		m_rising = true;

		m_octaRisingTextureRegion = octaRisingTextureRegion;
		m_octaFallingTextureRegion = octaFallingTextureRegion;
		m_octaDeadTexture = octaDeadTexture;
	}

	@Override
	public Vector2 getSize() {
		return OCTA_SIZE;
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
		sensorCollisionFilter.categoryBits = CollisionCategories.ENEMY_TOP_SENSOR;
		sensorCollisionFilter.maskBits = CollisionCategories.OBJECT | CollisionCategories.DUKE;
		sensorCollisionFixture.setFilterData(sensorCollisionFilter);
		polygonCollisionShape.dispose();

		m_startingPosition = new Vector2(m_body.getPosition());
	}

	@Override
	public void attack() { }

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

			m_verticalRiseOffset += (m_rising ? OCTA_RISE_VELOCITY : OCTA_FALL_VELOCITY) * deltaTime;

			if(m_rising) {
				if(m_verticalRiseOffset >= MAX_OCTA_RISE_DISTANCE) {
					m_verticalRiseOffset = MAX_OCTA_RISE_DISTANCE;
					m_rising = false;
				}
			}
			else {
				if(m_verticalRiseOffset <= 0.0f) {
					m_verticalRiseOffset = 0.0f;
					m_rising = true;
				}
			}

			setPosition(new Vector2(m_startingPosition).add(new Vector2(0.0f, m_verticalRiseOffset)));
		}

		Texture currentTexture = null;
		TextureRegion currentTextureRegion = null;
	
		if(m_alive) {
			if(m_rising) {
				currentTextureRegion = m_octaRisingTextureRegion;
			}
			else {
				currentTextureRegion = m_octaFallingTextureRegion;
			}
		}
		else {
			currentTexture = m_octaDeadTexture;
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
