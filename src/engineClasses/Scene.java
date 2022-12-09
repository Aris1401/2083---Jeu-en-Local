package engineClasses;

import core.Game;

public interface Scene {
	abstract void update(Game core);
	abstract void render(Game core);
	abstract void load(Game core);
	abstract void unload(Game core);
}
