package gameObjects.guns;

import java.awt.event.KeyEvent;

import core.Game;
import gameObjects.Joueur;
import gameObjects.guns.GunTypes.gunTypes;

public class WeaponManager {
	Gun primaryGun;
	Gun secondaryGun;
	
	public WeaponManager() {
		
	}
	
	public void getInputs(Game core, Joueur joueur) {
		if (core.input().isKeyDown(KeyEvent.VK_1)) {
			if (joueur.getGun() != null) {
				if (joueur.getGun().getGunType() == gunTypes.primary) return;
			}
			
			if (this.getPrimaryGun() != null) {
				if (joueur.getGun() != null)
					joueur.getGun().setActive(false);
				
				joueur.equipGun(getPrimaryGun(), core);
				joueur.getGun().setActive(true);
			}
		} 
		
		if (core.input().isKeyDown(KeyEvent.VK_2)) {
			if (joueur.getGun() != null) {
				if (joueur.getGun().getGunType() == gunTypes.secondary) return;
			}
			
			if (this.getSecondaryGun() != null) {
				if (joueur.getGun() != null)
					joueur.getGun().setActive(false);
				joueur.equipGun(getSecondaryGun(), core);
				joueur.getGun().setActive(true);
			}
		}
	}
	
	public Gun getPrimaryGun() {
		return this.primaryGun;
	}
	
	public Gun getSecondaryGun() {
		return this.secondaryGun;
	}
	
	public void addGun(Gun gun) {
		if (gun.getGunType() == gunTypes.primary) {
			this.primaryGun = gun;
		} else if (gun.getGunType() == gunTypes.secondary) {
			this.secondaryGun = gun;
		}
	}
}
