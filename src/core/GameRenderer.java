package core;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;

import javax.swing.JPanel;

import gameObjects.GameObject;
import gameObjects.Joueur;

public class GameRenderer extends JPanel{
	private static final long serialVersionUID = 1L;
	//Gameobjects 
	ArrayList<GameObject> gameObjects;
	Game core;
	
	static int WIDTH = 720;
	static int HEIGHT = 400;
	
	public GameRenderer(Game game) {
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		
		this.core = game;
		this.gameObjects = core.getGameObjectsOnScene();
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		ArrayList<GameObject> topObjects = new ArrayList<GameObject>();
		
		if (gameObjects != null) {
			for (int i = 0; i < gameObjects.size(); i++) {
				if (gameObjects.get(i) instanceof Joueur) {
					topObjects.add(gameObjects.get(i));
					
					continue;
				}
				if (gameObjects.get(i).getIsActive()) {
					gameObjects.get(i).drawObject(g2, core.getCurrentCamera());	
					
				}
			}
		}
		
		for (int i = 0; i < topObjects.size(); i++) {
			if (topObjects.get(i).getIsActive()) {
				topObjects.get(i).drawObject(g2, core.getCurrentCamera());
			}
		}
	}
	
	public void render() {
		this.gameObjects = core.getGameObjectsOnScene();
		
		this.repaint();
	}
}
