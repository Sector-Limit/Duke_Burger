package com.sectorlimit.dukeburger.object;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class PigCop extends StaticObject {

	private boolean m_visible;

	public PigCop(int type, Vector2 position, Texture texture) {
		super(position, texture);

		m_visible = type != 1;
	}

	public Vector2 getSize() {
		return new Vector2(m_texture.getWidth(), m_texture.getHeight());
	}

	public boolean isVisible() {
		return m_visible;
	}

	public void setVisible(boolean visible) {
		m_visible = visible;
	}

	public void render(SpriteBatch spriteBatch) {
		if(!m_visible) {
			return;
		}

		super.render(spriteBatch);
	}
}
