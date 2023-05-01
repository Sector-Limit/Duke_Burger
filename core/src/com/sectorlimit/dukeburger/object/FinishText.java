package com.sectorlimit.dukeburger.object;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class FinishText extends StaticObject {

	public FinishText(Vector2 position, Texture texture) {
		super(position, texture);
	}

	public Vector2 getSize() {
		return new Vector2(m_texture.getWidth(), m_texture.getHeight());
	}

}
