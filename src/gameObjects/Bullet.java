package gameObjects;

import java.awt.Graphics2D;

import core.Game;
import engineClasses.Vector2;

public class Bullet extends GameObject{
	Vector2 velocity = new Vector2();
	float bulletSpeed = 20;
	
	float lifeSpan = 1f;
	float currentLife = 0f;
	
	float bulletDamage = 0f;
	
	public Bullet(float width, float height, Game core) {
		this.transform.height = 3;
		
		core.addNewGameObject(this);
		
		this.transform.width =  width;
		this.transform.height = height;
	}
	
	public void setVelocity(Vector2 vel) {
		this.velocity = vel;
	} 
	
	public void setBulletDamage(float damage) {
		this.bulletDamage = damage;
	}
	
	@Override
	public void drawObject(Graphics2D g, Camera camera) {
		Vector2 offset = new Vector2();
		
		if (camera != null) {
			offset = camera.getPosition().clone();
		}
		
		g.fillRect((int) (position().x - offset.x), (int) (position().y - offset.y), (int) scale().x, (int) scale().y);
	}
	
	public void update(float delta) {
		translate(velocity.multiply(bulletSpeed));
	}
	
	public float dealDamage() {
		return this.bulletDamage;
	}
}
