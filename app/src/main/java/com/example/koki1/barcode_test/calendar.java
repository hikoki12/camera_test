package com.example.koki1.barcode_test;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

public class calendar extends AppCompatActivity {

    public int year,month,day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
    }

    public void pick_finished(View view){
        DatePicker finish = (DatePicker) findViewById(R.id.datePicker);
        year = finish.getYear();
        month = finish.getMonth();
        day = finish.getDayOfMonth();

        Intent intent = new Intent();
        intent.putExtra("year",year);
        intent.putExtra("month",month);
        intent.putExtra("day",day);
        setResult(RESULT_OK,intent);
        finish();

    }
}
