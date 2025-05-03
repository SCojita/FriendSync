package org.stefancojita.friendsync;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CalculadoraGastosActivity extends AppCompatActivity {

    private EditText etTotalGasto;
    private Button btnRepartir;
    private TextView tvResultadoGastos, tvContadorAsistentes;
    private FirebaseFirestore db;
    private String eventoId;
    private List<String> asistentes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculadora_gastos);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        etTotalGasto = findViewById(R.id.edtTotalGasto);
        btnRepartir = findViewById(R.id.btnRepartir);
        tvResultadoGastos = findViewById(R.id.txtResultadoGastos);
        tvContadorAsistentes = findViewById(R.id.txtContadorAsistentes);
        db = FirebaseFirestore.getInstance();

        eventoId = getIntent().getStringExtra("eventoId");

        cargarAsistentes();

        btnRepartir.setOnClickListener(v -> calcularReparto());
    }

    private void cargarAsistentes() {
        db.collection("eventos").document(eventoId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        asistentes = (List<String>) document.get("asistentes");
                        String creatorUid = document.getString("uid_usuario");

                        if (asistentes == null) asistentes = new ArrayList<>();
                        if (creatorUid != null && !asistentes.contains(creatorUid)) {
                            asistentes.add(0, creatorUid);
                        }

                        tvContadorAsistentes.setText("Total de participantes: " + asistentes.size());

                        if (asistentes.isEmpty()) {
                            Toast.makeText(this, "No hay asistentes en este evento", Toast.LENGTH_SHORT).show();
                            btnRepartir.setEnabled(false);
                        }
                    }
                });
    }

    private void calcularReparto() {
        String input = etTotalGasto.getText().toString().trim();

        if (input.isEmpty()) {
            Toast.makeText(this, "Introduce un gasto total", Toast.LENGTH_SHORT).show();
            return;
        }

        if (asistentes == null || asistentes.isEmpty()) {
            Toast.makeText(this, "No hay asistentes para repartir", Toast.LENGTH_SHORT).show();
            return;
        }

        double total;
        try {
            total = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Introduce un número válido", Toast.LENGTH_SHORT).show();
            return;
        }

        double porPersona = total / asistentes.size();

        String resultado = String.format(Locale.getDefault(),
                "Cada persona debe pagar: %.2f € (entre %d participantes)",
                porPersona, asistentes.size());

        tvResultadoGastos.setAlpha(0f);
        tvResultadoGastos.animate().alpha(1f).setDuration(500).start();

        tvResultadoGastos.setText(resultado);
    }
}
