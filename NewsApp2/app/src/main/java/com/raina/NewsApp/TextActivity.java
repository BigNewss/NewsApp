package com.raina.NewsApp;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.View;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.view.Menu;
import android.widget.Toast;
import android.view.MenuItem;
import android.widget.Button;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.net.*;
import java.io.*;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.text.method.LinkMovementMethod;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;

//import com.raina.newsapp2.R;

public class TextActivity extends AppCompatActivity {

    private boolean saved = false;
    private Toolbar toolbar;
    private PopupMenu popipMenu;
    private PopupWindow popupWindow;

    private SpeechSynthesizer mySynthesizer;
    private Button tts_Button;
    private TextView tts_TextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);

        //语音初始化，在使用应用使用时需要初始化一次就好，如果没有这句会出现10111初始化失败
        SpeechUtility.createUtility(TextActivity.this, "appid=59b0e6cf");
        //处理语音合成关键类
        mySynthesizer = SpeechSynthesizer.createSynthesizer(this, myInitListener);
        //findViewById(R.id.textview_toolbar).setVisibility(0);

       /*
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
                .penaltyLog().penaltyDeath().build());
        */
        initToolbar();
        initContent();
        initPopupWindow();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mySynthesizer.stopSpeaking();
    }

    private InitListener myInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d("mySynthesiezer:", "InitListener init() code = " + code);
        }
    };

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_text);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_favorite:
                        if(MainActivity.currentNews.checkMark()) {
                            item.setIcon(R.drawable.icon_heart);
                            try {
                                MainActivity.currentNews.unsave(getApplicationContext());
                                MainActivity.newsSystem.updateMarkNewsList(getApplicationContext());
                            }catch(Exception e) {}

                        } else {
                            item.setIcon(R.drawable.icon_hearted);
                            try {
                                MainActivity.currentNews.save(getApplicationContext());
                                MainActivity.newsSystem.updateMarkNewsList(getApplicationContext());
                            }catch(Exception e) {}
                        }
                        saved = !saved;
                        break;
                    case R.id.menu_share:
                        popupWindow.showAtLocation(findViewById(R.id.menu_share), Gravity.BOTTOM, 0, 0);
                        break;
                    case R.id.menu_speak:
                        mySynthesizer.setParameter(SpeechConstant.VOICE_NAME,"xiaoyu");
                        //设置音调
                        mySynthesizer.setParameter(SpeechConstant.PITCH,"50");
                        //设置音量
                        mySynthesizer.setParameter(SpeechConstant.VOLUME,"50");
                        String tts = pureText;
                        int code = mySynthesizer.startSpeaking(tts, mTtsListener);
                        Log.d("mySynthesiezer start code:", code+"");
                        break;
                    case R.id.menu_more:
                        popipMenu = new PopupMenu(TextActivity.this, findViewById(R.id.menu_share));
                        final Menu shareMenu = popipMenu.getMenu();
                        MenuInflater menuInflater = getMenuInflater();
                        menuInflater.inflate(R.menu.popup_share_menu, shareMenu);
                        popipMenu.show();
                        break;
                }
                return true;
            }
            private SynthesizerListener mTtsListener = new SynthesizerListener() {
                @Override
                public void onSpeakBegin() {
                }
                @Override
                public void onSpeakPaused() {
                }
                @Override
                public void onSpeakResumed() {
                }
                @Override
                public void onBufferProgress(int percent, int beginPos, int endPos,
                                             String info) {
                }
                @Override
                public void onSpeakProgress(int percent, int beginPos, int endPos) {
                }
                @Override
                public void onCompleted(SpeechError error) {
                    if(error!=null)
                    {
                        Log.d("mySynthesiezer complete code:", error.getErrorCode()+"");
                    }
                    else
                    {
                        Log.d("mySynthesiezer complete code:", "0");
                    }
                }
                @Override
                public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
                    // TODO Auto-generated method stub

                }
            };
        });

    }

    String body;
    String pureText;

    private void initContent() {
        Intent intent = getIntent();
        String title = intent.getStringExtra("Title");
        body = intent.getStringExtra("Body");
        pureText = body.replaceAll("<(\\s|\\S)*?>", "");
        ((TextView) findViewById(R.id.textview_body)).setText(Html.fromHtml(body));
        ((TextView) findViewById(R.id.textview_body)).setMovementMethod(LinkMovementMethod.getInstance());
        ((TextView) findViewById(R.id.textview_title)).setText(title);
        ((TextView) findViewById(R.id.textview_subtitle)).setText(MainActivity.currentNews.getNewsAuthor());
        String[] imageList = MainActivity.currentNews.getNewsPictures();
        if(imageList.length > 0) {
            ImageView imageView = (ImageView) findViewById(R.id.imageview_body);
            try {
                Toast.makeText(TextActivity.this, imageList[0], Toast.LENGTH_SHORT).show();
                imageView.setImageBitmap(returnBitMap(imageList[0]));
            } catch(Exception e) {
                Toast.makeText(TextActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }


        //toolbar.setTitle(title);
    }

    private void initPopupWindow() {
        View popupView = getLayoutInflater().inflate(R.layout.popup_window_layout, null);
        popupWindow = new PopupWindow(popupView, AppBarLayout.LayoutParams.MATCH_PARENT, AppBarLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setAnimationStyle(R.style.popup_window_anim);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_text_menu, menu);
        MenuItem item = menu.getItem(0);
        if(MainActivity.currentNews.checkMark()) {
            item.setIcon(R.drawable.icon_hearted);
        } else {
            item.setIcon(R.drawable.icon_heart);
        }
        return true;
    }

    public Bitmap returnBitMap(String url){
        URL myFileUrl = null;
        Bitmap bitmap = null;
        try {
            myFileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

}
