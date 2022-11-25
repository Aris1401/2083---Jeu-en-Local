package gameObjects;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import audio.SoundClip;
import core.Game;
import engineClasses.Vector2;

public class Gun extends GameObject{
	Vector2 rotationOrigin = new Vector2();
	
	boolean isFlipped = false;
	
	public Gun(Game core) {
		this.transform.height = 3;
		
		core.addNewGameObject(this);
	}
	
	public void setRotationOrigin(float x, float y) {
		rotationOrigin = new Vector2(x, y);
	}
	
	public void flipImage(boolean flipped) {
		isFlipped = flipped;
	}
	
	public boolean isGunFlipped() {
		return isFlipped;
	}
	
	public void fire() {
		SoundClip clip = new SoundClip("/Rifle Sound.wav");
		clip.play();
	}
	
	@Override
	public void drawObject(Graphics2D g) {
		AffineTransform oldTrans = g.getTransform();
		
		g.rotate(rotationDegree, rotationOrigin.x + (scale().x / 2), rotationOrigin.y + (scale().y));
		
		if (!isFlipped) 
			g.drawImage(sprite, (int) this.position().x, (int) this.position().y, 64, 64, null);
		else
			g.drawImage(sprite, (int) this.position().x, (int) this.position().y + 64, 64, -64, null);
		
		g.setTransform(oldTrans);
	}
}
