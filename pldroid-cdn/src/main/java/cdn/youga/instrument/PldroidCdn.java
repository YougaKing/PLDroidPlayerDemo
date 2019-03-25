package cdn.youga.instrument;

/**
 * @author: YougaKingWu@gmail.com
 * @created on: 2018/04/26 12:13
 * @description:
 */
public class PldroidCdn {

    private static PldroidCdn INSTACE;

    public static PldroidCdn getInstance() {
        if (INSTACE == null) {
            INSTACE = new PldroidCdn();
        }
        return INSTACE;
    }

    public void addTask(MediaMeta mediaMeta) {

    }

}
