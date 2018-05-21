package cdn.youga.instrument;

import com.pili.pldroid.player.PlayerState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * author: YougaKingWu@gmail.com
 * created on: 2018/04/26 12:13
 * description:
 */
public class MediaCollect {

    private static List<MediaMeta> sMetaDataList = new ArrayList<>();

    public static void setDataSource(String url, PlayerState playerState) {
        MediaMeta meta = findMetaData(url);
        if (meta == null) {
            meta = new MediaMeta(url);
            sMetaDataList.add(meta);
        }
        meta.setPlayerState(playerState);
    }

    private static MediaMeta findMetaData(String url) {
        for (MediaMeta meta : sMetaDataList) {
            if (url.equals(meta.getUrl()) && !meta.isDestroyed()) {
                return meta;
            }
        }
        return null;
    }

    public static void prepareAsync(String url, PlayerState playerState) {
        MediaMeta meta = findMetaData(url);
        if (meta == null) setDataSource(url, playerState);
        new LogThread(meta).start();
    }

    public static void playStop(String url, PlayerState playerState) {
        MediaMeta mediaMeta = findMetaData(url);
        if (mediaMeta == null) return;
        sMetaDataList.remove(mediaMeta);
        mediaMeta.setPlayerState(playerState);
        mediaMeta.playStop();
        PldroidCdn.getInstance().addTask(mediaMeta);
    }

    private static class LogThread extends Thread {

        private static final String TAG = "LogThread";
        private MediaMeta mMediaMeta;

        LogThread(MediaMeta mediaMeta) {
            mMediaMeta = mediaMeta;
        }

        @Override
        public void run() {
            super.run();
            String filter = "QCMSG";
            String[] find = new String[]{"logcat", "|find", "@@@QCLOG"};
//            String[] clear = new String[]{"logcat", "-c"};

            Process pro = null;
            BufferedReader bufferedReader = null;
            //筛选需要的字串
            try {
                pro = Runtime.getRuntime().exec(find);
                bufferedReader = new BufferedReader(new InputStreamReader(pro.getInputStream()));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
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
                try {
                    if (bufferedReader != null) bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
