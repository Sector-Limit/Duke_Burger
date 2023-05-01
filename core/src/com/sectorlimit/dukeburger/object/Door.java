package com.sectorlimit.dukeburger.object;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Door extends StaticObject {

	private boolean m_open;

	private TextureRegion m_doorClosedTextureRegion;
	private TextureRegion m_doorOpenTextureRegion;

	private static final Vector2 DOOR_SIZE = new Vector2(16, 32);

	public Door(Vector2 position, TextureRegion doorClosedTextureRegion, TextureRegion doorOpenTextureRegion) {
		super(position, null);

		m_open = false;

		m_doorClosedTextureRegion = doorClosedTextureRegion;
		m_doorOpenTextureRegion = doorOpenTextureRegion;
	}

	public boolean isOpen() {
		return m_open;
	}

	public void setOpen(boolean open) {
		m_open = open;
	}

	public void open() {
		setOpen(true);
	}

	public void close() {
		setOpen(false);
	}

	@Override
	public Vector2 getSize() {
		return DOOR_SIZE;
	}

	public void render(SpriteBatch spriteBatch) {
		TextureRegion doorTextureRegion = m_open ? m_doorOpenTextureRegion : m_doorClosedTextureRegion;

		Vector2 renderOrigin = new Vector2(getOriginPosition()).sub(new Vector2(getSize()).scl(0.5f));
		spriteBatch.draw(doorTextureRegion, renderOrigin.x, renderOrigin.y);
	}
		
}