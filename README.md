# COMP 261 Assignment 3 - gandhinira

## What the code does
* [x] Core P1: Find and display the maximum flow through a given flow network.
* [x] Core P2: Find and display all the Augmentation paths identified in the flow network.
* [x] Completion P1: Identify the s-t cut exhibited by your implementation of the Ford-fulkerson method.
* [x] Completion P2: Vehicle-type sensitivity.

I have completed the Minimum, Core, Completion alongside the addition of a button that allows switching between time or distance for the A* search which are apart of Challenge.


## Core
Core was the process of using a DFS/BFS (I did a BFS search) to find a flow between the source and sink nodes, I was able to find and display the flow value alongside finding and displaying the flow path.
I also went on to Implement the Edmon-karp algorithm which is an implementation of the Ford-fulkerson algorithm, essentially now My program is able to find and display the maximum flow throughout the given flow network alongside displaying all the augmentation paths that were identified in the flow network.


## Completion
For completion there are two parts, I completed both of these parts, these contained identifying the s-t cut exhibited by my implementation of the Ford-fulkerson method, I was able to both find/display the s-t cut alongside finding and displaying the capacity of the s-t cut. The minimum cut of a weighted graph is defined as the minimum sum of weights of edges that, when removed from the graph, divide the graph into two sets.

For the second part of completion I added more buttons to the FXML file via scenebuilder which then allows me to select which type of vehicle to be used as input, such as bus, car or moped. each of these different vehicles have different multipliers which therefore the capacity needs to be multiplied with. after doing this we can get the correct flow capacity of all the edges of the network graph dependant on the vehicle type that is being used.
