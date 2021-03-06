package com.java.group41;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Animation;
import android.content.Intent;


public class startActivity extends Activity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view = View.inflate(this, R.layout.activity_start, null);
        setContentView(view);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.black));
        //渐变展示启动屏
        AlphaAnimation aa = new AlphaAnimation(0.0f,1.0f);
        aa.setDuration(3000);
        aa.getFillAfter();
        findViewById(R.id.imageview_start).startAnimation(aa);
        aa.setAnimationListener(new AnimationListener()
        {
            @Override
            public void onAnimationEnd(Animation arg0) {
                redirectTo();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
            @Override
            public void onAnimationStart(Animation animation) {}

        });
    }

    private void redirectTo(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
    }
}
