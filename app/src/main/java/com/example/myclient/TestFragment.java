package com.example.myclient;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TestFragment extends Fragment {
    private Button startButton;
    private EditText IPText;
    private Context mContext;
    private boolean isConnecting=false;
    private Thread mThreadClient=null;
    private Socket mSocketClient=null;
    private static BufferedReader mBufferedReaderClient=null;
    private static PrintWriter mPrintWriterClient=null;
    private static int a=1;
    private String recvMessageClient="";
    private static TextView recvText,recvText1,recvText2;
    private Switch switch_c;
    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        View view = inflater.inflate(R.layout.fragment_test, null);
        mContext=getContext();
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build()
        );
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());

        IPText= view.findViewById(R.id.IPText);
        IPText.setText("192.168.1.146:8080");
        startButton= view.findViewById(R.id.StartConnect);
        startButton.setOnClickListener(StartClickListener);

        recvText= view.findViewById(R.id.tv1);
        recvText.setMovementMethod(ScrollingMovementMethod.getInstance());

        recvText1= view.findViewById(R.id.textView3);
        recvText1.setMovementMethod(ScrollingMovementMethod.getInstance());

        recvText2= view.findViewById(R.id.textView4);
        recvText2.setMovementMethod(ScrollingMovementMethod.getInstance());


        switch_c=view.findViewById(R.id.switch_c);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            switch_c.setShowText(true);
        }
        switch_c.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    switch_c.setSwitchTextAppearance(mContext,R.style.s_true);
                    if(isConnecting&&mSocketClient!=null)
                    {
                        String msgText ="c1\n";//发送给单片机的某个命令
                        try
                        {
                            mPrintWriterClient.print(msgText);
                            mPrintWriterClient.flush();
                        }catch (Exception e) {
                            // TODO: handle exception
                            Toast.makeText(mContext, "发送异常"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }else
                    {
                        Toast.makeText(mContext, "没有连接", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    switch_c.setSwitchTextAppearance(mContext,R.style.s_false);

                    if(isConnecting&&mSocketClient!=null)
                    {
                        String msgText ="c0\n";//发送给单片机的某个命令
                        try
                        {
                            mPrintWriterClient.print(msgText);
                            mPrintWriterClient.flush();
                        }catch (Exception e) {
                            // TODO: handle exception
                            Toast.makeText(mContext, "发送异常"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }else
                    {
                        Toast.makeText(mContext, "没有连接", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        final Switch switch_t=view.findViewById(R.id.switch_t);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            switch_t.setShowText(true);
        }
        switch_t.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    switch_t.setSwitchTextAppearance(mContext,R.style.s_true);
                    if(isConnecting&&mSocketClient!=null)
                    {
                        String msgText ="t1\n";//发送给单片机的某个命令
                        try
                        {
                            mPrintWriterClient.print(msgText);
                            mPrintWriterClient.flush();
                        }catch (Exception e) {
                            // TODO: handle exception
                            Toast.makeText(mContext, "发送异常"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }else
                    {
                        Toast.makeText(mContext, "没有连接", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    switch_t.setSwitchTextAppearance(mContext,R.style.s_false);
                    if(isConnecting&&mSocketClient!=null)
                    {
                        String msgText ="t0\n";//发送给单片机的某个命令
                        try
                        {
                            mPrintWriterClient.print(msgText);
                            mPrintWriterClient.flush();
                        }catch (Exception e) {
                            // TODO: handle exception
                            Toast.makeText(mContext, "发送异常"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }else
                    {
                        Toast.makeText(mContext, "没有连接", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });



        return view;
    }

    //连接到智能衣柜
    private View.OnClickListener StartClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if(isConnecting)
            {
                isConnecting = false;
                try
                {
                    if(mSocketClient!=null)
                    {
                        mSocketClient.close();
                        mSocketClient = null;

                        mPrintWriterClient.close();
                        mPrintWriterClient = null;
                    }
                }catch (IOException e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
                mThreadClient.interrupt();

                startButton.setText("开始连接");
                IPText.setEnabled(true);
                recvText.setText("已经断开连接\n");
            }else
            {
                isConnecting=true;
                startButton.setText("停止连接");
                recvText.setText("已经连接到智能衣柜！\n");
                IPText.setEnabled(false);
                mThreadClient = new Thread(mRunnable);
                mThreadClient.start();
            }
        }
    };


    //Ïß³Ì£º¼àÌý·þÎñÆ÷·¢À´µÄÏûÏ¢
    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            String msgText = IPText.getText().toString();
            if(msgText.length()<=0)
            {
                recvMessageClient="IP和端口号不能为空！\n";//消息换行
                Message msg = new Message();
                msg.what = 1;
                mHandler.sendMessage(msg);
                return;
            }
            int start = msgText.indexOf(":");
            if((start==-1)||(start+1>=msgText.length()))
            {
                recvMessageClient ="IP地址不合法\n";
                Message msg = new Message();
                msg.what = 1;
                mHandler.sendMessage(msg);
                return;
            }
            String sIP= msgText.substring(0,start);
            String sPort = msgText.substring(start+1);
            int port = Integer.parseInt(sPort);


            try
            {
                //连接服务器
                mSocketClient = new Socket(sIP,port);
                //取得输入、输出流
                mBufferedReaderClient=new BufferedReader(new InputStreamReader(mSocketClient.getInputStream()));
                mPrintWriterClient=new PrintWriter(mSocketClient.getOutputStream(),true);
                recvMessageClient=" ";

                Message msg = new Message();
                msg.what = 1;
                mHandler.sendMessage(msg);
            }catch (Exception e) {
                recvMessageClient = "连接IP异常:" + e.toString() + e.getMessage() + "\n";//消息换行
                Message msg = new Message();
                msg.what = 1;
                mHandler.sendMessage(msg);
                return;
            }

            char[] buffer = new char[256];
            int count = 0;
            while(isConnecting)
            {
                try
                {
                    if((count = mBufferedReaderClient.read(buffer))>0)
                    {
                        recvMessageClient = getInfoBuff(buffer,count)+"\n";
                        Message msg = new Message();
                        msg.what = 1;
                        mHandler.sendMessage(msg);
                    }
                }catch (Exception e) {
                    // TODO: handle exception
                    recvMessageClient = "接收异常:" + e.getMessage() + "\n";//消息换行
                    Message msg = new Message();
                    msg.what = 1;
                    mHandler.sendMessage(msg);
                }
            }
        }
    };

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler()
    {
        @SuppressLint("SetTextI18n")
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            if(msg.what==1)
            {
                char []arrs=null;
                while (a>0){
                    recvText.append(recvMessageClient);//刷新
                    a--;
                }
                arrs=recvMessageClient.toCharArray();
                if (arrs[0]=='1'&&arrs[8]>='7'){
                    recvText1.setText("温湿度: "+arrs[5] + arrs[6] + "℃" + ' ' + arrs[8] + arrs[9] + "%   差，请除湿");
                }
                else if (arrs[0]=='1'&&arrs[8]<='6'){
                    recvText1.setText("温湿度: "+arrs[5] + arrs[6] + "℃" + ' ' + arrs[8] + arrs[9] + "%   良");
                }
                else if (arrs[0]=='2'&&arrs[6]<='3'){
                    recvText2.setText("气体浓度："+arrs[5]+arrs[6]+arrs[7]+"%   良");
                }
                else if (arrs[0]=='2'&&arrs[5]>'3'){
                    recvText2.setText("气体浓度："+arrs[5]+arrs[6]+arrs[7]+"%   差，请通风");
                }
            }
        };
    };

    private String getInfoBuff(char[] buff,int count)
    {
        char[] temp = new char[count];
        for (int i = 0; i < count; i++) {
            temp[i]=buff[i];
        }
        return new String(temp);
    }
}
