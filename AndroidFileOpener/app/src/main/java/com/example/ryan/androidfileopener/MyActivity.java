package com.example.ryan.androidfileopener;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.util.Log;
import android.content.Context;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import android.widget.Button;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class MyActivity extends Activity implements ConnectionListener {

    private ListView mainListView ;
    private ArrayAdapter<String> listAdapter ;

    BluetoothSocket socket;
    Boolean server = true;
    Boolean connected = false;
    private DataInputStream in = null;
    private DataOutputStream out = null;
    ArrayList<String> fileList = new ArrayList<String>();
    String[] filenames;
    EditText display;
    boolean newFiles = false;
    final MyActivity myActivity = this;
    public static ArrayAdapter<String> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.main);
            mainListView = (ListView) findViewById( R.id.mainListView );
            display = (EditText) findViewById(R.id.editString);

            // Create and populate a List of planet names.
            String[] planets = new String[] { "Music File Listing"};
            ArrayList<String> planetList = new ArrayList<String>();
            planetList.addAll( Arrays.asList(planets) );

            // Create ArrayAdapter using the planet list.
            listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow, planetList);

            // Set the ArrayAdapter as the ListView's adapter.
            mainListView.setAdapter( listAdapter );

            Button cButton= (Button) findViewById(R.id.buttonConnect);
            cButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    // Get the ViewFlipper from the layout
                    new BluetoothP2P(myActivity, myActivity).start();
                }
            });
            Button uButton= (Button) findViewById(R.id.getFiles);
            uButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if(connected) {
                        ArrayList<String> fileList = null;
                        fileList = new ArrayList<String>();
                        confirmConnection();
                        populateList();
                        updateData();
                    }
                }
            });
            mainListView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id){
                String str = mainListView.getItemAtPosition(position).toString();
                    Toast.makeText(getApplicationContext(),
                            str, Toast.LENGTH_LONG)
                            .show();
                    sendFilename(str);
                }
            });
        } catch (Exception e) {
            Log.e("ERROR", "ERROR IN CODE: " + e.toString());
            e.printStackTrace();
        }
    }
    public void confirmConnection(){
        new Thread(new Runnable() {
            public void run() {
                try {
                    out.writeUTF("hello"); // Send remote
                } catch (Exception e) {
                }
            }
        }).start();
    }
    public void sendFilename(final String str){
        new Thread(new Runnable() {
            public void run() {
                try {
                     out.writeUTF(str);
                } catch (Exception e) {
                }
            }
        }).start();
    }

    public void updateData(){
        /*try {
            Thread.sleep(2000);
        }
        catch(Exception e){
        }*/
        adapter = null;
        adapter = new CustomAdapter(myActivity, R.layout.simplerow, fileList);
        adapter.notifyDataSetChanged();
        mainListView.setAdapter(adapter);

    }
    public void populateList(){
        new Thread(new Runnable() {
            public void run() {
                while(connected) {
                    try {
                        String s = in.readUTF();
                        addToList(s);
                    } catch (Exception e) {
                    }
                }
            }
        }).start();
    }
    public void addToList(String s){
        System.out.println(s);
        fileList.add(s);
    }

    public void setSocketServer(BluetoothSocket socket, boolean server) {
        this.server = server;
        this.socket = socket;
        connected = true;

        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            connected = false;
        }

        final String message = connected ? "Connected" : "Connection failed";
        System.out.println("oh my goodness");

        this.runOnUiThread(new Runnable() {
            public void run() {
              display.setText(message);
            }
        });
    }
    public void setMessage(final String s) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                //myView.setView(s);
            }
        });
    }
}
interface ConnectionListener {
    public void setSocketServer(BluetoothSocket socket, boolean server);
    public void setMessage(final String msg);
}