package es.cm.dam2.prdm.vamosdeviaje;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ViajeAdapter extends RecyclerView.Adapter<ViajeAdapter.ViajeViewHolder> {

    private List<Viaje> listaViajes;
    private OnItemClickListener listener;
    private Context context;

    // Interface para manejar clics en ítems y botones
    public interface OnItemClickListener {
        void onItemClick(int position);
        void onEditarClick(int position);
        void onEliminarClick(int position);
    }

    public ViajeAdapter(List<Viaje> listaViajes, OnItemClickListener listener, Context context) {
        this.listaViajes = listaViajes;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public ViajeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_viaje, parent, false);
        return new ViajeViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViajeViewHolder holder, int position) {
        Viaje viaje = listaViajes.get(position);
        holder.tvNombreViaje.setText(viaje.getNombre());
        holder.tvFechas.setText("Fechas: " + viaje.getFechas());
        holder.pgViaje.setProgress(viaje.getProgreso());
        holder.tvProgreso.setText(viaje.getProgreso() + "% Completo");
        holder.ivViaje.setImageResource(viaje.getImagenResId());
    }

    @Override
    public int getItemCount() {
        return listaViajes.size();
    }

    // ViewHolder
    public static class ViajeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView ivViaje;
        public TextView tvNombreViaje;
        public TextView tvFechas;
        public ProgressBar pgViaje;
        public TextView tvProgreso;
        public ImageButton buttonEditar;
        public ImageButton btnEliminar;
        OnItemClickListener onItemClickListener;

        public ViajeViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            ivViaje = itemView.findViewById(R.id.ivViaje);
            tvNombreViaje = itemView.findViewById(R.id.tvNombreViaje);
            tvFechas = itemView.findViewById(R.id.tvFechas);
            pgViaje = itemView.findViewById(R.id.pgViaje);
            tvProgreso = itemView.findViewById(R.id.tvProgreso);
            buttonEditar = itemView.findViewById(R.id.buttonEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
            this.onItemClickListener = listener;

            // Asignar listeners
            itemView.setOnClickListener(this);
            buttonEditar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onItemClickListener.onEditarClick(position);
                        }
                    }
                }
            });
            btnEliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onItemClickListener.onEliminarClick(position);
                        }
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener.onItemClick(position);
                }
            }
        }
    }
}