package Scenes;

import java.awt.Color;

import core.Game;
import engineClasses.Scene;
import gameObjects.Background;

public class MainGame implements Scene{
	Background bg;
	
	public MainGame(Game core) {
		bg = new Background(1000, 1000, core);
		bg.setMaterial(new Color(43, 198, 89));
		
		unload(core);
	}
	
	@Override
	public void update(Game core) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(Game core) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void load(Game core) {
		bg.setActive(true);
	}

	@Override
	public void unload(Game core) {
		bg.setActive(false);
	}
}
