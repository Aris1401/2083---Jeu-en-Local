package gameObjects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import core.Game;
import engineClasses.Vector2;

public class Gizmo extends GameObject{
	Vector2 startPos = new Vector2();
	Vector2 endPos = new Vector2();
	
	public Gizmo(Game core) {
		this.transform.height = 3;
		
		core.addNewGameObject(this);
		
		this.material = Color.RED;
	}
	
	public void drawGizmoLine(Vector2 pos1, Vector2 pos2) {
		this.startPos = pos1;
		this.endPos = pos2;
	}
	
	@Override
	public void drawObject(Graphics2D g, Camera camera) {
		AffineTransform oldTrans = g.getTransform();
		
		g.rotate(getRotationDegrees(), startPos.x, startPos.y);
		g.setColor(material);
		g.setStroke(new BasicStroke(1));
		g.drawLine((int) startPos.x, (int) startPos.y, (int) endPos.x, (int) endPos.y);
		
		g.setTransform(oldTrans);
	}
}
