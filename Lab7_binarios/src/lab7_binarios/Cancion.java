/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab7_binarios;

/**
 *
 * @author andres
 */
public class Cancion {
    public static final int TAM_NOMBRE=50;
    public static final int TAM_ARTISTA=40;
    public static final int TAM_GENERO=30;
    public static final int TAM_RUTA_MP3=200;
    public static final int TAM_RUTA_IMG=200;
    public static final int TAM_REGISTRO=4+(TAM_NOMBRE*2)+(TAM_ARTISTA*2)+(TAM_GENERO*2)+4+(TAM_RUTA_MP3*2)+(TAM_RUTA_IMG*2)+1;
    private int id;
    private String nombre;
    private String artista;
    private String genero;
    private int duracion;
    private String rutaMP3;
    private String rutaImagen;
    private boolean activo;
    public Cancion() {
    }
    public Cancion(int id,String nombre,String artista,String genero,int duracion,String rutaMP3,String rutaImagen,boolean activo){
        this.id=id;
        this.nombre=nombre;
        this.artista=artista;
        this.genero=genero;
        this.duracion=duracion;
        this.rutaMP3=rutaMP3;
        this.rutaImagen=rutaImagen;
        this.activo=activo;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id=id;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre=nombre;
    }
    public String getArtista() {
        return artista;
    }
    public void setArtista(String artista) {
        this.artista=artista;
    }
    public String getGenero() {
        return genero;
    }
    public void setGenero(String genero) {
        this.genero=genero;
    }
    public int getDuracion() {
        return duracion;
    }
    public void setDuracion(int duracion) {
        this.duracion=duracion;
    }
    public String getRutaMP3() {
        return rutaMP3;
    }
    public void setRutaMP3(String rutaMP3) {
        this.rutaMP3=rutaMP3;
    }
    public String getRutaImagen() {
        return rutaImagen;
    }
    public void setRutaImagen(String rutaImagen) {
        this.rutaImagen=rutaImagen;
    }
    public boolean isActivo() {
        return activo;
    }
    public void setActivo(boolean activo) {
        this.activo=activo;
    }
    public String getDuracionFormateada() {
        int minutos=duracion/60;
        int segundos=duracion%60;
        return minutos+":"+String.format("%02d",segundos);
    }
    public String toString() {
        return "Cancion{"
                + "id="       + id
                + ", nombre='" + nombre   + '\''
                + ", artista='" + artista + '\''
                + ", genero='"  + genero  + '\''
                + ", duracion=" + getDuracionFormateada()
                + ", activo="   + activo
                + '}';
    }
}
