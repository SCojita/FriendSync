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
        // Obtenemos la colección de eventos y escuchamos los cambios en tiempo real.
        db.collection("eventos")
                // Filtramos los eventos por el uid del usuario actual.
                .addSnapshotListener((querySnapshot, error) -> {
                    listaMisEventos.clear(); // Limpiamos la lista de eventos.
                    listaIds.clear(); // Limpiamos la lista de IDs.
                    listaAutores.clear(); // Limpiamos la lista de autores.

                    // Recorremos los documentos de la colección en la bd.
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Evento evento = doc.toObject(Evento.class); // Convertimos el documento a un objeto Evento.
                        String id = doc.getId(); // Obtenemos el ID del documento.
                        String creador = doc.getString("uid_usuario"); // Obtenemos el UID del creador del evento.
                        List<String> asistentes = (List<String>) doc.get("asistentes"); // Obtenemos la lista de asistentes.

                        boolean soyCreador = creador != null && creador.equals(usuarioActual.getUid()); // Verificamos si el usuario actual es el creador del evento.
                        boolean estoyUnido = asistentes != null && asistentes.contains(usuarioActual.getUid()); // Verificamos si el usuario actual está en la lista de asistentes.

                        // Si el usuario es el creador o está unido al evento, lo añadimos a la lista.
                        if (soyCreador || estoyUnido) {
                            listaMisEventos.add(evento); // Añadimos el evento a la lista.
                            listaIds.add(id); // Añadimos el ID a la lista.

                            // Obtenemos el alias del creador del evento.
                            db.collection("users").document(creador)
                                    .get()
                                    // Obtenemos el documento del usuario.
                                    .addOnSuccessListener(userDoc -> {
                                        String alias = userDoc.getString("alias"); // Obtenemos el alias del usuario.
                                        // Si el alias es nulo, lo asignamos como "Usuario".
                                        if (alias == null) alias = "Usuario";
                                        listaAutores.add(alias); // Añadimos el alias a la lista.
                                        adapter.notifyDataSetChanged(); // Notificamos al adaptador que los datos han cambiado.
                                    });
                        }
                    }
                });
    }

    // Creamos un método para filtrar los eventos según el texto ingresado en el SearchView.
    private void filtrarEventos(String texto) {
        // Declaramos las listas filtradas.
        List<Evento> listaFiltrada = new ArrayList<>();
        List<String> listaIdsFiltrada = new ArrayList<>();
        List<String> listaAutoresFiltrada = new ArrayList<>();

        // Creamos un bucle para recorrer la lista de eventos.
        for (int i = 0; i < listaMisEventos.size(); i++) {
            Evento evento = listaMisEventos.get(i); // Obtenemos el evento de la lista.
            String id = listaIds.get(i); // Obtenemos el ID del evento.
            String autor = listaAutores.get(i); // Obtenemos el autor del evento.

            // Verificamos si el título o el lugar del evento contienen el texto ingresado.
            if (evento.getTitulo().toLowerCase().contains(texto.toLowerCase()) ||
                    evento.getLugar().toLowerCase().contains(texto.toLowerCase())) {
                listaFiltrada.add(evento); // Añadimos el evento a la lista filtrada.
                listaIdsFiltrada.add(id); // Añadimos el ID a la lista filtrada.
                listaAutoresFiltrada.add(autor); // Añadimos el autor a la lista filtrada.
            }
        }

        adapter = new EventoAdapter(listaFiltrada, listaIdsFiltrada, listaAutoresFiltrada); // Creamos un nuevo adaptador con la lista filtrada.
        recyclerMisEventos.setAdapter(adapter); // Asignamos el nuevo adaptador al RecyclerView.
    }
}
