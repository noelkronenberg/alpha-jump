package grpcServer;

import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.stub.StreamObserver;
import de.mcc.FENServiceGrpc;
import de.mcc.FenService;

import java.util.concurrent.TimeUnit;

public class GRPCServer {
    public static void main(String[] args) throws Exception{
        Server server = Grpc.newServerBuilderForPort(9090, InsecureServerCredentials.create()).addService(new ServerImpl()).build();

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

    public static class ServerImpl extends FENServiceGrpc.FENServiceImplBase{
        @Override
        public void getFen(FenService.FENRequest request, StreamObserver<FenService.FENResponse> responseObserver) {
            String req = request.getRequest();
            System.out.println(req);
            if (req.equals("FEN")){
                FenService.FENResponse response = FenService.FENResponse.newBuilder().setAnswer("Beispiel FEN").build();
                // Send the response
                responseObserver.onNext(response);

                // Call onCompleted to signify end of messages
                responseObserver.onCompleted();
            }

        }
    }
}
