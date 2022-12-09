package gameCore;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import Scenes.MainGame;
import Scenes.Lobby;
import core.Game;
import engineClasses.Scene;
import engineClasses.SceneManager;
import engineClasses.Vector2;
import gameObjects.Bullet;
import gameObjects.Camera;
import gameObjects.Joueur;
import gameObjects.guns.Gun;
import net.packets.Packet00_Login;
import net.packets.Packet11_DestroyBullet;
import net.packets.Packet12_ChangeScene;
import net.packets.Packet13_Alive;
import uiComponents.UIButton;
import uiComponents.UIText;

public class Shooter {
	Game core;

	// Game
	Joueur joueur;

	static ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	static ArrayList<Joueur> players = new ArrayList<Joueur>();
	static ArrayList<Joueur> alivePlayers = new ArrayList<Joueur>();

	// UI
	UIText winnerText;
	UIButton startButton;

	boolean gameHasStarted = false;

	Camera currentCamera;

	// Scenes
	Scene mainGame;
	Scene lobby;

	boolean changed = false;

	static ArrayList<Scene> scenes = new ArrayList<Scene>();
	
	// Win timer
	boolean winTimerStarted = false;
	float winTimer = 200f;
	
	public Shooter(Game core) {
		this.core = core;
		SceneManager.getInstance().setGameCore(core);

		addScene(new MainGame(core));
		addScene(new Lobby(core));

		changeScene("Lobby");

		joueur = instancePlayerAt(new Vector2(100, 100), false);
		joueur.setIpAdress(null);
		joueur.setPort(-1);
		joueur.setIsNetworkMaster(true);

		String username = JOptionPane.showInputDialog(core.getGameFrame(), "Veuillez entrer un nom d'utilisateur: ");

		joueur.setUsername(username);

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
		startButton.setPosition(new Vector2(core.getViewportRect().x - startButton.scale().x,
				core.getViewportRect().y - startButton.scale().y));
		startButton.setText("Start Game");

		if (core.getGameServer() != null) {
			core.getGameServer().addConnection(joueur, packet);
		}

		packet.writeData(core.getGameClient());
	}

	public void update(float delta) {
		// Scene Update
		SceneManager.getInstance().update();
		SceneManager.getInstance().render();

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
			if (winTimerStarted) {
				winTimer -= delta;
				
				if (winTimer <= 0f && core.isServer()) {
					returnToLobby();
					
					winTimer = 200f;
					winTimerStarted = false;
				}
			}
			
			
			if (alivePlayers.size() <= 1 && alivePlayers.size() > 0) {
				System.out.println(alivePlayers.get(0).getUsername());
				if (!winTimerStarted) {
					winnerText.setActive(true);
					
					winTimerStarted = true;
				}
			}

			handleCollisions();
		}
	}
	
	public void returnToLobby() {
		winnerText.setActive(false);
		alivePlayers.clear();
		
		for (int i = 0; i < players.size(); i++) {
			Packet13_Alive packet = new Packet13_Alive(players.get(i).getUsername());
			packet.writeData(core.getGameClient());
		}
		
		for (int i = 0; i < players.size(); i++) {
			players.get(i).setEquipGun(core, false);
		}
		
		syncChangeScene("Lobby");
		
		gameHasStarted = false;
	}

	void startGame() {
		for (int i = 0; i < players.size(); i++) {
			alivePlayers.add(players.get(i));
		}

		for (int i = 0; i < players.size(); i++) {
			players.get(i).setHp(core, 1000f);
			players.get(i).setEquipGun(core, true);
		}
		
		syncChangeScene("MainGame");

		gameHasStarted = true;
		startButton.setActive(false);
	}

	public void setPlayerEquip(String username, boolean canEquip) {
		int index = getJoueurIndex(username);

		if (index != -1)
			players.get(index).setEquipGun(canEquip);
	}

	public void handleCollisions() {
		// Tsy optimiser
		for (int i = 0; i < players.size(); i++) {
			for (int j = 0; j < bullets.size(); j++) {
				if (players.get(i) == null || bullets.get(j) == null) continue;
				
				if (Vector2.distance(players.get(i).position(), bullets.get(j).position()) < players.get(i).scale().x
						+ bullets.get(j).scale().x) {
					if (players.get(i).getIsActive() && !bullets.get(j).getOwner().equals(players.get(i))) {

						if (core.isServer()) {
							float damage = bullets.get(j).dealDamage();
							bullets.get(j).syncDestroy(core, j);

							System.out.println("DAMAGE: " + damage);

							players.get(i).takeDamage(core, damage);
						}
						break;
					}
				}
			}
		}
	}

	public void instanceBullet(String username, Vector2 position, Vector2 velocity, float bulletSize,
			float bulletDamage) {
		Bullet bullet = new Bullet("Bullet" + bullets.size(), bulletSize, bulletSize, core);

		int index = getJoueurIndex(username);
		if (index == -1)
			return;

		bullet.setOwner(players.get(index));

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

	public void removePlayer(String username) {
		int removeIndex = 0;
		int gameObjectRemoveIndex = 0;
		for (int i = 0; i < Shooter.players.size(); i++) {
			if (players.get(i).getUsername().equals(username)) {
				removeIndex = i;
			}
		}

		for (int i = 0; i < core.getGameObjectsOnScene().size(); i++) {
			if (core.getGameObjectsOnScene().get(i) instanceof Joueur
					&& ((Joueur) core.getGameObjectsOnScene().get(i)).getUsername().equals(username)) {
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
			if (players.get(i).getUsername().equals(username))
				return i;
		}

		return -1;
	}

	public void moveJoueur(String username, Vector2 position) {
		int index = getJoueurIndex(username);

		if (index != -1)
			players.get(index).setPuppetPosition(position);
	}

	public void manageGunRotations(String username, float rotation) {
		int index = getJoueurIndex(username);

		if (index != -1) {
			players.get(index).setPuppetGunRotation(rotation);
		}
	}

	public void manageGunFlip(String username, boolean isFlipped) {
		int index = getJoueurIndex(username);

		if (index != -1) {
			players.get(index).setPuppetGunFlipped(isFlipped);
		}
	}

	public void manageGun(String username, float rotation, boolean isFlipped, Vector2 position) {
		int index = getJoueurIndex(username);

		if (index != -1) {
			players.get(index).setPuppetGunRotation(rotation);
			players.get(index).setPuppetGunFlipped(isFlipped);
			players.get(index).setPuppetGunPosition(position);
		}
	}

	public void setPlayerGun(String username, Class<?> gun) {
		int index = getJoueurIndex(username);

		if (index != -1) {
			try {
				players.get(index).setGun((Gun) gun.getConstructor(Game.class).newInstance(core), core);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
		}
	}

	public void setPlayerHp(String username, float hp) {
		int index = getJoueurIndex(username);

		if (index != -1) {
			players.get(index).setHp(core, hp);
		}
	}

	public void isDead(String username) {
		int index = getJoueurIndex(username);
		players.get(index).setAlive(false);

		int deadPlayerIndex = -1;
		for (int i = 0; i < alivePlayers.size(); i++) {
			if (alivePlayers.get(i).getUsername().equals(username)) {
				deadPlayerIndex = i;
			}
		}
		if (deadPlayerIndex != -1)
			alivePlayers.remove(deadPlayerIndex);
	}
	
	public void isAlive(String username) {
		int index = getJoueurIndex(username);
		players.get(index).setAlive(true);
	}

	public void removeBulletFromScene(String id, int indexOnScene) {

		int destroyId = -1;

		for (int i = 0; i < core.getGameObjectsOnScene().size(); i++) {
			if (core.getGameObjectsOnScene().get(i).getId().equals(id)) {
				destroyId = i;
			}
		}

		if (destroyId != -1) {
			core.getGameObjectsOnScene().remove(destroyId);
		}

		if (bullets.size() > 0 && indexOnScene < bullets.size()) {
			bullets.remove(indexOnScene);
		}
	}
	
	public void addScene(Scene scene) {
		scenes.add(scene);
	}
	
	public int getSceneIdByName(String name) {
		for (int i = 0; i < scenes.size(); i++) {
			if (scenes.get(i).getClass().getSimpleName().equalsIgnoreCase(name)) {
				return i;
			}
		}
		
		return -1;
	}
	
	public void changeScene(String sceneName) {
		SceneManager.changeScene(scenes.get(getSceneIdByName(sceneName)));
	}
	
	public void syncChangeScene(String name) {
		Packet12_ChangeScene packet = new Packet12_ChangeScene(name);
		packet.writeData(core.getGameClient());
	}
 }
