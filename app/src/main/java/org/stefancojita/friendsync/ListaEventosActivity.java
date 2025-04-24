package org.stefancojita.friendsync;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ListaEventosActivity extends AppCompatActivity {

    private RecyclerView recyclerEventos;
    private EventoAdapter adapter;
    private List<Evento> listaEventos;
    List<String> listaIds = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_eventos);

        recyclerEventos = findViewById(R.id.recyclerEventos);
        recyclerEventos.setLayoutManager(new LinearLayoutManager(this));

        listaEventos = new ArrayList<>();
        adapter = new EventoAdapter(listaEventos, listaIds);
        recyclerEventos.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        cargarEventosDesdeFirestore();
    }

    private void cargarEventosDesdeFirestore() {
        db.collection("eventos")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaEventos.clear();
                    listaIds.clear();

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    Date fechaActual = new Date();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Evento evento = doc.toObject(Evento.class);
                        String fechaStr = evento.getFecha();

                        try {
                            Date fechaEvento = sdf.parse(fechaStr);

                            if (fechaEvento != null && !fechaEvento.before(fechaActual)) {
                                listaEventos.add(evento);
                                listaIds.add(doc.getId());
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    adapter = new EventoAdapter(listaEventos, listaIds);
                    recyclerEventos.setAdapter(adapter);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al cargar eventos", Toast.LENGTH_SHORT).show()
                );
    }


}
