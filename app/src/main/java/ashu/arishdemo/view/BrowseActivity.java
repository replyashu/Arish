package ashu.arishdemo.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import ashu.arishdemo.R;

/**
 * Created by apple on 15/04/18.
 */

public class BrowseActivity extends AppCompatActivity {

    WebView webView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_browse);

        webView = (WebView) findViewById(R.id.webWiev);
        webView.setWebViewClient(new WebViewClient());

        webView.loadUrl("http://www.google.com");

    }
}
