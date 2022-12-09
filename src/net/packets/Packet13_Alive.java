package net.packets;

import net.GameClient;
import net.GameServer;

public class Packet13_Alive extends Packet{
	
	String username;
	
	public Packet13_Alive(byte[] data) {
		super(13);
		
		this.username = this.readData(data);
	}
	
	public Packet13_Alive(String username) {
		super(13);
		
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
		return ("13" + username).getBytes();
	}
	
	public String getUsername() {
		return this.username;
	}

}
