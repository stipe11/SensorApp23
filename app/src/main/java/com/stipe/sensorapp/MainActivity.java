package com.stipe.sensorapp;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.Callback;
import org.fusesource.mqtt.client.CallbackConnection;
import org.fusesource.mqtt.client.Listener;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.ttn.android.sdk.v1.api.DateTimeConverter;

import java.io.IOException;
import java.net.URISyntaxException;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity   {

    private FirebaseAuth mAuth;
    private android.support.v7.widget.Toolbar mToolbar;

    private ViewPager mViewPager;

    private TabLayout mTabLayout;


    private  Button button2;
    private TextInputLayout key;
    private TextInputLayout Name;
    private TextView sensor_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mToolbar=(android.support.v7.widget.Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("SensorApp");

        mViewPager=(ViewPager)findViewById(R.id.main_tabPager);

        mTabLayout=(TabLayout)findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        button2=(Button)findViewById(R.id.button2);
        key=(TextInputLayout) findViewById(R.id.accesskey);
        Name=(TextInputLayout)findViewById(R.id.appName);
        sensor_name=(TextView)findViewById(R.id.sensor);

        final TextView textViewTemperature=findViewById(R.id.textView2);
        final TextView textViewHumidity=findViewById(R.id.textView4);




        Gson mGson = new GsonBuilder()
                .registerTypeAdapter(DateTime.class, new DateTimeConverter())
                .create();

        String broker="eu.thethings.network";
        String accessKey="ttn-account-v2.Te15k94DT0zKqLsqrPVs83VHHs76CtThavHf5sHiFvg";
        String appName="senzor_zraka_t_h";
        String devEUI="+";




        sensor_name.setText(appName);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String accessKey=key.getEditText().getText().toString();
                String appName=Name.getEditText().getText().toString();
                sensor_name.setText(appName);

            }
        });


        final Topic mTopic = new Topic( "+"+ "/devices/" + devEUI + "/up",QoS.AT_LEAST_ONCE);

        final MQTT mMqtt = new MQTT();


        try {
            mMqtt.setHost("tcp://" + broker + ":" + "1883");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        mMqtt.setUserName(appName);
        mMqtt.setPassword(accessKey);

        final CallbackConnection mConnection = mMqtt.callbackConnection();

        mConnection.listener(new Listener() {
            @Override
            public void onConnected() {


            }

            @Override
            public void onDisconnected() {


            }

            @Override
            public void onPublish(UTF8Buffer topic, Buffer body, Runnable ack) {


                try {

                    String stringJson=new String((body.toByteArray()));
                    final TheThingsNetworkResponse response = new ObjectMapper().readValue(stringJson,TheThingsNetworkResponse.class);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Double temp = Double.parseDouble(response.PayloadFields.degressC);
                            Double humidity = Double.parseDouble(response.PayloadFields.getHumidity());
                            textViewTemperature.setText(String.format("%.2f °C", temp));
                            textViewHumidity.setText(String.format("%.2f %%", humidity));
                        }
                    });



                } catch (JsonParseException e) {
                    e.printStackTrace();
                } catch (JsonMappingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable value) {

            }
        });

        mConnection.connect(new Callback<Void>() {
            @Override
            public void onSuccess(Void value) {

                mConnection.subscribe(new Topic[]{mTopic}, new Callback<byte[]>() {
                    public void onSuccess(byte[] qoses) {

                    }

                    public void onFailure(Throwable value) {

                    }
                });
            }

            @Override
            public void onFailure(Throwable value) {

            }
        });



    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser==null){

            sendToStart();
        }

    }

    private void sendToStart() {
        Intent startIntent=new Intent(MainActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);



        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId()== R.id.main_logout_btn) {

            FirebaseAuth.getInstance().signOut();
            sendToStart();
        }



        return true;
    }

}
