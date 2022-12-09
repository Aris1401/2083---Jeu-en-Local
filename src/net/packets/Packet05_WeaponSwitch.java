package net.packets;

import java.util.ArrayList;

import engineClasses.Vector2;
import gameObjects.guns.Desert_Eagle;
import gameObjects.guns.Gun;
import gameObjects.guns.Rifle;
import gameObjects.guns.Shotgun;
import net.GameClient;
import net.GameServer;

public class Packet05_WeaponSwitch extends Packet{
	
	String username;
	String gunName;
	
	public Packet05_WeaponSwitch(byte[] data) {
		super(05);
		String[] dataArray = readData(data).split(",");
		this.username = dataArray[0];
		
		this.gunName = dataArray[1];
	}
	
	public Packet05_WeaponSwitch(String username, Gun gun) {
		super(05);
		
		this.username = username;
		this.gunName = gun.getClass().getSimpleName();
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
		return ("05" + username + "," + gunName).getBytes();
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Class<?> getCurrentGun() {
		ArrayList<Class<?>> availableGuns = new ArrayList<Class<?>>();
		availableGuns.add(Shotgun.class);
		availableGuns.add(Desert_Eagle.class);
		availableGuns.add(Rifle.class);
		
		for (int i = 0; i < availableGuns.size(); i++) {
			if (availableGuns.get(i).getSimpleName().equalsIgnoreCase(gunName)) return availableGuns.get(i);
		}
		
		return null;
	}
}
