package msu.edu.cse476.buglakda.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonLogin = findViewById(R.id.buttonLogin);
        Button buttonChangePassword = findViewById(R.id.buttonChange);

        buttonLogin.setOnClickListener(v -> {
            // Start LoginActivity
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });

        buttonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start ChangePasswordActivity
                startActivity(new Intent(MainActivity.this, ChangePasswordActivity.class));
            }
        });
    }
}