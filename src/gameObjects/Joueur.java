package gameObjects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import core.Game;
import engineClasses.Vector2;
import gameObjects.guns.Desert_Eagle;
import gameObjects.guns.Gun;
import gameObjects.guns.Rifle;
import gameObjects.guns.WeaponManager;
import net.packets.Packet10_TakeDamage;
import net.packets.Packet13_Alive;
import net.packets.Packet02_Movement;
import net.packets.Packet03_PlayerDetails;
import net.packets.Packet05_WeaponSwitch;
import net.packets.Packet06_Died;
import net.packets.Packet07_StartGame;

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
	WeaponManager weaponManager;
	
	boolean equipGun = false;
	
	Object parent;
	
	// Entity propreties
	float hp = 1000f;
	boolean alive = true;
	
	// Timer
	float timer = 0f;
	float maxTime = 5f;
	
	Vector2 puppetPosition = new Vector2();
	Vector2 puppetGunPosition = new Vector2();
	float puppetGunRotation = 0f;
	float puppetHp = hp;
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
		
		weaponManager = new WeaponManager();
		if (core != null) {
			weaponManager.addGun(new Rifle(core));
			weaponManager.addGun(new Desert_Eagle(core));
		}
	}
	
	@Override
	public void drawObject(Graphics2D g, Camera camera) {
		g.setColor(material);
		
		Vector2 offset = new Vector2();
		Vector2 cameraOffset = new Vector2();
		if (camera != null) {
			offset = camera.getPosition().clone();
			cameraOffset = camera.getOffset();
		}
		
		g.drawImage(sprite, (int) (this.position().x - offset.x + cameraOffset.x), (int) ((this.position().y + animation) - offset.y + cameraOffset.y), (int) this.scale().x, (int) this.scale().y, null);
//		g.fillRect((int) this.position().x, (int) (this.position().y + animation), (int) this.scale().x, (int) this.scale().y);
		g.drawString(username, this.position().x - offset.x + cameraOffset.x, this.position().y - (2) + animation - offset.y + cameraOffset.y);
	}
	
	public void setVelocity(Vector2 velocity) {
		this.velocity = velocity;
	}
	
	public void update(Game core, float delta) {
		timer += delta;
		
		if(gun != null) {
			manageGunRotation(core);
		}
		
		if (isNetworkMaster) {
			
			// Player movement
			if (isAlive()) {
				manageMovement(core, delta);
				
				if (equipGun) weaponManager.getInputs(core, this);
				
				// Gun rotation			
				if (gun != null && equipGun) gun.getInputs(delta, core);
				
				if (timer > maxTime) {
//				System.out.println("Timeout: " + this.position().toString());
					
					Packet02_Movement packet = new Packet02_Movement(this.getUsername(), this.position());
					packet.writeData(core.getGameClient());	
					
					timer = 0f;
				}				
			}
		} else {
			this.position = this.position.linearInterpolate(puppetPosition, delta * .1f);
			
			if (gun != null) {
				gun.setRotationOrigin(position().x, position().y + (scale().y / 2));
				gun.setRotationDegrees(puppetGunRotation);
				gun.setPosition(puppetGunPosition);
				
				gun.flipImage(puppetGunRotated);				
			}
		}
	}
	
	void manageGunRotation(Game core) {
//		Vector2 toMousePosition = new Vector2(core.input().getMouseY() - gun.position().y - (scale().y / 2), core.input().getMouseX() - gun.position().x - (scale().x / 2));
//		gun.setRotationOrigin(position().x, position().y + (scale().y / 2));
		gunDesiredPosition = position.add(new Vector2(30, 0));
		
		Vector2 gunDesiredPositionRotation = new Vector2(core.input().getMouseY() - position().y - (scale().y / 2), core.input().getMouseX() - position().x - (scale().x / 2));
		float desiredRotation = (float) Math.atan2(gunDesiredPositionRotation.x, gunDesiredPositionRotation.y);
		
		gunDesiredPosition = gunDesiredPosition.rotatePoint(position().x, position().y, desiredRotation);
		
//		gun.setRotationDegrees((float) Math.atan2(toMousePosition.x, toMousePosition.y));
		if (!gun.isReloading() && isNetworkMaster) gun.setRotationDegrees(desiredRotation);
		
		gun.setPosition(gunDesiredPosition);
		
		float rotationAngle = Math.signum(core.input().getMouseX() - (position().x + (scale().x / 2)));
		boolean flipped = false;
		if (rotationAngle < 0) {
			flipped = true;
		} 
		
		gun.flipImage(flipped);
		
		if (isNetworkMaster) {
			Packet03_PlayerDetails details = new Packet03_PlayerDetails(this.getUsername(), gun.getRotationDegrees(), flipped, gunDesiredPosition);
			details.writeData(core.getGameClient());			
		}
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
	
	public void addRecoil(Vector2 recoil) {
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
	
	public void equipGun(Gun gun, Game core) {
		this.gun = gun;
		
		System.out.println("Gun: " +  gun);
		
		if (isNetworkMaster) {
			Packet05_WeaponSwitch packet = new Packet05_WeaponSwitch(getUsername(), gun);
			packet.writeData(core.getGameClient());			
		}
		
		this.gun.setJoueur(this);
	}
	
	public void setGun(Gun gun, Game core) {
		if (isNetworkMaster) return;
		
		if (this.gun != null) this.gun.destroy(core);
		
		this.gun = gun;
		this.gun.setJoueur(this);
		
		this.gun.setActive(true);
	}
	
	public WeaponManager getWeaponManager() {
		return this.weaponManager;
	}
	
	public Gun getGun() {
		return this.gun;
	}
	
	// Entity
	public void takeDamage(Game core, float damage) {
		this.hp -= damage;
		
		System.out.println("Took damage " + hp);
		
		Packet10_TakeDamage packet = new Packet10_TakeDamage(getUsername(), this.hp);
		packet.writeData(core.getGameClient());
	}

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
		this.setActive(alive);
		
		if (this.gun != null) this.gun.setActive(alive);
	}

	public boolean isEquipGun() {
		return equipGun;
	}

	public void setEquipGun(Game core, boolean equipGun) {
		if (!isNetworkMaster) {
			Packet07_StartGame packet = new Packet07_StartGame(getUsername());
			packet.writeData(core.getGameClient());
		}
		
		this.equipGun = equipGun;
		
		if (this.equipGun == false && this.gun != null) {
			gun.setActive(false);
		}
	}
	
	public void setEquipGun(boolean equipGun) {
		this.equipGun = equipGun;
	}

	public void setHp(Game core, float hp) {
		this.hp = hp;
		
		if (this.hp <= 0f && alive) {
			Packet06_Died packet = new Packet06_Died(getUsername());
			packet.writeData(core.getGameClient());			
		}
	}
}
