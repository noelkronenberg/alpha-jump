import socket
from _thread import *
import json
import random
from game import Game

server = "localhost"
port = 5555

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

try:
    s.bind((server, port))
except socket.error as e:
    str(e)

s.listen(2)
print("Waiting for a connection, Server Started")

connected = set()
games = {}
idCount = 0

def threaded_client(conn, p, gameId):
    fileLog = open(str(gameId) + str(p) + str(random.randint(1, 999999)) + ".txt", "a")
    global idCount
    conn.send(str.encode(str(p)))

    if gameId in games:
        game = games[gameId]
        if p==0:
            print("P1 connected")
            fileLog.write("P1 connected \n")
            game.connectP1()
        else:
            print("P2 connected")
            fileLog.write("P2 connected \n")
            game.connectP2()
    
    reply = ""
    while True:
        try:
            print("HERE")
            data = conn.recv(4096).decode('utf-8')
            data = json.loads(data)
            if gameId in games:
                game = games[gameId]
                if not data:
                    break
                else:
                    if data != "get":
                        if p==0 and game.getP1Turn():
                            game.playTurn(p, data)
                        elif p==1 and not game.getP1Turn():
                            game.playTurn(p, data)
                        fileLog.write(game.getBoard())
                        fileLog.write("\n")
                        fileLog.write(data)
                        fileLog.write("\n")
                        if not game.getValid():
                            if game.getCurrentPlayer() == "r":
                                game.reset()
                                print("Game finished, win: b")
                                fileLog.write("Game finished, win: b \n")
                            if game.getCurrentPlayer() == "b":
                                game.reset()
                                print("Game finished, win: r")
                                fileLog.write("Game finished, win: r \n")
                        win = game.winnerDeter()
                        if not win == "0":
                            game.reset()
                            print("Game finished, win: ",win)
                            fileLog.write("Game finished, win: ",win, "\n")
                    output = dict(board = game.getBoard(), player1 = game.getP1Turn(), player2 = game.getP2Turn(), bothConnected = game.bothConnected())
                    output = json.dumps(output)
                    output = output.encode('utf-8')
                    print(f'TYPE: {type(output)}')
                    conn.sendall(output)
            else:
                break
        except:
            break
    fileLog.close()
    print("Lost connection")
    try:
        del games[gameId]
        print("Closing Game", gameId)
    except:
        pass
    idCount -= 1
    conn.close()

while True:
    conn, addr = s.accept()
    print("Connected to:", addr)

    idCount += 1
    p = 0
    gameId = (idCount - 1)//2
    if idCount % 2 == 1:
        games[gameId] = Game(gameId)
        print("Creating a new game...")
    else:
        games[gameId].ready = True
        p = 1


    start_new_thread(threaded_client, (conn, p, gameId))