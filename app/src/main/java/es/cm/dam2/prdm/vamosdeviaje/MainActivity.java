package es.cm.dam2.prdm.vamosdeviaje;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ViajeAdapter.OnItemClickListener {

    // UI Components
    private RecyclerView recyclerView;
    private ViajeAdapter viajeAdapter;
    private FloatingActionButton fabAgregarViaje;

    // Data Variables
    private List<Viaje> listaViajes;

    // Request Codes
    private static final int REQUEST_ADD_TRIP = 1;
    private static final int REQUEST_TRIP_DETAILS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar componentes y configurar la interfaz
        initToolbar();
        initRecyclerView();
        cargarViajesEjemplo();
        setupAdapter();
        setupFloatingActionButton();
    }

    /**
     * Configura la Toolbar con el título de la aplicación.
     */
    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Planificador de Viajes");
        }
    }

    /**
     * Inicializa el RecyclerView y sus propiedades básicas.
     */
    private void initRecyclerView() {
        recyclerView = findViewById(R.id.rvViajes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * Carga viajes de ejemplo en la lista de viajes.
     */
    private void cargarViajesEjemplo() {
        listaViajes = new ArrayList<>();
        listaViajes.add(new Viaje("Vacaciones en París", "París", "01/06/2024 - 10/06/2024", 70, R.drawable.ic_travel));
        listaViajes.add(new Viaje("Escapada a la Playa", "Cancún", "15/07/2024 - 20/07/2024", 40, R.drawable.ic_travel));
        listaViajes.add(new Viaje("Aventura en la Montaña", "Andes", "05/08/2024 - 12/08/2024", 55, R.drawable.ic_travel));
    }

    /**
     * Configura el adaptador del RecyclerView y lo asigna.
     */
    private void setupAdapter() {
        viajeAdapter = new ViajeAdapter(listaViajes, this, this);
        recyclerView.setAdapter(viajeAdapter);
    }

    /**
     * Configura el FloatingActionButton para añadir nuevos viajes.
     */
    private void setupFloatingActionButton() {
        fabAgregarViaje = findViewById(R.id.fabAgregarViaje);
        fabAgregarViaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iniciarActividadAgregarViaje();
            }
        });
    }

    /**
     * Inicia la actividad para agregar un nuevo viaje.
     */
    private void iniciarActividadAgregarViaje() {
        Intent intent = new Intent(MainActivity.this, AddTripActivity.class);
        startActivityForResult(intent, REQUEST_ADD_TRIP);
    }

    /**
     * Maneja los clics en los ítems del RecyclerView.
     */
    @Override
    public void onItemClick(int position) {
        Viaje viajeSeleccionado = listaViajes.get(position);
        Intent intent = new Intent(MainActivity.this, TripDetailsActivity.class);
        intent.putExtra("position", position);
        intent.putExtra("nombreViaje", viajeSeleccionado.getNombre());
        intent.putExtra("destino", viajeSeleccionado.getDestino());
        intent.putExtra("fechas", viajeSeleccionado.getFechas());
        intent.putExtra("progreso", viajeSeleccionado.getProgreso());
        intent.putExtra("imagenResId", viajeSeleccionado.getImagenResId());
        startActivityForResult(intent, REQUEST_TRIP_DETAILS);
    }

    /**
     * Maneja los clics en el botón de editar dentro del RecyclerView.
     */
    @Override
    public void onEditarClick(int position) {
        Viaje viajeSeleccionado = listaViajes.get(position);
        Intent intent = new Intent(MainActivity.this, EditTripActivity.class);
        intent.putExtra("position", position);
        intent.putExtra("nombreViaje", viajeSeleccionado.getNombre());
        intent.putExtra("destino", viajeSeleccionado.getDestino());
        intent.putExtra("fechas", viajeSeleccionado.getFechas());
        intent.putExtra("progreso", viajeSeleccionado.getProgreso());
        intent.putExtra("imagenResId", viajeSeleccionado.getImagenResId());
        startActivityForResult(intent, REQUEST_TRIP_DETAILS);
    }

    /**
     * Maneja los clics en el botón de eliminar dentro del RecyclerView.
     */
    @Override
    public void onEliminarClick(int position) {
        mostrarDialogoConfirmacionEliminar(position);
    }

    /**
     * Muestra un diálogo de confirmación antes de eliminar un viaje.
     */
    private void mostrarDialogoConfirmacionEliminar(final int position) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Viaje")
                .setMessage("¿Estás seguro de que deseas eliminar este viaje?")
                .setPositiveButton("Sí, eliminar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        eliminarViaje(position);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * Elimina un viaje de la lista y actualiza el adaptador.
     */
    private void eliminarViaje(int position) {
        listaViajes.remove(position);
        viajeAdapter.notifyItemRemoved(position);
        Toast.makeText(this, "Viaje eliminado exitosamente", Toast.LENGTH_SHORT).show();
    }

    /**
     * Maneja los resultados de las actividades iniciadas para añadir o editar viajes.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_ADD_TRIP && resultCode == RESULT_OK && data != null){
            agregarNuevoViaje(data);
        }

        if(requestCode == REQUEST_TRIP_DETAILS && resultCode == RESULT_OK && data != null){
            procesarResultadoTripDetails(data);
        }
    }

    /**
     * Agrega un nuevo viaje a la lista y actualiza el adaptador.
     */
    private void agregarNuevoViaje(Intent data) {
        String nombreViaje = data.getStringExtra("nombreViaje");
        String destino = data.getStringExtra("destino");
        String fechaInicio = data.getStringExtra("fechaInicio");
        String fechaFin = data.getStringExtra("fechaFin");
        boolean notificaciones = data.getBooleanExtra("notificaciones", false);
        int imagenResId = data.getIntExtra("imagenResId", R.drawable.ic_travel);
        int progreso = data.getIntExtra("progreso", 0);

        Viaje nuevoViaje = new Viaje(nombreViaje, destino, fechaInicio + " - " + fechaFin, progreso, imagenResId);
        listaViajes.add(nuevoViaje);
        viajeAdapter.notifyItemInserted(listaViajes.size() - 1);
        Toast.makeText(this, "Viaje agregado exitosamente", Toast.LENGTH_SHORT).show();
    }

    /**
     * Procesa el resultado de la actividad TripDetailsActivity para editar o eliminar viajes
     */
    private void procesarResultadoTripDetails(Intent data) {
        String accion = data.getStringExtra("accion");
        if(accion != null){
            if(accion.equals("editar")){
                actualizarViaje(data);
            }
            else if(accion.equals("eliminar")){
                int position = data.getIntExtra("position", -1);
                if(position != -1){
                    eliminarViaje(position);
                }
            }
        }
    }

    /**
     * Actualiza los detalles de un viaje existente en la lista y notifica al adaptador.
     */
    private void actualizarViaje(Intent data) {
        int position = data.getIntExtra("position", -1);
        if(position != -1){
            String nombreViaje = data.getStringExtra("nombreViaje");
            String destino = data.getStringExtra("destino");
            String fechas = data.getStringExtra("fechas");
            int progreso = data.getIntExtra("progreso", 0);
            int imagenResId = data.getIntExtra("imagenResId", R.drawable.ic_travel);

            Viaje viajeEditado = listaViajes.get(position);
            viajeEditado.setNombre(nombreViaje);
            viajeEditado.setDestino(destino);
            viajeEditado.setFechas(fechas);
            viajeEditado.setProgreso(progreso);
            viajeEditado.setImagenResId(imagenResId);

            viajeAdapter.notifyItemChanged(position);
            Toast.makeText(this, "Viaje editado exitosamente", Toast.LENGTH_SHORT).show();
        }
    }
}
