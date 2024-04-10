package msu.edu.cse476.buglakda.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



public class LoginActivity extends AppCompatActivity {

    private static final String USERNAME = "admin";
    private static final String PASSWORD = "password";
    private static final String PREFS_NAME = "LoginPrefs";
    private static final String PREF_USERNAME = "PrefUsername";
    private static final String PREF_PASSWORD = "PrefPassword";

    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;

    private EditText editTextUsername;
    private EditText editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);

        // Load the stored login info
        loadLoginInfo();

        Button buttonLogin = findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString();
            String password = editTextPassword.getText().toString();

            if (USERNAME.equals(username) && PASSWORD.equals(password)) {
                // Save login info
                saveLoginInfo(username, password);
                // Credentials match, proceed to MainActivity
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Finish LoginActivity so user can't go back
            } else {
                // Credentials don't match, show an error
                Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_LONG).show();
            }
        });

        Button buttonChangePassword = findViewById(R.id.buttonChange);
        buttonChangePassword.setOnClickListener(v -> {
            // Start ChangePasswordActivity
            startActivity(new Intent(LoginActivity.this, ChangePasswordActivity.class));
        });
    }

    private void saveLoginInfo(String username, String password) {
        loginPrefsEditor.putString(PREF_USERNAME, username);
        loginPrefsEditor.putString(PREF_PASSWORD, password);
        loginPrefsEditor.apply();
    }

    private void loadLoginInfo() {
        String username = loginPreferences.getString(PREF_USERNAME, "");
        String password = loginPreferences.getString(PREF_PASSWORD, "");
        editTextUsername.setText(username);
        editTextPassword.setText(password);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Save login info when the activity is paused
        saveLoginInfo(editTextUsername.getText().toString(), editTextPassword.getText().toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Load the login info when the activity is resumed
        loadLoginInfo();
    }
}
