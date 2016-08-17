package com.pressurelabs.flow;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class EULAActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_the_eula);
        Toolbar toolbar = (Toolbar) findViewById(R.id.eula_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(
                        EULAActivity.this, TheHubActivity.class
                ));
                finish();
                // TODO Fix app bar up
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
