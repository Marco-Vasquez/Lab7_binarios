/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab7_binarios;
import java.io.IOException;
import java.io.RandomAccessFile;
/**
 *
 * @author andres
 */
public class ArchivoCanciones{
    private static final String NOMBRE_ARCHIVO="canciones.dat";
    private static final int TAM_NOMBRE=50;
    private static final int TAM_ARTISTA=40;
    private static final int TAM_GENERO=30;
    private static final int TAM_RUTA=200;
    private static final int TAM_REGISTRO=4+(TAM_NOMBRE*2)+(TAM_ARTISTA*2)+(TAM_GENERO*2)+4+(TAM_RUTA*2)+(TAM_RUTA*2)+1;
    public void guardar(Cancion c) throws IOException{
        try (RandomAccessFile raf=new RandomAccessFile(NOMBRE_ARCHIVO,"rw")){
            int id=(int)(raf.length()/TAM_REGISTRO);
            c.setId(id);
            raf.seek(raf.length());
            escribirRegistro(raf, c);
        }
    }
    public Cancion[] leerTodos() throws IOException{
        try (RandomAccessFile raf=new RandomAccessFile(NOMBRE_ARCHIVO,"r")) {
            int total=(int)(raf.length()/TAM_REGISTRO);
            Cancion[] temp=new Cancion[total];
            int contador=0;
            for (int control=0;control<total;control++) {
                raf.seek((long) control*TAM_REGISTRO);
                Cancion c=leerRegistro(raf);
                if (c.isActivo()) {
                    temp[contador++]=c;
                }
            }
            Cancion[] resultado=new Cancion[contador];
            System.arraycopy(temp, 0, resultado, 0, contador);
            return resultado;
        }
    }
 
    public Cancion buscarPorId(int id) throws IOException{
        try (RandomAccessFile raf=new RandomAccessFile(NOMBRE_ARCHIVO, "r")) {
            int total = (int) (raf.length()/TAM_REGISTRO);
            if (id<0 || id>=total)
                return null;
            raf.seek((long) id*TAM_REGISTRO);
            Cancion c=leerRegistro(raf);
            return c.isActivo() ? c:null;
        }
    }
 
    public void eliminarPorId(int id) throws IOException {
        try (RandomAccessFile raf=new RandomAccessFile(NOMBRE_ARCHIVO, "rw")) {
            int total=(int)(raf.length()/TAM_REGISTRO);
            if (id <0 || id>=total)
                return;
            long offsetActivo=((long) id*TAM_REGISTRO)+4+(TAM_NOMBRE*2)+(TAM_ARTISTA*2)+(TAM_GENERO*2)+4+(TAM_RUTA*2)+(TAM_RUTA*2);
            raf.seek(offsetActivo);
            raf.writeBoolean(false);
        }
    } 
    public int obtenerTotalRegistros() throws IOException{
        try (RandomAccessFile raf=new RandomAccessFile(NOMBRE_ARCHIVO, "r")) {
            return (int)(raf.length()/TAM_REGISTRO);
        } catch (IOException e) {
            return 0;
        }
    }
    private void escribirRegistro(RandomAccessFile raf,Cancion c) throws IOException{
        raf.writeInt(c.getId());
        escribirString(raf, c.getNombre(),TAM_NOMBRE);
        escribirString(raf, c.getArtista(),TAM_ARTISTA);
        escribirString(raf, c.getGenero(),TAM_GENERO);
        raf.writeInt(c.getDuracion());
        escribirString(raf, c.getRutaMP3(),TAM_RUTA);
        escribirString(raf, c.getRutaImagen(),TAM_RUTA);
        raf.writeBoolean(c.isActivo());
    }
    private Cancion leerRegistro(RandomAccessFile raf) throws IOException {
        int id=raf.readInt();
        String nombre=leerString(raf, TAM_NOMBRE);
        String artista=leerString(raf, TAM_ARTISTA);
        String genero=leerString(raf, TAM_GENERO);
        int duracion=raf.readInt();
        String rutaMP3=leerString(raf, TAM_RUTA);
        String rutaImagen=leerString(raf, TAM_RUTA);
        boolean activo=raf.readBoolean();
        return new Cancion(id, nombre, artista, genero, duracion, rutaMP3, rutaImagen, activo);
    }
    private void escribirString(RandomAccessFile raf, String texto, int longitud) throws IOException {
        if (texto==null)
            texto="";
        if (texto.length()>longitud)
            texto=texto.substring(0, longitud);
        for (int control=0;control<longitud;control++) {
            if (control<texto.length()) {
                raf.writeChar(texto.charAt(control));
            }else{
                raf.writeChar(' ');
            }
        }
    }
    private String leerString(RandomAccessFile raf, int longitud) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int control=0;control<longitud;control++) {
            sb.append(raf.readChar());
        }
        return sb.toString().trim();
    }
}
