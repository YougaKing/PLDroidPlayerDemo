package cdn.youga.instrument;

import com.qiniu.qplayer.mediaEngine.MediaPlayer;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Map;

public class MediaPlayerInstrument {


    /**
     * cdn.youga.instrument.MediaPlayerInstrument.setDataSource(\$1, \$2, \$0);
     */
    public static void setDataSource(String url, Map<String, String> header, MediaPlayer mediaPlayer) {


    }


    /**
     * cdn.youga.instrument.MediaPlayerInstrument.prepareAsync(\$0);
     */
    public static void prepareAsync(MediaPlayer mediaPlayer) {

    }

    /**
     * cdn.youga.instrument.MediaPlayerInstrument.play(\$0);
     */
    public static void play(MediaPlayer mediaPlayer) {

    }

    /**
     * cdn.youga.instrument.postEventFromNative.prepareAsync(\$1, \$2, \$3,\$4, \$5);
     */
    public static void postEventFromNative(Object playerReference, int what, int ext1, int ext2, Object obj) {
        try {
            MediaPlayer mediaPlayer = (MediaPlayer) ((WeakReference) playerReference).get();
            JSONObject jsonObject = new JSONObject((String) obj);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
