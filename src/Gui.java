import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

/**
 * A class that defines gui for prominent-style tetris game for a phase 2 of project 1-1
 * @author Marcel Pendyk 
 * @version 1.0
 */
public class Gui extends JFrame {

    private JPanel mainMenu;
    private JPanel startGame;
    private JPanel credits;
    private JPanel highscore;
    private JPanel options;
    private JPanel UI;
    private FileHandler myFileHandler;

    private static int initialSpeed =0;
    private static int speedCap =0;
    private static String lineCap ="0";
    private static int gameStyle =-1;
    private static String alghorhtmType=null;

    private static final String[] TABLE_NAMES = {"Position","Name", "Score"};
    private static final String[] PHOTOS_NAME_STRINGS = {"placeholder1.png"};
    private static final String[] PHOTOS_DESCRIPTION_STRINGS = {"placeholder1"};
    private static final Integer[] INITIAL_SPEED_INTEGERS = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
    private static final Integer[] SPEED_CAP_INTEGERS = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
    private static final String[] LINE_CAP_STRINGS = {"150","300","500"};
    private static final String[] ALGORITHM_TYPES_STRINGS = {"Main algorithm 1","AI alg. To be added"};
    private static final String[] CREATOR_NAMES = {"Marcel", "Carrey"};
    /**
     * Constructor sets the bounds, name and default operation when closed and sets the frame to be visible for every object created.
     * It also initializes the whole panels for the menu, sets up the logic behind it, and it shows the menu itself that user can interact with.
     */
    Gui(){
        myFileHandler = new FileHandler("highscore.txt");

        this.setTitle("Pentomino-style Tetris");
        this.setSize(850,400);
        this.setDefaultCloseOperation(this.EXIT_ON_CLOSE);

        uiInit();
        mainMenuInit();
        startGameInit();
        creditsInit();
        highscoreInit();
        optionsInit();

        showMainMenu();
        this.setVisible(true);

    }


    /**
     * Initializes main menu with buttons and a logo of ours everything has action listener so that scenes can be switched 
     */
    private void mainMenuInit() {
        mainMenu= new JPanel();
        //set the layout
        mainMenu.setLayout(new GridLayout(0,4,5,5));

        //initialize the buttons
        JButton startGameButton = new JButton("Start Game");
        JButton creditsButton = new JButton("Credits");
        JButton highscoreButton = new JButton("Highscore");
        JButton exitButton = new JButton("Exit");

        //visual change 
        startGameButton.setFocusable(false);
        creditsButton.setFocusable(false);
        highscoreButton.setFocusable(false);
        exitButton.setFocusable(false);

        //implement action listener to the buttons
        startGameButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                showStartGame();
            }

        });
        creditsButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                showCredits();
            }

        });
        highscoreButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                showHighscore();
            }

        });
        exitButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(-1);
            }

        });

        //add components to the frame 
        mainMenu.add(startGameButton);
        mainMenu.add(creditsButton);
        mainMenu.add(highscoreButton);
        mainMenu.add(exitButton);

        add(mainMenu);

    }

    /**
     * Initializes panel that that is seen after pressing start game in main menu. here the user can select if he wants to play 
     * or the bot will use the algorithm to play the tetris.
     * When bot is selected user is presented with the option to select the type of algorithm
     */
    private void startGameInit() {
        startGame= new JPanel();
        startGame.setLayout(new GridLayout());
        //create radio buttons and add them to one grop 
        JRadioButton playerButton = new JRadioButton("Human Player");
        JRadioButton botButton = new JRadioButton("Bot Player");
        ButtonGroup playerSelectionGroup = new ButtonGroup();
        playerSelectionGroup.add(playerButton);
        playerSelectionGroup.add(botButton);
        //visual 
        playerButton.setFocusable(false);
        botButton.setFocusable(false);
        //add a combo selection that selects the algorithms of bot
        JComboBox _algorithmType = new JComboBox<>(ALGORITHM_TYPES_STRINGS);
        //make in initially invisible so that it is seen after pressing bot player
        _algorithmType.setVisible(false);
        //tenor add a confirm button
        JButton acceptButton = new JButton("Confirm");
        //add componenets to the scene 
        startGame.add(playerButton);
        startGame.add(botButton);
        startGame.add(_algorithmType);
        startGame.add(acceptButton);

        //add action listener on everything 
        _algorithmType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                alghorhtmType=(String) _algorithmType.getSelectedItem();
            }
        });

        playerButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(playerButton.isSelected()==true)
                {
                    //change the visibility of combo box of algorithm selection 
                    _algorithmType.setVisible(false);
                }
            }

        });

        botButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (botButton.isSelected()) {
                    //change the visibility of combo box of algorithm selection
                    _algorithmType.setVisible(true);
                }
            }
        });
        acceptButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                if(playerButton.isSelected()==true)
                {
                    gameStyle=1;
                    showOptions();
                }
                else if(botButton.isSelected()==true)
                {
                    gameStyle=2;
                    showOptions();
                }

                else
                {
                    JOptionPane.showMessageDialog(startGame, "Please select a playing style.");
                }
            }

        });
//        this.pack();
    }

    /**
     * Initializes panel that is seen after pressing the accept button in start game panel.
     * In this panel user can choose in what speed the tetris shold start, what will be the cap maximum speed of the pentominoes to fall.
     * User also can select the goal of lines cleared. and a confirm button to go to the tetris game
     */
    private void optionsInit(){
        options= new JPanel();
        options.setLayout(new GridLayout(0,2));
        //initialize the objects
        JComboBox _initialSpeed = new JComboBox<>(INITIAL_SPEED_INTEGERS);
        JComboBox _speedCap = new JComboBox<>(SPEED_CAP_INTEGERS);
        JComboBox _lineCap = new JComboBox<>(LINE_CAP_STRINGS);
        JButton confirmButton = new JButton("Confirm");
        //initialize the descryption of the combo boxes
        JLabel label1 = new JLabel("Initial Speed:");
        JLabel label2 = new JLabel("Speed Cap:");
        JLabel label3 = new JLabel("Line Cap:");
        //add the components 
        options.add(label1);
        options.add(_initialSpeed);
        options.add(label2);
        options.add(_speedCap);
        options.add(label3);
        options.add(_lineCap);
        options.add(new Label("Confirm button ->"));
        options.add(confirmButton);
//        this.pack();
        //implement action listener to everything 
        _initialSpeed.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                initialSpeed=(int) _initialSpeed.getSelectedItem();
            }
        });
        _speedCap.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                speedCap=(int) _speedCap.getSelectedItem();
            }
        });
        _lineCap.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lineCap=(String) _lineCap.getSelectedItem();
            }
        });

        confirmButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(initialSpeed<=speedCap)
                    showTetrisUI();
                else
                    JOptionPane.showMessageDialog(startGame, "Initial speed can't be higher than speed cap!");

            }

        });
    }

    /**
     * Initializes panel that is seen after pressing credits in main menu panel.
     * This panel shows the creators of the project: their photos, names and corresponding roles. 
     */
    private void creditsInit() {
        //not commenthing this only god know how it works by now :)
        //but basicly iterates over every photo adding it to the panel and adding a border, and adds names, roles etc.
        credits = new JPanel();
        credits.setLayout(new GridLayout(0,2));
        credits.setBackground(new Color(0xFAF0E6));

        JLabel nameLabel1 = new JLabel("Pendyk Marcel ");
        JLabel roleLabel1= new JLabel("Team Leader");
        JLabel nameLabel2 = new JLabel("HUANG, Zhengzhong Carrey ");
        JLabel roleLabel2= new JLabel("Code Lead");
        JLabel nameLabel3 = new JLabel("Donéa, Élisa ");
        JLabel roleLabel3= new JLabel("Flexible Assistant");
        JLabel nameLabel4 = new JLabel("Rogóż Aleks ");
        JLabel roleLabel4= new JLabel("Report Lead");
        JLabel nameLabel5 = new JLabel("Proesman Alexis ");
        JLabel roleLabel5= new JLabel("Report expert");
        JLabel nameLabel6 = new JLabel("Vittorio Lama ");
        JLabel roleLabel6= new JLabel("Presentation Lead");

        credits.add(nameLabel1);
        credits.add(roleLabel1);
        credits.add(nameLabel2);
        credits.add(roleLabel2);
        credits.add(nameLabel3);
        credits.add(roleLabel3);
        credits.add(nameLabel4);
        credits.add(roleLabel4);
        credits.add(nameLabel5);
        credits.add(roleLabel5);
        credits.add(nameLabel6);
        credits.add(roleLabel6);

        JButton goBack = new JButton("Go back");
        goBack.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                showMainMenu();
            }

        });
        credits.add(goBack);

    }

    /**
     * Initializes panel that is seen after pressing higscores in main menu panel.
     * This panel shows the best playes in the tetris game that are read from the file.
     * Format is *string* *int* 
     */
    private void highscoreInit() {
        /*
         placeholder of history
         in my mind a table of 2 cols x rows that has names and scores of the best players
        */
        int size =myFileHandler.getScoresArr().length;
        int[] scoreArr = new int[size];
        System.arraycopy(myFileHandler.getScoresArr(),0,scoreArr,0,size);
        String[] namesArr = new String[size];
        System.arraycopy(myFileHandler.getNamesArr(),0,namesArr,0,size);

        JTable scoreTable;
        highscore = new JPanel();
        Font hsFont = new Font("Comic Sans MS", Font.PLAIN,30);
        DefaultTableModel tableModel = new DefaultTableModel();

        tableModel.addColumn("Rank");
        tableModel.addColumn("Name");
        tableModel.addColumn("Score");
        tableModel.setColumnCount(3);
        for (int i = 0; i < Math.min(10, scoreArr.length); i++) {
            tableModel.addRow(new Object[]{i + 1, namesArr[i], scoreArr[i]});
        }
        scoreTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(scoreTable);
        scoreTable.setBackground(new Color(0,0,0,0));
        scoreTable.getTableHeader().setBackground((new Color(0,0,0,0)));
        scoreTable.setShowGrid(false);
        scoreTable.setFocusable(false);
        scoreTable.setDefaultEditor(Object.class, null);
        scoreTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scoreTable.setFocusable(false);
        scoreTable.setRowSelectionAllowed(false);
        scoreTable.setColumnSelectionAllowed(false);
        scoreTable.setFont(hsFont);
        scoreTable.setRowHeight(30);
        highscore.add(scoreTable, BorderLayout.CENTER);
        scoreTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        scoreTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        scoreTable.getColumnModel().getColumn(2).setPreferredWidth(200);

        JButton goBack = new JButton("Go back");
        goBack.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                showMainMenu();
            }

        });
        highscore.add(goBack);
    }

    /**
     * placeholder for the tetris game
     */
    private void uiInit(){
        UI = new JPanel();
        UI.add(new JLabel(" tetris game placeholder"));
    }

    /**
     * Sets the content pane and repaints it
     */
    private void showOptions(){
        this.setContentPane(options);
        this.revalidate();
        this.repaint();
    }
    /**
     * Sets the content pane and repaints it
     */
    private void showStartGame() {
        this.setContentPane(startGame);
        this.revalidate();
        this.repaint();
    }
    /**
     * Sets the content pane and repaints it
     */
    private void showMainMenu() {
        this.setContentPane(mainMenu);
        this.revalidate();
        this.repaint();
    }
    /**
     * Sets the content pane and repaints it
     */
    private void showCredits() {
        this.setContentPane(credits);
        this.revalidate();
        this.repaint();
    }
    /**
     * Sets the content pane and repaints it
     */
    private void showHighscore() {
        this.setContentPane(highscore);
        this.revalidate();
        this.repaint();
    }
    /**
     * Sets the content pane and repaints it
     */
    private void showTetrisUI(){
        this.setContentPane(UI);
        this.revalidate();
        this.repaint();
        this.setVisible(false);
        TetrisApp tetrisApp = new TetrisApp();
        tetrisApp.setVisible(true);

    }

    public static int getInitialSpeed() {
        return initialSpeed;
    }

    public static int getSpeedCap() {
        return speedCap;
    }

    public static int getGameStyle() {
        return gameStyle;
    }

    public static String getLineCap() {
        return lineCap;
    }
}