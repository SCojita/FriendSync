package org.stefancojita.friendsync;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EventoAdapter extends RecyclerView.Adapter<EventoAdapter.EventoViewHolder> {

    // Declaración de variables.
    private List<Evento> listaEventos;
    private List<String> listaIds;
    private List<String> listaAutores;

    public EventoAdapter(List<Evento> listaEventos, List<String> listaIds, List<String> listaAutores) {
        this.listaEventos = listaEventos;
        this.listaIds = listaIds;
        this.listaAutores = listaAutores;
    }

    // Sobreescribimos el método onCreateViewHolder para inflar el layout del item.
    @NonNull
    @Override
    public EventoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_evento, parent, false);
        return new EventoViewHolder(vista);
    }

    // Sobreescribimos el método onBindViewHolder para asignar los datos a las vistas.
    @Override
    public void onBindViewHolder(@NonNull EventoViewHolder holder, int position) {
        Evento evento = listaEventos.get(position); // Obtenemos el evento correspondiente a la posición.
        holder.textTitulo.setText(evento.getTitulo()); // Asignamos el título del evento.

        String fecha = evento.getFecha(); // Obtenemos la fecha del evento.
        String hora = evento.getHora(); // Obtenemos la hora del evento.

        // Formateamos la fecha y hora para mostrarla en el TextView con ternarios.
        String fechaHora = (hora != null && !hora.isEmpty())
                ? "Fecha: " + fecha + " (" + hora + ")"
                : "Fecha: " + fecha;

        holder.textFecha.setText(fechaHora); // Asignamos la fecha y hora al TextView.

        holder.textLugar.setText("Lugar: " + evento.getLugar()); // Asignamos el lugar del evento.

        String autor = listaAutores.get(position); // Obtenemos el autor del evento.
        holder.textAutor.setText("Creado por: " + autor); // Asignamos el autor al TextView.

        String eventoId = listaIds.get(position); // Obtenemos el ID del evento.

        // Configuramos el evento de clic para abrir la actividad DetalleEventoActivity.
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DetalleEventoActivity.class);
            intent.putExtra("eventoId", eventoId);
            v.getContext().startActivity(intent);
        });
    }

    // Sobreescribimos el método getItemCount para devolver la cantidad de eventos.
    @Override
    public int getItemCount() {
        return listaEventos.size();
    }

    // Creamos una clase interna para el ViewHolder.
    public static class EventoViewHolder extends RecyclerView.ViewHolder {
        TextView textTitulo, textFecha, textLugar, textAutor;

        public EventoViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitulo = itemView.findViewById(R.id.txtTitulo);
            textAutor = itemView.findViewById(R.id.txtAutor);
            textFecha = itemView.findViewById(R.id.txtFecha);
            textLugar = itemView.findViewById(R.id.txtLugar);
        }
    }
}
