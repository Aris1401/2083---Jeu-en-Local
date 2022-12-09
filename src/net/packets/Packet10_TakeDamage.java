package net.packets;

import net.GameClient;
import net.GameServer;

public class Packet10_TakeDamage extends Packet{
	
	String username;
	float hp;

	public Packet10_TakeDamage(byte[] data) {
		super(10);
		
		String[] dataArray = readData(data).split(",");
		
		this.username = dataArray[0];
		this.hp = Float.parseFloat(dataArray[1].trim());
	}
	
	public Packet10_TakeDamage(String username, float hp) {
		super(10);
		
		this.username = username;
		this.hp = hp;
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
		return ("10" + username + "," + hp).getBytes();
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public float getHp() {
		return this.hp;
	}

}
