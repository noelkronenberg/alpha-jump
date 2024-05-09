package game;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.*;

public class MoveGenerator {

    Piece[][] pieceBoard;
    Color[][] colorBoard;

    public void initializeBoard() {
        pieceBoard = new Piece[8][8];
        colorBoard = new Color[8][8];

        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {

                // first rows for each side
                if (row == 0 || row == 7) {

                    // Red
                    if (row == 0) {
                        // border
                        if (column == 0 || column == 7) {
                            pieceBoard[row][column] = null;
                            colorBoard[row][column] = null;
                        // color
                        } else {
                            pieceBoard[row][column] = Piece.SINGLE;
                            colorBoard[row][column] = Color.RED;
                        }

                    // Blue
                    } else if (row == 7) { // NOTE: for 100% coverage change this to else (because always true)
                        // border
                        if (column == 0 || column == 7) {
                            pieceBoard[row][column] = null;
                            colorBoard[row][column] = null;
                        // color
                        } else {
                            pieceBoard[row][column] = Piece.SINGLE;
                            colorBoard[row][column] = Color.BLUE;
                        }
                    }

                }

                // second row for each side
                else if (row == 1 || row == 6) {

                    // Red
                    if (row == 1) {
                        // no piece
                        if (column == 0 || column == 7) {
                            pieceBoard[row][column] = Piece.EMPTY;
                            colorBoard[row][column] = Color.EMPTY;
                         // color
                        } else {
                            pieceBoard[row][column] = Piece.SINGLE;
                            colorBoard[row][column] = Color.RED;
                        }

                    // Blue
                    } else if (row == 6) { // NOTE: for 100% coverage change this to else (because always true)
                        // no piece
                        if (column == 0 || column == 7) {
                            pieceBoard[row][column] = Piece.EMPTY;
                            colorBoard[row][column] = Color.EMPTY;
                        // color
                        } else {
                            pieceBoard[row][column] = Piece.SINGLE;
                            colorBoard[row][column] = Color.BLUE;
                        }
                    }

                }

                // middle
                else if (2 <= row & row <= 5) { // NOTE: for 100% coverage change this to else (because always true)
                    pieceBoard[row][column] = Piece.EMPTY;
                    colorBoard[row][column] = Color.EMPTY;
                }

            }
        }

    }

    public Color getColor(char c) {
        if (c == 'r') {
            return Color.RED;
        } else {
            return Color.BLUE;
        }
    }

    public void initializeBoard(String fen) {
        pieceBoard = new Piece[8][8];
        colorBoard = new Color[8][8];
        char[] fenArray = fen.toCharArray();
        fillBoardEdges(); // is used to fill the shortened edges
        int fenCounter = 0;
        int counter = 1; // is used to jump one column, depending on whether we are in a shot or "normal" row
        for (int row = 0; row < 8; row++) {

            if (row == 7) {
                counter = 1;
            }

            for (int column = 0; column < 8; column++) {
                char fenChar = fenArray[fenCounter];
                if (Character.isLetter(fenChar)) {

                    // CASE: PIECE SINGLE
                    if (fenArray[fenCounter + 1] == '0') {
                        pieceBoard[row][column + counter] = Piece.SINGLE;
                        colorBoard[row][column + counter] = getColor(fenChar);
                    }

                    // CASE: PIECE STACKED
                    else {
                        char fenCharSecondPos = fenArray[fenCounter + 1];

                        // CASE: PIECE MIXED STACK
                        if (fenCharSecondPos != fenChar) {
                            pieceBoard[row][column + counter] = Piece.MIXED;
                            colorBoard[row][column + counter] = getColor(fenCharSecondPos);
                        }

                        // CASE: PIECE SAME COLOR STACK
                        else {
                            pieceBoard[row][column + counter] = Piece.DOUBLE;
                            colorBoard[row][column + counter] = getColor(fenCharSecondPos);
                        }
                    }

                    fenCounter += 2; // we jump 2 lines in the fen, because it either a single: e.g. r0 or stack: e.g. bb
                    if (column + counter == 7 || row == 7 && column + counter == 6) { // first case: end of a normal length line or second case: we are in the last line at the end
                        column = 7;
                        counter = 0;
                        fenCounter++;
                    }

                // CASE: fenChar is a number, then we fill the places with EMPTY
                } else if (Character.isDigit(fenChar)) {
                    int numberOfFreePlaces = fenChar - '0'; // gives the int value for the Character: alternative: Character.getNumericValue(fenChar)
                    for (int j = 0; j < numberOfFreePlaces; j++) {
                        pieceBoard[row][column + counter + j] = Piece.EMPTY;
                        colorBoard[row][column + counter + j] = Color.EMPTY;
                    }
                    counter += numberOfFreePlaces - 1; // numberOfFreePlaces-1 due to column already jumping 1 for each iteration of for-loop
                    fenCounter++;

                    // CASE: end of a normal length line or second case: we are in the last line at the end
                    if (column + counter == 7 || row == 7 && column + counter == 6) {
                        column = 7;
                        counter = 0;
                        fenCounter++;
                    }
                }

                // CASE: this is only the case when we are in row 0, and we are at the end of it: fenChar will be '/' and we use this to jump to the next line
                else {
                    column = 7;
                    fenCounter++;
                    counter = 0;
                }
            }
        }
    }

    void fillBoardEdges() {
        pieceBoard[0][0] = null;
        colorBoard[0][0] = null;
        pieceBoard[0][7] = null;
        colorBoard[0][7] = null;
        pieceBoard[7][0] = null;
        colorBoard[7][0] = null;
        pieceBoard[7][7] = null;
        colorBoard[7][7] = null;
    }

    List<Integer> generatePossibleMoves(int position, Color color) {
        int row = position / 10;
        int column = position % 10;

        List<Integer> possibleMoves = new ArrayList<>();

        // Red
        if (color == Color.RED) {

            // single piece
            if (pieceBoard[row][column] == Piece.SINGLE) {
                if (row > 0) {                              //NOTE: remove: is always true

                    // forward
                    if (colorBoard[row - 1][column] != Color.BLUE) {
                        possibleMoves.add(convertToNumber(row - 1, column));
                    }

                    // left
                    if (column > 0 && colorBoard[row][column - 1] != Color.BLUE)
                        possibleMoves.add(convertToNumber(row, column - 1));

                    // right
                    if (column < 7 && colorBoard[row][column + 1] != Color.BLUE)
                        possibleMoves.add(convertToNumber(row, column + 1));

                    // left diagonal
                    if (column > 0 && colorBoard[row - 1][column - 1] == Color.BLUE) {
                        possibleMoves.add(convertToNumber(row - 1, column - 1));
                    }

                    // right diagonal
                    if (column < 7 && colorBoard[row - 1][column + 1] == Color.BLUE) {
                        possibleMoves.add(convertToNumber(row - 1, column + 1));
                    }
                }
            }

            // double piece
            else if (pieceBoard[row][column] == Piece.DOUBLE || pieceBoard[row][column] == Piece.MIXED) {
                addKnightMoves(possibleMoves, row, column, color);
            }

            // remove invalid moves
            possibleMoves.removeIf(move -> !isValidMove(move, Color.RED));

        // Blue
        } else if (color == Color.BLUE) {               //NOTE: always TRUE--> else

            // single piece
            if (pieceBoard[row][column] == Piece.SINGLE) {
                if (row < 7) {                          //NOTE: Delete

                    // forward
                    if (colorBoard[row + 1][column] != Color.RED) {
                        possibleMoves.add(convertToNumber(row + 1, column));
                    }

                    // left
                    if (column > 0 && colorBoard[row][column - 1] != Color.RED)
                        possibleMoves.add(convertToNumber(row, column - 1));

                    // right
                    if (column < 7 && colorBoard[row][column + 1] != Color.RED)
                        possibleMoves.add(convertToNumber(row, column + 1));

                    // left diagonal
                    if (column > 0 && colorBoard[row + 1][column - 1] == Color.RED) {
                        possibleMoves.add(convertToNumber(row + 1, column - 1));
                    }

                    // right diagonal
                    if (column < 7 && colorBoard[row + 1][column + 1] == Color.RED) {
                        possibleMoves.add(convertToNumber(row + 1, column + 1));
                    }
                }
            }

            // double piece
            else if (pieceBoard[row][column] == Piece.DOUBLE || pieceBoard[row][column] == Piece.MIXED) {       //NOTE: else
                addKnightMoves(possibleMoves, row, column, color);
            }

            // remove invalid moves
            possibleMoves.removeIf(move -> !isValidMove(move, Color.BLUE));
        }

        return possibleMoves;
    }

    private void addKnightMoves(List<Integer> possibleMoves, int row, int column, Color color) {

        // possible moves
        int[][] knightMoves = {
                {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
                {1, -2}, {1, 2}, {2, -1}, {2, 1}
        };

        // add moves
        for (int[] move : knightMoves) {
            int newRow = row + move[0];
            int newColumn = column + move[1];

            if (newRow >= 8 || newColumn >= 8) {
                continue;
            }

            // no moving backwards
            if (color == Color.RED) {
                if (newRow > row) {
                    continue;
                }
            } else if (color == Color.BLUE) {           //NOTE: else
                if (newRow < row) {
                    continue;
                }
            }

            // no moves outside the board
            if (newRow < 0 || newColumn < 0) {
                continue;
            }

            // cannot move to own double
            if (!((pieceBoard[newRow][newColumn] == Piece.DOUBLE || pieceBoard[newRow][newColumn] == Piece.MIXED)
                    && colorBoard[newRow][newColumn] == color)) {
                possibleMoves.add(convertToNumber(newRow, newColumn));
            }
        }
    }

    private boolean isValidMove(int move, Color pieceColor) {
        int row = move / 10;
        int column = move % 10;

        boolean withinBorder = row >= 0 && row < 8 && column >= 0 && column < 8;
        boolean notCut = (row != 0 || column != 0) && (row != 0 || column != 7) &&
                (row != 7 || column != 0) && (row != 7 || column != 7);
        boolean own = (pieceBoard[row][column] == Piece.DOUBLE || pieceBoard[row][column] == Piece.MIXED)
                && colorBoard[row][column] == pieceColor;

        return withinBorder & notCut & !own;
    }

    private int convertToNumber(int row, int column) {
        return row * 10 + column;
    }

    public void movePiece(int start, int end) {
        Piece piece = this.getPieceAtPosition(start);
        Color color = this.getColorAtPosition(start);

        int fromRow = start / 10;
        int fromColumn = start % 10;
        int toRow = end / 10;
        int toColumn = end % 10;

        // adjust old square

        // single
        if (piece == Piece.SINGLE) {
            pieceBoard[fromRow][fromColumn] = Piece.EMPTY;
            colorBoard[fromRow][fromColumn] = Color.EMPTY;
        }

        // double
        else if (piece == Piece.DOUBLE) {
            pieceBoard[fromRow][fromColumn] = Piece.SINGLE;
        }

        // mixed
        else if (piece == Piece.MIXED) {
            pieceBoard[fromRow][fromColumn] = Piece.SINGLE;
            Color oppositeColor = (color == Color.RED) ? Color.BLUE : Color.RED;
            colorBoard[fromRow][fromColumn] = oppositeColor;
        }

        // adjust new square

        // empty or opposite color
        if (colorBoard[toRow][toColumn] == Color.EMPTY ||
                (colorBoard[toRow][toColumn] != color && pieceBoard[toRow][toColumn] == Piece.SINGLE)) {
            pieceBoard[toRow][toColumn] = Piece.SINGLE;
            colorBoard[toRow][toColumn] = color;
        }

        // opposite color and double or mixed
        else if (colorBoard[toRow][toColumn] != color &&
                (pieceBoard[toRow][toColumn] == Piece.DOUBLE || pieceBoard[toRow][toColumn] == Piece.MIXED)) {
            pieceBoard[toRow][toColumn] = Piece.MIXED;
            colorBoard[toRow][toColumn] = color;
        }

        // same color
        else if (colorBoard[toRow][toColumn] == color && pieceBoard[toRow][toColumn] == Piece.SINGLE) {
            pieceBoard[toRow][toColumn] = Piece.DOUBLE;
        }
    }

    public Piece getPieceAtPosition(int position) {
        int row = position / 10;
        int column = position % 10;
        return pieceBoard[row][column];
    }

    public Color getColorAtPosition(int position) {
        int row = position / 10;
        int column = position % 10;
        return colorBoard[row][column];
    }

    public LinkedHashMap<Integer, List<Integer>> generateAllPossibleMoves(Color color) {
        LinkedHashMap<Integer, List<Integer>> allPossibleMoves = new LinkedHashMap<>();

        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {
                if (colorBoard[row][column] == color) {
                    int position = convertToNumber(row, column);
                    List<Integer> piecePossibleMoves = generatePossibleMoves(position, color);
                    if (!piecePossibleMoves.isEmpty()) { // ignores Pieces that have no moves
                        allPossibleMoves.put(position, piecePossibleMoves);
                    }
                }
            }
        }

        return allPossibleMoves;
    }

    public void printBoard(boolean fen) {
        if (fen) {
            System.out.println("     A   B   C   D   E   F   G   H");
        } else {
            System.out.println("     0   1   2   3   4   5   6   7");
        }
        System.out.println();
        for (int row = 7; row >= 0; row--) {
            if (fen) {
                System.out.print(row + 1 + "   ");
            } else {
                System.out.print(row + "   ");
            }
            for (int column = 0; column <= 7; column++) {

                // border
                if (pieceBoard[row][column] == null) {
                    System.out.print("   ");
                }

                // no piece
                else if (pieceBoard[row][column] == Piece.EMPTY) {
                    System.out.print(" . ");
                }

                else {
                    // Red piece
                    if (colorBoard[row][column] == Color.RED) {
                        switch (pieceBoard[row][column]) {
                            case SINGLE:
                                System.out.print(" R ");
                                break;
                            case DOUBLE:
                                System.out.print("RR ");
                                break;
                            case MIXED:
                                System.out.print("BR ");
                                break;
                        }

                    // Blue piece
                    } else {
                        switch (pieceBoard[row][column]) {
                            case SINGLE:
                                System.out.print(" B ");
                                break;
                            case DOUBLE:
                                System.out.print("BB ");
                                break;
                            case MIXED:
                                System.out.print("RB ");
                                break;
                        }
                    }

                }
                System.out.print(" ");
            }
            System.out.println();
        }
        System.out.println();
    }


    public static String convertAllMoves(Map<Integer, List<Integer>> possibleMoves) {
        // Mapping for the columns
        Map<Integer, Character> columnMapping = Map.of(
                0, 'A', 1, 'B', 2, 'C', 3, 'D', 4, 'E', 5, 'F', 6, 'G', 7, 'H'
        );

        StringBuilder formattedOutput = new StringBuilder();

        // Add the possible moves
        for (Map.Entry<Integer, List<Integer>> entry : possibleMoves.entrySet()) {
            int startY = entry.getKey() / 10 + 1;  // Y-coordinate of the starting point
            int startX = entry.getKey() % 10;  // X-coordinate of the starting point
            char startColumn = columnMapping.get(startX);
            for (int targetPosition : entry.getValue()) {
                int targetY = targetPosition / 10 + 1;  // Y-coordinate of the target point
                int targetX = targetPosition % 10;  // X-coordinate of the target point
                char targetColumn = columnMapping.get(targetX);
                formattedOutput.append(startColumn).append(startY).append("-").append(targetColumn).append(targetY).append(", ");
            }
        }

        // Remove the trailing comma and space
        if (!formattedOutput.isEmpty()) {
            formattedOutput.setLength(formattedOutput.length() - 2);
        }

        return formattedOutput.toString();
    }

    public boolean isGameOver(String move, Color opponentColor) {
        if (!move.isEmpty()) {
            Color ourColor = (opponentColor == Color.RED) ? Color.BLUE : Color.RED;
            if (ourColor==Color.RED && doesBaseRowContainEnemy(Color.RED,0)) {
                return true;
            }
            if (ourColor==Color.BLUE && doesBaseRowContainEnemy(Color.BLUE,7)) {
                return true;
            }
            return false;
        }
        return true;
    }

    boolean doesBaseRowContainEnemy(Color enemyColor, int rowToCheck) {
        for (int i = 1; i < 7; i++) {
            if (colorBoard[rowToCheck][i] == enemyColor) {
                return true;
            }
        }
        return false;
    }

    public String getRandomMove(LinkedHashMap<Integer, List<Integer>> moves) {
        Random generator =  new Random();
        ArrayList<Integer> allPieces = new ArrayList<>(moves.keySet());

        int number = generator.nextInt(allPieces.size());
        int randomPiece = allPieces.get(number);

        List<Integer> allMoveToPos = moves.get(randomPiece);
        number = generator.nextInt(allMoveToPos.size());

        int randomPos = allMoveToPos.get(number);
        return convertPosToString(randomPiece) + "-" + convertPosToString(randomPos);
    }

    String convertPosToString(int rowAndColInt) {
        int col = rowAndColInt % 10;
        int row = rowAndColInt / 10;
        String colString = String.valueOf(( (char) (65 + col) ));
        return colString + (row + 1);
    }

    public LinkedHashMap<Integer, List<Integer>> getMovesWrapper(String fen) {
        char color_fen = fen.charAt(fen.length() - 1);
        Color color = this.getColor(color_fen);
        this.initializeBoard(fen.substring(0, fen.length() - 2));
        return this.generateAllPossibleMoves(color);
    }

    int convertStringToPos(String pos) {
        char col=pos.charAt(0);
        char row=pos.charAt(1);

        int rowInt =Character.getNumericValue(row)-1;
        int colInt = col-65;

        return rowInt*10+colInt;
    }

    public int[] convertStringToPosWrapper(String position_string) {
        String[] position_strings = position_string.split("-");

        String start_string = position_strings[0];
        String end_string = position_strings[1];

        int start = this.convertStringToPos(start_string);
        int end =  this.convertStringToPos(end_string);

        return new int[]{start, end};
    }

    public String getFenFromBoard(){
        boolean isCounting = false;
        int counter = 0;
        String s= "";
        for (int i = 0; i < colorBoard.length; i++) {
            for (int j = 0; j < colorBoard.length; j++) {
                if (colorBoard[i][j]==null){
                    continue;
                }
                if (isCounting && colorBoard[i][j]!=Color.EMPTY){
                    isCounting=false;
                    s+=counter;
                    counter=0;
                }
                if (colorBoard[i][j]==Color.EMPTY){
                    isCounting=true;
                    counter++;
                }
                else {
                    if (colorBoard[i][j]==Color.RED){
                        if (pieceBoard[i][j]==Piece.SINGLE){
                            s+="r0";
                        } else if (pieceBoard[i][j]==Piece.DOUBLE){
                            s+="rr";
                        }
                        else {
                            s+="br";
                        }
                    }
                    else {
                        if (pieceBoard[i][j]==Piece.SINGLE){
                            s+="b0";
                        } else if (pieceBoard[i][j]==Piece.DOUBLE){
                            s+="bb";
                        }
                        else {
                            s+="rb";
                        }
                    }
                }
            }
            if (isCounting){
                isCounting=false;
                s+=counter;
                counter=0;
            }
            if (i!=7){
                s+="/";
            }
        }
        //maybe noch Color hinzufügen
        return s;
    }

    public static void main(String[] args) {
        MoveGenerator moveGenerator = new MoveGenerator();
        String fen = "5b0/1bbb0b0brb0b01/8/3b0r03/8/4b03/1rr1b0r0rrrr1/1r04 b";
        for (int i = 0; i < 1; i++) {
            LinkedHashMap<Integer, List<Integer>> moves = moveGenerator.getMovesWrapper(fen);
            System.out.println(moves);

            System.out.println();
            moveGenerator.printBoard(false);

            String move_string = moveGenerator.getRandomMove(moves);
            System.out.println(move_string);
            int[] move_int = moveGenerator.convertStringToPosWrapper(move_string);
            System.out.println(move_int[0] + "-" + move_int[1]);

            System.out.println();
            //moveGenerator.movePiece(move_int[0], move_int[1]);

            System.out.println();
            moveGenerator.printBoard(false);

            System.out.println(moveGenerator.getFenFromBoard());
        }
    }
}
