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
import net.packets.Packet01_Disconnect;
import net.packets.Packet02_Movement;
import net.packets.Packet03_PlayerDetails;
import net.packets.Packet04_BulletSpawn;
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
			System.out.println(((Packet00_Login) packet).getUsername() + " a rejoin connecter ( " + address.getHostAddress() + ")");
			
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
			
			core.getCurrentRunningGame().instanceBullet(((Packet04_BulletSpawn) packet).getSpawnPoint(), ((Packet04_BulletSpawn) packet).getVelocity());
			break;
		default:
			break;
			
		}
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
