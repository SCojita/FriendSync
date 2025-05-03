package org.stefancojita.friendsync;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NoticiasAdapter extends RecyclerView.Adapter<NoticiasAdapter.NoticiaViewHolder> {

    private List<Noticia> listaNoticias;

    public NoticiasAdapter(List<Noticia> listaNoticias) {
        this.listaNoticias = listaNoticias;
    }

    @NonNull
    @Override
    public NoticiaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_noticia, parent, false);
        return new NoticiaViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticiaViewHolder holder, int position) {
        Noticia noticia = listaNoticias.get(position);
        holder.tvAliasCreador.setText(noticia.getAliasCreador() + " ha creado este evento:");
        holder.tvNombreEvento.setText(noticia.getNombreEvento());
        holder.tvFechaLugar.setText(noticia.getFechaLugar());
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DetalleEventoActivity.class);
            intent.putExtra("eventoId", noticia.getEventoId());
            v.getContext().startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return listaNoticias.size();
    }

    public static class NoticiaViewHolder extends RecyclerView.ViewHolder {
        TextView tvAliasCreador, tvNombreEvento, tvFechaLugar;

        public NoticiaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAliasCreador = itemView.findViewById(R.id.txtAliasCreador);
            tvNombreEvento = itemView.findViewById(R.id.txtNombreEvento);
            tvFechaLugar = itemView.findViewById(R.id.txtFechaLugar);
        }
    }
}

