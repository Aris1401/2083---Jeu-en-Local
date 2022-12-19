package gameObjects.guns;

public class GunTypes {
	public enum gunTypes{
		primary("primary"), secondary("secondary");
		
		public String type;
		gunTypes(String type_) {
			type = type_;
		}
		
		public String getType() {
			return type;
		}
	}
}
