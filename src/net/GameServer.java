package net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

import core.Game;
import engineClasses.Vector2;
import gameObjects.Joueur;
import net.packets.Packet;
import net.packets.Packet.TypesPacket;
import net.packets.Packet00_Login;
import net.packets.Packet01_Disconnect;
import net.packets.Packet02_Movement;
import net.packets.Packet03_PlayerDetails;
import net.packets.Packet04_BulletSpawn;

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
			System.out.println(((Packet00_Login) packet).getUsername() + " s'est connecter (" + address.getHostAddress() + ")");
			
			Joueur joueur = core.getCurrentRunningGame().instancePlayerAt(new Vector2(100, 100), true);
			joueur.setIpAdress(address);
			joueur.setPort(port);
			joueur.setUsername(((Packet00_Login) packet).getUsername());
			
			addConnection(joueur, (Packet00_Login) packet);
			break;
		case MOVE:
			packet = new Packet02_Movement(data);
			System.out.println("Player " + ((Packet02_Movement) packet).getUsername() + " moved to " + ((Packet02_Movement) packet).getPosition());
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
		default:
			break;
			
		}
	}

	private void handlePlayerDetails(Packet03_PlayerDetails packet) {
		if (getJoueurIn(packet.getUsername()) != null) {
			int joueurIndex = getJoueurIndex(packet.getUsername());
			this.joueursConnectees.get(joueurIndex).setPuppetGunRotation(packet.getRotationDegrees());
			this.joueursConnectees.get(joueurIndex).setPuppetGunFlipped(packet.isGunFlipped());
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
		
		for (Joueur joueur_ : joueursConnectees) {
			if (joueur_.getUsername().equalsIgnoreCase(joueur.getUsername())) {
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
