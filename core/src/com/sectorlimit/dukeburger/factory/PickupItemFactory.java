package com.sectorlimit.dukeburger.factory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.sectorlimit.dukeburger.object.Burger;

public class PickupItemFactory {

	private Texture m_bigBurgerTexture;
	private Texture m_woodenBoxTexture;

	public PickupItemFactory() {
		m_bigBurgerTexture = new Texture(Gdx.files.internal("sprites/big_burger.png"));
		m_woodenBoxTexture = new Texture(Gdx.files.internal("sprites/wooden_box.png"));
	}

	public Burger createBurger(Vector2 position) {
		return new Burger(position, m_bigBurgerTexture);
	}

	public Burger createBox(Vector2 position) {
		return new Burger(position, m_woodenBoxTexture);
	}

	public void dispose() {
		m_bigBurgerTexture.dispose();
		m_woodenBoxTexture.dispose();
	}

}
