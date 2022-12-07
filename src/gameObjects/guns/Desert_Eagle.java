package gameObjects.guns;

import core.Game;
import gameObjects.guns.GunTypes.gunTypes;

public class Desert_Eagle extends Gun{
	public Desert_Eagle() {
		super();
	}

	public Desert_Eagle(Game core) {
		super(core);
		
		super.setGunPropreties(20f, 15, gunTypes.secondary, "Desert Eagle.png", 1.5f, 70f);
		
		setActive(false);
	}

}
