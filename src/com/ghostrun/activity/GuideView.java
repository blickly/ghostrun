package com.ghostrun.activity;

import com.ghostrun.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class GuideView extends Activity {
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guideview);
        
        // Stop the current activity and return to the previous view.
        Button logobutton=(Button)findViewById(R.id.guideview_paclogo);
        logobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
    }
}