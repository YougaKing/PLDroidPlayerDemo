package cdn.youga.instrument;

import android.text.TextUtils;

import com.pili.pldroid.player.PlayerState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static cdn.youga.instrument.PldroidCdn.ALL;
import static cdn.youga.instrument.PldroidCdn.TCP;

/**
 * @author: YougaKingWu@gmail.com
 * @created on: 2018/04/26 12:13
 * @description:
 */
public class MediaCollect {

    private static final Executor SINGLE_THREAD_EXECUTOR = Executors.newSingleThreadExecutor();
    private static MediaCollect INSTACE;
    private MediaMeta mMediaMeta;
    private LogRunnable mLogRunnable;

    public static MediaCollect getInstance() {
        if (INSTACE == null) {
            INSTACE = new MediaCollect();
        }
        return INSTACE;
    }

    public void setDataSource(String url, PlayerState playerState) {
        if (!isSupportCollectType(url)) return;
        if (mMediaMeta == null) {
            mMediaMeta = new MediaMeta(url);
        }
        mMediaMeta.setPlayerState(playerState);
    }

    public void prepareAsync(String url, PlayerState playerState) {
        if (!isSupportCollectType(url)) return;
        if (mMediaMeta == null) return;
        if (mLogRunnable == null) {
            mLogRunnable = new LogRunnable(mMediaMeta);
            SINGLE_THREAD_EXECUTOR.execute(mLogRunnable);
        }
    }

    public void playStop(String url, PlayerState playerState) {
        if (!isSupportCollectType(url)) return;
        if (mMediaMeta == null) return;
        mMediaMeta.setPlayerState(playerState);
        mMediaMeta.playStop();
        PldroidCdn.getInstance().upload(mMediaMeta);
        if (mLogRunnable != null) {
            mLogRunnable.finish();
            mLogRunnable = null;
            mMediaMeta = null;
        }
    }

    private boolean isSupportCollectType(String url) {
        if (TextUtils.isEmpty(url)) return false;
        if (PldroidCdn.getInstance().getCollectType() == ALL) {
            return true;
        } else if (PldroidCdn.getInstance().getCollectType() == TCP) {
            return url.contains("http://") || url.contains("https://");
        } else {
            return false;
        }
    }

    private static class LogRunnable implements Runnable {

        private MediaMeta mMediaMeta;
        private boolean mFinished;

        LogRunnable(MediaMeta mediaMeta) {
            mMediaMeta = mediaMeta;
        }

        void finish() {
            mFinished = true;
        }

        @Override
        public void run() {
            String filter = "QCMSG";
            String[] find = new String[]{"logcat", "|find", "@@@QCLOG"};
//            String[] clear = new String[]{"logcat", "-c"};

            Process pro;
            BufferedReader bufferedReader = null;
            //筛选需要的字串
            try {
                pro = Runtime.getRuntime().exec(find);
                bufferedReader = new BufferedReader(new InputStreamReader(pro.getInputStream()));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    if (mFinished) {
                        closeBufferedReader(bufferedReader);
                        return;
                    }
                    if (line.contains(filter)) {
                        String log = line.substring(line.indexOf(filter)).replace(filter, "").trim();
                        String[] logs = log.split("[ ]{2,}");
                        mMediaMeta.addLogs(logs);
//                        Runtime.getRuntime().exec(clear);
//                        Log.w(TAG, Arrays.toString(logs));
                        Thread.yield();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeBufferedReader(bufferedReader);
            }
        }

        void closeBufferedReader(BufferedReader bufferedReader) {
            try {
                if (bufferedReader != null) bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
