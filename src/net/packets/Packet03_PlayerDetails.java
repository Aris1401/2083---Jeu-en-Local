package net.packets;

import engineClasses.Vector2;
import net.GameClient;
import net.GameServer;

public class Packet03_PlayerDetails extends Packet{
	
	String username;
	private float gunRotationDegrees;
	private Vector2 gunPosition;
	private boolean isGunFlipped;
	
	public Packet03_PlayerDetails(byte[] data) {
		super(03);
		String[] dataArray = readData(data).split(",");
		this.username = dataArray[0];
		
		this.gunRotationDegrees = Float.parseFloat(dataArray[1]);
		
		this.isGunFlipped = true;
		if (dataArray[2].equals("false")) {
			this.isGunFlipped = false;
		} 
		
		this.gunPosition = new Vector2(Float.parseFloat(dataArray[3]), Float.parseFloat(dataArray[4]));
	}
	
	public Packet03_PlayerDetails(String username, float gunRotation, boolean isGunFlipped, Vector2 gunPosition) {
		super(03);
		
		this.username = username;
		this.gunRotationDegrees = gunRotation;
		this.isGunFlipped = isGunFlipped;
		this.gunPosition = gunPosition;
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
		return ("03" + username + "," + gunRotationDegrees + "," + Boolean.toString(isGunFlipped) + "," + this.gunPosition).getBytes();
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public float getRotationDegrees() {
		return this.gunRotationDegrees;
	}
	
	public boolean isGunFlipped() {
		return this.isGunFlipped;
	}
	
	public Vector2 getGunPosition() {
		return this.gunPosition;
	}
}
