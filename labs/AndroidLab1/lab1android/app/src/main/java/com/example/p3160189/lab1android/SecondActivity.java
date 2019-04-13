package com.example.p3160189.lab1android;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        final ImageView iv = (ImageView) findViewById(R.id.a_dog_image);
        iv.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                androidClick();
            }
        });
    }

    private void androidClick(){
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText input=new EditText(this);
        alert.setView(input);
        alert.setPositiveButton("Change text", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String value=input.getText().toString().trim();
                changeText(value);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alert.show();
    }

    private void changeText(String value){
        TextView tv=(TextView) findViewById(R.id.textView);
        tv.setText(value);

        Toast.makeText(getApplicationContext(),"Text changed!",Toast.LENGTH_SHORT).show();
    }
}
