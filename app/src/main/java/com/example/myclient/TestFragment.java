package com.example.myclient;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class TestFragment extends Fragment {
    private Button startButton;
    private EditText IPText;
    private Context mContext;
    private   boolean isConnecting=false;
    private Thread mThreadClient=null;
    private Socket mSocketClient=null;
    private static BufferedReader mBufferedReaderClient=null;
    private static PrintWriter mPrintWriterClient=null;
    private  String res="";
    private static TextView recvText,recvText1,recvText2;
    private View view;
    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub

         view = inflater.inflate(R.layout.fragment_test, null);
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
      // IPText.setText("10.10.10.11:8080");
        IPText.setText("192.168.1.127:8080");
        startButton= view.findViewById(R.id.StartConnect);
        startButton.setOnClickListener(StartClickListener);

        recvText= view.findViewById(R.id.tv1);
        recvText.setMovementMethod(ScrollingMovementMethod.getInstance());

        recvText1= view.findViewById(R.id.textView3);
        recvText1.setMovementMethod(ScrollingMovementMethod.getInstance());

        recvText2= view.findViewById(R.id.textView4);
        recvText2.setMovementMethod(ScrollingMovementMethod.getInstance());

        TempControlView tempControl = view.findViewById(R.id.temp_control);
        // 设置三格代表温度1度
        tempControl.setAngleRate(1);
        tempControl.setTemp(0, 5, 0);
        //设置旋钮是否可旋转
        tempControl.setCanRotate(true);

        tempControl.setOnTempChangeListener(new TempControlView.OnTempChangeListener() {
            @Override
            public void change(int temp) {
                switch (temp){
                    case 0:
                        send("*C");
                        break;
                    case 1:
                        send("*1");
                        break;
                    case 2:
                        send("*2");
                        break;
                    case 3:
                        send("*3");
                        break;
                    case 4:
                        send("*4");
                        break;
                    case 5:
                        send("*5");
                        break;
                }
            }
        });

        tempControl.setOnClickListener(new TempControlView.OnClickListener() {
            @Override
            public void onClick(int temp) {
                switch (temp){
                    case 0:
                        send("*C");
                        break;
                    case 1:
                        send("*1");
                        break;
                    case 2:
                        send("*2");
                        break;
                    case 3:
                        send("*3");
                        break;
                    case 4:
                        send("*4");
                        break;
                    case 5:
                        send("*5");
                        break;
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
                isConnecting=false;
                if(mSocketClient!=null)
                {
                    try{
                        mSocketClient.close();
                        mSocketClient = null;
                        if (mPrintWriterClient!=null){
                            mPrintWriterClient.close();
                            mPrintWriterClient = null;
                        }
                        mThreadClient.interrupt();
                        startButton.setText("开始连接");
                        IPText.setEnabled(true);//可以输入ip和端口号
                        recvText.setText("断开连接\n");

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            }else
            {
                mThreadClient = new Thread(mRunnable);
                mThreadClient.start();
            }
        }
    };

    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            String msgText = IPText.getText().toString();
            if(msgText.length()<=0)
            {
                Message msg = new Message();
                msg.what = 5;
                mHandler.sendMessage(msg);
                return;
            }
            int start = msgText.indexOf(":");
            if((start==-1)||(start+1>=msgText.length()))
            {
                Message msg = new Message();
                msg.what = 6;
                mHandler.sendMessage(msg);
                return;
            }
            String sIP= msgText.substring(0,start);
            String sPort = msgText.substring(start+1);
            int port = Integer.parseInt(sPort);

            try
            {
                //连接服务器
                mSocketClient = new Socket();
                SocketAddress socAddress = new InetSocketAddress(sIP, port);
                mSocketClient.connect(socAddress, 2000);//设置超时时间为2秒
                //取得输入、输出流
                mBufferedReaderClient=new BufferedReader(new InputStreamReader(mSocketClient.getInputStream()));
                mPrintWriterClient=new PrintWriter(mSocketClient.getOutputStream(),true);
                Message msg = new Message();
                msg.what = 1;
                mHandler.sendMessage(msg);

            }catch (Exception e) {
                Message msg = new Message();
                msg.what = 2;
                mHandler.sendMessage(msg);
                return;
            }
            char[] buffer = new char[256];
            int count = 0;

            while(true)
            {
                try
                {
                    if((count = mBufferedReaderClient.read(buffer))>0)
                    {
                        res = getInfoBuff(buffer,count)+"\n";
                        Message msg = new Message();
                        msg.what = 4;
                        mHandler.sendMessage(msg);
                    }
                }catch (Exception e) {
                    // TODO: handle exception
                    Message msg = new Message();
                    msg.what = 3;
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
            if(msg.what==4)
            {
                char []arrs=null;
                arrs=res.toCharArray();//接收来自服务器的字符串

                if (arrs[0]=='T'){
                    recvText1.setText("温度: "+arrs[3] + arrs[4] + "℃" + ' ' );
                }else if (arrs[0]=='R'){
                    recvText2.setText("湿度: "+arrs[3] + arrs[4] + "%" + ' ' );
                }
            }else if (msg.what==2){
                showDialog("连接失败，服务器走丢了");
                startButton.setText("开始连接");

            }else if (msg.what==1){
                showDialog("连接成功！");
                recvText.setText("已连接衣柜\n");
                IPText.setEnabled(false);//锁定ip地址和端口号
                isConnecting = true;
                startButton.setText("停止连接");
            }else if (msg.what==3){
              //  recvText.setText("连接已断开\n");
            }else if (msg.what==5){
                recvText.setText("IP和端口号不能为空\n");
            }
            else if (msg.what==6){
                recvText.setText("IP地址不合法\n");
            }
        }
    };
    private  void showDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }
    private String getInfoBuff(char[] buff,int count)
    {
        char[] temp = new char[count];
        for (int i = 0; i < count; i++) {
            temp[i]=buff[i];
        }
        return new String(temp);
    }
    private void send(String msg){
        if(isConnecting&&mSocketClient!=null)
                {
                    String msgText =msg;//发送给单片机的某个命令
                    try
                    {
                        mPrintWriterClient.print(msgText);
                        mPrintWriterClient.flush();
                    //    Toast.makeText(mContext,msg,Toast.LENGTH_SHORT).show();
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

