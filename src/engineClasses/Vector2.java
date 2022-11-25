package engineClasses;

public class Vector2 {
	public float x, y;
	public static final Vector2 ZERO = new Vector2();
	
	public Vector2() {
		this.x = 0.0f;
		this.y = 0.0f;
	}
	
	public Vector2(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public static double distance(Vector2 pos1, Vector2 pos2) {
		float x1tox0 = pos2.x - pos1.x;
		float y1toy0 = pos2.y - pos1.y;
		
		return Math.sqrt(x1tox0 * x1tox0 + y1toy0 * y1toy0);
	}
	
	public void normalize() {
		double longeur = Math.sqrt(x*x + y*y);
		
		if (longeur != 0.0f) {
			float normalizer = (float) (1.0f / longeur);
			
			x = x * normalizer;
			y = y * normalizer;
		}
	}
	
	public float length() {
		return (float) Math.sqrt(x*x + y*y);
	}
	
	public Vector2 add(Vector2 v2) {
		return new Vector2(this.x + v2.x, this.y + v2.y);
	}
	
	public Vector2 sub(Vector2 v2) {
		return new Vector2(this.x - v2.x, this.y - v2.y);
	}
	
	public float dot(Vector2 v2) {
		return this.x * v2.x + this.y * v2.y;
	}
	
	public Vector2 multiply(float scalaire) {
		return new Vector2(this.x * scalaire, this.y * scalaire);
	}
	
	public Vector2 linearInterpolate(Vector2 to, float length) {
		return new Vector2((float) EngineMath.Lerp(x, to.x, length), (float) EngineMath.Lerp(y, to.y, length));
	}
	
	public Vector2 rotatePoint(float cx,float cy,float angle)
	{
	  float s = (float) Math.sin(angle);
	  float c = (float) Math.cos(angle);

	  // translate point back to origin:
	  this.x -= cx;
	  this.y -= cy;

	  // rotate point
	  float xnew = this.x * c - this.y * s;
	  float ynew = this.x * s + this.y * c;

	  // translate point back:
	  this.x = xnew + cx;
	  this.y = ynew + cy;
	  return this;
	}
	
	@Override
	public String toString() {
		return this.x + "," + this.y;
	}
}
