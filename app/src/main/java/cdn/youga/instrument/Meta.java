package cdn.youga.instrument;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * author: YougaKingWu@gmail.com
 * created on: 2018/04/26 17:03
 * description:
 */
public class Meta {

    private long HTTP_CONNECT_START;
    private long HTTP_DNS_START;
    private long HTTP_DNS_GET_CACHE;
    private long HTTP_CONNECT_SUCESS;
    private long IO_FIRST_BYTE_DONE;
    private long PARSER_NEW_STREAM;
    private long SNKV_FIRST_FRAME;
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("HH : mm : ss : SSSS", Locale.CHINA);
    public String ip;
    public long length;
    public long dnsTime;
    public long httpTime;
    public long firstByte;
    public long parserFirstStream;
    public long firstFrame;

    //QC_MSG_HTTP_DNS_START           00 : 00 : 00 : 003           0             0    ghc40.aipai.com
    public void parse(String[] logs) throws ParseException {
        String type = logs[0];
        String time = logs[1];
        if (type.endsWith("HTTP_CONNECT_START")) {
            HTTP_CONNECT_START = mDateFormat.parse(time).getTime();
        } else if (type.endsWith("HTTP_DNS_START")) {
            HTTP_DNS_START = mDateFormat.parse(time).getTime();
        } else if (type.endsWith("HTTP_DNS_GET_CACHE")) {
            HTTP_DNS_GET_CACHE = mDateFormat.parse(time).getTime();
            ip = logs[4];
        } else if (type.endsWith("HTTP_CONNECT_SUCESS")) {
            HTTP_CONNECT_SUCESS = mDateFormat.parse(time).getTime();
        } else if (type.endsWith("IO_FIRST_BYTE_DONE")) {
            IO_FIRST_BYTE_DONE = mDateFormat.parse(time).getTime();
        } else if (type.endsWith("HTTP_CONTENT_LEN")) {
            length = Long.parseLong(logs[3]);
        } else if (type.endsWith("PARSER_NEW_STREAM")) {
            PARSER_NEW_STREAM = mDateFormat.parse(time).getTime();
        } else if (type.endsWith("SNKV_FIRST_FRAME")) {
            SNKV_FIRST_FRAME = mDateFormat.parse(time).getTime();
        }
    }

    public void clearing() {
        dnsTime = HTTP_DNS_GET_CACHE - HTTP_DNS_START;
        httpTime = HTTP_CONNECT_SUCESS - HTTP_CONNECT_START;
        firstByte = IO_FIRST_BYTE_DONE - HTTP_CONNECT_SUCESS;
        parserFirstStream = PARSER_NEW_STREAM - IO_FIRST_BYTE_DONE;
        firstFrame = SNKV_FIRST_FRAME - HTTP_CONNECT_START;
    }
}
