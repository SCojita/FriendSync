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

    // Declaración de variables.
    private EditText editTotalGasto;
    private Button btnRepartir;
    private TextView textResultadoGastos, textContadorAsistentes;
    private FirebaseFirestore db;
    private String evento_id;
    private List<String> asistentes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculadora_gastos);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); // Creamos una animación de transición.

        // Inicialización de variables.
        editTotalGasto = findViewById(R.id.edtTotalGasto);
        btnRepartir = findViewById(R.id.btnRepartir);
        textResultadoGastos = findViewById(R.id.txtResultadoGastos);
        textContadorAsistentes = findViewById(R.id.txtContadorAsistentes);

        db = FirebaseFirestore.getInstance(); // Inicializamos la base de datos de Firebase.

        evento_id = getIntent().getStringExtra("eventoId"); // Obtenemos el ID del evento de la actividad anterior.

        cargarAsistentes(); // Cargamos la lista de asistentes.

        btnRepartir.setOnClickListener(v -> calcularReparto()); // Configuramos el botón para calcular el reparto de gastos.
    }

    // Creamos un método para cargar la lista de asistentes desde Firestore.
    private void cargarAsistentes() {
        // Comprobamos que el ID del evento no sea nulo.
        db.collection("eventos").document(evento_id)
                .get()// Obtenemos el documento del evento.
                // Añadimos un listener para comprobar si se ha obtenido el documento correctamente.
                .addOnSuccessListener(document -> {
                    // Comprobamos si el documento existe.
                    if (document.exists()) {
                        asistentes = (List<String>) document.get("asistentes"); // Obtenemos la lista de asistentes.
                        String creatorUid = document.getString("uid_usuario"); // Obtenemos el UID del creador del evento.

                        // Comprobamos si la lista de asistentes es nula o vacía.
                        if (asistentes == null) asistentes = new ArrayList<>();

                        // Comprobamos si el UID del creador no es nulo y no está en la lista de asistentes.
                        if (creatorUid != null && !asistentes.contains(creatorUid)) {
                            asistentes.add(0, creatorUid); // Añadimos el UID del creador al principio de la lista.
                        }

                        textContadorAsistentes.setText("Total de participantes: " + asistentes.size()); // Mostramos el número de asistentes.

                        // Comprobamos si la lista de asistentes está vacía.
                        if (asistentes.isEmpty()) {
                            Toast.makeText(this, "No hay asistentes en este evento", Toast.LENGTH_SHORT).show(); // Mostramos un mensaje de error.
                            btnRepartir.setEnabled(false); // Desactivamos el botón de repartir gastos.
                        }
                    }
                });
    }

    // Creamos un método para calcular el reparto de gastos.
    private void calcularReparto() {
        String input = editTotalGasto.getText().toString().trim(); // Obtenemos el texto del EditText y lo eliminamos de espacios en blanco.

        // Comprobamos si el EditText está vacío.
        if (input.isEmpty()) {
            Toast.makeText(this, "Introduce un gasto total", Toast.LENGTH_SHORT).show(); // Mostramos un mensaje de error.
            return;
        }

        // Comprobamos si la lista de asistentes es nula o vacía.
        if (asistentes == null || asistentes.isEmpty()) {
            Toast.makeText(this, "No hay asistentes para repartir", Toast.LENGTH_SHORT).show(); // Mostramos un mensaje de error.
            return;
        }

        double total; // Declaramos la variable que contiene el total.

        // Bloque 'try-catch'.
        // Intentará convertir el texto del EditText a un número decimal.
        // Si no se puede convertir, mostrará un mensaje de error.
        try {
            total = Double.parseDouble(input); // Aquí convertimos el texto a un número decimal.
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Introduce un número válido", Toast.LENGTH_SHORT).show();
            return;
        }

        double porPersona = total / asistentes.size(); // Calculamos el gasto por persona en una nueva variable.

        // Comprobamos si el gasto por persona es menor que 0.
        String resultado = String.format(Locale.getDefault(),
                "Cada persona debe pagar: %.2f € (entre %d participantes)",
                porPersona, asistentes.size());

        textResultadoGastos.setAlpha(0f); // Hacemos que el TextView sea invisible.
        textResultadoGastos.animate().alpha(1f).setDuration(500).start(); // Animamos el TextView para que aparezca.

        textResultadoGastos.setText(resultado); // Mostramos el resultado en el TextView.
    }
}
