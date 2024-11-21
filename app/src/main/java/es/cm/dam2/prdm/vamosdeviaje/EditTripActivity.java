package es.cm.dam2.prdm.vamosdeviaje;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Calendar;

public class EditTripActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    // UI Components
    private EditText etNombreViajeEdit;
    private EditText etDestinoEdit;
    private Button btnSeleccionarFechaInicioEdit;
    private Button btnSeleccionarFechaFinEdit;
    private TextView tvFechaInicioSeleccionadaEdit;
    private TextView tvFechaFinSeleccionadaEdit;
    private Switch switchNotificacionesEdit;
    private ImageView ivImagenViajeEdit;
    private Button btnSeleccionarImagenEdit;
    private Button btnGuardarCambiosEdit;

    // Data Variables
    private String fechaInicio = "";
    private String fechaFin = "";
    private Uri imagenUri = null;

    private int position;
    private Viaje viajeOriginal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_trip);

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
        Toolbar toolbar = findViewById(R.id.toolbarEditTrip);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Editar Viaje");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Inicializa los componentes de la interfaz de usuario.
     */
    private void initView() {
        etNombreViajeEdit = findViewById(R.id.etNombreViajeEdit);
        etDestinoEdit = findViewById(R.id.etDestinoEdit);
        btnSeleccionarFechaInicioEdit = findViewById(R.id.btnSeleccionarFechaInicioEdit);
        btnSeleccionarFechaFinEdit = findViewById(R.id.btnSeleccionarFechaFinEdit);
        tvFechaInicioSeleccionadaEdit = findViewById(R.id.tvFechaInicioSeleccionadaEdit);
        tvFechaFinSeleccionadaEdit = findViewById(R.id.tvFechaFinSeleccionadaEdit);
        switchNotificacionesEdit = findViewById(R.id.switchNotificacionesEdit);
        ivImagenViajeEdit = findViewById(R.id.ivImagenViajeEdit);
        btnSeleccionarImagenEdit = findViewById(R.id.btnSeleccionarImagenEdit);
        btnGuardarCambiosEdit = findViewById(R.id.btnGuardarCambiosEdit);
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

        viajeOriginal = new Viaje(nombreViaje, destino, fechas, progreso, imagenResId);

        // Separar las fechas de inicio y fin
        String[] fechasSeparadas = fechas.split(" - ");
        if (fechasSeparadas.length == 2) {
            fechaInicio = fechasSeparadas[0];
            fechaFin = fechasSeparadas[1];
        }
    }

    /**
     * Población de la interfaz de usuario con los datos del viaje.
     */
    private void poblacionUI() {
        etNombreViajeEdit.setText(viajeOriginal.getNombre());
        etDestinoEdit.setText(viajeOriginal.getDestino());
        tvFechaInicioSeleccionadaEdit.setText("Fecha de Inicio: " + fechaInicio);
        tvFechaFinSeleccionadaEdit.setText("Fecha de Fin: " + fechaFin);
        switchNotificacionesEdit.setChecked(false); // Ajusta según el estado actual si lo tienes almacenado

        ivImagenViajeEdit.setImageResource(viajeOriginal.getImagenResId());
    }

    /**
     * Configura los listeners para los botones de seleccionar fecha, seleccionar imagen y guardar cambios.
     */
    private void initListeners() {
        btnSeleccionarFechaInicioEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDatePickerDialog(true);
            }
        });

        btnSeleccionarFechaFinEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDatePickerDialog(false);
            }
        });

        btnSeleccionarImagenEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarImagen();
            }
        });

        btnGuardarCambiosEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarCambiosViaje();
            }
        });
    }

    /**
     * Muestra un DatePickerDialog para seleccionar la fecha de inicio o fin.
     *
     * @param esInicio Indica si es para la fecha de inicio.
     */
    private void mostrarDatePickerDialog(final boolean esInicio) {
        final Calendar calendario = Calendar.getInstance();
        int año = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String fecha = dayOfMonth + "/" + (month + 1) + "/" + year;
                if (esInicio) {
                    fechaInicio = fecha;
                    tvFechaInicioSeleccionadaEdit.setText("Fecha de Inicio: " + fecha);
                } else {
                    fechaFin = fecha;
                    tvFechaFinSeleccionadaEdit.setText("Fecha de Fin: " + fecha);
                }
            }
        }, año, mes, dia);

        datePickerDialog.show();
    }

    /**
     * Inicia un Intent para seleccionar una imagen desde la galería.
     */
    private void seleccionarImagen() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleccionar Imagen"), PICK_IMAGE_REQUEST);
    }

    /**
     * Maneja la respuesta del Intent de selección de imagen.
     *
     * @param requestCode Código de solicitud.
     * @param resultCode  Código de resultado.
     * @param data        Datos devueltos.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imagenUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagenUri);
                ivImagenViajeEdit.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Guarda los cambios del viaje y envía los datos de vuelta a MainActivity.
     */
    private void guardarCambiosViaje() {
        String nombreViaje = etNombreViajeEdit.getText().toString().trim();
        String destino = etDestinoEdit.getText().toString().trim();

        // Validar campos obligatorios
        if (nombreViaje.isEmpty()) {
            etNombreViajeEdit.setError("Ingrese el nombre del viaje");
            etNombreViajeEdit.requestFocus();
            return;
        }

        if (destino.isEmpty()) {
            etDestinoEdit.setError("Ingrese el destino del viaje");
            etDestinoEdit.requestFocus();
            return;
        }

        if (fechaInicio.isEmpty()) {
            btnSeleccionarFechaInicioEdit.setError("Seleccione la fecha de inicio");
            btnSeleccionarFechaInicioEdit.requestFocus();
            return;
        }

        if (fechaFin.isEmpty()) {
            btnSeleccionarFechaFinEdit.setError("Seleccione la fecha de fin");
            btnSeleccionarFechaFinEdit.requestFocus();
            return;
        }

        // Obtener el estado del switch de notificaciones
        boolean notificacionesActivadas = switchNotificacionesEdit.isChecked();

        // Obtener la imagen seleccionada
        int imagenResId = viajeOriginal.getImagenResId(); // Mantener la imagen original si no se ha cambiado
        if (imagenUri != null) {
            // Aquí puedes manejar la imagen seleccionada, como almacenarla o obtener su URI real
            // Para simplificar, usaremos la imagen predeterminada
            imagenResId = R.drawable.ic_travel;
        }

        // Calcular el progreso (puedes implementar una lógica más compleja si lo deseas)
        int progreso = viajeOriginal.getProgreso(); // Mantener el progreso original

        // Crear un Intent para enviar los datos de vuelta a MainActivity
        Intent resultadoIntent = new Intent();
        resultadoIntent.putExtra("position", position);
        resultadoIntent.putExtra("nombreViaje", nombreViaje);
        resultadoIntent.putExtra("destino", destino);
        resultadoIntent.putExtra("fechas", fechaInicio + " - " + fechaFin);
        resultadoIntent.putExtra("progreso", progreso); // Puedes recalcular el progreso si es necesario
        resultadoIntent.putExtra("imagenResId", imagenResId);
        resultadoIntent.putExtra("accion", "editar");

        setResult(RESULT_OK, resultadoIntent);
        Toast.makeText(this, "Cambios guardados exitosamente", Toast.LENGTH_SHORT).show();
        regresar();
    }

    /**
     * Muestra un mensaje de error y cierra la actividad.
     *
     * @param message Mensaje de error a mostrar.
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
