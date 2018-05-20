package cdn.youga.instrument;

import android.os.AsyncTask;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class CdnRpc {

    public static String HOST;
    public static int PORT;

    public static void init(String host, int port) {
        HOST = host;
        PORT = port;
    }


    public static void addTask(MediaMeta mediaMeta) {
        new GrpcTask().execute(mediaMeta);
    }

    private static class GrpcTask extends AsyncTask<MediaMeta, Void, Integer> {
        private ManagedChannel channel;

        @Override
        protected Integer doInBackground(MediaMeta... params) {
            MediaMeta mediaMeta = params[0];
            try {
                channel = ManagedChannelBuilder.forAddress(HOST, PORT).usePlaintext().build();
                GreeterGrpc.GreeterBlockingStub stub = GreeterGrpc.newBlockingStub(channel);
                Meta meta = mediaMeta.getMeta();
                CdnRequest request = CdnRequest.newBuilder().setUrl(mediaMeta.getUrl())
                        .setIp(meta.ip)
                        .setLength(meta.length)
                        .setDnsTime(meta.dnsTime)
                        .setHttpTime(meta.httpTime)
                        .setFirstByte(meta.firstByte)
                        .setParserFirstStream(meta.parserFirstStream)
                        .setFirstFrame(meta.firstFrame)
                        .setBufferingCount(meta.bufferingCount)
                        .setBufferingTime(meta.bufferingTime)
                        .setDownloadLength(meta.downloadLength)
                        .setBufferingTime(meta.downloadTime)
                        .build();
                CdnReply reply = stub.sendSdn(request);
                return reply.getCode();
            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                pw.flush();
                return -999;
            }
        }

        @Override
        protected void onPostExecute(Integer code) {
            try {
                channel.shutdown().awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
