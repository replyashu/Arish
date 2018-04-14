package ashu.arishdemo.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import ashu.arishdemo.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton imgButtonBrowse;

    Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgButtonBrowse = (ImageButton) findViewById(R.id.btnBrowse);

        imgButtonBrowse.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnBrowse)
            startActivity(new Intent(MainActivity.this, BrowseActivity.class));
    }
}
