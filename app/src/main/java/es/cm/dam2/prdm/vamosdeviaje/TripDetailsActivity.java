package es.cm.dam2.prdm.vamosdeviaje;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class TripDetailsActivity extends AppCompatActivity {

    // UI Components
    private ImageView ivImagenViajeDetails;
    private TextView tvNombreViajeDetails;
    private TextView tvDestinoDetails;
    private TextView tvFechasDetails;
    private ProgressBar pgViajeDetails;
    private TextView tvProgresoDetails;
    private Button btnEditarViajeDetails;
    private Button btnEliminarViajeDetails;

    // Data Variables
    private int position;
    private Viaje viajeSeleccionado;

    // Request Codes
    private static final int REQUEST_EDIT_TRIP = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        initToolbar();
        initView();
        recuperarDatosViaje();
        poblacionUI();
        initListeners();
    }

    /**
     * Configura la Toolbar con el título y el botón de regreso.
     */
    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbarTripDetails);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Detalles del Viaje");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Inicializa los componentes de la interfaz de usuario.
     */
    private void initView() {
        ivImagenViajeDetails = findViewById(R.id.ivImagenViajeDetails);
        tvNombreViajeDetails = findViewById(R.id.tvNombreViajeDetails);
        tvDestinoDetails = findViewById(R.id.tvDestinoDetails);
        tvFechasDetails = findViewById(R.id.tvFechasDetails);
        pgViajeDetails = findViewById(R.id.pgViajeDetails);
        tvProgresoDetails = findViewById(R.id.tvProgresoDetails);
        btnEditarViajeDetails = findViewById(R.id.btnEditarViajeDetails);
        btnEliminarViajeDetails = findViewById(R.id.btnEliminarViajeDetails);
    }

    /**
     * Recupera los datos del viaje desde el Intent que inició la actividad.
     */
    private void recuperarDatosViaje() {
        Intent intent = getIntent();
        position = intent.getIntExtra("position", -1);
        if (position == -1) {
            mostrarMensajeError("Error al obtener datos del viaje");
            return;
        }

        String nombreViaje = intent.getStringExtra("nombreViaje");
        String destino = intent.getStringExtra("destino");
        String fechas = intent.getStringExtra("fechas");
        int progreso = intent.getIntExtra("progreso", 0);
        int imagenResId = intent.getIntExtra("imagenResId", R.drawable.ic_travel);

        viajeSeleccionado = new Viaje(nombreViaje, destino, fechas, progreso, imagenResId);
    }

    /**
     * Población de la interfaz de usuario con los datos del viaje.
     */
    private void poblacionUI() {
        ivImagenViajeDetails.setImageResource(viajeSeleccionado.getImagenResId());
        tvNombreViajeDetails.setText(viajeSeleccionado.getNombre());
        tvDestinoDetails.setText("Destino: " + viajeSeleccionado.getDestino());
        tvFechasDetails.setText("Fechas: " + viajeSeleccionado.getFechas());
        pgViajeDetails.setProgress(viajeSeleccionado.getProgreso());
        tvProgresoDetails.setText(viajeSeleccionado.getProgreso() + "% Completo");
    }

    /**
     * Configura los listeners para los botones de editar y eliminar.
     */
    private void initListeners() {
        btnEditarViajeDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irAEditarViaje();
            }
        });

        btnEliminarViajeDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmarEliminarViaje();
            }
        });
    }

    /**
     * Navega a la actividad EditTripActivity para editar el viaje.
     */
    private void irAEditarViaje() {
        Intent editIntent = new Intent(TripDetailsActivity.this, EditTripActivity.class);
        editIntent.putExtra("position", position);
        editIntent.putExtra("nombreViaje", viajeSeleccionado.getNombre());
        editIntent.putExtra("destino", viajeSeleccionado.getDestino());
        editIntent.putExtra("fechas", viajeSeleccionado.getFechas());
        editIntent.putExtra("progreso", viajeSeleccionado.getProgreso());
        editIntent.putExtra("imagenResId", viajeSeleccionado.getImagenResId());
        startActivityForResult(editIntent, REQUEST_EDIT_TRIP);
    }

    /**
     * Muestra un diálogo de confirmación antes de eliminar el viaje.
     */
    private void confirmarEliminarViaje() {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Viaje")
                .setMessage("¿Estás seguro de que deseas eliminar este viaje?")
                .setPositiveButton("Sí, eliminar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        eliminarViaje();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * Elimina el viaje y envía el resultado a MainActivity.
     */
    private void eliminarViaje() {
        Intent resultadoIntent = new Intent();
        resultadoIntent.putExtra("position", position);
        resultadoIntent.putExtra("accion", "eliminar");
        setResult(RESULT_OK, resultadoIntent);
        Toast.makeText(this, "Viaje eliminado exitosamente", Toast.LENGTH_SHORT).show();
        regresar();
    }

    /**
     * Maneja el resultado de EditTripActivity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_EDIT_TRIP && resultCode == RESULT_OK && data != null) {
            String accion = data.getStringExtra("accion");
            if ("editar".equals(accion)) {
                actualizarDetallesDelViaje(data);
            }
        }
    }

    /**
     * Actualiza los detalles del viaje con los datos editados.
     */
    private void actualizarDetallesDelViaje(@NonNull Intent data) {
        String nombreViaje = data.getStringExtra("nombreViaje");
        String destino = data.getStringExtra("destino");
        String fechas = data.getStringExtra("fechas");
        int progreso = data.getIntExtra("progreso", 0);
        int imagenResId = data.getIntExtra("imagenResId", R.drawable.ic_travel);

        // Actualizar objeto Viaje
        viajeSeleccionado.setNombre(nombreViaje);
        viajeSeleccionado.setDestino(destino);
        viajeSeleccionado.setFechas(fechas);
        viajeSeleccionado.setProgreso(progreso);
        viajeSeleccionado.setImagenResId(imagenResId);

        // Actualizar la interfaz de usuario
        poblacionUI();

        Toast.makeText(this, "Viaje editado exitosamente", Toast.LENGTH_SHORT).show();
    }

    /**
     * Muestra un mensaje de error y cierra la actividad.
     */
    private void mostrarMensajeError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        finish();
    }

    /**
     * Cierra la actividad y regresa a la anterior.
     */
    private void regresar() {
        finish();
    }

    // Manejar el botón de regreso en el Toolbar
    @Override
    public boolean onSupportNavigateUp() {
        regresar();
        return true;
    }
}
