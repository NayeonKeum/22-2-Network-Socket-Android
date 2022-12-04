package com.example.FireAlertApplication;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import androidx.recyclerview.widget.RecyclerView;

public class MessageViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    ArrayList<MessageModel> list;
    public static final int MESSAGE_TYPE_IN = 1;
    public static final int MESSAGE_TYPE_OUT = 2;
    public static final int MESSAGE_TYPE_NOTI = -1;

    private int doubleClickFlag = 0;
    private final long  CLICK_DELAY = 250;

    public DataOutputStream out;
    public String nickName;

    public MessageViewAdapter(Context context, ArrayList<MessageModel> list, DataOutputStream out, String nickName) { // you can pass other parameters in constructor
        this.context = context;
        this.list = list;
        this.out = out;
        this.nickName=nickName;
    }

    public interface OnItemClickEventListener {
        void onItemClick(View a_view, int a_position);
    }

    private OnItemClickEventListener mItemClickListener=null;

    public void setOnItemClickListener(OnItemClickEventListener a_listener) {
        mItemClickListener = a_listener;
    }

    private class MessageInViewHolder extends RecyclerView.ViewHolder {

        TextView messageTV, senderTV;
//        ImageView heart;
        CheckBox heart;

        MessageInViewHolder(final View itemView, final OnItemClickEventListener a_itemClickListener) {
            super(itemView);
            messageTV = itemView.findViewById(R.id.message_text);
            senderTV = itemView.findViewById(R.id.sender_text);
            heart = itemView.findViewById(R.id.heart);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View a_view) {
                    final int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        a_itemClickListener.onItemClick(a_view, position);
                    }
                }
            });
        }
        @SuppressLint("ResourceAsColor")
        void bind(int position) {
            MessageModel messageModel = list.get(position);
            messageTV.setText(getMsgContent(messageModel.msg));
//            messageTV.setAutoLinkMask(Linkify.WEB_URLS);

            senderTV.setText(messageModel.sender);

            Log.d("BIND", messageModel.msg);
            Log.d("BIND", String.valueOf(messageModel.liked));

            if (messageModel.senderType==0){
                // 서버(채팅)
                messageTV.setBackgroundResource(R.drawable.border_round_blue);
                messageTV.setTextColor(Color.parseColor("#ffffff"));
                heart.setVisibility(View.GONE);
            }else if (messageModel.senderType==1){
                // 중앙
                senderTV.setTextColor(Color.parseColor("#47A877"));
                messageTV.setBackgroundResource(R.drawable.border_round_white);
                messageTV.setTextColor(Color.parseColor("#000000"));
                heart.setVisibility(View.GONE);
            } else if (messageModel.senderType==2){
                // 지방
                senderTV.setTextColor(Color.parseColor("#D98209"));
                messageTV.setBackgroundResource(R.drawable.border_round_white);
                messageTV.setTextColor(Color.parseColor("#000000"));
                heart.setVisibility(View.GONE);
            } else if (messageModel.senderType==3){
                // 소방
                senderTV.setTextColor(Color.parseColor("#BA3D3D"));
                messageTV.setBackgroundResource(R.drawable.border_round_white);
                messageTV.setTextColor(Color.parseColor("#000000"));
                heart.setVisibility(View.GONE);
            } else if (messageModel.senderType==5){
                messageTV.setBackgroundResource(R.drawable.border_round_red);
                heart.setVisibility(View.VISIBLE);
            } else if (messageModel.senderType==6){
                // 위치
                messageTV.setAutoLinkMask(Linkify.WEB_URLS);
//                messageTV.setTextColor(Color.parseColor("#0000ff"));
                messageTV.setBackgroundResource(R.drawable.border_round_trans);
                heart.setVisibility(View.GONE);
            } else if (messageModel.senderType==-1){
                // notify
                messageTV.setBackgroundResource(R.drawable.border_round_trans);
                senderTV.setTextColor(Color.parseColor("#000000"));
            }
            if (messageModel.sender=="♥"){
                heart.setVisibility(View.GONE);
                messageTV.setBackgroundResource(R.drawable.border_round_red);
            }
            if (messageModel.msg.contains("http")){
                messageTV.setAutoLinkMask(Linkify.WEB_URLS);
            }
//            dateTV.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(messageModel.messageTime));

//            messageTV.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    doubleClickFlag++;
//                    Handler handler = new Handler();
//                    Runnable clickRunnable = new Runnable() {
//                        @Override
//                        public void run() {
//                            doubleClickFlag = 0;
//                            // todo click event
//                        }
//                    };
//                    if( doubleClickFlag == 1 ) {
//                        handler.postDelayed( clickRunnable, CLICK_DELAY);
//                    }else if( doubleClickFlag == 2 ) {
//                        doubleClickFlag = 0;
//                        // todo 더블클릭 이벤트
//                        // 서버만 더블클릭 적용 가능
//                        if (messageModel.senderType==5) {
//                            Log.d("EVENT", "doubleclicked, position"+position);
//
//                            heart.setBackgroundResource(R.drawable.heart_filled);
////                            heart.setSelected(true);
////                            messageModel.setLiked(1);
////                            list.get(position).liked=1;//setLiked(1);
////                            heart.setBackgroundResource(R.drawable.heart_filled);
//
//                            notifyItemChanged(position);
//
//                            String likeID = "[" + messageModel.sender +": "+getMsgContent(messageModel.msg) + "]";
//
//                            new Thread() {
//                                public void run() {
//                                    sendMessage(getRole(messageModel.msg)+";♥:"+ nickName + "님이 출동합니다.\n" + likeID);
//                                }
//                            }.start();
//                        }
//                    }
//                }
//            });
        }
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

    public void sendMessage(String msg2) {
        try {
            out.writeUTF(msg2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class MessageOutViewHolder extends RecyclerView.ViewHolder {
        TextView messageTV,senderTV;
        MessageOutViewHolder(final View itemView) {
            super(itemView);
            messageTV = itemView.findViewById(R.id.message_text);
            senderTV = itemView.findViewById(R.id.sender_text);
        }
        void bind(int position) {
            MessageModel messageModel = list.get(position);
            messageTV.setText(getMsgContent(messageModel.msg));
            senderTV.setText(messageModel.sender);
//            dateTV.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(messageModel.messageTime));
        }
    }

    private class MessageNotiViewHolder extends RecyclerView.ViewHolder {
        TextView messageTV,dateTV;
        MessageNotiViewHolder(final View itemView) {
            super(itemView);
            messageTV = itemView.findViewById(R.id.message_text);
//            dateTV = itemView.findViewById(R.id.date_text);
        }
        void bind(int position) {
            MessageModel messageModel = list.get(position);
            messageTV.setText(getMsgContent(messageModel.msg));
//            dateTV.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(messageModel.messageTime));
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == MESSAGE_TYPE_IN) {
            return new MessageInViewHolder(LayoutInflater.from(context).inflate(R.layout.item_text_in, parent, false), mItemClickListener);
        } else if (viewType == MESSAGE_TYPE_OUT) {
            return new MessageOutViewHolder(LayoutInflater.from(context).inflate(R.layout.item_text_out, parent, false));
        }else if (viewType == MESSAGE_TYPE_NOTI) {
            return new MessageNotiViewHolder(LayoutInflater.from(context).inflate(R.layout.item_text_noti, parent, false));
        }
        return null;
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Log.d("POSITION", String.valueOf(position));
        if (list.get(position).messageType == MESSAGE_TYPE_IN) {
            ((MessageInViewHolder) holder).bind(position);
            ((MessageInViewHolder) holder).messageTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = ((MessageInViewHolder) holder).getAdapterPosition();

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
                        handler.postDelayed( clickRunnable, CLICK_DELAY);
                    }else if( doubleClickFlag == 2 ) {
                        doubleClickFlag = 0;
                        // todo 더블클릭 이벤트
                        // 서버만 더블클릭 적용 가능
                        if (list.get(position).senderType==5) {
                            Log.d("EVENT", "doubleclicked, position"+position);

//                            ((MessageInViewHolder) holder).heart.setBackgroundResource(R.drawable.heart_filled);
                            ((MessageInViewHolder) holder).heart.setSelected(true);
                            notifyItemChanged(position);

                            //String likeID = "[" + list.get(position).sender +"| "+getMsgContent(list.get(position).msg) + "]";
                            String likeID = "::" + list.get(position).sender +":<html>"+getMsgContent(list.get(position).msg) + "</html>:::";
                            // ::서버:<html>센서가 산불을 감지하였습니<br>다.</html>:21:04:02
                            new Thread() {
                                public void run() {
                                    sendMessage(getRole(list.get(position).msg)+";♥:"+ nickName + "님이 출동합니다.\n" + likeID);
                                }
                            }.start();
                        }
                    }
                }
            });
        } else if (list.get(position).messageType == MESSAGE_TYPE_OUT){
            ((MessageOutViewHolder) holder).bind(position);
        } else if (list.get(position).messageType == MESSAGE_TYPE_NOTI){
            ((MessageNotiViewHolder) holder).bind(position);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).messageType;
    }

    private String getMsgContent(String msg4) {

        int indexColon = msg4.indexOf(":");  // msg에서 가장 먼저 나오는 : => 닉네임 규칙 - :를 포함하면 안됨!

        return msg4.substring(indexColon+1, msg4.length());
    }


}