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
		
		setCurrentPower(maxPower);
	}
	
	public void setCurrentPower(float power) {
		currentPower = (scale().x * power) / maxPower;
	}
	
	@Override
	public void drawObject(Graphics2D g, Camera camera) {
		g.setColor(Color.RED);
		g.fillRect((int) position.x, (int) position.y, scale().x, scale().y);
		g.setColor(Color.GRAY);
		g.fillRect((int) position.x, (int) position.y,(int) (scale().x - currentPower), (int) (scale().y ));
	}
}
