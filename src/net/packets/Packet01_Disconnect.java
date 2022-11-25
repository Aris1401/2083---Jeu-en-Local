package net.packets;

import net.GameClient;
import net.GameServer;

public class Packet01_Disconnect extends Packet{
	
	String username;
	
	public Packet01_Disconnect(byte[] data) {
		super(01);
		
		this.username = this.readData(data);
	}
	
	public Packet01_Disconnect(String username) {
		super(01);
		
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
		return ("01" + username).getBytes();
	}
	
	public String getUsername() {
		return this.username;
	}

}
