package net.jmake.youneedit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;

public class ListActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    String[] list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        findViewById(R.id.menu_search).setOnClickListener(this);
        findViewById(R.id.menu_list).setBackgroundColor(Color.parseColor("#006385"));
        findViewById(R.id.menu_quiz).setOnClickListener(this);
        findViewById(R.id.menu_contact).setOnClickListener(this);
        findViewById(R.id.menu_rank).setOnClickListener(this);

        // 폴더 체크
        AssetManager am = getAssets();
        try {
            list = am.list("sign"); // 폴더명 가져오기
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 데이터를 리스트에 연결처리
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        ListView listView = (ListView) findViewById(R.id.lt);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    // 버튼 이벤트
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_search:
                startActivity(new Intent(this, SearchActivity.class));
                finish();
                break;
            case R.id.menu_quiz:
                startActivity(new Intent(this, QuizActivity.class));
                finish();
                break;
            case R.id.menu_contact:
                startActivity(new Intent(this, ContactActivity.class));
                finish();
                break;
            case R.id.menu_rank:
                startActivity(new Intent(this, RankActivity.class));
                finish();
                break;
        }
    }

    // 리스트를 클릭 이벤트
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // 검색한 단어를 활용하기 위해 저장
        SharedPreferences sp = getSharedPreferences("Search", 0);
        SharedPreferences.Editor editor = sp.edit();
        int cnt = sp.getInt(list[(int)id], 0);
        editor.putInt(list[(int)id], cnt);   // 검색 단어 저장
        editor.commit();

        Intent intent = new Intent(this, SearchActivity.class);     // 검색액티비티로 넘김
        intent = intent.putExtra("keyword", list[(int)id]);     // 클릭시 값을 넘김(폴더명)
        startActivity(intent);
        finish();
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
