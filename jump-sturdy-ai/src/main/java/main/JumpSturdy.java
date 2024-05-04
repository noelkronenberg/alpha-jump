import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

//WICHTIG: CODE FUNKTIONIERT NICHT KORREKT

public class JumpSturdy {

    HashSet<Integer> positionOfOurPieces;

    HashSet<String> moves;

    HashMap<Integer, HashSet<String>> movesForFigure;

    HashMap<Integer,String> boardPostions;

    String[] piecesBoard;

    String[] colorsBoard;

    public JumpSturdy() {
        movesForFigure = new HashMap<>();
        positionOfOurPieces=new HashSet<>();
    }

    public void initializeBoardPositonsHM(){
        boardPostions=new HashMap<>();
        int startingRow = 8;
        int startingCol = 66;       //B
        for (int i = 0; i < 60; i++) {
            if (i==54){
                startingRow++;
                startingCol=66;
            }
            else if (i%8==6){
                startingRow++;
                startingCol=65;
            }
            char col=(char)startingCol;
            String row = String.valueOf(startingRow);
            String rowColName=col+row;
            boardPostions.put(i,rowColName);
            startingCol++;
        }
    }

    String getCapitalizedColor(char c){
        if (c=='r'){
            return "R";
        }
        else {
            return "B";
        }
    }

    void getColorAndPiecesBoardForFen(String fen,char player){
        piecesBoard=new String[60];
        colorsBoard=new String[60];
        int boardCounter = 0;      //TODO: nicht zurückzählen... alles nochmal änder.... ich könnte heulen
        char[] fenArray = fen.toCharArray();
        for (int i = 0; i < fenArray.length; i++) {
            char ch = fenArray[i];
            if (Character.isLetter(ch)){
                if (fen.charAt(i+1)=='0'){
                    piecesBoard[boardCounter]="P";
                    colorsBoard[boardCounter]=getCapitalizedColor(ch);
                    if (ch==player){
                        positionOfOurPieces.add(boardCounter);
                    }
                }
                else {
                    char ch2=fenArray[i+1];
                    piecesBoard[boardCounter]="S";
                    String stack = getCapitalizedColor(ch)+Character.toUpperCase(ch2);
                    colorsBoard[boardCounter]=stack;
                    if (ch2==player){
                        positionOfOurPieces.add(boardCounter);
                    }
                }
                i++;    //Damit wir zur nächsten figur/zahl springen
                boardCounter++;
            }
            else if (Character.isDigit(ch)){
                int numberOfFreePlaces =Integer.parseInt(fen.substring(i,i+1));
                for (int j = 0; j < numberOfFreePlaces; j++) {
                    piecesBoard[boardCounter+j]="E";
                    colorsBoard[boardCounter+j]="E";
                }
                boardCounter+=numberOfFreePlaces;
            }
        }
    }

    public void getAllMovesForPlayer(String player, String[] colorsBoard, String[] piecesBoard){ //Player soll sagen, ob wir rot oder blau sind (also Rot = R und Blau = B),
        Iterator<Integer> it=positionOfOurPieces.iterator();
        while (it.hasNext()) {
            int i = it.next();
            if (colorsBoard[i].length()==1) {
                //checke die moves
                HashSet<String> s = calculateAllPostions(player,i,piecesBoard[i], colorsBoard,piecesBoard);
                movesForFigure.put(i,s);
                //moves.clear();

            }
            else {
                if (colorsBoard[i].substring(1).equals(player)){
                    HashSet<String> s=calculateAllPostions(player,i,piecesBoard[i], colorsBoard,piecesBoard);
                    movesForFigure.put(i,s);
                    //moves.clear();
                }
            }
        }
        System.out.println(movesForFigure);
        //Maybe Funkion, die alle moves Souted
    }



    boolean isMovePossible(int from, int to, String[] colorsBoard,String[] piecesBoard,boolean isCapture, boolean isMove){
        if (to>59||to<0){
            return false;
        }
        if (isCapture){
            if (piecesBoard[to]=="P"&& colorsBoard[from]!=colorsBoard[to]){             //Case: wir haben ein enemy piece
                return true;
            }
            if (piecesBoard[to]=="S"&& !colorsBoard[from].equals(colorsBoard[to].substring(1))){     //Case: wir haben ein Stack mit enemy piece oben
                return true;
            }
        }
        if (isMove){
            if (piecesBoard[to]=="E"){                                          //Case: ist leer, also move
                return true;
            }
            if (piecesBoard[to]=="P" && colorsBoard[to]==colorsBoard[from]){    //Case: wir bauen einen Stack
                return true;
            }
        }
        return false;
    }

    HashSet<String> calculateAllPostions(String player, int pos, String piece,String[] colorsBoard,String[] piecesBoard){
        moves=new HashSet<>();
        if (player=="R"){
            if (piece=="P"){
                if (pos%8==6&&pos!=54){                                                                                 //Linker Rand
                    if (pos==6){
                        if (isMovePossible(pos,0,colorsBoard,piecesBoard,true,false)){                      //Capture Rechts
                            moves.add(boardPostions.get(0));
                        }
                        if (isMovePossible(pos,7,colorsBoard,piecesBoard,false,true)){                      //Move Rechts
                            moves.add(boardPostions.get(7));
                        }
                    }
                    else {
                        if (isMovePossible(pos,pos-7,colorsBoard,piecesBoard,true,false)){                  //Capture Rechts
                            moves.add(boardPostions.get(pos-7));
                        }
                        if (isMovePossible(pos,pos-8,colorsBoard,piecesBoard,false,true)){                  //Move gerade
                            moves.add(boardPostions.get(pos-8));
                        }
                        if (isMovePossible(pos,pos+1,colorsBoard,piecesBoard,false,true)){                  //Move Rechts
                            moves.add(boardPostions.get(pos+1));
                        }
                    }
                }
                else if (pos%8==5){                                                                                     //Rechter Rand
                    if (pos==13){
                        if (isMovePossible(pos,5, colorsBoard,piecesBoard,true,false)){                      //Capture Schräg links
                            moves.add(boardPostions.get(5));
                        }
                        if (isMovePossible(pos,12, colorsBoard,piecesBoard,false,true)){                     //Move links
                            moves.add(boardPostions.get(12));
                        }
                    }
                    else {                                                                                              //Schauen ob es mit 5 Faxxen macht... Sollte aber eigentlich nie kommen, weil pos == 5 wäre win!
                        if (isMovePossible(pos,pos-9, colorsBoard,piecesBoard,true,false)){                  //Capture Schräg links
                            moves.add(boardPostions.get(pos-9));
                        }
                        if (isMovePossible(pos,pos-8, colorsBoard,piecesBoard,false,true)){                  //Move gerade aus
                            moves.add(boardPostions.get(pos-8));
                        }
                        if (isMovePossible(pos,pos-1, colorsBoard,piecesBoard,false,true)){                  //Move links
                            moves.add(boardPostions.get(pos-1));
                        }
                    }
                }
                else {
                    if (pos<=12&&pos>=7) {                                                                              //Case: 1 vor der Roten Baseline

                        if (isMovePossible(pos, pos - 7, colorsBoard, piecesBoard, false,true)) {           //Move gerade aus
                            moves.add(boardPostions.get(pos - 7));
                        }
                        if (isMovePossible(pos, pos - 1, colorsBoard, piecesBoard, false,true)) {           //Move links
                            moves.add(boardPostions.get(pos - 1));
                        }
                        if (isMovePossible(pos, pos + 1, colorsBoard, piecesBoard, false,true)) {           //Move links
                            moves.add(boardPostions.get(pos + 1));
                        }
                        //TODO: pos!=7
                        if (pos == 7) {
                            if (isMovePossible(pos, pos - 6, colorsBoard, piecesBoard, true,false)) {            //Capture Schräg rechts
                                moves.add(boardPostions.get(pos - 6));
                            }
                        } else if (pos == 12) {
                            if (isMovePossible(pos, pos - 8, colorsBoard, piecesBoard, true,false)) {            //Capture Schräg links
                                moves.add(boardPostions.get(pos - 8));
                            }
                        } else {
                            if (isMovePossible(pos, pos - 8, colorsBoard, piecesBoard, true,false)) {            //Capture Schräg links
                                moves.add(boardPostions.get(pos - 8));
                            }
                            if (isMovePossible(pos, pos - 6, colorsBoard, piecesBoard, true,false)) {            //Capture Schräg rechts
                                moves.add(boardPostions.get(pos - 6));
                            }
                        }
                    } else if (pos>=54&&pos<=59) {                                                                      //Case: Blaue Baseline

                        if (isMovePossible(pos, pos - 6, colorsBoard, piecesBoard, true,false)) {             //Capture Schräg rechts
                            moves.add(boardPostions.get(pos - 6));
                        }
                        if (isMovePossible(pos, pos - 7, colorsBoard, piecesBoard, false,true)) {            //Move gerade aus
                            moves.add(boardPostions.get(pos - 7));
                        }
                        if (isMovePossible(pos, pos - 8, colorsBoard, piecesBoard, true,false)) {             //Capture Schräg links
                            moves.add(boardPostions.get(pos - 8));
                        }

                        if (pos!=54){                                                                                            //Darf dann nicht nach links, weil er sich in der linken unteren Ecke befindet
                            if (isMovePossible(pos, pos - 1, colorsBoard, piecesBoard, false,true)) {         //Move links
                                moves.add(boardPostions.get(pos - 1));
                            }
                        }
                        if (pos!=59){                                                                                            //Darf dann nicht nach rechts, weil er sich in der rechten unteren Ecke befindet
                            if (isMovePossible(pos, pos + 1, colorsBoard, piecesBoard, false,true)) {         //Move rechts
                                moves.add(boardPostions.get(pos + 1));
                            }
                        }
                    } else {                                                 //ist der "normale" Fall TODO: vielleicht hier den code debuggen und auf laufzeit testen ---> Standardfall irgendwie früher abdecken
                        if (isMovePossible(pos, pos - 7, colorsBoard, piecesBoard, true,false)) {             //Capture Schräg rechts
                            moves.add(boardPostions.get(pos - 7));
                        }
                        if (isMovePossible(pos, pos - 8, colorsBoard, piecesBoard, false,true)) {             //Move gerade aus
                            moves.add(boardPostions.get(pos - 8));
                        }
                        if (isMovePossible(pos, pos - 9, colorsBoard, piecesBoard, true,false)) {             //Capture Schräg links
                            moves.add(boardPostions.get(pos - 9));
                        }
                        if (isMovePossible(pos, pos - 1, colorsBoard, piecesBoard, false,true)) {             //Move links
                            moves.add(boardPostions.get(pos - 1));
                        }
                        if (isMovePossible(pos, pos + 1, colorsBoard, piecesBoard, false,true)) {             //Move rechts
                            moves.add(boardPostions.get(pos + 1));
                        }
                    }
                }
            }
            if (piece=="S"){
                if (53>=pos){
                    int posCalc = pos+2;
                    if (((posCalc/8)-((posCalc-15)/8))==2&&posCalc>0){                                                             //checkt den Sprung oben Rechts
                        if (((posCalc-15)/8)==0){                                                                           //Spezialfall, wenn wir auf die gewinner linie Springen.
                            if (pos!=20) {
                                if (isMovePossible(pos, pos - 14, colorsBoard, piecesBoard, true, true)) {   //Capture & Move Check
                                    moves.add(boardPostions.get(pos - 14));
                                }
                            }
                        }
                        else{           //TODO: Test for edge case pos=20 ob wir hier rein rutschen
                            if (isMovePossible(pos, pos-15, colorsBoard, piecesBoard, true,true)) {          //Capture & Move Check
                                moves.add(boardPostions.get(pos-15));
                            }

                        }
                    }
                    if (((posCalc/8)-((posCalc-17)/8))==2&&posCalc>0){                                                             //checkt den Sprung oben links
                        if (((posCalc-17)/8)==0){                                                                           //Spezialfall, wenn wir auf der gewinner linie sind.
                            if (pos!=15){
                                if (isMovePossible(pos, pos-16, colorsBoard, piecesBoard, true,true)) {      //Capture & Move Check
                                    moves.add(boardPostions.get(pos-16));
                                }
                            }
                        }
                        else{       //TODO: Test for edge case pos=15 ob wir hier rein rutschen
                            if (isMovePossible(pos, pos-17, colorsBoard, piecesBoard, true,true)) {          //Capture & Move Check
                                moves.add(boardPostions.get(pos-17));
                            }
                        }
                    }
                    if (((posCalc/8)-((posCalc-6)/8))==1&&posCalc>0){                                                              //checkt den Sprung Rechts hoch
                        if (((posCalc-6)/8)==0){                                                                              //Spezialfall, wenn wir auf die gewinner linie springen.
                            if (pos!=11){
                                if (isMovePossible(pos, pos-5, colorsBoard, piecesBoard, true,true)) {       //Capture & Move Check
                                    moves.add(boardPostions.get(pos-5));
                                }
                            }
                        }
                        else {       //TODO: Test for edge case pos=11 ob wir hier rein rutschen
                            if (isMovePossible(pos, pos-6, colorsBoard, piecesBoard, true,true)) {           //Capture & Move Check
                                moves.add(boardPostions.get(pos-6));
                            }
                        }
                    }
                    if (((posCalc/8)-((posCalc-10)/8))==1&&posCalc>0){                                                             //checkt den Sprung links hoch
                        if (((posCalc-10)/8)==0){
                            if (pos!=8){
                                if (isMovePossible(pos, pos-9, colorsBoard, piecesBoard, true,true)) {        //Capture & Move Check
                                    moves.add(boardPostions.get(pos-9));
                                }
                            }
                        }
                        else {        //TODO: Test for edge case pos=11 ob wir hier rein rutschen
                            if (isMovePossible(pos, pos-10, colorsBoard, piecesBoard, true,true)) {           //Capture & Move Check
                                moves.add(boardPostions.get(pos-10));
                            }
                        }
                    }
                }
                else {                                                                                                  //fall, wenn wir auf der Baseline sind.
                    int posCalc = pos+2;

                    if (isMovePossible(pos, pos-14, colorsBoard, piecesBoard, true,true)) {                   //Capture & Move Check Gerade Rechts
                        moves.add(boardPostions.get(pos-14));
                    }
                    if (isMovePossible(pos, pos-16, colorsBoard, piecesBoard, true,true)) {                   //Capture & Move Check Gerade Links
                        moves.add(boardPostions.get(pos-16));
                    }

                    if ((posCalc/8)-((posCalc-5)/8)==1){                                                                        //Rechts hoch
                        if (isMovePossible(pos, pos-5, colorsBoard, piecesBoard, true,true)) {               //Capture & Move Check
                            moves.add(boardPostions.get(pos-5));
                        }
                    }
                    if ((posCalc/8)-((posCalc-9)/8)==1){                                                                       //Links hoch
                        if (isMovePossible(pos, pos-9, colorsBoard, piecesBoard, true,true)) {              //Capture & Move Check
                            moves.add(boardPostions.get(pos-9));
                        }
                    }
                }
            }
        }
        if (player=="B"){
            if (piece=="P"){
                if (pos%8==6&&pos!=54){                                                                                 //Linker Rand
                    if (pos==46){
                        if (isMovePossible(pos, 54, colorsBoard, piecesBoard, true,false)) {                //Capture right
                            moves.add(boardPostions.get(54));
                        }
                        if (isMovePossible(pos, 47, colorsBoard, piecesBoard, false,true)) {                //Move right
                            moves.add(boardPostions.get(47));
                        }
                    }
                    else {
                        if (isMovePossible(pos, pos+9, colorsBoard, piecesBoard, true,false)) {             //Capture right
                            moves.add(boardPostions.get(pos+9));
                        }
                        if (isMovePossible(pos, pos+8, colorsBoard, piecesBoard, false,true)) {             //Move straight
                            moves.add(boardPostions.get(pos+8));
                        }
                        if (isMovePossible(pos, pos+1, colorsBoard, piecesBoard, false,true)) {             //Move right
                            moves.add(boardPostions.get(pos+1));
                        }
                    }
                }
                else if (pos%8==5&&pos!=5){                                                                             //Rechter Rand
                    if (pos==53){
                        if (isMovePossible(pos, 59, colorsBoard, piecesBoard, true,false)) {                //Capture left
                            moves.add(boardPostions.get(59));
                        }
                        if (isMovePossible(pos, 52, colorsBoard, piecesBoard, false,true)) {                //Move left
                            moves.add(boardPostions.get(52));
                        }
                    }
                    else {
                        if (isMovePossible(pos, pos+7, colorsBoard, piecesBoard, true,false)) {             //Capture left
                            moves.add(boardPostions.get(pos+7));
                        }
                        if (isMovePossible(pos, pos+8, colorsBoard, piecesBoard, false,true)) {             //Move straight
                            moves.add(boardPostions.get(pos+8));
                        }
                        if (isMovePossible(pos, pos-1, colorsBoard, piecesBoard, false,true)) {             //Move left
                            moves.add(boardPostions.get(pos-1));
                        }
                    }
                }
                else {
                    if (pos >= 47 && 52 <= pos) {
                        if (pos != 52) {
                            if (isMovePossible(pos, pos + 8, colorsBoard, piecesBoard, true, false)) {      //Capture right
                                moves.add(boardPostions.get(pos + 8));
                            }
                        }
                        if (pos != 47) {
                            if (isMovePossible(pos, pos + 6, colorsBoard, piecesBoard, true, false)) {      //Capture left
                                moves.add(boardPostions.get(pos + 6));
                            }
                        }
                        if (isMovePossible(pos, pos + 7, colorsBoard, piecesBoard, false, true)) {          //Move straight
                            moves.add(boardPostions.get(pos + 7));
                        }
                        if (isMovePossible(pos, pos+1, colorsBoard, piecesBoard, false,true)) {             //Move right
                            moves.add(boardPostions.get(pos+1));
                        }
                        if (isMovePossible(pos, pos-1, colorsBoard, piecesBoard, false,true)) {             //Move left
                            moves.add(boardPostions.get(pos-1));
                        }
                    } else if (pos<=5) {            //Case : Rote Baseline
                        if (isMovePossible(pos, pos + 7, colorsBoard, piecesBoard, false, true)) {          //Move straight
                            moves.add(boardPostions.get(pos + 7));
                        }
                        if (isMovePossible(pos, pos+6, colorsBoard, piecesBoard, true,false)) {             //Capture left
                            moves.add(boardPostions.get(pos+6));
                        }
                        if (isMovePossible(pos, pos+8, colorsBoard, piecesBoard, true,false)) {             //Capture right
                            moves.add(boardPostions.get(pos+8));
                        }
                        if (pos!=5){
                            if (isMovePossible(pos, pos+1, colorsBoard, piecesBoard, false,true)) {        //Move right
                                moves.add(boardPostions.get(pos+1));
                            }
                        }
                        if (pos!=0){
                            if (isMovePossible(pos, pos-1, colorsBoard, piecesBoard, false,true)) {        //Move left
                                moves.add(boardPostions.get(pos-1));
                            }
                        }
                    } else {                                                //ist der "normale" Fall
                        if (isMovePossible(pos, pos+7, colorsBoard, piecesBoard, true,false)) {            //Capture left
                            moves.add(boardPostions.get(pos+7));
                        }
                        if (isMovePossible(pos, pos+8, colorsBoard, piecesBoard, false,true)) {            //Move straight
                            moves.add(boardPostions.get(pos+8));
                        }
                        if (isMovePossible(pos, pos+9, colorsBoard, piecesBoard, true,false)) {            //Capture right
                            moves.add(boardPostions.get(pos+9));
                        }
                        if (isMovePossible(pos, pos+1, colorsBoard, piecesBoard, false,true)) {            //Move Right
                            moves.add(boardPostions.get(pos+1));
                        }
                        if (isMovePossible(pos, pos-1, colorsBoard, piecesBoard, false,true)) {            //Move Left
                            moves.add(boardPostions.get(pos-1));
                        }
                    }
                }
            }
            else if (piece=="S"){
                if (6<=pos){
                    int posCalc = pos+2;
                    if ((((posCalc/8)-7))-(((posCalc+17)/8)-7)==-2){        //TODO: Deckt den Rand nicht gut ab, bei allen                                                  //checkt den Sprung unten Rechts
                        if (((posCalc+17)/8)-7==0){                                                                          //Spezialfall, wenn wir auf die Gewinnerlinie springen
                            if (pos!=44){
                                if (isMovePossible(pos, pos+16, colorsBoard, piecesBoard, true,true)) {
                                    moves.add(boardPostions.get(pos+16));
                                }
                            }
                        }
                        else{
                            if (isMovePossible(pos, pos+17, colorsBoard, piecesBoard, true,true)) {
                                moves.add(boardPostions.get(pos+17));
                            }
                        }
                    }
                    if (((posCalc/8)-7)-(((posCalc+15)/8)-7)==-2){                                                         //checkt den Sprung unten links
                        if (((posCalc+15)/8)-7==0){                                                                          //Spezialfall, wenn wir auf die Gewinnerlinie springen
                            if (pos!=39){
                                if (isMovePossible(pos, pos+14, colorsBoard, piecesBoard, true,true)) {
                                    moves.add(boardPostions.get(pos+14));
                                }
                            }
                        }
                        else {
                            if (isMovePossible(pos, pos+15, colorsBoard, piecesBoard, true,true)) {
                                moves.add(boardPostions.get(pos+15));
                            }
                        }
                    }
                    if (((posCalc/8)-7)-(((posCalc+10)/8)-7)==-1&&posCalc<60){                                                         //checkt den Sprung Rechts runter
                        if (((posCalc+10)/8)-7==0){                                                                          //Spezialfall, wenn wir auf die Gewinnerlinie springen
                            if (pos!=51){
                                if (isMovePossible(pos, pos+9, colorsBoard, piecesBoard, true,true)) {
                                    moves.add(boardPostions.get(pos+9));
                                }
                            }
                        }
                        else{
                            if (isMovePossible(pos, pos+10, colorsBoard, piecesBoard, true,true)) {
                                moves.add(boardPostions.get(pos+10));
                            }
                        }
                    }
                    if (((posCalc/8)-7)-(((posCalc+6)/8)-7)==-1&&posCalc<60){                                                          //checkt den Sprung links runter
                        if (((posCalc+6)/8)-7==0){                                                                           //Spezialfall, wenn wir auf die Gewinnerlinie springen
                            if (pos != 48){
                                if (isMovePossible(pos, pos+5, colorsBoard, piecesBoard, true,true)) {
                                    moves.add(boardPostions.get(pos+5));
                                }
                            }
                        }
                        else{
                            if (isMovePossible(pos, pos+6, colorsBoard, piecesBoard, true,true)) {
                                moves.add(boardPostions.get(pos+6));
                            }
                        }
                    }
                }
                else {                                                                                                      //fall wenn wir auf der Baseline sind.
                    int posCalc = pos+2;
                    if (isMovePossible(pos, pos+16, colorsBoard, piecesBoard, true,true)) {             //Gerade Rechts
                        moves.add(boardPostions.get(pos+16));
                    }
                    if (isMovePossible(pos, pos+14, colorsBoard, piecesBoard, true,true)) {             //Gerade Links
                        moves.add(boardPostions.get(pos+14));
                    }
                    if ((((posCalc/8)-7)-(((posCalc+9)/8))-7)==-1){    //rechts Runter
                        if (isMovePossible(pos, pos+9, colorsBoard, piecesBoard, true,true)) {          //Gerade Links
                            moves.add(boardPostions.get(pos+9));
                        }
                    }
                    if ((((posCalc/8)-7)-(((posCalc+5)/8))-7)==-1){    //links Runter
                        if (isMovePossible(pos, pos+5, colorsBoard, piecesBoard, true,true)) {          //Gerade Links
                            moves.add(boardPostions.get(pos+5));
                        }
                    }
                }
            }
        }
        return moves;
    }

    public void printSpielfeld(String notation) {
        String[] zeilen = notation.split("/");

        for (int t = 0; t < zeilen.length; t++) {
            String zeile = zeilen[t];
            for (int i = 0; i < zeile.length(); i++) {
                // In erster und letzter Zeile die Ecken durch '-' kennzeichnen
                if ((t == 0 || t == 7) && i == 0 ) {
                    System.out.print("  ");
                }

                char zelle = zeile.charAt(i);
                if (Character.isDigit(zelle)) {
                    int leerfelder = Character.getNumericValue(zelle);
                    for (int j = 0; j < leerfelder; j++) {
                        System.out.print(".");
                        if (j < leerfelder - 1 || i <  zeile.length() - 1) {
                            System.out.print(" ");
                        }
                    }
                } else {
                    System.out.print(zelle);
                    if (i + 1 < zeile.length() - 1) {
                        System.out.print(" ");
                    }
                }
                if ((t == 0 || t == 7) && i == zeile.length() - 1) {
                    System.out.print("  ");
                }
            }
            System.out.println(); // Neue Zeile nach jeder Zeile
        }
    }

    public void clearAllDataStructure(){
        movesForFigure.clear();
        positionOfOurPieces.clear();
    }

    public static void main(String[] args) {
        JumpSturdy s = new JumpSturdy();
        s.initializeBoardPositonsHM();
        //TODO: IDEE Abhängig von der Implementation
        for (int i = 0; i < 2; i++) {
            String fen = "5b0/1bbb0b0brb0b01/8/3b0r03/8/4b03/1rr1b0r0rrrr1/1r04";//"1r04/1rr1b0r0rrrr1/4b03/8/3b0r03/8/1bbb0b0brb0b01/5b0";
            //s.printSpielfeld(fen);
            s.getColorAndPiecesBoardForFen(fen,'r');
            s.getAllMovesForPlayer("R",s.colorsBoard,s.piecesBoard);
            System.out.println(s.positionOfOurPieces);
            s.clearAllDataStructure();
        }
    }
}