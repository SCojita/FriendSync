package org.stefancojita.friendsync;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    // Declaración de variables.
    private EditText emailEditText, passwordEditText;
    private Button registerButton;
    private FirebaseAuth autenticacion;
    private EditText aliasEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        autenticacion = FirebaseAuth.getInstance(); // Inicializamos FirebaseAuth

        // Inicializamos las variables.
        emailEditText = findViewById(R.id.rgterEmailEditText);
        aliasEditText = findViewById(R.id.rgterAliasEditText);
        passwordEditText = findViewById(R.id.rgterPasswordEditText);
        registerButton = findViewById(R.id.rgterButton);

        registerButton.setOnClickListener(v -> registrarUsuario()); // Registramos el usuario.
    }

    private void registrarUsuario() {
        // Declaramos las variables de registro.
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String alias = aliasEditText.getText().toString().trim();

        // Validamos los campos de registro.
        if (email.isEmpty()) {
            emailEditText.setError("El correo es obligatorio");
            emailEditText.requestFocus();
            return;
        }

        // Validamos el formato del correo electrónico.
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Introduce un correo válido");
            emailEditText.requestFocus();
            return;
        }

        // Validamos la contraseña.
        if (password.length() < 6) {
            passwordEditText.setError("Contraseña mínimo 6 caracteres");
            passwordEditText.requestFocus();
            return;
        }

        // Validamos el alias.
        if (alias.isEmpty()) {
            aliasEditText.setError("El nombre de usuario es obligatorio");
            aliasEditText.requestFocus();
            return;
        }

        // Registramos el usuario en Firebase Authentication.
        autenticacion.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    // Si el registro es exitoso, añadimos el usuario a Firestore.
                    if (task.isSuccessful()) {
                        FirebaseUser user = autenticacion.getCurrentUser(); // Obtenemos el usuario actual
                        FirebaseFirestore db = FirebaseFirestore.getInstance(); // Inicializamos Firestore

                        if (user != null) {
                            Map<String, Object> datosUsuario = new HashMap<>(); // Creamos un mapa para almacenar los datos del usuario
                            datosUsuario.put("email", user.getEmail()); // Guardamos el correo electrónico
                            datosUsuario.put("alias", alias); // Guardamos el alias

                            // Añadimos el usuario a Firestore.
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
