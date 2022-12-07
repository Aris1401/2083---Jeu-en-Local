package gameCore;

import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import core.Game;
import engineClasses.Vector2;
import gameObjects.Bullet;
import gameObjects.Camera;
import gameObjects.Joueur;
import gameObjects.guns.Gun;
import net.packets.Packet00_Login;
import uiComponents.UIButton;
import uiComponents.UIText;

public class Shooter {
	Game core;
	
	// Game
	Joueur joueur;
	
	ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	ArrayList<Joueur> players = new ArrayList<Joueur>();
	
	// UI
	UIText winnerText;
	UIButton startButton;
	
	boolean gameHasStarted = false;
	
	Camera currentCamera;
	
	public Shooter (Game core) {
		this.core = core;
		
		joueur = instancePlayerAt(new Vector2(100, 100), false);
		joueur.setIpAdress(null);
		joueur.setPort(-1);
		joueur.setIsNetworkMaster(true);
		joueur.setUsername(JOptionPane.showInputDialog(core.getGameFrame(), "Veuillez entrer un nom d'utilisateur: "));
		
		addPlayer(joueur);
		Packet00_Login packet = new Packet00_Login(joueur.getUsername());
		
		currentCamera = new Camera(new Vector2(), core);
		core.setCurrentCamera(currentCamera);
		
		winnerText = new UIText(30, core);
		winnerText.setText("Winner winner chicken dinner");
		winnerText.setActive(false);
		winnerText.setPosition(new Vector2((core.getViewportRect().x / 2) - 210, core.getViewportRect().y / 2));
		winnerText.setIsNetworkMaster(true);
		
		
		startButton = new UIButton(150, 50, core);
		startButton.setActive(false);
		startButton.setPosition(new Vector2(core.getViewportRect().x - startButton.scale().x, core.getViewportRect().y - startButton.scale().y));
		startButton.setText("Start Game");
		
		if (core.getGameServer() != null) {
			core.getGameServer().addConnection(joueur, packet);
		}
		
		packet.writeData(core.getGameClient());
	}
	
	public void update(float delta) {
		if (!startButton.getIsActive() && players.size() > 1 && core.isServer() && !gameHasStarted) {
			startButton.setActive(true);
		}
		
		if (startButton.getIsActive() && core.isServer() && !gameHasStarted) {
			if (startButton.isButtonHovered(new Vector2(core.input().getMouseX(), core.input().getMouseY()))) {
				if (core.input().isButtonUp(MouseEvent.BUTTON1)) {
					startGame();
				}
			}
		}
		
		currentCamera.target(joueur);
		
		for (int j = 0; j < players.size(); j++) {
			players.get(j).update(core, delta);
		}
		
		for (int i = 0; i < bullets.size(); i++) {
			bullets.get(i).update(delta);
		}
		
		if (gameHasStarted) {
			if (getAlivePlayerNumber().size() <= 1 && getAlivePlayerNumber().size() > 0) {
				if (getAlivePlayerNumber().get(0).getIsNetworkMaster()) {
					winnerText.setActive(true);
				}
			} 			
		}
		
		if (gameHasStarted)
			handleCollisions();
	}
	
	void startGame() {
		for (int i = 0; i < players.size(); i++) {
			players.get(i).setEquipGun(core, true);
		}
		
		gameHasStarted = true;
		startButton.setActive(false);
	}
	
	public void setPlayerEquip(String username, boolean canEquip) {
		int index = getJoueurIndex(username);
		
		players.get(index).setEquipGun(true);
	}
	
	public void handleCollisions() {
		// Tsy optimiser
		for (int i = 0; i < players.size(); i++) {
			for (int j = 0; j < bullets.size(); j++) {
				if (players.get(i).getRectange().intersects(bullets.get(j).getRectange())) {
					if (players.get(i).getIsActive()) {
						players.get(i).takeDamage(bullets.get(j).dealDamage());
						bullets.get(j).destroy(core);						
					}
				}
			}
		}
	}
	
	public void instanceBullet(Vector2 position, Vector2 velocity, float bulletSize, float bulletDamage) {
		Bullet bullet = new Bullet(bulletSize, bulletSize, core);
		
		bullet.setPosition(position);
		bullet.setVelocity(velocity);
		bullet.setBulletDamage(bulletDamage);
		
		bullets.add(bullet);
	}
	
	public Joueur instancePlayerAt(Vector2 position, boolean fromServer) {
		Joueur nouveauJoueur = null;
		if (fromServer)
			nouveauJoueur = new Joueur(40, 60, null);
		else
			nouveauJoueur = new Joueur(40, 60, core);
		
		nouveauJoueur.setPosition(position);
		nouveauJoueur.setParent(this);
		
		return nouveauJoueur;
	}
	
	public void addPlayer(Joueur joueur) {
		players.add(joueur);
	}
	
	public ArrayList<Joueur> getAlivePlayerNumber() {
		ArrayList<Joueur> alivePlayers = new ArrayList<Joueur>();
		
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).isAlive()) {
				alivePlayers.add(players.get(i));
			}
		}
		
		return alivePlayers;
	}
	
	public void removePlayer(String username) {
		int removeIndex = 0;
		int gameObjectRemoveIndex = 0;
		for (int i = 0; i < this.players.size(); i++) {
			if (players.get(i).getUsername().equals(username)) {
				removeIndex = i;
			}
		}
		
		for (int i = 0; i < core.getGameObjectsOnScene().size(); i++) {
			if (core.getGameObjectsOnScene().get(i) instanceof Joueur && ((Joueur) core.getGameObjectsOnScene().get(i)).getUsername().equals(username)) {
				gameObjectRemoveIndex = i;
			}
		}
		
		players.get(removeIndex).destroyPlayer(core);
		players.remove(removeIndex);
		core.getGameObjectsOnScene().remove(gameObjectRemoveIndex);
	}
	
	public Joueur getCurrentJoueur() {
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).getIsNetworkMaster()) {
				return players.get(i);
			}
		}
		
		return null;
	}
	
	public int getJoueurIndex(String username) {
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).getUsername().equals(username)) return i;
		}
		
		return -1;
	}
	
	public void moveJoueur(String username, Vector2 position) {
		int index = getJoueurIndex(username);
		
		players.get(index).setPuppetPosition(position);
	}
	
	public void manageGunRotations(String username, float rotation) {
		int index = getJoueurIndex(username);
		
		players.get(index).setPuppetGunRotation(rotation);
	}
	
	public void manageGunFlip(String username, boolean isFlipped) {
		int index = getJoueurIndex(username);
		
		players.get(index).setPuppetGunFlipped(isFlipped);
	}
	
	public void manageGun(String username, float rotation, boolean isFlipped, Vector2 position) {
		int index = getJoueurIndex(username);
		
		players.get(index).setPuppetGunRotation(rotation);
		players.get(index).setPuppetGunFlipped(isFlipped);
		players.get(index).setPuppetGunPosition(position);
	}
	
	public void setPlayerGun(String username, Class<?> gun) {
		int index = getJoueurIndex(username);
		
		try {
			players.get(index).setGun((Gun) gun.getConstructor(Game.class).newInstance(core), core);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
	public void isDead(String username) {
		int index = getJoueurIndex(username);
		
		players.get(index).setActive(false);
		players.get(index).setAlive(false);
	}
 }
