import numpy as np

class Game:
    def __init__(self, id):
        self.p1Turn = True
        self.p2Turn = False
        self.p1Connected = False
        self.p2Connected = False
        self.ready = False
        self.id = id
        self.move = np.array([[None, None],[None, None]]) #first array: x and y from, Second array: x and y to
        self.currentPlayer = "r"
        self.valid = True
        self.winner = 0
        self.oldBoard = np.array([["N","r","r","r","r","r","r","N"],[0,"r","r","r","r","r","r",0],[0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0,0],[0,"b","b","b","b","b","b",0],["N","b","b","b","b","b","b","N"]])
        self.newBoard = np.copy(self.oldBoard)
        self.boardObject = "b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 r"
        self.wins = [0,0]
        self.ties = 0
        self.message = ""
    
    def reset(self):
        self.move = np.array([[None, None],[None, None]]) #first array: x and y from, Second array: x and y to
        self.currentPlayer = "r"
        self.valid = True
        self.winner = "0"
        self.oldBoard = np.array([["N","r","r","r","r","r","r","N"],[0,"r","r","r","r","r","r",0],[0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0,0],[0,"b","b","b","b","b","b",0],["N","b","b","b","b","b","b","N"]])
        self.newBoard = np.copy(self.oldBoard)
        self.boardObject = "b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 r"
        self.p1Turn = True
        self.p2Turn = False
        self.p1Connected = False
        self.p2Connected = False
        self.ready = False
        self.message = ""

    
    
    def playTurn(self, player, move):
        if player == 0:
            if self.p2Turn:
                self.winner = "r"
                return
        if player == 1:
            if self.p1Turn:
                self.winner = "b"
                return
        self.parseMove(move)
        self.validMove()
        if self.valid:
            self.doMove()
            self.parseBoard()
            self.playerChange()    
                
    def winnerDeter(self):
        redBaseLine = self.newBoard[0:1]
        blueBaseLine = self.newBoard[7:8]
        if "r" in blueBaseLine:
            return "r"
        elif "b" in redBaseLine:
            return "b"
        else: return "0"

    def getBoard(self):
        return self.boardObject
    
    def getBoardArray(self):
        return self.newBoard
    
    def getMove(self):
        return self.move
    
    def getWinner(self):
        return self.winner
    
    def getCurrentPlayer(self):
        return self.currentPlayer
    
    def getP1Turn(self):
        return self.p1Turn
    
    def getP2Turn(self):
        return self.p2Turn
    
    def bothConnected(self):
        return self.p1Connected and self.p2Connected
    
    def connectP1(self):
        self.p1Connected = True

    def connectP2(self):
        self.p2Connected = True
    
    def getMessage(self):
        return self.message
    
    def getValid(self):
        return self.valid
    
    def playerChange(self):
        if(self.p1Turn):
            self.p1Turn = False
            self.p2Turn = True
        else:
            self.p1Turn = True
            self.p2Turn = False
        self.valid = True
    
    def validMove(self):
        #check Corners
        if self.move[0,0] == 0 and self.move[0,1] == 0:
            self.valid = False
        if self.move[1,0] == 0 and self.move[1,1] == 0:
            self.valid = False
        
        if self.move[0,0] == 0 and self.move[0,1] == 7:
            self.valid = False
        if self.move[1,0] == 0 and self.move[1,1] == 7:
            self.valid = False

        if self.move[0,0] == 7 and self.move[0,1] == 0:
            self.valid = False
        if self.move[1,0] == 7 and self.move[1,1] == 0:
            self.valid = False

        if self.move[0,0] == 7 and self.move[0,1] == 7:
            self.valid = False
        if self.move[1,0] == 7 and self.move[1,1] == 7:
            self.valid = False
        
        #check out of Bounds
        for i in range(0, self.move.shape[0]):
            for j in range(0, self.move.shape[1]):
                if self.move[i, j] < 0 or self.move[i, j] > 7:
                    self.valid = False
        
        #check if double or single move and if correct piece is moved
        begin = self.move[0]
        end = self.move[-1]
        single = False
        double = False
        check = False
        if self.oldBoard[begin[0]][begin[1]] == "0":
            self.valid = False
        
        if self.oldBoard[begin[0]][begin[1]] == "rr" or self.oldBoard[begin[0]][begin[1]] == "br":
            if self.currentPlayer == "b":
                self.valid = False
            else: double = True
        
        if self.oldBoard[begin[0]][begin[1]] == "bb" or self.oldBoard[begin[0]][begin[1]] == "rb":
            if self.currentPlayer == "r":
                self.valid = False
            else: double = True

        if self.oldBoard[begin[0]][begin[1]] == "r":
            if self.currentPlayer == "b":
                self.valid = False
            else: single = True
        
        if self.oldBoard[begin[0]][begin[1]] == "b":
            if self.currentPlayer == "r":
                self.valid = False
            else: single = True
        
        #check if move is valid with current board state
        #for blue moves
        if self.currentPlayer == "b":
            #check if move includes a check
            if self.oldBoard[end[0]][end[1]] == "r" or self.oldBoard[end[0]][end[1]] == "rr" or self.oldBoard[end[0]][end[1]] == "br":
                check = True

            #check with single
            if check == True and single == True:
                if begin[0]-1 != end[0]:
                    self.valid = False
                if not (begin[1]+1 == end[1] or begin[1]-1 == end[1]):
                    self.valid = False
            #no check with single
            if check == False and single == True:
                if not (self.oldBoard[end[0]][end[1]] == "0" or self.oldBoard[end[0]][end[1]] == "b"):
                    self.valid = False
                if not (begin[0]-1 == end[0] or begin[0] == end[0]):
                    self.valid = False
                if begin[0] == end[0]:
                    if not (begin[1]-1 == end[1] or begin[1]+1 == end[1]):
                        self.valid = False

                if begin[0]-1 == end[0]:
                    if not begin[1] == end[1]:
                        self.valid = False
            #check with double
            if check == True and double == True:
                if begin[0]-2 == end[0]:
                    if not (begin[1]+1 == end[1] or begin[1]-1 == end[1]):
                        self.valid = False
                elif begin[0]-1 == end[0]:
                    if not (begin[1]+2 == end[1] or begin[1]-2 == end[1]):
                        self.valid = False
                else: 
                    self.valid = False
            #no check with double
            if check == False and double == True:
                if not (self.oldBoard[end[0]][end[1]] == "0" or self.oldBoard[end[0]][end[1]] == "b"):
                    self.valid = False
                if begin[0]-2 == end[0]:
                    if not (begin[1]+1 == end[1] or begin[1]-1 == end[1]):
                        self.valid = False
                elif begin[0]-1 == end[0]:
                    if not (begin[1]-2 == end[1] or begin[1]+2 == end[1]):
                        self.valid = False
                else:
                    self.valid = False
        
        #for red moves
        if self.currentPlayer == "r":
            #check if move includes a check
            if self.oldBoard[end[0]][end[1]] == "bb" or self.oldBoard[end[0]][end[1]] == "bb" or self.oldBoard[end[0]][end[1]] == "rb":
                check = True

            #check with single
            if check == True and single == True:
                if begin[0]+1 != end[0]:
                    self.valid = False
                if not (begin[1]+1 == end[1] or begin[1]-1 == end[1]):
                    self.valid = False
            #no check with single
            if check == False and single == True:
                if not (self.oldBoard[end[0]][end[1]] == "0" or self.oldBoard[end[0]][end[1]] == "r"):
                    self.valid = False
                if not (begin[0]+1 == end[0] or begin[0] == end[0]):
                    self.valid = False
                if begin[0] == end[0]:
                    if not (begin[1]-1 == end[1] or begin[1]+1 == end[1]):
                        self.valid = False

                if begin[0]+1 == end[0]:
                    if not begin[1] == end[1]:
                        self.valid = False
            #check with double
            if check == True and double == True:
                if begin[0]+2 == end[0]:
                    if not (begin[1]+1 == end[1] or begin[1]-1 == end[1]):
                        self.valid = False
                elif begin[0]+1 == end[0]:
                    if not (begin[1]+2 == end[1] or begin[1]-2 == end[1]):
                        self.valid = False
                else: 
                    self.valid = False
            #no check with double
            if check == False and double == True:
                if not (self.oldBoard[end[0]][end[1]] == "0" or self.oldBoard[end[0]][end[1]] == "r"):
                    self.valid = False
                if begin[0]+2 == end[0]:
                    if not (begin[1]+1 == end[1] or begin[1]-1 == end[1]):
                        self.valid = False
                elif begin[0]+1 == end[0]:
                    if not (begin[1]-2 == end[1] or begin[1]+2 == end[1]):
                        self.valid = False
                else:
                    self.valid = False
    
    def doMove(self):
        begin = self.move[0]
        end = self.move[-1]
        self.newBoard = np.copy(self.oldBoard)
        
        #moves with red
        #adjust start point of move
        if (self.oldBoard[begin[0]][begin[1]] == "r"):
            self.newBoard[begin[0]][begin[1]] = 0
        if (self.oldBoard[begin[0]][begin[1]] == "br"):
            self.newBoard[begin[0]][begin[1]] = "b"
        if (self.oldBoard[begin[0]][begin[1]] == "rr"):
            self.newBoard[begin[0]][begin[1]] = "r"

        #moves with blue
        #adjust start point of move
        if (self.oldBoard[begin[0]][begin[1]] == "b"):
            self.newBoard[begin[0]][begin[1]] = 0
        if (self.oldBoard[begin[0]][begin[1]] == "rb"):
            self.newBoard[begin[0]][begin[1]] = "r"
        if (self.oldBoard[begin[0]][begin[1]] == "bb"):
            self.newBoard[begin[0]][begin[1]] = "b"

        #adjust end point of move
        if (self.oldBoard[end[0]][end[1]] == "0"):
            if self.currentPlayer == "r":
                self.newBoard[end[0]][end[1]] = "r"
            if self.currentPlayer == "b":
                self.newBoard[end[0]][end[1]] = "b"
        if (self.oldBoard[end[0]][end[1]] == "r"):
            if self.currentPlayer == "r":
                self.newBoard[end[0]][end[1]] = "rr"
            if self.currentPlayer == "b":
                self.newBoard[end[0]][end[1]] = "b"
        if (self.oldBoard[end[0]][end[1]] == "b"):
            if self.currentPlayer == "r":
                self.newBoard[end[0]][end[1]] = "r"
            if self.currentPlayer == "b":
                self.newBoard[end[0]][end[1]] = "bb"
        if (self.oldBoard[end[0]][end[1]] == "br"):
            self.newBoard[end[0]][end[1]] = "bb"
        if (self.oldBoard[end[0]][end[1]] == "rb"):
            self.newBoard[end[0]][end[1]] = "rr"
        if (self.oldBoard[end[0]][end[1]] == "rr"):
            self.newBoard[end[0]][end[1]] = "rb"
        if (self.oldBoard[end[0]][end[1]] == "bb"):
            self.newBoard[end[0]][end[1]] = "br"
        self.oldBoard = np.copy(self.newBoard)

    def parseMove(self, moveInput):
        if not len(moveInput) == 5:
            if self.currentPlayer == "r":
                self.winner = "b"
            else: self.winner = "r"
        
        #ord to get the number to letter (a = 1, b = 2, etc.)
        #8 - number to get the correct move to calculate because the the stupid moves are backwards... Or my stupid array.
        self.move[0,1] = ord(moveInput[0])-ord("A")
        self.move[0,0] = 8 - int(moveInput[1])
        self.move[1,1] = ord(moveInput[3])-ord("A")
        self.move[1,0] = 8 - int(moveInput[4])

    def parseBoard(self):
        boardString = []
        reverseBoard = self.newBoard[::-1]
        for i in reverseBoard:
            for j in i:
                #Parsing Board correctly
                if j == "r":
                    boardString.append("r0")
                elif j == "b":
                    boardString.append("b0")
                elif j == "rr" or j == "bb" or j == "rb" or j == "br":
                    boardString.append(j)
                elif j == "0":
                    boardString.append("X")
            boardString.append("/")
        counter = 0
        position = 0
        for i in boardString:
            if i == "X":
                counter += 1
            if not i == "X":
                if counter > 0:
                    boardString.insert(position, str(counter))
                counter = 0
            position += 1
        boardString[:] = (value for value in boardString if value != "X")
        self.boardObject = "".join(boardString)
        if self.currentPlayer == "r":
            self.currentPlayer = "b"
        else: self.currentPlayer = "r"
        self.boardObject = self.boardObject[:-1] + " " + self.currentPlayer

