
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

public class TetrisApp extends JFrame {

	Tetris tetris = new Tetris();

	public TetrisApp() {
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(560, 700);
		this.setTitle("Tetris");
		this.setResizable(false);
		this.setLocation(0,0);
		JMenuBar menu = new JMenuBar();
		this.setJMenuBar(menu);
		JMenu gameMenu = new JMenu("Game");
		JMenuItem newGameItem = gameMenu.add("New Game");
		newGameItem.addActionListener(this.NewGameAction);
		JMenuItem pauseItem = gameMenu.add("Pause");
		pauseItem.addActionListener(this.PauseAction);
		JMenuItem continueItem = gameMenu.add("Continue");
		continueItem.addActionListener(this.ContinueAction);
		JMenuItem exitItem = gameMenu.add("Quit");
		exitItem.addActionListener(this.ExitAction);

		JMenu playerMenu = new JMenu("Player");
		JMenuItem botItem = playerMenu.add("Bot");
		botItem.addActionListener(this.BotAction);
		JMenuItem humanItem = playerMenu.add("Human");
		humanItem.addActionListener(this.HumanAction);

		JMenu helpMenu = new JMenu("Help");
		JMenuItem aboutItem = helpMenu.add("Info");
		aboutItem.addActionListener(this.AboutAction);

		JMenu orderMenue = new JMenu("Order");
		JMenuItem bestOrder = orderMenue.add("Best Order");
		bestOrder.addActionListener(this.BestOrderAction);

		menu.add(gameMenu);
		menu.add(playerMenu);
		menu.add(helpMenu);
		menu.add(orderMenue);

		this.add(this.tetris);
		this.tetris.setFocusable(true);
	}

	ActionListener NewGameAction = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {

			TetrisApp.this.tetris.Initial();
			TetrisApp.this.tetris.SetPause(false);
		}
	};

	ActionListener PauseAction = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {

			TetrisApp.this.tetris.SetPause(true);
		}
	};

	ActionListener ContinueAction = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {

			TetrisApp.this.tetris.SetPause(false);
		}
	};

	ActionListener ExitAction = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {

			System.exit(0);
		}
	};

	ActionListener BotAction = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			TetrisApp.this.tetris.SetBot();
		}
	};

	ActionListener HumanAction = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			TetrisApp.this.tetris.SetHuman();
		}
	};

	ActionListener AboutAction = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {

			JOptionPane.showMessageDialog(TetrisApp.this,
					"Press space bar to fall down the pentominoes\n Press up arrow to rotate pentominoes\nPress left and right arrow to move",
					"0", JOptionPane.WARNING_MESSAGE);

		}
	};

	ActionListener BestOrderAction = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {

			TetrisApp.this.tetris.SetMode();

		}
	};

}
