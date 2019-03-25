package cdn.youga.instrument;

/**
 * @author: YougaKingWu@gmail.com
 * @created on: 2018/04/26 12:13
 * @description:
 */
public class PldroidCdn {

    private static PldroidCdn INSTACE;
    private PldroidPlayerListener mPlayerListener;

    public static PldroidCdn getInstance() {
        if (INSTACE == null) {
            INSTACE = new PldroidCdn();
        }
        return INSTACE;
    }

    public static void init(PldroidPlayerListener listener) {
        getInstance().setPlayerListener(listener);
    }

    public void upload(MediaMeta mediaMeta) {
        if (mPlayerListener == null) return;
        mPlayerListener.upload(mediaMeta);
    }

    private void setPlayerListener(PldroidPlayerListener playerListener) {
        mPlayerListener = playerListener;
    }

    public interface PldroidPlayerListener {
        void upload(MediaMeta mediaMeta);
    }
}
