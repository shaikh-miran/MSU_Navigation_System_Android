package msu.edu.cse476.buglakda.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private static final String USERNAME = "admin";
    private static final String PASSWORD = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // This is now your updated layout file

        EditText editTextUsername = findViewById(R.id.editTextUsername);
        EditText editTextPassword = findViewById(R.id.editTextPassword);
        Button buttonLogin = findViewById(R.id.buttonLogin);

        buttonLogin.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString();
            String password = editTextPassword.getText().toString();

            if (USERNAME.equals(username) && PASSWORD.equals(password)) {
                // Credentials match, proceed to MainActivity
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Finish LoginActivity so user can't go back
            } else {
                // Credentials don't match, show an error
                Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_LONG).show();
            }
        });

        // Assuming you want to keep the change password button functionality
        Button buttonChangePassword = findViewById(R.id.buttonChange);
        buttonChangePassword.setOnClickListener(v -> {
            // Start ChangePasswordActivity
            startActivity(new Intent(LoginActivity.this, ChangePasswordActivity.class));
        });
    }
}
