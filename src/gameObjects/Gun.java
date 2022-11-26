package gameObjects;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import audio.SoundClip;
import core.Game;
import engineClasses.Vector2;

public class Gun extends GameObject{
	Vector2 rotationOrigin = new Vector2();
	
	boolean isFlipped = false;
	
/// Sounds
	SoundClip emptySound = new SoundClip("/Empty.wav");
	SoundClip reloadSound = new SoundClip("/Reload.wav");
	
	boolean isReloading = false;
	float currentRotationDegree = 0f;
	
	
	float fireRate = 8f;
	boolean canFire = true;
	
	float magazine = 35f;
	float bulletFired = 0f;
	
	public Gun(Game core) {
		this.transform.height = 3;
		
		core.addNewGameObject(this);
	}
	
	public void setRotationOrigin(float x, float y) {
		rotationOrigin = new Vector2(x, y);
	}
	
	public void flipImage(boolean flipped) {
		isFlipped = flipped;
	}
	
	public boolean isGunFlipped() {
		return isFlipped;
	}
	
	public void fire() {
		bulletFired++;
		
		System.out.println(bulletFired);
		
		SoundClip clip = new SoundClip("/Rifle Sound.wav");
		clip.play();
	}
	
	@Override
	public void drawObject(Graphics2D g) {
		AffineTransform oldTrans = g.getTransform();
		
		g.rotate(rotationDegree, position.x + (64 / 2), position.y + (64 / 2));
//			g.rotate(rotationDegree, rotationOrigin.x + (scale().x / 2), rotationOrigin.y + (scale().y));		
		if (isReloading) {
			rotationDegree += 0.2f;
			currentRotationDegree += 0.2f;
			
			if (Math.toDegrees(currentRotationDegree) >= 360) {
				reloaded();
			}
		}
		
		if (!isFlipped) 
			g.drawImage(sprite, (int) this.position().x, (int) this.position().y, 64, 64, null);
		else
			g.drawImage(sprite, (int) this.position().x, (int) this.position().y + 64, 64, -64, null);	
		
		g.setTransform(oldTrans);
	}
	
	public void playEmptySound() {
		if (!emptySound.isRunning()) {
			emptySound.play();			
		}
	}
	
	void reloaded() {
		bulletFired = 0f;
		
		isReloading = false;
		currentRotationDegree = 0f;
	}
	
	public void reloadGun() {
		isReloading = true;
		
		if (!reloadSound.isRunning()) {
			reloadSound.play();
		}
	}
	
	public boolean getIsReloading() {
		return isReloading;
	}
	
	public float getFireRate() {
		return this.fireRate;
	}
	
	public void setFireRate(float fireRate) {
		this.fireRate = fireRate;
	}
	
	public boolean getCanFire() {
		return this.canFire;
	}
	
	public void setCanFire(boolean canFire) {
		this.canFire = canFire;
	}

	public float getMagazine() {
		return magazine;
	}

	public void setMagazine(float magazine) {
		this.magazine = magazine;
	}

	public float getBulletFired() {
		return bulletFired;
	}

	public void setBulletFired(float bulletFired) {
		this.bulletFired = bulletFired;
	}
}
