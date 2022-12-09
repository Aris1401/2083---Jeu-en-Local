package Scenes;

import java.awt.Color;

import core.Game;
import engineClasses.Scene;
import gameObjects.Background;

public class Lobby implements Scene{
	Background bg;
	
	public Lobby(Game core) {
		bg = new Background(1000, 1000, core);
		bg.setMaterial(Color.gray);
		
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
