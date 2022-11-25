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
	Gun gun;
	
	Object parent;
	
	// Timer
	float timer = 0f;
	float maxTime = 10f;
	
	Vector2 puppetPosition = new Vector2();
	float puppetGunRotation = 0f;
	boolean puppetGunRotated = false;
	
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
		// Translate gun w/ player
		gun.setPosition(this.position().add(new Vector2(40, 0)));
		
		if (isNetworkMaster) {
			timer += delta;
			
			// Player movement
			manageMovement(core, delta);
			
			if (core.input().isButton(MouseEvent.BUTTON1)) {
				// Add recoil
				Vector2 recoil = new Vector2(core.input().getMouseX() - position().x - (scale().x / 2), core.input().getMouseY() - position().y - (scale().y / 2));
				recoil.normalize();
				addRecoil(recoil.multiply(-0.2f));
				
				// Bullet instance point
				Vector2 instancePoint = new Vector2(position().x + 64, gun.position().y + (64 / 2));
				instancePoint = instancePoint.rotatePoint(position().x + (scale().x / 2), position().y + (scale().y / 2), gun.getRotationDegrees());
				
				gun.fire();
				
				// Instance bullet
//				((Shooter) parent).instanceBullet(instancePoint, recoil); 
				
				Packet04_BulletSpawn packet = new Packet04_BulletSpawn(this.getUsername(), recoil, instancePoint);
				packet.writeData(core.getGameClient());
			}
			
			// Gun rotation
			manageGunRotation(core);
			
			if (timer > maxTime) {
				System.out.println("Timeout: " + this.position().toString());
				
				Packet02_Movement packet = new Packet02_Movement(this.getUsername(), this.position());
				packet.writeData(core.getGameClient());	
				
				timer = 0f;
			}
		} else {
			this.position = this.position.linearInterpolate(puppetPosition, delta * acceleration);
			
			gun.setRotationOrigin(position().x, position().y + (scale().y / 2));
			gun.setRotationDegrees(puppetGunRotation);
			
			gun.flipImage(puppetGunRotated);
		}
	}
	
	void manageGunRotation(Game core) {
		Vector2 toMousePosition = new Vector2(core.input().getMouseY() - position().y - (scale().y / 2), core.input().getMouseX() - position().x - (scale().x / 2));
		gun.setRotationOrigin(position().x, position().y + (scale().y / 2));
		gun.setRotationDegrees((float) Math.atan2(toMousePosition.x, toMousePosition.y));
		
		float rotationAngle = Math.signum(core.input().getMouseX() - (position().x + (scale().x / 2)));
		boolean flipped = false;
		if (rotationAngle < 0) {
			flipped = true;
		} 
		
		gun.flipImage(flipped);
		
		Packet03_PlayerDetails details = new Packet03_PlayerDetails(this.getUsername(), gun.getRotationDegrees(), flipped);
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
