package com.sectorlimit.dukeburger.enemy;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.sectorlimit.dukeburger.CollisionCategories;

public abstract class BasicEnemy extends Enemy {

	private static final float HORIZONTAL_VELOCITY = 45.0f;

	public BasicEnemy() { }

	@Override
	public void assignPhysics(World world, Vector2 position) {
		super.assignPhysics(world, position);

		Vector2 halfSize = new Vector2(getSize()).scl(0.5f);
		float halfSensorHeight = halfSize.y * 0.99f;
		BodyDef leftSensorBodyDefinition = new BodyDef();
		leftSensorBodyDefinition.fixedRotation = true;
		PolygonShape leftPolygonCollisionShape = new PolygonShape();
		leftPolygonCollisionShape.set(new Vector2[] {
			new Vector2(-halfSize.x - 1.0f, -halfSensorHeight),
			new Vector2(-halfSize.x - 1.0f, halfSensorHeight),
			new Vector2(-halfSize.x, halfSensorHeight),
			new Vector2(-halfSize.x, -halfSensorHeight)
		});
		FixtureDef leftFixtureDefinition = new FixtureDef();
		leftFixtureDefinition.shape = leftPolygonCollisionShape;
		leftFixtureDefinition.isSensor = true;
		Fixture leftCollisionFixture = m_body.createFixture(leftFixtureDefinition);
		leftPolygonCollisionShape.dispose();
		Filter leftCollisionFilter = new Filter();
		leftCollisionFilter.categoryBits = CollisionCategories.ENEMY_SIDE_SENSOR;
		leftCollisionFilter.maskBits = CollisionCategories.GROUND | CollisionCategories.ENEMY_BOUNDARY;
		leftCollisionFixture.setFilterData(leftCollisionFilter);
		leftCollisionFixture.setUserData("left");

		BodyDef rightSensorBodyDefinition = new BodyDef();
		rightSensorBodyDefinition.fixedRotation = true;
		PolygonShape rightPolygonCollisionShape = new PolygonShape();
		rightPolygonCollisionShape.set(new Vector2[] {
			new Vector2(halfSize.x + 1.0f, -halfSensorHeight),
			new Vector2(halfSize.x + 1.0f, halfSensorHeight),
			new Vector2(halfSize.x, halfSensorHeight),
			new Vector2(halfSize.x, -halfSensorHeight)
		});
		FixtureDef rightFixtureDefinition = new FixtureDef();
		rightFixtureDefinition.shape = rightPolygonCollisionShape;
		rightFixtureDefinition.isSensor = true;
		Fixture rightCollisionFixture = m_body.createFixture(rightFixtureDefinition);
		rightPolygonCollisionShape.dispose();
		Filter rightCollisionFilter = new Filter();
		rightCollisionFilter.categoryBits = CollisionCategories.ENEMY_SIDE_SENSOR;
		rightCollisionFilter.maskBits = CollisionCategories.GROUND | CollisionCategories.ENEMY_BOUNDARY;
		rightCollisionFixture.setFilterData(rightCollisionFilter);
		rightCollisionFixture.setUserData("right");
	}

	@Override
	public void onCollideWithWall(boolean leftSide) {
		m_facingLeft = !leftSide;
	}

	@Override
	public void update() {
		super.update();

		if(!isActive()) {
			if(!isTossed() && isAlive()) {
				m_body.setLinearVelocity(new Vector2(0.0f, 0.0f));
			}

			return;
		}

		m_body.setLinearVelocity(new Vector2((m_facingLeft ? -1.0f : 1.0f) * HORIZONTAL_VELOCITY, 0.0f));
	}

}
