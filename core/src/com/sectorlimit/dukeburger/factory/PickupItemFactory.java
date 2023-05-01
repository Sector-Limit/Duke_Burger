package com.sectorlimit.dukeburger.factory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.sectorlimit.dukeburger.object.Barrel;
import com.sectorlimit.dukeburger.object.Box;
import com.sectorlimit.dukeburger.object.Burger;

public class PickupItemFactory {

	private World m_world;

	private Texture m_bigBurgerTexture;
	private Texture m_woodenBoxTexture;
	private Texture m_barrelTexture;

	private Sound m_destroyBoxSound;

	public PickupItemFactory(World world) {
		m_world = world;

		m_bigBurgerTexture = new Texture(Gdx.files.internal("sprites/burger_pickup.png"));
		m_woodenBoxTexture = new Texture(Gdx.files.internal("sprites/wooden_box.png"));
		m_barrelTexture = new Texture(Gdx.files.internal("sprites/barrel.png"));

		m_destroyBoxSound = Gdx.audio.newSound(Gdx.files.internal("sounds/BoxHit.wav"));
	}

	public Burger createBurger(Vector2 position) {
		Burger burger = new Burger(m_bigBurgerTexture);
		burger.assignPhysics(m_world, position);
		return burger;
	}

	public Box createBox(Vector2 position) {
		Box box = new Box(m_woodenBoxTexture, m_destroyBoxSound);
		box.assignPhysics(m_world, position);
		return box;
	}

	public Barrel createBarrel(Vector2 position) {
		Barrel barrel = new Barrel(m_barrelTexture);
		barrel.assignPhysics(m_world, position);
		return barrel;
	}

	public void dispose() {
		m_bigBurgerTexture.dispose();
		m_woodenBoxTexture.dispose();
		m_barrelTexture.dispose();
	}

}
