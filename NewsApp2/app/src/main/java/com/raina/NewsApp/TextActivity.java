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
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
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
import cn.sharesdk.onekeyshare.OnekeyShare;

//import com.raina.newsapp2.R;

public class TextActivity extends AppCompatActivity {

    private boolean saved = false;
    private Toolbar toolbar;
    private PopupMenu popipMenu;
    private PopupWindow popupWindow;

    private SpeechSynthesizer mySynthesizer;

    private void showShare() {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网、QQ和QQ空间使用
        oks.setTitle(getTitle);
        // titleUrl是标题的网络链接，仅在Linked-in,QQ和QQ空间使用
        oks.setTitleUrl(getURL);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(getIntro);
        //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
        oks.setImageUrl(getPic);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        //oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        //oks.setSite("ShareSDK");
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        //oks.setSiteUrl("http://sharesdk.cn");
        oks.show(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
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
                        showShare();
                        //popupWindow.showAtLocation(findViewById(R.id.menu_share), Gravity.BOTTOM, 0, 0);
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
    String getTitle;
    String getIntro;
    String getPic;
    String getURL;

    private void initContent() {
        Intent intent = getIntent();
        final String title = intent.getStringExtra("Title");
        getTitle = title;
        getIntro = intent.getStringExtra("Intro");
        getURL = intent.getStringExtra("URL");
        final String keyWords = intent.getStringExtra("keywords");
        body = intent.getStringExtra("Body");
        pureText = body.replaceAll("<(\\s|\\S)*?>", "");
        ((TextView) findViewById(R.id.textview_body)).setText(Html.fromHtml(body));
        ((TextView) findViewById(R.id.textview_body)).setMovementMethod(LinkMovementMethod.getInstance());
        ((TextView) findViewById(R.id.textview_title)).setText(title);
        ((TextView) findViewById(R.id.textview_subtitle)).setText(MainActivity.currentNews.getNewsAuthor());

        final AlphaAnimation aa = new AlphaAnimation(0.0f,1.0f);
        aa.setDuration(1000);
        aa.getFillAfter();
        findViewById(R.id.textview_body).startAnimation(aa);
        aa.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationEnd(Animation arg0) {

            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
            @Override
            public void onAnimationStart(Animation animation) {}

        });

        final ImageView imageView = (ImageView) findViewById(R.id.imageview_body);
        if(MainActivity.picMode) {
            String[] imageList = MainActivity.currentNews.getNewsPictures();

            if(imageList.length > 0) {
                try {
                    //imageView.setImageBitmap(returnBitMap(imageList[0]));
                    final String get_path = imageList[0];
                    getPic = get_path;
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            //从网络上获取图片
                            final Bitmap bitmap=returnBitMap(get_path);

                            try {
                                Thread.sleep(500);//线程休眠两秒钟
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                //e.printStackTrace();
                            }
                            //alpha.setRepeatCount(0);//循环显示
                            //发送一个Runnable对象
                            imageView.post(new Runnable(){


                                @Override
                                public void run() {
                                    imageView.setImageBitmap(bitmap);//在ImageView中显示从网络上获取到的图片
                                    ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                                    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                                    imageView.setLayoutParams(layoutParams);
                                    imageView.setAnimation(aa);
                                }

                            });

                        }
                    }).start();
                } catch(Exception e) {
                }
            } else {
                try {
                    //imageView.setImageBitmap(returnBitMap(GetPictures.getURL(title)));
                    //final String gsset_path = GetPictures.getURL(title);
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            //从网络上获取图片
                            String get_path = "";
                            try {
                                get_path = GetPictures.getURL(keyWords);
                                getPic = get_path;
                            } catch (Exception e) {}
                            final Bitmap bitmap=returnBitMap(get_path);

                            try {
                                Thread.sleep(500);//线程休眠两秒钟
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            //alpha.setRepeatCount(0);//循环显示
                            //发送一个Runnable对象
                            imageView.post(new Runnable(){


                                @Override
                                public void run() {
                                    if (bitmap != null) {
                                        imageView.setImageBitmap(bitmap);//在ImageView中显示从网络上获取到的图片
                                        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                                        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                                        imageView.setLayoutParams(layoutParams);
                                        imageView.setAnimation(aa);
                                    }

                                }

                            });

                        }
                    }).start();
                } catch(Exception e) {

                }
            }

        } else {
            imageView.setImageDrawable(null);
        }
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

    public Bitmap returnBitMap(String path){
        Bitmap bm=null;
        URL url;
        try {
            url = new URL(path);//创建URL对象
            URLConnection conn=url.openConnection();//获取URL对象对应的连接
            conn.connect();//打开连接
            InputStream is=conn.getInputStream();//获取输入流对象
            bm=BitmapFactory.decodeStream(is);//根据输入流对象创建Bitmap对象
            is.close();
        } catch (MalformedURLException e1) {
            //e1.printStackTrace();//输出异常信息
            bm = null;
        }catch (IOException e) {
            bm = null;
            //e.printStackTrace();//输出异常信息
        }


        return bm;
    }

}
