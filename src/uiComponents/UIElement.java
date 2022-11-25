package uiComponents;

import java.awt.Color;
import java.awt.Graphics2D;

import core.Game;
import gameObjects.GameObject;

public class UIElement extends GameObject {
	public UIElement(float width, float height, Game core) {
		this.transform.width = width;
		this.transform.height = height;
		
		core.addNewGameObject(this);
		
		this.material = Color.RED;
	}
}
