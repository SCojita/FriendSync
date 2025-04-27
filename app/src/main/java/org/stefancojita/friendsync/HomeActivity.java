package org.stefancojita.friendsync;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private Button btnCrearEvento, btnVerEventos, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tvWelcome = findViewById(R.id.tvWelcome);
        btnCrearEvento = findViewById(R.id.btnCrearEvento);
        btnVerEventos = findViewById(R.id.btnVerEventos);
        btnLogout = findViewById(R.id.btnLogout);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            db.collection("users").document(user.getUid())
                    .get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            String alias = document.getString("alias");
                            String correo = user.getEmail();
                            tvWelcome.setText("¡Bienvenido, " + alias + " (" + correo + ")!");
                        }
                    });
        }

        btnCrearEvento.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CrearEventoActivity.class);
            startActivity(intent);
        });

        btnVerEventos.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ListaEventosActivity.class);
            startActivity(intent);
        });

        Button btnMisEventos = findViewById(R.id.btnMisEventos);
        btnMisEventos.setOnClickListener(v -> {
            startActivity(new Intent(this, MisEventosActivity.class));
        });

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}

