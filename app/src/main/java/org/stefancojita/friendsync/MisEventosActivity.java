package org.stefancojita.friendsync;

import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MisEventosActivity extends AppCompatActivity {

    private RecyclerView recyclerMisEventos;
    private EventoAdapter adapter;
    private List<Evento> listaMisEventos = new ArrayList<>();
    private List<String> listaIds = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_eventos); // Puedes usar el mismo layout

        recyclerMisEventos = findViewById(R.id.recyclerEventos);
        recyclerMisEventos.setLayoutManager(new LinearLayoutManager(this));
        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // No hacemos nada al pulsar "Enter"
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filtrarEventos(newText);
                return true;
            }
        });

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        cargarMisEventos();
    }

    private void cargarMisEventos() {
        db.collection("eventos")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaMisEventos.clear();
                    listaIds.clear();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Evento evento = doc.toObject(Evento.class);
                        String id = doc.getId();

                        String creador = doc.getString("uid_usuario");
                        List<String> asistentes = (List<String>) doc.get("asistentes");

                        boolean soyCreador = creador != null && creador.equals(currentUser.getUid());
                        boolean estoyUnido = asistentes != null && asistentes.contains(currentUser.getUid());

                        if (soyCreador || estoyUnido) {
                            listaMisEventos.add(evento);
                            listaIds.add(id);
                        }
                    }

                    adapter = new EventoAdapter(listaMisEventos, listaIds);
                    recyclerMisEventos.setAdapter(adapter);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al cargar tus eventos", Toast.LENGTH_SHORT).show()
                );
    }

    private void filtrarEventos(String texto) {
        List<Evento> listaFiltrada = new ArrayList<>();
        List<String> listaIdsFiltrada = new ArrayList<>();

        for (int i = 0; i < listaMisEventos.size(); i++) {
            Evento evento = listaMisEventos.get(i);
            String id = listaIds.get(i);

            if (evento.getTitulo().toLowerCase().contains(texto.toLowerCase()) ||
                    evento.getLugar().toLowerCase().contains(texto.toLowerCase())) {
                listaFiltrada.add(evento);
                listaIdsFiltrada.add(id);
            }
        }

        // Actualizamos el adaptador con la lista filtrada
        adapter = new EventoAdapter(listaFiltrada, listaIdsFiltrada);
        recyclerMisEventos.setAdapter(adapter);
    }

}

