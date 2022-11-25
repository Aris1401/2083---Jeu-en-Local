package gameObjects;

import java.awt.Color;
import java.awt.Graphics2D;

import core.Game;

public class Bounds extends GameObject{
	int shapeType = 0;
	
	public Bounds(float width, float height, Game core) {
		this.transform.width = width;
		this.transform.height = height;
		
		core.addNewGameObject(this);
		
		this.material = Color.RED;
	}
	
	public void setShape(String shape) {
		switch (shape) {
		case "Oval":
			shapeType = 1;
			break;
		default:
			shapeType = 0;
			break;
		}
	}
	
	@Override
	public void drawObject(Graphics2D g) {
		g.setColor(material);
		
		if (shapeType == 0)
			g.fillRect((int) this.position().x, (int) this.position().y, this.scale().x, this.scale().y);
		else if (shapeType == 1)
			g.fillOval((int) this.position().x, (int) this.position().y, this.scale().x, this.scale().y);
	}
}
