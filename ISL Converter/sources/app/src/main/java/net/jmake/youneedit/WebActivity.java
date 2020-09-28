package net.jmake.youneedit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebActivity extends AppCompatActivity {
    private WebView wv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        // url 가져오기
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");

        // 웹뷰 표시
        wv = findViewById(R.id.wv);
        wv.getSettings().setJavaScriptEnabled(true);    // 자바스크립트 활성화
        wv.setWebViewClient(new WebViewClient());      // 새창 방지
        wv.setWebChromeClient(new WebChromeClient());
        wv.loadUrl(url);
    }
}
