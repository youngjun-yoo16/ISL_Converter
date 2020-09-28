package net.jmake.youneedit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class RankActivity extends AppCompatActivity implements View.OnClickListener {
    private int i, j;
    private TextView tvWord1, tvWord2, tvWord3, tvWord4, tvWord5;
    private TextView tvCount1, tvCount2, tvCount3, tvCount4, tvCount5;
    private TextView tvId1, tvId2, tvId3, tvId4, tvId5;
    private TextView tvPoint1, tvPoint2, tvPoint3, tvPoint4, tvPoint5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);

        // 버튼 이벤트
        findViewById(R.id.menu_search).setOnClickListener(this);
        findViewById(R.id.menu_list).setOnClickListener(this);
        findViewById(R.id.menu_quiz).setOnClickListener(this);
        findViewById(R.id.menu_contact).setOnClickListener(this);
        findViewById(R.id.menu_rank).setBackgroundColor(Color.parseColor("#006385"));

        tvWord1 = findViewById(R.id.tv_word1);    tvWord2 = findViewById(R.id.tv_word2);  tvWord3 = findViewById(R.id.tv_word3);  tvWord4 = findViewById(R.id.tv_word4);  tvWord5 = findViewById(R.id.tv_word5);
        tvCount1 = findViewById(R.id.tv_count1);    tvCount2 = findViewById(R.id.tv_count2);    tvCount3 = findViewById(R.id.tv_count3);    tvCount4 = findViewById(R.id.tv_count4);    tvCount5 = findViewById(R.id.tv_count5);
        tvId1 = findViewById(R.id.tv_id1);  tvId2 = findViewById(R.id.tv_id2);  tvId3 = findViewById(R.id.tv_id3);  tvId4 = findViewById(R.id.tv_id4);  tvId5 = findViewById(R.id.tv_id5);
        tvPoint1 = findViewById(R.id.tv_point1);    tvPoint2 = findViewById(R.id.tv_point2);    tvPoint3 = findViewById(R.id.tv_point3);    tvPoint4 = findViewById(R.id.tv_point4);    tvPoint5 = findViewById(R.id.tv_point5);

        // 검색 랭킹 표시
        SharedPreferences sp = getSharedPreferences("Search", 0);     // 검색한 단어 가져오기
        // 변수에 넣어주기
        Map<String, String> map = (Map<String, String>) sp.getAll();
        if(map.size() > 0) {
            String word_arr[] = new String[map.size()];
            String count_arr[] = new String[map.size()];

            // 검색데이터 입력
            i = 0;
            for(Map.Entry entry : map.entrySet()) {
                word_arr[i] = entry.getKey().toString();
                count_arr[i] = entry.getValue().toString();
                i++;
            }

            // 검색순위 정렬
            String temp_word, temp_count;
            for(i=0; i<map.size() - 1; i++) {
                for(j=0; j<map.size() - 1; j++) {
                    if (Integer.valueOf(count_arr[j]) < Integer.valueOf(count_arr[j + 1])) {
                        temp_word = word_arr[j];
                        temp_count = count_arr[j];
                        word_arr[j] = word_arr[j + 1];
                        count_arr[j] = count_arr[j + 1];
                        word_arr[j + 1] = temp_word;
                        count_arr[j + 1] = temp_count;
                    }
                }
            }

            // 화면에 값 입력
            tvWord1.setText(word_arr[0]);
            tvCount1.setText(count_arr[0]);
            if(map.size() >= 2) {
                tvWord2.setText(word_arr[1]);
                tvCount2.setText(count_arr[1]);
            }
            if(map.size() >= 3) {
                tvWord3.setText(word_arr[2]);
                tvCount3.setText(count_arr[2]);
            }
            if(map.size() >= 4) {
                tvWord4.setText(word_arr[3]);
                tvCount4.setText(count_arr[3]);
            }
            if(map.size() >= 5) {
                tvWord5.setText(word_arr[4]);
                tvCount5.setText(count_arr[4]);
            }
        }

        // 점수 랭킹 표시
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://youneedit.jmake.net/external/rank.php", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                String[] id_arr = new String[5];
                String[] point_arr = new String[5];

                // 퀴즈순위 값 입력
                for(i=0; i<response.length(); i++){
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = response.getJSONObject(i);
                        id_arr[i] = jsonObject.getString("mb_id");
                        point_arr[i] = jsonObject.getString("mb_point");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                // 화면에 값 입력
                tvId1.setText(id_arr[0]);   tvPoint1.setText(point_arr[0]);
                tvId2.setText(id_arr[1]);   tvPoint2.setText(point_arr[1]);
                tvId3.setText(id_arr[2]);   tvPoint3.setText(point_arr[2]);
                tvId4.setText(id_arr[3]);   tvPoint4.setText(point_arr[3]);
                tvId5.setText(id_arr[4]);   tvPoint5.setText(point_arr[4]);
            }
        });
    }

    // 버튼 이벤트
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_search:
                startActivity(new Intent(this, SearchActivity.class));
                finish();
                break;
            case R.id.menu_list:
                startActivity(new Intent(this, ListActivity.class));
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
