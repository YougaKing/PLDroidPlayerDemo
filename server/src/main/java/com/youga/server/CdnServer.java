/*
 * Copyright 2015 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.youga.server;

import java.io.IOException;
import java.util.logging.Logger;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

/**
 * Server that manages startup/shutdown of a {@code Greeter} server.
 */
public class CdnServer {
    private static final Logger logger = Logger.getLogger(CdnServer.class.getName());

    private Server server;

    private void start() throws IOException {
        /* The port on which the server should run */
        int port = 50051;
        server = ServerBuilder.forPort(port)
                .addService(new GreeterImpl())
                .build()
                .start();
        logger.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                CdnServer.this.stop();
                System.err.println("*** server shut down");
            }
        });
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    /**
     * Main launches the server from the command line.
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        final CdnServer server = new CdnServer();
        server.start();
        server.blockUntilShutdown();
    }

    static class GreeterImpl extends GreeterGrpc.GreeterImplBase {

        @Override
        public void sendSdn(CdnRequest request, StreamObserver<CdnReply> responseObserver) {
            System.out.println("url:" + request.getUrl() + "\nip:" + request.getIp() + "\nlength:" + request.getLength());
            System.out.println("dnsTime:" + request.getDnsTime());
            System.out.println("httpTime:" + request.getHttpTime());
            System.out.println("firstByte:" + request.getFirstByte());
            System.out.println("parserFirstStream:" + request.getParserFirstStream());
            System.out.println("firstFrame:" + request.getFirstFrame());
            System.out.println("bufferingCount:" + request.getBufferingCount());
            System.out.println("bufferingTime:" + request.getBufferingTime());
            System.out.println("downloadLength:" + request.getDownloadLength());
            System.out.println("downloadTime:" + request.getDownloadTime());

            CdnReply reply = CdnReply.newBuilder()
                    .setCode(1)
                    .build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }
}
