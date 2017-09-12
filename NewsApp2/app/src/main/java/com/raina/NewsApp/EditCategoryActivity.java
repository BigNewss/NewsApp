package com.raina.NewsApp;;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.ButtonBarLayout;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by pilgrims on 09/09/2017.
 */

public class EditCategoryActivity extends Activity implements View.OnClickListener{
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);

    }

    @Override
    protected void onStart() {
        super.onStart();
        addButton();
    }

    //保存持久性数据
    @Override
    protected void onPause() {
        super.onPause();
        //Intent intent = new Intent(this,MainActivity.class);
        //startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        Button but = (Button) view;
        //Toast.makeText(this, Boolean.toString(Category.get(but.getId())),Toast.LENGTH_SHORT).show();
        Category.getCategory().change(but.getId());
        changeColor(but);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
    private void addButton(){
        LinearLayout layout = (LinearLayout) findViewById(R.id.linearLayout_edit_category);
        layout.setOrientation(LinearLayout.VERTICAL);
        String[] categories = getResources().getStringArray(R.array.categories);
        
        for (int i = 0; i < 12; i++){
            Button button = new Button(this);
            button.setId(i);
            button.setText(categories[i]);
            button.setOnClickListener(this);
            if(Category.getCategory().get(i)){
                button.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            }
            else
                button.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            layout.addView(button,i);
        }
        setContentView(layout);
    }
    private void changeColor(Button but){
        if(((ColorDrawable)but.getBackground()).getColor() == getResources().getColor(R.color.colorAccent)){
            but.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        }else{
            but.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
    }
}
