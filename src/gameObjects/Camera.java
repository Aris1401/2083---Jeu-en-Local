package gameObjects;

import core.Game;
import engineClasses.Vector2;

public class Camera {
	Vector2 position;
	
	Game core;
	
	public Camera(Vector2 position, Game core) {
		this.position = position;
		this.core = core;
	}
	
	public boolean isCaptured(GameObject gameobject) {
		return gameobject.position().x <= (position.x + core.getViewportRect().x) && (gameobject.position().x + gameobject.scale().x) >= position.x && gameobject.position().y <= (position.y + core.getViewportRect().y) && (gameobject.position().y + gameobject.scale().y) >= position.y;
	}

	public Vector2 getPosition() {
		return position;
	}

	public void setPosition(Vector2 position) {
		this.position = position;
	}
	
	public void target(GameObject object) {
		this.setPosition(object.position.sub(new Vector2((core.getViewportRect().x / 2) - object.scale().x, (core.getViewportRect().y / 2) - object.scale().y)));
	}

	public Game getCore() {
		return core;
	}

	public void setCore(Game core) {
		this.core = core;
	}
}
