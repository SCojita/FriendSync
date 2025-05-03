package org.stefancojita.friendsync;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PreferencesActivity extends AppCompatActivity {

    // Declaración de variables.
    // private SwitchCompat switchTema;
    private MaterialButton btnCambiarContrasenya, btnEliminarCuenta;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        // Configuración de la barra de estado y navegación para que no se superpongan con el contenido.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setContentView(R.layout.activity_preferences);

//        switchTema = findViewById(R.id.switchTema);
        pref = getSharedPreferences("preferencias", MODE_PRIVATE);


//        boolean temaOscuro = sharedPreferences.getBoolean("temaOscuro", false);
//        switchTema.setChecked(temaOscuro);
//
//        AppCompatDelegate.setDefaultNightMode(
//                temaOscuro ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
//
//        switchTema.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putBoolean("temaOscuro", isChecked);
//            editor.apply();
//
//            AppCompatDelegate.setDefaultNightMode(
//                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
//        });

        btnCambiarContrasenya = findViewById(R.id.btnCambiarContrasena);

        // Establecemos el listener para el botón de cambiar contraseña.
        btnCambiarContrasenya.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // Obtenemos el usuario actual de FirebaseAuth
            // Si el usuario no es nulo, enviamos un correo para restablecer la contraseña.
            if (user != null) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(user.getEmail()) // Enviamos el correo para restablecer la contraseña
                        // Si el correo se envía correctamente, mostramos un mensaje de éxito.
                        .addOnSuccessListener(unused ->
                                Toast.makeText(this, "Se ha enviado un correo para cambiar la contraseña.", Toast.LENGTH_LONG).show())
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Error al enviar el correo: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });

        btnEliminarCuenta = findViewById(R.id.btnEliminarCuenta);
        // Establecemos el listener para el botón de eliminar cuenta.
        btnEliminarCuenta.setOnClickListener(v -> {
            // Mostramos un diálogo de confirmación antes de eliminar la cuenta.
            new AlertDialog.Builder(this)
                    .setTitle("Eliminar cuenta")
                    .setMessage("¿Estás seguro de que deseas eliminar tu cuenta? Esta acción no se puede deshacer.")
                    .setPositiveButton("Sí, eliminar", (dialog, which) -> eliminarCuenta())
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

    }

    // Creamos un método para eliminar la cuenta del usuario.
    private void eliminarCuenta() {
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser(); // Obtenemos el usuario actual de FirebaseAuth
        FirebaseFirestore db = FirebaseFirestore.getInstance(); // Obtenemos la instancia de Firestore

        if (usuario == null) return; // Si el usuario es nulo, no hacemos nada.

        String uid = usuario.getUid(); // Obtenemos el UID del usuario actual

        db.collection("eventos")
                .whereEqualTo("uid_usuario", uid)
                .get()
                // Obtenemos todos los eventos del usuario actual.
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot doc : querySnapshot) {
                        doc.getReference().delete();
                    }

                    db.collection("users").document(uid)
                            .delete()
                            .addOnSuccessListener(unused -> {
                                // Si se eliminan los datos del usuario, eliminamos la cuenta de FirebaseAuth.
                                usuario.delete()
                                        .addOnSuccessListener(unused2 -> {
                                            Toast.makeText(this, "Cuenta eliminada correctamente", Toast.LENGTH_SHORT).show();
                                            FirebaseAuth.getInstance().signOut();
                                            Intent intent = new Intent(this, LoginActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        })
                                        .addOnFailureListener(e ->
                                                Toast.makeText(this, "Error al eliminar cuenta: " + e.getMessage(), Toast.LENGTH_LONG).show()
                                        );
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Error al eliminar datos del usuario: " + e.getMessage(), Toast.LENGTH_LONG).show()
                            );
                });
    }


}