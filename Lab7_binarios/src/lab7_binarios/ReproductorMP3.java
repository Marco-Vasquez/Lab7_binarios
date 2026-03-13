/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab7_binarios;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import javazoom.jl.player.Player;
/**
 *
 * @author andres
 */
public class ReproductorMP3 {
    private Player player;
    private PausableInputStream pauseStream;
    private Thread hiloReproduccion;
    private String rutaActual;
    private boolean reproduciendo = false;
    public void play(String rutaMP3) {
        stop();
        rutaActual=rutaMP3;
        try{
            FileInputStream fis=new FileInputStream(rutaMP3);
            pauseStream=new PausableInputStream(new BufferedInputStream(fis));
            player=new Player(pauseStream);
            reproduciendo=true;
            hiloReproduccion=new Thread(()->{
                try{
                    player.play();
                }catch (Exception e) {}
                reproduciendo=false;
            });
            hiloReproduccion.setDaemon(true);
            hiloReproduccion.start();
        } catch (Exception e) {
            reproduciendo=false;
        }
    }
    public void pausarReanudar(){
        if (pauseStream==null) 
            return;
        if (pauseStream.estaPausado()) {
            pauseStream.reanudar();
        }else {
            pauseStream.pausar();
        }
    }
    public void stop(){
        if (pauseStream!=null){
            pauseStream.reanudar();
        }
        if (player!=null){
            player.close();
        }
        if (hiloReproduccion!=null){
            hiloReproduccion.interrupt();
        }
        player=null;
        pauseStream=null;
        hiloReproduccion=null;
        reproduciendo=false;
        rutaActual=null;
    }
    public boolean estaReproduciendo() {
        return reproduciendo && (pauseStream!=null && !pauseStream.estaPausado());
    }
    public boolean estaPausado() {
        return pauseStream!=null && pauseStream.estaPausado();
    }
    public String getRutaActual() {
        return rutaActual;
    }
}
