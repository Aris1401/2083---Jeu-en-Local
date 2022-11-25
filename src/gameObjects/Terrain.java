package gameObjects;

import java.awt.Color;
import java.awt.Graphics2D;

import core.Game;

public class Terrain extends GameObject{
	public Terrain(float width, float height, Game core) {
		this.transform.width = width;
		this.transform.height = height;
		
		core.addNewGameObject(this);
		
		this.material = Color.DARK_GRAY;
		
		this.setSprite("Terrain.png");
	}
	
	@Override
	public void drawObject(Graphics2D g) {
		g.setColor(material);
		g.drawImage(sprite, (int) this.position.x, (int) this.position.y, this.scale().x, this.scale().y,null);
	}
}
