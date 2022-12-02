package com.example.bubblechatapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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


    private LinearLayout nickContainer, portContainer, chatBubblesBox, bubbleContainer, sendContainer, chatReceive, header, header2;
    private EditText editNick, editPort, editMsg;
    private Button btnOK, btnEnter, btnSend, btnRole1, btnRole2, btnRole3;
    private ImageView btnHeart, heart;
    private ScrollView chatboxScroll;
    private TextView bubble, tv_sender, tv_guide1;

    private int doubleClickFlag = 0;
    private final long  CLICK_DELAY = 250;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_guide1 = (TextView)findViewById(R.id.textView2);

        portContainer = (LinearLayout)findViewById(R.id.linearLayout_port);
        nickContainer = (LinearLayout) findViewById(R.id.linearLayout1);
        chatboxScroll = (ScrollView) findViewById(R.id.scrollView);
        chatBubblesBox = (LinearLayout) findViewById(R.id.linearLayout2);

        sendContainer = (LinearLayout)findViewById(R.id.linearLayout3);
        sendContainer.setVisibility(View.INVISIBLE);

        header = (LinearLayout)findViewById(R.id.linearLayout4);
        header2 = (LinearLayout)findViewById(R.id.linearLayout5);


        editNick = (EditText)findViewById(R.id.edit_nick);
        editPort = (EditText)findViewById(R.id.edit_port);
        editMsg = (EditText)findViewById(R.id.edit_msg);
        btnSend = (Button)findViewById(R.id.btn_send);

        btnHeart = (ImageView)findViewById(R.id.heart);
        btnRole1=(Button)findViewById(R.id.btn_role_1);
        btnRole2=(Button)findViewById(R.id.btn_role_2);
        btnRole3=(Button)findViewById(R.id.btn_role_3);

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
                                        connect();
                                    }
                                }.start();
                            }
                        }
                    }
                }
            }
        });

        /* 역할 구분 */
        final int[] roleNum = {0};

        btnRole1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                roleNum[0] =1;
                Toast.makeText(MainActivity.this, "[중앙관리본부]를 선택하셨습니다.", Toast.LENGTH_SHORT).show();
//                Log.d("Role", String.valueOf(roleNum[0]));
            }
        });
        btnRole2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                roleNum[0] = 2;
                Toast.makeText(MainActivity.this, "[지역소방본부]를 선택하셨습니다.", Toast.LENGTH_SHORT).show();
            }
        });
        btnRole3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                roleNum[0] = 3;
                Toast.makeText(MainActivity.this, "[소방대원]을 선택하셨습니다.", Toast.LENGTH_SHORT).show();
            }
        });



        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!String.valueOf(editMsg.getText()).equals("")) {
                    msg1 = String.valueOf(roleNum[0]) + ";" + nickName + ":" + String.valueOf(editMsg.getText()) + "\n";
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

    @SuppressLint("ResourceAsColor")
    private void connect() {
        try {
            port_num= Integer.valueOf(portStr);
            if (port_num != 0)
                socket = new Socket("10.101.13.24", port_num);//String.valueOf(R.string.host), port_num);
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
                    btnRole1.setEnabled(false);
                    btnRole2.setEnabled(false);
                    btnRole3.setEnabled(false);
                    nickContainer.setVisibility(View.GONE);
                    portContainer.setVisibility(View.GONE);

                    // "이곳에 채팅내용이 표시됩니다" 가이드 텍스트 숨기기
                    tv_guide1.setVisibility(View.GONE);
                    btnHeart.setVisibility(View.GONE);
                    header.setVisibility(View.GONE);
                    header2.setVisibility(View.GONE);

                    // 전송 레이아웃 보이게
                    sendContainer.setVisibility(View.VISIBLE);

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

                Log.d("MESSAGE", msg);

                if (isNotification(msg)) {  // 안내메시지
                    bubbleContainer =  new LinearLayout(MainActivity.this);
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
                final int role = Integer.parseInt(getRole(msg));
                bubbleContainer = new LinearLayout(MainActivity.this);

                bubble = new TextView(MainActivity.this);
                heart = new ImageView(MainActivity.this);
                heart.setImageResource(R.drawable.heart);
                heart.setMaxWidth(15);
                heart.setMaxHeight(15);
                chatReceive = new LinearLayout(MainActivity.this);

                bubble.setMaxWidth(700);
                bubbleContainer.setOrientation(LinearLayout.VERTICAL);

                chatReceive.setOrientation(LinearLayout.HORIZONTAL);
                chatReceive.setGravity(Gravity.CENTER_VERTICAL);

                if (!sender.equals(lastSender)) {
                    if (sender.equals(nickName)) {
                        bubbleContainer.setLayoutParams(params0);
                    }
                    else {
                        tv_sender = new TextView(MainActivity.this);
                        tv_sender.setText(sender);

                        tv_sender.setTextSize(12);
                        tv_sender.setTypeface(null, Typeface.BOLD);
                        tv_sender.setLayoutParams(params1);
                        if(role==1){
                            tv_sender.setTextColor(R.color.center_green);
                        }else if (role==2){
                            tv_sender.setTextColor(R.color.local_org);
                        }else if (role==3){
                            tv_sender.setTextColor(R.color.ff_red);
                        }
                        if (role==6){
                            bubble.setAutoLinkMask(Linkify.WEB_URLS);
                        }
                    }
                }

                bubble.setTextColor(Color.BLACK);
                if (sender.equals(nickName)) {   // 내가 보낸 메시지
                    bubble.setBackgroundResource(R.drawable.border_round_yellow);
                    bubbleContainer.setGravity(Gravity.END);
                    heart.setVisibility(View.INVISIBLE);
                } else if (sender.equals("위치")) {    // 위치가 보낸 메시지
                    bubble.setBackgroundResource(R.drawable.border_round_trans);
                    bubbleContainer.setGravity(Gravity.START);
                    bubble.setTextColor(Color.BLUE);
                }
                else if (sender.equals("서버")) {    // 서버가 보낸 메시지
                    bubble.setBackgroundResource(R.drawable.border_round_blue);
                    bubbleContainer.setGravity(Gravity.START);
                    bubble.setTextColor(Color.WHITE);
                } else if (sender.equals("인원수")) {
                    // 버블 추가하지 않음.
                    pp_CountStr = msg;
                    heart.setVisibility(View.INVISIBLE);
                    continue;
                } else if (sender.equals("♥")) {
                    bubble.setBackgroundResource(R.drawable.border_round_red);
                    bubbleContainer.setGravity(Gravity.START);
                    bubble.setTextColor(Color.WHITE);
                    heart.setVisibility(View.INVISIBLE);
                }
                else {    // 상대방(채팅방 참가자)이 보낸 메시지
                    Log.d("SENDER",sender );
                    bubble.setBackgroundResource(R.drawable.border_round_white);
                    bubbleContainer.setGravity(Gravity.START);
                    heart.setVisibility(View.INVISIBLE);
                }
                bubble.setLayoutParams(params);
                bubble.setTextSize(16);
                bubble.setText(getMsgContent(msg));

                /* 좋아요 */
                bubble.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doubleClickFlag++;
                        Handler handler = new Handler();
                        Runnable clickRunnable = new Runnable() {
                            @Override
                            public void run() {
                                doubleClickFlag = 0;
                                // todo click event
                            }
                        };
                        if( doubleClickFlag == 1 ) {
                            handler.postDelayed( clickRunnable, CLICK_DELAY );
                        }else if( doubleClickFlag == 2 ) {
                            doubleClickFlag = 0;
                            // todo 더블클릭 이벤트
                            // 서버만 더블클릭 적용 가능
                            if (sender.equals("서버")) {
                                heart.setBackgroundResource(R.drawable.heart_filled);
                                String likeID = "[" + sender +": "+getMsgContent(msg) + "]"; // + sender +

                                new Thread() {
                                    public void run() {
                                        sendMessage(String.valueOf(role)+";♥:"+ nickName + "님이 출동합니다.\n" + likeID);
                                    }
                                }.start();
                            }
                        }
                    }
                });

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!sender.equals(lastSender) && !sender.equals(nickName)) {
                            bubbleContainer.addView(tv_sender);
                        }
//                        bubbleContainer.addView(bubble);
//                        bubbleContainer.addView(heart);
                        chatReceive.addView(bubble);

//                        if (!sender.equals(nickName) && !sender.equals("인원수") && !sender.equals("서버") && !sender.equals("♥")) {
//                            chatReceive.addView(heart);
//                        }
                        if (sender.equals("서버")) {
                            chatReceive.addView(heart);
                        }
                        bubbleContainer.addView(chatReceive);
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
    private String getRole(String msg0) {
        int indexColon = msg0.indexOf(";");  // msg에서 가장 먼저 나오는 : => 닉네임 규칙 - :를 포함하면 안됨!

        return msg0.substring(0, indexColon);
    }

    private String whoIsSender(String msg3) {
        int indexColon_role = msg3.indexOf(";");
        int indexColon = msg3.indexOf(":");  // msg에서 가장 먼저 나오는 : => 닉네임 규칙 - :를 포함하면 안됨!

        return msg3.substring(indexColon_role+1, indexColon);
    }

    private String getMsgContent(String msg4) {

        int indexColon = msg4.indexOf(":");  // msg에서 가장 먼저 나오는 : => 닉네임 규칙 - :를 포함하면 안됨!

        return msg4.substring(indexColon+1, msg4.length());
    }

    private String removeLastEnter(String msg5) {
        if (msg5.charAt(msg5.length()-1) == '\n') {
            return msg5.substring(0, msg5.length()-1);
        } else return msg5;
    }
}
