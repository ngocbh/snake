package maze80b1;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Stack;
import javax.swing.*;

/**
 *
 * @author Nikos Kanargias
 * E-mail: nkana@tee.gr
 * @version 8.0 b1
 * 
 * The software solves and visualizes the robot motion planning problem,
 * by implementing variants of DFS, BFS and A* algorithms, as described
 * by E. Keravnou in her book: "Artificial Intelligence and Expert Systems",
 * Hellenic Open University,  Patra 2000 (in Greek)
 * as well as the Greedy search algorithm, as a special case of A*.
 * 
 * The software also implements Dijkstra's algorithm,
 * as just described in the relevant article in Wikipedia.
 * http://en.wikipedia.org/wiki/Dijkstra%27s_algorithm
 * 
 * In version 8.0 the Iterative Deepening Depth-First Search (IDDFS) algorithm is also implemented.
 * 
 * The superiority of  A* and Dijkstra's algorithms against the other four becomes obvious.
 * 
 * The user can change the number of the grid cells, indicating
 * the desired number of rows and columns.
 * 
 * The user can add as many obstacles he/she wants, as he/she
 * would "paint" free curves with a drawing program.
 * 
 * Individual obstacles can be removed by clicking them.
 * 
 * The position of the robot and/or the target can be changed by dragging with the mouse.
 * 
 * Jump from search in "Step-by-Step" way to "Animation" way and vice versa is done
 * by pressing the corresponding button, even when the search is in progress.
 * 
 * The speed of a search can be changed, even if the search is in progress.
 * It is sufficient to place the slider "Delay" in the new desired position
 * and then press the "Animation" button.
 * 
 * The application considers that the robot itself has some volume.
 * Therefore it can’t move diagonally to a free cell passing between two obstacles
 * adjacent to one apex.
 *
 * When 'Step-by-Step' or 'Animation' search is underway it is not possible to change
 * the position of obstacles, robot and target, as well as the search algorithm.
 * 
 * When 'Real-Time' search is underway the position of obstacles, robot and target
 * can be changed.
 * 
 * The IDDFS algorithm runs only in 'Real-Time' mode.
 * The search tree varies as the algorithm goes on.
 * Because the size of the tree may become huge as the depth of the search
 * and the branching factor increase, it is expected to reach the goal only if the
 * necessary depth of search (distance between the robot and target) is relatively small.
 * One must keep in mind that the branching factor for rectangular cells with diagonal
 * moves allowed is 8, whereas for triangular cells is 3.
 * To see the DLS traversal of the tree, uncomment the print statements
 * of IDDFS and DLS methods and run the program under the NetBeans IDE.
 * 
 * Advisable not to draw arrows to predecessors in large grids.
 */

public class Maze80b1 {

    public static JFrame mazeFrame;  // The main form of the program
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int width  = 693;
        int height = 545;
        mazeFrame = new JFrame("Maze 8.0 b1");
        mazeFrame.setContentPane(new MazePanel(width,height));
        mazeFrame.pack();
        mazeFrame.setResizable(false);
        // the form is located in the center of the screen
        mazeFrame.setLocationRelativeTo(null);
        mazeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mazeFrame.setVisible(true);
    } // end main()
    
    /**
      * This class defines the contents of the main form
      * and contains all the functionality of the program.
      */
    public static class MazePanel extends JPanel {
        
        /*
         **********************************************************
         *          Nested classes in MazePanel
         **********************************************************
         */
        
        /**
         * The class that creates the AboutBox
         */
        private class AboutBox extends JDialog{

            public AboutBox(Frame parent, boolean modal){
                super(parent, modal);
                
                int width = 350;
                int height = 190;
                super.setSize(width,height);
         
                // the aboutBox is located in the center of the screen
                super.setLocationRelativeTo(null);

                super.setResizable(false);
                super.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
 
                JLabel title = new JLabel("Maze", JLabel.CENTER);
                title.setFont(new Font("Helvetica",Font.PLAIN,24));
                title.setForeground(new java.awt.Color(255, 153, 102));
 
                JLabel version = new JLabel("Version: 8.0 b1", JLabel.CENTER);
                version.setFont(new Font("Helvetica",Font.BOLD,14));
 
                JLabel programmer = new JLabel("Designer: Nikos Kanargias", JLabel.CENTER);
                programmer.setFont(new Font("Helvetica",Font.PLAIN,16));
 
                JLabel email = new JLabel("E-mail: nkana@tee.gr", JLabel.CENTER);
                email.setFont(new Font("Helvetica",Font.PLAIN,14));
 
                JLabel sourceCode = new JLabel("Code and documentation:", JLabel.CENTER);
                sourceCode.setFont(new Font("Helvetica",Font.PLAIN,14));
 
                JLabel link = new JLabel("<html><a href=\\\"\\\">Code and documentation</a></html>", JLabel.CENTER);
                link.setCursor(new Cursor(Cursor.HAND_CURSOR));
                link.setFont(new Font("Helvetica",Font.PLAIN,16));
                link.setToolTipText
                    ("Click this link to retrieve code and documentation from DropBox");
 
                JLabel video = new JLabel("<html><a href=\\\"\\\">Watch demo video on YouTube</a></html>", JLabel.CENTER);
                video.setCursor(new Cursor(Cursor.HAND_CURSOR));
                video.setFont(new Font("Helvetica",Font.PLAIN,16));
                video.setToolTipText
                    ("Click this link to watch demo video on YouTube");
 
                JLabel dummy = new JLabel("");
 
                super.add(title);
                super.add(version);
                super.add(programmer);
                super.add(email);
                super.add(link);
                super.add(video);
                super.add(dummy);
         
                goDropBox(link);
                goYouTube(video);
 
                title.     setBounds(5,  0, 330, 30);
                version.   setBounds(5, 30, 330, 20);
                programmer.setBounds(5, 55, 330, 20);
                email.     setBounds(5, 80, 330, 20);
                link.      setBounds(5,105, 330, 20);
                video.     setBounds(5,130, 330, 20);
            }
 
            private void goDropBox(JLabel website) {
                website.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        try {
                            Desktop.getDesktop().browse(new URI("https://goo.gl/tRaLfe"));
                        } catch (URISyntaxException | IOException ex) {
                            //It looks like there's a problem
                        }
                    }
                });
            }

            private void goYouTube(JLabel video) {
                video.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        try {
                            Desktop.getDesktop().browse(new URI("https://youtu.be/jvHA21M5Ugw"));
                        } catch (URISyntaxException | IOException ex) {
                            //It looks like there's a problem
                        }
                    }
                });
            }
        } // end nested class AboutBox

        /**
         * Creates a random, perfect (without cycles) maze
         * From http://rosettacode.org/wiki/Maze_generation
         *
         * recursive backtracking algorithm
         * shamelessly borrowed from the ruby at
         * http://weblog.jamisbuck.org/2010/12/27/maze-generation-recursive-backtracking
         */
        private static final class MazeGenerator {
            private final int x;
            private final int y;
            private final int[][] maze;
            private String maze_str = "";
 
            public MazeGenerator(int x, int y) {
                this.x = x;
                this.y = y;
                maze = new int[this.x][this.y];
                generateMaze(0, 0);
                make_string();
            }
 
            public void make_string() {
                for (int i = 0; i < y; i++) {
                    // draw the north edge
                    for (int j = 0; j < x; j++)
                        maze_str = maze_str.concat((maze[j][i] & 1) == 0 ? "+-" : "+ ");
                    maze_str = maze_str.concat("+");
                    // draw the west edge
                    for (int j = 0; j < x; j++)
                        maze_str = maze_str.concat((maze[j][i] & 8) == 0 ? "| " : "  ");
                    maze_str = maze_str.concat("|");
                }
                // draw the bottom line
                for (int j = 0; j < x; j++)
                    maze_str = maze_str.concat("+-");
                maze_str = maze_str.concat("+");
            }
 
            private void generateMaze(int cx, int cy) {
                DIR[] dirs = DIR.values();
                Collections.shuffle(Arrays.asList(dirs));
                for (DIR dir : dirs) {
                    int nx = cx + dir.dx;
                    int ny = cy + dir.dy;
                    if (between(nx, x) && between(ny, y) && (maze[nx][ny] == 0)) {
                        maze[cx][cy] |= dir.bit;
                        maze[nx][ny] |= dir.opposite.bit;
                        generateMaze(nx, ny);
                    }
                }
            }
 
            private static boolean between(int v, int upper) {
                return (v >= 0) && (v < upper);
            }
 
            private static enum DIR {
                N(1, 0, -1), S(2, 0, 1), E(4, 1, 0), W(8, -1, 0);
                private final int bit;
                private final int dx;
                private final int dy;
                private DIR opposite;
 
                // use the static initializer to resolve forward references
                static {
                    N.opposite = S;
                    S.opposite = N;
                    E.opposite = W;
                    W.opposite = E;
                }
 
                private DIR(int bit, int dx, int dy) {
                    this.bit = bit;
                    this.dx = dx;
                    this.dy = dy;
                }
            };
        } // end nested class MazeGenerator

        /**
         * Helper class that represents the cell of the grid
         */
        private class Cell {
            int row;     // the row number of the cell(row 0 is the top)
            int col;     // the column number of the cell (Column 0 is the left)
            double g;    // the value of the function g of A* and Greedy algorithms
            double h;    // the value of the function h of A* and Greedy algorithms
            double f;    // the value of the function h of A* and Greedy algorithms
            double dist; // the distance of the cell from the initial position of the robot
                         // Ie the label that updates the Dijkstra's algorithm
            Cell prev;   // Each state corresponds to a cell
                         // and each state has a predecessor which
                         // is stored in this variable
            
            public Cell(int row, int col){
               this.row = row;
               this.col = col;
            }
            
        } // end nested class Cell
      
        /**
         * Auxiliary class that specifies that the cells will be sorted
         * according their 'f' field
         */
        private class CellComparatorByF implements Comparator<Cell>{
            @Override
            public int compare(Cell cell1, Cell cell2){
                return Double.compare(cell1.f,cell2.f);
            }
        } // end nested class CellComparatorByF
      
        /**
         * Auxiliary class that specifies that the cells will be sorted
         * according their 'dist' field
         */
        private class CellComparatorByDist implements Comparator<Cell>{
            @Override
            public int compare(Cell cell1, Cell cell2){
                return Double.compare(cell1.dist,cell2.dist);
            }
        } // end nested class CellComparatorByDist
      
        /**
         * Class that handles mouse movements as we "paint"
         * obstacles or move the robot and/or target.
         */
        private class MouseHandler implements MouseListener, MouseMotionListener {
            private int cur_row, cur_col, cur_val, row, col;
            private double dist = INFINITY;

            private void findRowCol(int cur_Y, int cur_X){
                double cur_dist;
                if (square.isSelected()){
                    row = (cur_Y - 10) / squareSize;
                    col = (cur_X - 10) / squareSize;
                } else {
                    for (int r = 0; r < rows; r++)
                        for (int c = 0; c < columns; c++) {
                            cur_dist = Math.hypot(cur_X-centers[r][c].getX(),cur_Y-centers[r][c].getY());
                            if (cur_dist < Math.min(dist, radius)){
                                dist = cur_dist;
                                row = r;
                                col = c;
                            }
                        }
                }
            }

            @Override
            public void mousePressed(MouseEvent evt) {
                row = 0; col = 0; dist = INFINITY;
                
                findRowCol(evt.getY(), evt.getX());

                if (square.isSelected() ? row >= 0 && row < rows && col >= 0 && col < columns : dist < INFINITY) {
                    if (realTime ? true : !found && !searching){
                        if (realTime)
                            fillGrid();
                        cur_row = row;
                        cur_col = col;
                        cur_val = grid[row][col];
                        if (cur_val == EMPTY)
                            grid[row][col] = OBST;
                        if (cur_val == OBST)
                            grid[row][col] = EMPTY;
                        if (realTime && dijkstra.isSelected())
                            initializeDijkstra();
                    }
                    if (realTime)
                        realTimeAction();
                    else
                        repaint();
                }
            }

            @Override
            public void mouseDragged(MouseEvent evt) {
                row = 0; col = 0; dist = INFINITY;

                findRowCol(evt.getY(), evt.getX());

                if (square.isSelected() ? row >= 0 && row < rows && col >= 0 && col < columns : dist < INFINITY) {
                    if (realTime ? true : !found && !searching){
                        if (realTime)
                            fillGrid();
                        if (!(row == cur_row && col == cur_col) && (cur_val == ROBOT || cur_val == TARGET)){
                            int new_val = grid[row][col];
                            if (new_val == EMPTY){
                                grid[row][col] = cur_val;
                                if (cur_val == ROBOT) {
                                    robotStart.row = row;
                                    robotStart.col = col;
                                } else {
                                    targetPos.row = row;
                                    targetPos.col = col;
                                }
                                grid[cur_row][cur_col] = new_val;
                                cur_row = row;
                                cur_col = col;
                                cur_val = grid[row][col];
                            }
                        } else if (grid[row][col] != ROBOT && grid[row][col] != TARGET)
                            grid[row][col] = OBST;
                        if (realTime && dijkstra.isSelected())
                            initializeDijkstra();
                    }
                    if (realTime)
                        realTimeAction();
                    else
                        repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent evt) { }
            @Override
            public void mouseEntered(MouseEvent evt) { }
            @Override
            public void mouseExited(MouseEvent evt) { }
            @Override
            public void mouseMoved(MouseEvent evt) { }
            @Override
            public void mouseClicked(MouseEvent evt) { }
            
        } // end nested class MouseHandler
        
        /**
         * The class that is responsible for the animation
         */
        private class RepaintAction implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent evt) {
                checkTermination();
                repaint();
                if (endOfSearch)
                {
                    animation = false;
                    timer.stop();
                }
            }
        } // end nested class RepaintAction
      
        /*
         **********************************************************
         *          Constants of MazePanel class
         **********************************************************
         */
        
        private final static double
            INFINITY = Double.MAX_VALUE; // The representation of the infinite
        private final static int
            EMPTY    = 0,  // empty cell
            OBST     = 1,  // cell with obstacle
            ROBOT    = 2,  // the position of the robot
            TARGET   = 3,  // the position of the target
            FRONTIER = 4,  // cells that form the frontier (OPEN SET)
            CLOSED   = 5,  // cells that form the CLOSED SET
            ROUTE    = 6;  // cells that form the robot-to-target path
        
        // Messages to the user
        private final static String
            MSG_DRAW_AND_SELECT =
                "\"Paint\" obstacles, then click 'Real-Time' or 'Step-by-Step' or 'Animation'",
            MSG_SELECT_STEP_BY_STEP_ETC =
                "Click 'Step-by-Step' or 'Animation' or 'Clear'",
            MSG_NO_SOLUTION =
                "There is no path to the target !!!";

        /*
         **********************************************************
         *          Variables of MazePanel class
         **********************************************************
         */
        
        JSpinner rowsSpinner, columnsSpinner; // Spinners for entering # of rows and columns
        
        int
            rows,        // the number of rows of the grid
            columns,     // the number of columns of the grid
            squareSize,  // the size of the square cell in pixels
            arrowSize;   // the size of the tip of the arrow
                         // pointing the predecessor cell
        double
            radius,      // the radius of triangular and hexagonal cells
            height,      // half the height of hexagonal cells or the height of triangular cells
            edge;        // the edge of the triangular cell
        
        ArrayList<Cell> openSet   = new ArrayList();// the OPEN SET
        ArrayList<Cell> closedSet = new ArrayList();// the CLOSED SET
        ArrayList<Cell> graph     = new ArrayList();// the set of vertices of the graph
                                                    // to be explored by Dijkstra's algorithm
         
        Cell robotStart; // the initial position of the robot
        Cell targetPos;  // the position of the target
      
        JLabel message;  // message to the user

        // buttons for selecting the shape of the cell
        JRadioButton triangle, square, hexagon;
        
        // basic buttons
        JButton resetButton, mazeButton, clearButton, realTimeButton, stepButton, animationButton, aboutButton;
        
        // buttons for selecting the algorithm
        JRadioButton dfs, iddfs, bfs, aStar, greedy, dijkstra;
        
        // the slider for adjusting the speed of the animation
        JSlider slider;
        
        // Diagonal movements allowed?
        JCheckBox diagonal;
        // Draw arrows to predecessors
        JCheckBox drawArrows;
        
        int[][] grid;        // the grid
        Point[][] centers;   // the centers of the cells
        boolean realTime;    // Solution is displayed instantly
        boolean found;       // flag that the goal was found
        boolean searching;   // flag that the search is in progress
        boolean endOfSearch; // flag that the search came to an end
        boolean animation;   // flag that the animation is running
        int delay;           // time delay of animation (in msec)
        int expanded;        // the number of nodes that have been expanded
        
        // the object that controls the animation
        RepaintAction action = new RepaintAction();
        
        // the Timer which governs the execution speed of the animation
        Timer timer;
      
        /**
         * The creator of the panel
         * @param width  the width of the panel.
         * @param height the height of the panel.
         */
        public MazePanel(int width, int height) {
      
            super.setLayout(null);
            
            MouseHandler listener = new MouseHandler();
            super.addMouseListener(listener);
            super.addMouseMotionListener(listener);

            super.setBorder(BorderFactory.createMatteBorder(2,2,2,2,Color.blue));
            super.setPreferredSize( new Dimension(width,height) );

            grid    = new int[rows][columns];

            // We create the contents of the panel

            message = new JLabel(MSG_DRAW_AND_SELECT, JLabel.CENTER);
            message.setForeground(Color.blue);
            message.setFont(new Font("Helvetica",Font.PLAIN,16));

            JLabel rowsLbl = new JLabel("# of rows (5-83):", JLabel.RIGHT);
            rowsLbl.setFont(new Font("Helvetica",Font.PLAIN,13));

            SpinnerModel rowModel = new SpinnerNumberModel(41, //initial value
                                       5,  //min
                                       83, //max
                                       1); //step
            rowsSpinner = new JSpinner(rowModel);
 
            JLabel columnsLbl = new JLabel("# of columns (5-83):", JLabel.RIGHT);
            columnsLbl.setFont(new Font("Helvetica",Font.PLAIN,13));

            SpinnerModel colModel = new SpinnerNumberModel(41, //initial value
                                       5,  //min
                                       83, //max
                                       1); //step
            columnsSpinner = new JSpinner(colModel);

            // ButtonGroup that synchronizes the two RadioButtons
            // choosing the shape of the cell, so that only one
            // can be selected anytime
            ButtonGroup shapeGroup = new ButtonGroup();

            triangle = new JRadioButton("Triangle");
            triangle.setToolTipText("Trigonal cell");
            triangle.addActionListener(this::radioButtonsActionPerformed);
            shapeGroup.add(triangle);

            square = new JRadioButton("Square");
            square.setToolTipText("Square cell");
            square.addActionListener(this::radioButtonsActionPerformed);
            shapeGroup.add(square);

            hexagon = new JRadioButton("Hexagon");
            hexagon.setToolTipText("Hexagonal cell");
            hexagon.addActionListener(this::radioButtonsActionPerformed);
            shapeGroup.add(hexagon);

            JPanel shapePanel = new JPanel();
            shapePanel.setBorder(javax.swing.BorderFactory.
                    createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(),
                    "Shape of Cell", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.TOP, new java.awt.Font("Helvetica", 0, 14)));
            
            square.setSelected(true);  // Rectangular cell is initially selected 

            resetButton = new JButton("New grid");
            resetButton.setBackground(Color.lightGray);
            resetButton.setToolTipText
                    ("Clears and redraws the grid according to the given rows and columns");
            resetButton.addActionListener(this::resetButtonActionPerformed);

            mazeButton = new JButton("Maze");
            mazeButton.setBackground(Color.lightGray);
            mazeButton.setToolTipText
                    ("Creates a random maze");
            mazeButton.addActionListener(this::mazeButtonActionPerformed);

            clearButton = new JButton("Clear");
            clearButton.setBackground(Color.lightGray);
            clearButton.setToolTipText
                    ("First click: clears search, Second click: clears obstacles");
            clearButton.addActionListener(this::clearButtonActionPerformed);

            realTimeButton = new JButton("Real-Time");
            //realTimeButton.addActionListener(new ActionHandler());
            realTimeButton.setBackground(Color.lightGray);
            realTimeButton.setToolTipText
                    ("Position of obstacles, robot and target can be changed when search is underway");
            realTimeButton.addActionListener(this::realTimeButtonActionPerformed);

            stepButton = new JButton("Step-by-Step");
            stepButton.setBackground(Color.lightGray);
            stepButton.setToolTipText
                    ("The search is performed step-by-step for every click");
            stepButton.addActionListener(this::stepButtonActionPerformed);

            animationButton = new JButton("Animation");
            animationButton.setBackground(Color.lightGray);
            animationButton.setToolTipText
                    ("The search is performed automatically");
            animationButton.addActionListener(this::animationButtonActionPerformed);

            JLabel delayLbl = new JLabel("Delay (0-1000 msec)", JLabel.CENTER);
            delayLbl.setFont(new Font("Helvetica",Font.PLAIN,10));
            
            slider = new JSlider(0,1000,500); // initial value of delay 500 msec
            slider.setToolTipText
                    ("Regulates the delay for each step (0 to 1000 msec)");
            
            delay = slider.getValue();
            
            // ButtonGroup that synchronizes the five RadioButtons
            // choosing the algorithm, so that only one
            // can be selected anytime
            ButtonGroup algoGroup = new ButtonGroup();

            dfs = new JRadioButton("DFS");
            dfs.setToolTipText("Depth First Search algorithm");
            dfs.addActionListener(this::algoButtonsActionPerformed);
            algoGroup.add(dfs);

            iddfs = new JRadioButton("IDDFS");
            iddfs.setToolTipText("Iterative Deeping Depth First Search algorithm");
            iddfs.addActionListener(this::algoButtonsActionPerformed);
            algoGroup.add(iddfs);

            bfs = new JRadioButton("BFS");
            bfs.setToolTipText("Breadth First Search algorithm");
            bfs.addActionListener(this::algoButtonsActionPerformed);
            algoGroup.add(bfs);

            aStar = new JRadioButton("A*");
            aStar.setToolTipText("A* algorithm");
            aStar.addActionListener(this::algoButtonsActionPerformed);
            algoGroup.add(aStar);

            greedy = new JRadioButton("Greedy");
            greedy.setToolTipText("Greedy search algorithm");
            greedy.addActionListener(this::algoButtonsActionPerformed);
            algoGroup.add(greedy);

            dijkstra = new JRadioButton("Dijkstra");
            dijkstra.setToolTipText("Dijkstra's algorithm");
            dijkstra.addActionListener(this::algoButtonsActionPerformed);
            algoGroup.add(dijkstra);

            JPanel algoPanel = new JPanel();
            algoPanel.setBorder(javax.swing.BorderFactory.
                    createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(),
                    "Algorithms", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.TOP, new java.awt.Font("Helvetica", 0, 14)));
            
            dfs.setSelected(true);  // DFS is initially selected 
            
            diagonal = new
                    JCheckBox("Diagonal movements");
            diagonal.setToolTipText("Diagonal movements are also allowed");

            drawArrows = new
                    JCheckBox("Arrows to predecessors");
            drawArrows.setToolTipText("Draw arrows to predecessors");
            
            JLabel robot = new JLabel("Robot", JLabel.CENTER);
            robot.setForeground(Color.red);
            robot.setFont(new Font("Helvetica",Font.PLAIN,14));

            JLabel target = new JLabel("Target", JLabel.CENTER);
            target.setForeground(Color.GREEN);
            target.setFont(new Font("Helvetica",Font.PLAIN,14));
         
            JLabel frontier = new JLabel("Frontier", JLabel.CENTER);
            frontier.setForeground(Color.blue);
            frontier.setFont(new Font("Helvetica",Font.PLAIN,14));

            JLabel closed = new JLabel("Closed set", JLabel.CENTER);
            closed.setForeground(Color.CYAN);
            closed.setFont(new Font("Helvetica",Font.PLAIN,14));

            aboutButton = new JButton("About Maze");
            aboutButton.setBackground(Color.lightGray);
            aboutButton.addActionListener(this::aboutButtonActionPerformed);

            // we add the contents of the panel
            super.add(message);
            super.add(rowsLbl);
            super.add(rowsSpinner);
            super.add(columnsLbl);
            super.add(columnsSpinner);
            super.add(triangle);
            super.add(square);
            super.add(hexagon);
            super.add(shapePanel);
            super.add(resetButton);
            super.add(mazeButton);
            super.add(clearButton);
            super.add(realTimeButton);
            super.add(stepButton);
            super.add(animationButton);
            super.add(delayLbl);
            super.add(slider);
            super.add(dfs);
            super.add(iddfs);
            super.add(bfs);
            super.add(aStar);
            super.add(greedy);
            super.add(dijkstra);
            super.add(algoPanel);
            super.add(diagonal);
            super.add(drawArrows);
            super.add(robot);
            super.add(target);
            super.add(frontier);
            super.add(closed);
            super.add(aboutButton);

            // we regulate the sizes and positions
            message.setBounds(0, 515, 500, 23);
            rowsLbl.setBounds(520, 5, 130, 25);
            rowsSpinner.setBounds(655, 5, 35, 25);
            columnsLbl.setBounds(520, 35, 130, 25);
            columnsSpinner.setBounds(655, 35, 35, 25);

            shapePanel.setLocation(520,60);
            shapePanel.setSize(170, 70);
            triangle.setBounds(530, 80, 80, 25);
            square.setBounds(610, 80, 70, 25);
            hexagon.setBounds(530, 100, 80, 25);

            resetButton.setBounds(520, 135, 170, 25);
            mazeButton.setBounds(520, 165, 170, 25);
            clearButton.setBounds(520, 195, 170, 25);
            realTimeButton.setBounds(520, 225, 170, 25);
            stepButton.setBounds(520, 255, 170, 25);
            animationButton.setBounds(520, 285, 170, 25);
            delayLbl.setBounds(520, 315, 170, 10);
            slider.setBounds(520, 325, 170, 25);

            algoPanel.setLocation(520,350);
            algoPanel.setSize(170, 90);
            dfs.setBounds(530, 370, 70, 25);
            iddfs.setBounds(600, 370, 70, 25);
            bfs.setBounds(530, 390, 70, 25);
            aStar.setBounds(600, 390, 70, 25);
            greedy.setBounds(530, 410, 70, 25);
            dijkstra.setBounds(600, 410, 85, 25);

            diagonal.setBounds(520, 437, 170, 25);
            drawArrows.setBounds(520, 457, 170, 25);
            robot.setBounds(520, 475, 80, 25);
            target.setBounds(605, 475, 80, 25);
            frontier.setBounds(520, 495, 80, 25);
            closed.setBounds(605, 495, 80, 25);
            aboutButton.setBounds(520, 520, 170, 25);
            
            // we create the timer
            timer = new Timer(delay, action);
            
            // We attach to cells in the grid initial values.
            // Here is the first step of the algorithms
            initializeGrid(false);
            
        } // end constructor

        /**
         * Creates a new clean grid or a new maze
         */
        private void initializeGrid(Boolean makeMaze) {                                           
            rows    = (int)(rowsSpinner.getValue());
            columns = (int)(columnsSpinner.getValue());
            // the square maze must have an odd number of rows
            // the rows of the triangular maze must be at least 8 and a multiple of 4 
            if (makeMaze && (square.isSelected()? rows % 2 != 1 : rows % 4 != 0)){
                if (square.isSelected())
                    rows--;
                else
                    rows = Math.max((rows/4)*4,8);
                rowsSpinner.setValue(rows);
            }
            // a hexagonal grid must have an odd number of columns
            if (hexagon.isSelected() && columns % 2 != 1){
                columns--;
                columnsSpinner.setValue(columns);
            }
            // the columns of the triangular maze must be rows+1
            if (makeMaze && triangle.isSelected()){
                columns = rows + 1;
                columnsSpinner.setValue(columns);
            }
            // the columns of the square maze must be equal to rows
            if (makeMaze && square.isSelected()){
                columns = rows;
                columnsSpinner.setValue(columns);
            }
            grid = new int[rows][columns];
            centers = new Point[rows][columns];
            if (square.isSelected()){
                robotStart = new Cell(rows-2,1);
                targetPos = new Cell(1,columns-2);
            } else {
                robotStart = new Cell(rows-1,0);
                targetPos = new Cell(0,columns-1);
            }

            //  Calculation of the edge and the height of the triangular cell
            if (triangle.isSelected()){
                edge   = Math.min(500/(columns/2.0+1), 1000/(rows*Math.sqrt(3)));
                height = edge*Math.sqrt(3)/2;
                radius = height*2/3;
                arrowSize = (int)edge/4;
            }
            
            //  Calculation of the size of the square cell
            if (square.isSelected()){
                squareSize = 500/(rows > columns ? rows : columns);
                arrowSize = squareSize/2;
            }
            
            //  Calculation of the radius and the half height of the hexagonal cell
            if (hexagon.isSelected()){
                radius = Math.min(1000/(3.0*columns+1), 500/(rows*Math.sqrt(3)));
                height = radius*Math.sqrt(3)/2;
                arrowSize = (int)radius/2;
            }
            
            //  Calculation of the coordinates of the cells' centers
            int y=0;
            for (int r = 0; r < rows; r++)
                for (int c = 0; c < columns; c++){
                    if (triangle.isSelected()){
                        if ((c % 2 == 0 && r % 2 == 0) || (c % 2 != 0 && r % 2 != 0))
                            y = (int)Math.round(r*height+height/3);
                        if ((c % 2 == 0 && r % 2 != 0) || (c % 2 != 0 && r % 2 == 0))
                            y = (int)Math.round(r*height+height*2/3);
                        centers[r][c] = new Point(11+(int)Math.round(((c+1)/2.0)*edge),
                                                  11+y);
                    }
                    if (square.isSelected())
                        centers[r][c] = new Point(11+c*squareSize+squareSize/2,
                                                  11+r*squareSize+squareSize/2);
                    if (hexagon.isSelected())
                        if (c % 2 == 0)
                            centers[r][c] = new Point((int)Math.round(11+(c/2*3+1)*radius),
                                                      (int)Math.round(11+(r*2+1)*height));
                        else
                            centers[r][c] = new Point((int)Math.round(11+radius/2.0+(c/2*3+2)*radius),
                                                      (int)Math.round(11+2*(r+1)*height));
                }
            fillGrid();
            if (makeMaze) {
                if (square.isSelected()){
                    MazeGenerator maze = new MazeGenerator(rows/2,columns/2);
                    for (int r = 0; r < rows; r++)
                        for (int c = 0; c < columns; c++)
                            if (maze.maze_str.substring(r*columns+c, r*columns+c+1).matches(".*[+-|].*"))
                                grid[r][c] = OBST;
                } else {
                    MazeGenerator maze = new MazeGenerator(rows/4,columns/4);
                    int rows2 = rows/2+1;
                    int cols2 = columns/2+1;
                    for (int r1 = 0; r1 < rows2; r1++)
                        for (int c1 = 0; c1 < rows2; c1++){
                            if (maze.maze_str.substring(r1*cols2+c1, r1*cols2+c1+1).matches(".*[+-|].*")){
                                if (rows2-2+r1-c1 >= 0)
                                    grid[rows2-2+r1-c1][r1+c1] = OBST;  
                                if (rows2-1+r1-c1 < rows )
                                    grid[rows2-1+r1-c1][r1+c1] = OBST;
                            }
                        }
                    grid[robotStart.row][robotStart.col] = EMPTY;
                    grid[targetPos.row][targetPos.col] = EMPTY; 
                    robotStart.row = rows-2;
                    robotStart.col = columns/2;
                    targetPos.row = 1;
                    targetPos.col = columns/2;
                    grid[robotStart.row][robotStart.col] = ROBOT;
                    grid[targetPos.row][targetPos.col] = TARGET; 
                }
            }
            if (hexagon.isSelected())
                for (int c = 0; c < columns; c++)
                    if (c % 2 != 0)
                        grid[rows-1][c] = OBST;
        } // end initializeGrid()
        
        /**
         * Gives initial values ​​for the cells in the grid.
         */
        private void fillGrid() {
            /**
             * With the first click on button 'Clear' clears the data
             * of any search was performed (Frontier, Closed Set, Route)
             * and leaves intact the obstacles and the robot and target positions
             * in order to be able to run another algorithm
             * with the same data.
             * With the second click removes any obstacles also.
             */
            if (searching || endOfSearch){ 
                for (int r = 0; r < rows; r++)
                    for (int c = 0; c < columns; c++) {
                        if (grid[r][c] == FRONTIER || grid[r][c] == CLOSED || grid[r][c] == ROUTE)
                            grid[r][c] = EMPTY;
                        if (grid[r][c] == ROBOT)
                            robotStart = new Cell(r,c);
                        if (grid[r][c] == TARGET)
                            targetPos = new Cell(r,c);
                    }
                searching = false;
            } else {
                for (int r = 0; r < rows; r++)
                    for (int c = 0; c < columns; c++)
                        grid[r][c] = EMPTY;
                if (square.isSelected()){
                    robotStart = new Cell(rows-2,1);
                    targetPos = new Cell(1,columns-2);
                } else {
                    robotStart = new Cell(rows-1,0);
                    targetPos = new Cell(0,columns-1);
                }
            }
            if (aStar.isSelected() || greedy.isSelected()){
                robotStart.g = 0;
                robotStart.h = 0;
                robotStart.f = 0;
            }
            expanded = 0;
            found = false;
            searching = false;
            endOfSearch = false;
         
            // The first step of the other four algorithms is here
            // 1. OPEN SET: = [So], CLOSED SET: = []
            openSet.removeAll(openSet);
            openSet.add(robotStart);
            closedSet.removeAll(closedSet);
         
            grid[targetPos.row][targetPos.col] = TARGET; 
            grid[robotStart.row][robotStart.col] = ROBOT;
            message.setText(MSG_DRAW_AND_SELECT);
            timer.stop();
            repaint();
            
        } // end fillGrid()

        /**
         * Enables radio buttons and checkboxes
         */
        private void enableRadiosAndChecks() {                                           
            slider.setEnabled(true);
            dfs.setEnabled(true);
            bfs.setEnabled(true);
            aStar.setEnabled(true);
            greedy.setEnabled(true);
            dijkstra.setEnabled(true);
            diagonal.setEnabled(true);
            drawArrows.setEnabled(true);
        } // end enableRadiosAndChecks()
    
        /**
         * Disables radio buttons and checkboxes
         */
        private void disableRadiosAndChecks() {                                           
            slider.setEnabled(false);
            dfs.setEnabled(false);
            bfs.setEnabled(false);
            aStar.setEnabled(false);
            greedy.setEnabled(false);
            dijkstra.setEnabled(false);
            diagonal.setEnabled(false);
            drawArrows.setEnabled(false);
        } // end disableRadiosAndChecks()
        
        /**
         * Executes when the user selects the shape of the cell
         */
        private void radioButtonsActionPerformed(java.awt.event.ActionEvent evt) {                                           
            animation = false;
            realTime = false;
            realTimeButton.setEnabled(true);
            realTimeButton.setForeground(Color.black);
            stepButton.setEnabled(true);
            animationButton.setEnabled(true);
            enableRadiosAndChecks();
            initializeGrid(false);
            if (square.isSelected()){
                mazeButton.setEnabled(true);
                diagonal.setEnabled(true);
            } else {
                mazeButton.setEnabled(triangle.isSelected() ? true : false);
                diagonal.setSelected(false);
                diagonal.setEnabled(false);
            }
            if (iddfs.isSelected()) {
                stepButton.setEnabled(false);
                animationButton.setEnabled(false);
            } else {
                stepButton.setEnabled(true);
                animationButton.setEnabled(true);
            }
            
        } // end radioButtonsActionPerformed()
    
        /**
         * Executes when the user selects an Algorithm
         */
        private void algoButtonsActionPerformed(java.awt.event.ActionEvent evt) {                                           
            if (iddfs.isSelected()) {
                stepButton.setEnabled(false);
                animationButton.setEnabled(false);
            } else {
                stepButton.setEnabled(true);
                animationButton.setEnabled(true);
            }
            
        } // end algoButtonsActionPerformed()
    
        /**
         * Executes when the user presses the button "New Grid"
         */
        private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {                                           
            animation = false;
            realTime = false;
            realTimeButton.setEnabled(true);
            realTimeButton.setForeground(Color.black);
            stepButton.setEnabled(true);
            animationButton.setEnabled(true);
            enableRadiosAndChecks();
            initializeGrid(false);
            if (triangle.isSelected() || hexagon.isSelected())
                diagonal.setEnabled(false);
            if (iddfs.isSelected()){
                stepButton.setEnabled(false);
                animationButton.setEnabled(false);
            }
        } // end resetButtonActionPerformed()
    
        /**
         * Executes when the user presses the button "Maze"
         */
        private void mazeButtonActionPerformed(java.awt.event.ActionEvent evt) {
            animation = false;
            realTime = false;
            realTimeButton.setEnabled(true);
            realTimeButton.setForeground(Color.black);
            stepButton.setEnabled(true);
            animationButton.setEnabled(true);
            enableRadiosAndChecks();
            initializeGrid(true);
            if (triangle.isSelected())
                diagonal.setEnabled(false);
            if (iddfs.isSelected()){
                stepButton.setEnabled(false);
                animationButton.setEnabled(false);
            }
        } // end mazeButtonActionPerformed()
    
        /**
         * Executes when the user presses the button "Clear"
         */
        private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {
            animation = false;
            realTime = false;
            realTimeButton.setEnabled(true);
            realTimeButton.setForeground(Color.black);
            stepButton.setEnabled(true);
            animationButton.setEnabled(true);
            enableRadiosAndChecks();
            fillGrid();
            if (triangle.isSelected() || hexagon.isSelected())
                diagonal.setEnabled(false);
            if (iddfs.isSelected()){
                stepButton.setEnabled(false);
                animationButton.setEnabled(false);
            }
        } // end clearButtonActionPerformed()
    
        /**
         * Executes when the user presses the button "Real-Time"
         */
        private void realTimeButtonActionPerformed(java.awt.event.ActionEvent evt) {
            if (realTime)
                return;
            realTime = true;
            searching = true;
            // The Dijkstra's initialization should be done just before the
            // start of search, because obstacles must be in place.
            if (dijkstra.isSelected())
               initializeDijkstra();
            realTimeButton.setForeground(Color.red);
            disableRadiosAndChecks();
            realTimeAction();
        } // end realTimeButtonActionPerformed()
    
        /**
         * Action performed during real-time search
         */
        public void realTimeAction() {
            if (iddfs.isSelected())
                IDDFS(robotStart, 1000);
            else
                do
                    checkTermination();
                while (!endOfSearch);
        } // end of realTimeAction()

        /**
         * Executes when the user presses the button "Step-by-Step"
         */
        private void stepButtonActionPerformed(java.awt.event.ActionEvent evt) {
            animation = false;
            timer.stop();
            if (found || endOfSearch)
                return;
            if (!searching && dijkstra.isSelected())
                initializeDijkstra();
            searching = true;
            message.setText(MSG_SELECT_STEP_BY_STEP_ETC);
            realTimeButton.setEnabled(false);
            disableRadiosAndChecks();
            slider.setEnabled(true);
            checkTermination();
            repaint();
        } // end stepButtonActionPerformed()
    
        /**
         * Executes when the user presses the button "Animation"
         */
        private void animationButtonActionPerformed(java.awt.event.ActionEvent evt) {
            animation = true;
            if (!searching && dijkstra.isSelected())
                initializeDijkstra();
            searching = true;
            message.setText(MSG_SELECT_STEP_BY_STEP_ETC);
            realTimeButton.setEnabled(false);
            disableRadiosAndChecks();
            slider.setEnabled(true);
            delay = slider.getValue();
            timer.setDelay(delay);
            timer.start();
        } // end animationButtonActionPerformed()
    
        /**
         * Executes when the user presses the button "About Maze"
         */
        private void aboutButtonActionPerformed(java.awt.event.ActionEvent evt) {
            AboutBox aboutBox = new AboutBox(mazeFrame,true);
            aboutBox.setVisible(true);
        } // end aboutButtonActionPerformed()
    
        /**
         * Checks if we have reached the end of search
         */
        public void checkTermination() {
            // Here we decide whether we can continue the search or not.
            
            // In the case of Dijkstra's algorithm
            // here we check condition of step 11:
            // 11. while Q is not empty.
            
            // In the case of DFS, BFS, A* and Greedy algorithms
            // here we have the second step:
            // 2. If OPEN SET = [], then terminate. There is no solution.
            if ((dijkstra.isSelected() && graph.isEmpty()) ||
                          (!dijkstra.isSelected() && openSet.isEmpty())) {
                endOfSearch = true;
                grid[robotStart.row][robotStart.col]=ROBOT;
                message.setText(MSG_NO_SOLUTION);
                stepButton.setEnabled(false);
                animationButton.setEnabled(false);
                repaint();
            } else {
                expandNode();
                if (found) {
                    endOfSearch = true;
                    plotRoute();
                    stepButton.setEnabled(false);
                    animationButton.setEnabled(false);
                    slider.setEnabled(false);
                    repaint();
                }
            }
        } // end checkTermination()

        /**
         * Expands a node and creates his successors
         */
        private void expandNode(){
            // Dijkstra's algorithm to handle separately
            if (dijkstra.isSelected()){
                Cell u;
                // 11: while Q is not empty:
                if (graph.isEmpty())
                    return;
                // 12:  u := vertex in Q (graph) with smallest distance in dist[] ;
                // 13:  remove u from Q (graph);
                u = graph.remove(0);
                // Add vertex u in closed set
                closedSet.add(u);
                // If target has been found ...
                if (u.row == targetPos.row && u.col == targetPos.col){
                    found = true;
                    return;
                }
                // Counts nodes that have expanded.
                expanded++;
                // Update the color of the cell
                grid[u.row][u.col] = CLOSED;
                // 14: if dist[u] = infinity:
                if (u.dist == INFINITY){
                    // ... then there is no solution.
                    // 15: break;
                    return;
                } // 16: end if
                // Create the neighbors of u
                ArrayList<Cell> neighbors = createSuccesors(u, false);
                // 18: for each neighbor v of u:
                for (Cell v: neighbors) {
                    // 20: alt := dist[u] + dist_between(u, v) ;
                    double alt = u.dist + distBetween(u,v);
                    // 21: if alt < dist[v]:
                    if (alt < v.dist) {
                        // 22: dist[v] := alt ;
                        v.dist = alt;
                        // 23: previous[v] := u ;
                        v.prev = u;
                        // Update the color of the cell
                        grid[v.row][v.col] = FRONTIER;
                        // 24: decrease-key v in Q;
                        // (sort list of nodes with respect to dist)
                        Collections.sort(graph, new CellComparatorByDist());
                    }
                }
            } else { // The handling of the other four algorithms
                Cell current;
                if (dfs.isSelected() || bfs.isSelected()) {
                    // Here is the 3rd step of the algorithms DFS and BFS
                    // 3. Remove the first state, Si, from OPEN SET ...
                    current = openSet.remove(0);
                } else {
                    // Here is the 3rd step of the algorithms A* and Greedy
                    // 3. Remove the first state, Si, from OPEN SET,
                    // for which f(Si) ≤ f(Sj) for all other
                    // open states Sj  ...
                    // (sort first OPEN SET list with respect to 'f')
                    Collections.sort(openSet, new CellComparatorByF());
                    current = openSet.remove(0);
                }
                // ... and add it to CLOSED SET.
                closedSet.add(0,current);
                // Update the color of the cell
                grid[current.row][current.col] = CLOSED;
                // If the selected node is the target ...
                if (current.row == targetPos.row && current.col == targetPos.col) {
                    // ... then terminate etc
                    Cell last = targetPos;
                    last.prev = current.prev;
                    closedSet.add(last);
                    found = true;
                    return;
                }
                // Count nodes that have been expanded.
                expanded++;
                // Here is the 4rd step of the algorithms
                // 4. Create the successors of Si, based on actions
                //    that can be implemented on Si.
                //    Each successor has a pointer to the Si, as its predecessor.
                //    In the case of DFS and BFS algorithms, successors should not
                //    belong neither to the OPEN SET nor the CLOSED SET.
                ArrayList<Cell> successors;
                successors = createSuccesors(current, false);
                // Here is the 5th step of the algorithms
                // 5. For each successor of Si, ...
                for (Cell cell: successors){
                    // ... if we are running DFS ...
                    if (dfs.isSelected()) {
                        // ... add the successor at the beginning of the list OPEN SET
                        openSet.add(0, cell);
                        // Update the color of the cell
                        grid[cell.row][cell.col] = FRONTIER;
                        // ... if we are runnig BFS ...
                    } else if (bfs.isSelected()){
                        // ... add the successor at the end of the list OPEN SET
                        openSet.add(cell);
                        // Update the color of the cell
                        grid[cell.row][cell.col] = FRONTIER;
                        // ... if we are running A* or Greedy algorithms (step 5 of A* algorithm) ...
                    } else if (aStar.isSelected() || greedy.isSelected()){
                        // ... calculate the value f(Sj) ...
                        double dxg = centers[current.row][current.col].getX() - centers[cell.row][cell.col].getX();
                        double dyg = centers[current.row][current.col].getY() - centers[cell.row][cell.col].getY();
                        double dxh = centers[targetPos.row][targetPos.col].getX() - centers[cell.row][cell.col].getX();
                        double dyh = centers[targetPos.row][targetPos.col].getY() - centers[cell.row][cell.col].getY();
                        if (diagonal.isSelected() || triangle.isSelected()|| hexagon.isSelected()){
                            // with diagonal movements or triangular or hexagonal cells
                            // calculate the Euclidean distance
                            if (greedy.isSelected()) {
                                // especially for the Greedy ...
                                cell.g = 0;
                            } else {
                                cell.g = current.g + Math.hypot(dxg, dyg);
                            }
                            cell.h = Math.hypot(dxh, dyh);
                        } else {
                            // othewise
                            // calculate the Manhattan distance
                            if (greedy.isSelected()) {
                                // especially for the Greedy ...
                                cell.g = 0;
                            } else {
                                cell.g = current.g + Math.abs(dxg) + Math.abs(dyg);
                            }
                            cell.h = Math.abs(dxh) + Math.abs(dyh);
                        }
                        cell.f = cell.g+cell.h;
                        // ... If Sj is neither in the OPEN SET nor in the CLOSED SET states ...
                        int openIndex   = isInList(openSet,cell);
                        int closedIndex = isInList(closedSet,cell);
                        if (openIndex == -1 && closedIndex == -1) {
                            // ... then add Sj in the OPEN SET ...
                            // ... evaluated as f(Sj)
                            openSet.add(cell);
                            // Update the color of the cell
                            grid[cell.row][cell.col] = FRONTIER;
                            // Else ...
                        } else {
                            // ... if already belongs to the OPEN SET, then ...
                            if (openIndex > -1){
                                // ... compare the new value assessment with the old one. 
                                // If old <= new ...
                                if (openSet.get(openIndex).f <= cell.f) {
                                    // ... then eject the new node with state Sj.
                                    // (ie do nothing for this node).
                                    // Else, ...
                                } else {
                                    // ... remove the element (Sj, old) from the list
                                    // to which it belongs ...
                                    openSet.remove(openIndex);
                                    // ... and add the item (Sj, new) to the OPEN SET.
                                    openSet.add(cell);
                                    // Update the color of the cell
                                    grid[cell.row][cell.col] = FRONTIER;
                                }
                                // ... if already belongs to the CLOSED SET, then ...
                            } else {
                                // ... compare the new value assessment with the old one. 
                                // If old <= new ...
                                if (closedSet.get(closedIndex).f <= cell.f) {
                                    // ... then eject the new node with state Sj.
                                    // (ie do nothing for this node).
                                    // Else, ...
                                } else {
                                    // ... remove the element (Sj, old) from the list
                                    // to which it belongs ...
                                    closedSet.remove(closedIndex);
                                    // ... and add the item (Sj, new) to the OPEN SET.
                                    openSet.add(cell);
                                    // Update the color of the cell
                                    grid[cell.row][cell.col] = FRONTIER;
                                }
                            }
                        }
                    }
                }
            }
        } //end expandNode()
        
        /**
         * Creates three, four, six or eight successors of a cell
         * 
         * @param current       the cell for which we ask successors
         * @param makeConnected flag that indicates that we are interested only on the coordinates
         *                      of cells and not on the label 'dist' (concerns only Dijkstra's)
         * @return              the successors of the cell as a list
         */
        private ArrayList<Cell> createSuccesors(Cell current, boolean makeConnected){
            int r = current.row;
            int c = current.col;
            // We create an empty list for the successors of the current cell.
            ArrayList<Cell> temp = new ArrayList<>();
            // With triangular cells pointing up the priority is:
            // 1: Up-right 2: Down 3: Up-left
            // With triangular cells pointing down the priority is:
            // 1: Up 2: Down-right 3: Down-left

            // With square cells and diagonal movements the priority is:
            // 1: Up 2: Up-right 3: Right 4: Down-right
            // 5: Down 6: Down-left 7: Left 8: Up-left
            
            // With square cells without diagonal movements the priority is:
            // 1: Up 2: Right 3: Down 4: Left
            
            // With hexagonal cells the priority is:
            // 1: Up 2: Up-right 3: Down-right
            // 4: Down 5: Down-left 6: Up-left
            
            // If not at the topmost limit of the grid
            // and the up-side cell is not an obstacle ...
            if (r > 0 && grid[r-1][c] != OBST && (triangle.isSelected() ?
                    ((r % 2 == 0 && c % 2 == 0) || (r % 2 != 0 && c % 2 != 0)) : true) &&
                    // ... and (only in the case DFS or BFS is running)
                    // not already belongs neither to the OPEN SET nor to the CLOSED SET ...
                    ((dfs.isSelected() || bfs.isSelected()) ?
                          isInList(openSet,new Cell(r-1,c)) == -1 &&
                          isInList(closedSet,new Cell(r-1,c)) == -1 : true)) {
                Cell cell = new Cell(r-1,c);
                // In the case of Dijkstra's algorithm we can not append to
                // the list of successors the "naked" cell we have just created.
                // The cell must be accompanied by the label 'dist',
                // so we need to track it down through the list 'graph'
                // and then copy it back to the list of successors.
                // The flag makeConnected is necessary to be able
                // the present method createSuccesors() to collaborate
                // with the method findConnectedComponent(), which creates
                // the connected component when Dijkstra's initializes.
                if (dijkstra.isSelected()){
                    if (makeConnected)
                        temp.add(cell);
                    else {
                        int graphIndex = isInList(graph,cell);
                        if (graphIndex > -1)
                            temp.add(graph.get(graphIndex));
                    }
                } else {
                    // ... update the pointer of the up-side cell so it points the current one ...
                    cell.prev = current;
                    // ... and add the up-side cell to the successors of the current one. 
                    temp.add(cell);
                 }
            }
            if (!triangle.isSelected() && (diagonal.isSelected() || (hexagon.isSelected() && c % 2 == 0))){
                // If we are not even at the topmost nor at the rightmost border of the grid
                // and the up-right-side cell is not an obstacle ...
                if (r > 0 && c < columns-1 && grid[r-1][c+1] != OBST &&
                        // ... and (only in case of squareGrid)
                        // one of the upper side or right side cells are not obstacles ...
                        // (because it is not reasonable to allow the robot to pass through a "slot")                        
                        (square.isSelected() ? (grid[r-1][c] != OBST || grid[r][c+1] != OBST) : true) &&
                        // ... and (only in the case DFS or BFS is running)
                        // not already belongs neither to the OPEN SET nor CLOSED SET ...
                        ((dfs.isSelected() || bfs.isSelected()) ?
                              isInList(openSet,new Cell(r-1,c+1)) == -1 &&
                              isInList(closedSet,new Cell(r-1,c+1)) == -1 : true)) {
                    Cell cell = new Cell(r-1,c+1);
                    if (dijkstra.isSelected()){
                        if (makeConnected)
                            temp.add(cell);
                        else {
                            int graphIndex = isInList(graph,cell);
                            if (graphIndex > -1)
                                temp.add(graph.get(graphIndex));
                        }
                    } else {
                        // ... update the pointer of the up-right-side cell so it points the current one ...
                        cell.prev = current;
                        // ... and add the up-right-side cell to the successors of the current one. 
                        temp.add(cell);
                    }
                }
            }
            // If not at the rightmost limit of the grid
            // and the right-side cell is not an obstacle ...
            if (c < columns-1 && grid[r][c+1] != OBST && 
                    // ... and (only in the case DFS or BFS is running)
                    // not already belongs neither to the OPEN SET nor to the CLOSED SET ...
                    ((dfs.isSelected() || bfs.isSelected()) ?
                          isInList(openSet,new Cell(r,c+1)) == -1 &&
                          isInList(closedSet,new Cell(r,c+1)) == -1 : true)) {
                Cell cell = new Cell(r,c+1);
                if (dijkstra.isSelected()){
                    if (makeConnected)
                        temp.add(cell);
                    else {
                        int graphIndex = isInList(graph,cell);
                        if (graphIndex > -1)
                            temp.add(graph.get(graphIndex));
                    }
                } else {
                    // ... update the pointer of the right-side cell so it points the current one ...
                    cell.prev = current;
                    // ... and add the right-side cell to the successors of the current one. 
                    temp.add(cell);
                }
            }
            if (!triangle.isSelected() && (diagonal.isSelected() || (hexagon.isSelected() && c % 2 != 0))){
                // If we are not even at the lowermost nor at the rightmost border of the grid
                // and the down-right-side cell is not an obstacle ...
                if (r < rows-1 && c < columns-1 && grid[r+1][c+1] != OBST &&
                        // ... and one of the down-side or right-side cells are not obstacles ...
                        (square.isSelected() ? (grid[r+1][c] != OBST || grid[r][c+1] != OBST) : true) &&
                        // ... and (only in the case DFS or BFS is running)
                        // not already belongs neither to the OPEN SET nor to the CLOSED SET ...
                        ((dfs.isSelected() || bfs.isSelected()) ?
                              isInList(openSet,new Cell(r+1,c+1)) == -1 &&
                              isInList(closedSet,new Cell(r+1,c+1)) == -1 : true)) {
                    Cell cell = new Cell(r+1,c+1);
                    if (dijkstra.isSelected()){
                        if (makeConnected)
                            temp.add(cell);
                        else {
                            int graphIndex = isInList(graph,cell);
                            if (graphIndex > -1)
                                temp.add(graph.get(graphIndex));
                        }
                    } else {
                        // ... update the pointer of the downr-right-side cell so it points the current one ...
                        cell.prev = current;
                        // ... and add the down-right-side cell to the successors of the current one. 
                        temp.add(cell);
                    }
                }
            }
            // If not at the lowermost limit of the grid
            // and the down-side cell is not an obstacle ...
            if (r < rows-1 && grid[r+1][c] != OBST && (triangle.isSelected() ?
                    ((r % 2 == 0 && c % 2 != 0) || (r % 2 != 0 && c % 2 == 0)) : true) &&
                    // ... and (only in the case DFS or BFS is running)
                    // not already belongs neither to the OPEN SET nor to the CLOSED SET ...
                    ((dfs.isSelected() || bfs.isSelected()) ?
                          isInList(openSet,new Cell(r+1,c)) == -1 &&
                          isInList(closedSet,new Cell(r+1,c)) == -1 : true)) {
                Cell cell = new Cell(r+1,c);
                if (dijkstra.isSelected()){
                    if (makeConnected)
                        temp.add(cell);
                    else {
                        int graphIndex = isInList(graph,cell);
                        if (graphIndex > -1)
                            temp.add(graph.get(graphIndex));
                    }
                } else {
                   // ... update the pointer of the down-side cell so it points the current one ...
                    cell.prev = current;
                    // ... and add the down-side cell to the successors of the current one. 
                    temp.add(cell);
                }
            }
            if (!triangle.isSelected() && (diagonal.isSelected()  || (hexagon.isSelected() && c % 2 != 0))){
                // If we are not even at the lowermost nor at the leftmost border of the grid
                // and the down-left-side cell is not an obstacle ...
                if (r < rows-1 && c > 0 && grid[r+1][c-1] != OBST &&
                        // ... and one of the down-side or left-side cells are not obstacles ...
                        (square.isSelected() ? (grid[r+1][c] != OBST || grid[r][c-1] != OBST) : true) &&
                        // ... and (only in the case DFS or BFS is running)
                        // not already belongs neither to the OPEN SET nor to the CLOSED SET ...
                        ((dfs.isSelected() || bfs.isSelected()) ? 
                              isInList(openSet,new Cell(r+1,c-1)) == -1 &&
                              isInList(closedSet,new Cell(r+1,c-1)) == -1 : true)) {
                    Cell cell = new Cell(r+1,c-1);
                    if (dijkstra.isSelected()){
                        if (makeConnected)
                            temp.add(cell);
                        else {
                            int graphIndex = isInList(graph,cell);
                            if (graphIndex > -1)
                                temp.add(graph.get(graphIndex));
                        }
                    } else {
                        // ... update the pointer of the down-left-side cell so it points the current one ...
                        cell.prev = current;
                        // ... and add the down-left-side cell to the successors of the current one. 
                        temp.add(cell);
                    }
                }
            }
            // If not at the leftmost limit of the grid
            // and the left-side cell is not an obstacle ...
            if (c > 0 && grid[r][c-1] != OBST && 
                    // ... and (only in the case DFS or BFS is running)
                    // not already belongs neither to the OPEN SET nor to the CLOSED SET ...
                    ((dfs.isSelected() || bfs.isSelected()) ?
                          isInList(openSet,new Cell(r,c-1)) == -1 &&
                          isInList(closedSet,new Cell(r,c-1)) == -1 : true)) {
                Cell cell = new Cell(r,c-1);
                if (dijkstra.isSelected()){
                    if (makeConnected)
                        temp.add(cell);
                    else {
                        int graphIndex = isInList(graph,cell);
                        if (graphIndex > -1)
                            temp.add(graph.get(graphIndex));
                    }
                } else {
                   // ... update the pointer of the left-side cell so it points the current one ...
                    cell.prev = current;
                    // ... and add the left-side cell to the successors of the current one. 
                    temp.add(cell);
                }
            }
            if (!triangle.isSelected() && (diagonal.isSelected() || (hexagon.isSelected() && c % 2 == 0))){
                // If we are not even at the topmost nor at the leftmost border of the grid
                // and the up-left-side cell is not an obstacle ...
                if (r > 0 && c > 0 && grid[r-1][c-1] != OBST &&
                        // ... and one of the up-side or left-side cells are not obstacles ...
                        (square.isSelected() ? (grid[r-1][c] != OBST || grid[r][c-1] != OBST) : true) &&
                        // ... and (only in the case DFS or BFS is running)
                        // not already belongs neither to the OPEN SET nor to the CLOSED SET ...
                        ((dfs.isSelected() || bfs.isSelected()) ?
                              isInList(openSet,new Cell(r-1,c-1)) == -1 &&
                              isInList(closedSet,new Cell(r-1,c-1)) == -1 : true)) {
                    Cell cell = new Cell(r-1,c-1);
                    if (dijkstra.isSelected()){
                        if (makeConnected)
                            temp.add(cell);
                        else {
                            int graphIndex = isInList(graph,cell);
                            if (graphIndex > -1)
                                temp.add(graph.get(graphIndex));
                        }
                    } else {
                        // ... update the pointer of the up-left-side cell so it points the current one ...
                        cell.prev = current;
                        // ... and add the up-left-side cell to the successors of the current one. 
                        temp.add(cell);
                    }
                }
            }
            // When DFS algorithm is in use, cells are added one by one at the beginning of the
            // OPEN SET list. Because of this, we must reverse the order of successors formed,
            // so the successor corresponding to the heightest priority, to be placed
            // the first in the list.
            // For the Greedy, A* and Dijkstra's no issue, because the list is sorted
            // according to 'f' or 'dist' before extracting the first element of.
            if (dfs.isSelected())
                Collections.reverse(temp);
            
            return temp;
        } // end createSuccesors()
        
        /**
         * Returns the distance between two cells
         *
         * @param u the first cell
         * @param v the other cell
         * @return  the distance between the cells u and v
         */
        private double distBetween(Cell u, Cell v){
            double dist;
            double dx = centers[u.row][u.col].getX() - centers[v.row][v.col].getX();
            double dy = centers[u.row][u.col].getY() - centers[v.row][v.col].getY();
            if (diagonal.isSelected() || triangle.isSelected() || hexagon.isSelected()){
                // with diagonal movements or triangular cells or hexagonal cells
                // calculate the Euclidean distance
                dist = Math.hypot(dx, dy);
            } else {
                // otherwise
                // calculate the Manhattan distance
                dist = Math.abs(dx) + Math.abs(dy);
            }
            return dist;
        } // end distBetween()
        
        /**
         * Returns the index of the cell 'current' in the list 'list'
         *
         * @param list    the list in which we seek
         * @param current the cell we are looking for
         * @return        the index of the cell in the list
         *                if the cell is not found returns -1
         */
        private int isInList(ArrayList<Cell> list, Cell current){
            int index = -1;
            for (int i = 0 ; i < list.size(); i++) {
                Cell listItem = list.get(i);
                if (current.row == listItem.row && current.col == listItem.col) {
                    index = i;
                    break;
                }
            }
            return index;
        } // end isInList()
        
        /**
         * Returns the predecessor of cell 'current' in list 'list'
         *
         * @param list      the list in which we seek
         * @param current   the cell we are looking for
         * @return          the predecessor of cell 'current'
         */
        private Cell findPrev(ArrayList<Cell> list, Cell current){
            int index = isInList(list, current);
            Cell listItem = list.get(index);
            return listItem.prev;
        } // end findPrev()
        
        /**
         * Calculates the path from the target to the initial position
         * of the robot, counts the corresponding steps
         * and measures the distance travelled.
         */
        private void plotRoute(){
            int steps = 0;
            double distance = 0;
            int index = isInList(closedSet,targetPos);
            Cell cur = closedSet.get(index);
            grid[cur.row][cur.col]= TARGET;
            do {
                steps++;
                double dx = centers[cur.row][cur.col].getX() - centers[cur.prev.row][cur.prev.col].getX();
                double dy = centers[cur.row][cur.col].getY() - centers[cur.prev.row][cur.prev.col].getY();
                distance += Math.hypot(dx, dy);
                cur = cur.prev;
                grid[cur.row][cur.col] = ROUTE;
            } while (!(cur.row == robotStart.row && cur.col == robotStart.col));
            grid[robotStart.row][robotStart.col]=ROBOT;
            String msg;
            if (iddfs.isSelected())
                msg = String.format("Steps = Depth: %d, Distance: %.1f",
                        steps,distance); 
            else
                msg = String.format("Nodes expanded: %d, Steps: %d, Distance: %.1f",
                        expanded,steps,distance); 
                
            message.setText(msg);
          
        } // end plotRoute()
        
        /**
         * Appends to the list containing the nodes of the graph only
         * the cells belonging to the same connected component with node v.
         * This is a Breadth First Search of the graph starting from node v.
         *
         * @param v    the starting node
         */
        private void findConnectedComponent(Cell v){
            Stack<Cell> stack;
            stack = new Stack();
            ArrayList<Cell> successors;
            stack.push(v);
            graph.add(v);
            while(!stack.isEmpty()){
                v = stack.pop();
                successors = createSuccesors(v, true);
                for (Cell c: successors)
                    if (isInList(graph, c) == -1){
                        stack.push(c);
                        graph.add(c);
                    }
            }
        } // end findConnectedComponent()
        
        /**
         * Initialization of Dijkstra's algorithm
         */
        private void initializeDijkstra() {
            /**
             * When one thinks of Wikipedia pseudocode, observe that the
             * algorithm is still looking for his target while there are still
             * nodes in the queue Q.
             * Only when we run out of queue and the target has not been found,
             * can answer that there is no solution .
             * As is known, the algorithm models the problem as a connected graph.
             * It is obvious that no solution exists only when the graph is not
             * connected and the target is in a different connected component
             * of this initial position of the robot.
             * To be thus possible negative response from the algorithm,
             * should search be made ONLY in the coherent component to which the
             * initial position of the robot belongs.
             */
            
            // First create the connected component
            // to which the initial position of the robot belongs.
            graph.removeAll(graph);
            findConnectedComponent(robotStart);
            // Here is the initialization of Dijkstra's algorithm 
            // 2: for each vertex v in Graph;
            for (Cell v: graph) {
                // 3: dist[v] := infinity ;
                v.dist = INFINITY;
                // 5: previous[v] := undefined ;
                v.prev = null;
            }
            // 8: dist[source] := 0;
            graph.get(isInList(graph,robotStart)).dist = 0;
            // 9: Q := the set of all nodes in Graph;
            // Instead of the variable Q we will use the list
            // 'graph' itself, which has already been initialised.            

            // Sorts the list of nodes with respect to 'dist'.
            Collections.sort(graph, new CellComparatorByDist());
            // Initializes the list of closed nodes
            closedSet.removeAll(closedSet);
        } // end initializeDijkstra()

        /**
         * Repaints the grid
         */
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.lightGray);
            // Fills the background color.
            if (triangle.isSelected())
                g.fillRect(10, 10, (int)((columns/2.0+0.5)*edge + 1), (int)(rows*height + 1));
            if (square.isSelected())
                g.fillRect(10, 10, columns*squareSize + 1, rows*squareSize + 1);
            if (hexagon.isSelected())
                g.fillRect(10, 10, (int)((columns-1)/2*3*radius + 2*radius), (int)(rows*2*height + 1));   
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < columns; c++) {
                    if (grid[r][c] == EMPTY) {
                        g.setColor(Color.WHITE);
                    } else if (grid[r][c] == ROBOT) {
                        g.setColor(Color.RED);
                    } else if (grid[r][c] == TARGET) {
                        g.setColor(Color.GREEN);
                    } else if (grid[r][c] == OBST) {
                        g.setColor(Color.BLACK);
                    } else if (grid[r][c] == FRONTIER) {
                        g.setColor(Color.BLUE);
                    } else if (grid[r][c] == CLOSED) {
                        g.setColor(Color.CYAN);
                    } else if (grid[r][c] == ROUTE) {
                        g.setColor(Color.YELLOW);
                    }
                    if (triangle.isSelected())
                        g.fillPolygon(calcTriangle(r,c));
                    if (square.isSelected())
                        g.fillPolygon(calcSquare(r,c));
                    if (hexagon.isSelected())
                        if ((c % 2 == 0) || (!(c % 2 == 0) && (r < rows - 1)))
                            g.fillPolygon(calcHexagon(r,c));
                }
            }
            
            if (drawArrows.isSelected()) {
                // We draw all arrows from each open or closed state
                // to its predecessor.
                for (int r = 0; r < rows; r++)
                    for (int c = 0; c < columns; c++)
                        // If the current cell is the goal and the solution has been found,
                        // or belongs in the route to the target,
                        // or is an open state,
                        // or is a closed state but not the initial position of the robot
                        if ((grid[r][c] == TARGET && found)  || grid[r][c] == ROUTE  || 
                                grid[r][c] == FRONTIER || (grid[r][c] == CLOSED &&
                                !(r == robotStart.row && c == robotStart.col))){
                            // The tail of the arrow is the current cell, while
                            // the arrowhead is the predecessor cell.
                            Cell head;
                            if (grid[r][c] == FRONTIER)
                                if (dijkstra.isSelected())
                                    head = findPrev(graph,new Cell(r,c));
                                else
                                    head = findPrev(openSet,new Cell(r,c));
                            else
                                head = findPrev(closedSet,new Cell(r,c));
                            
                            // The center of the current cell
                            Point arrowTail = centers[r][c];
                            // The center of the predecessor cell
                            Point arrowHead = centers[head.row][head.col];
                            int thickness = arrowSize >= 10 ? 2 : 1;
                            
                            // If the current cell is the target
                            // or belongs to the path to the target ...
                            if (grid[r][c] == TARGET  || grid[r][c] == ROUTE){
                                // ... draw a red arrow directing to the target.
                                g.setColor(Color.RED);
                                drawArrow(g,thickness,arrowTail,arrowHead);
                            // Else ...
                            } else {
                                // ... draw a black arrow to the predecessor cell.
                                g.setColor(Color.BLACK);
                                drawArrow(g,thickness,arrowHead,arrowTail);
                            }
                        }
            }
        } // end paintComponent()
        
        /**
         * Returns the triangle representing a cell
         *
         * @param r   the row of the cell
         * @param c   the row of the cell
         * @return    the Polygon corresponding to the cell
         */
        private Polygon calcTriangle(int r, int c){
            if (((c % 2 == 0) && (r % 2 == 0)) || ((c % 2 != 0) && (r % 2 != 0))) {
                int xPoly[] = {
                    (int)(11 + (c/2+0.0)*edge + (r % 2 != 0 ? edge/2 : 0) + 1),
                    (int)(11 + (c/2+1.0)*edge + (r % 2 != 0 ? edge/2 : 0) - 1),
                    (int)(11 + (c/2+0.5)*edge + (r % 2 != 0 ? edge/2 : 0) + 0)};
                int yPoly[] = {
                    (int)(11 +     r*height + 0),
                    (int)(11 +     r*height + 0),
                    (int)(11 + (r+1)*height - 1)};
                return new Polygon(xPoly,yPoly,3);
            } else {
                int xPoly[] = {
                    (int)(11 + (c/2+1.0)*edge - (r % 2 != 0 ? edge/2 : 0) + 0),
                    (int)(11 + (c/2+1.5)*edge - (r % 2 != 0 ? edge/2 : 0) - 1),
                    (int)(11 + (c/2+0.5)*edge - (r % 2 != 0 ? edge/2 : 0) + 1)};
                int yPoly[] = {
                    (int)(11 +     r*height + 1),
                    (int)(11 + (r+1)*height - 1),
                    (int)(11 + (r+1)*height - 1)};
                return new Polygon(xPoly,yPoly,3);
            }
        } // end calcTriangle()

        /**
         * Returns the square representing a cell
         *
         * @param r   the row of the cell
         * @param c   the row of the cell
         * @return    the Polygon corresponding to the cell
         */
        private Polygon calcSquare(int r, int c){
            int xPoly[] = {
                (int)(11 +     c*squareSize + 0),
                (int)(11 + (c+1)*squareSize - 1),
                (int)(11 + (c+1)*squareSize - 1),
                (int)(11 +     c*squareSize + 0)};
            int yPoly[] = {
                (int)(11 +     r*squareSize + 0),
                (int)(11 +     r*squareSize + 0),
                (int)(11 + (r+1)*squareSize - 1),
                (int)(11 + (r+1)*squareSize - 1)};
            return new Polygon(xPoly,yPoly,4);
        } // end calcSquare()

        /**
         * Returns the hexagon representing a cell
         *
         * @param r   the row of the cell
         * @param c   the row of the cell
         * @return    the Polygon corresponding to the cell
         */
        private Polygon calcHexagon(int r, int c){
            if ((c % 2) == 0) {
                int xPoly[] = {
                    (int)(10 +            radius/2 + c/2*3*radius + 0),
                    (int)(10 +   radius + radius/2 + c/2*3*radius + 0),
                    (int)(10 + 2*radius +            c/2*3*radius - 1),
                    (int)(10 +   radius + radius/2 + c/2*3*radius + 0),
                    (int)(10 +            radius/2 + c/2*3*radius + 0),
                    (int)(10 +                       c/2*3*radius + 1)};
                int yPoly[] = {
                    (int)(10 +            r*2*height + 1),
                    (int)(10 +            r*2*height + 1),
                    (int)(10 +   height + r*2*height + 0),
                    (int)(10 + 2*height + r*2*height + 0),
                    (int)(10 + 2*height + r*2*height + 0),
                    (int)(10 +   height + r*2*height + 0)};
                return new Polygon(xPoly,yPoly,6);
            } else {
                int xPoly[] = {
                    (int)(10 + 2*radius +            c/2*3*radius + 0),
                    (int)(10 + 3*radius +            c/2*3*radius + 0),
                    (int)(10 + 3*radius + radius/2 + c/2*3*radius - 1),
                    (int)(10 + 3*radius +            c/2*3*radius + 0),
                    (int)(10 + 2*radius +            c/2*3*radius + 0),
                    (int)(10 +   radius + radius/2 + c/2*3*radius + 1)};
                int yPoly[] = {
                    (int)(10 +   height + r*2*height + 1),
                    (int)(10 +   height + r*2*height + 1),
                    (int)(10 + 2*height + r*2*height + 0),
                    (int)(10 + 3*height + r*2*height + 0),
                    (int)(10 + 3*height + r*2*height + 0),
                    (int)(10 + 2*height + r*2*height + 0)};
                return new Polygon(xPoly,yPoly,6);
            }
        } // end calcHexagon()

        /**
         * Draws an arrow from point p2 to point p1
         */
        private void drawArrow(Graphics g1, int thickness, Point p1, Point p2) {
            Graphics2D g = (Graphics2D) g1.create();

            double dx = p2.getX() - p1.getX();
            double dy = p2.getY() - p1.getY();
            double angle = Math.atan2(dy, dx);
            int len = (int) Math.hypot(dx, dy);
            AffineTransform at = AffineTransform.getTranslateInstance(p1.getX(), p1.getY());
            at.concatenate(AffineTransform.getRotateInstance(angle));
            g.transform(at);

            // We draw an horizontal arrow 'len' in length
            // that ends at the point (0,0) with two tips 'arrowSize' in length
            // which form 20 degrees angles with the axis of the arrow ...
            g.setStroke(new BasicStroke(thickness));
            g.drawLine(0, 0, len, 0);
            g.drawLine(0, 0, (int)(arrowSize * Math.sin(70 * Math.PI / 180)) , (int)(arrowSize  *Math.cos(70 * Math.PI / 180)));
            g.drawLine(0, 0, (int)(arrowSize * Math.sin(70 * Math.PI / 180)) ,-(int)(arrowSize * Math.cos(70 * Math.PI / 180)));
            // ... and class AffineTransform handles the rest !!!!!!
        } // end drawArrow()
        
        // The following code is due to Vamsi Sangam 
        // http://theoryofprogramming.com/2018/01/14/iterative-deepening-depth-first-search-iddfs/
        // See also: https://en.wikipedia.org/wiki/Iterative_deepening_depth-first_search
        
        public boolean IDDFS(Cell node, int maxDepth) {
            searching = true;
            for (int depth = 0; depth <= maxDepth; ++depth) {
                closedSet.clear();  
                // closedSet is the set of nodes visited so far.
                // Because the search tree varies as the algorithm goes on,
                // only the necessary nodes must be kept in the closedSet.
                // By doing so the closedSet is reduced just to the path from robot to target.

                //System.out.print("Depth = " + depth + ", DLS Traversal => ");
                if (DLS(node, depth)) {
                    //System.out.println();
                    return true;
                }
                //System.out.println();
            }
            //System.out.println("not found");
            message.setText(MSG_NO_SOLUTION);
            return false;
        } // end of IDDFS()

        // Depth limited search method
        public boolean DLS(Cell node, int depth) {
            closedSet.add(node);
            //System.out.print("("+node.row+" "+node.col+")");

            if (node.row == targetPos.row && node.col == targetPos.col) {
                endOfSearch = true;
                found = true;
                closedSet.add(node);
                plotRoute();
                repaint();
                //System.out.println();
                //System.out.println("found");
                return true;
            }

            if (depth == 0) {
                // remove the current node from the closedSet, if the goal has not been reached yet
                closedSet.remove(isInList(closedSet,node));
                return false;
            }

            ArrayList<Cell> successors = createSuccesors(node, false);
            
            for (Cell adjacentNode : successors) {
                if (isInList(closedSet,adjacentNode) == -1) {
                    // if the current adjacentNode does not belong to the closedSet
                    if (DLS(adjacentNode, depth - 1)) {
                        return true;
                    }
                    int index = isInList(closedSet,adjacentNode);
                    if (index != -1)
                        // if the current adjacentNode belongs to the closedSet,
                        // remove it from the set before examining the next one
                        closedSet.remove(index);
                }
            }
            return false;
        } // end of DLS()

    } // end nested classs MazePanel
  
} // end class Maze80b1