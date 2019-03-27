package cdn.youga.instrument;

import com.pili.pldroid.player.PlayerState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author: YougaKingWu@gmail.com
 * @created on: 2018/04/26 12:13
 * @description:
 */
public class MediaCollect {

    private static MediaCollect INSTACE;
    private MediaMeta mMediaMeta;
    private LogThread mLogThread;

    public static MediaCollect getInstance() {
        if (INSTACE == null) {
            INSTACE = new MediaCollect();
        }
        return INSTACE;
    }

    public void setDataSource(String url, PlayerState playerState) {
        if (mMediaMeta == null) {
            mMediaMeta = new MediaMeta(url);
        }
        mMediaMeta.setPlayerState(playerState);
    }

    public void prepareAsync(String url, PlayerState playerState) {
        if (mMediaMeta == null) return;
        if (mLogThread == null) {
            mLogThread = new LogThread(mMediaMeta);
            mLogThread.start();
        }
    }

    public void playStop(String url, PlayerState playerState) {
        if (mMediaMeta == null) return;
        mMediaMeta.setPlayerState(playerState);
        mMediaMeta.playStop();
        PldroidCdn.getInstance().upload(mMediaMeta);
        if (mLogThread != null) {
            mLogThread.finish();
            mLogThread = null;
            mMediaMeta = null;
        }
    }

    private static class LogThread extends Thread {

        private MediaMeta mMediaMeta;
        private boolean mFinished;

        LogThread(MediaMeta mediaMeta) {
            mMediaMeta = mediaMeta;
        }

        void finish() {
            mFinished = true;
        }

        @Override
        public void run() {
            super.run();
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
