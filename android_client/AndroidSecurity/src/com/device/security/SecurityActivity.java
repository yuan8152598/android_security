package com.device.security;

import java.io.OutputStream;
import java.net.Socket;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import com.device.security.TelephonyInfo;

public class SecurityActivity extends Activity {

    EditText input, show;
    OutputStream os;
    Handler handler;
    Socket s;
    private Context mContext;
    private TelephonyInfo myInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);
        show = (EditText) findViewById(R.id.TextShow);
        input = (EditText) findViewById(R.id.TextInput);
        HandlerFunction();
        mContext = this.getApplicationContext();
        Log.d("chao","TelephonyInfo in Mani Activity");
        myInfo = new TelephonyInfo(mContext);
        myInfo.GetTelephonyInfo();
        try
        {
            s = new Socket("192.168.68.176", 6666);
            new Thread(new ClientThread(s, handler)).start();
            os = s.getOutputStream();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        Button send_button =  (Button)findViewById(R.id.send_button);
        send_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                try
                {
                    os.write((input.getText().toString() + "\r\n")
                        .getBytes("utf-8"));
                    input.setText("");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    private void HandlerFunction()
    {
        handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                if (msg.what == 0x123)
                {
                    show.append("\n" + msg.obj.toString());
                }
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.security, menu);
        return true;
    }

}
