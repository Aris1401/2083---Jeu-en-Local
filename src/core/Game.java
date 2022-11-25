package core;

import java.awt.Point;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import gameCore.GamePanel;
import gameCore.Shooter;
import gameObjects.GameObject;
import inputManager.Input;
import inputManager.WindowInput;
import net.GameClient;
import net.GameServer;

public class Game implements Runnable{
	GameFrame gameFrame;
	GameRenderer gameRenderer;
	GamePanel gamePanel;
	
	//Updates propreties
	int FPS_CAP = 120;
	int UPDATE_TICK = 70;
	double lastTimeCheck = 0;
	
	//Game thread
	Thread gameThread;
	
	//Game classes
	Shooter billard;
	
	//Input manager
	Input input;
	WindowInput windowInput;
	
	//Gameobject manager
	ArrayList<GameObject> gameObjects;
	
	//Game states
	boolean gameIsRunning = true;
	
	
	// Network propreties
	GameServer server;
	GameClient client;
	
	public Game() {
		initialiserGame();
	}
	
	void startThread() {
		gameThread.start();
	}
	
	void initialiserGame() {
		//Game thread
		gameThread = new Thread(this);
		
		gameThread.setDaemon(true);
				
		//Game objects
		gameObjects = new ArrayList<GameObject>();
		
		//game class propreties
		gameRenderer = new GameRenderer(this);
		gameRenderer.requestFocus();
		gameRenderer.setFocusable(true);
		
		gamePanel = new GamePanel();
		
		gameFrame = new GameFrame(gameRenderer, gamePanel);
		
		gameRenderer.requestFocus();
		gameRenderer.setFocusable(true);

		//game inputs
		input = new Input(this);
		windowInput = new WindowInput(this);
		
		// Network stuff
		if (JOptionPane.showConfirmDialog(gameFrame.getGameFrame(), "Voule vous etre le serveur?") == JOptionPane.YES_OPTION) {
			server = new GameServer(this);
			server.start();
		}
		
		
		client = new GameClient(this, JOptionPane.showInputDialog(gameFrame.getGameFrame(), "Entrez server Ip:"));
		client.start();
		
		//Game classes
		billard = new Shooter(this);
		
		startThread();
	}

	@Override
	public void run() {
		double frameDrawInterval = 1000000000 / FPS_CAP;
		double updateInterval = 1000000000 / UPDATE_TICK;
		double delta = 0;
		double deltaTime = 0;
		
		lastTimeCheck = System.nanoTime();
		long currentTime = 0;
		
		while (true) {
			currentTime = System.nanoTime();
			
			delta += (currentTime - lastTimeCheck) / frameDrawInterval;
			deltaTime += (currentTime - lastTimeCheck) / updateInterval;
			lastTimeCheck = currentTime;
			
			if (deltaTime >= 1) {
				updateGame((float) deltaTime);
				deltaTime--;
			}
			
			if (delta >= 1) {
				gameRenderer.repaint();
				gameRenderer.getToolkit().sync();
				
				delta--;
			}
			
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	void updateGame(float delta) {
		input.update();
		
		if (gameIsRunning)
			this.updatePlaying(delta);
	}
	
	void updatePlaying(float delta) {
		billard.update(delta);
	}
	
	//Gameobject management
	public void addNewGameObject(GameObject gameObject) {
		this.gameObjects.add(gameObject);
	}
	
	public void removeGameObject(GameObject gameObject) {
		this.gameObjects.remove(gameObject);
	}
	
	public ArrayList<GameObject> getGameObjectsOnScene() {
		return this.gameObjects;
	}
	
	//Inputs
	public Input input() {
		return this.input;
	}
	
	//Viewport
	public Point getViewportRect() {
		return new Point(GameRenderer.WIDTH, GameRenderer.HEIGHT);
	}
	
	public boolean getGameIsRunning() {
		return this.gameIsRunning;
	}
	
	public void setGameIsRunning(boolean value) {
		this.gameIsRunning = value;
	}
	
	public GameRenderer getGameRenderer() {
		return this.gameRenderer;
	}
	
	public int getScale() {
		return 1;
	}
	
	public Shooter getCurrentRunningGame() {
		return billard;
	}
	
	public JFrame getGameFrame() {
		return this.gameFrame.getGameFrame();
	}
	
	// Networking
	public GameServer getGameServer() {
		return this.server;
	}
	
	public GameClient getGameClient() {
		return this.client;
	}
} 
