package msu.edu.cse476.buglakda.project;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ChangePasswordActivity extends AppCompatActivity {

    // This would be the key to store the password in SharedPreferences.
    private static final String PREFS_NAME = "AppPrefs";
    private static final String PREF_PASSWORD = "pref_password";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        // Load the current password, default to "password" if not set
        final String currentPassword = sharedPreferences.getString(PREF_PASSWORD, "password");

        final EditText oldPasswordEditText = findViewById(R.id.oldPassword);
        final EditText newPasswordEditText = findViewById(R.id.newPassword);
        Button changePasswordButton = findViewById(R.id.changePasswordButton);

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = oldPasswordEditText.getText().toString().trim();
                String newPassword = newPasswordEditText.getText().toString().trim();

                if(currentPassword.equals(oldPassword)) {
                    if(!newPassword.isEmpty() && !oldPassword.equals(newPassword)) {
                        // Save the new password in SharedPreferences
                        sharedPreferences.edit().putString(PREF_PASSWORD, newPassword).apply();
                        Toast.makeText(ChangePasswordActivity.this, "Password changed successfully!", Toast.LENGTH_SHORT).show();
                        finish(); // Optional: Close activity after changing the password
                    } else {
                        Toast.makeText(ChangePasswordActivity.this, "New password cannot be empty or the same as the old password!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "Old password is incorrect!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
