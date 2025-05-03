package org.stefancojita.friendsync;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView goToRegisterButton;
    private FirebaseAuth mAuth;
    private TextView tvOlvidasteContrasena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.emlEditText);
        passwordEditText = findViewById(R.id.pwdEditText);
        loginButton = findViewById(R.id.btnLogin);
        tvOlvidasteContrasena = findViewById(R.id.txtOlvidasteContrasenya);
        tvOlvidasteContrasena.setOnClickListener(v -> mostrarDialogoRecuperarContrasena());
        goToRegisterButton = (TextView) findViewById(R.id.txtRegistraButton);

        loginButton.setOnClickListener(v -> loginUser());
        goToRegisterButton.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }

    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

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

        if (password.isEmpty()) {
            passwordEditText.setError("La contraseña es obligatoria");
            passwordEditText.requestFocus();
            return;
        }


        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void mostrarDialogoRecuperarContrasena() {
        EditText inputCorreo = new EditText(this);
        inputCorreo.setHint("Introduce tu correo");

        new AlertDialog.Builder(this)
                .setTitle("Recuperar contraseña")
                .setMessage("Introduce tu correo electrónico para recibir un enlace de recuperación")
                .setView(inputCorreo)
                .setPositiveButton("Enviar", (dialog, which) -> {
                    String correo = inputCorreo.getText().toString().trim();
                    if (!correo.isEmpty()) {
                        enviarCorreoRecuperacion(correo);
                    } else {
                        Toast.makeText(this, "Introduce un correo válido", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void enviarCorreoRecuperacion(String correo) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(correo)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Correo de recuperación enviado", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Error al enviar el correo", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
