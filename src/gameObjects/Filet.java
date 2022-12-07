package gameObjects;

import java.awt.Color;
import java.awt.Graphics2D;

import core.Game;

public class Filet extends GameObject{
	//Proprietes filet
	float hauteurFilet = 0.5f;
	
	public Filet(float width, float height, Game core) {
		this.transform.width = width;
		this.transform.height = height;
		
		core.addNewGameObject(this);
		
		this.material = Color.DARK_GRAY;
	}
	
	public float getHauteurFilet() {
		return this.hauteurFilet;
	}
	
	public void setHauteurFilet(float hauteurFilet) {
		this.hauteurFilet = hauteurFilet;
	}
	
	@Override
	public void drawObject(Graphics2D g, Camera camera) {
		g.setColor(material);
		g.fillRect((int) this.position().x, (int) this.position().y, this.scale().x, this.scale().y);
	}
}
