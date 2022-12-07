package gameObjects.guns;

import core.Game;
import gameObjects.guns.GunTypes.gunTypes;

public class Shotgun extends Gun{
	public Shotgun() {
		super();
	}
	
	public Shotgun(Game core) {
		super(core);
		
		super.setGunPropreties(15f, 5, gunTypes.primary, "Shotgun.png", 3f, 100f);
		super.setBulletSize(10f);
		
		setActive(false);
	}

}
