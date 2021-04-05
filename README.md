# Dipole Player - University project
Authors: Antonio Commisso, Antonio Sola, Marco Dramisino 

Autonomous Dipole player 

We built a player for Dipole. Dipole is a board game with 12 pawns. You can find more information here: https://boardgamegeek.com/boardgame/29491/dipole

In order to build this player we used several strategies, such as: 
- Tree based states exploration
- Iterative deepening with min-max, alpha beta
- An hash table to save equal states from different path, using as hash function the Zobrist function
- We measured the quality of a state using a heuristic function
- Adaptive depth visit 
