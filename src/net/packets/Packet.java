package net.packets;

import net.GameClient;
import net.GameServer;

public abstract class Packet {
	public static enum TypesPacket {
		INVALIDE(-1), LOGIN(00), DISCONNECT(01), MOVE(02), PLAYER_DETAILS(03), BULLET_SPAWN(04), CHANGE_WEAPON(05), DIED(06), START_GAME(07), TAKE_DAMAGE(10), DESTROY_BULLET(11), CHANGE_SCENE(12), ALIVE(13);
		
		private int packetId;
		private TypesPacket(int packetId) {
			this.packetId = packetId;
		}
		
		public int getId() {
			return packetId;
		}
	}
	
	public byte packetId;
	
	public Packet(int packetId) {
		this.packetId = (byte) packetId;
	}
	
	public abstract void writeData(GameClient client);
	public abstract void writeData(GameServer server);
	public abstract byte[] getData();
	
	public String readData(byte[] data) {
		String message = new String(data).trim();
		
		return message.substring(2);
	}
	
	public static TypesPacket findPacket(String packetId) {
		try {
			return findPacket(Integer.parseInt(packetId));
		} catch (NumberFormatException e) {
			return TypesPacket.INVALIDE;
		}
	}
	
	public static TypesPacket findPacket(int packetId) {
		for (TypesPacket type : TypesPacket.values()) {
			if (type.getId() == packetId) return type;
		}
		
		return TypesPacket.INVALIDE;
	}
}
