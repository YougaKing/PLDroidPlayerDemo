再缓冲次数：流媒体播放器在播放过程中出现了缓冲所花费的次数。

缓冲前准备时间：从开始监测到第一次缓冲出现的时间，包含了ＤＮＳ解析时间、发出数据请求及接收第一个数据包的时间。

缓冲时间：流媒体播放器处于首次缓冲状态待续的总时间。

再缓冲时间：流媒体播放器在播放过程中出现了缓冲所花费的时间。

等待时间：等于连接时间＋首次缓冲时间+所有再缓冲时间；是一个重要的指标，系统用此值来表示流媒体文件监测的性能。

比特率：流媒体播放时的速度，单位为Kbps。

码流：视频文件在单位时间内使用的数据流量，单位为bps。

监测时长：监测一个流媒体文件所花费的时间，即从开始监测到监测结束的时间；此时间不超过任务属性所设置的时间，如：设置的监测时间为１分钟，当此流媒体文件在１分钟内能够播放完毕，则是实际的播放时间；如果１分钟内不能完成播放，则是１分钟（如果超过１分钟则停止监测）。

首次播放时长：流媒体播放器开始播放的时间，如果在一次监测中出现了再次缓冲，则此时间指的是开始播放到出现再次缓冲所花费的时间。

播放时长：流媒体播放器播放的总时间，如果在一次监测中没有出现再次缓冲，则此时间等于首次播放时间。

页面流媒体首播时间：从监测开始到捕捉到流媒体文件的播放URL所用的时间，包括访问页面的DNS时间，连接时间等。只用于配置了 流媒体特征码 的流媒体监测任务。

用户体验指数：反映用户实际播放体验的综合指标，等于等待时间(秒)+(缓冲次数-1)，等待时间越长，缓冲次数越多，用户体验指数表现越差。




单点指标：
1. 首包时间
2. 总下载时间
3. 下载字节数
//4. 下载速度
5. 首播时间：流媒体播放器开始播放的时间
6. 缓冲时间：流媒体播放器处于首次缓冲状态待续的总时间
7. 再缓存时间：流媒体播放器在播放过程中出现了缓冲所花费的时间
//8. 建立连接时间
9. SSL握手时间
10. DNS时间