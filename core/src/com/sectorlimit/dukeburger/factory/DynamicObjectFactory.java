package com.sectorlimit.dukeburger.factory;

import java.util.Vector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.sectorlimit.dukeburger.object.BoxDebris;

public class DynamicObjectFactory {

	private World m_world;

	private Texture[] m_boxDebrisTextures;

	public DynamicObjectFactory(World world) {
		m_world = world;

		m_boxDebrisTextures = new Texture[] {
			new Texture(Gdx.files.internal("sprites/box_debris_1.png")),
			new Texture(Gdx.files.internal("sprites/box_debris_2.png"))
		};
	}

	public Vector<BoxDebris> createBoxDebris(Vector2 position) {
		Vector<BoxDebris> boxDebrisCollection = new Vector<BoxDebris>();

		for(int i = 0; i < 2; i++) {
			BoxDebris boxDebris = new BoxDebris(m_boxDebrisTextures[i], i == 0 ? BoxDebris.Type.Left : BoxDebris.Type.Right);
			boxDebris.assignPhysics(m_world, position);
			boxDebrisCollection.add(boxDebris);
		}
		
		return boxDebrisCollection;
	}

}
