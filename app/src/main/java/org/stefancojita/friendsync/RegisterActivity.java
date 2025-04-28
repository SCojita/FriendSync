package org.stefancojita.friendsync;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button registerButton;
    private FirebaseAuth mAuth;
    private EditText aliasEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.registerEmailEditText);
        aliasEditText = findViewById(R.id.registerAliasEditText);
        passwordEditText = findViewById(R.id.registerPasswordEditText);
        registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String alias = aliasEditText.getText().toString().trim();

        if (email.isEmpty()) {
            emailEditText.setError("El correo es obligatorio");
            emailEditText.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Introduce un correo válido");
            emailEditText.requestFocus();
            return;
        }

        if (password.length() < 6) {
            passwordEditText.setError("Contraseña mínimo 6 caracteres");
            passwordEditText.requestFocus();
            return;
        }

        if (alias.isEmpty()) {
            aliasEditText.setError("El nombre de usuario es obligatorio");
            aliasEditText.requestFocus();
            return;
        }


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        if (user != null) {
                            Map<String, Object> datosUsuario = new HashMap<>();
                            datosUsuario.put("email", user.getEmail());
                            datosUsuario.put("alias", alias);

                            db.collection("users")
                                    .document(user.getUid())
                                    .set(datosUsuario)
                                    .addOnSuccessListener(unused -> {
                                        Log.d("Registro", "Usuario añadido a Firestore");
                                        Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
                                        finish();
                                    });
                        }
                    } else {
                        Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
