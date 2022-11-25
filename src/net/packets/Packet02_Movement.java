package net.packets;

import engineClasses.Vector2;
import net.GameClient;
import net.GameServer;

public class Packet02_Movement extends Packet{
	
	String username;
	private Vector2 position;
	
	public Packet02_Movement(byte[] data) {
		super(02);
		String[] dataArray = readData(data).split(",");
		this.username = dataArray[0];
		
		this.position = new Vector2(Float.parseFloat(dataArray[1]), Float.parseFloat(dataArray[2]));
	}
	
	public Packet02_Movement(String username, Vector2 position) {
		super(02);
		
		this.username = username;
		this.position = position;
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
		return ("02" + username + "," + position.toString()).getBytes();
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public Vector2 getPosition() {
		return this.position;
	}
}
