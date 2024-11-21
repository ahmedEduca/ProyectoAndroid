package es.cm.dam2.prdm.vamosdeviaje;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.Calendar;

public class AddTripActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int PERMISOS_CAMARA = 100;

    // Variables de UI
    private EditText etNombreViaje;
    private EditText etDestino;
    private TextView tvFechaInicioSeleccionada;
    private TextView tvFechaFinSeleccionada;
    private Switch switchNotificaciones;
    private ImageView ivImagenViaje;

    // Variables de lógica
    private String fechaInicio = "";
    private String fechaFin = "";
    private Uri imagenUri = null;
    private Bitmap imagenBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);

        configurarToolbar();
        inicializarComponentes();
        configurarListeners();
    }

    /**
     * Configura la Toolbar de la actividad.
     */
    private void configurarToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbarAddTrip);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Inicializa los componentes de la interfaz.
     */
    private void inicializarComponentes() {
        etNombreViaje = findViewById(R.id.etNombreViaje);
        etDestino = findViewById(R.id.etDestino);
        tvFechaInicioSeleccionada = findViewById(R.id.tvFechaInicioSeleccionada);
        tvFechaFinSeleccionada = findViewById(R.id.tvFechaFinSeleccionada);
        switchNotificaciones = findViewById(R.id.switchNotificaciones);
        ivImagenViaje = findViewById(R.id.ivImagenViaje);
    }

    /**
     * Configura los listeners para los botones.
     */
    private void configurarListeners() {
        findViewById(R.id.btnSeleccionarFechaInicio).setOnClickListener(v -> mostrarDatePickerDialog(true));
        findViewById(R.id.btnSeleccionarFechaFin).setOnClickListener(v -> mostrarDatePickerDialog(false));
        findViewById(R.id.btnSeleccionarImagen).setOnClickListener(v -> mostrarOpcionesSeleccionImagen());
        findViewById(R.id.btnGuardarViaje).setOnClickListener(v -> guardarViaje());
    }

    /**
     * Muestra el DatePickerDialog para seleccionar una fecha.
     */
    private void mostrarDatePickerDialog(boolean esInicio) {
        Calendar calendario = Calendar.getInstance();
        int año = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String fecha = dayOfMonth + "/" + (month + 1) + "/" + year;
            if (esInicio) {
                fechaInicio = fecha;
                tvFechaInicioSeleccionada.setText("Fecha de Inicio: " + fecha);
            } else {
                fechaFin = fecha;
                tvFechaFinSeleccionada.setText("Fecha de Fin: " + fecha);
            }
        }, año, mes, dia).show();
    }

    /**
     * Muestra opciones para seleccionar una imagen desde la galería o la cámara.
     */
    private void mostrarOpcionesSeleccionImagen() {
        // Para simplicidad, mostraremos un diálogo con dos opciones
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Seleccionar Imagen")
                .setItems(new CharSequence[]{"Galería", "Cámara"}, (dialog, which) -> {
                    if (which == 0) {
                        abrirGaleria();
                    } else if (which == 1) {
                        abrirCamara();
                    }
                })
                .show();
    }

    /**
     * Abre la galería para seleccionar una imagen.
     */
    private void abrirGaleria() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleccionar Imagen"), PICK_IMAGE_REQUEST);
    }

    /**
     * Abre la cámara para capturar una imagen.
     */
    private void abrirCamara() {
        if (verificarPermisoCamara()) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            } else {
                Toast.makeText(this, "No hay una aplicación de cámara disponible", Toast.LENGTH_SHORT).show();
            }
        } else {
            solicitarPermisoCamara();
        }
    }

    /**
     * Verifica si el permiso de cámara ya está concedido.
     */
    private boolean verificarPermisoCamara() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Solicita el permiso de cámara al usuario.
     */
    private void solicitarPermisoCamara() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                PERMISOS_CAMARA);
    }

    /**
     * Maneja la respuesta del usuario a la solicitud de permisos.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISOS_CAMARA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, abre la cámara
                abrirCamara();
            } else {
                // Permiso denegado, muestra un mensaje
                Toast.makeText(this, "Permiso de cámara es necesario para usar esta función", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Maneja el resultado de las actividades iniciadas para seleccionar o capturar una imagen.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Selección de imagen desde la galería
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imagenUri = data.getData();
            cargarImagenSeleccionada();
        }

        // Captura de imagen desde la cámara
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            imagenBitmap = (Bitmap) extras.get("data");
            ivImagenViaje.setImageBitmap(imagenBitmap);
            // Opcional: Guardar la imagenBitmap en un Uri si lo deseas
        }
    }

    /**
     * Carga la imagen seleccionada desde la galería en el ImageView.
     */
    private void cargarImagenSeleccionada() {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagenUri);
            ivImagenViaje.setImageBitmap(bitmap);
        } catch (IOException e) {
            Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Valida los datos ingresados y guarda el viaje.
     */
    private void guardarViaje() {
        if (!validarCampos()) return;

        String nombreViaje = etNombreViaje.getText().toString().trim();
        String destino = etDestino.getText().toString().trim();
        boolean notificaciones = switchNotificaciones.isChecked();

        Intent resultadoIntent = new Intent();
        resultadoIntent.putExtra("nombreViaje", nombreViaje);
        resultadoIntent.putExtra("destino", destino);
        resultadoIntent.putExtra("fechaInicio", fechaInicio);
        resultadoIntent.putExtra("fechaFin", fechaFin);
        resultadoIntent.putExtra("notificaciones", notificaciones);
        // Para simplificar, usaremos una imagen predeterminada
        resultadoIntent.putExtra("imagenResId", R.drawable.ic_travel);
        resultadoIntent.putExtra("progreso", 0); // Valor inicial

        setResult(RESULT_OK, resultadoIntent);
        Toast.makeText(this, "Viaje guardado", Toast.LENGTH_SHORT).show();
        finish();
    }

    /**
     * Valida que los campos obligatorios estén completos.
     */
    private boolean validarCampos() {
        if (etNombreViaje.getText().toString().trim().isEmpty()) {
            etNombreViaje.setError("Ingrese el nombre del viaje");
            etNombreViaje.requestFocus();
            return false;
        }
        if (etDestino.getText().toString().trim().isEmpty()) {
            etDestino.setError("Ingrese el destino del viaje");
            etDestino.requestFocus();
            return false;
        }
        if (fechaInicio.isEmpty()) {
            Toast.makeText(this, "Seleccione la fecha de inicio", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (fechaFin.isEmpty()) {
            Toast.makeText(this, "Seleccione la fecha de fin", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Maneja el botón de regreso en la Toolbar.
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
