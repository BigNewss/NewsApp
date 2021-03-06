package com.java.group41;;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import java.io.FileOutputStream;

/**
 * Created by pilgrims on 09/09/2017.
 */

public class EditCategoryActivity extends Activity implements View.OnClickListener{

    int[] id = new int[] {R.id.type_0, R.id.type_1, R.id.type_2, R.id.type_3, R.id.type_4, R.id.type_5, R.id.type_6,
            R.id.type_7, R.id.type_8, R.id.type_9, R.id.type_10, R.id.type_11};
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.black));
        for(int i = 0; i < 12; i++) {
            Button button = ((Button) findViewById(id[i]));
            button.setOnClickListener(this);
            if(Category.getCategory().get(i)){
                button.setBackgroundDrawable(getDrawable(R.drawable.btn_0_shape));
                button.setTextColor(getResources().getColor(R.color.colorGrey));
            }
            else {
                button.setBackgroundDrawable(getDrawable(R.drawable.btn_1_shape));
                button.setTextColor(getResources().getColor(R.color.colorWhite));
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    //保存持久性数据
    @Override
    protected void onPause() {
        super.onPause();
        try {
            FileOutputStream fw = openFileOutput("cateList.txt", Context.MODE_PRIVATE);
            String s = "";
            for (int i = 0; i < Category.exist.length - 1; i++){
                s += Category.exist[i] +" ";
            }
            s+=Category.exist[Category.exist.length - 1];
            fw.write(s.getBytes());
            fw.close();
        } catch (Exception e) {}
        //Intent intent = new Intent(this,MainActivity.class);
        //startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        Button but = (Button) view;

        for (int i = 0; i < 12; i++) {
            if(view.getId() == id[i]) {
                Category.getCategory().change(i);
                if(Category.getCategory().get(i)){
                    but.setBackgroundDrawable(getDrawable(R.drawable.btn_0_shape));
                    but.setTextColor(getResources().getColor(R.color.colorGrey));
                } else {
                    but.setBackgroundDrawable(getDrawable(R.drawable.btn_1_shape));
                    but.setTextColor(getResources().getColor(R.color.colorWhite));
                }
                break;
            }
        }

    }
    public void doClick(View v){
        this.finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
    /*
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
    */
    private void changeColor(Button but){
        if(((ColorDrawable)but.getBackground()).getColor() == getResources().getColor(R.color.colorWhite)){
            but.setBackgroundColor(getResources().getColor(R.color.colorButton));
            but.setTextColor(getResources().getColor(R.color.colorWhite));

        }else{
            but.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            but.setTextColor(getResources().getColor(R.color.colorGrey));
        }
    }
}
