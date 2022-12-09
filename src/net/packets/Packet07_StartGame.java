package net.packets;

import net.GameClient;
import net.GameServer;

public class Packet07_StartGame extends Packet{
	
	String username;
	
	public Packet07_StartGame(byte[] data) {
		super(07);
		
		this.username = this.readData(data);
	}
	
	public Packet07_StartGame(String username) {
		super(07);
		
		this.username = username;
	}

	@Override
	public void writeData(GameClient client) {
		client.sendData(getData());
	}

	@Override
	public void writeData(GameServer server) {
		server.sendDataToAll(getData());
	}

	@Override
	public byte[] getData() {
		return ("07" + username).getBytes();
	}
	
	public String getUsername() {
		return this.username;
	}

}
