package com.alameen.wael.hp.chatapplication;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

public class ConfirmCode extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_code);

        final AutoCompleteTextView code = (AutoCompleteTextView) findViewById(R.id.code);
        final String phone = getIntent().getExtras().getString("phone_number");
        Button next = (Button) findViewById(R.id.next);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sms_code = "1234";
                String c = code.getText().toString();

                if (c.equals(sms_code)) {
                    Intent intent = new Intent(ConfirmCode.this, PutUserImage.class);
                    intent.putExtra("phone_number", phone);
                    startActivity(intent);

                } else {
                    Snackbar.make(view, "الكود المدخل غير مطابق", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }
}
