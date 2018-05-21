package cdn.youga.instrument;

import android.os.AsyncTask;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;

/**
 * author: YougaKingWu@gmail.com
 * created on: 2018/05/21 16:16
 * description:
 */
public class GrpcTask<T> extends AsyncTask<Void, Void, Wrapper<T>> {
    private ManagedChannel mChannel;
    private GrpcRunnable<T> mRunnable;
    private Action<T> mAction;

    public GrpcTask(ManagedChannel channel, GrpcRunnable<T> runnable, Action<T> action) {
        mChannel = channel;
        mRunnable = runnable;
        mAction = action;
    }

    @Override
    protected Wrapper<T> doInBackground(Void... nothing) {
        try {
            return mRunnable.run(GreeterGrpc.newBlockingStub(mChannel), GreeterGrpc.newStub(mChannel));
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            pw.flush();
            return new Wrapper<>(-999, sw.toString());
        }
    }

    @Override
    protected void onPostExecute(Wrapper<T> wrapper) {
        try {
            mChannel.shutdown().awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        mAction.call(wrapper);
    }
}