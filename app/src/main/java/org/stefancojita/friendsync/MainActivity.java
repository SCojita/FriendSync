package org.stefancojita.friendsync;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseAuth autenticacion = FirebaseAuth.getInstance(); // Inicializamos Firebase Authentication


        // Si hay un usuario autenticado, lo redirigimos a la pantalla de inicio.
        // Si no, lo redirigimos a la pantalla de inicio de sesión.
        if (autenticacion.getCurrentUser() != null) {
            startActivity(new Intent(this, HomeActivity.class));
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }

        // Si la versión de Android es 12 o superior, solicitamos el permiso de notificaciones
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        finish();
    }

    // Sobreescribimos el método onRequestPermissionsResult para manejar la respuesta del usuario
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Si el usuario concede el permiso de notificaciones, mostramos un mensaje
        if (requestCode == 1) {
            // Si el usuario concede el permiso de notificaciones, mostramos un mensaje
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso de notificaciones concedido", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permiso de notificaciones denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
