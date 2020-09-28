package net.jmake.youneedit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText et_id;
    private EditText et_pw;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_id = findViewById(R.id.et_id);
        et_pw = findViewById(R.id.et_pw);
        findViewById(R.id.bt_login).setOnClickListener(this);
        findViewById(R.id.bt_findpw).setOnClickListener(this);
        findViewById(R.id.bt_join).setOnClickListener(this);

        // 로그인 정보 가져오기
        SharedPreferences settings = getSharedPreferences("Member", 0);
        String mb_id = settings.getString("mb_id","");
        String mb_password = settings.getString("mb_password","");

        et_id.setText(mb_id);       // 아이디 입력
        et_pw.setText(mb_password); // 비밀번호 입력
    }

    // 클릭이벤트
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_login:     // 로그인 버튼
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                params.put("mb_id", et_id.getText());           // 아이디
                params.put("mb_password", et_pw.getText());     // 비밀번호
                client.get("http://youneedit.jmake.net/external/login.php", params, new AsyncHttpResponseHandler() {    // 주소
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {      // 로그인 성공
                        if(new String(response).equals("1") == true) {
                            // 로그인 정보 저장
                            SharedPreferences sp = getSharedPreferences("Member", 0);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("mb_id", String.valueOf(et_id.getText()));
                            editor.putString("mb_password", String.valueOf(et_pw.getText()));
                            editor.commit();

                            startActivity(new Intent(LoginActivity.this, SearchActivity.class));
                            finish();   // 액티비티 끝내기
                        } else {
                            Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();      // 알림 메세지
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {        // 로그인 실패
                    }
                });
                break;
            case R.id.bt_findpw:        // 비밀번호 찾기
                intent = new Intent(this, WebActivity.class);
                intent = intent.putExtra("url", "http://youneedit.jmake.net/bbs/password_lost.php");    // url 값 첨부해서 보냄
                startActivity(intent);
                break;
            case R.id.bt_join:      // 회원가입
                intent = new Intent(this, WebActivity.class);
                intent = intent.putExtra("url", "http://youneedit.jmake.net/bbs/register.php");    // url 값 첨부해서 보냄
                startActivity(intent);
                break;
        }
    }

    // 뒤로가기 이벤트
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.app_name)
                .setIcon(R.mipmap.ic_launcher)
                .setMessage("Do you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}
