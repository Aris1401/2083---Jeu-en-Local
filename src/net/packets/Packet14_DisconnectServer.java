package net.packets;

import net.GameClient;
import net.GameServer;

public class Packet14_DisconnectServer extends Packet{
	
	public Packet14_DisconnectServer() {
		super(14);
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
		return ("14").getBytes();
	}

}
