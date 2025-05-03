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

    // Declaración de variables.
    private List<Articulo> listaArticulos;
    private Context context;

    // Declaración de constructor.
    public ArticuloAdapter(Context context, List<Articulo> listaArticulos) {
        this.context = context;
        this.listaArticulos = listaArticulos;
    }

    // Sobrescribimos el método onCreateViewHolder para inflar el layout del item.
    @NonNull
    @Override
    public ArticuloViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflamos el layout del item_articulo.xml y lo pasamos al ViewHolder.
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_articulo, parent, false);
        return new ArticuloViewHolder(vista);
    }

    // Sobrescribimos el método onBindViewHolder para asignar los datos al ViewHolder.
    @Override
    public void onBindViewHolder(@NonNull ArticuloViewHolder holder, int position) {
        Articulo articulo = listaArticulos.get(position); // Obtenemos el artículo en la posición actual.
        holder.txtTitulo.setText(articulo.getTitulo()); // Asignamos el título del artículo al TextView.
        holder.txtDescripcion.setText(articulo.getDescripcion()); // Asignamos la descripción del artículo al TextView.

        // Asignamos un listener al itemView para manejar el clic.
        holder.itemView.setOnClickListener(v -> {
            Intent intent = null; // Inicializamos el Intent como null.

            // Estructura ''switch' para determinar qué actividad abrir.
            // Dependiendo del idArticulo, creamos un Intent diferente.
            switch (articulo.getIdArticulo()) {
                // Cada case representa un artículo diferente.
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
                case 4:
                    intent = new Intent(context, ArticuloErroresActivity.class);
                    break;
            }
            // Si el Intent no es null, iniciamos la actividad correspondiente.
            if (intent != null) {
                context.startActivity(intent);
            }
        });
    }

    // Sobrescribimos el método getItemCount para devolver el tamaño de la lista de artículos.
    @Override
    public int getItemCount() {
        return listaArticulos.size();
    }

    // Creamos una clase interna estática que representa el ViewHolder.
    public static class ArticuloViewHolder extends RecyclerView.ViewHolder {

        // Declaración de variables.
        TextView txtTitulo, txtDescripcion;

        // Declaración del constructor del ViewHolder.
        public ArticuloViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitulo = itemView.findViewById(R.id.txtTituloArticulo);
            txtDescripcion = itemView.findViewById(R.id.txtDescripcionArticulo);
        }
    }
}
