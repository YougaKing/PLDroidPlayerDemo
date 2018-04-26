package cdn.youga.instrument;

/**
 * author: YougaKingWu@gmail.com
 * created on: 2018/04/26 17:03
 * description:
 */
public class Meta {


    //QC_MSG_HTTP_DNS_START           00 : 00 : 00 : 003           0             0    ghc40.aipai.com
    public String type;
    public String time;
    public String dummy;
    public String length;
    public String extra;

    public Meta(String[] logs) {
        if (logs == null || logs.length < 4) return;
        this.type = logs[0];
        this.time = logs[1];
        this.dummy = logs[2];
        this.length = logs[3];
        if (logs.length == 5) {
            this.extra = logs[4];
        }
    }

    @Override
    public String toString() {
        return "Meta{" +
                "type='" + type + '\'' +
                ", time='" + time + '\'' +
                ", dummy='" + dummy + '\'' +
                ", length='" + length + '\'' +
                ", extra='" + extra + '\'' +
                '}';
    }
}
