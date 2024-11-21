package es.cm.dam2.prdm.vamosdeviaje;

public class Viaje {
    private String nombre;
    private String destino;
    private String fechas;
    private int progreso;
    private int imagenResId; // ID del recurso drawable para la imagen

    public Viaje(String nombre, String destino, String fechas, int progreso, int imagenResId) {
        this.nombre = nombre;
        this.destino = destino;
        this.fechas = fechas;
        this.progreso = progreso;
        this.imagenResId = imagenResId;
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getFechas() {
        return fechas;
    }

    public void setFechas(String fechas) {
        this.fechas = fechas;
    }

    public int getProgreso() {
        return progreso;
    }

    public void setProgreso(int progreso) {
        this.progreso = progreso;
    }

    public int getImagenResId() {
        return imagenResId;
    }

    public void setImagenResId(int imagenResId) {
        this.imagenResId = imagenResId;
    }
}