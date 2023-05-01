package com.sectorlimit.dukeburger.powerup;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Cola extends Powerup {

	private Sound m_consumeSound;

	private static final Vector2 COLA_SIZE = new Vector2(16, 16);

	public Cola(Vector2 position, Animation<TextureRegion> animation, Sound consumeSound) {
		super(position, animation);

		m_consumeSound = consumeSound;
	}

	public Vector2 getSize() {
		return COLA_SIZE;
	}

	@Override
	public void consume() {
		super.consume();

		m_consumeSound.play();
	}

}
