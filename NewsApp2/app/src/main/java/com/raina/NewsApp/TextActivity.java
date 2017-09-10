package com.raina.NewsApp;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
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

//import com.raina.newsapp2.R;

public class TextActivity extends AppCompatActivity {

    private boolean saved = false;
    private Toolbar toolbar;
    private PopupMenu popipMenu;
    private PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);


        //findViewById(R.id.textview_toolbar).setVisibility(0);


        initToolbar();
        initContent();
        initPopupWindow();
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*
        if(MainActivity.currentNews.checkMark()) {
            ((MenuItem)findViewById(R.id.menu_favorite)).setIcon(R.drawable.icon_hearted);
        } else {
            ((MenuItem)findViewById(R.id.menu_favorite)).setIcon(R.drawable.icon_heart);
        }*/

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
        });

    }


    private void initContent() {
        Intent intent = getIntent();
        String title = intent.getStringExtra("Title");
        String intro = intent.getStringExtra("Body");
        TextView bodyTextView = (TextView) findViewById(R.id.textview_body);
        ImageView imageView = (ImageView) findViewById(R.id.imageview_body);
        bodyTextView.setText(intro);
        //imageView.setImageResource(R.drawable.icon_category);
        toolbar.setTitle(title);
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

}
