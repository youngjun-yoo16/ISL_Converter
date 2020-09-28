package net.jmake.youneedit;

import android.app.Dialog;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;

public class QuizActivity extends AppCompatActivity implements View.OnClickListener {
    private int i = 0;
    private Map<String, String> map;
    private Random random = new Random();   // 랜덤 함수

    private String questions[]; // 문제들
    private String question;    // 문제
    private int correct;    // 정답 변수(1번, 2번)
    private int score = 0;      // 점수

    private int cnt, t_cnt;     // 이미지 개수
    private TextView tv_score;  // 퀴즈 사진
    private ImageView iv_quiz;  // 퀴즈 사진
    private TextView tv_quiz;   // 퀴즈 단어
    private AssetManager am;
    private Handler handler = new Handler();
    private InputStream[] is;   // 파일변수
    private Bitmap[] bm;        // 이미지 변수
    private Timer timer = new Timer();  // 타이머

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // 버튼 이벤트
        findViewById(R.id.menu_search).setOnClickListener(this);
        findViewById(R.id.menu_list).setOnClickListener(this);
        findViewById(R.id.menu_quiz).setBackgroundColor(Color.parseColor("#006385"));
        findViewById(R.id.menu_contact).setOnClickListener(this);
        findViewById(R.id.menu_rank).setOnClickListener(this);

        tv_score = findViewById(R.id.tv_score);
        iv_quiz = findViewById(R.id.iv_quiz);
        tv_quiz = findViewById(R.id.tv_quiz);
        findViewById(R.id.bt_o).setOnClickListener(this);
        findViewById(R.id.bt_x).setOnClickListener(this);



        SharedPreferences sp = getSharedPreferences("Search", 0);     // 검색한 단어 가져오기
        // 변수에 넣어주기
        map = (Map<String, String>) sp.getAll();
        questions = new String[map.size()];
        Iterator<String> iterator = map.keySet().iterator();

        Log.e("Quiz", "map.size():" + map.size());
        if(map.size() < 2) {    // 검색 단어가 2개 이하일 때
            Toast.makeText(this, "You must search more than two words first.", Toast.LENGTH_SHORT).show();      // 메세지 띄우기
            startActivity(new Intent(this, SearchActivity.class));
            finish();
        } else {
            // 변수에 넣어주기
            while (iterator.hasNext()) {
                questions[i] = (String) iterator.next();
                i++;
            }

            am = getAssets();
            timer();    // 타이머 실행
            question(); // 문제 표시
        }
    }

    // 문제 표시
    private void question() {
        int q_num = random.nextInt(map.size());     // 랜덤으로 문제 번호 가져오기
        question = questions[q_num];    // 문제 데이터 가져오기
        correct = random.nextInt(2) + 1;    // 랜덤으로 1 아니면 2를 가져오기

        Log.e("Quiz", "q_num(현재문제의 번호): " + String.valueOf(q_num));
        Log.e("Quiz", "map.size()(총문제개수): " + String.valueOf(map.size()));
        Log.e("Quiz", "correct(정답): " + String.valueOf(correct));

        if(correct == 1) {  // O일 때
            tv_quiz.setText(questions[q_num]);  // 문제 표시
        } else {    // X일 때 다른 단어를 가져오기
            if(q_num == map.size() - 1) {
                tv_quiz.setText(questions[q_num - 1]);
            } else {
                tv_quiz.setText(questions[q_num + 1]);
            }
        }

        getImage();
    }

    // 이미지 가져오기
    private void getImage() {
        cnt = 1;

        // 폴더의 파일 개수 체크
        try {
            String list[] = am.list("sign/" + question);
            t_cnt = list.length - 1;
            Log.e("Quiz", "t_cnt: " + String.valueOf(t_cnt));
        } catch (IOException e) {
            e.printStackTrace();
        }

        is = new InputStream[t_cnt];
        bm = new Bitmap[t_cnt];

        // 수화사진 가져오기
        try {
            for(i=0; i<t_cnt; i++) {
                is[i] = am.open("sign/" + question + "/" + String.valueOf(i) + ".jpg");
                bm[i] = BitmapFactory.decodeStream(is[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 타이머 이벤트
    private void timer() {
        TimerTask timertask = new TimerTask() {
            @Override
            public void run() {
                if(t_cnt > 0) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            iv_quiz.setImageBitmap(bm[cnt]);    // 이미지 표시
                        }
                    });

                    // 이미지가 끝번호일 때 처음으로
                    if (cnt == t_cnt - 1) {
                        cnt = 1;
                    } else {
                        cnt++;
                    }
                }
            }
        };
        timer.schedule(timertask, 1000, 2000);  // 타이머 시간 설정 (1초 후에 2초마다 실행)
    }

    // 정답 처리
    private void answer(int a) {
        if(correct == a) {  // 정답일 때
            Toast.makeText(this, "The answer is correct.", Toast.LENGTH_SHORT).show();
            score += 10;
        } else {    // 정답 아닐 때
            Toast.makeText(this, "It is wrong.", Toast.LENGTH_SHORT).show();
            score -= 10;
        }

        tv_score.setText(String.valueOf(score));
        question();
    }

    // 종료시 팝업창 처리
    private void scoreUpload() {
        SharedPreferences sp = getSharedPreferences("Member", 0);
        SharedPreferences.Editor editor = sp.edit();
        String mb_id = sp.getString("mb_id", "");
        editor.putInt("mb_point", score);
        editor.commit();

        // 점수 입력
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("mb_id", mb_id);           // 아이디
        params.put("mb_point", score);     // 점수
        client.get("http://youneedit.jmake.net/external/point.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }

    // 버튼 이벤트
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_o: // O 클릭
                answer(1);
                break;
            case R.id.bt_x: // X 클릭
                answer(2);
                break;

            case R.id.menu_search:
                scoreUpload();
                startActivity(new Intent(this, SearchActivity.class));
                finish();
                break;
            case R.id.menu_list:
                scoreUpload();
                startActivity(new Intent(this, ListActivity.class));
                finish();
                break;
            case R.id.menu_contact:
                scoreUpload();
                startActivity(new Intent(this, ContactActivity.class));
                finish();
                break;
            case R.id.menu_rank:
                scoreUpload();
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
