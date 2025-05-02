package org.stefancojita.friendsync;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AsistenteAdapter extends RecyclerView.Adapter<AsistenteAdapter.AsistenteViewHolder> {

    private List<Asistente> listaAsistentes;

    public AsistenteAdapter(List<Asistente> listaAsistentes) {
        this.listaAsistentes = listaAsistentes;
    }

    @NonNull
    @Override
    public AsistenteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new AsistenteViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull AsistenteViewHolder holder, int position) {
        Asistente asistente = listaAsistentes.get(position);
        String alias = asistente.getAlias();
        String emailField = asistente.getEmail();
        boolean esCreador = emailField.endsWith(" [creador]");
        if (esCreador) {
            String emailSolo = emailField.substring(0, emailField.length() - " [creador]".length());
            holder.textView.setText(alias + " (" + emailSolo + ") [creador]");
        } else {
            holder.textView.setText(alias + " (" + emailField + ")");
        }
    }

    @Override
    public int getItemCount() {
        return listaAsistentes.size();
    }

    public static class AsistenteViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public AsistenteViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}
