package gameCore;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import core.Game;
import engineClasses.Vector2;
import gameObjects.Bullet;
import gameObjects.Joueur;
import net.packets.Packet00_Login;

public class Shooter {
	Game core;
	
	// Game
	Joueur joueur;
	
	ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	ArrayList<Joueur> players = new ArrayList<Joueur>();
	
	public Shooter (Game core) {
		this.core = core;
		
		Joueur joueur = instancePlayerAt(new Vector2(100, 100), false);
		joueur.setIpAdress(null);
		joueur.setPort(-1);
		joueur.setIsNetworkMaster(true);
		joueur.setUsername(JOptionPane.showInputDialog(core.getGameFrame(), "Veuillez entrer un nom d'utilisateur: "));
		
		addPlayer(joueur);
		Packet00_Login packet = new Packet00_Login(joueur.getUsername());
		
		if (core.getGameServer() != null) {
			core.getGameServer().addConnection(joueur, packet);
		}
		
		packet.writeData(core.getGameClient());
	}
	
	
	public void update(float delta) {
		for (int j = 0; j < players.size(); j++) {
			players.get(j).update(core, delta);
		}
		
		for (int i = 0; i < bullets.size(); i++) {
			bullets.get(i).update();
		}
	}
	
	public void instanceBullet(Vector2 position, Vector2 velocity) {
		Bullet bullet = new Bullet(5, 5, core);
		
		bullet.setPosition(position);
		bullet.setVelocity(velocity);
		
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
}
