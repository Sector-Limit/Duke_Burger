package com.sectorlimit.dukeburger;

import java.util.Vector;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.sectorlimit.dukeburger.enemy.Enemy;
import com.sectorlimit.dukeburger.factory.ProjectileFactory;
import com.sectorlimit.dukeburger.projectile.Fireball;
import com.sectorlimit.dukeburger.projectile.Projectile;

public class ProjectileSystem {

	private World m_world;
	private ProjectileFactory m_projectileFactory;

	private Vector<Projectile> m_projectiles;

	public ProjectileSystem(World world) {
		m_world = world;
		m_projectileFactory = new ProjectileFactory(m_world);
		m_projectiles = new Vector<Projectile>();
	}

	public Fireball spawnFireball(Enemy source, Vector2 position, Vector2 direction) {
		Fireball fireball = m_projectileFactory.createFireball(source, position, direction);
		m_projectiles.add(fireball);
		return fireball;
	}

	public void dispose() {
		m_projectileFactory.dispose();
	}

	public void render(SpriteBatch spriteBatch) {
		Vector<Projectile> projectilesToRemove = new Vector<Projectile>();

		for(Projectile projectile : m_projectiles) {
			if(projectile.isDestroyed()) {
				projectilesToRemove.add(projectile);
				continue;
			}

			projectile.render(spriteBatch);
		}

		for(Projectile projectileToRemove : projectilesToRemove) {
			projectileToRemove.cleanup(m_world);
			m_projectiles.remove(projectileToRemove);
		}
	}

}
