package gameObjects.guns;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

import audio.SoundClip;
import core.Game;
import engineClasses.Vector2;
import gameObjects.Camera;
import gameObjects.GameObject;
import gameObjects.Joueur;
import gameObjects.guns.GunTypes.gunTypes;
import net.packets.Packet04_BulletSpawn;

public class Gun extends GameObject{
	Joueur holder;
	Vector2 rotationOrigin = new Vector2();
	boolean isFlipped = false;
	
/// Sounds
	SoundClip emptySound = new SoundClip("/Empty.wav");
	SoundClip reloadSound = new SoundClip("/Reload.wav");
	
	private boolean isReloading = false;
	float currentRotationDegree = 0f;
	
	float fireRate = 8f;
	boolean canFire = true;
	
	float gunRecoil = 1f;
	float bulletSize = 5f;
	
	float magazine = 35f;
	float bulletFired = 0f;
	
	float fireTimer = 0f;
	boolean startTimer = false;
	
	float gunDamage = 100f;
	
	gunTypes gunType = gunTypes.primary;
	
	public void setGunPropreties(float fireRate, float magazine, gunTypes gunType, String spritePath, float gunRecoil, float gunDamage) {
		setFireRate(fireRate);
		setMagazine(magazine);
		setSprite(spritePath);
		setGunType(gunType);
		setGunRecoil(gunRecoil);
	}
	
	public Gun(Game core) {
		this.transform.height = 3;
		
		if (core != null)
			core.addNewGameObject(this);
	}
	
	public Gun() {
		this.transform.height = 3;
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
	public void drawObject(Graphics2D g, Camera camera) {
		AffineTransform oldTrans = g.getTransform();
		
		Vector2 offset = new Vector2();
		Vector2 cameraOffset = new Vector2();
		if (camera != null) {
			offset = camera.getPosition().clone();
			cameraOffset = camera.getOffset().clone();
		}
		
		g.rotate(rotationDegree, position.x + (64 / 2) - offset.x + cameraOffset.x, position.y + (64 / 2) - offset.y + cameraOffset.y);
//			g.rotate(rotationDegree, rotationOrigin.x + (scale().x / 2), rotationOrigin.y + (scale().y));		
		if (isReloading()) {
			rotationDegree += 0.2f;
			currentRotationDegree += 0.2f;
			
			if (Math.toDegrees(currentRotationDegree) >= 360) {
				reloaded();
			}
		}
		
		if (!isFlipped) 
			g.drawImage(sprite, (int) (this.position().x - offset.x + cameraOffset.x), (int) (this.position().y - offset.y + cameraOffset.y), 64, 64, null);
		else
			g.drawImage(sprite, (int) (this.position().x - offset.x + cameraOffset.y), (int) (this.position().y + 64 - offset.y + cameraOffset.y), 64, -64, null);	
		
		g.setTransform(oldTrans);
	}
	
	public void playEmptySound() {
		if (!emptySound.isRunning()) {
			emptySound.play();			
		}
	}
	
	void reloaded() {
		bulletFired = 0f;
		
		setReloading(false);
		currentRotationDegree = 0f;
	}
	
	public void reloadGun() {
		setReloading(true);
		
		if (!reloadSound.isRunning()) {
			reloadSound.play();
		}
	}
	
	public boolean getIsReloading() {
		return isReloading();
	}
	
	public void getInputs(float delta, Game core) {
		if (startTimer) {
			fireTimer += delta;
			
			if (fireTimer > this.getFireRate()) {
				this.setCanFire(true);
				fireTimer = 0f;
				startTimer = false;
			}
		}
		
		if (core.input().isKeyDown(KeyEvent.VK_R)) {
			this.reloadGun();	
		}
		
		if (core.input().isButton(MouseEvent.BUTTON1) && this.getCanFire() && !this.getIsReloading()) {
			if (this.getBulletFired() < this.getMagazine()) {
				// Add recoil
				Vector2 recoil = new Vector2(core.input().getMouseX() - position().x - (64 / 2), core.input().getMouseY() - position().y - (64 / 2));
				recoil.normalize();
				holder.addRecoil(recoil.multiply(-gunRecoil));
				
				// Bullet instance point
				Vector2 instancePoint = new Vector2(position().x + (64), position().y + (64 / 2));
				instancePoint = instancePoint.rotatePoint(position().x + (64 / 2), position().y + (64 / 2), this.getRotationDegrees());
				
				if (core.getCurrentCamera() != null) {
					core.getCurrentCamera().applyShake();
				}
				
				this.fire();
				
				// Instance bullet
//			((Shooter) parent).instanceBullet(instancePoint, recoil); 
				
				Packet04_BulletSpawn packet = new Packet04_BulletSpawn(holder.getUsername(), recoil, instancePoint, getBulletSize(),gunDamage);
				packet.writeData(core.getGameClient());
				
				startTimer = true;
				this.setCanFire(false);	
			} else {
				this.playEmptySound();
			}
		}
	}
	
/// SETTER GETTERS
	public void setJoueur(Joueur joueur) {
		this.holder = joueur;
	}
	
	public Joueur getJoueur(Joueur joueur) {
		return this.holder;
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

	public void setReloading(boolean isReloading) {
		this.isReloading = isReloading;
	}

	public boolean isReloading() {
		return isReloading;
	}
	
	public void setGunType(gunTypes gunType) {
		this.gunType = gunType;
	}
	
	public gunTypes getGunType() {
		return this.gunType;
	}

	public float getGunRecoil() {
		return gunRecoil;
	}

	public void setGunRecoil(float gunRecoil) {
		this.gunRecoil = gunRecoil;
	}

	public SoundClip getEmptySound() {
		return emptySound;
	}

	public void setEmptySound(SoundClip emptySound) {
		this.emptySound = emptySound;
	}

	public SoundClip getReloadSound() {
		return reloadSound;
	}

	public void setReloadSound(SoundClip reloadSound) {
		this.reloadSound = reloadSound;
	}

	public float getBulletSize() {
		return bulletSize;
	}

	public void setBulletSize(float bulletSize) {
		this.bulletSize = bulletSize;
	}

	public float getGunDamage() {
		return gunDamage;
	}

	public void setGunDamage(float gunDamage) {
		this.gunDamage = gunDamage;
	}
}
