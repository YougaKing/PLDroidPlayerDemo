package cdn.youga.instrument;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author: YougaKingWu@gmail.com
 * @created on: 2018/04/26 12:13
 * @description:
 */
public class PldroidCdn {

    public static final String SUPPORT_COLLECT_MEDIA_META = "support_collect_media_meta";
    public static final int ALL = 0, TCP = 1;

    @Retention(RetentionPolicy.SOURCE)
    @interface CollectType {
    }

    @CollectType
    private int mCollectType = ALL;
    private static PldroidCdn INSTACE;
    private PldroidPlayerListener mPlayerListener;

    static PldroidCdn getInstance() {
        if (INSTACE == null) {
            INSTACE = new PldroidCdn();
        }
        return INSTACE;
    }

    public static void init(@CollectType int type, PldroidPlayerListener listener) {
        PldroidCdn pldroidCdn = getInstance();
        pldroidCdn.mCollectType = type;
        pldroidCdn.setPlayerListener(listener);
    }

    void upload(MediaMeta mediaMeta) {
        if (mPlayerListener == null) return;
        mPlayerListener.upload(mediaMeta);
    }

    int getCollectType() {
        return mCollectType;
    }

    private void setPlayerListener(PldroidPlayerListener playerListener) {
        mPlayerListener = playerListener;
    }

    public interface PldroidPlayerListener {
        void upload(MediaMeta mediaMeta);
    }
}
