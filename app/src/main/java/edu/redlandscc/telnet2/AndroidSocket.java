package edu.redlandscc.telnet2;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.concurrent.TimeUnit;

import static edu.redlandscc.telnet2.R.*;
import static edu.redlandscc.telnet2.R.id.*;


public class AndroidSocket extends Activity implements OnClickListener {
    TextView text;
    EditText edit1;
    EditText edit2;
    EditText edit3;
    Button edit4;
    Editable server, username, password, command;
    private String USER = null;
    private String PASS = null;
    private String CMD = null;
    private String host = null;
    private int port = 24;
    private TelnetClient telnet = new TelnetClient();
    private InputStream in;
    private PrintStream out;
    StringBuffer sb;
    Handler mHandler = new Handler();
    int len;


@Override
public void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
setContentView(layout.activity_main);
   if (android.os.Build.VERSION.SDK_INT > 9) {
      StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
      StrictMode.setThreadPolicy(policy);
   }
text = (TextView)findViewById(id.text);
edit1 = (EditText)findViewById(id.edit1);
edit2 = (EditText)findViewById(id.edit2);
edit3 = (EditText)findViewById(R.id.edit3);
edit4 = (Button)findViewById(R.id.buttonvc); //(EditText)findViewById(R.id.edit4);
server = edit1.getEditableText();
username = edit2.getEditableText();
password = edit3.getEditableText();
command = edit4.getEditableText();
Button buttonvc = (Button)findViewById(id.buttonvc);
buttonvc.setOnClickListener(this);
text.setText("Android Socket" + "\n");
}

    @Override
    public void onClick(View arg0) {
// TODO Auto-generated method stub
        text.setText("Android Socket" + "\n");
        try {
            telnet.connect(server.toString(), 24);
            in = telnet.getInputStream();
            out = new PrintStream(telnet.getOutputStream());
            telnet.setKeepAlive(true);
            Thread mThread = new Thread(new Runnable() {
                @Override
                public void run() {
// TODO Auto-generated method stub
                    try {
                        sb = new StringBuffer();
                        while (true)
                        {
                            len = in.read();
                            String s = Character.toString((char)len);
                            sb.append( s );
                            AndroidSocket.this.mHandler.post(new Runnable(){
                                @Override
                                public void run() {
// TODO Auto-generated method stub
                                    AndroidSocket.this.text.getText();
                                    AndroidSocket.this.text.append( sb.toString() );
                                }
                            });
                            System.out.println( sb );
                            mylogin();
                            mypass();
                            mycommand();
                        }
                    } catch (IOException e) {
// TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            });
            mThread.start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void mycommand() throws IOException {
// TODO Auto-generated method stub
        if (sb.toString().endsWith("SNMP Enabled:        True")) {
            out.println(command.toString() + "\r\n");
            out.flush();
            out.println("exit\r\n");
            out.flush();
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }
            disconnect();
        } else
        if (sb.toString().endsWith("# ")) {
            out.println(command.toString() + "\r\n");
            out.flush();
            out.println("exit\r\n");
            out.flush();
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }
            disconnect();
        }
    }
    private void mypass() {
// TODO Auto-generated method stub
        if (sb.toString().endsWith("Password: ")) {
            out.println(password.toString() + "\r\n");
            out.flush();
        } else
        if (sb.toString().endsWith("password: ")) {
            out.println(password.toString() + "\r\n");
            out.flush();
        }
    }
    private void mylogin() {
// TODO Auto-generated method stub
        if (sb.toString().endsWith("login: ")) {
            out.println(username.toString() + "\r");
            out.flush();
        }
    }
    public void disconnect() {
        try {
            in.close();
            out.close();
            telnet.disconnect();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

