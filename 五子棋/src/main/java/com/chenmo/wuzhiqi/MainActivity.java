package com.chenmo.wuzhiqi;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    WuZhiQiView wzq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wzq = (WuZhiQiView) findViewById(R.id.wzq);
        wzq.setGameoverlistener(new WuZhiQiView.GameOverListener() {
            @Override
            public void restart() {
                new AlertDialog.Builder(MainActivity.this).setTitle("再来一局?").setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        wzq.restart();
                    }
                }).setNegativeButton("NO", null).show();
            }
        });
    }
}
