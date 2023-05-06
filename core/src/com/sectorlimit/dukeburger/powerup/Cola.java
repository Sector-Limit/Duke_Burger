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
		this(-1, position, animation, consumeSounds);
	}

	public Cola(int id, Vector2 position, Animation<TextureRegion> animation, Vector<Sound> consumeSounds) {
		super(id, position, animation);

		m_consumeSounds = consumeSounds;
	}

	@Override
	public boolean isOneTimeUse() {
		return false;
	}

	public Vector2 getSize() {
		return COLA_SIZE;
	}

	@Override
	public void consume() {
		super.consume();

		m_consumeSounds.elementAt((int) (Math.random() * 2.0f)).play(0.5f);
	}

}
