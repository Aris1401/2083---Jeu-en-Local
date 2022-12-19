package uiComponents;

import java.awt.Color;

import core.Game;
import gameObjects.GameObject;

public class UIElement extends GameObject {
	Game core;
	
	public UIElement(float width, float height, Game core) {
		this.transform.width = width;
		this.transform.height = height;
		
		core.addNewGameObject(this);
		this.core = core;
		
		this.material = Color.RED;
	}
}
