package com.example.android.guittone;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import static com.example.android.guittone.R.menu.toolbar;



public class MainActivity extends AppCompatActivity {

    public static ArrayList <Device> devices = MainFragment.devices;
    //DeviceAdapter adapter = MainFragment.adapter;
    public static WebView webView;
    //ListView listView = MainFragment.listView;
    //TextView instructionsTextView = MainFragment.instructionsTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpager);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar3);
        myToolbar.setTitleTextAppearance(getApplicationContext(), R.style.MyTitleTextAppearance);
        setSupportActionBar(myToolbar);
        //myToolbar.setcolo

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        GuittoneFragmentPagerAdapter gadapter = new GuittoneFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(gadapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);


        //myToolbar.setcolo

        /*SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        Gson gson = new Gson();
        String json = appSharedPrefs.getString("Devices", "");
        devices.clear();
        if (json.equals("")) {
        } else {
            devices = gson.fromJson(json, new TypeToken<ArrayList<Device>>() {
            }.getType());
        }*/

        //View initialization
        webView = (WebView) findViewById(R.id.webview);
        //adapter = new DeviceAdapter(this, devices);
        //listView = (ListView) findViewById(R.id.list_item);
        //listView.setAdapter(adapter);
        //instructionsTextView = (TextView) findViewById(R.id.instructions_textView);






    }







    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:

                AddDevice();
                return true;

            case R.id.action_info:

                return true;

            case R.id.action_reconnect:
                android.support.v7.app.AlertDialog.Builder alert = new android.support.v7.app.AlertDialog.Builder(this);
                alert.setMessage("Make sure you are connected to GuittoneWiFi");
                alert.setTitle("Reconnect device");

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String url = "http://192.168.1.1";
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                        browserIntent.setData(Uri.parse(url));
                        startActivity(browserIntent);
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // what ever you want to do with No option.
                    }
                });
                alert.show();
                return true;

            case R.id.power:
                //Intent deviceIntent = new Intent(this, PowerActivity.class);
                //this.startActivity(deviceIntent);

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    public void AddDevice() {
        GetUrlAsyncTask url = new GetUrlAsyncTask();
        url.execute();
        android.support.v7.app.AlertDialog.Builder alert = new android.support.v7.app.AlertDialog.Builder(this);
        alert.setMessage("Make sure you are connected to GuittoneWiFi");
        alert.setTitle("Connect device");

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String url = "http://192.168.1.1";
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse(url));
                startActivity(browserIntent);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });
        alert.show();

    }

    @Override
    protected void onPause() {
        Save();
        super.onPause();
    }

    public void Save(){
        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(devices);
        prefsEditor.putString("Devices", json);
        prefsEditor.apply();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("asoi", " forze non va una pe");
        if(!isNetworkAvailable()){
            Toast toast = Toast.makeText(this, "Could not connect to server - Check your internet connection", Toast.LENGTH_SHORT);
            toast.show();
        }
        /*SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        Gson gson = new Gson();
        String json = appSharedPrefs.getString("Devices", "");
        devices.clear();
        if (json.equals("")) {
        } else {
            devices = gson.fromJson(json, new TypeToken<ArrayList<Device>>() {
            }.getType());
        }
        Log.w("" + devices.size(), "array size on resume");
        //adapter = new DeviceAdapter(this, devices);
        //ListView listView = (ListView) findViewById(R.id.list_item);
        listView.setClickable(true);
        listView.setAdapter(adapter);
        if(isNetworkAvailable()){
            //CheckAsyncTask task = new CheckAsyncTask();
            //task.execute();
        }else{
            Toast toast = Toast.makeText(this, "Could not connect to server - Check your internet connection", Toast.LENGTH_SHORT);
            toast.show();
        }
        adapter.notifyDataSetChanged();
        listView.requestFocus();
        if(devices.size()>0){
            listView.setVisibility(View.VISIBLE);
            instructionsTextView.setVisibility(View.GONE);
        }else{
            listView.setVisibility(View.GONE);
            instructionsTextView.setVisibility(View.VISIBLE);
        }*/
    }
     private boolean isNetworkAvailable() {
         ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
         NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
         return activeNetworkInfo != null && activeNetworkInfo.isConnected();
      }

    private class CheckAsyncTask extends AsyncTask<URL, Void, ArrayList<Device>> {

        @Override
        protected ArrayList<Device> doInBackground(URL... urls) {
            // Create URL object
            URL url ;


            // Extract relevant fields from the JSON response and create an {@link Event} object
            for(int z =0; z<devices.size();z++){
                // Perform HTTP request to the URL and receive a JSON response back
                String jsonResponse = "";
                try {
                    url = createUrl(devices.get(z).getCheckUrl());
                    if(url == null){}else{jsonResponse = makeHttpRequest(url);}
                } catch (IOException e) {
                    // TODO Handle the IOException
                }

                if(jsonResponse.equals("0l")){
                    devices.get(z).setmStatus(false);
                }
                if(jsonResponse.equals("1h")){
                    devices.get(z).setmStatus(true);
                }

            }

            // Return the {@link Event} object as the result fo the {@link TsunamiAsyncTask}
            return devices;
        }


        @Override
        protected void onPostExecute(ArrayList<Device> earthquake) {
            if (earthquake == null) {
                return;
            }

            //MainFragment.dataNotify();
        }

        private URL createUrl(String stringUrl) {
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                Log.e("", "Error with creating URL", exception);
                return null;
            }
            return url;
        }


        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";
            //if (url == null){
            //    return jsonResponse;
            //}
            HttpURLConnection urlConnection = null;
             InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                //urlConnection.setRequestMethod("GET");
                //urlConnection.setReadTimeout(10000 /* milliseconds */);
              // /* urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.connect();

                 if(urlConnection.getResponseCode()==200){
                       inputStream = urlConnection.getInputStream();
                        jsonResponse = readFromStream(inputStream);
                   } else {
                        Log.e("MainActivity","" + urlConnection.getResponseCode());
                     Toast toast = Toast.makeText(getApplicationContext(),"Could not connect to server", Toast.LENGTH_SHORT);
                     toast.show();
                  }


            } catch (IOException e) {
                // TODO: Handle the exception
                Log.e("MainActivity", e.getMessage());
                Toast toast = Toast.makeText(getApplicationContext(),"Could not connect to server", Toast.LENGTH_SHORT);
                toast.show();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                //if (inputStream != null) {
                    //function must handle java.io.IOException here
                  //  inputStream.close();
               // }
            }
            return jsonResponse;

        }

        /**
         * Convert the {@link InputStream} into a String which contains the
         * whole JSON response from the server.
         */
        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }


    }


    private class GetUrlAsyncTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            // Create URL object
            URL url ;


            // Extract relevant fields from the JSON response and create an {@link Event} object

            // Perform HTTP request to the URL and receive a JSON response back
            String jsonResponse = "";
            try {
                url = createUrl("http://192.168.1.1");//devices.get(e).getCheckUrl
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                // TODO Handle the IOException
            }


            // Return the {@link Event} object as the result fo the {@link TsunamiAsyncTask}
            return jsonResponse;
        }


        @Override
        protected void onPostExecute(String dataFromUrl) {
            ArrayList<String> CheckUrl = new ArrayList<>();
            ArrayList<String> OnUrl = new ArrayList<>();
            ArrayList<String> OffUrl = new ArrayList<>();
            OffUrl.clear();
            OnUrl.clear();
            CheckUrl.clear();
            int StopOn = dataFromUrl.indexOf("StopOn");
            int StopOff = dataFromUrl.indexOf("StopOff");
            int StopCheck = dataFromUrl.indexOf("StopCheck");

            if(dataFromUrl.contains("OnUrl")){
                for (int i = 60;i<StopOn;i+=70){
                    int a = dataFromUrl.indexOf("OnUrl",i);
                    int b = dataFromUrl.indexOf("on.php",i+40);
                    if(a==-1 || b==-1){}else {
                        Log.e("" + dataFromUrl.substring(a + 16, b + 6), "On url");
                        OnUrl.add(dataFromUrl.substring(a + 16, b + 6));
                    }
                }
            }

            if(dataFromUrl.contains("OffUrl")){
                for (int i = StopOn;i<StopOff;i+=70){
                    int a = dataFromUrl.indexOf("OffUrl",i);
                    int b = dataFromUrl.indexOf("off.php",i+40);
                    if(a==-1 || b==-1){}else {
                        Log.e("" + dataFromUrl.substring(a+17,b+7),"Off url");
                        OffUrl.add(dataFromUrl.substring(a+17,b+7));
                    }
                }
            }

            if(dataFromUrl.contains("CheckUrl")){
                for (int i = StopOff;i<StopCheck;i+=70){
                    int a = dataFromUrl.indexOf("CheckUrl",i);
                    int b = dataFromUrl.indexOf("arduino.txt",i+40);
                    if(a==-1 || b==-1){}else {
                        Log.e("" + dataFromUrl.substring(a+19,b+11),"Check url");
                        CheckUrl.add(dataFromUrl.substring(a+19,b+11));
                    }
                }
            }

            for (int i = 0;i<OffUrl.size();i++){
                devices.add(new Device("New plug",OnUrl.get(i),OffUrl.get(i),CheckUrl.get(i)));
                //MainFragment.dataNotify(devices);
                Save();
            }
            Log.e("finitop","sono a posto così grazie");
            MainFragment.dataNotify(devices);
            /*if(devices.size()>0){
                listView.setVisibility(View.VISIBLE);
                instructionsTextView.setVisibility(View.GONE);
            }else{
                listView.setVisibility(View.GONE);
                instructionsTextView.setVisibility(View.VISIBLE);
            }*/

        }

        private URL createUrl(String stringUrl) {
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                Log.e("", "Error with creating URL", exception);
                return null;
            }
            return url;
        }

        /**
         * Make an HTTP request to the given URL and return a String as the response.
         */

        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";
            if (url == null){
                return jsonResponse;
            }
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                //urlConnection.setRequestMethod("GET");
                //urlConnection.setReadTimeout(10000 /* milliseconds */);
                //urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.connect();

                if(urlConnection.getResponseCode()==200){
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                } else {
                    Log.e("MainActivity","" + urlConnection.getResponseCode());
                }


            } catch (IOException e) {
                // TODO: Handle the exception
                Log.e("MainActivity", e.getMessage());
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                // if (inputStream != null) {
                //   // function must handle java.io.IOException here
                //   inputStream.close();
                //}
            }
            return jsonResponse;

        }

        /**
         * Convert the {@link InputStream} into a String which contains the
         * whole JSON response from the server.
         */
        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }


    }
}