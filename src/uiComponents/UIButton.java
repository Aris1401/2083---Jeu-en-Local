package uiComponents;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Vector;

import core.Game;
import engineClasses.Vector2;

public class UIButton extends UIElement{
	String text = " ";
	Vector2 textOffset = new Vector2(-17, 5);
	
	public UIButton(float width, float height, Game core) {
		super(width, height, core);
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public void translateTextOffset(Vector2 offset) {
		this.textOffset = this.textOffset.add(offset);
	}
	
	@Override
	public void drawObject(Graphics2D g) {
		g.setColor(Color.BLACK);
		g.fillRect((int) this.position().x, (int) this.position().y, this.scale().x, this.scale().y);
		
		g.setColor(Color.white);
		g.setFont(new Font("Open sans", Font.BOLD, 15));
		g.drawString(text, this.position().x + (this.scale().x / 2) + textOffset.x, this.position().y + (this.scale().y / 2) + textOffset.y);
		
		g.setColor(Color.BLACK);
	}
	
	public boolean isButtonHovered(Vector2 mousePos) {
		if ((mousePos.x > this.position.x && mousePos.x < this.position.x + this.scale().x) && (mousePos.y > this.position.y && mousePos.y < this.position.y + this.scale().y)) {
			return true;
		}
		
		return false;
	}

}
