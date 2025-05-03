package org.stefancojita.friendsync;

import android.os.Bundle;
import androidx.appcompat.widget.SearchView;

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
    private List<Evento> listaMisEventos;
    private List<String> listaIds;
    private List<String> listaAutores;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_eventos);

        recyclerMisEventos = findViewById(R.id.rcyEventos);
        recyclerMisEventos.setLayoutManager(new LinearLayoutManager(this));

        listaMisEventos = new ArrayList<>();
        listaIds = new ArrayList<>();
        listaAutores = new ArrayList<>();
        adapter = new EventoAdapter(listaMisEventos, listaIds, listaAutores);
        recyclerMisEventos.setAdapter(adapter);

        searchView = findViewById(R.id.srchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
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
                .addSnapshotListener((querySnapshot, error) -> {
                    listaMisEventos.clear();
                    listaIds.clear();
                    listaAutores.clear();

                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Evento evento = doc.toObject(Evento.class);
                        String id = doc.getId();
                        String creador = doc.getString("uid_usuario");
                        List<String> asistentes = (List<String>) doc.get("asistentes");

                        boolean soyCreador = creador != null && creador.equals(currentUser.getUid());
                        boolean estoyUnido = asistentes != null && asistentes.contains(currentUser.getUid());

                        if (soyCreador || estoyUnido) {
                            listaMisEventos.add(evento);
                            listaIds.add(id);

                            db.collection("users").document(creador)
                                    .get()
                                    .addOnSuccessListener(userDoc -> {
                                        String alias = userDoc.getString("alias");
                                        if (alias == null) alias = "Usuario";
                                        listaAutores.add(alias);
                                        adapter.notifyDataSetChanged();
                                    });
                        }
                    }
                });
    }

    private void filtrarEventos(String texto) {
        List<Evento> listaFiltrada = new ArrayList<>();
        List<String> listaIdsFiltrada = new ArrayList<>();
        List<String> listaAutoresFiltrada = new ArrayList<>();

        for (int i = 0; i < listaMisEventos.size(); i++) {
            Evento evento = listaMisEventos.get(i);
            String id = listaIds.get(i);
            String autor = listaAutores.get(i);

            if (evento.getTitulo().toLowerCase().contains(texto.toLowerCase()) ||
                    evento.getLugar().toLowerCase().contains(texto.toLowerCase())) {
                listaFiltrada.add(evento);
                listaIdsFiltrada.add(id);
                listaAutoresFiltrada.add(autor);
            }
        }

        adapter = new EventoAdapter(listaFiltrada, listaIdsFiltrada, listaAutoresFiltrada);
        recyclerMisEventos.setAdapter(adapter);
    }
}
