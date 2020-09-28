package net.jmake.youneedit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ContactActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText et_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        // 버튼이벤트
        findViewById(R.id.menu_search).setOnClickListener(this);
        findViewById(R.id.menu_list).setOnClickListener(this);
        findViewById(R.id.menu_quiz).setOnClickListener(this);
        // 현재 메뉴 색상 바꾸기
        findViewById(R.id.menu_contact).setBackgroundColor(Color.parseColor("#006385"));
        findViewById(R.id.menu_rank).setOnClickListener(this);

        et_text = findViewById(R.id.et_text);
        findViewById(R.id.bt_send).setOnClickListener(this);
    }

    // 버튼 이벤트
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_send:  // 메일 보내기
                Intent email = new Intent(Intent.ACTION_SEND);
                email.setType("plain/text");
                email.putExtra(Intent.EXTRA_EMAIL, new String[] {"visualjoon0916@gmail.com"});
                email.putExtra(Intent.EXTRA_SUBJECT,"ISL Converter");
                email.putExtra(Intent.EXTRA_TEXT,et_text.getText());
                startActivity(email);
                break;
            case R.id.menu_search:  // 검색 버튼
                startActivity(new Intent(this, SearchActivity.class));
                finish();
                break;
            case R.id.menu_list:    // 메뉴 리스트 버튼
                startActivity(new Intent(this, ListActivity.class));    // 리스트로 이동
                finish();
                break;
            case R.id.menu_quiz:    // 퀴즈 버튼
                startActivity(new Intent(this, QuizActivity.class));
                finish();
                break;
            case R.id.menu_rank:    // 퀴즈 버튼
                startActivity(new Intent(this, RankActivity.class));
                finish();
                break;
        }
    }

    // 뒤로가기 이벤트
    @Override
    public void onBackPressed() {
        finishDialog();
    }

    // 나가기
    private void finishDialog() {
        SharedPreferences sp = getSharedPreferences("Member", 0);
        int mood = sp.getInt("mb_mood", 0);

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.finish_dialog);
        dialog.setTitle(R.string.app_name);
        dialog.findViewById(R.id.bt_exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        dialog.findViewById(R.id.bt_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ImageView iv = (ImageView) dialog.findViewById(R.id.iv_feel);
        TextView tv = (TextView) dialog.findViewById(R.id.tv_explain);
        if(mood < -10) {
            iv.setImageResource(R.drawable.score1);
            tv.setText("It seems that you are in a bad mood today");
        } else if(mood < 0) {
            iv.setImageResource(R.drawable.score2);
            tv.setText("Is everything fine? You seem to be a bit upset");
        } else if(mood <= 10) {
            iv.setImageResource(R.drawable.score3);
            tv.setText("You are in a normal mood today");
        } else {
            iv.setImageResource(R.drawable.score4);
            tv.setText("Anything good happened? You seem to be happy today!");
        }

        dialog.show();
    }
}
