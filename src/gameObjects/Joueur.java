package gameObjects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import core.Game;
import engineClasses.Vector2;
import gameCore.Shooter;
import net.packets.Packet02_Movement;
import net.packets.Packet03_PlayerDetails;
import net.packets.Packet04_BulletSpawn;

public class Joueur extends GameObject{
	String username = "NULL";
	
	Vector2 movement = new Vector2();
	Vector2 velocity = new Vector2();
	
	float time = 0;
	float amplitude = 2.5f;
	float frequence = .4f;
	
	float animation = 0;
	
	float movementSpeed = 3f;
	float acceleration = .05f;
	
//	Guns
	Vector2 gunDesiredPosition = new Vector2();
	
	Gun gun;
	float fireTimer = 0f;
	boolean startTimer = false;
	
	Object parent;
	
	// Timer
	float timer = 0f;
	float maxTime = 5f;
	
	Vector2 puppetPosition = new Vector2();
	float puppetGunRotation = 0f;
	boolean puppetGunRotated = false;
	Vector2 puppetGunPosition = new Vector2();
	
	public void setParent(Object parent) {
		this.parent = parent;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public Joueur(float width, float height, Game core) {
		this.transform.width =  width;
		this.transform.height = height;
		
		this.id = "Joueur";
		
		if (core != null)
			core.addNewGameObject(this);
		
		this.setSprite("Imposter.png");
		this.setMaterial(new Color(106, 106, 106));
		
		if (core != null) {
			gun = new Gun(core);
			gun.setSprite("Rifle.png");			
		}
	}
	
	@Override
	public void drawObject(Graphics2D g) {
		g.setColor(material);
		g.drawImage(sprite, (int) this.position().x, (int) (this.position().y + animation), (int) this.scale().x, (int) this.scale().y, null);
//		g.fillRect((int) this.position().x, (int) (this.position().y + animation), (int) this.scale().x, (int) this.scale().y);
		g.drawString(username, this.position().x, this.position().y - (2) + animation);
	}
	
	public void setVelocity(Vector2 velocity) {
		this.velocity = velocity;
	}
	
	public void update(Game core, float delta) {
		timer += delta;
		
		if (startTimer) {
			fireTimer += delta;
			
			if (fireTimer > gun.getFireRate()) {
				gun.setCanFire(true);
				fireTimer = 0f;
				startTimer = false;
			}
		}
		
		if (isNetworkMaster) {
			// Player movement
			manageMovement(core, delta);
			
			if (core.input().isKeyDown(KeyEvent.VK_R)) {
				gun.reloadGun();	
			}
			
			if (core.input().isButton(MouseEvent.BUTTON1) && gun.getCanFire()) {
				if (gun.getBulletFired() < gun.getMagazine()) {
					// Add recoil
					Vector2 recoil = new Vector2(core.input().getMouseX() - position().x - (scale().x / 2), core.input().getMouseY() - position().y - (scale().y / 2));
					recoil.normalize();
					addRecoil(recoil.multiply(-0.2f));
					
					// Bullet instance point
					Vector2 instancePoint = new Vector2(position().x + (64 + (64 / 2)), position().y + (64 / 2));
					instancePoint = instancePoint.rotatePoint(position().x + (scale().x / 2), position().y + (scale().y / 2), gun.getRotationDegrees());
					
					gun.fire();
					
					// Instance bullet
//				((Shooter) parent).instanceBullet(instancePoint, recoil); 
					
					Packet04_BulletSpawn packet = new Packet04_BulletSpawn(this.getUsername(), recoil, instancePoint);
					packet.writeData(core.getGameClient());
					
					startTimer = true;
					gun.setCanFire(false);	
				} else {
					gun.playEmptySound();
				}
			}
			
			// Gun rotation
			manageGunRotation(core);
			
			if (timer > maxTime) {
//				System.out.println("Timeout: " + this.position().toString());
				
				Packet02_Movement packet = new Packet02_Movement(this.getUsername(), this.position());
				packet.writeData(core.getGameClient());	
				
				timer = 0f;
			}
		} else {
			this.position = this.position.linearInterpolate(puppetPosition, delta * .1f);
			
			gun.setRotationOrigin(position().x, position().y + (scale().y / 2));
			gun.setRotationDegrees(puppetGunRotation);
			gun.setPosition(puppetGunPosition);
			
			gun.flipImage(puppetGunRotated);
		}
	}
	
	void manageGunRotation(Game core) {
		Vector2 toMousePosition = new Vector2(core.input().getMouseY() - gun.position().y - (scale().y / 2), core.input().getMouseX() - gun.position().x - (scale().x / 2));
//		gun.setRotationOrigin(position().x, position().y + (scale().y / 2));
		
		Vector2 gunDesiredPositionRotation = new Vector2(core.input().getMouseY() - position().y - (scale().y / 2), core.input().getMouseX() - position().x - (scale().x / 2));
		float desiredRotation = (float) Math.atan2(gunDesiredPositionRotation.x, gunDesiredPositionRotation.y);
		
		gunDesiredPosition = position.add(new Vector2(30, 0));
		gunDesiredPosition = gunDesiredPosition.rotatePoint(position().x, position().y, desiredRotation);
		
//		gun.setRotationDegrees((float) Math.atan2(toMousePosition.x, toMousePosition.y));
		if (!gun.isReloading)
			gun.setRotationDegrees(desiredRotation);
		
		gun.setPosition(gunDesiredPosition);
		
		float rotationAngle = Math.signum(core.input().getMouseX() - (position().x + (scale().x / 2)));
		boolean flipped = false;
		if (rotationAngle < 0) {
			flipped = true;
		} 
		
		gun.flipImage(flipped);
		
		Packet03_PlayerDetails details = new Packet03_PlayerDetails(this.getUsername(), gun.getRotationDegrees(), flipped, gunDesiredPosition);
		details.writeData(core.getGameClient());
	}
	
	void manageMovement(Game core, float delta) {
		time += delta;
		
		movement = new Vector2();
		
		if (core.input().isKey(KeyEvent.VK_W)) {
			movement.y -= 1;
		}
		
		if (core.input().isKey(KeyEvent.VK_S)) {
			movement.y += 1;		
		}
		
		if (core.input().isKey(KeyEvent.VK_D)) {
			movement.x += 1;
		}
		
		if (core.input().isKey(KeyEvent.VK_A)) {
			movement.x -= 1;
		}
		
		movement.normalize();
		animation = (float) ((Math.cos(time * frequence) * amplitude) * movement.length()) * delta;
		velocity = velocity.linearInterpolate(movement.multiply(movementSpeed), acceleration * delta);
		
//		System.out.println(velocity.length());s
	
		translate(velocity);				
	}
	
	void addRecoil(Vector2 recoil) {
		velocity = velocity.add(recoil);
	}
	
/// Network propreties
	public void setPuppetPosition(Vector2 position) {
		puppetPosition = position;
	}
	
	public void setPuppetGunRotation(float rotations) {
		puppetGunRotation = rotations;
	}
	
	public void setPuppetGunFlipped(boolean isFlipped) {
		this.puppetGunRotated = isFlipped;
	}
	
	public void setPuppetGunPosition(Vector2 position) {
		this.puppetGunPosition = position;
	}
	
	public void destroyPlayer(Game core) {
		int gunIndex = 0;
		
		for (int i = 0; i < core.getGameObjectsOnScene().size(); i++) {
			if (core.getGameObjectsOnScene().get(i) instanceof Gun && core.getGameObjectsOnScene().get(i).equals((Gun) gun)) {
				gunIndex = i;
			}
		}
		
		core.getGameObjectsOnScene().remove(gunIndex);
	}
}
