package org.stefancojita.friendsync;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AsistenteAdapter extends RecyclerView.Adapter<AsistenteAdapter.AsistenteViewHolder> {

    // Declaración de la lista de asistentes.
    private List<Asistente> listaAsistentes;

    // Declaración de constructor.
    public AsistenteAdapter(List<Asistente> listaAsistentes) {
        this.listaAsistentes = listaAsistentes;
    }

    // Sobreescribimos el método onCreateViewHolder para inflar el layout de cada elemento de la lista.
    @NonNull
    @Override
    public AsistenteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new AsistenteViewHolder(vista);
    }

    // Sobreescribimos el método onBindViewHolder para establecer los datos en cada elemento de la lista.
    @Override
    public void onBindViewHolder(@NonNull AsistenteViewHolder holder, int position) {
        Asistente asistente = listaAsistentes.get(position); // Obtenemos el asistente en la posición actual.
        String alias = asistente.getAlias(); // Obtenemos el alias del asistente.
        String emailField = asistente.getEmail(); // Obtenemos el email del asistente.
        boolean esCreador = emailField.endsWith(" [creador]"); // Verificamos si el asistente es el creador.
        // Si el asistente es el creador, eliminamos la parte " [creador]" del email.
        if (esCreador) {
            String emailSolo = emailField.substring(0, emailField.length() - " [creador]".length()); // Obtenemos el email sin la parte " [creador]".
            holder.textView.setText(alias + " (" + emailSolo + ") [creador]"); // Establecemos el texto en el TextView del ViewHolder.
        } else {
            holder.textView.setText(alias + " (" + emailField + ")"); // Establecemos el texto en caso de que no sea el creador.
        }
    }

    // Sobreescribimos el método getItemCount para devolver el tamaño de la lista de asistentes.
    @Override
    public int getItemCount() {
        return listaAsistentes.size();
    }

    // Creamos una clase interna que representa el ViewHolder para cada elemento de la lista.
    public static class AsistenteViewHolder extends RecyclerView.ViewHolder {

        // Declaración del TextView que mostrará el alias y el email del asistente.
        TextView textView;

        // Declaración del constructor del ViewHolder que recibe la vista del elemento.
        public AsistenteViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}
