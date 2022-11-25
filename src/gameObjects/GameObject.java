package gameObjects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

import javax.imageio.ImageIO;

import engineClasses.Vector2;

public class GameObject {
	//Transform
	protected Rectangle2D.Float transform;
	protected Vector2 position;
	
	//Propreties
	String id = "Object";
	boolean isActive = true;
	protected boolean collsions = true;
	
	protected Color material = new Color(0, 0, 0);
	
	//Sprite
	BufferedImage sprite;
	
	// Rotations
	float rotationDegree = 0;
	
	// Multiplayer Propreties (optional)
	InetAddress ipAdress;
	int port;
	
	boolean isNetworkMaster = false;
	
	public GameObject() {
		position = new Vector2();
		transform = new Rectangle2D.Float(position.x, position.y, 50, 50);
	}
	
	//Transform propreties
	public void translate(Vector2 translation) {
		position.x += translation.x;
		position.y += translation.y;
		
		updateRectPos();
	}
	
	public void setPosition(Vector2 newPos) {
		float x = newPos.x;
		float y = newPos.y;
		position = new Vector2(x, y);
		
		updateRectPos();
	}
	
	void updateRectPos() {
		transform.x = position.x;
		transform.y = position.y;
	}
	
	public Vector2 position() {
		return this.position;
	}
	
	public Point scale() {
		return new Point((int) transform.width, (int) transform.height);
	}
	
	public Rectangle2D.Float getRectange() {
		return this.transform;
	}
	
	//Draw
	public void drawObject(Graphics2D g) {
		g.rotate(rotationDegree);
		g.fillOval((int) position.x, (int) position.y, (int) transform.width, (int) transform.height);
	}
	
	public void setMaterial(Color color) {
		this.material = color;
	}
	
	//Getter settes proprietes
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	public boolean getIsActive() {
		return this.isActive;
	}
	
	//Get sprite
	public void setSprite(String nomSprite) {
		try {
			sprite = ImageIO.read(new File("src/assets/"+nomSprite));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setRotationDegrees(float rotationDegree) {
		this.rotationDegree = rotationDegree;
	}
	
	public float getRotationDegrees() {
		return this.rotationDegree;
	}

	public InetAddress getIpAdress() {
		return ipAdress;
	}

	public void setIpAdress(InetAddress ipAdress) {
		this.ipAdress = ipAdress;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	public void setIsNetworkMaster(boolean isMaster) {
		this.isNetworkMaster = isMaster;
	}
	
	public boolean getIsNetworkMaster() {
		return this.isNetworkMaster;
	}
}
