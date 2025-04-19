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

    private List<Evento> listaEventos;
    private List<String> listaIds; // ← aquí guardaremos los IDs de Firestore

    public EventoAdapter(List<Evento> listaEventos, List<String> listaIds) {
        this.listaEventos = listaEventos;
        this.listaIds = listaIds;
    }

    @NonNull
    @Override
    public EventoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_evento, parent, false);
        return new EventoViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull EventoViewHolder holder, int position) {
        Evento evento = listaEventos.get(position);
        holder.tvTitulo.setText(evento.getTitulo());
        holder.tvFecha.setText("Fecha: " + evento.getFecha());
        holder.tvLugar.setText("Lugar: " + evento.getLugar());

        // Añadimos el listener para abrir DetalleEventoActivity
        String eventoId = listaIds.get(position); // ← ID real del documento

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DetalleEventoActivity.class);
            intent.putExtra("eventoId", eventoId);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaEventos.size();
    }

    public static class EventoViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvFecha, tvLugar;

        public EventoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvLugar = itemView.findViewById(R.id.tvLugar);
        }
    }
}



