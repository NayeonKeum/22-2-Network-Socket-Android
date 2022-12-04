package com.example.bubblechatapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    ArrayList<chatroomModel> chatroomList;
    EditText edit_area, edit_port;
    Button btn_refresh, btn_open;

    int port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.InitializeData();

//        edit_area=(EditText) findViewById(R.id.edit_area);
//        edit_port=findViewById(R.id.edit_port);

//        btn_add=(Button) findViewById(R.id.btn_refresh);
        btn_refresh = findViewById(R.id.btn_refresh);
        btn_open = findViewById(R.id.btn_open);


//        area= String.valueOf(edit_area.getText());
//        port= String.valueOf(edit_port.getText());


        ListView listView = (ListView) findViewById(R.id.listview);
        final ListAdapter myAdapter = new ListAdapter(this, chatroomList);

//        Log.d("LIST", String.valueOf(chatroomList.get(1).getPort()));

        listView.setAdapter(myAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                port = chatroomList.get(position).getPort();
                Log.d("PORT selected", String.valueOf(port));
                listView.setSelection(Color.parseColor("#c8c8c8"));
                Toast.makeText(LoginActivity.this, port+"번 포트 선택", Toast.LENGTH_SHORT).show();
            }
        });

        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myAdapter.notifyDataSetChanged();
            }
        });


        btn_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RCMainActivity.class);
//                intent.putExtra("area", area);
                intent.putExtra("port", port);
                startActivity(intent);
            }
        });


    }

    public void InitializeData() {
        chatroomList= new ArrayList<>();

        this.chatroomList.add(new chatroomModel(9000));//, "지역1"));
        this.chatroomList.add(new chatroomModel(9001));//, "지역1"));
        this.chatroomList.add(new chatroomModel(9003));//, "지역1"));
    }
}