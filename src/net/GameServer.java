package net;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

import core.Game;
import engineClasses.Vector2;
import gameObjects.Joueur;
import gameObjects.guns.Gun;
import net.packets.Packet;
import net.packets.Packet.TypesPacket;
import net.packets.Packet00_Login;
import net.packets.Packet10_TakeDamage;
import net.packets.Packet11_DestroyBullet;
import net.packets.Packet12_ChangeScene;
import net.packets.Packet13_Alive;
import net.packets.Packet14_DisconnectServer;
import net.packets.Packet01_Disconnect;
import net.packets.Packet02_Movement;
import net.packets.Packet03_PlayerDetails;
import net.packets.Packet04_BulletSpawn;
import net.packets.Packet05_WeaponSwitch;
import net.packets.Packet06_Died;
import net.packets.Packet07_StartGame;

public class GameServer extends Thread{
	private DatagramSocket socket;
	
	private Game core;
	
	ArrayList<Joueur> joueursConnectees = new ArrayList<Joueur>();
	
	public GameServer(Game core) {
		this.core = core;
		try {
			this.socket = new DatagramSocket(5000);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		while (true) {
			byte[] data = new byte[1024];
			
			DatagramPacket packet = new DatagramPacket(data, data.length);
			try {
				socket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
		}
	}
	
	private void parsePacket(byte[] data, InetAddress address, int port) {
		String message = new String(data).trim();
		
		TypesPacket type = Packet.findPacket(message.substring(0, 2));
		Packet packet;
		
		switch (type) {
		case DISCONNECT:
			packet = new Packet01_Disconnect(data);
			System.out.println(((Packet01_Disconnect) packet).getUsername() + " s'est deconnecter(" + address.getHostAddress() + ")");
			
			removeConnection((Packet01_Disconnect) packet);
			break;
		case INVALIDE:
			break;
		case LOGIN:
			packet = new Packet00_Login(data);
			System.out.println("SERVER: " + ((Packet00_Login) packet).getUsername() + " s'est connecter (" + address.getHostAddress() + ")");
			
			Joueur joueur = core.getCurrentRunningGame().instancePlayerAt(new Vector2(100, 100), true);
			joueur.setIpAdress(address);
			joueur.setPort(port);
			joueur.setUsername(((Packet00_Login) packet).getUsername());
			
			addConnection(joueur, (Packet00_Login) packet);
			break;
		case MOVE:
			packet = new Packet02_Movement(data);
//			System.out.println("Player " + ((Packet02_Movement) packet).getUsername() + " moved to " + ((Packet02_Movement) packet).getPosition());
			this.handleMove((Packet02_Movement) packet);
			break;
		case PLAYER_DETAILS:
			packet = new Packet03_PlayerDetails(data);
			this.handlePlayerDetails((Packet03_PlayerDetails) packet);
			
			break;
		case BULLET_SPAWN:
			packet = new Packet04_BulletSpawn(data);
			
			packet.writeData(this);
			break;
		case CHANGE_WEAPON:
			packet = new Packet05_WeaponSwitch(data);
			
			handleWeaponChange((Packet05_WeaponSwitch) packet);
			break;
		case DIED:
			packet = new Packet06_Died(data);
			
			int player_index = getJoueurIndex(((Packet06_Died) packet).getUsername());
			joueursConnectees.get(player_index).setAlive(false);
			
			packet.writeData(this);
			break;
		case ALIVE:
			packet = new Packet13_Alive(data);
			
			player_index = getJoueurIndex(((Packet13_Alive) packet).getUsername());
			packet.writeData(this);
			
			break;
		case START_GAME:
			packet = new Packet07_StartGame(data);
			
			startGame((Packet07_StartGame) packet);
			break;
		case TAKE_DAMAGE:
			packet = new Packet10_TakeDamage(data);
			
			dealWithDamage((Packet10_TakeDamage) packet);
			
			break;
		case DESTROY_BULLET:
			packet = new Packet11_DestroyBullet(data);
			
			packet.writeData(this);
			
			break;
		case CHANGE_SCENE:
			packet = new Packet12_ChangeScene(data);
			
			packet.writeData(this);
			break;
		case DISCONNECT_SERVER:
			packet = new Packet14_DisconnectServer();
			
			packet.writeData(this);
		default:
			break;
			
		}
	}

	private void dealWithDamage(Packet10_TakeDamage packet) {
		if (getJoueurIn(packet.getUsername()) != null) {
			int joueurIndex = getJoueurIndex(packet.getUsername());
			
			this.joueursConnectees.get(joueurIndex).setHp(core, packet.getHp());
			
			packet.writeData(this);
		}
	}

	private void startGame(Packet07_StartGame packet) {
		if (getJoueurIn(packet.getUsername()) != null) {
			int joueurIndex = getJoueurIndex(packet.getUsername());
			
			this.joueursConnectees.get(joueurIndex).setEquipGun(core, true);
			
			packet.writeData(this);
		}
	}

	private void handleWeaponChange(Packet05_WeaponSwitch packet) {
		if (getJoueurIn(packet.getUsername()) != null) {
			int joueurIndex = getJoueurIndex(packet.getUsername());
			try {
				this.joueursConnectees.get(joueurIndex).setGun((Gun) packet.getCurrentGun().getConstructor().newInstance(), core);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
			
			packet.writeData(this);
		}
	}

	private void handlePlayerDetails(Packet03_PlayerDetails packet) {
		if (getJoueurIn(packet.getUsername()) != null) {
			int joueurIndex = getJoueurIndex(packet.getUsername());
			this.joueursConnectees.get(joueurIndex).setPuppetGunRotation(packet.getRotationDegrees());
			this.joueursConnectees.get(joueurIndex).setPuppetGunFlipped(packet.isGunFlipped());
			this.joueursConnectees.get(joueurIndex).setPuppetGunPosition(packet.getGunPosition());
			packet.writeData(this);
		}
	}

	public void handleMove(Packet02_Movement packet) {
		if (getJoueurIn(packet.getUsername()) != null) {
			int joueurIndex = getJoueurIndex(packet.getUsername());
			this.joueursConnectees.get(joueurIndex).setPosition(packet.getPosition());
			packet.writeData(this);
		}
	}

	public void addConnection(Joueur joueur, Packet00_Login packet) {
		boolean alreadyConnected = false;
		
		Packet00_Login temp = packet;
		for (Joueur joueur_ : joueursConnectees) {
			packet = temp;
			if (joueur.getUsername().equalsIgnoreCase(joueur_.getUsername())) {
				if (joueur_.getIpAdress() == null) {
					joueur_.setIpAdress(joueur.getIpAdress());
				}
				
				if (joueur_.getPort() == -1) {
					joueur_.setPort(joueur.getPort());
				}
				
				alreadyConnected = true;
			} else {
				sendData(packet.getData(), joueur_.getIpAdress(), joueur_.getPort());
				
				packet = new Packet00_Login(joueur_.getUsername());
				sendData(packet.getData(), joueur.getIpAdress(), joueur.getPort());
			}
		}
		
		if (!alreadyConnected) {
			this.joueursConnectees.add(joueur);
		}
	}

	public void removeConnection(Packet01_Disconnect packet) {
		this.joueursConnectees.remove(getJoueurIndex(packet.getUsername()));		
		packet.writeData(this);
	}
	
	public Joueur getJoueurIn(String username) {
		for (int i = 0; i < joueursConnectees.size(); i++) {
			if (joueursConnectees.get(i).getUsername().equals(username)) {
				return joueursConnectees.get(i);
			}
		}
		
		return null;
	}
	
	public int getJoueurIndex(String username) {
		for (int i = 0; i < joueursConnectees.size(); i++) {
			if (joueursConnectees.get(i).getUsername().equals(username)) {
				return i;
			}
		}
		
		return -1;
	}

	public void sendData(byte[] data, InetAddress ipAddress, int port) {
		DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendDataToAll(byte[] data) {
		for (Joueur joueur : joueursConnectees) {
			sendData(data, joueur.getIpAdress(), joueur.getPort());
		}
	}
}
