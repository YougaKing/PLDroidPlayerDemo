package cdn.youga.instrument;


import android.util.Log;

import com.pili.pldroid.player.PlayerState;

import java.text.ParseException;

public class MediaMeta {

    private final String TAG = "MediaMeta";
    private String mUrl;
    private PlayerState mPlayerState;
    private Meta mMeta;

    public MediaMeta(String url) {
        mUrl = url;
    }

    public String getUrl() {
        return mUrl;
    }

    public boolean isDestroyed() {
        return mPlayerState == PlayerState.DESTROYED;
    }

    public void setPlayerState(PlayerState playerState) {
        mPlayerState = playerState;
    }

    public void addLogs(String[] logs) {
        if (mMeta == null) mMeta = new Meta();
        try {
            mMeta.parse(logs);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void playStop() {
        mMeta.clearing();
        Log.e(TAG, "url:" + mUrl + "\nip:" + mMeta.ip + "\nlength:" + mMeta.length);
        Log.e(TAG, "dnsTime:" + mMeta.dnsTime);
        Log.e(TAG, "httpTime:" + mMeta.httpTime);
        Log.e(TAG, "firstByte:" + mMeta.firstByte);
        Log.e(TAG, "parserFirstStream:" + mMeta.parserFirstStream);
        Log.e(TAG, "firstFrame:" + mMeta.firstFrame);
        Log.e(TAG, "bufferingCount:" + mMeta.bufferingCount);
        Log.e(TAG, "bufferingTime:" + mMeta.bufferingTime);
    }


}
