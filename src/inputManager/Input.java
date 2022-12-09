package inputManager;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import core.Game;
import engineClasses.Vector2;

public class Input implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener{
	private Game gc;
	
	private final int NUM_KEYS = 256;
	private boolean[] keys = new boolean[NUM_KEYS];
	private boolean[] keysLast = new boolean[NUM_KEYS];
	
	private final int NUM_BUTTON = 5;
	private boolean[] buttons = new boolean[NUM_BUTTON];
	private boolean[] buttonsLast = new boolean[NUM_BUTTON];
	
	private int mouseX, mouseY;
	private int scroll;
	
	public Input(Game gc) {
		this.gc = gc;
		
		mouseX = 0;
		mouseY = 0;
		scroll = 0;
		
		gc.getGameRenderer().addKeyListener(this);
		gc.getGameRenderer().addMouseListener(this);
		gc.getGameRenderer().addMouseMotionListener(this);
		gc.getGameRenderer().addMouseWheelListener(this);
	}
	
	public void update() {
		scroll = 0;
		
		for (int i = 0; i < NUM_KEYS; i++) {
			keysLast[i] = keys[i];
		}
		
		for (int i = 0; i < NUM_BUTTON; i++) {
			buttonsLast[i] = buttons[i];
		}
	}
	
	public boolean isKey(int keyCode) {
		return keys[keyCode];
	}
	
	public boolean isKeyUp(int keyCode) {
		return !keys[keyCode] && keysLast[keyCode];
	}
	
	public boolean isKeyDown(int keyCode) {
		return keys[keyCode] && !keysLast[keyCode];
	}
	
	
	public boolean isButton(int buttonCode) {
		return buttons[buttonCode];
	}
	
	public boolean isButtonUp(int buttonCode) {
		return !buttons[buttonCode] && buttonsLast[buttonCode];
	}
	
	public boolean isButtonDown(int buttonCode) {
		return buttons[buttonCode] && !buttonsLast[buttonCode];
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		Vector2 offset = new Vector2();
		
		if (gc.getCurrentCamera() != null) {
			offset = gc.getCurrentCamera().getPosition().clone();
		}
		
		mouseX = (int) ((e.getX() + offset.x / gc.getScale()));
		mouseY = (int) ((e.getY() + offset.y / gc.getScale()));
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		Vector2 offset = new Vector2();
		
		if (gc.getCurrentCamera() != null) {
			offset = gc.getCurrentCamera().getPosition().clone();
		}
		
		mouseX = (int) ((e.getX() + offset.x / gc.getScale()));
		mouseY = (int) ((e.getY() + offset.y / gc.getScale()));
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		buttons[e.getButton()] = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		buttons[e.getButton()] = false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		keys[e.getKeyCode()] = true;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keys[e.getKeyCode()] = false;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		scroll = e.getWheelRotation();
	}

	public int getMouseX() {
		return mouseX;
	}

	public int getMouseY() {
		return mouseY;
	}

	public int getScroll() {
		return scroll;
	}
}
