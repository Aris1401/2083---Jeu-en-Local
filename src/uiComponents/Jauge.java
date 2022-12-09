package uiComponents;

import java.awt.Color;
import java.awt.Graphics2D;

import core.Game;
import gameObjects.Camera;

public class Jauge extends UIElement{
	float maxPower = 0;
	float currentPower = 0;
	
	public Jauge(float width, float height, Game core, float maxPower) {
		super(width, height, core);
		
		this.material = Color.RED;
		this.maxPower = maxPower;
	}
	
	public void setCurrentPower(float power) {
		currentPower = (scale().y * power) / maxPower;
	}
	
	@Override
	public void drawObject(Graphics2D g, Camera camera) {
		g.fillRect((int) position.x, (int) position.y, scale().x, scale().y);
		g.setColor(Color.BLUE);
		g.fillRect((int) position.x, (int) position.y, scale().x, (int) (scale().y - currentPower));
	}
}
