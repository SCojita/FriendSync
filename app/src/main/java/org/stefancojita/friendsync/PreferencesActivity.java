package org.stefancojita.friendsync;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PreferencesActivity extends AppCompatActivity {

    // private SwitchCompat switchTema;
    private MaterialButton btnCambiarContrasena;
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


    }
}