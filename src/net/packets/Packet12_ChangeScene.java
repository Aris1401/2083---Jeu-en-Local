package net.packets;

import net.GameClient;
import net.GameServer;

public class Packet12_ChangeScene extends Packet{
	
	String sceneName;
	
	public Packet12_ChangeScene(byte[] data) {
		super(12);
		
		this.sceneName = readData(data);
	}
	
	public Packet12_ChangeScene(String sceneName) {
		super(12);
		
		this.sceneName = sceneName;
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
		return ("12" + sceneName).getBytes();
	}
	
	public String getSceneName() {
		return this.sceneName;
	}
}
