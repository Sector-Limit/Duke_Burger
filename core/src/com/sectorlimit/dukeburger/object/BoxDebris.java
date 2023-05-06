package com.sectorlimit.dukeburger.object;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.sectorlimit.dukeburger.CollisionCategories;

public class BoxDebris extends Debris {

	private Type m_type;

	private static final Vector2 BOX_DEBRIS_SIZE = new Vector2(17, 17);

	public enum Type {
		Left,
		Right
	}

	public BoxDebris(Texture texture, Type type) {
		super(texture);

		m_type = type;
	}

	@Override
	public Vector2 getSize() {
		return BOX_DEBRIS_SIZE;
	}

	public Type getType() {
		return m_type;
	}

	@Override
	public void assignPhysics(World world, Vector2 position) {
		Vector2 halfSize = new Vector2(getSize()).scl(0.5f);
		BodyDef bodyDefinition = new BodyDef();
		bodyDefinition.type = BodyType.DynamicBody;
		bodyDefinition.position.set(position);
		m_body = world.createBody(bodyDefinition);
		m_body.setUserData(this);
		PolygonShape polygonCollisionShape = new PolygonShape();

		if(m_type == Type.Left) {
			polygonCollisionShape.set(new Vector2[] {
				new Vector2(-halfSize.x * 0.9f, halfSize.y * 0.95f),
				new Vector2(halfSize.x * 0.675f, halfSize.y),
				new Vector2(halfSize.x * 0.875f, -halfSize.y * 0.9f),
				new Vector2(-halfSize.x * 0.55f, -halfSize.y)
			});
		}
		else {
			polygonCollisionShape.set(new Vector2[] {
				new Vector2(-halfSize.x * 0.625f, halfSize.y),
				new Vector2(halfSize.x * 0.625f, halfSize.y * 0.9f),
				new Vector2(halfSize.x * 0.4f, -halfSize.y),
				new Vector2(-halfSize.x * 0.9f, -halfSize.y * 0.85f)
			});
		}

		FixtureDef fixtureDefinition = new FixtureDef();
		fixtureDefinition.shape = polygonCollisionShape;
		fixtureDefinition.density = 0.1f;
		fixtureDefinition.friction = 0.85f;
		fixtureDefinition.restitution = 0.3f;
		Fixture collisionFixture = m_body.createFixture(fixtureDefinition);
		polygonCollisionShape.dispose();
		Filter collisionFilter = new Filter();
		collisionFilter.categoryBits = CollisionCategories.OBJECT;
		collisionFilter.maskBits = CollisionCategories.GROUND;
		collisionFixture.setFilterData(collisionFilter);

		m_body.setTransform(new Vector2(m_body.getPosition()).add(new Vector2((m_type == Type.Left ? -1.0f: 1.0f) * BOX_DEBRIS_SIZE.x * 0.475f, 0.0f)), 0.0f);
		m_body.setLinearVelocity(new Vector2((m_type == Type.Left ? 1.0f: -1.0f) * (float) ((Math.random() * 10.0) + 3.0), (float) ((Math.random() * 4.0) + 2.0)));
		m_body.setAngularVelocity((m_type == Type.Left ? 1.0f : -1.0f) * (float) ((Math.random() * 7.0) + 3.0));
	}

}
