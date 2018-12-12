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
* Đã xử lý trường hợp con rắn chạy vòng lặp vô hạn
* Thái chiều về clone file git này về. import vào netbean. Nhớ giữ đúng cấu trúc file như này. giữ lại các file run, test.sh này nọ, chỉ thêm các file cần có để import netbean. mục đích để có thể vừa import vào netbean được vừa chạy console được. 
* Các việc còn lại Thái Linh Văn chia nhau ra làm :

* Tạo một panel bên phải window chạy rắn. chứa 1 rút reset và 1 panel chứa điểm.
* Chạy một thuật toán nhiều lần ( Khoảng 60-100 lần gì đó ) tính trung bình kết quả ra. Nếu show vào được cái panel bên trái thì tốt còn không thì show vào text hoặc table như Văn làm cũng đc. 
* Tất cả các file mới tạo vứt vào thư mục src/evaluator , kể cả file txt.
* Có sẵn file evalutor.py rồi. vẽ ra các trường hợp tường để test với từng thuật toán ( A* và BFS thôi ). Quay video màn hình lại đoạn chạy đó để show vào slide. 
* đọc github https://github.com/chuyangliu/Snake để rõ thuật toán sử dụng rồi viết báo cáo. thuật toán t sử dụng từ đó.