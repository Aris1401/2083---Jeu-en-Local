package core;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import gameCore.GamePanel;

public class GameFrame {
	private JFrame gameFrame;
	
	public JFrame getGameFrame() {
		return this.gameFrame;
	}
	
	public GameFrame(GameRenderer gameRenderer, GamePanel gamePanel) {
		initialiserFenetre(gameRenderer, gamePanel);
	}
	
	void initialiserFenetre(GameRenderer gameRenderer, GamePanel gamePanel) {
		//Init frame
		gameFrame = new JFrame();
		
		gameFrame.setTitle("Multiplayer");
		gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameFrame.setLocationRelativeTo(null);
		gameFrame.setResizable(true); // Changer en false
		
		gameFrame.setLayout(new BorderLayout());
		
		gameFrame.add(gamePanel, BorderLayout.NORTH);
		gameFrame.add(gameRenderer, BorderLayout.CENTER);
		
		gameFrame.pack();
		gameFrame.setVisible(true);
	}
}
