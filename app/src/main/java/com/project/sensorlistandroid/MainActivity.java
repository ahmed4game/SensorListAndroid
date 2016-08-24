package com.project.sensorlistandroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements SensorEventListener {

    private ListView listView;
    private SensorManager mSensorManager;
    private List<Sensor> deviceSensors = null;
    public List<String> sensorsName = new ArrayList<String>();
    private static final String sensorState="sensor";
    private static final String sensorSelected="selected";
    private String selectedsensorname ="";
    private boolean isActivated=false;
    private Bundle b=new Bundle();
    private static String log="lifecycle";
    private TextView header,body,x,y,z;
    private Sensor mSensorObject=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpUI();
        setupListAdapter();
        populateSensors();
        Log.d(log, "onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(log,"onStart");
    }

    private void setupListAdapter() {
        listView.setAdapter(new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, sensorsName));
    }

    private void setUpUI(){
        header=(TextView)findViewById(R.id.sensorname);
        body=(TextView)findViewById(R.id.sensordetails);
        x=(TextView)findViewById(R.id.x);
        y=(TextView)findViewById(R.id.y);
        z=(TextView)findViewById(R.id.z);

        listView=(ListView)findViewById(R.id.sensorlist);
        listView.setVisibility(View.VISIBLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                isActivated=true;
                selectedsensorname =(String)listView.getItemAtPosition(position);
                Log.d(log,"Selected Sensor: "+ selectedsensorname);
                recreate();
            }
        });
        mSensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
        deviceSensors=mSensorManager.getSensorList(Sensor.TYPE_ALL);
    }

    private void populateSensors(){
        for (Sensor sensorName:deviceSensors){
            sensorsName.add(sensorName.getName());
        }
    }

    private void disabeListView(){
        listView.setVisibility(View.GONE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (isActivated){
        outState.putBoolean(sensorState,isActivated);
        outState.putString(sensorSelected, selectedsensorname);
        }
        Log.d(log,"onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isActivated=savedInstanceState.getBoolean(sensorState);
        if (isActivated) {

            if (!savedInstanceState.get(sensorSelected).equals("")){
                selectedsensorname=savedInstanceState.getString(sensorSelected);
                Log.d("Inside loop ",selectedsensorname);
            }
            disabeListView();
            for(Sensor s:deviceSensors){
                if (s.getName().equals(selectedsensorname)) {
                    mSensorObject = s;
                    break;
                }
            }
        }
        Log.d(log,"onRestoreInstanceState");
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d(log,"onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isActivated){
            mSensorManager.registerListener(this,mSensorObject,SensorManager.SENSOR_DELAY_NORMAL);
            header.setVisibility(View.VISIBLE);
            body.setVisibility(View.VISIBLE);
            x.setVisibility(View.VISIBLE);
            y.setVisibility(View.VISIBLE);
            z.setVisibility(View.VISIBLE);
        }else{
            header.setVisibility(View.GONE);
            body.setVisibility(View.GONE);
            x.setVisibility(View.GONE);
            y.setVisibility(View.GONE);
            z.setVisibility(View.GONE);
        }
        Log.d(log, "onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this, mSensorObject);
        Log.d(log, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(log, "onDestroy");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] evt = event.values;

        StringBuilder accelerate=new StringBuilder(2048);

        accelerate.append("  Name: "+mSensorObject.getName()+"\n");
        accelerate.append("  Type: "+mSensorObject.getType()+"\n");
        accelerate.append("  Vendor: "+mSensorObject.getVendor()+"\n");
        accelerate.append("  Version: "+mSensorObject.getVersion()+"\n");
        accelerate.append("  Resolution: "+mSensorObject.getResolution()+"\n");
        accelerate.append("  MaxRange: "+mSensorObject.getMaximumRange()+"\n");
        accelerate.append("  Power: "+mSensorObject.getPower()+"\n");
        accelerate.append("  MinDelay: "+mSensorObject.getMinDelay()+"\n");

        body.setText(accelerate);
        if(event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE)
        {
            x.setText("VALUE: "+evt[0]);
        }
        else
        {
            x.setText("X:"+evt[0]);
        }

        y.setText("Y:"+evt[1]);
        z.setText("Z:"+evt[2]);

        if(evt[0]==0.0)
        {	x.setVisibility(View.INVISIBLE);	}
        else
        {	x.setVisibility(View.VISIBLE);		}
        if(evt[1]==0.0)
        {	y.setVisibility(View.INVISIBLE);	}
        else
        {	y.setVisibility(View.VISIBLE);	}
        if(evt[2]==0.0)
        {	z.setVisibility(View.INVISIBLE);	}
        else
        {	z.setVisibility(View.VISIBLE);	}
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void confirmExit(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure want to Exit");

        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (isActivated){
            isActivated=!isActivated;
            recreate();
            return;
        }else {
            confirmExit();
        }
    }
}
