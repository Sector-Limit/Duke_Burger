package com.sectorlimit.dukeburger.powerup;

import java.util.Vector;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Cola extends Powerup {

	private Vector<Sound> m_consumeSounds;

	private static final Vector2 COLA_SIZE = new Vector2(16, 16);

	public Cola(Vector2 position, Animation<TextureRegion> animation, Vector<Sound> consumeSounds) {
		super(position, animation);

		m_consumeSounds = consumeSounds;
	}

	public Vector2 getSize() {
		return COLA_SIZE;
	}

	@Override
	public void consume() {
		super.consume();

		m_consumeSounds.elementAt((int) (Math.random() * 2.0f)).play();
	}

}
