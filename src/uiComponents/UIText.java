package uiComponents;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import core.Game;
import gameObjects.GameObject;

public class UIText extends UIElement{
	String text = "Votre texte ici";
	Font textFont;
	
	public UIText(int tailleTexte, Game core) {
		super(tailleTexte, tailleTexte, core);
		
		this.material = Color.WHITE;
		this.collsions = false;
		
		textFont = new Font("Open sans", Font.BOLD, tailleTexte);
	}
	
	public void setFontPropreties(Font font) {
		this.textFont = font;
	}
	
	public void setTailleTexte(int taille){
		 setFontPropreties(new Font("Open sans", Font.BOLD, taille));
	}
	public void setText(String text) {
		this.text = text;
	}
	
	@Override
	public void drawObject(Graphics2D g) {
		g.setColor(material);
		g.setFont(textFont);
		g.drawString(text, this.position().x, (int) this.position().y);
	}
}
