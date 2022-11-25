package net.packets;

import net.GameClient;
import net.GameServer;

public class Packet00_Login extends Packet{
	
	String username;
	
	public Packet00_Login(byte[] data) {
		super(00);
		
		this.username = this.readData(data);
	}
	
	public Packet00_Login(String username) {
		super(00);
		
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
		return ("00" + username).getBytes();
	}
	
	public String getUsername() {
		return this.username;
	}

}
