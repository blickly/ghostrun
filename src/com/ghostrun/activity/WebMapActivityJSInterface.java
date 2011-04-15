package com.ghostrun.activity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

import com.ghostrun.R;

public class WebMapActivityJSInterface extends Activity implements LocationListener {
    /** Called when the activity is first created. */
    private static final String Map_URL = "http://inst.eecs.berkeley.edu/~darrenk/PacmanMap/";
    private static final String TAG = "WebMapActivityJSInterface";
    private WebView webView;
    private Location mostRecentLocation;
    private String xmlContent="";
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webmap);
        getLocation();
        setupWebView();
        //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        final Date d = new Date();
        String filename = "Map_"+d.getTime();
        final EditText text = (EditText)findViewById(R.id.filename);
        text.setText(filename);
        Button save = (Button)findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                writeToFile(text.getText().toString()+".pac");
            }
        });
        
        // Stop the current activity and return to the previous view.
        Button logobutton=(Button)findViewById(R.id.webmap_paclogo);
        logobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    
    private void writeToFile(String fileName) {
        if (xmlContent==null) {
            return;
        }
        try {
            File root = Environment.getExternalStorageDirectory();
            if (root.canWrite()){
                File outputfile = new File(root, fileName);
                FileWriter filewriter = new FileWriter(outputfile);
                BufferedWriter out = new BufferedWriter(filewriter);
                webView.loadUrl("javascript:xmlButton.click_func();");
                out.write(xmlContent);
                out.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "Could not write file " + e.getMessage());
        }
    }
    
    /** Sets up the WebView object and loads the URL of the page **/
    private void setupWebView(){
        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(Map_URL);  

        /** Allows JavaScript calls to access application resources **/
        webView.addJavascriptInterface(new JavaScriptInterface(), "webviewinterface");
    }
    
    /** Sets up the interface for getting access to Latitude and Longitude data from device **/
    @SuppressWarnings("unused")
    private class JavaScriptInterface {
        public double getLatitude(){
            return mostRecentLocation.getLatitude();
        } 
        public double getLongitude(){
            return mostRecentLocation.getLongitude();
        }
        
        public void setXmlContent(String s, String filename) {
            xmlContent=s;
            //             writeToFile(filename);
        }
    }

    /** The Location Manager manages location providers. This code searches
        for the best provider of data (GPS, WiFi/cell phone tower lookup,
        some other mechanism) and finds the last known location.
    **/
    private void getLocation() {      
        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String provider = locationManager.getBestProvider(criteria,true);

        //In order to make sure the device is getting location, request updates.        locationManager.requestLocationUpdates(provider, 1, 0, this);
        mostRecentLocation = locationManager.getLastKnownLocation(provider);
    }

    /** Sets the mostRecentLocation object to the current location of the device **/
    @Override
    public void onLocationChanged(Location location) {
        mostRecentLocation = location;
    }

    /** The following methods are only necessary because WebMapActivity implements LocationListener **/ 
    @Override
    public void onProviderDisabled(String provider) {
    }
        
    @Override
    public void onProviderEnabled(String provider) {
    }
        
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}