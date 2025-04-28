package org.stefancojita.friendsync;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CrearEventoActivity extends AppCompatActivity {

    private EditText etTitulo, etFecha, etLugar, etDescripcion;
    private Button btnGuardarEvento;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private MaterialCheckBox checkboxPublico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_evento);

        etTitulo = findViewById(R.id.etTitulo);
        etFecha = findViewById(R.id.etFecha);
        etFecha.setOnClickListener(v -> mostrarDatePicker());
        etLugar = findViewById(R.id.etLugar);
        etDescripcion = findViewById(R.id.etDescripcion);
        btnGuardarEvento = findViewById(R.id.btnGuardarEvento);
        checkboxPublico = findViewById(R.id.checkboxPublico);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        btnGuardarEvento.setOnClickListener(v -> guardarEvento());
    }

    private void mostrarDatePicker() {
        final Calendar calendario = Calendar.getInstance();
        int anio = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    String fechaSeleccionada = String.format("%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, year);
                    etFecha.setText(fechaSeleccionada);
                }, anio, mes, dia);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }


    private void guardarEvento() {
        String titulo = etTitulo.getText().toString().trim();
        String fecha = etFecha.getText().toString().trim();
        String lugar = etLugar.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        boolean esPublico = checkboxPublico.isChecked();

        if (titulo.isEmpty()) {
            etTitulo.setError("El título es obligatorio");
            etTitulo.requestFocus();
            return;
        }

        if (fecha.isEmpty()) {
            etFecha.setError("La fecha es obligatoria");
            etFecha.requestFocus();
            return;
        }

        if (lugar.isEmpty()) {
            etLugar.setError("El lugar es obligatorio");
            etLugar.requestFocus();
            return;
        }

        Map<String, Object> evento = new HashMap<>();
        evento.put("titulo", titulo);
        evento.put("fecha", fecha);
        evento.put("lugar", lugar);
        evento.put("descripcion", descripcion);
        evento.put("publico", esPublico);
        evento.put("uid_usuario", currentUser.getUid());

        db.collection("eventos")
                .add(evento)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Evento guardado con éxito", Toast.LENGTH_SHORT).show();
                    finish(); // volver a la pantalla anterior
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al guardar el evento", Toast.LENGTH_SHORT).show()
                );
    }
}
