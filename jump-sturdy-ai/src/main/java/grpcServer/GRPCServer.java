package grpcServer;

import game.MoveGenerator;
import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.stub.StreamObserver;
import de.mcc.FENServiceGrpc;
import de.mcc.FenService;

import java.util.concurrent.TimeUnit;

public class GRPCServer {

    static MoveGenerator moveGenerator;

    public static void main(String[] args) throws Exception{
        System.out.println(getChessFen("b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/3r04/1r0r01r0r0r01/r0r0r0r0r0r0"));

        Server server = Grpc.newServerBuilderForPort(8080, InsecureServerCredentials.create()).addService(new ServerImpl()).build();

        moveGenerator = new MoveGenerator();
        moveGenerator.initializeBoard();

        server.start();

        // Shut down the server when runtime shuts down (e.g., when CTRL+C is received by the terminal)
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Server is shut down");
            }
        });

        // Wait forever until the server terminates
        server.awaitTermination();
    }

    /**
     *
     *
     * @param gameFen The java internal FEN Notation
     */
    public static String getChessFen(String gameFen){
        String chessFen = "";
        String line = "/1";
        int countSlash = 0;
        boolean maybeHorse = false;
        for (char c : gameFen.toCharArray()){
            if (c=='b'){
                if (maybeHorse) {
                    maybeHorse = false;
                    //Lösche letzten char aus line und ersetze mit Schwarzen Pferd
                    line =  line.substring(0, line.length()-1)+"n";
                    continue;
                }
                line += "p";
                maybeHorse=true;
            }
            else if (c=='r'){
                if (maybeHorse) {
                    maybeHorse = false;
                    //Lösche letzten char aus line und ersetze mit Schwarzen Pferd
                    line =  line.substring(0, line.length()-1)+"N";
                    continue;
                }
                line += "P";
                maybeHorse=true;
            }
            else if (Character.isDigit(c)){
                if (maybeHorse && c=='0'){
                    maybeHorse = false;
                    continue;
                }
                line+=c;
                maybeHorse = false;
            }
            else {  //Slash
                maybeHorse = false;
                if (countSlash==0 || countSlash==7){
                    char last =line.charAt(line.length()-1);
                    if (Character.isDigit(last)){
                        int digit = Character.getNumericValue(last);
                        digit +=1;
                        line += digit;
                    }
                    else {
                        line += "1";
                    }

                    countSlash++;
                    chessFen = line+chessFen;
                    line="/";
                    continue;
                }
                if (countSlash==6){
                    chessFen = line+chessFen;
                    line ="1";
                    countSlash++;
                    continue;
                }
                chessFen = line+chessFen;
                line="/";
                countSlash++;
            }
        }
        char last =line.charAt(line.length()-1);
        if (Character.isDigit(last)){
            int digit = Character.getNumericValue(last);
            digit +=1;
            line += digit;
        }
        else {
            line += "1";
        }
        chessFen = line+chessFen;
        return chessFen;
    }

    public static class ServerImpl extends FENServiceGrpc.FENServiceImplBase{
        @Override
        public void getFen(FenService.FENRequest request, StreamObserver<FenService.FENResponse> responseObserver) {
            String req = request.getRequest();
            String message = request.getMessage();
            System.out.println(req);
            if (req.equals("FEN")){
                FenService.FENResponse response = FenService.FENResponse.newBuilder().setAnswer(getChessFen(moveGenerator.getFenFromBoard())).build();
                // Send the response
                responseObserver.onNext(response);

                // Call onCompleted to signify end of messages
                responseObserver.onCompleted();
            }
            else if (req.equals("MOVE")){    //Client makes a move
                int[] moves = moveGenerator.convertStringToPosWrapper(message);
                //Make Move
                moveGenerator.movePiece(moves[0], moves[1]);

                FenService.FENResponse response = FenService.FENResponse.newBuilder().setAnswer(getChessFen(moveGenerator.getFenFromBoard())).build();

                // Send the response
                responseObserver.onNext(response);

                // Call onCompleted to signify end of messages
                responseObserver.onCompleted();
            }
        }
    }
}
