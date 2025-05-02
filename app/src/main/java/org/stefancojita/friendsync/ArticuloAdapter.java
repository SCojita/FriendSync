package org.stefancojita.friendsync;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ArticuloAdapter extends RecyclerView.Adapter<ArticuloAdapter.ArticuloViewHolder> {

    private List<Articulo> listaArticulos;
    private Context context;

    public ArticuloAdapter(Context context, List<Articulo> listaArticulos) {
        this.context = context;
        this.listaArticulos = listaArticulos;
    }

    @NonNull
    @Override
    public ArticuloViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_articulo, parent, false);
        return new ArticuloViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticuloViewHolder holder, int position) {
        Articulo articulo = listaArticulos.get(position);
        holder.tvTitulo.setText(articulo.getTitulo());
        holder.tvDescripcion.setText(articulo.getDescripcion());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = null;
            switch (articulo.getIdArticulo()) {
                case 0:
                    intent = new Intent(context, IntroduccionActivity.class);
                    break;
                case 1:
                    intent = new Intent(context, SobreNosotrosActivity.class);
                    break;
                case 2:
                    intent = new Intent(context, UneteActivity.class);
                    break;
                case 3:
                    intent = new Intent(context, DemoActivity.class);
                    break;
            }
            if (intent != null) {
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaArticulos.size();
    }

    public static class ArticuloViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvDescripcion;

        public ArticuloViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTituloArticulo);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcionArticulo);
        }
    }
}
