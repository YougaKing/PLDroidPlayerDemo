package cdn.youga.instrument;

//1. 首包时间
//2. 首次下载时间
//3. 下载字节数
////4. 下载速度
//5. 首播时间：流媒体播放器开始播放的时间
//6. 缓冲时间：流媒体播放器处于首次缓冲状态待续的总时间
//7. 再缓存时间：流媒体播放器在播放过程中出现了缓冲所花费的时间
////8. 建立连接时间
//9. SSL握手时间
//10. DNS时间

public enum TimeSlice {

    CONNECTED("建立连接时间"), FIRST_PACK("首包时间"), FIRST_PREPARED("首次下载时间"), FIRST_PLAY("首播时间");

    private String describe;

    TimeSlice(String describe) {
        this.describe = describe;
    }

    public String getDescribe() {
        return describe;
    }
}
