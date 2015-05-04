package com.example.tut1;

import android.bluetooth.BluetoothAdapter;
import android.support.v4.app.Fragment;
//import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import org.achartengine.GraphicalView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.app.IntentService;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
//import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.achartengine.GraphicalView;


public class ECGPLotActivity  extends Activity {
    String MacAddress ="30:14:12:08:24:18";


    private static final String FILENAME="simavals123";
    //ByteQueue queue = new ByteQueue();
    boolean testFlag=true;
    byte[] buffer;
    byte[] readBuf;
    int FLAG = 1;
    int i=0,k=0;
    private static GraphicalView view;
    private DynamicECGPLot dynamicPlot = new DynamicECGPLot();
    private static Thread thread;
    private static Thread thread2;
    private static Thread thread3;
    int bytes;
    ArrayAdapter<String> listAdapter;
    TextView t;
    ListView listView;
    BluetoothAdapter btAdapter;
    Set<BluetoothDevice> devicesArray;
    ArrayList<String> pairedDevices;
    ArrayList<BluetoothDevice> devices;
    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    protected static final int SUCCESS_CONNECT = 0;
    protected static final int MESSAGE_READ = 1;
    IntentFilter filter;
    BroadcastReceiver receiver;
    String tag = "debugging";

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            Log.i(tag, "in handler");
            super.handleMessage(msg);
            switch(msg.what){
                case SUCCESS_CONNECT:
                    // DO something
                    ConnectedThread connectedThread = new ConnectedThread((BluetoothSocket)msg.obj);
                    Toast.makeText(getApplicationContext(), "CONNECT", Toast.LENGTH_LONG).show();
                    connectedThread.write("A".getBytes());

                    Log.i(tag, "connected");
                    connectedThread.run();
                    break;
                case MESSAGE_READ:
                    readBuf=(byte[]) msg.obj;

                    thread3 = new Thread() {
                        public void run() {

                            //while(i<(1024/8)) {
                            //Plot((double) readBuf[0], i++);
                            k++;
                            point p = new point((double) bytes, k++);//Got new Data
                            dynamicPlot.addNewPoint(p);
                            i++;
                            k = k + 10;
                            if (i == 255)

                            {


                                for (int h = 0; h < 99; h++) {
                                    for (int j = 0; j < 99; j++) {
                                        //delay
                                        Log.i(tag, "Reached Delay ");
                                    }
                                }

                            }
                        }
                    };thread3.start();


                    String string = new String(readBuf);
                    Log.i(tag, string);
                    //t.setText(string);
                    Toast.makeText(getApplicationContext(), "" +  readBuf[1] + " ,  " + i, Toast.LENGTH_SHORT).show();
                    //}
                    break;



            }
        }
    };

    // Initialize the button to perform device discovery





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecgplot);
        Intent chartingIntent = getIntent(); // gets the previously created intent
        MacAddress = chartingIntent.getStringExtra("MacAddress");
        FLAG=chartingIntent.getIntExtra("FLAG",1);
        init();
        if(btAdapter==null){
            Toast.makeText(getApplicationContext(), "No bluetooth detected", Toast.LENGTH_LONG).show();
            finish();
        }
        else{
            if(!btAdapter.isEnabled()){
                turnOnBT();
            }

            getPairedDevices();
            startDiscovery();
        }

        //If flag is set to 1 then plot existing data
        if(FLAG==1)
            Plot();

       /* thread = new Thread(){
            public void run(){

                for(int i=0;i<50000;i++){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                     point p = MockData.getDataFromReceiver(i);//Got new Data
                    dynamicPlot.addNewPoint(p);//Add it ot the graph
                    view.repaint();

                }
            }
        };
        thread.start();*/



    }//Close OnCreate


    private void startDiscovery() {
        // TODO Auto-generated method stub
        btAdapter.cancelDiscovery();
        btAdapter.startDiscovery();

    }
    private void turnOnBT() {
        // TODO Auto-generated method stub
        Intent intent =new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, 1);
    }
    private void getPairedDevices() {
        // TODO Auto-generated method stub
        devicesArray = btAdapter.getBondedDevices();
        if(devicesArray.size()>0){
            for(BluetoothDevice device:devicesArray){
                pairedDevices.add(device.getName());

            }
        }
    }


    private void init() {
        // TODO Auto-generated method stub
        //t=(TextView)findViewById(R.id.data);
        listView=(ListView)findViewById(R.id.listView);
        //listView.setOnItemClickListener(this);
        //listAdapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,0);
        //listView.setAdapter(listAdapter);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        pairedDevices = new ArrayList<String>();
        filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        devices = new ArrayList<BluetoothDevice>();
        receiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO Auto-generated method stub
                String action = intent.getAction();

                if(BluetoothDevice.ACTION_FOUND.equals(action)){
                   /* BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    devices.add(device);
                    String s = "";
                    for(int a = 0; a < pairedDevices.size(); a++){
                        if(device.getName().equals(pairedDevices.get(a))){
                            //append
                            s = "(Paired)";
                            break;
                        }
                    }

                    listAdapter.add(device.getName()+" "+s+" "+"\n"+device.getAddress());*/
                }

                else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
                    // run some code
                }
                else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                    // run some code



                }
                else if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
                    if(btAdapter.getState() == btAdapter.STATE_OFF){
                        turnOnBT();
                    }
                }

            }
        };

        registerReceiver(receiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        registerReceiver(receiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(receiver, filter);
    }


    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_CANCELED){
            Toast.makeText(getApplicationContext(), "Bluetooth must be enabled to continue", Toast.LENGTH_SHORT).show();
            finish();
        }
    }



    // public void onItemClick(AdapterView<?> arg0, View arg1,int arg2,long arg3) {
    // TODO Auto-generated method stub
    public void startBluetooth(View view){

        if(btAdapter.isDiscovering()){
            btAdapter.cancelDiscovery();
        }


        BluetoothDevice selectedDevice =btAdapter.getRemoteDevice(MacAddress);//btAdapter.getRemoteDevice ("30:14:12:08:24:18");(BluetoothDevice)o[arg2];
        ConnectThread connect = new ConnectThread(selectedDevice);
        connect.start();
        Log.i(tag, "in click listener");
    }



    public void startBluetooth(){

        if(btAdapter.isDiscovering()){
            btAdapter.cancelDiscovery();
        }


        BluetoothDevice selectedDevice =btAdapter.getRemoteDevice(MacAddress);//btAdapter.getRemoteDevice ("30:14:12:08:24:18");(BluetoothDevice)o[arg2];
        ConnectThread connect = new ConnectThread(selectedDevice);
        connect.start();
        Log.i(tag, "in click listener");


    }








    private class ConnectThread extends Thread {

        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;
            Log.i(tag, "construct");
            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.i(tag, "get socket failed");

            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            btAdapter.cancelDiscovery();
            Log.i(tag, "connect - run");
            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
                Log.i(tag, "connect - succeeded");
            } catch (IOException connectException) {

                Log.i(tag, "connect failed");
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                    startBluetooth();
                } catch (IOException closeException) {

                }
                return;
            }

            // Do work to manage the connection (in a separate thread)

            mHandler.obtainMessage(SUCCESS_CONNECT, mmSocket).sendToTarget();
        }



        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }



    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;


        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {

            // buffer store for the stream
            // bytes returned from read()


            // Keep listening to the InputStream until an exception occurs
            //Plot(true);
            thread2 = new Thread() {
                public void run() {
                    while (true) {
                        try {

                            // Read from the InputStream
                            buffer = new byte[1024];

                            bytes = mmInStream.read(buffer);
                            //queue.addByte((byte)bytes);
                            Log.i(tag, "SHOULD PLOT A POINT");
                            //Plot((double)bytes,k);
                            /*k++;
                            point p = new point((double) bytes, k++);//Got new Data
                            dynamicPlot.addNewPoint(p);
                            i++;
                            k = k + 10;
                            if (i == 255) {


                                for (int h = 0; h < 999; h++) {
                                    for (int j = 0; j < 999; j++) {
                                        //delay
                                        Log.i(tag, "Reached Delay ");
                                    }
                                }

                            }*/
                            Log.i(tag, "Surpassed dynamicPlot.addNewPoint(p); ");

                            String m = new String("" + (double) bytes);

                            Log.i(tag, m);

                            // Send the obtained bytes to the UI activity
                            mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                                    .sendToTarget();

                        } catch (IOException e) {
                            break;
                        }
                    }
                }
            };thread2.start();
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }



    void Plot(){
        final InputStream iStream = this.getResources().openRawResource(R.raw.simvals123);

        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(iStream));


        thread = new Thread() {
            public void run() {
                int i = 0, k = 0;





                String readLine = null;
                try {
                    // While the BufferedReader readLine is not null
                    while ((readLine = bufferedReader.readLine()) != null) {


                        point p = new point(Double.parseDouble(readLine), k);//Got new Data
                        dynamicPlot.addNewPoint(p);
                        /*for(int h=0;h<999;h++){
                            for(int j=0;j<999;j++){
                                //delay
                            }
                        }*/
                        //view.repaint();
                        i++;
                        k = k + 10;
                        if (i == 255) {

                            for(int h=0;h<9999;h++){
                                for(int j=0;j<9999;j++){
                                    //delay
                                }
                            }

                        }
                    }
                    //view.repaint();

                    // Close the InputStream and BufferedReader
                    iStream.close();
                    bufferedReader.close();

                } catch (IOException m) {
                    m.printStackTrace();
                }
            }
        };
        thread.start();

    }
    //redundant
    void Plot(boolean f){



        thread = new Thread() {
            public void run() {
                int i = 0, k = 0;


                // While the BufferedReader readLine is not null
                while (true) {


                    //point p = new point((queue.deleteByte()), k);//Got new Data
                    //dynamicPlot.addNewPoint(p);

                    //view.repaint();
                    i++;
                    k = k + 10;
                    if (i == 255) {


                        for(int h=0;h<9999;h++){
                            for(int j=0;j<9999;j++){
                                //delay
                            }
                        }

                    }
                }
                //view.repaint();
            }
        };
        thread.start();
    }






    private void Plot(double x, int y){
        int i=0;

        //point p = new point(Double.parseDouble(readLine), k);//Got new Data
        point p = new point(1256.45+i, i);
        dynamicPlot.addNewPoint(p);
        i++;
    }





























    protected void onStart() {

        super.onStart();
        view = dynamicPlot.getView(this);
        setContentView(view);
        if(FLAG==0)
            startBluetooth();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.isFinishing()) { // real stoppage
            finish();
        } else { // orientation change
            // Keep threads alive on orientation change
        }

    }
}

