package gameCore;

import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class GamePanel extends JPanel{
	private static final long serialVersionUID = 1L;
	int nombreJoueurs = 2;
	JLabel[] scores;
	
	int nombreScores[];
	
	public GamePanel() {
		
	}
	
	void initialiserFenetre() {
		scores = new JLabel[nombreJoueurs];
		nombreScores = new int[nombreJoueurs];
		
		this.setLayout(new FlowLayout(FlowLayout.CENTER, 50, 0));
		
		for (int i = 0; i < nombreJoueurs; i++) {
			nombreScores[i] = 0;
			
			scores[i] = new JLabel();
			scores[i].setText("Joueur " + (i + 1) + ": " + nombreScores[i]);
			scores[i].setFont(new Font("Open sans", Font.BOLD, 20));
			
			this.add(scores[i]);
		}
	}
	
	public void setScore(int camp, int score) {
		this.nombreScores[camp - 1] = score;
		
		for (int i = 0; i < nombreJoueurs; i++) {
			scores[i].setText("Joueur " + (i + 1) + ": " + nombreScores[i]);
		}
	}
	
	public int getScore(int camp) {
		return this.nombreScores[camp - 1];
	}
	
	public void resetScore() {
		for (int i = 0; i < nombreJoueurs; i++) {
			nombreScores[i] = 0;
			
			scores[i].setText("Joueur " + (i + 1) + ": " + nombreScores[i]);
		}
	}
}
