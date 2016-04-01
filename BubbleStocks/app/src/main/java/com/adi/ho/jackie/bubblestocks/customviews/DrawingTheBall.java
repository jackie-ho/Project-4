package com.adi.ho.jackie.bubblestocks.customviews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.view.View;

import com.adi.ho.jackie.bubblestocks.R;

import java.util.Random;

/**
 * Created by JHADI on 3/31/16.
 */
public class DrawingTheBall extends View implements Runnable {

    final Bitmap bball;
    Random randX, randY;
    double theta;
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            invalidate();
            System.out.println("redraw");
        }

        ;
    };

    public DrawingTheBall(Context context) {
        super(context);
        bball = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher);
        randX = new Random();
        randY = new Random();
        theta = 45;
        new Thread(this).start();
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Radius, angle, and coordinates for circle motion
        float a = 50;
        float b = 50;
        float r = 50;
        int x = 0;
        int y = 0;
        theta = theta + Math.toRadians(2);

        // move ball in circle
        if (x < canvas.getWidth()) {
            x = randX.nextInt(100) + (int) (a + r * Math.cos(theta)); // create
            // randX
            // integer
        } else {
            x = 0;
        }
        if (y < canvas.getHeight()) {
            y = randY.nextInt(100) + (int) (b + r * Math.sin(theta));// create
            // randX
            // integer
        } else {
            y = 0;
        }
        Paint p = new Paint();
        canvas.drawBitmap(bball, x, y, p);
    }

    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            handler.sendEmptyMessage(0);
        }
    }
}