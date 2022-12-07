package gameObjects;

import java.awt.Color;
import java.awt.Graphics2D;

import core.Game;

public class Position2D extends GameObject{
	public Position2D(float width, float height, Game core) {
		this.transform.width = width;
		this.transform.height = height;
		
		core.addNewGameObject(this);
		
		this.material = Color.DARK_GRAY;
	}
	
	@Override
	public void drawObject(Graphics2D g, Camera camera) {
		g.setColor(material);
		g.fillRect((int) this.position().x, (int) this.position().y, this.scale().x, this.scale().y);
	}
}
