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

    // Declaración de variables
    private RecyclerView recyclerMisEventos;
    private EventoAdapter adapter;
    private List<Evento> listaMisEventos;
    private List<String> listaIds;
    private List<String> listaAutores;
    private FirebaseFirestore db;
    private FirebaseUser usuarioActual;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_eventos);

        // Inicialización de las variables.
        recyclerMisEventos = findViewById(R.id.rcyEventos);
        recyclerMisEventos.setLayoutManager(new LinearLayoutManager(this));
        listaMisEventos = new ArrayList<>();
        listaIds = new ArrayList<>();
        listaAutores = new ArrayList<>();
        adapter = new EventoAdapter(listaMisEventos, listaIds, listaAutores); // Creamos el adaptador.
        recyclerMisEventos.setAdapter(adapter); // Asignamos el adaptador al RecyclerView.

        searchView = findViewById(R.id.srchView); // Inicializamos el SearchView.

        // Configuramos el SearchView para filtrar eventos.
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // Sobreescribimos los métodos del SearchView.
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filtrarEventos(newText); // Llamamos al método para filtrar eventos.
                return true;
            }
        });

        db = FirebaseFirestore.getInstance(); // Inicializamos Firestore.
        usuarioActual = FirebaseAuth.getInstance().getCurrentUser(); // Obtenemos el usuario actual.

        cargarMisEventos(); // Llamamos al método para cargar los eventos.
    }

    // Creamos un método para cargar los eventos del usuario actual.
    private void cargarMisEventos() {
        // Escuchamos los cambios en la colección "eventos".
        db.collection("eventos")
                .addSnapshotListener((querySnapshot, error) -> {
                    // Limpiamos listas y notificamos para evitar errores por reciclaje.
                    listaMisEventos.clear();
                    listaIds.clear();
                    listaAutores.clear();
                    adapter.notifyDataSetChanged();

                    // Creamos listas temporales para datos sincronizados.
                    List<Evento> eventosTemporales = new ArrayList<>();
                    List<String> idsTemporales = new ArrayList<>();
                    List<String> uidsTemporales = new ArrayList<>();

                    if (querySnapshot == null) return;

                    // Recorremos todos los eventos.
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Evento evento = doc.toObject(Evento.class);
                        String id = doc.getId();
                        String creador = doc.getString("uid_usuario");
                        List<String> asistentes = (List<String>) doc.get("asistentes");

                        boolean soyCreador = creador != null && creador.equals(usuarioActual.getUid());
                        boolean estoyUnido = asistentes != null && asistentes.contains(usuarioActual.getUid());

                        // Si es creador o está unido, guardamos datos temporalmente.
                        if (soyCreador || estoyUnido) {
                            eventosTemporales.add(evento);
                            idsTemporales.add(id);
                            uidsTemporales.add(creador);
                        }
                    }

                    // Si no hay eventos válidos, salimos.
                    if (uidsTemporales.isEmpty()) return;

                    // Inicializamos autores temporales y contador.
                    List<String> autoresTemporales = new ArrayList<>();
                    for (int i = 0; i < uidsTemporales.size(); i++) autoresTemporales.add("");
                    final int total = uidsTemporales.size();
                    final int[] contador = {0};

                    // Buscamos alias de cada UID y los colocamos en orden.
                    for (int i = 0; i < total; i++) {
                        final int index = i;
                        db.collection("users").document(uidsTemporales.get(i))
                                .get()
                                .addOnSuccessListener(userDoc -> {
                                    String alias = userDoc.getString("alias");
                                    if (alias == null) alias = "Usuario";
                                    autoresTemporales.set(index, alias);
                                    contador[0]++;

                                    // Cuando terminamos, pasamos datos reales al adaptador.
                                    if (contador[0] == total) {
                                        listaMisEventos.addAll(eventosTemporales);
                                        listaIds.addAll(idsTemporales);
                                        listaAutores.addAll(autoresTemporales);
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                    }
                });
    }

    // Creamos un método para filtrar los eventos según el texto ingresado en el SearchView.
    private void filtrarEventos(String texto) {
        // Declaramos las listas filtradas.
        List<Evento> listaFiltrada = new ArrayList<>();
        List<String> listaIdsFiltrada = new ArrayList<>();
        List<String> listaAutoresFiltrada = new ArrayList<>();

        // Nos aseguramos de que todas las listas tengan el mismo tamaño antes de filtrar.
        int total = Math.min(Math.min(listaMisEventos.size(), listaIds.size()), listaAutores.size());

        // Recorremos con seguridad todas las listas.
        for (int i = 0; i < total; i++) {
            Evento evento = listaMisEventos.get(i);
            String id = listaIds.get(i);
            String autor = listaAutores.get(i);

            // Verificamos si el título o el lugar del evento contienen el texto ingresado.
            if (evento.getTitulo().toLowerCase().contains(texto.toLowerCase()) ||
                    evento.getLugar().toLowerCase().contains(texto.toLowerCase())) {
                listaFiltrada.add(evento);
                listaIdsFiltrada.add(id);
                listaAutoresFiltrada.add(autor);
            }
        }

        // Creamos un nuevo adaptador y lo asignamos al RecyclerView.
        adapter = new EventoAdapter(listaFiltrada, listaIdsFiltrada, listaAutoresFiltrada);
        recyclerMisEventos.setAdapter(adapter);
    }

}
