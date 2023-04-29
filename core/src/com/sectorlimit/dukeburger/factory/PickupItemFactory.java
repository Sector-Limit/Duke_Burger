package com.sectorlimit.dukeburger.factory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.sectorlimit.dukeburger.object.Box;
import com.sectorlimit.dukeburger.object.Burger;
import com.sectorlimit.dukeburger.object.PickupItem;

public class PickupItemFactory {

	private World m_world;

	private Texture m_bigBurgerTexture;
	private Texture m_woodenBoxTexture;

	public PickupItemFactory(World world) {
		m_world = world;

		m_bigBurgerTexture = new Texture(Gdx.files.internal("sprites/big_burger.png"));
		m_woodenBoxTexture = new Texture(Gdx.files.internal("sprites/wooden_box.png"));
	}

	public Burger createBurger(Vector2 position) {
		Burger burger = new Burger(position, m_bigBurgerTexture);
		assignPhysicsToPickupItem(burger, position);
		return burger;
	}

	public Box createBox(Vector2 position) {
		Box box = new Box(position, m_woodenBoxTexture);
		assignPhysicsToPickupItem(box, position);
		return box;
	}

	private void assignPhysicsToPickupItem(PickupItem pickupItem, Vector2 position) {
		BodyDef bodyDefinition = new BodyDef();
		bodyDefinition.type = BodyType.DynamicBody;
		bodyDefinition.position.set(position);
		Body body = m_world.createBody(bodyDefinition);
		PolygonShape polygonCollisionShape = new PolygonShape();
		polygonCollisionShape.setAsBox(pickupItem.getSize().x / 2.0f, pickupItem.getSize().y / 2.0f);
		FixtureDef fixtureDefinition = new FixtureDef();
		fixtureDefinition.shape = polygonCollisionShape;
		fixtureDefinition.density = 0.5f;
		fixtureDefinition.friction = 0.4f;
		fixtureDefinition.restitution = 0.6f;
		Fixture fixture = body.createFixture(fixtureDefinition);
		polygonCollisionShape.dispose();

		pickupItem.setPhysicsProperties(body, fixture);
	}

	public void dispose() {
		m_bigBurgerTexture.dispose();
		m_woodenBoxTexture.dispose();
	}

}
