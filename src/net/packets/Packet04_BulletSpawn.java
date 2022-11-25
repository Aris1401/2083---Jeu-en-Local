package net.packets;

import engineClasses.Vector2;
import net.GameClient;
import net.GameServer;

public class Packet04_BulletSpawn extends Packet{
	
	String username;
	private Vector2 velocity;
	private Vector2 spawnPoint;
	
	public Packet04_BulletSpawn(byte[] data) {
		super(04);
		String[] dataArray = readData(data).split(",");
		this.username = dataArray[0];
		
		this.velocity = new Vector2(Float.parseFloat(dataArray[1]), Float.parseFloat(dataArray[2]));
		this.spawnPoint = new Vector2(Float.parseFloat(dataArray[3]), Float.parseFloat(dataArray[4]));
	}
	
	public Packet04_BulletSpawn(String username, Vector2 velocity, Vector2 spawnPoint) {
		super(04);
		
		this.username = username;
		this.velocity = velocity;
		this.spawnPoint = spawnPoint;
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
		return ("04" + username + "," + velocity.toString() + "," + spawnPoint.toString()).getBytes();
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public Vector2 getVelocity() {
		return this.velocity;
	}
	
	public Vector2 getSpawnPoint() {
		return this.spawnPoint;
	}
}
