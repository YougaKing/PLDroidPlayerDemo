package cdn.youga.instrument;

/*******************************************************************************
 File:		BasePlayer.java

 Contains:	player interface file.

 Written by:	Fenger King

 Change History (most recent first):
 2013-12-28		Fenger			Create file

 *******************************************************************************/

public class EventCodes {
    public static final int QC_IOPROTOCOL_HTTPPD = 6;

    public static final	int QCPLAY_PID_Flush_Buffer	= 0X11000025;
    public static final int PARAM_PID_QPLAYER_VERSION = 0X110;

    // define the flag for player
    public static final int QCPLAY_OPEN_VIDDEC_HW = 0X01000000;
    public static final int QCPLAY_OPEN_SAME_SOURCE = 0X02000000;

    // define the param id
    public static final int PARAM_PID_EVENT_DONE = 0X100;
    // get: the param is null, set the param is 0 -100
    public static final int PARAM_PID_AUDIO_VOLUME = 0X101;

    // the param should be: 0X00010002 is 0.5 speed. 0X00040001 is 4 speed.
    public static final int QCPLAY_PID_Speed = 0X11000002;
    // the param shoud be 1 disable video rnd, 0 enable render.
    public static final int QCPLAY_PID_Disable_Video = 0X11000003;

    // the param should be int
    public static final int QCPLAY_PID_StreamNum = 0X11000005;
    public static final int QCPLAY_PID_StreamPlay = 0X11000006;
    public static final int QCPLAY_PID_StreamInfo = 0X1100000F;
    public static final int QCPLAY_PID_Clock_OffTime = 0X11000020;

    public static final int	QC_MSG_HTTP_DOWNLOAD_FINISH	= 0x11000060;

    public static final int	QCPLAY_PID_RTMP_AUDIO_MSG_TIMESTAMP	= 0x11000073;
    public static final int	QCPLAY_PID_RTMP_VIDEO_MSG_TIMESTAMP	= 0x11000074;

    // the param.
    public static final int QCPLAY_PID_Reconnect = 0X11000030;

    // Set the perfer io protocol. Param should QC_IOPROTOCOL_HTTPPD
    // This should be called before open.
    public static final int QCPLAY_PID_Prefer_Protocol = 0X11000060;

    // Set the PD save path. Param should char *
    // This should be called before open.
    public static final int QCPLAY_PID_PD_Save_Path = 0X11000061;

    // Set the PD save ext name. Param should char *
    // This should be called before open.
    public static final int QCPLAY_PID_PD_Save_ExtName = 0X11000062;

    // Set the perfer file format. Param should QCPLAY_FORMAT_*
    // This should be called before open.
    public static final int QCPLAY_PID_Prefer_Format = 0X11000050;
    public static final int QCPLAY_FORMAT_M3U8 = 1;
    public static final int QCPLAY_FORMAT_MP4 = 2;
    public static final int QCPLAY_FORMAT_FLV = 3;
    public static final int QCPLAY_FORMAT_MP3 = 5;

    // Set the http header referer
    // The parameter should be String.
    public static final int	QCPLAY_PID_DNS_SERVER = 0X11000208;
    // Set the ext dns server
    // The parameter should be String. "127.0.0.1" use local server.
    public static final int	QCPLAY_PID_DNS_DETECT = 0X11000209;

    // Set to capture video image
    // The parameter should be long long * (ms). capture time. 0 is immediatily.
    public static final int QCPLAY_PID_Capture_Image = 0X11000310;

    // Set / get the socket connect timeout time
    // The parameter should be int (ms)
    public static final int QCPLAY_PID_Socket_ConnectTimeout = 0X11000200;

    // Set / get the socket read timeout time
    // The parameter should be int (ms)
    public static final int QCPLAY_PID_Socket_ReadTimeout = 0X11000201;

    // Set the http header referer
    // The parameter should be String.
    public static final int QCPLAY_PID_HTTP_HeadReferer = 0X11000205;

    // Set the max buffer time
    // The parameter should be int (ms)
    public static final int QCPLAY_PID_PlayBuff_MaxTime = 0X11000211;

    // Set  the min buffer time
    // The parameter should be int (ms)
    public static final int QCPLAY_PID_PlayBuff_MinTime = 0X11000212;

    // Set the log out level
    // The parameter should be int, 0, None, 1 error, 2 warning, 3 info, 4 debug.
    public static final int QCPLAY_PID_Log_Level = 0X11000320;
    // Set the DRM key
    // The parameter should be byte[].
    public static final int QCPLAY_PID_DRM_KeyText = 0X11000301;

    // Set to call back video buffer. It should be set after open before run.
    // The parameter is 1, it will render outside, 0 render internal
    public static final int QCPLAY_PID_SendOut_VideoBuff = 0X11000330;

    // Set to call back Audio buffer. It should be set after open before run.
    // The parameter is 1, it will render outside, 0 render internal
    public static final int QCPLAY_PID_SendOut_AudioBuff = 0X11000331;

    // Seek mode, 0 for seek to nearest key frame, 1 for frame in the second.
    public static final int QCPLAY_PID_Seek_Mode = 0x11000021;

    // Define id of event listener.
    public static final int QC_MSG_PLAY_OPEN_DONE = 0x16000001;
    public static final int QC_MSG_PLAY_OPEN_FAILED = 0x16000002;
    public static final int QC_MSG_PLAY_COMPLETE = 0x16000007;
    public static final int QC_MSG_PLAY_SEEK_DONE = 0x16000005;
    public static final int QC_MSG_PLAY_SEEK_FAILED = 0x16000006;
    // The first frame video was displayed.
    public static final int QC_MSG_SNKV_FIRST_FRAME = 0x15200001;
    // The video was render, the param is timestamp.
    public static final int QC_MSG_SNKV_RENDER = 0x15200004;
    // The first frame audio was displayed.
    public static final int QC_MSG_SNKA_FIRST_FRAME = 0x15100001;
    // The audio was render, the param is timestamp.
    public static final int QC_MSG_SNKA_RENDER = 0x15100004;

    // The nArg1 is the value
    public static final int QC_MSG_PLAY_STATUS = 0x16000008;
    public static final int QC_MSG_PLAY_DURATION = 0x16000009;

    public static final int QC_MSG_PLAY_RUN = 0x1600000C;
    public static final int QC_MSG_PLAY_PAUSE = 0x1600000D;
    public static final int QC_MSG_PLAY_STOP = 0x1600000E;
    public static final int QC_MSG_PLAY_UNKNOW = 0x1600000A;


    // http protocol messaage id
    public static final int QC_MSG_HTTP_CONNECT_START = 0x11000001;
    public static final int QC_MSG_HTTP_CONNECT_FAILED = 0x11000002;
    public static final int QC_MSG_HTTP_CONNECT_SUCESS = 0x11000003;
    public static final int QC_MSG_HTTP_DNS_START = 0x11000004;
    public static final int QC_MSG_HTTP_REDIRECT = 0x11000012;
    public static final int QC_MSG_HTTP_DISCONNECT_START = 0x11000021;
    public static final int QC_MSG_HTTP_DISCONNECT_DONE = 0x11000022;
    public static final int QC_MSG_HTTP_DOWNLOAD_SPEED = 0x11000030;
    public static final int QC_MSG_HTTP_DISCONNECTED = 0x11000050;
    public static final int QC_MSG_HTTP_RECONNECT_FAILED = 0x11000051;
    public static final int QC_MSG_HTTP_RECONNECT_SUCESS = 0x11000052;

    public static final int QC_MSG_RTMP_CONNECT_START = 0x11010001;
    public static final int QC_MSG_RTMP_CONNECT_FAILED = 0x11010002;
    public static final int QC_MSG_RTMP_CONNECT_SUCESS = 0x11010003;
    public static final int QC_MSG_RTMP_DISCONNECTED = 0x11010007;
    public static final int QC_MSG_RTMP_RECONNECT_FAILED = 0x11010008;
    public static final int QC_MSG_RTMP_RECONNECT_SUCESS = 0x11010009;

    public static final int QC_MSG_HTTP_RETURN_CODE = 0x11000023;

    // The obj is the string value
    public static final int QC_MSG_RTMP_METADATA = 0x11010006;

    // The nArg1 is the value.
    public static final int QC_MSG_RTMP_DOWNLOAD_SPEED = 0x11010004;
    public static final int QC_MSG_BUFF_VBUFFTIME = 0x18000001;
    public static final int QC_MSG_BUFF_ABUFFTIME = 0x18000002;

    public static final int QC_MSG_BUFF_GOPTIME = 0x18000003;
    public static final int QC_MSG_BUFF_VFPS = 0x18000004;
    public static final int QC_MSG_BUFF_AFPS = 0x18000005;
    public static final int QC_MSG_BUFF_VBITRATE = 0x18000006;
    public static final int QC_MSG_BUFF_ABITRATE = 0x18000007;

    // there is no value.
    public static final int QC_MSG_BUFF_START_BUFFERING = 0x18000016;
    public static final int QC_MSG_BUFF_END_BUFFERING = 0x18000017;

    public static final int QC_MSG_PARSER_M3U8_ERROR = 0x12000010;
    public static final int QC_MSG_PARSER_FLV_ERROR = 0x12000020;
    public static final int QC_MSG_PARSER_MP4_ERROR = 0x12000030;

    public static final int QC_MSG_VIDEO_HWDEC_FAILED = 0x14000001;

    // The video size was changed. The player need to resize the
    // surface pos and size.
    public static final int QC_MSG_SNKV_NEW_FORMAT = 0x15200003;
    // The audio format was changed.
    public static final int QC_MSG_SNKA_NEW_FORMAT = 0x15100003;
    // The video was rotate, the param is rotate angle.
    public static final int QC_MSG_SNKV_ROTATE = 0x15200005;

    public static final int QC_FLAG_Video_YUV420P = 0x00000000;
    public static final int QC_FLAG_Video_CaptureImage = 0x00000010;
    public static final int QC_FLAG_Video_SEIDATA = 0x00000020;

    // the param should int array. the value should be divide by 4
    // the value is left, top, right, bottom
    public static final	int QCPLAY_PID_ZoomVideo = 0X11000011;

    // Set to playback or not.
    // The parameter should be int. 0 not loop, 1 loop.
    public static final int	QCPLAY_PID_Playback_Loop = 0X11000340;

    public static final int QC_MSG_HTTP_DOWNLOAD_PERCENT = 0x11000061;

    // Param: int, 0 None, 1 File, 2 Http
    public static final int	QC_MSG_IO_SEEK_SOURCE_TYPE = 0x11020002;

    public static final int QC_ERR_FAILED = 0x80000001;
    public static final int QC_ERR_MEMORY = 0x80000002;
    public static final int QC_ERR_IMPLEMENT = 0x80000003;
    public static final int QC_ERR_ARG = 0x80000004;
    public static final int QC_ERR_TIMEOUT = 0x80000005;
    public static final int QC_ERR_STATUS = 0x80000008;
    public static final int QC_ERR_PARAMID = 0x80000009;
    public static final int QC_ERR_UNSUPPORT = 0x8000000b;
    public static final int QC_ERR_FORCECLOSE = 0x8000000c;
    public static final int QC_ERR_FORMAT = 0x8000000d;
    public static final int QC_ERR_IO_FAILED = 0x80000010;

    // Network info for qos report
    public static final int QC_FLAG_NETWORK_CHANGED = 0x20000000;
    public static final int QC_FLAG_NETWORK_TYPE = 0x20000001;
    public static final int QC_FLAG_ISP_NAME = 0x20000002;
    public static final int QC_FLAG_WIFI_NAME = 0x20000003;
    public static final int QC_FLAG_SIGNAL_DB = 0x20000004;
    public static final int QC_FLAG_SIGNAL_LEVEL = 0x20000005;

    public static final int QC_FLAG_APP_VERSION = 0x21000001;
    public static final int QC_FLAG_APP_NAME = 0x21000002;
    public static final int QC_FLAG_SDK_ID = 0x21000003;
    public static final int QC_FLAG_SDK_VERSION = 0x21000004;
}


