package com.sndf.connection.remote;

import android.app.Instrumentation;
import android.os.SystemClock;
import android.view.MotionEvent;

import com.sndf.connection.base.DefaultExecutor;

public class SystemEventUtil
{
    private static Instrumentation mInstrumentation = new Instrumentation();

    /**
     * Handle the key click event
     * @param keycode 
     * @throws Exception
     */
    public static void handleKeyEvent(final int keycode) throws Exception
    {
        Runnable runnable = new Runnable()
        {
            @Override
            public void run()
            {
                Instrumentation mInstrumentation = new Instrumentation();
                mInstrumentation.sendKeyDownUpSync(keycode);
            }
        };

        DefaultExecutor.executeInNonMainThread(runnable);
    }
    
    /**
     * Handle the click event
     * @param x X-coordinate of the click position.
     * @param y Y-coordinate of the click position.
     * @throws Exception
     */
    public static void handleClickEvent(final float x, final float y) throws Exception
    {
        Runnable runnable = new Runnable()
        {
            @Override
            public void run()
            {
                long startTime = SystemClock.uptimeMillis();
                mInstrumentation.sendPointerSync(MotionEvent.obtain(startTime, SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, x, y, 0));
                mInstrumentation.sendPointerSync(MotionEvent.obtain(startTime, SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, x, y, 0));
            }
        };

        DefaultExecutor.executeInNonMainThread(runnable);
    }
}
