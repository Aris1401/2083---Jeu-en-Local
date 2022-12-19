package gameObjects;

import java.awt.Graphics2D;

import core.Game;
import engineClasses.Vector2;

public class Bullet extends GameObject{
	Vector2 velocity = new Vector2();
	float bulletSpeed = 15;
	
	float lifeSpan = 1f;
	float currentLife = 0f;
	
	float bulletDamage = 0f;
	
	Joueur owner;
	
	public Bullet(String id, float width, float height, Game core) {
		this.transform.height = 3;
		
		setId(id);
		
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
		Vector2 cameraOffset = new Vector2();
		if (camera != null) {
			offset = camera.getPosition().clone();
			cameraOffset = camera.getOffset().clone();
		}
		
		g.fillRect((int) (position().x - offset.x + cameraOffset.x), (int) (position().y - offset.y + cameraOffset.y), (int) scale().x, (int) scale().y);
	}
	
	public void update(float delta) {
		translate(velocity.multiply(bulletSpeed));
	}
	
	public float dealDamage() {
		return this.bulletDamage;
	}
	
	public void setOwner(Joueur owner) {
		this.owner = owner;
	}
	
	public Joueur getOwner() {
		return this.owner;
	}
}
