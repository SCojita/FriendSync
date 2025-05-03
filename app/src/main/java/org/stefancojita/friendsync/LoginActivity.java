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

    // Declaración de variables.
    private EditText emailEditText, passwordEditText;
    private Button buttonLogin;
    private TextView textRegistraButton;
    private FirebaseAuth autenticacion;
    private TextView textOlvidasteContrasenya;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        autenticacion = FirebaseAuth.getInstance(); // Inicializamos FirebaseAuth.

        // Inicializamos las variables.
        emailEditText = findViewById(R.id.emlEditText);
        passwordEditText = findViewById(R.id.pwdEditText);
        buttonLogin = findViewById(R.id.btnLogin);
        textOlvidasteContrasenya = findViewById(R.id.txtOlvidasteContrasenya);
        textOlvidasteContrasenya.setOnClickListener(v -> mostrarDialogoRecuperarContrasena());
        textRegistraButton = (TextView) findViewById(R.id.txtRegistraButton);

        buttonLogin.setOnClickListener(v -> loginUser()); // Llamamos al método loginUser() al hacer clic en el botón de inicio de sesión.

        // Al hacer clic en el texto de registro, se inicia la actividad de registro.
        textRegistraButton.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

        // Comprobamos si el usuario ya está autenticado.
        if (autenticacion.getCurrentUser() != null) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }

    }

    // Creamos el método loginUser() para iniciar sesión.
    private void loginUser() {
        // Obtenemos el correo y la contraseña introducidos por el usuario.
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validamos el correo y la contraseña.
        if (email.isEmpty()) {
            emailEditText.setError("El correo es obligatorio"); // Mostramos un mensaje de error si el correo está vacío.
            emailEditText.requestFocus(); // Solicitamos el foco en el campo de correo.
            return;
        }

        // Validamos el formato del correo electrónico.
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Introduce un correo válido"); // Mostramos un mensaje de error si el formato del correo no es válido.
            emailEditText.requestFocus(); // Solicitamos el foco en el campo de correo.
            return;
        }

        // Validamos la contraseña.
        if (password.isEmpty()) {
            passwordEditText.setError("La contraseña es obligatoria"); // Mostramos un mensaje de error si la contraseña está vacía.
            passwordEditText.requestFocus(); // Solicitamos el foco en el campo de contraseña.
            return;
        }

        // Validamos la longitud de la contraseña.
        autenticacion.signInWithEmailAndPassword(email, password)
                // Iniciamos sesión con el correo y la contraseña introducidos.
                .addOnCompleteListener(task -> {
                    // Comprobamos si la tarea se ha completado con éxito.
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

    // Creamos un método para mostrar un diálogo de recuperación de contraseña.
    private void mostrarDialogoRecuperarContrasena() {
        EditText inputCorreo = new EditText(this); // Creamos un EditText para que el usuario introduzca su correo.
        inputCorreo.setHint("Introduce tu correo"); // Establecemos un texto de sugerencia.

        // Creamos un diálogo de alerta.
        new AlertDialog.Builder(this)
                .setTitle("Recuperar contraseña")
                .setMessage("Introduce tu correo electrónico para recibir un enlace de recuperación")
                .setView(inputCorreo)
                .setPositiveButton("Enviar", (dialog, which) -> {
                    String correo = inputCorreo.getText().toString().trim(); // Obtenemos el correo introducido por el usuario.
                    // Validamos el formato del correo electrónico.
                    if (!correo.isEmpty()) {
                        enviarCorreoRecuperacion(correo); // Llamamos al método para enviar el correo de recuperación.
                    } else {
                        Toast.makeText(this, "Introduce un correo válido", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // Creamos un método para enviar el correo de recuperación.
    private void enviarCorreoRecuperacion(String correo) {
        // Enviamos un correo de recuperación utilizando FirebaseAuth.
        FirebaseAuth.getInstance().sendPasswordResetEmail(correo)
                // Enviamos un correo de recuperación al correo introducido por el usuario.
                .addOnCompleteListener(task -> {
                    // Comprobamos si la tarea se ha completado con éxito.
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Correo de recuperación enviado", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Error al enviar el correo", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
