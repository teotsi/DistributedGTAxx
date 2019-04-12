package com.example.p3160189.lab1android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Lab1Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab1);

        Button next=(Button) findViewById(R.id.enterButton);

        next.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent mySndActivity=new Intent(view.getContext(),SecondActivity.class);
                startActivityForResult(mySndActivity, 0);
            }
        });

    }
}
