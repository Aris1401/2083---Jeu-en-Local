package gameObjects;

import java.awt.Graphics2D;

import core.Game;
import engineClasses.Vector2;

public class Bullet extends GameObject{
	Vector2 velocity = new Vector2();
	float bulletSpeed = 10;
	
	public Bullet(float width, float height, Game core) {
		this.transform.height = 3;
		
		core.addNewGameObject(this);
		
		this.transform.width =  width;
		this.transform.height = height;
	}
	
	public void setVelocity(Vector2 vel) {
		this.velocity = vel;
	} 
	
	@Override
	public void drawObject(Graphics2D g) {
		g.fillRect((int) position().x, (int) position().y, (int) scale().x, (int) scale().y);
	}
	
	public void update() {
		translate(velocity.multiply(bulletSpeed));
	}
}
