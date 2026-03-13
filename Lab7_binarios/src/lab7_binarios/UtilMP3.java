/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab7_binarios;
import com.mpatric.mp3agic.Mp3File;
/**
 *
 * @author andres
 */
public class UtilMP3 {
    public static int obtenerDuracion(String rutaMP3){
        try{
            Mp3File mp3=new Mp3File(rutaMP3);
            return (int)mp3.getLengthInSeconds();
        } catch(Exception e){
            return 0;
        }
    }
 
    public static String obtenerArtista(String rutaMP3){
        try{
            Mp3File mp3=new Mp3File(rutaMP3);
            if(mp3.hasId3v2Tag()) {
                String a=mp3.getId3v2Tag().getArtist();
                if(a!=null&&!a.trim().isEmpty()) return a.trim();
            }
            if(mp3.hasId3v1Tag()){
                String a=mp3.getId3v1Tag().getArtist();
                if(a!=null&&!a.trim().isEmpty())
                    return a.trim();
            }
        }catch(Exception e) {}
        return "";
    }
 
    public static String obtenerNombreSugerido(String rutaMP3){
        try{
            Mp3File mp3=new Mp3File(rutaMP3);
            if(mp3.hasId3v2Tag()){
                String t=mp3.getId3v2Tag().getTitle();
                if(t!=null&&!t.trim().isEmpty()) return t.trim();
            }
            if(mp3.hasId3v1Tag()){
                String t=mp3.getId3v1Tag().getTitle();
                if(t!=null&&!t.trim().isEmpty()) return t.trim();
            }
        }catch(Exception e) {}
        return "";
    }
    public static String obtenerGenero(String rutaMP3){
        try{
            Mp3File mp3=new Mp3File(rutaMP3);
            if(mp3.hasId3v2Tag()){
                String g=mp3.getId3v2Tag().getGenreDescription();
                if(g!=null&&!g.trim().isEmpty()) return g.trim();
            }
            if(mp3.hasId3v1Tag()){
                String g=mp3.getId3v1Tag().getGenreDescription();
                if(g!=null&&!g.trim().isEmpty()) return g.trim();
            }
        }catch(Exception e){}
        return "";
    }
    public static String[] extraerDesdNombreArchivo(String rutaMP3){
        java.io.File archivo=new java.io.File(rutaMP3);
        String nombreArchivo=archivo.getName();
        if(nombreArchivo.toLowerCase().endsWith(".mp3")){
            nombreArchivo=nombreArchivo.substring(0,nombreArchivo.length()-4);
        }
        String[] partes=nombreArchivo.split(" - ");
        String artista="";
        String nombre="";
        String genero="";
        if(partes.length==1){
            nombre=partes[0].trim();
        }else if(partes.length==2){
            artista=partes[0].trim();
            nombre=partes[1].trim();
        }else if(partes.length>=3){
            artista=partes[0].trim();
            nombre=partes[1].trim();
            genero=partes[2].trim();
        }
        return new String[]{artista,nombre,genero};
    }
}
