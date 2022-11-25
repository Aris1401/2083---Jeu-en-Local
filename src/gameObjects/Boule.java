package gameObjects;

import java.awt.Color;
import java.awt.Graphics2D;

import core.Game;
import engineClasses.Vector2;

public class Boule extends GameObject{
	boolean isFotsy = false;
	Vector2 velocity = new Vector2();
	
	int numero = 0;
	public int getNumero() {
		return this.numero;
	}
	
	public void setNumero(int value) {
		this.numero = value;
	}
	
	public Boule(float width, float height, Game core) {
		this.transform.width = width;
		this.transform.height = height;
		
		core.addNewGameObject(this);
		
		this.material = Color.RED;
	}
	
	public boolean getIsFotsy() {
		return this.isFotsy;
	}
	
	public void setIsFotsy(boolean fotsy) {
		this.isFotsy = fotsy;
	}
	
	public void setVelocity(Vector2 vel) {
		this.velocity = vel;
	}
	
	public Vector2 getVelocity() {
		return this.velocity;
	}
	
	public boolean getIsMoving() {
		if (velocity.length() > 0.05) {
			return true;
		}
		
		return false;
	}
	
	public void moveBall() {
		this.translate(new Vector2(this.getVelocity().x, this.getVelocity().y));
		
		this.setVelocity(new Vector2((float) (this.getVelocity().x * 0.974f), (float) (this.getVelocity().y * 0.974f)));
	}
	
	public void collideToSisiny(Game core) {
		if (!this.getIsMoving()) return;
		
		boolean collided = false;
		
		if (this.position.y >= core.getViewportRect().y - (this.scale().y)) {
			this.position.y = core.getViewportRect().y - (this.scale().y);
			this.setVelocity(new Vector2(this.getVelocity().x, -this.getVelocity().y));
			collided = true;
		} if (this.position().y - (this.scale().y / 4) <= 0) {
			this.position.y = this.scale().y / 4;
			this.setVelocity(new Vector2(this.getVelocity().x, -this.getVelocity().y));
			collided = true;
		} if (this.position.x >= core.getViewportRect().x - (this.scale().x)) {
			this.position.x = core.getViewportRect().x - (this.scale().x);
			this.setVelocity(new Vector2(-this.getVelocity().x, this.getVelocity().y));
			collided = true;
		} if (this.position().x - (this.scale().x / 2) <= 0) {
			this.position.x = (this.scale().x / 2);
			this.setVelocity(new Vector2(-this.getVelocity().x, this.getVelocity().y));
			collided = true;
		}
		
		if (collided) {
			this.velocity = velocity.multiply(0.974f);
		}
	}
	
	@Override
	public void drawObject(Graphics2D g) {
		g.setColor(new Color(0f, 0f, 0f, .4f));
		g.fillOval((int) this.position().x, (int) this.position().y + 2, this.scale().x, this.scale().y);
		
		super.drawObject(g);
	}
	
	// COllisions
	public void collidedWith(Boule boule) {
		Vector2 normal = this.position().sub(boule.position());
		
		float distance = normal.length();
		
		if (distance > this.scale().x) {
			return;
		}
		
		Vector2 min = normal.multiply((this.scale().x - distance) / distance);
		
		this.position = this.position.add(min.multiply(1/2));
		boule.position = boule.position.sub(min.multiply(1/2));
		
		Vector2 un = normal.multiply(1/normal.length());
		Vector2 ut = new Vector2(-un.y, un.x);
		
		float v1n = un.dot(this.getVelocity());
		float v1t = ut.dot(this.getVelocity());
		
		float v2n = un.dot(boule.getVelocity());
		float v2t = ut.dot(boule.getVelocity());
		
		float vp1n = v2n;
		float vp2n = v1n;
		
		Vector2 vp1nVec = un.multiply(vp1n);
		Vector2 vp1tVec = ut.multiply(v1t);
		
		Vector2 vp2nVec = un.multiply(vp2n);
		Vector2 vp2tVec = ut.multiply(v2t);
		
		setVelocity(vp1nVec.add(vp1tVec));
		boule.setVelocity(vp2nVec.add(vp2tVec));
	}
}
