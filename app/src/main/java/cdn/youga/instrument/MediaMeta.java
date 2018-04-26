package cdn.youga.instrument;


import android.util.Log;

import com.pili.pldroid.player.PlayerState;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MediaMeta {

    private final String TAG = "MediaMeta";
    private String mUrl;
    private String mIP;
    private long mLength;
    private PlayerState mPlayerState;
    private List<Meta> mMetaList = new ArrayList<>();
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("HH : mm : ss : SSSS", Locale.CHINA);

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

    public void addMeta(Meta meta) {
        mMetaList.add(meta);
    }

    public void playStop() throws ParseException {
        for (Meta meta : mMetaList) {
            if (meta.type.endsWith("HTTP_CONNECT_START")) {
                HTTP_CONNECT_START = mDateFormat.parse(meta.time).getTime();
            } else if (meta.type.endsWith("HTTP_DNS_START")) {
                HTTP_DNS_START = mDateFormat.parse(meta.time).getTime();
            } else if (meta.type.endsWith("HTTP_DNS_GET_CACHE")) {
                HTTP_DNS_GET_CACHE = mDateFormat.parse(meta.time).getTime();
                mIP = meta.extra;
            } else if (meta.type.endsWith("HTTP_CONNECT_SUCESS")) {
                HTTP_CONNECT_SUCESS = mDateFormat.parse(meta.time).getTime();
            } else if (meta.type.endsWith("IO_FIRST_BYTE_DONE")) {
                IO_FIRST_BYTE_DONE = mDateFormat.parse(meta.time).getTime();
            } else if (meta.type.endsWith("HTTP_CONTENT_LEN")) {
                HTTP_CONTENT_LEN = mDateFormat.parse(meta.time).getTime();
                mLength = Long.parseLong(meta.length);
            } else if (meta.type.endsWith("PARSER_NEW_STREAM")) {
                PARSER_NEW_STREAM = mDateFormat.parse(meta.time).getTime();
            } else if (meta.type.endsWith("SNKV_FIRST_FRAME")) {
                SNKV_FIRST_FRAME = mDateFormat.parse(meta.time).getTime();
            }
        }
        Log.e(TAG, "url:" + mUrl + "\nip:" + mIP + "\nlength:" + mLength);
        long dnsTime = HTTP_DNS_GET_CACHE - HTTP_DNS_START;
        Log.e(TAG, "dnsTime:" + dnsTime);
        long httpTime = HTTP_CONNECT_SUCESS - HTTP_CONNECT_START;
        Log.e(TAG, "httpTime:" + httpTime);
        long firstByte = IO_FIRST_BYTE_DONE - HTTP_CONNECT_SUCESS;
        Log.e(TAG, "firstByte:" + firstByte);
        long parserFirstStream = PARSER_NEW_STREAM - IO_FIRST_BYTE_DONE;
        Log.e(TAG, "parserFirstStream:" + parserFirstStream);
        long firstFrame = SNKV_FIRST_FRAME - HTTP_CONNECT_START;
        Log.e(TAG, "firstFrame:" + firstFrame);
    }


    public long HTTP_CONNECT_START;
    public long HTTP_DNS_START;
    public long HTTP_DNS_GET_CACHE;
    public long HTTP_CONNECT_SUCESS;
    public long IO_FIRST_BYTE_DONE;
    public long HTTP_CONTENT_LEN;
    public long PARSER_NEW_STREAM;
    public long SNKV_FIRST_FRAME;
}
