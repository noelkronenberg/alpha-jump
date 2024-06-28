import socket
from _thread import *
import json
import random
from game import Game
import pygame
import sys

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
pygame.init()
clock = pygame.time.Clock()


def threaded_client(conn, p, gameId):
    initialTime = 120000
    timePlayer = initialTime
    fileLog = open(str(gameId) + str(p) + str(random.randint(1, 999999)) + ".txt", "a")
    global idCount
    conn.send(str.encode(str(p)))
    firstSend = False
    if gameId in games:
        game = games[gameId]
        if p==0:
            print("P1 connected")
            fileLog.write("P1 connected \n")
            firstSend = True
            game.connectP1()
        else:
            print("P2 connected")
            fileLog.write("P2 connected \n")
            game.connectP2()
    
    reply = ""
    while True:
        try:
            data = conn.recv(4096).decode('utf-8')
            data = json.loads(data)
            if gameId in games:
                game = games[gameId]
                if not data:
                    data = "get"
                else:
                    if data != "get":
                        timeForMove = clock.tick()
                        timePlayer = timePlayer - timeForMove + 10
                        fileLog.write("Time remaining active player: " + str(timePlayer) + "\n")
                        if p==0 and game.getP1Turn():
                            print("Time active Player : " + str(timePlayer))
                            if timePlayer < 0:
                                print("Game finished Timeout, win: b")
                                fileLog.write("Game finished, Timeout win: b \n")
                                output = dict(board = game.getBoard(), player1 = game.getP1Turn(), player2 = game.getP2Turn(), bothConnected = game.bothConnected(), time = timePlayer, end = True)
                                output = json.dumps(output)
                                output = output.encode('utf-8')
                                conn.sendall(output)
                                fileLog.close()
                                break
                            game.playTurn(p, data)
                        elif p==1 and not game.getP1Turn():
                            print("Time active Player : " + str(timePlayer))
                            if timePlayer < 0:
                                print("Game finished Timeout, win: r")
                                fileLog.write("Game finished Timeout, win: r \n")
                                output = dict(board = game.getBoard(), player1 = game.getP1Turn(), player2 = game.getP2Turn(), bothConnected = game.bothConnected(), time = timePlayer, end = True)
                                output = json.dumps(output)
                                output = output.encode('utf-8')
                                conn.sendall(output)
                                fileLog.close()
                                break
                            game.playTurn(p, data)
                        fileLog.write(game.getBoard())
                        fileLog.write("\n")
                        fileLog.write(data)
                        fileLog.write("\n")
                        if not game.getValid():
                            if game.getCurrentPlayer() == "r":
                                game.reset()
                                print("Game finished, Invalid Move win: b")
                                fileLog.write("Game finished, Invalid Move win: b \n")
                                output = dict(board = game.getBoard(), player1 = game.getP1Turn(), player2 = game.getP2Turn(), bothConnected = game.bothConnected(), time = timePlayer, end = True)
                                output = json.dumps(output)
                                output = output.encode('utf-8')
                                conn.sendall(output)
                                fileLog.close()
                                break
                            if game.getCurrentPlayer() == "b":
                                game.reset()
                                print("Game finished, Invalid Move win: r")
                                fileLog.write("Game finished, Invalid Move win: r \n")
                                output = dict(board = game.getBoard(), player1 = game.getP1Turn(), player2 = game.getP2Turn(), bothConnected = game.bothConnected(), time = timePlayer, end = True)
                                output = json.dumps(output)
                                output = output.encode('utf-8')
                                conn.sendall(output)
                                fileLog.close()
                                break
                        win = game.winnerDeter()
                        if not win == "0":
                            game.reset()
                            print("Game finished, win: ",win)
                            fileLog.write("Game finished, win: "+ win + "\n")
                            output = dict(board = game.getBoard(), player1 = game.getP1Turn(), player2 = game.getP2Turn(), bothConnected = game.bothConnected(), time = timePlayer, end = True)
                            output = json.dumps(output)
                            output = output.encode('utf-8')
                            conn.sendall(output)
                            fileLog.close()
                            break
                        clock.tick()
                    output = dict(board = game.getBoard(), player1 = game.getP1Turn(), player2 = game.getP2Turn(), bothConnected = game.bothConnected(), time = timePlayer, end = False)
                    output = json.dumps(output)
                    output = output.encode('utf-8')
                    conn.sendall(output)
                    if firstSend and game.bothConnected():
                        clock.tick()
                        firstSend = False
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
    sys.exit(0)

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