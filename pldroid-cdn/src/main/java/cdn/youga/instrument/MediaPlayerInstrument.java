package cdn.youga.instrument;

import android.util.Log;

import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PlayerState;
import com.qiniu.qplayer.mediaEngine.MediaPlayer;

import java.lang.ref.WeakReference;
import java.util.Map;

import static cdn.youga.instrument.EventCodes.QC_MSG_HTTP_CONNECT_FAILED;
import static cdn.youga.instrument.EventCodes.QC_MSG_HTTP_CONNECT_START;
import static cdn.youga.instrument.EventCodes.QC_MSG_HTTP_CONNECT_SUCESS;
import static cdn.youga.instrument.EventCodes.QC_MSG_HTTP_DNS_START;
import static cdn.youga.instrument.EventCodes.QC_MSG_PLAY_OPEN_DONE;
import static cdn.youga.instrument.EventCodes.QC_MSG_PLAY_STOP;
import static cdn.youga.instrument.EventCodes.QC_MSG_PLAY_UNKNOW;
import static cdn.youga.instrument.EventCodes.QC_MSG_SNKV_NEW_FORMAT;

/**
 * @author: YougaKingWu@gmail.com
 * @created on: 2018/04/26 12:13
 * @description:
 */
public class MediaPlayerInstrument {


    private static final String TAG = "MediaPlayerInstrument";

    /**
     * com.youga.pldroid.MediaPlayerInstrument.setAVOptions(\$1);
     */
    public static void setAVOptions(AVOptions avOptions) {
        Log.e(TAG, "setAVOptions():" + avOptions);
        MediaCollect.getInstance().setAVOptions(avOptions);
    }

    /**
     * com.youga.pldroid.MediaPlayerInstrument.setDataSource(\$1, \$2, \$0);
     */
    public static void setDataSource(String url, Map<String, String> header, MediaPlayer mediaPlayer) {
        Log.e(TAG, "setDataSource():" + url);
        MediaCollect.getInstance().setDataSource(url, header, mediaPlayer.g());
    }

    /**
     * com.youga.pldroid.MediaPlayerInstrument.prepareAsync(\$0);
     */
    public static void prepareAsync(MediaPlayer mediaPlayer) {
        Log.e(TAG, "prepareAsync()");
        MediaCollect.getInstance().prepareAsync(mediaPlayer);
    }

    /**
     * com.youga.pldroid.MediaPlayerInstrument.start(\$0);
     */
    public static void start(MediaPlayer mediaPlayer) {
        if (mediaPlayer.g() == PlayerState.PLAYING)
            Log.e(TAG, "start()");
    }

    /**
     * com.youga.pldroid.MediaPlayerInstrument.pause(\$0);
     */
    public static void pause(MediaPlayer mediaPlayer) {
        Log.e(TAG, "pause()");
    }

    /**
     * com.youga.pldroid.MediaPlayerInstrument.stop(\$0);
     */
    public static void stop(MediaPlayer mediaPlayer) {
        Log.e(TAG, "stop()");
    }

    /**
     * com.youga.pldroid.MediaPlayerInstrument.seekTo(\$1,\$0);
     */
    public static void seekTo(int pos, MediaPlayer mediaPlayer) {
        Log.e(TAG, "seekTo()");
    }

    /**
     * cdn.youga.instrument.postEventFromNative.prepareAsync(\$1, \$2, \$3,\$4, \$5);
     */
    public static void postEventFromNative(Object playerReference, int what, int ext1, int ext2, Object obj) {
        try {
            MediaPlayer mediaPlayer = (MediaPlayer) ((WeakReference) playerReference).get();
            if (mediaPlayer == null) return;
//            Log.d(TAG, "what:" + Integer.toHexString(what) + "-->ext1:" + ext1 + "-->ext2:" + ext2 + "obj:" + obj);
            String url = mediaPlayer.r();
            PlayerState playerState = mediaPlayer.g();
            switch (what) {
                case QC_MSG_PLAY_UNKNOW:
                    Log.e(TAG, "QC_MSG_PLAY_UNKNOW");
                    break;
                case QC_MSG_SNKV_NEW_FORMAT://onVideoSizeChanged
                    Log.e(TAG, "QC_MSG_SNKV_NEW_FORMAT");
                    break;
                case QC_MSG_PLAY_OPEN_DONE://onPrepared
//                    var1 = (int) (System.currentTimeMillis() - mStartPrepare);
//                    Log.e(TAG, "准备完成时间:" + var1);
                    Log.e(TAG, "QC_MSG_PLAY_OPEN_DONE");
                    break;
                case QC_MSG_PLAY_STOP://停止
                    MediaCollect.getInstance().playStop(url, playerState);
                    break;
                case QC_MSG_HTTP_CONNECT_START:
                    Log.e(TAG, "QC_MSG_HTTP_CONNECT_START");
                    break;
                case QC_MSG_HTTP_CONNECT_FAILED:
                    Log.e(TAG, "QC_MSG_HTTP_CONNECT_FAILED");
                    break;
                case QC_MSG_HTTP_CONNECT_SUCESS:
                    Log.e(TAG, "QC_MSG_HTTP_CONNECT_SUCESS");
                    break;
                case QC_MSG_HTTP_DNS_START:
                    Log.e(TAG, "QC_MSG_HTTP_DNS_START");
                    break;
                case 285212752:
                case 285212753:
                case 285278210:
                case 285278215:
                case 285278216://onError ERROR_CODE_IO_ERROR
//                    var5.c(-3);
                    break;
                case 285212754:
                case 285278211:
                case 285278217://onInfo MEDIA_INFO_CONNECTED 连接成功
//                    var5.a(var5, 200, ext1);
//                    Log.e(TAG, "建立连接时间");
                    break;
                case 285212768://onBufferingUpdate
//                    Log.d(TAG, "onBufferingUpdate:" + 100);
                    break;
                case 285212769://onBufferingUpdate
//                    Log.d(TAG, "onBufferingUpdate:" + ext1);
                    break;
                case 285278214://onInfo MEDIA_INFO_METADATA
//                    JSONObject jsonObject = new JSONObject((String) obj);
//                    Log.d(TAG, "jsonObject:" + jsonObject.toString());
                    break;
                case 285343746://onInfo MEDIA_INFO_BUFFERING_START
//                    if (ext1 == 2) {
//                        var5.a(var5, 701, 0);
//                    }
                    break;
                case 335544321://onError ERROR_CODE_HW_DECODE_FAILURE
//                    var5.i = PlayerState.ERROR;
//                    var5.c(-2003);
                    break;
                case 353370113://onInfo 	停止缓冲
//                    if (!var5.D) {
//                        var5.D = true; MEDIA_INFO_AUDIO_RENDERING_START 第一帧音频已成功播放
//                        var5.a(var5, 10002, (int)(System.currentTimeMillis() - var5.h));
//                    }
//
//                    var5.a(var5, 702, 0);
                    break;
                case 353370115://onError ERROR_CODE_PLAYER_CREATE_AUDIO_FAILED
//                    if (ext1 > 0 && ext2 > 0) {
//                        var5.w = ext1;
//                        var5.x = ext2;
//                        if (var5.d.a(ext1, ext2) != 0 || var5.d.b().getState() == 0) {
//                            var5.c(-4410);
//                        }
//                    }
                    break;
                case 353370116://onInfo MEDIA_INFO_AUDIO_FRAME_RENDERING //音频帧的时间戳
//                    var5.a(var5, 10005, ext1);
//                    if (var5.i == PlayerState.RECONNECTING) {
//                        var5.i = PlayerState.PLAYING_CACHE;
//                    }
                    break;
                case 354418689://onInfo MEDIA_INFO_BUFFERING_END 停止缓冲
//                    if (!mFirstPlay) { //MEDIA_INFO_VIDEO_RENDERING_START  	第一帧视频已成功渲染
//                        var1 = (int) (System.currentTimeMillis() - mStartPrepare);
//                        Log.e(TAG, "首播时间:" + var1);
//                    }
//
//                    var5.a(var5, 702, 0);
                    Log.e(TAG, "停止缓冲");
                    break;
                case 354418692://onInfo MEDIA_INFO_VIDEO_FRAME_RENDERING 视频帧的时间戳
//                    var5.a(var5, 10004, ext1);
//                    if (var5.i == PlayerState.RECONNECTING) {
//                        var5.i = PlayerState.PLAYING_CACHE;
//                    }
                    break;
                case 354418693://onInfo MEDIA_INFO_VIDEO_ROTATION_CHANGED

                    break;
                case 369098754://onError ERROR_CODE_OPEN_FAILED
//                    if (var5.p <= 0 || ext1 != -2147483632) {
//                        var5.F = false;
//                        var5.i = PlayerState.ERROR;
//                    }
//
//                    if (ext1 == -2147483632) {
//                        var5.F = true;
//                    }
//
//                    var5.c(-2);
                    break;
                case 369098757://onSeekComplete
                    break;
                case 369098758://onError ERROR_CODE_SEEK_FAILED
//                    var5.i = PlayerState.ERROR;
//                    var5.c(-4);
                    break;
                case 369098759:
                    switch (ext1) {
                        case 0://onCompletion
//                            var5.i = PlayerState.COMPLETED;
                            return;
                        case 1://onInfo MEDIA_INFO_CACHED_COMPLETE
//                            var5.a(var5, 1345, 0);
                            return;
                        default:
                            return;
                    }
                case 402653187://onInfo MEDIA_INFO_VIDEO_GOP_TIME 获取视频的I帧间隔
//                    var5.a(var5, 10003, ext1);
                    break;
                case 402653188://onInfo MEDIA_INFO_VIDEO_FPS 每秒传输帧数
//                    var5.a(var5, 20002, ext1);
//                    Log.e(TAG, "视频每秒传输帧数:" + ext1);
                    break;
                case 402653189://onInfo MEDIA_INFO_AUDIO_FPS
//                    var5.a(var5, 20004, ext1);
                    break;
                case 402653190://onInfo MEDIA_INFO_VIDEO_BITRATE 比特率 每秒传送的比特(bit)数
//                    var5.a(var5, 20001, ext1);
//                    Log.e(TAG, "视频比特率:" + ext1);
                    break;
                case 402653191://onInfo MEDIA_INFO_AUDIO_BITRATE 比特率
//                    var5.a(var5, 20003, ext1);
                    break;
                case 402653206://onInfo  MEDIA_INFO_BUFFERING_START 开始缓冲
//                    var5.a(var5, 701, ext1);
                    Log.e(TAG, "开始缓冲");
                    break;
                case 402653207: //MEDIA_INFO_BUFFERING_END
//                    var5.i = PlayerState.PLAYING;
//                    int var6 = (int) (System.currentTimeMillis() - var5.j);
//                    var5.a(var5, 702, var6);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
