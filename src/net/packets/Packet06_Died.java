package net.packets;

import net.GameClient;
import net.GameServer;

public class Packet06_Died extends Packet{
	
	String username;
	
	public Packet06_Died(byte[] data) {
		super(06);
		
		this.username = this.readData(data);
	}
	
	public Packet06_Died(String username) {
		super(06);
		
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
		return ("06" + username).getBytes();
	}
	
	public String getUsername() {
		return this.username;
	}

}
