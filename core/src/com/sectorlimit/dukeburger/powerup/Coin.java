package com.sectorlimit.dukeburger.powerup;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Coin extends Powerup {

	private Sound m_consumeSound;

	private static final Vector2 COIN_SIZE = new Vector2(16, 16);

	public Coin(Vector2 position, Animation<TextureRegion> animation, Sound consumeSound) {
		this(-1, position, animation, consumeSound);
	}

	public Coin(int id, Vector2 position, Animation<TextureRegion> animation, Sound consumeSound) {
		super(id, position, animation);

		m_consumeSound = consumeSound;
	}

	public Vector2 getSize() {
		return COIN_SIZE;
	}

	@Override
	public void consume() {
		super.consume();

		m_consumeSound.play(0.4f);
	}

}
