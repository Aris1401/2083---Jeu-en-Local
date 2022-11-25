package gameCore;

import uiComponents.UIText;

public class ScoreManager {
	UIText scores[];
	GamePanel gamePanel;
	
	public ScoreManager(UIText scores[], GamePanel gamePanel) {
		this.scores = scores;
		this.gamePanel = gamePanel;
	}
	
	void addScores(int camp) {
		gamePanel.setScore(camp, gamePanel.getScore(camp) + 1);
		
		updateDisplayScore(camp);
	}
	
	void updateDisplayScore(int camp) {
		scores[camp - 1].setText(Integer.toString(gamePanel.getScore(camp)));
	}
	
	void hideScores() {
		for (UIText score : scores) {
			score.setActive(false);
		}
	}
	
	void showScores() {
		for (UIText score : scores) {
			score.setActive(true);
		}
	}
}
