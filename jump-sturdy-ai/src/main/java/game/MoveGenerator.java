package game;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.*;

public class MoveGenerator {

    Piece[][] pieceBoard;
    Color[][] colorBoard;
    int totalPossibleMoves;
    int protectedPieces;
    public HashMap<Integer,Integer> capturingHM= new HashMap<>();

    public Piece[][] getPieceBoard() {
        return pieceBoard;
    }
    public Color[][] getColorBoard() {
        return colorBoard;
    }
    public int getTotalPossibleMoves() {
        return totalPossibleMoves;
    }
    public int getProtectedPieces() {
        return protectedPieces;
    }


    // START: board basics

    public void setPieceBoard(Piece[][] givenPieceBoard) {
        for (int row = 0; row < pieceBoard.length; row++) {
            for (int col = 0; col < pieceBoard[row].length; col++) {
                pieceBoard[row][col] = givenPieceBoard[row][col];
            }
        }
    }

    public void setColorBoard(Color[][] givenColorBoard) {
        for (int row = 0; row < colorBoard.length; row++) {
            for (int col = 0; col < colorBoard[row].length; col++) {
                colorBoard[row][col] = givenColorBoard[row][col];
            }
        }
    }

    public void initializeBoard() {
        pieceBoard = new Piece[8][8];
        colorBoard = new Color[8][8];

        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {

                // first rows for each side
                if (row == 0 || row == 7) {

                    // RED
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

                    // BLUE
                    } else if (row == 7) {
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

                    // RED
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

                    // BLUE
                    } else if (row == 6) {
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
                else if (2 <= row & row <= 5) {
                    pieceBoard[row][column] = Piece.EMPTY;
                    colorBoard[row][column] = Color.EMPTY;
                }

            }
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

    public String getFenFromBoard() {
        boolean isCounting = false;
        int counter = 0;
        String s= "";
        for (int i = 0; i < colorBoard.length; i++) {
            for (int j = 0; j < colorBoard.length; j++) {
                if (colorBoard[i][j] == null) {
                    continue;
                }
                if (isCounting && colorBoard[i][j] != Color.EMPTY) {
                    isCounting = false;
                    s += counter;
                    counter = 0;
                }
                if (colorBoard[i][j] == Color.EMPTY) {
                    isCounting = true;
                    counter++;
                }
                else {
                    if (colorBoard[i][j] == Color.RED) {
                        if (pieceBoard[i][j] == Piece.SINGLE) {
                            s += "r0";
                        } else if (pieceBoard[i][j] == Piece.DOUBLE) {
                            s += "rr";
                        }
                        else {
                            s += "br";
                        }
                    }
                    else {
                        if (pieceBoard[i][j] == Piece.SINGLE) {
                            s += "b0";
                        } else if (pieceBoard[i][j] == Piece.DOUBLE) {
                            s += "bb";
                        }
                        else {
                            s += "rb";
                        }
                    }
                }
            }
            if (isCounting) {
                isCounting = false;
                s += counter;
                counter = 0;
            }
            if (i!=7) {
                s += "/";
            }
        }

        // NOTE: maybe add Color
        return s;
    }

    public Color getColor(char c) {
        if (c == 'r') {
            return Color.RED;
        } else {
            return Color.BLUE;
        }
    }

    public Color getColorAtPosition(int position) {
        int row = position / 10;
        int column = position % 10;
        return colorBoard[row][column];
    }

    public Piece getPieceAtPosition(int position) {
        int row = position / 10;
        int column = position % 10;
        return pieceBoard[row][column];
    }

    private int convertToNumber(int row, int column) {
        return row * 10 + column;
    }

    public static String convertPosToString(int rowAndColInt) {
        int col = rowAndColInt % 10;
        int row = rowAndColInt / 10;
        String colString = String.valueOf(( (char) (65 + col) ));
        return colString + (row + 1);
    }

    public static String convertMoveToFEN(int move) {
        int from = move / 100;
        int to = move % 100;
        return convertPosToString(from) + "-" +  convertPosToString(to);
    }

    public static int convertStringToPos(String pos) {
        char col=pos.charAt(0);
        char row=pos.charAt(1);

        int rowInt =Character.getNumericValue(row)-1;
        int colInt = col - 65;

        return rowInt * 10 + colInt;
    }

    public int[] convertStringToPosWrapper(String position_string) {
        String[] position_strings = position_string.split("-");

        String start_string = position_strings[0];
        String end_string = position_strings[1];

        int start = MoveGenerator.convertStringToPos(start_string);
        int end =  MoveGenerator.convertStringToPos(end_string);

        return new int[]{start, end};
    }

    // END: board basics

    // START: move generation

    List<Integer> generatePossibleMoves(int position, Color color) {
        int row = position / 10;
        int column = position % 10;

        List<Integer> possibleMoves = new ArrayList<>();

        // RED
        if (color == Color.RED) {

            // single piece
            if (pieceBoard[row][column] == Piece.SINGLE) {
                if (row > 0) {

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
                        int move=convertToNumber(row - 1, column - 1);
                        possibleMoves.add(move);
                        capturingHM.put((position * 100) + move, 1); // capturing move (for Evaluation)
                    }

                    // left diagonal protection (for Evaluation)
                    else if (column > 0 && colorBoard[row - 1][column - 1] == Color.RED) {
                        protectedPieces += 1;
                    }

                    // right diagonal
                    if (column < 7 && colorBoard[row - 1][column + 1] == Color.BLUE) {
                        int move=convertToNumber(row - 1, column + 1);
                        possibleMoves.add(move);
                        capturingHM.put((position * 100) + move, 1); // capturing move (for Evaluation)
                    }

                    // right diagonal protection (for Evaluation)
                    else if (column < 7 && colorBoard[row - 1][column + 1] == Color.RED) {
                        protectedPieces += 1;
                    }
                }
            }

            // double piece
            else if (pieceBoard[row][column] == Piece.DOUBLE || pieceBoard[row][column] == Piece.MIXED) {
                addKnightMoves(possibleMoves, row, column, color,position);
            }

            // remove invalid moves
            possibleMoves.removeIf(move -> !isValidMove(move, Color.RED));

        // BLUE
        } else if (color == Color.BLUE) {

            // single piece
            if (pieceBoard[row][column] == Piece.SINGLE) {
                if (row < 7) {

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
                        int move = convertToNumber(row + 1, column - 1);
                        possibleMoves.add(move);
                        capturingHM.put((position * 100) + move, 1); // capturing move (for Evaluation)
                    }

                    // left diagonal protection (for Evaluation)
                    else if (column > 0 && colorBoard[row + 1][column - 1] == Color.BLUE) {
                        protectedPieces += 1;
                    }

                    // right diagonal
                    if (column < 7 && colorBoard[row + 1][column + 1] == Color.RED) {
                        int move = convertToNumber(row + 1, column + 1);
                        possibleMoves.add(move);
                        capturingHM.put((position * 100) + move, 1); // capturing move (for Evaluation)
                    }

                    // right diagonal protection (for Evaluation)
                    else if (column < 7 && colorBoard[row + 1][column + 1] == Color.BLUE) {
                        protectedPieces += 1;
                    }
                }
            }

            // double piece
            else if (pieceBoard[row][column] == Piece.DOUBLE || pieceBoard[row][column] == Piece.MIXED) {
                addKnightMoves(possibleMoves, row, column, color,position);
            }

            // remove invalid moves
            possibleMoves.removeIf(move -> !isValidMove(move, Color.BLUE));
        }

        totalPossibleMoves += possibleMoves.size(); // for Evaluation
        return possibleMoves;
    }

    private void addKnightMoves(List<Integer> possibleMoves, int row, int column, Color color, Integer position) {

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
            } else if (color == Color.BLUE) {
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

            // count protected pieces (for Evaluation)
            if (colorBoard[newRow][newColumn] == color) {
                protectedPieces += 1;
            }
            // is attacking
            else if (colorBoard[newRow][newColumn] != color && colorBoard[newRow][newColumn] != Color.EMPTY){
                capturingHM.put((position * 100) + convertToNumber(newRow, newColumn), 1); // capturing move (for Evaluation)
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

    public static String convertAllMoves(Map<Integer, List<Integer>> possibleMoves) {
        // mapping for the columns
        Map<Integer, Character> columnMapping = Map.of(
                0, 'A', 1, 'B', 2, 'C', 3, 'D', 4, 'E', 5, 'F', 6, 'G', 7, 'H'
        );

        StringBuilder formattedOutput = new StringBuilder();

        // add the possible moves
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

        // remove the trailing comma and space
        if (!formattedOutput.isEmpty()) {
            formattedOutput.setLength(formattedOutput.length() - 2);
        }

        return formattedOutput.toString();
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

    public int getRandomMoveInt(LinkedHashMap<Integer, List<Integer>> moves) {
        Random generator =  new Random();
        ArrayList<Integer> allPieces = new ArrayList<>(moves.keySet());

        if (allPieces.size() == 0) {
            return 0;
        }
        int number = generator.nextInt(allPieces.size());
        int randomPiece = allPieces.get(number);

        List<Integer> allMoveToPos = moves.get(randomPiece);
        number = generator.nextInt(allMoveToPos.size());

        int randomPos = allMoveToPos.get(number);
        return randomPiece * 100 + randomPos;
    }

    public LinkedHashMap<Integer, List<Integer>> getMovesWrapper(String fen) {
        char color_fen = fen.charAt(fen.length() - 1);
        Color color = this.getColor(color_fen);
        // System.out.println("Color: " + color);
        String fenBoard=fen.substring(0, fen.length() - 2);
        this.initializeBoard(fenBoard);
        return this.generateAllPossibleMoves(color);
    }

    // END: move generation

    // START: gameplay

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

    public void movePiece(int startEnd) {
        int start = startEnd / 100;
        int end = startEnd % 100;
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

    public LinkedHashMap<Integer, List<Integer>> generateAllPossibleMoves(Color color) {
        capturingHM= new HashMap<>();
        LinkedHashMap<Integer, List<Integer>> allPossibleMoves = new LinkedHashMap<>();
        totalPossibleMoves = 0;
        protectedPieces = 0;

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

    public LinkedHashMap<Integer, List<Integer>> generateMaxOnePossibleMoveForKI(Color color) {
        LinkedHashMap<Integer, List<Integer>> allPossibleMoves = new LinkedHashMap<>();
        totalPossibleMoves = 0;
        protectedPieces = 0;

        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {
                if (colorBoard[row][column] == color) {
                    int position = convertToNumber(row, column);
                    List<Integer> piecePossibleMoves = generatePossibleMoves(position, color);
                    if (!piecePossibleMoves.isEmpty()) { // ignores Pieces that have no moves
                        allPossibleMoves.put(position, piecePossibleMoves);
                        return allPossibleMoves;
                    }
                }
            }
        }

        return allPossibleMoves;
    }

    public boolean isGameOver(Color color) {
        if (color == Color.RED) {
            if (doesBaseRowContainColor(color,0)) {
                return false;
            } else if (doesBaseRowContainColor(Color.BLUE,7) || generateMaxOnePossibleMoveForKI(color).isEmpty()) {
                return true;
            }
        }
        else {
            if (doesBaseRowContainColor(color,7)) {
                return false;
            } else if (doesBaseRowContainColor(Color.RED,0) || generateMaxOnePossibleMoveForKI(color).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public boolean isGameOver(String move, Color color) {
        if (color == Color.RED) {
            if (doesBaseRowContainColor(color,0)) {
                return false;
            } else if (move.isEmpty() || doesBaseRowContainColor(Color.BLUE,7)) {
                return true;
            }
        }
        else {
            if (doesBaseRowContainColor(color,7)) {
                return false;
            } else if (move.isEmpty() || doesBaseRowContainColor(Color.RED,0)) {
                return true;
            }
        }
        return false;
    }

    public boolean isGameOver(LinkedHashMap<Integer, List<Integer>> moves, Color color) {
        if (color == Color.RED) {
            if (doesBaseRowContainColor(color,0)) {
                return false;
            } else if (moves.isEmpty() || doesBaseRowContainColor(Color.BLUE,7)) {
                return true;
            }
        }
        else {
            if (doesBaseRowContainColor(color,7)) {
                return false;
            } else if (moves.isEmpty() || doesBaseRowContainColor(Color.RED,0)) {
                return true;
            }
        }
        return false;
    }

    public boolean isWinForMCTS(Color color) {
        if (color == Color.RED) {
            if (doesBaseRowContainColor(Color.BLUE,7) || generateMaxOnePossibleMoveForKI(color).isEmpty()) {
                return true;
            }
        }
        else {
            if (doesBaseRowContainColor(Color.RED,0) || generateMaxOnePossibleMoveForKI(color).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public int isGameOverMCTS(LinkedHashMap<Integer, List<Integer>> moves, Color color) {
        if (color == Color.RED) {
            if (moves.isEmpty() || doesBaseRowContainColor(Color.BLUE,7)) {
                return 1;
            } else if (doesBaseRowContainColor(color,0)) {
                return -1;
            }
        }
        else {
            if (moves.isEmpty() || doesBaseRowContainColor(Color.RED,0)){
                return -1;
            } else if (doesBaseRowContainColor(color,7)) {
                return 1;
            }
        }
        return 0;
    }

    public boolean isGameOverMCTS(LinkedHashMap<Integer, List<Integer>> moves) {
        //returns true if somebody has won in that position
        if (doesBaseRowContainColor(Color.RED, 0) || doesBaseRowContainColor(Color.BLUE, 7) || moves.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean getWinner(LinkedHashMap<Integer, List<Integer>> moves, Color currPlayer) {
        //returns false if red won, returns true if blue won
        //only use when confirmed that game is over
        if (currPlayer == Color.RED) {
            if (doesBaseRowContainColor(Color.BLUE, 7) || moves.isEmpty()) {
                return true;
            }
        } 
        return false;
    }

    public boolean doesBaseRowContainColor(Color color, int rowToCheck) {
        for (int i = 1; i < 7; i++) {
            if (colorBoard[rowToCheck][i] == color) {
                return true;
            }
        }
        return false;
    }

    // END: gameplay

    // START: visualisation

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

    // END: visualisation

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

            moveGenerator.movePiece(move_int[0], move_int[1]);

            System.out.println();
            moveGenerator.printBoard(false);

            System.out.println(moveGenerator.getFenFromBoard());
        }
    }
}
