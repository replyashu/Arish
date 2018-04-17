package ashu.arishdemo.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

import ashu.arishdemo.R;
import ashu.arishdemo.model.LogTimeStamp;
import ashu.arishdemo.utils.NetworkInterface;
import io.realm.Realm;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.R.attr.format;
import static android.R.attr.mimeType;
import static android.R.id.input;
import static java.security.AccessController.getContext;

/**
 * Created by apple on 15/04/18.
 */

public class BrowseActivity extends AppCompatActivity implements View.OnClickListener, Callback<ResponseBody>{

    SearchView searchView;
    Button btnGo;
    Button btnHome;
    RelativeLayout relSafe;
    WebView webView;

    private final Handler uiHandler = new Handler();
    private ProgressDialog progressDialog;

    Handler handlerForJavascriptInterface = new Handler();

    private String urlLoad = null;


    SearchResultAdapter adapter;


    private SharedPreferences sp;
    private Realm mRealm;
    private String prev = null;
    Set<String> stringSet;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_browse);

        searchView = (SearchView) findViewById(R.id.searchContent);
        btnGo = (Button) findViewById(R.id.btnGo);
        btnHome = (Button) findViewById(R.id.btnHome);
        btnHome.setOnClickListener(this);
        btnGo.setOnClickListener(this);
        relSafe = (RelativeLayout) findViewById(R.id.relSafe);
        webView = (WebView) findViewById(R.id.webView);

        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.getSettings().setGeolocationEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        sp = getSharedPreferences("list", 0);

        stringSet = sp.getStringSet("keywords", null);
        mRealm = Realm.getInstance(getApplicationContext());

        searchView.setIconified(false);

        webView.addJavascriptInterface(new MyJavaScriptInterface(this), "HtmlViewer");



    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnGo){
            try {
                browseSafe(readInput());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(v.getId() == R.id.btnHome) {
            startActivity(new Intent(BrowseActivity.this, MainActivity.class));
            finish();
        }

    }

    private String readInput(){
        return searchView.getQuery().toString();
    }

    private void browseSafe(String query) throws IOException {

        if(stringSet != null){
            boolean isBlocked = false;
            for(String key: stringSet){
                if(query.contains(key)){
                    isBlocked = true;
                }
            }
            if(isBlocked) {
                webView.setVisibility(View.GONE);
                relSafe.setVisibility(View.VISIBLE);
                dumpLog(query);
            }
            else {
                webView.setVisibility(View.VISIBLE);
                relSafe.setVisibility(View.GONE);

                new SearchInBackground().execute(query);
                webView.loadUrl("https://www.google.com/search?q=" + query);
                webView.setWebViewClient(new WebViewClient());
                getHtml(webView.getUrl());

            }
        }
        else{
            webView.setVisibility(View.VISIBLE);
            relSafe.setVisibility(View.GONE);

            new SearchInBackground().execute(query);
            webView.loadUrl("https://www.google.com/search?q=" + query);
            webView.setWebViewClient(new WebViewClient(){
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    webView.loadUrl("about:blank");
                    getHtml(webView.getUrl());
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    return true;
                }
            });
        }
    }



    private void getHtml(String url){
        Retrofit retrofit = getRetrofit(url);

        NetworkInterface networkInterface = retrofit.create(NetworkInterface.class);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Call<ResponseBody> resultHtml = networkInterface.getUsers(url);
        resultHtml.enqueue(this);


    }


    private Retrofit getRetrofit(String url){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.google.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit;
    }



    private void search(String q) throws IOException {
        Elements links=null;

        List<String> urls = new ArrayList<>();

        try {
            Document doc = Jsoup.connect("https://www.google.com/search?q=" + q).get();
            links = doc.select("img");
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        for (Element link : links) {
            String url = link.toString().substring(10, link.toString().length()-2);
            boolean toBeAdded = true;

            if(stringSet != null) {
                for (String key : stringSet) {
                    if (url.contains(key)) {
                        dumpLog(key);
                        toBeAdded = false;
                    }
                }
            }

            if(toBeAdded)
                urls.add(url);
        }

        adapter = new SearchResultAdapter(BrowseActivity.this, urls);

    }

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        if(response.isSuccessful()){
            Document doc = null;
            try {
                doc = Jsoup.parse(response.body().string());

                String mat = "";
                for(String key: stringSet){
                    mat += ":contains(" + key + "),";
                }

                String orig = doc.toString().toLowerCase();

                for(String key: stringSet){
                    orig = orig.replace(key, " ");
                }

                mat = mat.substring(0, mat.length()-1);


                Elements elements = doc.select(mat);

                if(elements.size() > 0) {
                    String dump = elements.first().text();
                    if(dump.length() > 37){
                        dump = dump.substring(0,37) + "...";
                    }
                    dumpLog(dump);
                }


                webView.loadDataWithBaseURL("http://www.google.com",orig, "text/html", "UTF-8",null);


            } catch (IOException e) {
                e.printStackTrace();
            }
            catch (NullPointerException e){

            }

        }
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {

    }

    class SearchInBackground extends AsyncTask<String, Void, Void>{


        @Override
        protected Void doInBackground(String... params) {
            try {
                search(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    @Override
    protected void onDestroy() {
        if(webView != null)
            webView.destroy();
        webView = null;
        mRealm.close();
        super.onDestroy();

    }

    private void dumpLog(String query){
        mRealm.beginTransaction();
        LogTimeStamp logTimeStamp = mRealm.createObject(LogTimeStamp.class);
        logTimeStamp.setKeyword(query);

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String date = dateFormat.format(new Date()); // Find todays date
            logTimeStamp.setDateTime(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mRealm.commitTransaction();
    }


    class MyJavaScriptInterface
    {
        private Context ctx;

        MyJavaScriptInterface(Context ctx)
        {
            this.ctx = ctx;
        }

        @JavascriptInterface
        @SuppressWarnings("unused")
        public void showHTML(final String html)
        {
            //code to use html content here
            Log.d("HTML", html);
        }
    }
}
