package grpcServer;


import de.mcc.FenService;
import io.grpc.*;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import de.mcc.FENServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class AsyncClient {
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws Exception{
        String target = "localhost:9090";

        // Open a channel to the target server
        //TODO: Discuss if we should use TLS credentials
        ManagedChannel channel = Grpc.newChannelBuilder(target, InsecureChannelCredentials.create()).build();

        // Create a blocking stub to communicate with the target server. There is also the option to create an async stub
        FENServiceGrpc.FENServiceStub stub = FENServiceGrpc.newStub(channel);

        while (true) {
            Thread.sleep(300);
            System.out.println("Input request:");
            String req = scanner.nextLine();

            if (req.equals("")) {
                System.out.println("Exiting...");
                break;
            }

            FenService.FENRequest request = FenService.FENRequest.newBuilder().setRequest(req).build();

            StreamObserver<FenService.FENResponse> responseObserver = new StreamObserver<FenService.FENResponse>() {
                @Override
                public void onNext(FenService.FENResponse fenResponse) {
                    System.out.println("Client received response to request: " + request + "\n Response: " + fenResponse);
                }

                @Override
                public void onError(Throwable throwable) {
                    System.err.println("Error in responseObserver");
                    throwable.printStackTrace();
                }

                @Override
                public void onCompleted() {
                    System.out.println("Request: " + request + " is completed");
                }
            };

            stub.getFen(request, responseObserver);
        }
        channel.shutdown();
        channel.awaitTermination(5, TimeUnit.SECONDS);
    }
}