from tkinter import *
from tkinter import font
from tkinter import messagebox
from functools import partial
from operator import attrgetter
from random import shuffle, randrange
import webbrowser
import numpy
import math
import os
 
"""
@author Nikos Kanargias
E-mail: nkana@tee.gr
@version 7.1
 
The software solves and visualizes the robot motion planning problem,
by implementing variants of DFS, BFS and A* algorithms, as described
by E. Keravnou in her book: "Artificial Intelligence and Expert Systems",
Hellenic Open University,  Patra 2000 (in Greek)
as well as the Greedy search algorithm, as a special case of A*.
 
The software also implements Dijkstra's algorithm,
as just described in the relevant article in Wikipedia.
http://en.wikipedia.org/wiki/Dijkstra%27s_algorithm
 
The superiority of  A* and Dijkstra's algorithms against the other three becomes obvious.
 
The user can change the number of the grid cells, indicating
the desired number of rows and columns.
 
The user can add as many obstacles he/she wants, as he/she
would "paint" free curves with a drawing program.
 
Individual obstacles can be removed by clicking them.
 
The position of the robot and/or the target can be changed by dragging with the mouse.
 
Jump from search in "Step-by-Step" way to "Animation" way and vice versa is done
by pressing the corresponding button, even when the search is in progress.
 
The speed of a search can be changed, even if the search is in progress.
It is sufficient to place the slider "Delay" in the new desired position
and then press the "Animation" button.
 
The application considers that the robot itself has some volume.
Therefore it can’t move diagonally to a free cell passing between two obstacles
adjacent to one apex.
 
When 'Step-by-Step' or 'Animation' search is underway it is not possible to change the position of obstacles,
robot and target, as well as the search algorithm.
 
When 'Real-Time' search is underway the position of obstacles, robot and target can be changed.
 
Drawing of arrows to predecessors, when requested, is performed only at the end of the search.
"""
 
 
class Maze71:
 
    class CreateToolTip(object):
        """
        Helper class that creates a tooltip for a given widget
        """
        # from https://stackoverflow.com/questions/3221956/what-is-the-simplest-way-to-make-tooltips-in-tkinter
        def __init__(self, widget, text='widget info'):
            self.waittime = 500  # milliseconds
            self.wraplength = 180  # pixels
            self.widget = widget
            self.text = text
            self.widget.bind("<Enter>", self.enter)
            self.widget.bind("<Leave>", self.leave)
            self.widget.bind("<ButtonPress>", self.leave)
            self._id = None
            self.tw = None
 
        def enter(self, event=None):
            self.schedule()
 
        def leave(self, event=None):
            self.unschedule()
            self.hidetip()
 
        def schedule(self):
            self.unschedule()
            self._id = self.widget.after(self.waittime, self.showtip)
 
        def unschedule(self):
            _id = self._id
            self._id = None
            if _id:
                self.widget.after_cancel(_id)
 
        def showtip(self, event=None):
            x, y, cx, cy = self.widget.bbox("insert")
            x += self.widget.winfo_rootx() + 25
            y += self.widget.winfo_rooty() + 20
            # creates a toplevel window
            self.tw = Toplevel(self.widget)
            # Leaves only the label and removes the app window
            self.tw.wm_overrideredirect(True)
            self.tw.wm_geometry("+%d+%d" % (x, y))
            label = Label(self.tw, text=self.text, justify='left', background="#ffffff",
                          relief='solid', borderwidth=1, wraplength=self.wraplength)
            label.pack(ipadx=1)
 
        def hidetip(self):
            tw = self.tw
            self.tw = None
            if tw:
                tw.destroy()
 
    class Cell(object):
        """
        Helper class that represents the cell of the grid
        """
 
        def __init__(self, row, col):
            self.row = row  # the row number of the cell(row 0 is the top)
            self.col = col  # the column number of the cell (column 0 is the left)
            self.g = 0      # the value of the function g of A* and Greedy algorithms
            self.h = 0      # the value of the function h of A* and Greedy algorithms
            self.f = 0      # the value of the function f of A* and Greedy algorithms
            # the distance of the cell from the initial position of the robot
            # Ie the label that updates the Dijkstra's algorithm
            self.dist = 0
            # Each state corresponds to a cell
            # and each state has a predecessor which
            # stored in this variable
            self.prev = self.__class__
 
        def __eq__(self, other):
            """
            useful Cell equivalence
            """
            if isinstance(other, self.__class__):
                return self.row == other.row and self.col == other.col
            else:
                return False
 
    class Point(object):
        """
        Helper class that represents the point on the grid
        """
 
        def __init__(self, x, y):
            self.X = x
            self.Y = y
 
        def get_x(self):
            return self.X
 
        def get_y(self):
            return self.Y
 
    #######################################
    #                                     #
    #      Constants of Maze71 class      #
    #                                     #
    #######################################
    INFINITY = sys.maxsize  # The representation of the infinite
    EMPTY = 0       # empty cell
    OBST = 1        # cell with obstacle
    ROBOT = 2       # the position of the robot
    TARGET = 3      # the position of the target
    FRONTIER = 4    # cells that form the frontier (OPEN SET)
    CLOSED = 5      # cells that form the CLOSED SET
    ROUTE = 6       # cells that form the robot-to-target path
 
    MSG_DRAW_AND_SELECT = "\"Paint\" obstacles, then click 'Real-Time' or 'Step-by-Step' or 'Animation'"
    MSG_SELECT_STEP_BY_STEP_ETC = "Click 'Step-by-Step' or 'Animation' or 'Clear'"
    MSG_NO_SOLUTION = "There is no path to the target !!!"
 
    def __init__(self, maze):
        """
        Constructor
        """
        self.center(maze)
 
        self.rows = 0               # the number of rows of the grid
        self.columns = 0            # the number of columns of the grid
        self.square_size = 0        # the cell size in pixels
        self.arrow_size = 0         # the size of the tips of the arrow pointing the predecessor cell
 
        self.radius = 0.0           # the radius of triangular and hexagonal cells
        self.height = 0.0           # half the height of hexagonal cells or the height of triangular cells
        self.edge = 0.0             # the edge of the triangular cell
 
        self.openSet = []           # the OPEN SET
        self.closedSet = []         # the CLOSED SET
        self.graph = []             # the set of vertices of the graph to be explored by Dijkstra's algorithm
 
        self.robotStart = self.Cell(self.rows - 2, 1)    # the initial position of the robot
        self.targetPos = self.Cell(1, self.columns - 2)  # the position of the target
 
        self.grid = [[]]            # the grid
        self.centers = [[self.Point(0, 0) for c in range(83)] for r in range(83)]  # the centers of the cells
        self.dist = self.INFINITY   # distance of the point the user clicked on the canvas from the centre of some cell
        self.realTime = False       # Solution is displayed instantly
        self.found = False          # flag that the goal was found
        self.searching = False      # flag that the search is in progress
        self.endOfSearch = False    # flag that the search came to an end
        self.animation = False      # flag that the animation is running
        self.delay = 500            # time delay of animation (in msec)
        self.expanded = 0           # the number of nodes that have been expanded
        self.shape = "Square"       # Square is initially selected
        self.algorithm = "DFS"      # DFS is initially selected
 
        self.array = numpy.array([0] * (83 * 83))
        self.cur_row = self.cur_col = self.cur_val = 0
        app_highlight_font = font.Font(app, family='Helvetica', size=9, weight='bold')
 
        ##########################################
        #                                        #
        #   the widgets of the user interface    #
        #                                        #
        ##########################################
        self.message = Label(app, text=self.MSG_DRAW_AND_SELECT, width=55, anchor='center',
                             font=('Helvetica', 12), fg="BLUE")
        self.message.place(x=5, y=510)
 
        rows_lbl = Label(app, text="# of rows (5-83):", width=16, anchor='e', font=("Helvetica", 9))
        rows_lbl.place(x=530, y=5)
 
        validate_rows_cmd = (app.register(self.validate_rows), '%P')
        invalid_rows_cmd = (app.register(self.invalid_rows))
 
        self.rows_var = StringVar()
        self.rows_var.set(41)
        self.rowsSpinner = Spinbox(app, width=3, from_=5, to=83, textvariable=self.rows_var, validate='focus',
                                   validatecommand=validate_rows_cmd, invalidcommand=invalid_rows_cmd)
        self.rowsSpinner.place(x=652, y=5)
 
        cols_lbl = Label(app, text="# of columns (5-83):", width=16, anchor='e', font=("Helvetica", 9))
        cols_lbl.place(x=530, y=25)
 
        validate_cols_cmd = (app.register(self.validate_cols), '%P')
        invalid_cols_cmd = (app.register(self.invalid_cols))
 
        self.cols_var = StringVar()
        self.cols_var.set(41)
        self.colsSpinner = Spinbox(app, width=3, from_=5, to=83, textvariable=self.cols_var, validate='focus',
                                   validatecommand=validate_cols_cmd, invalidcommand=invalid_cols_cmd)
        self.colsSpinner.place(x=652, y=25)
 
        self.shape_frame = LabelFrame(app, text="Shape of Cell", width=170, height=45+20)
        self.shape_frame.place(x=515, y=45)
        self.shape_var = StringVar()
        self.shape_buttons = list()
        shape_tool_tips = ("Trigonal Cell",
                           "Square Cell",
                           "Hexagonal Cell")
        for i, shape in enumerate(("Triangle", "Square", "Hexagon")):
            btn = Radiobutton(self.shape_frame, text=shape,  variable=self.shape_var, font=app_highlight_font,
                              value=i + 1, command=partial(self.select_shape, shape))
            btn.place(x=5 if i % 2 == 0 else 90, y=int(i/2)*20)
            self.CreateToolTip(btn, shape_tool_tips[i])
            btn.deselect()
            self.shape_buttons.append(btn)
        self.shape_buttons[1].select()  # Square cell is initially selected
 
        self.buttons = list()
        buttons_tool_tips = ("Clears and redraws the grid according to the given rows and columns",
                             "Creates a random maze",
                             "First click: clears search, Second click: clears obstacles",
                             "Position of obstacles, robot and target can be changed when search is underway",
                             "The search is performed step-by-step for every click",
                             "The search is performed automatically")
        for i, action in enumerate(("New grid", "Maze", "Clear", "Real-Time", "Step-by-Step", "Animation")):
            btn = Button(app, text=action, width=23, font=app_highlight_font,  bg="light grey",
                         command=partial(self.select_action, action))
            btn.place(x=515, y=113+30*i)
            self.CreateToolTip(btn, buttons_tool_tips[i])
            self.buttons.append(btn)
 
        time_delay = Label(app, text="Delay (msec)", width=27, anchor='center', font=("Helvetica", 8))
        time_delay.place(x=515, y=290)
        slider_value = IntVar()
        slider_value.set(500)
        self.slider = Scale(app, orient=HORIZONTAL, length=165, width=10, from_=0, to=1000,
                            showvalue=1, variable=slider_value,)
        self.slider.place(x=515, y=305)
        self.CreateToolTip(self.slider, "Regulates the delay for each step (0 to 1000 msec)")
 
        self.algo_frame = LabelFrame(app, text="Algorithms", width=170, height=80)
        self.algo_frame.place(x=515, y=345)
        self.algo_buttons = list()
        algo_tool_tips = ("Depth First Search algorithm",
                          "Breadth First Search algorithm",
                          "A* algorithm",
                          "Greedy search algorithm",
                          "Dijkstra's algorithm")
        for i, algorithm in enumerate(("DFS", "BFS", "A*", "Greedy", "Dijkstra")):
            btn = Radiobutton(self.algo_frame, text=algorithm,  font=app_highlight_font, value=i + 1,
                              command=partial(self.select_algo, algorithm))
            btn.place(x=10 if i % 2 == 0 else 90, y=int(i/2)*18)
            self.CreateToolTip(btn, algo_tool_tips[i])
            btn.deselect()
            self.algo_buttons.append(btn)
        self.algo_buttons[0].select()  # DFS is initially selected
 
        self.diagonal = IntVar()
        self.diagonalBtn = Checkbutton(app, text="Diagonal movements", font=app_highlight_font,
                                       variable=self.diagonal)
        self.diagonalBtn.place(x=515, y=425)
        self.CreateToolTip(self.diagonalBtn, "Diagonal movements are also allowed")
 
        self.drawArrows = IntVar()
        self.drawArrowsBtn = Checkbutton(app, text="Arrows to predecessors", font=app_highlight_font,
                                         variable=self.drawArrows)
        self.drawArrowsBtn.place(x=515, y=445)
        self.CreateToolTip(self.drawArrowsBtn, "Draw arrows to predecessors")
 
        memo_colors = ("RED", "GREEN", "BLUE", "CYAN")
        for i, memo in enumerate(("Robot", "Target", "Frontier", "Closed set")):
            label = Label(app, text=memo,  width=8, anchor='center', fg=memo_colors[i], font=("Helvetica", 11))
            label.place(x=515 if i % 2 == 0 else 605, y=466+int(i/2)*20)
 
        self.about_button = Button(app, text='About Maze', width=23, font=app_highlight_font, bg="light grey",
                                   command=self.about_click)
        self.about_button.place(x=515, y=510)
 
        self.canvas = Canvas(app, bd=0, highlightthickness=0)
        self.canvas.bind("<Button-1>", self.left_click)
        self.canvas.bind("<B1-Motion>", self.drag)
 
        self.initialize_grid(False)
 
    def validate_rows(self, entry):
        """
        Validates entry in rowsSpinner
 
        :param entry: the value entered by the user
        :return:      True, if entry is valid
        """
        try:
            value = int(entry)
            valid = value in range(5, 84)
        except ValueError:
            valid = False
        if not valid:
            app.bell()
            # The following line is due to user PRMoureu of stackoverflow. See:
            # https://stackoverflow.com/questions/46861236/widget-validation-in-tkinter/46863849?noredirect=1#comment80675412_46863849
            self.rowsSpinner.after_idle(lambda: self.rowsSpinner.config(validate='focusout'))
        return valid
 
    def invalid_rows(self):
        """
        Sets default value to rowsSpinner in case of invalid entry
        """
        self.rows_var.set(41)
 
    def validate_cols(self, entry):
        """
        Validates entry in colsSpinner
 
        :param entry: the value entered by the user
        :return:      True, if entry is valid
        """
        try:
            value = int(entry)
            valid = value in range(5, 84)
        except ValueError:
            valid = False
        if not valid:
            app.bell()
            self.colsSpinner.after_idle(lambda: self.colsSpinner.config(validate='focusout'))
        return valid
 
    def invalid_cols(self):
        """
        Sets default value to colsSpinner in case of invalid entry
        """
        self.cols_var.set(41)
 
    def select_shape(self, shape):
        self.shape = shape
        self.animation = False
        self.realTime = False
        for but in self.buttons:
            but.configure(state="normal")
        self.buttons[3].configure(fg="BLACK")  # Real-Time button
        self.slider.configure(state="normal")
        for but in self.algo_buttons:
            but.configure(state="normal")
        self.initialize_grid(False)
        self.drawArrowsBtn.configure(state="normal")
        if self.shape == "Square":
            self.diagonalBtn.configure(state="normal")
        else:
            if self.shape == "Hexagon":
                self.buttons[1].configure(state="disabled")  # Maze button
            self.diagonalBtn.deselect()
            self.diagonalBtn.configure(state="disabled")
 
    def select_action(self, action):
        if action == "New grid":
            self.reset_click()
        elif action == "Maze":
            self.maze_click()
        elif action == "Clear":
            self.clear_click()
        elif action == "Real-Time":
            self.real_time_click()
        elif action == "Step-by-Step":
            self.step_by_step_click()
        elif action == "Animation":
            self.animation_click()
 
    def select_algo(self, algorithm):
        self.algorithm = algorithm
 
    def find_row_col(self, cur_y, cur_x):
        """
        Decides on which cell the mouse is pointing to.
 
        :param cur_y: the y coordinate of the canvas
        :param cur_x: the x coordinate of the canvas
        :return:      the cell of the grid that is pointed to
       """
        row = col = 0
        self.dist = self.INFINITY
        if self.shape == "Square":
            row = int(cur_y/self.square_size)
            col = int(cur_x/self.square_size)
        else:
            for r in range(self.rows):
                for c in range(self.columns):
                    cur_dist = math.hypot(cur_x-self.centers[r][c].get_x(), cur_y-self.centers[r][c].get_y())
                    if cur_dist < min(self.dist, self.radius):
                        self.dist = cur_dist
                        row = r
                        col = c
        return self.Cell(row, col)
 
    def left_click(self, event):
        """
        Handles clicks of left mouse button as we add or remove obstacles
        """
        cur_cell = self.find_row_col(event.y, event.x)
        row = cur_cell.row
        col = cur_cell.col
 
        if row in range(self.rows) and col in range(self.columns) if self.shape == "Square" else\
                self.dist < self.INFINITY:
            if True if self.realTime else (not self.found and not self.searching):
                if self.realTime:
                    self.fill_grid()
                self.cur_row = row
                self.cur_col = col
                self.cur_val = self.grid[row][col]
                if self.cur_val == self.EMPTY:
                    self.grid[row][col] = self.OBST
                    self.paint_cell(row, col, "BLACK")
                if self.cur_val == self.OBST:
                    self.grid[row][col] = self.EMPTY
                    self.paint_cell(row, col, "WHITE")
                if self.realTime and self.algorithm == "Dijkstra":
                    self.initialize_dijkstra()
            if self.realTime:
                self.real_time_action()
 
    def drag(self, event):
        """
        Handles mouse movements as we "paint" obstacles or move the robot and/or target.
        """
        cur_cell = self.find_row_col(event.y, event.x)
        row = cur_cell.row
        col = cur_cell.col
 
        if row in range(self.rows) and col in range(self.columns) if self.shape == "Square" else self.dist < self.INFINITY:
            if True if self.realTime else (not self.found and not self.searching):
                if self.realTime:
                    self.fill_grid()
                if self.Cell(row, col) != self.Cell(self.cur_row, self.cur_col) and\
                        self.cur_val in [self.ROBOT, self.TARGET]:
                    new_val = self.grid[row][col]
                    if new_val == self.EMPTY:
                        self.grid[row][col] = self.cur_val
                        if self.cur_val == self.ROBOT:
                            self.grid[self.robotStart.row][self.robotStart.col] = self.EMPTY
                            self.paint_cell(self.robotStart.row, self.robotStart.col, "WHITE")
                            self.robotStart.row = row
                            self.robotStart.col = col
                            self.grid[self.robotStart.row][self.robotStart.col] = self.ROBOT
                            self.paint_cell(self.robotStart.row, self.robotStart.col, "RED")
                        else:
                            self.grid[self.targetPos.row][self.targetPos.col] = self.EMPTY
                            self.paint_cell(self.targetPos.row, self.targetPos.col, "WHITE")
                            self.targetPos.row = row
                            self.targetPos.col = col
                            self.grid[self.targetPos.row][self.targetPos.col] = self.TARGET
                            self.paint_cell(self.targetPos.row, self.targetPos.col, "GREEN")
                        self.cur_row = row
                        self.cur_col = col
                        self.cur_val = self.grid[row][col]
                elif self.grid[row][col] != self.ROBOT and self.grid[row][col] != self.TARGET:
                    self.grid[row][col] = self.OBST
                    self.paint_cell(row, col, "BLACK")
                if self.realTime and self.algorithm == "Dijkstra":
                    self.initialize_dijkstra()
            if self.realTime:
                self.real_time_action()
 
    @staticmethod
    def make_maze(w, h):
        """
        Creates a random, perfect (without cycles) maze
        From http://rosettacode.org/wiki/Maze_generation
 
        recursive backtracking algorithm
 
        :param w:   the width of the maze
        :param h:   the height of the maze
 
        :return:    the maze as a string
        """
        vis = [[0] * w + [1] for _ in range(h)] + [[1] * (w + 1)]
        ver = [["| "] * w + ['|'] for _ in range(h)] + [[]]
        hor = [["+-"] * w + ['+'] for _ in range(h + 1)]
 
        def walk(x, y):
            vis[y][x] = 1
 
            d = [(x - 1, y), (x, y + 1), (x + 1, y), (x, y - 1)]
            shuffle(d)
            for (xx, yy) in d:
                if vis[yy][xx]:
                    continue
                if xx == x:
                    hor[max(y, yy)][x] = "+ "
                if yy == y:
                    ver[y][max(x, xx)] = "  "
                walk(xx, yy)
 
        walk(randrange(w), randrange(h))
 
        s = ""
        for (a, b) in zip(hor, ver):
            s += ''.join(a + b)
        return s
 
    def initialize_grid(self, make_maze):
        """
        Creates a new clean grid or a new maze
 
        :param make_maze: flag that indicates the creation of a random maze
        """
        self.rows = int(self.rowsSpinner.get())
        self.columns = int(self.colsSpinner.get())
        # the square maze must have an odd number of rows
        # the rows of the triangular maze must be at least 8 and a multiple of 4
        if make_maze and (self.rows % 2 != 1 if self.shape == "Square" else self.rows % 4 !=0):
            if self.shape == "Square":
                self.rows -= 1
            else:
                self.rows = max(int(self.rows/4)*4,8)
            self.rows_var.set(self.rows)
        # a hexagonal grid must have an odd number of columns
        if self.shape == "Hexagon" and self.columns % 2 != 1:
            self.columns -= 1
            self.cols_var.set(self.columns)
        # the columns of the triangular maze must be rows+1
        if make_maze and self.shape == "Triangle":
            self.columns = self.rows + 1
            self.cols_var.set(self.columns)
        # the columns of the square maze must be equal to rows
        if make_maze and self.shape == "Square":
            self.columns = self.rows
            self.cols_var.set(self.columns)
 
        self.grid = self.array[:self.rows*self.columns]
        self.grid = self.grid.reshape(self.rows, self.columns)
 
        # Calculation of the edge and the height of the triangular cell
        if self.shape == "Triangle":
            self.edge = min(500 / (int(self.columns/2) + 1), 1000 / (self.rows * math.sqrt(3)))
            self.height = self.edge * math.sqrt(3) / 2
            self.radius = self.height*2/3
            self.arrow_size = int(self.edge / 4)
 
        # Calculation of the size of the square cell
        if self.shape == "Square":
            self.square_size = int(500 / (self.rows if self.rows > self.columns else self.columns))
            self.arrow_size = int(self.square_size / 2)
 
        # Calculation of the radius and the half height of the hexagonal cell
        if self.shape == "Hexagon":
            self.radius = min(1000 / (3 * self.columns + 1), 500 / (self.rows * math.sqrt(3)))
            self.height = self.radius * math.sqrt(3) / 2
            self.arrow_size = int(self.radius / 2)
 
        # Creation of the canvas' background
        if self.shape == "Triangle":
            self.canvas.configure(width=(self.columns/2 + 0.5) * self.edge + 1, height=self.rows * self.height + 1)
            self.canvas.place(x=10, y=10)
            self.canvas.create_rectangle(0, 0, (self.columns/2 + 0.5) * self.edge + 1,
                                         self.rows * self.height + 1, width=0, fill="DARK GREY")
        if self.shape == "Square":
            self.canvas.configure(width=self.columns * self.square_size + 1, height=self.rows * self.square_size + 1)
            self.canvas.place(x=10, y=10)
            self.canvas.create_rectangle(0, 0, self.columns*self.square_size+1,
                                         self.rows*self.square_size+1, width=0, fill="DARK GREY")
        if self.shape == "Hexagon":
            self.canvas.configure(width=(self.columns-1)/2*3*self.radius + 2*self.radius,
                                  height=self.rows*2*self.height + 1)
            self.canvas.place(x=10, y=10)
            self.canvas.create_rectangle(0, 0, (self.columns-1)/2*3*self.radius + 2*self.radius,
                                         self.rows*2*self.height + 1, width=0, fill="DARK GREY")
 
        for r in range(self.rows):
            for c in list(range(self.columns)):
                self.grid[r][c] = self.EMPTY
        if self.shape == "Square":
            self.robotStart = self.Cell(self.rows-2, 1)
            self.targetPos = self.Cell(1, self.columns-2)
        else:
            self.robotStart = self.Cell(self.rows-1, 0)
            self.targetPos = self.Cell(0, self.columns-1)
 
        # Calculation of the coordinates of the cells' centers
        y = 0
        for r in range(self.rows):
            for c in range(self.columns):
                if self.shape == "Triangle":
                    if (c % 2 == 0 and r % 2 == 0) or (c % 2 != 0 and r % 2 != 0):
                        y = round(r*self.height + self.height/3)
                    if (c % 2 == 0 and r % 2 != 0) or (c % 2 != 0 and r % 2 == 0):
                        y = round(r * self.height + self.height*2/3)
                    self.centers[r][c] = self.Point(round((c + 1) / 2.0 * self.edge), y)
                if self.shape == "Square":
                    self.centers[r][c] = self.Point(c*self.square_size+self.square_size/2,
                                                    r*self.square_size+self.square_size/2)
                if self.shape == "Hexagon":
                    if c % 2 == 0:
                        self.centers[r][c] = self.Point(round((int(c/2)*3+1)*self.radius),
                                                        round((r*2+1)*self.height))
                    else:
                        self.centers[r][c] = self.Point(round(self.radius/2+(int(c/2)*3+2)*self.radius),
                                                        round(2*(r+1)*self.height))
        self.fill_grid()
        if make_maze:
            if self.shape == "Square":
                maze = self.make_maze(int(self.rows / 2), int(self.columns / 2))
                for r in range(self.rows):
                    for c in range(self.columns):
                        if maze[r * self.columns + c : r * self.columns + c + 1] in "|-+":
                            self.grid[r][c] = self.OBST
            else:
                maze = self.make_maze(int(self.rows / 4), int(self.columns / 4))
                rows2 = int(self.rows / 2) + 1
                cols2 = int(self.columns / 2) + 1
                for r1 in range(rows2):
                    for c1 in range(cols2):
                        if maze[r1 * cols2 + c1 : r1 * cols2 + c1 + 1] in "|-+":
                            if rows2-2+r1-c1 >= 0:
                                self.grid[rows2-2+r1-c1][r1+c1] = self.OBST
                            if rows2 - 1 + r1 - c1 < self.rows:
                                self.grid[rows2 - 1 + r1 - c1][r1 + c1] = self.OBST
                self.grid[self.robotStart.row][self.robotStart.col] = self.EMPTY
                self.grid[self.targetPos.row][self.targetPos.col] = self.EMPTY
                self.robotStart.row = self.rows-2
                self.robotStart.col = int(self.columns/2)
                self.targetPos.row = 1
                self.targetPos.col = int(self.columns/2)
                self.grid[self.robotStart.row][self.robotStart.col] = self.ROBOT
                self.grid[self.targetPos.row][self.targetPos.col] = self.TARGET
        if self.shape == "Hexagon":
            for c in range(self.columns):
                if c % 2 != 0:
                    self.grid[self.rows-1][c] = self.OBST
        self.repaint()
 
    def fill_grid(self):
        """
        Gives initial values ​​for the cells in the grid.
        """
        # With the first click on button 'Clear' clears the data
        # of any search was performed (Frontier, Closed Set, Route)
        # and leaves intact the obstacles and the robot and target positions
        # in order to be able to run another algorithm
        # with the same data.
        # With the second click removes any obstacles also.
        if self.searching or self.endOfSearch:
            for r in range(self.rows):
                for c in range(self.columns):
                    if self.grid[r][c] in [self.FRONTIER, self.CLOSED, self.ROUTE]:
                        self.grid[r][c] = self.EMPTY
                    if self.grid[r][c] == self.ROBOT:
                        self.robotStart = self.Cell(r, c)
            self.searching = False
        else:
            for r in range(self.rows):
                for c in range(self.columns):
                    self.grid[r][c] = self.EMPTY
            if self.shape == "Square":
                self.robotStart = self.Cell(self.rows - 2, 1)
                self.targetPos = self.Cell(1, self.columns - 2)
            else:
                self.robotStart = self.Cell(self.rows - 1, 0)
                self.targetPos = self.Cell(0, self.columns - 1)
        if self.algorithm in ["A*", "Greedy"]:
            self.robotStart.g = 0
            self.robotStart.h = 0
            self.robotStart.f = 0
        self.expanded = 0
        self.found = False
        self.searching = False
        self.endOfSearch = False
 
        self.openSet.clear()
        self.closedSet.clear()
        self.openSet = [self.robotStart]
        self.closedSet = []
 
        self.grid[self.targetPos.row][self.targetPos.col] = self.TARGET
        self.grid[self.robotStart.row][self.robotStart.col] = self.ROBOT
        self.message.configure(text=self.MSG_DRAW_AND_SELECT)
 
        self.repaint()
 
    def repaint(self):
        """
        Repaints the grid
        """
        color = ""
        for r in range(self.rows):
            for c in range(self.columns):
                if self.grid[r][c] == self.EMPTY:
                    color = "WHITE"
                elif self.grid[r][c] == self.ROBOT:
                    color = "RED"
                elif self.grid[r][c] == self.TARGET:
                    color = "GREEN"
                elif self.grid[r][c] == self.OBST:
                    color = "BLACK"
                elif self.grid[r][c] == self.FRONTIER:
                    color = "BLUE"
                elif self.grid[r][c] == self.CLOSED:
                    color = "CYAN"
                elif self.grid[r][c] == self.ROUTE:
                    color = "YELLOW"
                self.paint_cell(r, c, color)
 
    def paint_cell(self, row, col, color):
        """
        Paints a particular cell
 
        :param row:   the row of the cell
        :param col:   the column of the cell
        :param color: the color of the cell
        """
        if self.shape == "Triangle":
            self.canvas.create_polygon(self.calc_triangle(row, col), width=0, fill=color)
        if self.shape == "Square":
            self.canvas.create_polygon(self.calc_square(row, col), width=0, fill=color)
        if self.shape == "Hexagon":
            if col % 2 == 0 or (col % 2 != 0 and row < self.rows - 1):
                self.canvas.create_polygon(self.calc_hexagon(row, col), width=0, fill=color)
 
    def calc_triangle(self, r, c):
        """
        Calculates the coordinates of the vertices of the square corresponding to a particular cell
 
        :param r:   the row of the cell
        :param c:   the column of the cell
        :return :   List of the pairs of coordinates
        """
        polygon = []
        if (c % 2 == 0 and r % 2 == 0) or (c % 2 != 0 and r % 2 != 0):
            polygon.extend(((c/2 + 0.0) * self.edge + 1,       r * self.height + 1))
            polygon.extend(((c/2 + 1.0) * self.edge - 1,       r * self.height + 1))
            polygon.extend(((c/2 + 0.5) * self.edge + 0, (r + 1) * self.height - 0))
        else:
            polygon.extend(((c/2 + 0.5) * self.edge + 0,       r * self.height + 2))
            polygon.extend(((c/2 + 1.0) * self.edge - 1, (r + 1) * self.height - 0))
            polygon.extend(((c/2 + 0.0) * self.edge + 1, (r + 1) * self.height - 0))
        return polygon
 
    def calc_square(self, r, c):
        """
        Calculates the coordinates of the vertices of the square corresponding to a particular cell
 
        :param r:   the row of the cell
        :param c:   the column of the cell
        :return :   List of the pairs of coordinates
        """
        polygon = []
        polygon.extend((    c*self.square_size + 1,     r*self.square_size + 1))
        polygon.extend(((c+1)*self.square_size + 0,     r*self.square_size + 1))
        polygon.extend(((c+1)*self.square_size + 0, (r+1)*self.square_size + 0))
        polygon.extend((    c*self.square_size + 1, (r+1)*self.square_size + 0))
        return polygon
 
    def calc_hexagon(self, r, c):
        """
        Calculates the coordinates of the vertices of the hexagon corresponding to a particular cell
 
        :param r:   the row of the cell
        :param c:   the column of the cell
        :return :   List of the pairs of coordinates
        """
        polygon = []
        if c % 2 == 0:
            polygon.extend((                   self.radius/2 + c/2*3*self.radius+0,                 r*2*self.height+1))
            polygon.extend((     self.radius + self.radius/2 + c/2*3*self.radius+0,                 r*2*self.height+1))
            polygon.extend(( 2.0*self.radius +                 c/2*3*self.radius-1,   self.height + r*2*self.height+0))
            polygon.extend((     self.radius + self.radius/2 + c/2*3*self.radius+0, 2*self.height + r*2*self.height+0))
            polygon.extend((                   self.radius/2 + c/2*3*self.radius+0, 2*self.height + r*2*self.height+0))
            polygon.extend((                                   c/2*3*self.radius+1,   self.height + r*2*self.height+0))
        else:
            polygon.extend(( 0.5*self.radius +                 c/2*3*self.radius+0,   self.height + r*2*self.height+1))
            polygon.extend(( 1.5*self.radius +                 c/2*3*self.radius+0,   self.height + r*2*self.height+1))
            polygon.extend(( 1.5*self.radius + self.radius/2 + c/2*3*self.radius-1, 2*self.height + r*2*self.height+0))
            polygon.extend(( 1.5*self.radius +                 c/2*3*self.radius+0, 3*self.height + r*2*self.height+0))
            polygon.extend(( 0.5*self.radius +                 c/2*3*self.radius+0, 3*self.height + r*2*self.height+0))
            polygon.extend((-0.5*self.radius + self.radius/2 + c/2*3*self.radius+1, 2*self.height + r*2*self.height+0))
        return polygon
 
    def reset_click(self):
        """
        Action performed when user clicks "New grid" button
        """
        self.animation = False
        self.realTime = False
        for but in self.buttons:
            but.configure(state="normal")
        self.buttons[3].configure(fg="BLACK")  # Real-Time button
        self.slider.configure(state="normal")
        for but in self.algo_buttons:
            but.configure(state="normal")
        self.diagonalBtn.configure(state="normal")
        if self.shape in ["Triangle","Hexagon"]:
            self.diagonalBtn.configure(state="disabled")
        self.drawArrowsBtn.configure(state="normal")
        self.initialize_grid(False)
 
    def maze_click(self):
        """
        Action performed when user clicks "Maze" button
        """
        self.animation = False
        self.realTime = False
        for but in self.buttons:
            but.configure(state="normal")
        self.buttons[3].configure(fg="BLACK")  # Real-Time button
        self.slider.configure(state="normal")
        for but in self.algo_buttons:
            but.configure(state="normal")
        self.diagonalBtn.configure(state="normal")
        self.drawArrowsBtn.configure(state="normal")
        self.initialize_grid(True)
        if self.shape == "Triangle":
            self.diagonalBtn.configure(state="disabled")
 
    def clear_click(self):
        """
        Action performed when user clicks "Clear" button
        """
        self.animation = False
        self.realTime = False
        for but in self.buttons:
            but.configure(state="normal")
        self.buttons[3].configure(fg="BLACK")  # Real-Time button
        self.slider.configure(state="normal")
        for but in self.algo_buttons:
            but.configure(state="normal")
        self.diagonalBtn.configure(state="normal")
        if self.shape in ["Triangle","Hexagon"]:
            self.diagonalBtn.configure(state="disabled")
        self.drawArrowsBtn.configure(state="normal")
        self.fill_grid()
 
    def real_time_click(self):
        """
        Action performed when user clicks "Real-Time" button
        """
        if self.realTime:
            return
        self.realTime = True
        self.searching = True
        # The Dijkstra's initialization should be done just before the
        # start of search, because obstacles must be in place.
        if self.algorithm == "Dijkstra":
            self.initialize_dijkstra()
        self.buttons[3].configure(fg="RED")  # Real-Time button
        self.slider.configure(state="disabled")
        for but in self.algo_buttons:
            but.configure(state="disabled")
        self.diagonalBtn.configure(state="disabled")
        self.drawArrowsBtn.configure(state="disabled")
        self.real_time_action()
 
    def real_time_action(self):
        """
        Action performed during real-time search
        """
        while not self.endOfSearch:
            self.check_termination()
 
    def step_by_step_click(self):
        """
        Action performed when user clicks "Step-by-Step" button
        """
        if self.found or self.endOfSearch:
            return
        if not self.searching and self.algorithm == "Dijkstra":
            self.initialize_dijkstra()
        self.animation = False
        self.searching = True
        self.message.configure(text=self.MSG_SELECT_STEP_BY_STEP_ETC)
        self.buttons[3].configure(state="disabled")  # Real-Time button
        for but in self.algo_buttons:
            but.configure(state="disabled")
        self.diagonalBtn.configure(state="disabled")
        self.drawArrowsBtn.configure(state="disabled")
        self.check_termination()
 
    def animation_click(self):
        """
        Action performed when user clicks "Animation" button
        """
        self.animation = True
        if not self.searching and self.algorithm == "Dijkstra":
            self.initialize_dijkstra()
        self.searching = True
        self.message.configure(text=self.MSG_SELECT_STEP_BY_STEP_ETC)
        self.buttons[3].configure(state="disabled")  # Real-Time button
        for but in self.algo_buttons:
            but.configure(state="disabled")
        self.diagonalBtn.configure(state="disabled")
        self.drawArrowsBtn.configure(state="disabled")
        self.delay = self.slider.get()
        self.animation_action()
 
    def animation_action(self):
        """
        The action periodically performed during searching in animation mode
        """
        if self.animation:
            self.check_termination()
            if self.endOfSearch:
                return
            self.canvas.after(self.delay, self.animation_action)
 
    def about_click(self):
        """
        Action performed when user clicks "About Maze" button
        """
        about_box = Toplevel(master=app)
        about_box.title("")
        about_box.geometry("340x160")
        about_box.resizable(False, False)
        self.center(about_box)
        about_box.transient(app)  # only one window in the task bar
        about_box.grab_set()      # modal
 
        title = Label(about_box, text="Maze", width=20, anchor='center', fg='SANDY BROWN', font=("Helvetica", 20))
        title.place(x=0, y=0)
        version = Label(about_box, text="Version: 7.1", width=35, anchor='center', font=("Helvetica", 11, 'bold'))
        version.place(x=0, y=35)
        programmer = Label(about_box, text="Designer: Nikos Kanargias", width=35, anchor='center',
                           font=("Helvetica", 12))
        programmer.place(x=0, y=60)
        email = Label(about_box, text="E-mail: nkana@tee.gr", width=40, anchor='center', font=("Helvetica", 10))
        email.place(x=0, y=80)
        source_code = Label(about_box, text="Code and documentation", fg="blue", cursor="hand2", width=35,
                            anchor='center',
                            font=("Helvetica", 12))
        f = font.Font(source_code, source_code.cget("font"))
        f.configure(underline=True)
        source_code.configure(font=f)
        source_code.place(x=0, y=100)
        source_code.bind("<Button-1>", self.source_code_callback)
        self.CreateToolTip(source_code, "Click this link to retrieve code and documentation from DropBox")
        video = Label(about_box, text="Watch demo video on YouTube", fg="blue", cursor="hand2", width=35,
                      anchor='center')
        video.configure(font=f)
        video.place(x=0, y=125)
        video.bind("<Button-1>", self.video_callback)
        self.CreateToolTip(video, "Click this link to watch a demo video on YouTube")
 
    def check_termination(self):
        """
        Checks if search is completed
        """
        # Here we decide whether we can continue the search or not.
        # In the case of DFS, BFS, A* and Greedy algorithms
        # here we have the second step:
        # 2. If OPEN SET = [], then terminate. There is no solution.
        if (self.algorithm == "Dijkstra" and not self.graph) or\
                self.algorithm != "Dijkstra" and not self.openSet:
            self.endOfSearch = True
            self.grid[self.robotStart.row][self.robotStart.col] = self.ROBOT
            self.message.configure(text=self.MSG_NO_SOLUTION)
            self.buttons[4].configure(state="disabled")     # Step-by-Step button
            self.buttons[5].configure(state="disabled")     # Animation button
            self.slider.configure(state="disabled")
            self.repaint()
            if self.drawArrows.get():
                self.draw_arrows()
        else:
            self.expand_node()
            if self.found:
                self.endOfSearch = True
                self.plot_route()
                self.buttons[4].configure(state="disabled")  # Step-by-Step button
                self.buttons[5].configure(state="disabled")  # Animation button
                self.slider.configure(state="disabled")
 
    def expand_node(self):
        """
        Expands a node and creates his successors
        """
        # Dijkstra's algorithm to handle separately
        if self.algorithm == "Dijkstra":
            # 11: while Q is not empty:
            if not self.graph:
                return
            # 12:  u := vertex in Q (graph) with smallest distance in dist[] ;
            # 13:  remove u from Q (graph);
            u = self.graph.pop(0)
            # Add vertex u in closed set
            self.closedSet.append(u)
            # If target has been found ...
            if u == self.targetPos:
                self.found = True
                return
            # Counts nodes that have expanded.
            self.expanded += 1
            # Update the color of the cell
            self.grid[u.row][u.col] = self.CLOSED
            # paint the cell
            self.paint_cell(u.row, u.col, "CYAN")
            # 14: if dist[u] = infinity:
            if u.dist == self.INFINITY:
                # ... then there is no solution.
                # 15: break;
                return
                # 16: end if
            # Create the neighbors of u
            neighbors = self.create_successors(u, False)
            # 18: for each neighbor v of u:
            for v in neighbors:
                # 20: alt := dist[u] + dist_between(u, v) ;
                alt = u.dist + self.dist_between(u, v)
                # 21: if alt < dist[v]:
                if alt < v.dist:
                    # 22: dist[v] := alt ;
                    v.dist = alt
                    # 23: previous[v] := u ;
                    v.prev = u
                    # Update the color of the cell
                    self.grid[v.row][v.col] = self.FRONTIER
                    # paint the cell
                    self.paint_cell(v.row, v.col, "BLUE")
                    # 24: decrease-key v in Q;
                    # (sort list of nodes with respect to dist)
                    self.graph.sort(key=attrgetter("dist"))
        # The handling of the other four algorithms
        else:
            if self.algorithm in ["DFS", "BFS"]:
                # Here is the 3rd step of the algorithms DFS and BFS
                # 3. Remove the first state, Si, from OPEN SET ...
                current = self.openSet.pop(0)
            else:
                # Here is the 3rd step of the algorithms A* and Greedy
                # 3. Remove the first state, Si, from OPEN SET,
                # for which f(Si) ≤ f(Sj) for all other
                # open states Sj  ...
                # (sort first OPEN SET list with respect to 'f')
                self.openSet.sort(key=attrgetter("f"))
                current = self.openSet.pop(0)
            # ... and add it to CLOSED SET.
            self.closedSet.insert(0, current)
            # Update the color of the cell
            self.grid[current.row][current.col] = self.CLOSED
            # paint the cell
            self.paint_cell(current.row, current.col, "CYAN")
            # If the selected node is the target ...
            if current == self.targetPos:
                # ... then terminate etc
                last = self.targetPos
                last.prev = current.prev
                self.closedSet.append(last)
                self.found = True
                return
            # Count nodes that have been expanded.
            self.expanded += 1
            # Here is the 4rd step of the algorithms
            # 4. Create the successors of Si, based on actions
            #    that can be implemented on Si.
            #    Each successor has a pointer to the Si, as its predecessor.
            #    In the case of DFS and BFS algorithms, successors should not
            #    belong neither to the OPEN SET nor the CLOSED SET.
            successors = self.create_successors(current, False)
            # Here is the 5th step of the algorithms
            # 5. For each successor of Si, ...
            for cell in successors:
                # ... if we are running DFS ...
                if self.algorithm == "DFS":
                    # ... add the successor at the beginning of the list OPEN SET
                    self.openSet.insert(0, cell)
                    # Update the color of the cell
                    self.grid[cell.row][cell.col] = self.FRONTIER
                    # paint the cell
                    self.paint_cell(cell.row, cell.col, "BLUE")
                # ... if we are runnig BFS ...
                elif self.algorithm == "BFS":
                    # ... add the successor at the end of the list OPEN SET
                    self.openSet.append(cell)
                    # Update the color of the cell
                    self.grid[cell.row][cell.col] = self.FRONTIER
                    # paint the cell
                    self.paint_cell(cell.row, cell.col, "BLUE")
                # ... if we are running A* or Greedy algorithms (step 5 of A* algorithm) ...
                elif self.algorithm in ["A*", "Greedy"]:
                    # ... calculate the value f(Sj) ...
                    dxg = (self.centers[current.row][current.col].get_x() -
                           self.centers[cell.row][cell.col].get_x())
                    dyg = (self.centers[current.row][current.col].get_y() -
                           self.centers[cell.row][cell.col].get_y())
                    dxh = (self.centers[self.targetPos.row][self.targetPos.col].get_x() -
                           self.centers[cell.row][cell.col].get_x())
                    dyh = (self.centers[self.targetPos.row][self.targetPos.col].get_y() -
                           self.centers[cell.row][cell.col].get_y())
                    if self.diagonal.get() or self.shape == "Hexagon":
                        # with diagonal movements or hexagonal cells calculate the Euclidean distance
                        if self.algorithm == "Greedy":
                            # especially for the Greedy ...
                            cell.g = 0
                        else:
                            cell.g = current.g + math.hypot(dxg, dyg)
                        cell.h = math.hypot(dxh, dyh)
                    else:
                        # otherwise, calculate the Manhattan distance
                        if self.algorithm == "Greedy":
                            # especially for the Greedy ...
                            cell.g = 0
                        else:
                            cell.g = current.g + abs(dxg) + abs(dyg)
                        cell.h = abs(dxh) + abs(dyh)
                    cell.f = cell.g+cell.h
                    # ... If Sj is neither in the OPEN SET nor in the CLOSED SET states ...
                    if cell not in self.openSet and cell not in self.closedSet:
                        # ... then add Sj in the OPEN SET ...
                        # ... evaluated as f(Sj)
                        self.openSet.append(cell)
                        # Update the color of the cell
                        self.grid[cell.row][cell.col] = self.FRONTIER
                        # paint the cell
                        self.paint_cell(cell.row, cell.col, "BLUE")
                    # Else ...
                    else:
                        # ... if already belongs to the OPEN SET, then ...
                        if cell in self.openSet:
                            open_index = self.openSet.index(cell)
                            # ... compare the new value assessment with the old one.
                            # If old <= new ...
                            if self.openSet[open_index].f <= cell.f:
                                # ... then eject the new node with state Sj.
                                # (ie do nothing for this node).
                                pass
                            # Else, ...
                            else:
                                # ... remove the element (Sj, old) from the list
                                # to which it belongs ...
                                self.openSet.pop(open_index)
                                # ... and add the item (Sj, new) to the OPEN SET.
                                self.openSet.append(cell)
                                # Update the color of the cell
                                self.grid[cell.row][cell.col] = self.FRONTIER
                                # paint the cell
                                self.paint_cell(cell.row, cell.col, "BLUE")
                        # ... if already belongs to the CLOSED SET, then ...
                        elif cell in self.closedSet:
                            closed_index = self.closedSet.index(cell)
                            # ... compare the new value assessment with the old one.
                            # If old <= new ...
                            if self.closedSet[closed_index].f <= cell.f:
                                # ... then eject the new node with state Sj.
                                # (ie do nothing for this node).
                                pass
                            # Else, ...
                            else:
                                # ... remove the element (Sj, old) from the list
                                # to which it belongs ...
                                self.closedSet.pop(closed_index)
                                # ... and add the item (Sj, new) to the OPEN SET.
                                self.openSet.append(cell)
                                # Update the color of the cell
                                self.grid[cell.row][cell.col] = self.FRONTIER
                                # paint the cell
                                self.paint_cell(cell.row, cell.col, "BLUE")
 
    def create_successors(self, current, make_connected):
        """
        Creates three, four, six or eight successors of a cell
 
 
        :param current:        the cell for which we ask successors
        :param make_connected: flag that indicates that we are interested only on the coordinates
                               of cells and not on the label 'dist' (concerns only Dijkstra's)
        :return:               the successors of the cell as a list
        """
        r = current.row
        c = current.col
        # We create an empty list for the successors of the current cell.
        temp = []
        # With triangular cells pointing up the priority is:
        # 1: Up - right 2: Down 3: Up - left
        # With triangular cells pointing down the priority is:
        # 1: Up 2: Down - right 3: Down - left
 
        # With square cells and diagonal movements the priority is:
        # 1: Up 2: Up - right 3: Right 4: Down - right
        # 5: Down 6: Down - left 7: Left 8: Up - left
 
        # With square cells without diagonal movements the priority is:
        # 1: Up 2: Right 3: Down 4: Left
 
        # With hexagonal cells the priority is:
        # 1: Up 2: Up - right 3: Down - right
        # 4: Down 5: Down - left 6: Up - left
 
        # If not at the topmost limit of the grid
        # and the up-side cell is not an obstacle
        # and (only in the case DFS or BFS is running)
        # not already belongs neither to the OPEN SET nor to the CLOSED SET ...
        if (r > 0 and self.grid[r-1][c] != self.OBST and
                (((r % 2 == 0 and c % 2 == 0) or (r % 2 != 0 and c % 2 != 0)) if self.shape == "Triangle" else True)
                and (self.algorithm in ["A*", "Greedy", "Dijkstra"] or (self.algorithm in ["DFS", "BFS"]
                and not self.Cell(r-1, c) in self.openSet and not self.Cell(r-1, c) in self.closedSet))):
            cell = self.Cell(r-1, c)
            # In the case of Dijkstra's algorithm we can not append to
            # the list of successors the "naked" cell we have just created.
            # The cell must be accompanied by the label 'dist',
            # so we need to track it down through the list 'graph'
            # and then copy it back to the list of successors.
            # The flag makeConnected is necessary to be able
            # the present method create_succesors() to collaborate
            # with the method find_connected_component(), which creates
            # the connected component when Dijkstra's initializes.
            if self.algorithm == "Dijkstra":
                if make_connected:
                    temp.append(cell)
                elif cell in self.graph:
                    graph_index = self.graph.index(cell)
                    temp.append(self.graph[graph_index])
            else:
                # ... update the pointer of the up-side cell so it points the current one ...
                cell.prev = current
                # ... and add the up-side cell to the successors of the current one.
                temp.append(cell)
 
        if self.shape != "Triangle" and (self.diagonal.get() or (self.shape == "Hexagon" and c % 2 == 0)):
            # If we are not even at the topmost nor at the rightmost border of the grid
            # and the up-right-side cell is not an obstacle
            # and one of the upper side or right side cells are not obstacles
            # (because it is not reasonable to allow the robot to pass through a "slot")
            # and (only in the case DFS or BFS is running)
            # not already belongs neither to the OPEN SET nor CLOSED SET ...
            if (r > 0 and c < self.columns-1 and self.grid[r-1][c+1] != self.OBST and
                    (self.grid[r-1][c] != self.OBST or self.grid[r][c+1] != self.OBST if self.shape == "Square" else True)
                    and (self.algorithm in ["A*", "Greedy", "Dijkstra"] or (self.algorithm in ["DFS", "BFS"]
                    and not self.Cell(r-1, c+1) in self.openSet and not self.Cell(r-1, c+1) in self.closedSet))):
                cell = self.Cell(r-1, c+1)
                if self.algorithm == "Dijkstra":
                    if make_connected:
                        temp.append(cell)
                    elif cell in self.graph:
                        graph_index = self.graph.index(cell)
                        temp.append(self.graph[graph_index])
                else:
                    # ... update the pointer of the up-right-side cell so it points the current one ...
                    cell.prev = current
                    # ... and add the up-right-side cell to the successors of the current one.
                    temp.append(cell)
 
        # If not at the rightmost limit of the grid
        # and the right-side cell is not an obstacle ...
        # and (only in the case DFS or BFS is running)
        # not already belongs neither to the OPEN SET nor to the CLOSED SET ...
        if (c < self.columns-1 and self.grid[r][c+1] != self.OBST and
                (self.algorithm in ["A*", "Greedy", "Dijkstra"] or (self.algorithm in ["DFS", "BFS"]
                 and not self.Cell(r, c+1) in self.openSet and not self.Cell(r, c+1) in self.closedSet))):
            cell = self.Cell(r, c+1)
            if self.algorithm == "Dijkstra":
                if make_connected:
                    temp.append(cell)
                elif cell in self.graph:
                    graph_index = self.graph.index(cell)
                    temp.append(self.graph[graph_index])
            else:
                # ... update the pointer of the right-side cell so it points the current one ...
                cell.prev = current
                # ... and add the right-side cell to the successors of the current one.
                temp.append(cell)
 
        if self.shape != "Triangle" and (self.diagonal.get() or (self.shape == "Hexagon" and c % 2 != 0)):
            # If we are not even at the lowermost nor at the rightmost border of the grid
            # and the down-right-side cell is not an obstacle
            # and one of the down-side or right-side cells are not obstacles
            # and (only in the case DFS or BFS is running)
            # not already belongs neither to the OPEN SET nor to the CLOSED SET ...
            if (r < self.rows-1 and c < self.columns-1 and self.grid[r+1][c+1] != self.OBST and
                    (self.grid[r+1][c] != self.OBST or self.grid[r][c+1] != self.OBST if self.shape == "Square" else True)
                    and (self.algorithm in ["A*", "Greedy", "Dijkstra"] or (self.algorithm in ["DFS", "BFS"]
                    and not self.Cell(r+1, c+1) in self.openSet and not self.Cell(r+1, c+1) in self.closedSet))):
                cell = self.Cell(r+1, c+1)
                if self.algorithm == "Dijkstra":
                    if make_connected:
                        temp.append(cell)
                    elif cell in self.graph:
                        graph_index = self.graph.index(cell)
                        temp.append(self.graph[graph_index])
                else:
                    # ... update the pointer of the downr-right-side cell so it points the current one ...
                    cell.prev = current
                    # ... and add the down-right-side cell to the successors of the current one.
                    temp.append(cell)
 
        # If not at the lowermost limit of the grid
        # and the down-side cell is not an obstacle
        # and (only in the case DFS or BFS is running)
        # not already belongs neither to the OPEN SET nor to the CLOSED SET ...
        if (r < self.rows-1 and self.grid[r+1][c] != self.OBST and
                (((r % 2 == 0 and c % 2 != 0) or (r % 2 != 0 and c % 2 == 0)) if self.shape == "Triangle" else True)
                and (self.algorithm in ["A*", "Greedy", "Dijkstra"] or (self.algorithm in ["DFS", "BFS"]
                and not self.Cell(r+1, c) in self.openSet and not self.Cell(r+1, c) in self.closedSet))):
            cell = self.Cell(r+1, c)
            if self.algorithm == "Dijkstra":
                if make_connected:
                    temp.append(cell)
                elif cell in self.graph:
                    graph_index = self.graph.index(cell)
                    temp.append(self.graph[graph_index])
            else:
                # ... update the pointer of the down-side cell so it points the current one ...
                cell.prev = current
                # ... and add the down-side cell to the successors of the current one.
                temp.append(cell)
 
        if self.shape != "Triangle" and (self.diagonal.get() or (self.shape == "Hexagon" and c % 2 != 0)):
            # If we are not even at the lowermost nor at the leftmost border of the grid
            # and the down-left-side cell is not an obstacle
            # and one of the down-side or left-side cells are not obstacles
            # and (only in the case DFS or BFS is running)
            # not already belongs neither to the OPEN SET nor to the CLOSED SET ...
            if (r < self.rows-1 and c > 0 and self.grid[r+1][c-1] != self.OBST and
                    (self.grid[r+1][c] != self.OBST or self.grid[r][c-1] != self.OBST if self.shape == "Square" else True)
                    and (self.algorithm in ["A*", "Greedy", "Dijkstra"] or (self.algorithm in ["DFS", "BFS"]
                    and not self.Cell(r+1, c-1) in self.openSet and not self.Cell(r+1, c-1) in self.closedSet))):
                cell = self.Cell(r+1, c-1)
                if self.algorithm == "Dijkstra":
                    if make_connected:
                        temp.append(cell)
                    elif cell in self.graph:
                        graph_index = self.graph.index(cell)
                        temp.append(self.graph[graph_index])
                else:
                    # ... update the pointer of the down-left-side cell so it points the current one ...
                    cell.prev = current
                    # ... and add the down-left-side cell to the successors of the current one.
                    temp.append(cell)
 
        # If not at the leftmost limit of the grid
        # and the left-side cell is not an obstacle
        # and (only in the case DFS or BFS is running)
        # not already belongs neither to the OPEN SET nor to the CLOSED SET ...
        if (c > 0 and self.grid[r][c-1] != self.OBST and
                (self.algorithm in ["A*", "Greedy", "Dijkstra"] or (self.algorithm in ["DFS", "BFS"]
                 and not self.Cell(r, c-1) in self.openSet and not self.Cell(r, c-1) in self.closedSet))):
            cell = self.Cell(r, c-1)
            if self.algorithm == "Dijkstra":
                if make_connected:
                    temp.append(cell)
                elif cell in self.graph:
                    graph_index = self.graph.index(cell)
                    temp.append(self.graph[graph_index])
            else:
                # ... update the pointer of the left-side cell so it points the current one ...
                cell.prev = current
                # ... and add the left-side cell to the successors of the current one.
                temp.append(cell)
 
        if self.shape != "Triangle" and (self.diagonal.get() or (self.shape == "Hexagon" and c % 2 == 0)):
            # If we are not even at the topmost nor at the leftmost border of the grid
            # and the up-left-side cell is not an obstacle
            # and one of the up-side or left-side cells are not obstacles
            # and (only in the case DFS or BFS is running)
            # not already belongs neither to the OPEN SET nor to the CLOSED SET ...
            if (r > 0 and c > 0 and self.grid[r-1][c-1] != self.OBST and
                    (self.grid[r-1][c] != self.OBST or self.grid[r][c-1] != self.OBST if self.shape == "Square" else True)
                    and (self.algorithm in ["A*", "Greedy", "Dijkstra"] or (self.algorithm in ["DFS", "BFS"]
                    and not self.Cell(r-1, c-1) in self.openSet and not self.Cell(r-1, c-1) in self.closedSet))):
                cell = self.Cell(r-1, c-1)
                if self.algorithm == "Dijkstra":
                    if make_connected:
                        temp.append(cell)
                    elif cell in self.graph:
                        graph_index = self.graph.index(cell)
                        temp.append(self.graph[graph_index])
                else:
                    # ... update the pointer of the up-left-side cell so it points the current one ...
                    cell.prev = current
                    # ... and add the up-left-side cell to the successors of the current one.
                    temp.append(cell)
 
        # When DFS algorithm is in use, cells are added one by one at the beginning of the
        # OPEN SET list. Because of this, we must reverse the order of successors formed,
        # so the successor corresponding to the highest priority, to be placed the first in the list.
        # For the Greedy, A* and Dijkstra's no issue, because the list is sorted
        # according to 'f' or 'dist' before extracting the first element of.
        if self.algorithm == "DFS":
            return reversed(temp)
        else:
            return temp
 
    def dist_between(self, u, v):
        """
        Returns the distance between two cells
 
        :param u: the first cell
        :param v: the other cell
        :return:  the distance between the cells u and v
        """
        dx = self.centers[u.row][u.col].get_x() - self.centers[v.row][v.col].get_x()
        dy = self.centers[u.row][u.col].get_y() - self.centers[v.row][v.col].get_y()
        if self.diagonal.get() or self.shape in ["Triangle", "Hexagon"]:
            # with diagonal movements or triangular cells or hexagonal cells calculate the Euclidean distance
            return math.hypot(dx, dy)
        else:
            # otherwise calculate the Manhattan distance
            return abs(dx) + abs(dy)
 
    def plot_route(self):
        """
        Calculates the path from the target to the initial position of the robot,
        counts the corresponding steps and measures the distance traveled.
        """
        self.repaint()
        self.searching = False
        steps = 0
        distance = 0.0
        index = self.closedSet.index(self.targetPos)
        cur = self.closedSet[index]
        self.grid[cur.row][cur.col] = self.TARGET
        self.paint_cell(cur.row, cur.col, "GREEN")
        while cur != self.robotStart:
            steps += 1
            dx = self.centers[cur.row][cur.col].get_x() - self.centers[cur.prev.row][cur.prev.col].get_x()
            dy = self.centers[cur.row][cur.col].get_y() - self.centers[cur.prev.row][cur.prev.col].get_y()
            distance += math.hypot(dx, dy)
            cur = cur.prev
            self.grid[cur.row][cur.col] = self.ROUTE
            self.paint_cell(cur.row, cur.col, "YELLOW")
 
        self.grid[self.robotStart.row][self.robotStart.col] = self.ROBOT
        self.paint_cell(self.robotStart.row, self.robotStart.col, "RED")
 
        if self.drawArrows.get():
            self.draw_arrows()
 
        msg = "Nodes expanded: {0}, Steps: {1}, Distance: {2:.1f}".format(self.expanded, steps, distance)
        self.message.configure(text=msg)
 
    def find_connected_component(self, v):
        """
        Appends to the list containing the nodes of the graph only
        the cells belonging to the same connected component with node v.
 
        :param v: the starting node
        """
        # This is a Breadth First Search of the graph starting from node v.
        stack = [v]
        self.graph.append(v)
        while stack:
            v = stack.pop()
            successors = self.create_successors(v, True)
            for c in successors:
                if c not in self.graph:
                    stack.append(c)
                    self.graph.append(c)
 
    def initialize_dijkstra(self):
        """
        Initialization of Dijkstra's algorithm
        """
        # When one thinks of Wikipedia pseudocode, observe that the
        # algorithm is still looking for his target while there are still
        # nodes in the queue Q.
        # Only when we run out of queue and the target has not been found,
        # can answer that there is no solution.
        # As is known, the algorithm models the problem as a connected graph
        # It is obvious that no solution exists only when the graph is not
        # connected and the target is in a different connected component
        # of this initial position of the robot
        # To be thus possible negative response from the algorithm,
        # should search be made ONLY in the coherent component to which the
        # initial position of the robot belongs.
 
        # First create the connected component
        # to which the initial position of the robot belongs.
        self.graph.clear()
        self.find_connected_component(self.robotStart)
        # Here is the initialization of Dijkstra's algorithm
        # 2: for each vertex v in Graph;
        for v in self.graph:
            # 3: dist[v] := infinity ;
            v.dist = self.INFINITY
            # 5: previous[v] := undefined ;
            v.prev = None
        # 8: dist[source] := 0;
        self.graph[self.graph.index(self.robotStart)].dist = 0
        # 9: Q := the set of all nodes in Graph;
        # Instead of the variable Q we will use the list
        # 'graph' itself, which has already been initialised.
 
        # Sorts the list of nodes with respect to 'dist'.
        self.graph.sort(key=attrgetter("dist"))
        # Initializes the list of closed nodes
        self.closedSet.clear()
 
    def draw_arrows(self):
        """
        Draws the arrows to predecessors
        """
        # We draw black arrows from each open or closed state to its predecessor.
        for r in range(self.rows):
            for c in range(self.columns):
                tail = head = cell = self.Cell(r, c)
                # If the current cell is an open state, or is a closed state
                # but not the initial position of the robot
                if self.grid[r][c] in [self.FRONTIER, self.CLOSED] and not cell == self.robotStart:
                    # The tail of the arrow is the current cell, while
                    # the arrowhead is the predecessor cell.
                    if self.grid[r][c] == self.FRONTIER:
                        if self.algorithm == "Dijkstra":
                            tail = self.graph[self.graph.index(cell)]
                            head = tail.prev
                        else:
                            tail = self.openSet[self.openSet.index(cell)]
                            head = tail.prev
                    elif self.grid[r][c] == self.CLOSED:
                        tail = self.closedSet[self.closedSet.index(cell)]
                        head = tail.prev
 
                    self.draw_arrow(tail, head, self.arrow_size, "BLACK", 2 if self.arrow_size >= 10 else 1)
 
        if self.found:
            # We draw red arrows along the path from robotStart to targetPos.
            # index = self.closedSet.index(self.targetPos)
            cur = self.closedSet[self.closedSet.index(self.targetPos)]
            while cur != self.robotStart:
                head = cur
                cur = cur.prev
                tail = cur
                self.draw_arrow(tail, head, self.arrow_size, "RED", 2 if self.arrow_size >= 10 else 1)
 
    def draw_arrow(self, tail, head, a, color, width):
        """
        Draws an arrow from center of tail cell to center of head cell
 
        :param tail:   the tail of the arrow
        :param head:   the head of the arrow
        :param a:      size of arrow tips
        :param color:  color of the arrow
        :param width:  thickness of the lines
        """
        # The coordinates of the center of the tail cell
        x1 = self.centers[tail.row][tail.col].get_x()
        y1 = self.centers[tail.row][tail.col].get_y()
        # The coordinates of the center of the head cell
        x2 = self.centers[head.row][head.col].get_x()
        y2 = self.centers[head.row][head.col].get_y()
 
        sin20 = math.sin(20*math.pi/180)
        cos20 = math.cos(20*math.pi/180)
        sin25 = math.sin(25*math.pi/180)
        cos25 = math.cos(25*math.pi/180)
        sin10 = math.sin(10*math.pi/180)
        cos10 = math.cos(10*math.pi/180)
        sin40 = math.sin(40*math.pi/180)
        cos40 = math.cos(40*math.pi/180)
        u3 = v3 = u4 = v4 = 0
 
        if x1 == x2 and y1 > y2:  # up
            u3 = x2 - a*sin20
            v3 = y2 + a*cos20
            u4 = x2 + a*sin20
            v4 = v3
        elif x1 < x2 and y1 > y2 and self.shape == "Square":  # up-right square cell
            u3 = x2 - a*cos25
            v3 = y2 + a*sin25
            u4 = x2 - a*sin25
            v4 = y2 + a*cos25
        elif x1 < x2 and y1 > y2 and self.shape in ["Triangle", "Hexagon"]:  # up-right triangular and hexagonal cells
            u3 = x2 - a * cos10
            v3 = y2 + a * sin10
            u4 = x2 - a * sin40
            v4 = y2 + a * cos40
        elif x1 < x2 and y1 == y2:  # right
            u3 = x2 - a*cos20
            v3 = y2 - a*sin20
            u4 = u3
            v4 = y2 + a*sin20
        elif x1 < x2 and y1 < y2 and self.shape == "Square":  # right-down square cell
            u3 = x2 - a*cos25
            v3 = y2 - a*sin25
            u4 = x2 - a*sin25
            v4 = y2 - a*cos25
        elif x1 < x2 and y1 < y2 and self.shape in ["Triangle", "Hexagon"]:  # right-down triangular and hexagonal cells
            u3 = x2 - a * cos10
            v3 = y2 - a * sin10
            u4 = x2 - a * sin40
            v4 = y2 - a * cos40
        elif x1 == x2 and y1 < y2:  # down
            u3 = x2 - a*sin20
            v3 = y2 - a*cos20
            u4 = x2 + a*sin20
            v4 = v3
        elif x1 > x2 and y1 < y2 and self.shape == "Square":  # left-down square cell
            u3 = x2 + a*sin25
            v3 = y2 - a*cos25
            u4 = x2 + a*cos25
            v4 = y2 - a*sin25
        elif x1 > x2 and y1 < y2 and self.shape in ["Triangle", "Hexagon"]:  # left-down triangular and hexagonal cells
            u3 = x2 + a * sin40
            v3 = y2 - a * cos40
            u4 = x2 + a * cos10
            v4 = y2 - a * sin10
        elif x1 > x2 and y1 == y2:  # left
            u3 = x2 + a*cos20
            v3 = y2 - a*sin20
            u4 = u3
            v4 = y2 + a*sin20
        elif x1 > x2 and y1 > y2 and self.shape == "Square":  # left-up square cell
            u3 = x2 + a*sin25
            v3 = y2 + a*cos25
            u4 = x2 + a*cos25
            v4 = y2 + a*sin25
        elif x1 > x2 and y1 > y2 and self.shape in ["Triangle", "Hexagon"]:  # left-up triangular and hexagonal cells
            u3 = x2 + a*sin40
            v3 = y2 + a*cos40
            u4 = x2 + a*cos10
            v4 = y2 + a*sin10
 
        self.canvas.create_line(x1, y1, x2, y2, fill=color, width=width)
        self.canvas.create_line(x2, y2, u3, v3, fill=color, width=width)
        self.canvas.create_line(x2, y2, u4, v4, fill=color, width=width)
 
    @staticmethod
    def center(window):
        """
        Places a window at the center of the screen
        """
        window.update_idletasks()
        w = window.winfo_screenwidth()
        h = window.winfo_screenheight()
        size = tuple(int(_) for _ in window.geometry().split('+')[0].split('x'))
        x = w / 2 - size[0] / 2
        y = h / 2 - size[1] / 2
        window.geometry("%dx%d+%d+%d" % (size + (x, y)))
 
    @staticmethod
    def source_code_callback(self):
        webbrowser.open_new(r"https://goo.gl/tRaLfe")
 
    @staticmethod
    def video_callback(self):
        webbrowser.open_new(r"https://youtu.be/7GLqy61X2oU")
 
 
def on_closing():
    if messagebox.askokcancel("Quit", "Do you want to quit?"):
        os._exit(0)
 
 
if __name__ == '__main__':
    app = Tk()
    app.protocol("WM_DELETE_WINDOW", on_closing)
    app.title("Maze 7.1")
    app.geometry("693x545")
    app.resizable(False, False)
    Maze71(app)
    app.mainloop()