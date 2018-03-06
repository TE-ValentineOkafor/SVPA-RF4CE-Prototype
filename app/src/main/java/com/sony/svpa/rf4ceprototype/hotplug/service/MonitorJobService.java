package com.sony.svpa.rf4ceprototype.hotplug.service;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.sony.svpa.rf4ceprototype.hotplug.BraviaSyncController;


public class MonitorJobService extends JobService {
    private static final String TAG = JobService.class.getSimpleName();
    private static final long MONITOR_INTERVAL = 5000;
    private static final int MONITOR_JOB_ID = 14118;
    private BraviaSyncController braviaSyncController;

    public MonitorJobService() {
    }

    @Override
    public void onCreate(){
        super.onCreate();
        braviaSyncController  = BraviaSyncController.getInstance(getApplicationContext());
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        onStopJob(null);
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        jobFinished(params, false);
        new Handler(Looper.getMainLooper()).post(this::scheduleNextRun);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG,"onStopJob");
        JobScheduler jobScheduler = (JobScheduler) getApplicationContext().getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(MONITOR_JOB_ID);
        braviaSyncController.onDestroy();
        return true;
    }

    private void scheduleNextRun(){
        scheduleHdmiScan(getApplicationContext());
    }

    private static void scheduleHdmiScan(Context context){
        Log.d(TAG, "Scheduling HDMI scan.");
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo jobInfo = new JobInfo.Builder(MONITOR_JOB_ID,
                new ComponentName(context, MonitorJobService.class))
                .setMinimumLatency(MONITOR_INTERVAL)
                .build();
        jobScheduler.schedule(jobInfo);
    }

}
