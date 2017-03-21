package game;

import java.awt.BorderLayout;

import javax.swing.JFrame;

public class GameLauncher {

	private static Game game = new Game();

	
	public static void main(String args[]){
		game.setMinimumSize(Game.DIMENSIONS);
		game.setMaximumSize(Game.DIMENSIONS);
		game.setPreferredSize(Game.DIMENSIONS);
		
		game.frame = new JFrame(Game.NAME);
		game.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game.frame.setLayout(new BorderLayout());
		
		game.frame.add(game, BorderLayout.CENTER);
		game.frame.pack();
		
		game.frame.setResizable(false);
		game.frame.setLocationRelativeTo(null);
		game.frame.setVisible(true);
		
		for(int i = 0; i < game.cheats.length; i++)
			game.cheats[i] = false;
		new Game().start();
	}
}
