package com.sectorlimit.dukeburger.object;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.sectorlimit.dukeburger.CollisionCategories;

public class Door extends StaticObject {

	private boolean m_open;

	protected Body m_body;

	private TextureRegion m_doorClosedTextureRegion;
	private TextureRegion m_doorOpenTextureRegion;

	private Sound m_doorSound;

	private static final Vector2 DOOR_SIZE = new Vector2(16, 32);
	private static final float DOOR_SOUND_VOLUME = 0.5f;

	public Door(Vector2 position, TextureRegion doorClosedTextureRegion, TextureRegion doorOpenTextureRegion, Sound doorSound) {
		super(position);

		m_open = false;

		m_doorClosedTextureRegion = doorClosedTextureRegion;
		m_doorOpenTextureRegion = doorOpenTextureRegion;

		m_doorSound = doorSound;
	}

	public boolean isOpen() {
		return m_open;
	}

	public boolean isClosed() {
		return !m_open;
	}

	public void setOpen(boolean open) {
		if(m_open == open) {
			return;
		}

		m_open = open;

		Fixture firstFixture = m_body.getFixtureList().first();

		if(m_open) {
			firstFixture.setSensor(true);

			m_doorSound.play(DOOR_SOUND_VOLUME);
		}
		else {
			firstFixture.setSensor(false);

			m_doorSound.play(DOOR_SOUND_VOLUME, 0.75f, 0.0f);
		}
	}

	public void open() {
		setOpen(true);
	}

	public void close() {
		setOpen(false);
	}

	@Override
	public Vector2 getSize() {
		return DOOR_SIZE;
	}

	public void assignPhysics(World world, Vector2 position) {
		BodyDef bodyDefinition = new BodyDef();
		bodyDefinition.type = BodyType.StaticBody;
		bodyDefinition.position.set(position);
		m_body = world.createBody(bodyDefinition);
		m_body.setUserData(this);
		PolygonShape polygonCollisionShape = new PolygonShape();
		polygonCollisionShape.setAsBox(getSize().x / 2.0f, getSize().y / 2.0f);
		FixtureDef fixtureDefinition = new FixtureDef();
		fixtureDefinition.shape = polygonCollisionShape;
		Fixture collisionFixture = m_body.createFixture(fixtureDefinition);
		Filter collisionFilter = new Filter();
		collisionFilter.categoryBits = CollisionCategories.OBJECT | CollisionCategories.DOOR;
		collisionFilter.maskBits = CollisionCategories.OBJECT | CollisionCategories.DUKE | CollisionCategories.BURGER | CollisionCategories.ENEMY | CollisionCategories.ENEMY_SENSOR;
		collisionFixture.setFilterData(collisionFilter);
		polygonCollisionShape.dispose();
	}

	public void render(SpriteBatch spriteBatch) {
		TextureRegion doorTextureRegion = m_open ? m_doorOpenTextureRegion : m_doorClosedTextureRegion;

		Vector2 renderOrigin = new Vector2(getOriginPosition()).sub(new Vector2(getSize()).scl(0.5f));
		spriteBatch.draw(doorTextureRegion, renderOrigin.x, renderOrigin.y);
	}

}