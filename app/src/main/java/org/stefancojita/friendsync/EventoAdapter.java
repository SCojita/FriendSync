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
    private List<String> listaIds;
    private List<String> listaAutores;

    public EventoAdapter(List<Evento> listaEventos, List<String> listaIds, List<String> listaAutores) {
        this.listaEventos = listaEventos;
        this.listaIds = listaIds;
        this.listaAutores = listaAutores;
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

        String autor = listaAutores.get(position);
        holder.tvAutor.setText("Creado por: " + autor);

        String eventoId = listaIds.get(position);

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
        TextView tvTitulo, tvFecha, tvLugar, tvAutor;

        public EventoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvAutor = itemView.findViewById(R.id.tvAutor);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvLugar = itemView.findViewById(R.id.tvLugar);
        }
    }
}
