package inputManager;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import core.Game;
import net.packets.Packet01_Disconnect;
import net.packets.Packet14_DisconnectServer;

public class WindowInput implements WindowListener{
	
	Game core;
	
	public WindowInput(Game core) {
		this.core = core;
		
		this.core.getGameFrame().addWindowListener(this);
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		if (core.isServer()) {
			Packet14_DisconnectServer packet = new Packet14_DisconnectServer();
			packet.writeData(core.getGameClient());
		}
		
		Packet01_Disconnect packet = new Packet01_Disconnect(core.getCurrentRunningGame().getCurrentJoueur().getUsername());
		packet.writeData(core.getGameClient());
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

}
