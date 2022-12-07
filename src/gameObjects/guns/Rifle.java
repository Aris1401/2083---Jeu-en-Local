package gameObjects.guns;

import core.Game;
import gameObjects.guns.GunTypes.gunTypes;

public class Rifle extends Gun{
	public Rifle() {
		super();
	}
	
	public Rifle(Game core) {
		super(core);
				
		super.setGunPropreties(8f, 35, gunTypes.primary, "Rifle.png", 1.2f, 50f);
		
		setActive(false);
	}

}
