package com.sectorlimit.dukeburger.factory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.sectorlimit.dukeburger.projectile.BurgerProjectile;

public class ProjectileFactory {

	private Texture m_cheeseBurgerTexture;
	private Texture m_lettuceBurgerTexture;
	private Texture m_tomatoBurgerTexture;

	public ProjectileFactory() {
		m_cheeseBurgerTexture = new Texture(Gdx.files.internal("sprites/burger_cheese.png"));
		m_lettuceBurgerTexture = new Texture(Gdx.files.internal("sprites/burger_salad.png"));
		m_tomatoBurgerTexture = new Texture(Gdx.files.internal("sprites/burger_salad.png"));
	}

	public BurgerProjectile createBurger(Vector2 position, BurgerProjectile.Type burgerType) {
		Texture burgerTexture = null;

		switch(burgerType) {
			case Cheese: {
				burgerTexture = m_cheeseBurgerTexture;
				break;
			}

			case Lettuce: {
				burgerTexture = m_lettuceBurgerTexture;
				break;
			}

			case Tomato: {
				burgerTexture = m_tomatoBurgerTexture;
				break;
			}
		}

		return new BurgerProjectile(position, burgerType, burgerTexture);
	}

	public void dispose() {
		m_cheeseBurgerTexture.dispose();
		m_lettuceBurgerTexture.dispose();
		m_tomatoBurgerTexture.dispose();
	}

}
