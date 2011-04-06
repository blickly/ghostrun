package com.ghostrun.activity;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ghostrun.R;

public class WebMapActivity extends Activity implements LocationListener {
    //private static final String Map_URL = "http://gmaps-samples.googlecode.com/svn/trunk/articles-android-webmap/simple-android-map.html";
    private static final String Map_URL = "http://inst.eecs.berkeley.edu/~darrenk/pacmanmap/";
    private WebView webView;
    private Location mostRecentLocation;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webmap);
        getLocation();
        setupWebView();
    }
    
    /** Sets up the WebView object and loads the URL of the page **/
    private void setupWebView(){
        String centerURL="";
        if (mostRecentLocation != null) {
            centerURL = "javascript:centerAt(" + 
            mostRecentLocation.getLatitude() + "," + 
            mostRecentLocation.getLongitude()+ ")";
        }
        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        // Wait for the page to load then send the location information
        final String centerURL2 = centerURL;
        webView.setWebViewClient(new WebViewClient(){  
            @Override  
            public void onPageFinished(WebView view, String url)  
            {
                if (mostRecentLocation!=null){
                    webView.loadUrl(centerURL2);
                }
            }
        });
        webView.loadUrl(Map_URL);  
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