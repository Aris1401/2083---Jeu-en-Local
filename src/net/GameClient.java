package net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import core.Game;
import engineClasses.Vector2;
import gameObjects.Joueur;
import net.packets.Packet;
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
import net.packets.Packet.TypesPacket;

public class GameClient extends Thread{
	private InetAddress ipAddress;
	private DatagramSocket socket;
	
	private Game core;
	
	public GameClient(Game core, String ipAdress) {
		this.core = core;
		try {
			this.socket = new DatagramSocket();
			
			this.ipAddress = InetAddress.getByName(ipAdress);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
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
			System.out.println(((Packet01_Disconnect) packet).getUsername() + " deconnecter (" + address.getHostAddress() + ")");
			core.getCurrentRunningGame().removePlayer(((Packet01_Disconnect) packet).getUsername());
			break;
		case INVALIDE:
			break;
		case LOGIN:
			packet = new Packet00_Login(data);
			System.out.println("CLIENT: " + ((Packet00_Login) packet).getUsername() + " a rejoin ( " + address.getHostAddress() + ")");
			
			Joueur joueur = core.getCurrentRunningGame().instancePlayerAt(new Vector2(100, 100), false);
			joueur.setIpAdress(address);
			joueur.setPort(port);
			joueur.setUsername(((Packet00_Login) packet).getUsername());
			
			core.getCurrentRunningGame().addPlayer(joueur);
			break;
		case MOVE:
			packet = new Packet02_Movement(data);
			handleMove((Packet02_Movement) packet);
			break;
		case PLAYER_DETAILS:
			packet = new Packet03_PlayerDetails(data);
			handlePlayerDetails((Packet03_PlayerDetails) packet);
			break;
		case BULLET_SPAWN:
			packet= new Packet04_BulletSpawn(data);
			
			core.getCurrentRunningGame().instanceBullet(((Packet04_BulletSpawn) packet).getUsername(), ((Packet04_BulletSpawn) packet).getSpawnPoint(), ((Packet04_BulletSpawn) packet).getVelocity(), ((Packet04_BulletSpawn) packet).getBulletSize(), ((Packet04_BulletSpawn) packet).getBulletDamage());
			break;
		case CHANGE_WEAPON:
			packet = new Packet05_WeaponSwitch(data);
			
			handleWeaponChange((Packet05_WeaponSwitch) packet);
			
			break;
		case DIED:
			packet = new Packet06_Died(data);
			System.out.println("Dead: " + ((Packet06_Died) packet).getUsername() + " | " + address.getHostAddress());
			core.getCurrentRunningGame().isDead(((Packet06_Died) packet).getUsername());
			
			break;
		case ALIVE:
			packet = new Packet13_Alive(data);
			
			core.getCurrentRunningGame().isAlive(((Packet13_Alive) packet).getUsername());
			
			break;
		case START_GAME:
			packet = new Packet07_StartGame(data);
			
			startGame((Packet07_StartGame) packet);
			break;
		case TAKE_DAMAGE:
			System.out.println(new String(data));
			packet = new Packet10_TakeDamage(data);
			
			dealWithDamage((Packet10_TakeDamage) packet);
			
			break;
		case DESTROY_BULLET:
			packet = new Packet11_DestroyBullet(data);
			
			core.getCurrentRunningGame().removeBulletFromScene(((Packet11_DestroyBullet) packet).getId(), ((Packet11_DestroyBullet) packet).getIndexOnScene());
			break;
		case CHANGE_SCENE:
			packet = new Packet12_ChangeScene(data);
			
			core.getCurrentRunningGame().changeScene(((Packet12_ChangeScene) packet).getSceneName());
			break;
		case DISCONNECT_SERVER:
			packet = new Packet14_DisconnectServer();
			
			core.getCurrentRunningGame().disonnectedFromServer();
		default:
			break;
			
		}
	}
	
	private void dealWithDamage(Packet10_TakeDamage packet) {
		core.getCurrentRunningGame().setPlayerHp(packet.getUsername(), packet.getHp());
	}

	private void startGame(Packet07_StartGame packet) {
		core.getCurrentRunningGame().setPlayerEquip(packet.getUsername(), true);
	}

	private void handleWeaponChange(Packet05_WeaponSwitch packet) {
		core.getCurrentRunningGame().setPlayerGun(packet.getUsername(), packet.getCurrentGun());
	}

	private void handlePlayerDetails(Packet03_PlayerDetails packet) {
		core.getCurrentRunningGame().manageGun(packet.getUsername(), packet.getRotationDegrees(), packet.isGunFlipped(), packet.getGunPosition());
	}

	private void handleMove(Packet02_Movement packet) {
		core.getCurrentRunningGame().moveJoueur(packet.getUsername(), packet.getPosition());
	}

	public void sendData(byte[] data) {
		DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, 5000);
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
