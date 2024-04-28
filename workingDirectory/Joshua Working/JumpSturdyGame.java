import java.util.HashMap;



public class JumpSturdyGame {
    String fenBoard;
    boolean isMyTurn;

    HashMap<Integer,String> boardPostions;

    String[] piecesBoard;

    String[] colorsBoard;

    public String getBoard(){
        return fenBoard;
    }

    public boolean isMyTurn() {
        return isMyTurn;
    }

    public void initializeBoardPositonsHM(){
        boardPostions=new HashMap<>();
        int startingRow = 8;
        int startingCol = 66;       //B
        for (int i = 0; i < 60; i++) {
            if (i==54){
                startingRow--;
                startingCol=66;
            }
            else if (i%8==6){
                startingRow--;
                startingCol=65;
            }
            char col=(char)startingCol;
            String row = String.valueOf(startingRow);
            String rowColName=col+row;
            boardPostions.put(i,rowColName);
            startingCol++;
        }
    }

    String getCapitalizedColor(String fen, int index){
        if (fen.charAt(index)=='r'){
            return "R";
        }
        else {
            return "B";
        }
    }

    void getColorAndPiecesBoardForFen(String fen){
        piecesBoard=new String[60];
        colorsBoard=new String[60];
        int boardCounter = 0;
        for (int i = 0; i < fen.length(); i++) {
            if (Character.isLetter(fen.charAt(i))){
                if (fen.charAt(i+1)=='0'){
                    piecesBoard[boardCounter]="P";
                    colorsBoard[boardCounter]=getCapitalizedColor(fen,i);
                }
                else {
                    piecesBoard[boardCounter]="S";
                    String stack = getCapitalizedColor(fen,i)+getCapitalizedColor(fen,i+1);
                    colorsBoard[boardCounter]=stack;
                }
                i++;    //Damit wir zur nächsten figut/zahl springen
                boardCounter++;
            }
            else if (Character.isDigit(fen.charAt(i))){
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
        HashMap<Integer,String> movesForFigure = new HashMap<>();
        for (int i = 0; i < colorsBoard.length; i++) {
            if (colorsBoard[i].length()==1) {
                //checke die moves
                if (colorsBoard[i].equals(player)){
                    String s=calculateAllPostions(player,i,piecesBoard[i], colorsBoard,piecesBoard);
                    movesForFigure.put(i,s);
                }
            }
            else {
                if (colorsBoard[i].substring(1).equals(player)){
                    String s=calculateAllPostions(player,i,piecesBoard[i], colorsBoard,piecesBoard);
                    movesForFigure.put(i,s);
                }
            }
        }
        System.out.println(movesForFigure);
        //Maybe Funkion, die alle moves Souted
    }



    boolean isMovePossible(int from, int to, String[] colorsBoard,String[] piecesBoard,boolean isCapture, boolean isMove){
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

    String calculateAllPostions(String player, int pos, String piece,String[] colorsBoard,String[] piecesBoard){
        String moves = "";
        if (player=="B"){
            if (piece=="P"){
                if (pos%8==6&&pos!=54){                                                                                 //Linker Rand
                    if (pos==6){
                        if (isMovePossible(pos,0,colorsBoard,piecesBoard,true,false)){                      //Capture Rechts
                            moves+=boardPostions.get(0)+",";
                        }
                        if (isMovePossible(pos,7,colorsBoard,piecesBoard,false,true)){                      //Move Rechts
                            moves+=boardPostions.get(7)+",";
                        }
                    }
                    else {
                        if (isMovePossible(pos,pos-7,colorsBoard,piecesBoard,true,false)){                  //Capture Rechts
                            moves+=boardPostions.get(pos-7)+",";
                        }
                        if (isMovePossible(pos,pos-8,colorsBoard,piecesBoard,false,true)){                  //Move gerade
                            moves+=boardPostions.get(pos-8)+",";
                        }
                        if (isMovePossible(pos,pos+1,colorsBoard,piecesBoard,false,true)){                  //Move Rechts
                            moves+=boardPostions.get(pos+1)+",";
                        }
                    }
                }
                else if (pos%8==5){                                                                                     //Rechter Rand
                    if (pos==13){
                        if (isMovePossible(pos,5,colorsBoard,piecesBoard,true,false)){                      //Capture Schräg links
                            moves+=boardPostions.get(5)+",";
                        }
                        if (isMovePossible(pos,12,colorsBoard,piecesBoard,false,true)){                     //Move links
                            moves+=boardPostions.get(12)+",";
                        }
                    }
                    else {                                                                                              //Schauen ob es mit 5 Faxxen macht... Sollte aber eigentlich nie kommen, weil pos == 5 wäre win!
                        if (isMovePossible(pos,pos-9,colorsBoard,piecesBoard,true,false)){                  //Capture Schräg links
                            moves+=boardPostions.get(pos-9)+",";
                        }
                        if (isMovePossible(pos,pos-8,colorsBoard,piecesBoard,false,true)){                  //Move gerade aus
                            moves+=boardPostions.get(pos-8)+",";
                        }
                        if (isMovePossible(pos,pos-1,colorsBoard,piecesBoard,false,true)){                  //Move links
                            moves+=boardPostions.get(pos-1)+",";
                        }
                    }
                }
                else {
                    if (pos<=12&&pos>=7) {                                                                              //Case: 1 vor der Roten Baseline

                        if (isMovePossible(pos, pos - 7, colorsBoard, piecesBoard, false,true)) {           //Move gerade aus
                            moves+=boardPostions.get(pos - 7)+",";
                        }
                        if (isMovePossible(pos, pos - 1, colorsBoard, piecesBoard, false,true)) {           //Move links
                            moves+=boardPostions.get(pos - 1)+",";
                        }
                        if (isMovePossible(pos, pos + 1, colorsBoard, piecesBoard, false,true)) {           //Move links
                            moves+=boardPostions.get(pos + 1)+",";
                        }
                        //TODO: pos!=7
                        if (pos == 7) {
                            if (isMovePossible(pos, pos - 6, colorsBoard, piecesBoard, true,false)) {            //Capture Schräg rechts
                                moves+=boardPostions.get(pos - 6)+",";
                            }
                        } else if (pos == 12) {
                            if (isMovePossible(pos, pos - 8, colorsBoard, piecesBoard, true,false)) {            //Capture Schräg links
                                moves+=boardPostions.get(pos - 8)+",";
                            }
                        } else {
                            if (isMovePossible(pos, pos - 8, colorsBoard, piecesBoard, true,false)) {            //Capture Schräg links
                                moves+=boardPostions.get(pos - 8)+",";
                            }
                            if (isMovePossible(pos, pos - 6, colorsBoard, piecesBoard, true,false)) {            //Capture Schräg rechts
                                moves+=boardPostions.get(pos - 6)+",";
                            }
                        }
                    } else if (pos>=54&&pos<=59) {                                                                      //Case: Blaue Baseline

                        if (isMovePossible(pos, pos - 6, colorsBoard, piecesBoard, true,false)) {             //Capture Schräg rechts
                            moves+=boardPostions.get(pos - 6)+",";
                        }
                        if (isMovePossible(pos, pos - 7, colorsBoard, piecesBoard, false,true)) {            //Move gerade aus
                            moves+=boardPostions.get(pos - 7)+",";
                        }
                        if (isMovePossible(pos, pos - 8, colorsBoard, piecesBoard, true,false)) {             //Capture Schräg links
                            moves+=boardPostions.get(pos - 8)+",";
                        }

                        if (pos!=54){                                                                                            //Darf dann nicht nach links, weil er sich in der linken unteren Ecke befindet
                            if (isMovePossible(pos, pos - 1, colorsBoard, piecesBoard, false,true)) {         //Move links
                                moves+=boardPostions.get(pos - 1)+",";
                            }
                        }
                        if (pos!=59){                                                                                            //Darf dann nicht nach rechts, weil er sich in der rechten unteren Ecke befindet
                            if (isMovePossible(pos, pos + 1, colorsBoard, piecesBoard, false,true)) {         //Move rechts
                                moves+=boardPostions.get(pos + 1)+",";
                            }
                        }
                    } else {                                                 //ist der "normale" Fall TODO: vielleicht hier den code debuggen und auf laufzeit testen ---> Standardfall irgendwie früher abdecken
                        if (isMovePossible(pos, pos - 7, colorsBoard, piecesBoard, true,false)) {             //Capture Schräg rechts
                            moves+=boardPostions.get(pos - 7)+",";
                        }
                        if (isMovePossible(pos, pos - 8, colorsBoard, piecesBoard, false,true)) {             //Move gerade aus
                            moves+=boardPostions.get(pos - 8)+",";
                        }
                        if (isMovePossible(pos, pos - 9, colorsBoard, piecesBoard, true,false)) {             //Capture Schräg links
                            moves+=boardPostions.get(pos - 9)+",";
                        }
                        if (isMovePossible(pos, pos - 1, colorsBoard, piecesBoard, false,true)) {             //Move links
                            moves+=boardPostions.get(pos - 1)+",";
                        }
                        if (isMovePossible(pos, pos + 1, colorsBoard, piecesBoard, false,true)) {           //Move rechts
                            moves+=boardPostions.get(pos + 1)+",";
                        }
                    }
                }
            }
            if (piece=="S"){
                if (53>=pos){
                    int posCalc = pos+2;
                    if (((posCalc/8)-((posCalc-15)/8))==2){                                                             //checkt den Sprung oben Rechts
                        if (((posCalc-15)/8)==0){                                                                           //Spezialfall, wenn wir auf die gewinner linie Springen.
                            if (pos!=20) {
                                if (isMovePossible(pos, pos - 14, colorsBoard, piecesBoard, true, true)) {   //Capture & Move Check
                                    moves+=boardPostions.get(pos - 14)+",";
                                }
                            }
                        }
                        else{           //TODO: Test for edge case pos=20 ob wir hier rein rutschen
                            if (isMovePossible(pos, pos-15, colorsBoard, piecesBoard, true,true)) {          //Capture & Move Check
                                moves+=boardPostions.get(pos-15)+",";
                            }

                        }
                    }
                    if (((posCalc/8)-((posCalc-17)/8))==2){                                                             //checkt den Sprung oben links
                        if (((posCalc-17)/8)==0){                                                                           //Spezialfall, wenn wir auf der gewinner linie sind.
                            if (pos!=15){
                                if (isMovePossible(pos, pos-16, colorsBoard, piecesBoard, true,true)) {      //Capture & Move Check
                                    moves+=boardPostions.get(pos-16)+",";
                                }
                            }
                        }
                        else{       //TODO: Test for edge case pos=15 ob wir hier rein rutschen
                            if (isMovePossible(pos, pos-17, colorsBoard, piecesBoard, true,true)) {          //Capture & Move Check
                                moves+=boardPostions.get(pos-17)+",";
                            }
                        }
                    }
                    if (((posCalc/8)-((posCalc-6)/8))==1){                                                              //checkt den Sprung Rechts hoch
                        if (((posCalc-6)/8)==0){                                                                              //Spezialfall, wenn wir auf die gewinner linie springen.
                            if (pos!=11){
                                if (isMovePossible(pos, pos-5, colorsBoard, piecesBoard, true,true)) {       //Capture & Move Check
                                    moves+=boardPostions.get(pos-5)+",";
                                }
                            }
                        }
                        else {       //TODO: Test for edge case pos=11 ob wir hier rein rutschen
                            if (isMovePossible(pos, pos-6, colorsBoard, piecesBoard, true,true)) {           //Capture & Move Check
                                moves+=boardPostions.get(pos-6)+",";
                            }
                        }
                    }
                    if (((posCalc/8)-((posCalc-10)/8))==1){                                                             //checkt den Sprung links hoch
                        if (((posCalc-10)/8)==0){
                            if (pos!=8){
                                if (isMovePossible(pos, pos-9, colorsBoard, piecesBoard, true,true)) {        //Capture & Move Check
                                    moves+=boardPostions.get(pos-9)+",";
                                }
                            }
                        }
                        else {        //TODO: Test for edge case pos=11 ob wir hier rein rutschen
                            if (isMovePossible(pos, pos-10, colorsBoard, piecesBoard, true,true)) {           //Capture & Move Check
                                moves+=boardPostions.get(pos-10)+",";
                            }
                        }
                    }
                }
                else {                                                                                                  //fall, wenn wir auf der Baseline sind.
                    int posCalc = pos+2;

                    if (isMovePossible(pos, pos-14, colorsBoard, piecesBoard, true,true)) {                   //Capture & Move Check Gerade Rechts
                        moves+=boardPostions.get(pos-14)+",";
                    }
                    if (isMovePossible(pos, pos-16, colorsBoard, piecesBoard, true,true)) {                   //Capture & Move Check Gerade Links
                        moves+=boardPostions.get(pos-16)+",";
                    }

                    if ((posCalc/8)-((posCalc-5)/8)==1){                                                                        //Rechts hoch
                        if (isMovePossible(pos, pos-5, colorsBoard, piecesBoard, true,true)) {               //Capture & Move Check
                            moves+=boardPostions.get(pos-5)+",";
                        }
                    }
                    if ((posCalc/8)-((posCalc-9)/8)==1){                                                                       //Links hoch
                        if (isMovePossible(pos, pos-9, colorsBoard, piecesBoard, true,true)) {              //Capture & Move Check
                            moves+=boardPostions.get(pos-9)+",";
                        }
                    }
                }
            }
        }
        if (player=="R"){
            if (piece=="P"){
                if (pos%8==6&&pos!=54){                                                                                 //Linker Rand
                    if (pos==46){
                        if (isMovePossible(pos, 54, colorsBoard, piecesBoard, true,false)) {                //Capture right
                            moves+=boardPostions.get(54)+",";
                        }
                        if (isMovePossible(pos, 47, colorsBoard, piecesBoard, false,true)) {                //Move right
                            moves+=boardPostions.get(47)+",";
                        }
                    }
                    else {
                        if (isMovePossible(pos, pos+9, colorsBoard, piecesBoard, true,false)) {             //Capture right
                            moves+=boardPostions.get(pos+9)+",";
                        }
                        if (isMovePossible(pos, pos+8, colorsBoard, piecesBoard, false,true)) {             //Move straight
                            moves+=boardPostions.get(pos+8)+",";
                        }
                        if (isMovePossible(pos, pos+1, colorsBoard, piecesBoard, false,true)) {             //Move right
                            moves+=boardPostions.get(pos+1)+",";
                        }
                    }
                }
                else if (pos%8==5&&pos!=5){                                                                             //Rechter Rand
                    if (pos==53){
                        if (isMovePossible(pos, 59, colorsBoard, piecesBoard, true,false)) {                //Capture left
                            moves+=boardPostions.get(59)+",";
                        }
                        if (isMovePossible(pos, 52, colorsBoard, piecesBoard, false,true)) {                //Move left
                            moves+=boardPostions.get(52)+",";
                        }
                    }
                    else {
                        if (isMovePossible(pos, pos+7, colorsBoard, piecesBoard, true,false)) {             //Capture left
                            moves+=boardPostions.get(pos+7)+",";
                        }
                        if (isMovePossible(pos, pos+8, colorsBoard, piecesBoard, false,true)) {             //Move straight
                            moves+=boardPostions.get(pos+8)+",";
                        }
                        if (isMovePossible(pos, pos-1, colorsBoard, piecesBoard, false,true)) {             //Move left
                            moves+=boardPostions.get(pos-1)+",";
                        }
                    }
                }
                else {
                    if (pos >= 47 && 52 <= pos) {
                        if (pos != 52) {
                            if (isMovePossible(pos, pos + 8, colorsBoard, piecesBoard, true, false)) {      //Capture right
                                moves+=boardPostions.get(pos + 8)+",";
                            }
                        }
                        if (pos != 47) {
                            if (isMovePossible(pos, pos + 6, colorsBoard, piecesBoard, true, false)) {      //Capture left
                                moves+=boardPostions.get(pos + 6)+",";
                            }
                        }
                        if (isMovePossible(pos, pos + 7, colorsBoard, piecesBoard, false, true)) {          //Move straight
                            moves+=boardPostions.get(pos + 7)+",";
                        }
                        if (isMovePossible(pos, pos+1, colorsBoard, piecesBoard, false,true)) {             //Move right
                            moves+=boardPostions.get(pos+1)+",";
                        }
                        if (isMovePossible(pos, pos-1, colorsBoard, piecesBoard, false,true)) {             //Move left
                            moves+=boardPostions.get(pos-1)+",";
                        }
                    } else if (pos<=5) {            //Case : Rote Baseline
                        if (isMovePossible(pos, pos + 7, colorsBoard, piecesBoard, true, false)) {          //Move straight
                            moves+=boardPostions.get(pos + 7)+",";
                        }
                        if (isMovePossible(pos, pos+6, colorsBoard, piecesBoard, true,false)) {             //Capture left
                            moves+=boardPostions.get(pos+6)+",";
                        }
                        if (isMovePossible(pos, pos+8, colorsBoard, piecesBoard, true,false)) {             //Capture right
                            moves+=boardPostions.get(pos+8)+",";
                        }
                        if (pos!=5){
                            if (isMovePossible(pos, pos+1, colorsBoard, piecesBoard, false,true)) {        //Move right
                                moves+=boardPostions.get(pos+1)+",";
                            }
                        }
                        if (pos!=0){
                            if (isMovePossible(pos, pos-1, colorsBoard, piecesBoard, false,true)) {        //Move left
                                moves+=boardPostions.get(pos-1)+",";
                            }
                        }
                    } else {                                                //ist der "normale" Fall
                        if (isMovePossible(pos, pos+7, colorsBoard, piecesBoard, true,false)) {            //Capture left
                            moves+=boardPostions.get(pos+7)+",";
                        }
                        if (isMovePossible(pos, pos+8, colorsBoard, piecesBoard, false,true)) {            //Move straight
                            moves+=boardPostions.get(pos+8)+",";
                        }
                        if (isMovePossible(pos, pos+9, colorsBoard, piecesBoard, true,false)) {            //Capture right
                            moves+=boardPostions.get(pos+9)+",";
                        }
                        if (isMovePossible(pos, pos+1, colorsBoard, piecesBoard, false,true)) {            //Move Right
                            moves+=boardPostions.get(pos+1)+",";
                        }
                        if (isMovePossible(pos, pos-1, colorsBoard, piecesBoard, false,true)) {            //Move Left
                            moves+=boardPostions.get(pos-1)+",";
                        }
                    }
                }
            }
            else if (piece=="S"){
                if (6>=pos){
                    int posCalc = pos+2;
                    if ((((posCalc/8)-7)-(((posCalc+17)/8))-7)==-2){                                                         //checkt den Sprung unten Rechts
                        if (((posCalc+17)/8)-7==0){                                                                          //Spezialfall, wenn wir auf die Gewinnerlinie springen
                            if (pos!=44){
                                if (isMovePossible(pos, pos+16, colorsBoard, piecesBoard, true,true)) {
                                    moves+=boardPostions.get(pos+16)+",";
                                }
                            }
                        }
                        else{
                            if (isMovePossible(pos, pos+17, colorsBoard, piecesBoard, true,true)) {
                                moves+=boardPostions.get(pos+17)+",";
                            }
                        }
                    }
                    if ((((posCalc/8)-7)-(((posCalc+15)/8))-7)==-2){                                                         //checkt den Sprung unten links
                        if (((posCalc+15)/8)-7==0){                                                                          //Spezialfall, wenn wir auf die Gewinnerlinie springen
                            if (pos!=39){
                                if (isMovePossible(pos, pos+14, colorsBoard, piecesBoard, true,true)) {
                                    moves+=boardPostions.get(pos+14)+",";
                                }
                            }
                        }
                        else {
                            if (isMovePossible(pos, pos+15, colorsBoard, piecesBoard, true,true)) {
                                moves+=boardPostions.get(pos+15)+",";
                            }
                        }
                    }
                    if ((((posCalc/8)-7)-(((posCalc+10)/8))-7)==-1){                                                         //checkt den Sprung Rechts runter
                        if (((posCalc+10)/8)-7==0){                                                                          //Spezialfall, wenn wir auf die Gewinnerlinie springen
                            if (pos!=51){
                                if (isMovePossible(pos, pos+9, colorsBoard, piecesBoard, true,true)) {
                                    moves+=boardPostions.get(pos+9)+",";
                                }
                            }
                        }
                        else{
                            if (isMovePossible(pos, pos+10, colorsBoard, piecesBoard, true,true)) {
                                moves+=boardPostions.get(pos+10)+",";
                            }
                        }
                    }
                    if ((((posCalc/8)-7)-(((posCalc+6)/8))-7)==-1){                                                          //checkt den Sprung links runter
                        if (((posCalc+6)/8)-7==0){                                                                           //Spezialfall, wenn wir auf die Gewinnerlinie springen
                            if (pos != 48){
                                if (isMovePossible(pos, pos+5, colorsBoard, piecesBoard, true,true)) {
                                    moves+=boardPostions.get(pos+5)+",";
                                }
                            }
                        }
                        else{
                            if (isMovePossible(pos, pos+6, colorsBoard, piecesBoard, true,true)) {
                                moves+=boardPostions.get(pos+6)+",";
                            }
                        }
                    }
                }
                else {                                                                                                      //fall wenn wir auf der Baseline sind.
                    int posCalc = pos+2;
                    if (isMovePossible(pos, pos+16, colorsBoard, piecesBoard, true,true)) {             //Gerade Rechts
                        moves+=boardPostions.get(pos+16)+",";
                    }
                    if (isMovePossible(pos, pos+14, colorsBoard, piecesBoard, true,true)) {             //Gerade Links
                        moves+=boardPostions.get(pos+14)+",";
                    }
                    if ((((posCalc/8)-7)-(((posCalc+9)/8))-7)==-1){    //rechts Runter
                        if (isMovePossible(pos, pos+9, colorsBoard, piecesBoard, true,true)) {          //Gerade Links
                            moves+=boardPostions.get(pos+9)+",";
                        }
                    }
                    if ((((posCalc/8)-7)-(((posCalc+5)/8))-7)==-1){    //links Runter
                        if (isMovePossible(pos, pos+5, colorsBoard, piecesBoard, true,true)) {          //Gerade Links
                            moves+=boardPostions.get(pos+5)+",";
                        }
                    }
                }
            }
        }
        return moves;
    }

    public static void main(String[] args) {
        JumpSturdyGame s = new JumpSturdyGame();
        s.initializeBoardPositonsHM();
        String fen = "r0r0r0r0r0r0/1r0r0r0r0r0r01/8/8/8/8/1bbb0b0b0b0b01/b0b0b0b0b0b0";
        s.getColorAndPiecesBoardForFen(fen);
        s.getAllMovesForPlayer("B",s.colorsBoard,s.piecesBoard);
        String str = s.colorsBoard[47].substring(1);
        System.out.println(str.equals("B"));
    }

}

