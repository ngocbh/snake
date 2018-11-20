### The Snake

A simple snake game in java . Using Threads and Java Swing to display the game.
Snake have some option : 
* play mutiple player ( to add play, config Window.java )
* use AI  to autoplay snake

### How it looks:
![alt tag](https://raw.githubusercontent.com/ngocjr7/snake/master/sample.png)

### How to run the project:

#### Requirements:
* Java runtime installed

#### How to play the game:

* Just download the Snake.jar file ( NEED SOMEONE COMBINE THIS PROJECT TO JAR FILE )
* Run it in terminal 
```
./run.sh
```
* To config some argument for game, open run file and config this.
- width 20 -height 20 -speed 20 - algorithm 4

* Algorithm 
1. BFS Proposal
2. Greedy base on BFS Proposal
3. A* Proposal
4. Greedy base on A* Proposal

### AI Algorithm
* Greedy 
Greedy Solver directs the snake to eat the food along the shortest path if it thinks the snake will be safe. Otherwise, it makes the snake wander around until a safe path can be found. As it needs paths searching, it depends on Path Solver
Concretely, to find the snake S1's next moving direction D, the solver follows the steps below:

1. Compute the shortest path P1 from S1's head to the food. If P1 exists, go to step 2. Otherwise, go to step 4.
2. Move a virtual snake S2 (the same as S1) to eat the food along path P1.
3. Compute the longest path P2 from S2's head to its tail. If P2 exists, let D be the first direction in path P1. Otherwise, go to step 4.
4. Compute the longest path P3 from S1's head to its tail. If P3 exists, let D be the first direction in path P3. Otherwise, go to step 5.
5. Let D be the direction that makes S1 the farthest from the food.
6. If snake cannot find the food. it wander in its space.

#### Sources : 
[chuyangliu](https://github.com/chuyangliu/Snake)
[hexadeciman](https://github.com/hexadeciman/Snake)

* Find shortest path: 2 algorithm to find shortest path are breadth-first search and A-star algorithm. it is the same but A-star is faster a little bit.

* Find longest path: It extend the shortest path to left and right whenever it can.

### Some thing uncompleted
* Tạo nút reset cho game
* Thêm bảng đánh giá từng thuật toán một
* Có một thuật toán nữa là tìm một chu trình qua tất cả các ô. Thuật toán này sẽ đảm bảo luôn điền đầy bảng nhưng chả có gì hay cả. Nếu ai thích thì có thể code. ko quá khó.  
