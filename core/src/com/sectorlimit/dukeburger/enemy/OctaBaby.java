package com.sectorlimit.dukeburger.enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.sectorlimit.dukeburger.CollisionCategories;

public class OctaBaby extends BasicEnemy {

	private Type m_type;
	private boolean m_squished;
	private float m_squishedTimeElapsed;

	private Texture m_octaBabySquishedTexture;
	private Animation<TextureRegion> m_octaBabyWalkAnimation;
	private float m_elapsedAnimationTime;

	private static final Vector2 OCTA_BABY_SIZE = new Vector2(16, 16);
	private static final float MAX_SQUISHED_DURATION = 3.0f;

	public enum Type {
		Green,
		Blue
	}

	public OctaBaby(Type type, Animation<TextureRegion> octaBabyWalkAnimation, Texture octaBabySquishedTexture) {
		m_type = type;
		m_squished = false;
		m_squishedTimeElapsed = 0.0f;

		m_octaBabySquishedTexture = octaBabySquishedTexture;
		m_octaBabyWalkAnimation = octaBabyWalkAnimation;
	}

	public Type getType() {
		return m_type;
	}

	public Vector2 getSize() {
		return OCTA_BABY_SIZE;
	}

	public void assignPhysics(World world, Vector2 position) {
		super.assignPhysics(world, position);

		Vector2 halfSize = new Vector2(getSize()).scl(0.5f);
		float halfSensorWidth = halfSize.x * 0.8f;
		PolygonShape polygonCollisionShape = new PolygonShape();
		polygonCollisionShape.set(new Vector2[] {
			new Vector2(-halfSensorWidth, halfSize.y + 1.0f),
			new Vector2(halfSensorWidth, halfSize.y + 1.0f),
			new Vector2(halfSensorWidth, halfSize.y),
			new Vector2(-halfSensorWidth, halfSize.y)
		});
		FixtureDef fixtureDefinition = new FixtureDef();
		fixtureDefinition.shape = polygonCollisionShape;
		fixtureDefinition.isSensor = true;
		Fixture collisionFixture = m_body.createFixture(fixtureDefinition);
		polygonCollisionShape.dispose();
		Filter collisionFilter = new Filter();
		collisionFilter.categoryBits = CollisionCategories.ENEMY;
		collisionFilter.maskBits = CollisionCategories.DUKE | CollisionCategories.OBJECT;
		collisionFixture.setFilterData(collisionFilter);
	}

	@Override
	public boolean isActive() {
		return !isPickedUp() && !isTossed() && !isSquished();
	}

	@Override
	public boolean isPickupable() {
		return !isPickedUp() && !isTossed() && isSquished();
	}

	public boolean isSquished() {
		return m_squished;
	}

	public boolean squish() {
		if(m_squished) {
			return false;
		}

		m_squished = true;

		return true;
	}

	public boolean unsquish() {
		if(!m_squished || isPickedUp() || isTossed()) {
			return false;
		}

		m_squished = false;
		m_squishedTimeElapsed = 0.0f;
		m_body.setActive(true);

		return true;
	}

	@Override
	public void attack() { }

	@Override
	public void kill() {
		squish();

		super.kill();
	}

	public void render(SpriteBatch spriteBatch) {
		update();

		float deltaTime = Gdx.graphics.getDeltaTime();

		if(isAlive()) {
			if(!isTossed()) {
				if(isSquished()) {
					if(m_body.isActive()) {
						m_body.setActive(false);
					}
		
					m_squishedTimeElapsed += deltaTime;
		
					if(m_squishedTimeElapsed >= MAX_SQUISHED_DURATION) {
						unsquish();
					}
				}
				else {
					if(!m_body.isActive()) {
						m_body.setActive(true);
					}
				}
			}
		}

		Texture currentTexture = null;
		TextureRegion currentTextureRegion = null;
	
		if(m_squished) {
			currentTexture = m_octaBabySquishedTexture;
		}
		else {
			m_elapsedAnimationTime += deltaTime;
			
			if(m_elapsedAnimationTime >= m_octaBabyWalkAnimation.getAnimationDuration()) {
				m_elapsedAnimationTime = m_elapsedAnimationTime % m_octaBabyWalkAnimation.getAnimationDuration();
			}

			currentTextureRegion = m_octaBabyWalkAnimation.getKeyFrame(m_elapsedAnimationTime, true);
		}

		Vector2 renderOrigin = new Vector2(getOriginPosition()).sub(new Vector2(getSize()).scl(0.5f));

		if(m_pickedUp) {
			renderOrigin.sub(new Vector2(-1.0f, getSize().y * 0.5f));
		}

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
