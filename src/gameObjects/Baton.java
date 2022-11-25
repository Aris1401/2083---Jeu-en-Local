package gameObjects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import core.Game;
import engineClasses.Vector2;

public class Baton extends GameObject {
	float power = 0;
	final float MAX_POWER = 30;
	Vector2 bouleCurrentPos;
	
	boolean shooted = false;
	
	public boolean getShooted() {
		return this.shooted;
	}
	
	public void setShooted(boolean b) {
		this.shooted = b;
	}
 	
	public enum BatonStates {
		Normal,
		Charge,
		Shoot;
	}
	BatonStates state = BatonStates.Normal;
	
	Boule bouleFotsy;
	
	public Baton(float width, float height, Game core, Boule bouleFotsy) {
		this.transform.width = width;
		this.transform.height = height;
		
		core.addNewGameObject(this);
		
		this.material = Color.RED;
		
		this.bouleFotsy = bouleFotsy;
	}
	
	public float getMaxPower() {
		return this.MAX_POWER;
	}
	
	public BatonStates getState() {
		return state;
	}
	
	public void setState(BatonStates state) {
		this.state = state;
	}
	
	public float getPower() {
		return this.power;
	}
	
	public void setOrigin() {
		this.bouleCurrentPos = new Vector2(this.bouleFotsy.position().x, this.bouleFotsy.position().y);
	}
	
	public Vector2 getOrigin() {
		return this.bouleCurrentPos;
	}
	
	public void updateRotation(Vector2 mousePos) {
		float op = mousePos.y - this.getOrigin().y;
		float adj = mousePos.x - this.getOrigin().x;
		
		this.setRotationDegrees((float) Math.atan2(op, adj));
	}
	
	@Override
	public void drawObject(Graphics2D g) {
		AffineTransform oldTransform = g.getTransform();
		
		g.rotate(rotationDegree, this.bouleCurrentPos.x + (20 / 2), this.bouleCurrentPos.y + (20 / 2));
		g.fillRect((int) this.position().x, (int) this.position.y, (int) this.scale().x, (int) this.scale().y);
		
		g.setTransform(oldTransform);
	}
	
	public void increasePower() {
		if (power >= MAX_POWER) return;
		
		power += .8;
		this.position.x -= 1;
	}
	
	public void resetPower() {
		this.power = 0;
	}
}
