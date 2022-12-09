package gameObjects;

import org.joml.Quaterniond;
import org.joml.SimplexNoise;
import org.joml.Vector2d;

import core.Game;
import engineClasses.EngineMath;
import engineClasses.Vector2;

public class Camera {
	Vector2 position;
	Vector2 offset = new Vector2();
	
	Game core;
	
	float RANDOM_SHAKE_STRENGTH = 10.0f;
	float SHAKE_DECAY_RATE = 0.1f;
	float NOISE_SHAKE_SPEED = 30.0f;
	
	Quaterniond test = new Quaterniond();
	
	float shake_strength = 0.0f;
	float noise_i = 0.0f;
	
	public Camera(Vector2 position, Game core) {
		this.position = position;
		this.core = core;
	}
	
	public void update(float delta) {
		shake_strength = (float) EngineMath.Lerp(shake_strength, 0, SHAKE_DECAY_RATE * delta);
		
		offset = getNoiseOffset(delta);
	}
	
	public Vector2 getNoiseOffset(float delta) {
		noise_i += delta * NOISE_SHAKE_SPEED;
		
		return new Vector2(SimplexNoise.noise(1, noise_i) * shake_strength, SimplexNoise.noise(100, noise_i) * shake_strength);
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
	
	public Vector2 getOffset() {
		return this.offset;
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
	
	public void applyShake() {
		shake_strength = RANDOM_SHAKE_STRENGTH;
	}
}
