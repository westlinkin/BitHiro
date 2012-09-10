package com.westlinkin.bithiro;

import com.westlinkin.bithiro.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

public class FinishActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);
     	 //fini
        Log.v("finish", "onCreate finish");
        this.finish();
        Intent intent = new Intent(Intent.ACTION_MAIN);    
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);    
        intent.addCategory(Intent.CATEGORY_HOME);    
        startActivity(intent);    
        Log.v("finish", "HOME");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_finish, menu);
        return true;
    }
    
    @Override
    public void onDestroy(){
        Log.v("finish", "onDestroy finish");
    //	System.exit(0);
        super.onDestroy();
    }
}
