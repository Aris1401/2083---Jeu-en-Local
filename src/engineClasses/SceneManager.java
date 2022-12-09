package engineClasses;

import core.Game;

public class SceneManager {
	private static SceneManager instance;
	Scene currentScene;
	Game core;
	
	public void setGameCore(Game core) {
		instance.core = core;
	}
	
	public static SceneManager getInstance() {
		if (instance == null) instance = new SceneManager();
		
		return instance;
	}
	
	public void update() {
		if (currentScene != null)
		currentScene.update(instance.core);
	}
	
	public void render() {
		if (currentScene != null)
		currentScene.render(instance.core);
	}
	
	public static void changeScene(Scene scene) {
		if (instance.currentScene != null) {
			instance.currentScene.unload(instance.core);
		} 
		
		instance.currentScene = scene;
		instance.currentScene.load(instance.core);
	}
}
