package net.packets;

import net.GameClient;
import net.GameServer;

public class Packet11_DestroyBullet extends Packet{
	
	String id;
	int indexOnScene;
	
	public Packet11_DestroyBullet(byte[] data) {
		super(11);
		
		String[] dataArray = readData(data).split(",");
		this.id = dataArray[0];
		this.indexOnScene = Integer.parseInt(dataArray[1].trim());
	}
	
	public Packet11_DestroyBullet(String id, int indexOnScene) {
		super(11);
		
		this.id = id;
		this.indexOnScene = indexOnScene;
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
		return ("11" + id + "," + indexOnScene).getBytes();
	}
	
	public String getId() {
		return this.id;
	}

	public int getIndexOnScene() {
		return this.indexOnScene;
	}
}
