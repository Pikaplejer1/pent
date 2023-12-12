
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Tetris extends JPanel {

	// basic variables

	private static final String[] AuthorInfo = { "Producer:", "SevenUp" };
	private int Score = 0;
	private static int firstHighestScore = 0;
	private static int secondHighestScore = 0;
	private static int thirdHighestScore = 0;
	private static double avgScore = 0.0;
	private static double sumOfScore = 0;
	private static int numOfGames = 0;
	private String avgScoreNumber;

	private static boolean isBestOrderMode = false;
	private int numOfOrder = -1;

	private static int currentLevel = Gui.getInitialSpeed();
	private static final int BASE_TIME_DELAY = 600;
	private static final int STEP_SIZE = 50;
	private static int TimeDelay = BASE_TIME_DELAY-(Gui.getInitialSpeed()-1)*STEP_SIZE;

	private boolean isPause = false;

	FileHandler myFileHandler;

	// sends events based on a timer
	private Timer timer;
	private Timer timerBot;

	// field size
	private static final int BlockSize = 40; // cell size in pixels
	private static final int fieldWidth = 5;
	private static final int fieldHeight = 15;

	// create field
	private int[][] field = new int[fieldHeight][fieldWidth];

	// import Database of Pentominoes
	static int[][][] Pento = PentominoBuilder.basicDatabase;

	// brick state for shape and rotation
	private int NowPentoID;

	// matrix of the current brick
	private int[][] NowPento;

	// -matrix for next brck
	private int NextPentoID;
	private int[][] NextPento;

	// -position of current brick centered at top left
	private Point NowPentoPosition;

	// position and rotation that the bot is trying to reach
	private int WantedPos = 1;
	private int WantedRot;

	// assigns a rotation variable for the bot

	private int BlockRotation = 0;

	// to start new game
	public Tetris() {

		this.Initial();
		timer = new Timer(TimeDelay, TimerListener);
		timer.start();
		addKeyListener(KeyListener);
        if (Gui.getGameStyle() == 2) {
            SetBot();
        }

	}

	// turns upconing brick into current brick and create a new upcoming brick
	private void getNextBlock() {
		botState = 0;
		BlockRotation = 0;
		NowPentoID = NextPentoID;
		NowPento = NextPento;
		NextPentoID = GenerateNewPentoID();
		NextPento = getPento(NextPentoID);
		NowPentoPosition = CalNewPentoInitPos();
	}

	// collision
	private boolean isTouch(int[][] SrcNextPento, Point SrcNextPentoPos) {
		for (int i = 0; i < SrcNextPento.length; i++) {
			for (int j = 0; j < SrcNextPento[i].length; j++) {
				if (SrcNextPento[i][j] == 1) {
					int y = SrcNextPentoPos.y + i;
					int x = SrcNextPentoPos.x + j;

					if (y >= fieldHeight || x < 0 || x >= fieldWidth) {
						return true;
					}
					if (y < fieldHeight && SrcNextPentoPos.y + i >= 0
							&& SrcNextPentoPos.x + j < fieldWidth
							&& SrcNextPentoPos.x + j >= 0
							&& field[y][x] != -1) {
						return true;
					}
					if (y < 0) {
						continue;
					}

				}
			}
		}
		return false;
	}

	// turns falling brick into part of the tower
	private boolean isWithinField() {
		for (int i = 0; i < NowPento.length; i++) {
			for (int j = 0; j < NowPento[i].length; j++) {
				if (NowPento[i][j] == 1)
					if (field[NowPentoPosition.y + i][NowPentoPosition.x + j] != -1) {
						return false;
					} else {
						field[NowPentoPosition.y + i][NowPentoPosition.x + j] = NowPentoID;
					}

			}
		}
		return true;
	}

	// returns a starting point at the top of the gameboard
	private Point CalNewPentoInitPos() {
		int realNumofCol = NowPento[0].length;
		for (int i = 0; i < NowPento.length; i++) {
			boolean containOne = false;
			for (int j = 0; j < NowPento[0].length; j++) {
				if (NowPento[j][i] == 1) {
					containOne = true;
					break;
				}
			}
			if (!containOne) {
				realNumofCol--;
			}
		}

		return new Point(fieldWidth / 2 - realNumofCol / 2, 0);
	}

	// runs on game start and game restart
	public void Initial() {
		botState = 0;
		BlockRotation = 0;

		numOfOrder = -1;

		Score = 0;
		for (int i = 0; i < field.length; i++) {
			for (int j = 0; j < field[i].length; j++) {
				field[i][j] = -1;
			}
		}

		// generate new bricks
		NowPentoID = GenerateNewPentoID();
		NowPento = getPento(NowPentoID);
		NextPentoID = GenerateNewPentoID();
		NextPento = getPento(NextPentoID);

		NowPentoPosition = CalNewPentoInitPos();
		repaint();
	}

	// pauses and unpauses

	public void SetPause(boolean value) {
		isPause = value;
		if (isPause) {

			timer.stop();

		} else {
			timer.restart();

		}
		repaint();
	}

	public void SetBot() {
		isBestOrderMode = false;
		timerBot = new Timer(TimeDelay, BotTimerListener);
		timerBot.start();

		Initial();
		// Have to use repaint here
		repaint();
	}

	public void SetHuman() {
		isBestOrderMode = false;
		timerBot.stop();
		timer.start();

		this.Initial();
		this.repaint();
	}

	public void SetMode() {
		timerBot.stop();

		timer.start();
		Pento = BestOrderPentoDatabase.basicDatabase;
		isBestOrderMode = true;

		this.Initial();
		this.repaint();
	}

	// brick shape and rotation
	public int GenerateNewPentoID() {
		if (!isBestOrderMode) {
			return (int) (Math.random() * 12);
		} else {
			if (numOfOrder == 11) {
				numOfOrder = 0;
			} else {
				numOfOrder++;
			}
			return numOfOrder;

		}

	}

	// brick matrix based on createnewblockstate
	private int[][] getPento(int BlockState) {

		return Pento[BlockState];
	}

	// rotates brick

	private int[][] rotate(int[][] pento, int time) {
		if (time == 0) {
			return pento;
		}
		int heigth = pento.length;
		int width = pento[0].length;
		int[][] tempPento = new int[heigth][width];
		for (int i = 0; i < tempPento.length; i++) {
			for (int j = 0; j < tempPento.length; j++) {
				tempPento[i][j] = -1;
			}
		}
		int tmpH = heigth - 1, tmpW = 0;
		for (int i = 0; i < heigth && tmpW < width; i++) {
			for (int j = 0; j < width && tmpH > -1; j++) {
				tempPento[i][j] = pento[tmpH][tmpW];
				tmpH--;
			}
			tmpH = heigth - 1;
			tmpW++;
		}
		for (int i = 1; i < time; i++) {
			tempPento = rotate(tempPento, 0);
		}
		return tempPento;
	}

	static public void main(String... args) {

		Tetris tetris = new Tetris();
	}

	// ------- Returns a color object based on a number
	private Color GetColorOfID(int i) {
		if (i == 0) {
			return Color.BLUE;
		} else if (i == 1) {
			return Color.ORANGE;
		} else if (i == 2) {
			return Color.CYAN;
		} else if (i == 3) {
			return Color.GREEN;
		} else if (i == 4) {
			return Color.MAGENTA;
		} else if (i == 5) {
			return Color.PINK;
		} else if (i == 6) {
			return Color.RED;
		} else if (i == 7) {
			return Color.YELLOW;
		} else if (i == 8) {
			return new Color(0, 0, 0);
		} else if (i == 9) {
			return new Color(0, 0, 100);
		} else if (i == 10) {
			return new Color(100, 0, 0);
		} else if (i == 11) {
			return new Color(0, 100, 0);
		} else {
			return Color.LIGHT_GRAY;
		}
	}

	// ------- GraMphiccs
	@Override
	public void paintComponent(Graphics g) {
		/*
		 * boolean[][] TurnBlock = rotate(NowPento, 1);
		 * if (!isTouch(TurnBlock, NowPentoPosition)) {
		 * NowPento = TurnBlock;
		 * }
		 */
		Graphics2D localGraphics2D = (Graphics2D) g;

		super.paintComponent(g);

		// Draw the boundary
		for (int i = 0; i < Tetris.fieldHeight + 1; i++) {
			localGraphics2D.drawRect(0 * Tetris.BlockSize, i * Tetris.BlockSize, Tetris.BlockSize,
					Tetris.BlockSize);
			localGraphics2D.drawRect((Tetris.fieldWidth + 1) * Tetris.BlockSize, i * Tetris.BlockSize,
					Tetris.BlockSize,
					Tetris.BlockSize);
		}
		for (int i = 0; i < Tetris.fieldWidth; i++) {
			localGraphics2D.drawRect((1 + i) * Tetris.BlockSize, Tetris.fieldHeight * Tetris.BlockSize,
					Tetris.BlockSize,
					Tetris.BlockSize);

		}

		// Draw the current pentomino
		System.out.println("NowPentoID: " + NowPentoID);

		for (int i = 0; i < NowPento.length; i++) {
			for (int j = 0; j < NowPento[i].length; j++) {
				if (NowPento[i][j] == 1) {

					localGraphics2D.setColor(GetColorOfID(NowPentoID));
					localGraphics2D.fillRect((1 + this.NowPentoPosition.x + j) * BlockSize,
							(this.NowPentoPosition.y + i) * BlockSize,
							BlockSize, BlockSize);
					localGraphics2D.setColor(Color.BLACK);
					localGraphics2D.drawRect((1 + this.NowPentoPosition.x + j) * BlockSize,
							(this.NowPentoPosition.y + i) * BlockSize,
							BlockSize, BlockSize);
				}
			}

		}

		// Draw the pentominoes already in the field
		for (int i = 0; i < fieldHeight; i++) {
			for (int j = 0; j < fieldWidth; j++) {
				if (field[i][j] != -1) {
					localGraphics2D.setColor(GetColorOfID(field[i][j]));
					localGraphics2D.fillRect(BlockSize + j * BlockSize, i * BlockSize, BlockSize,
							BlockSize);
					localGraphics2D.setColor(Color.BLACK);
					localGraphics2D.drawRect(BlockSize + j * BlockSize, i * BlockSize, BlockSize,
							BlockSize);
				}

			}
		}

		// Next pentomino
		System.out.println("NextPentoID: " + NextPentoID);

		for (int i = 0; i < NextPento.length; i++) {
			for (int j = 0; j < NextPento[i].length; j++) {
				if (NextPento[i][j] == 1) {
					localGraphics2D.setColor(GetColorOfID(NextPentoID));
					localGraphics2D.fillRect(370 + j * 20, 325 + i * 20, 20,
							20);
					localGraphics2D.setColor(Color.BLACK);
					localGraphics2D.drawRect(370 + j * 20, 325 + i * 20, 20,
							20);

				}

			}
		}

		localGraphics2D.setColor(Color.BLACK);

		// score, highest score and authorInfo
		localGraphics2D.drawString("Score:" + this.Score, 350, 20);
		localGraphics2D.drawString("Rank", 375, 65);
		localGraphics2D.drawString("1st Score:" + firstHighestScore, 350, 85);
		localGraphics2D.drawString("2nd Score:" + secondHighestScore, 350, 105);
		localGraphics2D.drawString("3rd Score:" + thirdHighestScore, 350, 125);
		localGraphics2D.drawString("Average Score:" + avgScoreNumber, 350, 145);
		localGraphics2D.drawString("Number of Games:" + numOfGames, 350, 165);
		localGraphics2D.drawString("Next Pentomino:", 350, 270);

		for (int i = 0; i < Tetris.AuthorInfo.length; i++) {
			localGraphics2D.drawString(Tetris.AuthorInfo[i], 350, 500 + i * 20);
		}

	}

	// clears line and return numbers of line cleared
	private int ClearLines() {
		int completedLines = 0;

		for (int row = 0; row < field.length; row++) {
			if (isLineCompleted(row)) {
				// to make the pentomino fall down
				shiftLinesDown(row);
				// if that line is completed, then we make it empty

				for (int i = 0; i < fieldWidth; i++) {
					field[0][i] = -1;
				}
				completedLines++;
			}
		}

		return completedLines;
	}

	private boolean isLineCompleted(int row) {
		for (int cell : field[row]) {
			if (cell == -1) {
				return false;
			}
		}
		return true;
	}

	private void shiftLinesDown(int startingRow) {
		for (int row = startingRow; row > 0; row--) {
			System.arraycopy(field[row - 1], 0, field[row], 0, field[row].length);
		}
	}

	// game loop
	ActionListener TimerListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {

			// Move the falling block down, convert it into terrain, clear lines, generate a
			// new block
			if (isTouch(NowPento, new Point(NowPentoPosition.x, NowPentoPosition.y + 1))) {
				if (isWithinField()) {
					Score += ClearLines();
					if(Score%5==0)
					{
						TimeDelay = BASE_TIME_DELAY-(currentLevel-1)*STEP_SIZE;
						if(currentLevel<Gui.getSpeedCap())
							currentLevel++;
					}
//					if((Score>=Integer.parseInt(Gui.getLineCap())))
//						getNextBlock();
					getNextBlock();
				} else { // If upon converting to terrain the brick intersects the top of the gameboard
					if (Score > firstHighestScore) {
						thirdHighestScore = secondHighestScore;
						secondHighestScore = firstHighestScore;
						firstHighestScore = Score;
					} else if (Score > secondHighestScore && Score < firstHighestScore) {
						thirdHighestScore = secondHighestScore;
						secondHighestScore = Score;
					} else if (Score > thirdHighestScore && Score < secondHighestScore) {
						thirdHighestScore = Score;
					}
					sumOfScore += Score;
					numOfGames++;
					avgScoreNumber = String.format("%.3f", avgScore);
					avgScore = sumOfScore / numOfGames;
					myFileHandler = new FileHandler("highscore.txt");
					if(Gui.getGameStyle()!=2)
						myFileHandler.saveFile("highscore.txt",Score,JOptionPane.showInputDialog(getParent(), "What is your name so that we can save your score?"));
					JOptionPane.showMessageDialog(getParent(), "GAME OVER");
					Initial();
				}
			} else {
				NowPentoPosition.y++;
			}
			repaint();
		}
	};

	private int botState = 0;
	double bestScore = Double.MIN_VALUE;

	ActionListener BotTimerListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {

			Point DesPoint; // point ahead of where the brick is moving for testing collision

			switch (botState) {
				case 0: // SEARCHING
					int depth = 0; // to record the depth (inverted height) of the tower (depth = 15 - height)

					int completeLines = 0;
					int bumpiness = 0;
					int holes = 0;

					double bestScore = 0;

					Point originalPos = new Point(NowPentoPosition); // Save pentomino coords to recover them after
																		// search is
					// done.
					for (int r = 1; r < 5; r++) {
						NowPento = rotate(NowPento, 1); // Rotate pentomino

						NowPentoPosition.y = 0; // Move pentomino just above the game board, away from obstacles.
						while (!isTouch(NowPento, NowPentoPosition)) {
							// top edge of the board can not be collided with. There is no ceiling
							NowPentoPosition.x++;
						} // move pentomino to the left until it collides with a border
						NowPentoPosition.x--; // move it back so its not colliding anymore.
						for (; isTouch(NowPento, NowPentoPosition) == false; NowPentoPosition.x--) {
							while (!isTouch(NowPento, NowPentoPosition)) {
								NowPentoPosition.y++;
							}
							NowPentoPosition.y--; // 1 block up up so theres no overlap.

							// copies field
							int[][] originalField = new int[fieldHeight][fieldWidth];

							for (int i = 0; i < fieldHeight; i++) {
								for (int j = 0; j < fieldWidth; j++) {
									originalField[i][j] = field[i][j];

								}
							}

							// checks height
							if (isWithinField()) {
								for (int i = 0; i < fieldHeight; i++) {
									for (int j = 0; j < fieldWidth; j++) {
										if (field[i][j] != -1) {
											depth = i;
											j = fieldWidth;
											i = fieldHeight;

										}
									}
								}

							}
							// check bumpiness
							if (isWithinField()) {
								for (int x = 0; x < fieldWidth; x++) {
									int columnHeight = 0;
									for (int y = 0; y < fieldHeight; y++) {
										if (field[y][x] != -1) {
											columnHeight = fieldHeight - y;
											break;
										}
									}
									bumpiness += columnHeight;

								}
							}
							// check holes and the number of complete Lines
							if (isWithinField()) {
								for (int i = 0; i < fieldHeight; i++) {
									if (isLineCompleted(i)) {
										completeLines++;
									} else {
										holes++;
									}
								}
							}

							// set new objective for positon and rotation
							double score = -12.53 * depth + 1.02 * completeLines - 0.15 * bumpiness - 0.87 * holes;

							if (bestScore > score) {
								bestScore = score;

								WantedRot = r;
								WantedPos = NowPentoPosition.x;
								if (WantedRot == 4) {
									WantedRot = 0;
								}
							}

							for (int i = 0; i < fieldHeight; i++) {
								for (int j = 0; j < fieldWidth; j++) {
									field[i][j] = originalField[i][j];

								}
							}

							NowPentoPosition.y = 0;

						}
						NowPentoPosition = new Point(originalPos); // recenter it so it can rotate no matter what
					}
					NowPentoPosition = new Point(originalPos);
					botState = 1; // Change state so this doesn't run again.

				case 1: // MOVING
					if (BlockRotation != WantedRot) {

						int[][] TurnBlock = rotate(NowPento, 1);
						if (!isTouch(TurnBlock, NowPentoPosition)) {
							NowPento = TurnBlock;
							BlockRotation++;
							if (BlockRotation == 4) {
								BlockRotation = 0;
							}

						}

					} else {
						if (NowPentoPosition.x < WantedPos) {
							DesPoint = new Point(NowPentoPosition.x + 1, NowPentoPosition.y);
							if (!isTouch(NowPento, DesPoint)) {
								NowPentoPosition = DesPoint;
							}
						}

						if (NowPentoPosition.x > WantedPos) {
							DesPoint = new Point(NowPentoPosition.x - 1, NowPentoPosition.y);
							if (!isTouch(NowPento, DesPoint)) {
								NowPentoPosition = DesPoint;
							}
						}
					}
			}
		}
	};

	// ------- Input
	java.awt.event.KeyListener KeyListener = new java.awt.event.KeyListener() {
		@Override
		public void keyPressed(KeyEvent e) {

			if (!isPause) {
				Point DesPoint; // point ahead of where the brick is moving for testing collision
				switch (e.getKeyCode()) {
					case KeyEvent.VK_SPACE:
						int dis = 1;
						while (!isTouch(NowPento, new Point(NowPentoPosition.x, NowPentoPosition.y + dis))) {
							dis++;
						}
						NowPentoPosition = new Point(NowPentoPosition.x, NowPentoPosition.y + dis - 1);
						break;
					// Up
					case KeyEvent.VK_UP:
						int[][] TurnBlock = rotate(NowPento, 1);
						if (!isTouch(TurnBlock, NowPentoPosition)) {
							NowPento = TurnBlock;
							BlockRotation++;
						}
						break;
					case KeyEvent.VK_RIGHT:
						DesPoint = new Point(NowPentoPosition.x + 1, NowPentoPosition.y);
						if (!isTouch(NowPento, DesPoint)) {
							NowPentoPosition = DesPoint;
						}
						break;
					case KeyEvent.VK_LEFT:
						DesPoint = new Point(NowPentoPosition.x - 1, NowPentoPosition.y);
						if (!isTouch(NowPento, DesPoint)) {
							NowPentoPosition = DesPoint;
						}
						break;
					case KeyEvent.VK_P:
						timer.stop();
						break;
					case KeyEvent.VK_O:
						timer.start();
						break;
				}
				repaint();
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
		}

		@Override
		public void keyTyped(KeyEvent e) {
		}
	};
}