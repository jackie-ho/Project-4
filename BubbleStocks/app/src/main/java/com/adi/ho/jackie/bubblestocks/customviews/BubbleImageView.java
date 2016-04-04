package com.adi.ho.jackie.bubblestocks.customviews;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by JHADI on 4/2/16.
 */
public class BubbleImageView extends ImageView{

    //GradientDrawable drawable;

    //Couldn't implement runnable and onclick listener
    public BubbleImageView(Context context) {
        super(context);

        int xPadding = (int) (Math.random() * 400) ;
        int yPadding = (int) (Math.random() * 1000) + 1000;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
      //  params.setMargins(xPadding,yPadding,0,0);
        setLayoutParams(params);
        setMaxHeight(100);
        setMaxWidth(100);
        setAlpha(0f);

        setPadding(0, 0,0,0);
//        if (xPadding % 2 == 0) {
//            setPadding(xPadding, yPadding, 0, 0);
//        } else {
//            setPadding(0,yPadding,xPadding,0);
//        }

        //setting padding causes clipping
        //setting imagedrawables causes it to lag.
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
    }

}
