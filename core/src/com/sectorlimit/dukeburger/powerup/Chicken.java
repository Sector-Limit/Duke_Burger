package com.sectorlimit.dukeburger.powerup;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Chicken extends Powerup {

	private Sound m_consumeSound;

	private static final Vector2 CHICKEN_SIZE = new Vector2(16, 18);

	public Chicken(Vector2 position, Animation<TextureRegion> animation, Sound consumeSound) {
		this(-1, position, animation, consumeSound);
	}

	public Chicken(int id, Vector2 position, Animation<TextureRegion> animation, Sound consumeSound) {
		super(id, position, animation);

		m_consumeSound = consumeSound;
	}

	public Vector2 getSize() {
		return CHICKEN_SIZE;
	}

	@Override
	public void consume() {
		super.consume();

		m_consumeSound.play(0.8f);
	}

}
