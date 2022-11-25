package uiComponents;

import java.awt.Color;
import java.awt.Graphics2D;

import core.Game;

public class Filter extends UIElement{
	public Filter(float width, float height, Game core) {
		super(width, height, core);
		
		this.setMaterial(new Color(0f, 0f, 0f, .7f));
	}
	
	@Override
	public void drawObject(Graphics2D g) {
		g.setColor(material);
		g.fillRect((int) this.position().x, (int) this.position().y, this.scale().x, this.scale().y);
		
		g.setColor(Color.BLACK);
	}
	
}
