package cdn.youga.instrument;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class PldroidCdn {

    public String mHost;
    public int mPort;
    private ManagedChannel mChannel;
    private static PldroidCdn INSTACE;

    public static PldroidCdn getInstance() {
        if (INSTACE == null) {
            INSTACE = new PldroidCdn();
        }
        return INSTACE;
    }

    public static void init(String host, int port) {
        PldroidCdn pldroidCdn = getInstance();
        pldroidCdn.mHost = host;
        pldroidCdn.mPort = port;
    }

    public void addTask(MediaMeta mediaMeta) {
        if (mChannel == null) {
            mChannel = ManagedChannelBuilder.forAddress(mHost, mPort).usePlaintext().build();
        }
        new GrpcTask<>(mChannel, new MediaMetaRunnable(mediaMeta), new Action<CdnReply>() {
            @Override
            public void call(Wrapper<CdnReply> wrapper) {

            }
        }).execute();
    }


    public class MediaMetaRunnable implements GrpcRunnable<CdnReply> {

        private MediaMeta mMediaMeta;

        public MediaMetaRunnable(MediaMeta mediaMeta) {
            mMediaMeta = mediaMeta;
        }

        @Override
        public Wrapper<CdnReply> run(GreeterGrpc.GreeterBlockingStub blockingStub, GreeterGrpc.GreeterStub asyncStub) throws Exception {

            Meta meta = mMediaMeta.getMeta();
            CdnRequest request = CdnRequest.newBuilder().setUrl(mMediaMeta.getUrl())
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
            return new Wrapper<>(1, blockingStub.sendSdn(request));
        }


    }

}
