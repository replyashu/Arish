package ashu.arishdemo.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

import ashu.arishdemo.R;
import ashu.arishdemo.model.LogTimeStamp;
import ashu.arishdemo.presenter.MainPresenter;
import io.realm.Realm;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton imgButtonBrowse;

    Button btnAdd;

    EditText editQuery;

    TextView txtKeyWords;

    RecyclerView recyclerLogs;

    private Realm realm;

    MainPresenter presenter;

    SharedPreferences sp;

    Set<String> keywordSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = getSharedPreferences("list", 0);

        keywordSet = sp.getStringSet("keywords", null);

        imgButtonBrowse = (ImageButton) findViewById(R.id.btnBrowse);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        editQuery = (EditText) findViewById(R.id.editQuery);
        txtKeyWords = (TextView) findViewById(R.id.txtKeyWords);
        recyclerLogs = (RecyclerView) findViewById(R.id.recyclerLogs);
        recyclerLogs.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        realm = Realm.getInstance(getApplicationContext());

        if(keywordSet != null ) {
            txtKeyWords.setText(keywordSet.toString());
        }


        imgButtonBrowse.setOnClickListener(this);
        btnAdd.setOnClickListener(this);

        fetchLogs();


    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnBrowse)
            startActivity(new Intent(MainActivity.this, BrowseActivity.class));

        else if(v.getId() == R.id.btnAdd){
            if(keywordSet == null)
                keywordSet = new HashSet<>();
            if(!editQuery.getText().toString().isEmpty() && editQuery.getText().toString()!= null) {
                keywordSet.add(editQuery.getText().toString());
                sp.edit().putStringSet("keywords", keywordSet).commit();
                txtKeyWords.setText(keywordSet.toString());
                editQuery.setText("");
            }
        }
    }

    private void fetchLogs(){
        LogAdapter adapter = new LogAdapter(MainActivity.this, realm.allObjects(LogTimeStamp.class));
        recyclerLogs.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
