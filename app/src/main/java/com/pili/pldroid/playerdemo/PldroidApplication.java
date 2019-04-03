package com.pili.pldroid.playerdemo;

import android.app.Application;

import com.orhanobut.logger.Logger;

import cdn.youga.instrument.MediaMeta;
import cdn.youga.instrument.PldroidCdn;
import cdn.youga.instrument.PldroidCdn.PldroidPlayerListener;

/**
 * author: YougaKingWu@gmail.com
 * created on: 2018/04/27 12:13
 * description:
 */
public class PldroidApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
//        NewRelic.enableFeature(FeatureFlag.DistributedTracing);
//
//        NewRelic.withApplicationToken("GENERATED_TOKEN")
//                .usingSsl(true)
//                .withLoggingEnabled(true)
//                .withHttpResponseBodyCaptureEnabled(true)
//                .withInteractionTracing(true)
//                .start(this);

        PldroidCdn.init(PldroidCdn.ALL, new PldroidPlayerListener() {
            @Override
            public void upload(MediaMeta mediaMeta) {
                Logger.d(mediaMeta);
            }
        });
    }
}
