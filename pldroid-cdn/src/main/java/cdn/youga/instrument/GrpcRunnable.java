package cdn.youga.instrument;

/**
 * author: YougaKingWu@gmail.com
 * created on: 2018/05/21 16:15
 * description:
 */
public interface GrpcRunnable<T> {
    Wrapper<T> run(GreeterGrpc.GreeterBlockingStub blockingStub, GreeterGrpc.GreeterStub asyncStub) throws Exception;
}