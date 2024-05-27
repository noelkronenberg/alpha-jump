# import pygame
import json
from network import Network
# pygame.font.init()
import middleware

import middleware

def main():
    run = True
    # clock = pygame.time.Clock()
    n = Network()
    player = int(n.getP())
    print("You are player", player)

    while run:
        # clock.tick(60)
        try:
            #try to send get as a json to server over network, rest is error handling
            game = n.send(json.dumps("get"))
            if game is None:
                raise ValueError("Game data is None")
        except:
            run = False
            print("Couldn't get game")
            break

        #response is also a json, json.loads transforms into a python dictionary
        #dictionary consists of board string, a variable player1 which is true, when player 1 (or better 0), 
        #variable player2 with the same concept and bothConnected, also a boolean
        game = json.loads(game)

        #allow input just when both players are in
        if game["bothConnected"]:

            #allow to only give input, when it is your turn
            if player == 0 and game["player1"]:
                #printing not necessary, game["board"] is the way to get the board string
                print("New Board: " + game["board"])

                i = middleware.getMove(game["board"])
                print(f'Move: {i}')

                #json.dumps(i) transforms the input into a json. You can print it, if you want to see the difference
                data = json.dumps(i)

                #send data via network
                n.send(data)
            elif player == 1 and game["player2"]:
                print("New Board: " + game["board"])
                i = middleware.getMove(game["board"])
                print(f'Move: {i}')
                data = json.dumps(i)
                n.send(data)

while True:
    main()