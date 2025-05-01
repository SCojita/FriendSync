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

    // private SwitchCompat switchTema;
    private MaterialButton btnCambiarContrasena, btnEliminarCuenta;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
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

        btnCambiarContrasena = findViewById(R.id.btnCambiarContrasena);
        btnCambiarContrasena.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(user.getEmail())
                        .addOnSuccessListener(unused ->
                                Toast.makeText(this, "Se ha enviado un correo para cambiar la contraseña.", Toast.LENGTH_LONG).show())
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Error al enviar el correo: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });

        btnEliminarCuenta = findViewById(R.id.btnEliminarCuenta);
        btnEliminarCuenta.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Eliminar cuenta")
                    .setMessage("¿Estás seguro de que deseas eliminar tu cuenta? Esta acción no se puede deshacer.")
                    .setPositiveButton("Sí, eliminar", (dialog, which) -> eliminarCuenta())
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

    }

    private void eliminarCuenta() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (user == null) return;

        String uid = user.getUid();

        db.collection("eventos")
                .whereEqualTo("uid_usuario", uid)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot doc : querySnapshot) {
                        doc.getReference().delete();
                    }

                    db.collection("users").document(uid)
                            .delete()
                            .addOnSuccessListener(unused -> {
                                // 3. Eliminar la cuenta del usuario en FirebaseAuth
                                user.delete()
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