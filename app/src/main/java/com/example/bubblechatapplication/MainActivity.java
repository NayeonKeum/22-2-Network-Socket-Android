package com.example.bubblechatapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String msg, msg1, lastSender="";
    private String nickName, portStr, pp_CountStr = "0";
    private int port_num = 0;


    private LinearLayout nickContainer, portContainer, chatBubblesBox, bubbleContainer;
    private EditText editNick, editPort, editMsg;
    private Button btnOK, btnEnter, btnSend;
    private ScrollView chatboxScroll;
    private TextView bubble, tv_sender, tv_guide1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_guide1 = (TextView)findViewById(R.id.textView2);

        portContainer = (LinearLayout)findViewById(R.id.linearLayout_port);
        nickContainer = (LinearLayout) findViewById(R.id.linearLayout1);
        chatboxScroll = (ScrollView) findViewById(R.id.scrollView);
        chatBubblesBox = (LinearLayout) findViewById(R.id.linearLayout2);

        editNick = (EditText)findViewById(R.id.edit_nick);
        editPort = (EditText)findViewById(R.id.edit_port);
        editMsg = (EditText)findViewById(R.id.edit_msg);
        btnSend = (Button)findViewById(R.id.btn_send);

        editMsg.setEnabled(false);
        btnSend.setEnabled(false);


        btnEnter = (Button)findViewById(R.id.btn_enter);
        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nickName = String.valueOf(editNick.getText());
                if (!nickName.equals("")) {
                    if (nickName.contains(":") || nickName.contains(" ") || nickName.length() > 4) {
                        Toast.makeText(getApplicationContext(),
                                "닉네임에는 : 문자나 공백을 포함할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        portStr = String.valueOf(editPort.getText());
                        if (!portStr.equals("")) {
                            if (portStr.contains(":") || portStr.contains(" ")) {
                                Toast.makeText(getApplicationContext(),
                                        "포트 번호에는 : 문자나 공백을 포함할 수 없습니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                new Thread() {
                                    public void run() {
                                        connet();
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!String.valueOf(editMsg.getText()).equals("")) {
                    msg1 = nickName + ":" + String.valueOf(editMsg.getText()) + "\n";
                    new Thread() {
                        public void run() {
                            sendMessage(msg1);  // 메시지 전송
                        }
                    }.start();
                    editMsg.setText("");
                }
            }
        });

    }

    private void connet() {
        try {
            port_num= Integer.valueOf(portStr);
            if (port_num != 0)
                socket = new Socket("192.168.0.23", port_num);
            System.out.println("서버 연결됨.");


            //setTitle("포트: " + portStr);

            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());

            // 닉네임 설정
            out.writeUTF(nickName);
            System.out.println("클라이언트 : 메시지 전송완료");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // 메시지 작성 및 전송 기능 활성화
                    editMsg.setEnabled(true);
                    btnSend.setEnabled(true);

                    // 닉네임 입력 및 확인 기능 비활성화 + 숨기기
                    editNick.setEnabled(false);
                    editPort.setEnabled(false);
                    btnEnter.setEnabled(false);
                    nickContainer.setVisibility(View.GONE);
                    portContainer.setVisibility(View.GONE);

                    // "이곳에 채팅내용이 표시됩니다" 가이드 텍스트 숨기기
                    tv_guide1.setVisibility(View.GONE);
                }
            });

            // bubble용
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(30,4,30,4);

            // bubbleContainer용
            LinearLayout.LayoutParams params0 = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params0.setMargins(0,35,0,0);
            params0.gravity = Gravity.END;

            // tv_sender용 (이름 표시)
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params1.setMargins(50,35,50,1);

            // notification용
            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params2.setMargins(5,40,5,30);


            // 화면에 메시지 버블 출력
            while (in != null) {

                msg = removeLastEnter(in.readUTF());
                //System.out.println("############# lastSender : " + lastSender + " ##############");
                //System.out.println("############# msg : " + msg + " ##############");

                if (isNotification(msg)) {  // 안내메시지
                    bubbleContainer = new LinearLayout(MainActivity.this);
                    bubble = new TextView(MainActivity.this);

                    bubble.setTextColor(Color.BLACK);
                    bubble.setLayoutParams(params2);

                    bubble.setTextSize(11);
                    bubble.setText(msg);

                    bubble.setBackgroundResource(R.drawable.border_round_notif);
                    bubbleContainer.setGravity(Gravity.CENTER);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bubbleContainer.addView(bubble);
                            chatBubblesBox.addView(bubbleContainer);
                        }
                    });
                    chatboxScroll.fullScroll(ScrollView.FOCUS_DOWN);

                    lastSender = "";
                    continue;
                }

                final String sender = whoIsSender(msg);
                bubbleContainer = new LinearLayout(MainActivity.this);
                bubble = new TextView(MainActivity.this);
                bubble.setMaxWidth(700);
                bubbleContainer.setOrientation(LinearLayout.VERTICAL);


                if (!sender.equals(lastSender)) {
                    if (sender.equals(nickName)) {
                        bubbleContainer.setLayoutParams(params0);
                    }
                    else {
                        tv_sender = new TextView(MainActivity.this);
                        tv_sender.setText(sender);
                        tv_sender.setTextColor(Color.WHITE);
                        tv_sender.setTextSize(12);
                        tv_sender.setTypeface(null, Typeface.BOLD);
                        tv_sender.setLayoutParams(params1);
                    }
                }


                bubble.setTextColor(Color.BLACK);
                if (sender.equals(nickName)) {   // 내가 보낸 메시지
                    bubble.setBackgroundResource(R.drawable.border_round_yellow);
                    bubbleContainer.setGravity(Gravity.END);
                } else if (sender.equals("서버")) {    // 서버가 보낸 메시지
                    bubble.setBackgroundResource(R.drawable.border_round_blue);
                    bubbleContainer.setGravity(Gravity.START);
                    bubble.setTextColor(Color.WHITE);
                } else if (sender.equals("인원수")) {
                    // 버블 추가하지 않음.
                    pp_CountStr = msg;
                    continue;
                } else if (sender.equals("♥")) {
                    bubble.setBackgroundResource(R.drawable.border_round_pink);
                    bubbleContainer.setGravity(Gravity.START);
                    bubble.setTextColor(Color.WHITE);
                }
                else {    // 상대방(채티방 참가자)이 보낸 메시지
                    bubble.setBackgroundResource(R.drawable.border_round_white);
                    bubbleContainer.setGravity(Gravity.START);
                }
                bubble.setLayoutParams(params);
                bubble.setTextSize(16);
                bubble.setText(getMsgContent(msg));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!sender.equals(lastSender) && !sender.equals(nickName)) {
                            bubbleContainer.addView(tv_sender);
                        }
                        bubbleContainer.addView(bubble);
                        chatBubblesBox.addView(bubbleContainer);
                    }
                });
                chatboxScroll.fullScroll(ScrollView.FOCUS_DOWN);

                lastSender = sender;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String msg2) {
        try {
            out.writeUTF(msg2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setNickname(String nickName) {
        this.nickName = nickName;
    }

    private Boolean isNotification(String msg) {
        /*
            채팅이 아니라 정보알림일 경우, true
            "***님이 접속하셨습니다."
            "***님이 나가셨습니다."
        */
        return !msg.contains(":");
    }

    private String whoIsSender(String msg3) {
        int indexColon = msg3.indexOf(":");  // msg에서 가장 먼저 나오는 : => 닉네임 규칙 - :를 포함하면 안됨!

        return msg3.substring(0, indexColon);
    }

    private String getMsgContent(String msg4) {
        int indexColon = msg4.indexOf(":");  // msg에서 가장 먼저 나오는 : => 닉네임 규칙 - :를 포함하면 안됨!

        return msg4.substring(indexColon+1);
    }

    private String removeLastEnter(String msg5) {
        if (msg5.charAt(msg5.length()-1) == '\n') {
            return msg5.substring(0, msg5.length()-1);
        } else return msg5;
    }
}
