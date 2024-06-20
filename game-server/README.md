# Game Server and Client README

## Overview
This repository contains code for a simple Jump Sturdygame server and client implemented in Python. The server allows two clients to connect and play, communicating over a network using JSON for data exchange.

## Components
1. **Server**: Handles client connections, game logic, and communication between clients.
2. **Client**: Connects to the server, sends player input, and receives game updates from the server. This is an example.
3. **Network**: Intermediary between server and client.
3. **Game Logic**: Implements the rules and mechanics of the game.

## Requirements
- Python 3.x
- Pygame (for future time management)
- Numpy

## Usage
1. **Server**: Run the server script (`server.py`) to start the game server. Make sure to specify the appropriate host and port. The server should be localhost, if you want to run it in your own network. 
   ```bash
   python server.py
   ```

2. **Client**: Run the client script (`client.py`) to connect to the server and play the game. Input is right now the input in the terminal. You will need to start two clients in different terminals to start the game.
   ```bash
   python client.py
   ```

## Configuration
- Server: Modify the server script to change the host address and port as needed.
- Network: Adjust the networkt script to specify the server's address and port.

## Good to know
- The game class operates on a "backwards" array, also the moves are parsed this way. So starting point of the array is the upper left corner.
- You should be able to connect to the server with clients in various languages. Try and adapt. If your experiencing issues feel free to ask for help. I will try
- Red starts and is on top of the board
- The server sends the game state to the client in the following JSON format:
```python
output = {
    "board": "b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 r",
    "player1": True,
    "player2": False,
    "bothConnected": True
}
```
- You should answer in the following format. Where `"E7-F7"` represents a move from position E7 to position F7.
```json
{
    "move": "E7-F7"
}
```
- The game will be counted as a loss for a client who sends a unvalid or incorrectly parsed move. 
- The game will be counted as a loss for a client who sends a move, if it is not their turn.

## ToDo and Outlook
- Adding time management
- Including the Stalemate rule
- Being able to Start in any position
- Tournament system (Maybe Swiss) with two games each (everyone will be red)

## Troubleshooting
- If you encounter any issues with the server or client communication, check for errors in the console output and ensure that the network configuration is correct. Feel free to ask me under sarah.lengfeld@campus.tu-berlin.de

## Credits
- Developed by Sarah Lengfeld
- Basic structure from Tim Ruscica, https://www.techwithtim.net/tutorials/python-online-game-tutorial/
