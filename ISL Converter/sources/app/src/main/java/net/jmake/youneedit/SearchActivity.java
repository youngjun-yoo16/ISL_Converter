package net.jmake.youneedit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    private Handler handler = new Handler();
    private AssetManager am;

    private EditText et_keyword;        // 검색칸
    private ImageView iv_picture, iv_image;     // 수화사진, 수화이미지

    private int i;
    private String keyword;      // 검색어
    private int cnt, t_cnt;     // cnt: 현재 수화사진 번호, t_cnt: 수화사진 총 개수

    private InputStream[] is;
    private Bitmap[] bm;
    private Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        findViewById(R.id.menu_search).setBackgroundColor(Color.parseColor("#006385"));     // 현재 메뉴 색상 바꾸기
        // 버튼 이벤트
        findViewById(R.id.bt_search).setOnClickListener(this);
        findViewById(R.id.menu_list).setOnClickListener(this);
        findViewById(R.id.menu_quiz).setOnClickListener(this);
        findViewById(R.id.menu_contact).setOnClickListener(this);
        findViewById(R.id.menu_rank).setOnClickListener(this);

        et_keyword = findViewById(R.id.et_keyword);     // 검색칸
        iv_picture = findViewById(R.id.iv_picture);     // 수화사진
        iv_image = findViewById(R.id.iv_image);         // 수화 이미지

        am = getAssets();      // 이미지 폴더
        timer();                // 이미지 바꾸는 이벤트

        // 리스트에서 클릭할때 검색 값 가져오기
        Intent intent = getIntent();
        keyword = intent.getStringExtra("keyword");
        if(keyword != null) {
            et_keyword.setText(keyword);
            search();
        }
    }

    // 검색 처리
    private void search() {
        cnt = 1;

        // 폴더의 파일 개수 체크
        try {
            String list[] = am.list("sign/" + keyword);     // 변수에 폴더 리스트를 저장
            t_cnt = list.length - 1;    // 총 개수를 저장
            Log.e("Search", "t_cnt: " + String.valueOf(t_cnt));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 변수 생성
        is = new InputStream[t_cnt];
        bm = new Bitmap[t_cnt];

        // 이미지 가져오기
        if(t_cnt > 0) {     // 검색 성공
            try {
                // 검색한 폴더의 사진 및 이미지를 저장
                for(i=0; i<t_cnt; i++) {
                    is[i] = am.open("sign/" + keyword + "/" + String.valueOf(i) + ".jpg");
                    bm[i] = BitmapFactory.decodeStream(is[i]);
                }

                // 저장
                SharedPreferences sp;
                SharedPreferences.Editor editor;

                // 검색한 단어를 퀴즈에 활용하기 위해 저장
                sp = getSharedPreferences("Search", 0);
                editor = sp.edit();
                int cnt = sp.getInt(keyword, 0);
                Log.e("숫자", String.valueOf(cnt));
                editor.putInt(keyword, 0);   // 검색 단어 저장
                editor.commit();

                // 기분 점수 저장
                sp = getSharedPreferences("Member", 0);
                editor = sp.edit();
                InputStream is = am.open("sign/" + keyword + "/score.txt");
                int length = is.available();
                if (length > 0) {
                    byte[] buffer = new byte[length];
                    is.read(buffer);
                    editor.putInt("mb_mood", Integer.parseInt(new String(buffer)));   // 검색 단어 저장
                    editor.commit();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {    // 검색 실패
            Toast.makeText(this, "This word has not been added yet", Toast.LENGTH_SHORT).show();
            iv_picture.setImageBitmap(null);
            iv_image.setImageBitmap(null);
        }

        // 키보드 안보이게 하는거
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et_keyword.getWindowToken(), 0);
    }

    // 타이머 이벤트
    private void timer() {
        TimerTask timertask = new TimerTask() {
            @Override
            public void run() {
                if(t_cnt > 0) {
                    Log.e("Search", "cnt: " + String.valueOf(cnt));
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // 이미지 표시
                            iv_picture.setImageBitmap(bm[cnt]);
                            iv_image.setImageBitmap(bm[0]);
                        }
                    });

                    // 이미지 번호가 끝이면 처음으로
                    if (cnt == t_cnt - 1) {
                        cnt = 1;
                    } else {
                        cnt++;
                    }
                }
            }
        };
        timer.schedule(timertask, 1000, 2000);  // 타이머 이벤트 1초 후에 2초마다 실행
    }

    // 버튼 이벤트
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_search:    // 검색 버튼 이벤트
                keyword = String.valueOf(et_keyword.getText());
                keyword = keyword.toLowerCase();    // 소문자로 변환
                search();
                break;
            // 메뉴 이벤트
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
            case R.id.menu_rank:
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

    // 액티비티 종료할 때
    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();     // 타이머 꺼주기
    }
}
