/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab7_binarios;
import java.io.IOException;
import java.io.InputStream;
/**
 *
 * @author andres
 */
public class PausableInputStream extends InputStream{
    private final InputStream wrapped;
    private volatile boolean pausado = false;
    public PausableInputStream(InputStream inputStream){
        this.wrapped=inputStream;
    }
    public int read() throws IOException{
        bloquearSiPausado();
        return wrapped.read();
    }
    public int read(byte[] buffer,int offset,int length) throws IOException{
        bloquearSiPausado();
        return wrapped.read(buffer, offset, length);
    }
    private synchronized void bloquearSiPausado(){
        while (pausado){
            try{
                wait();
            }catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }
    }
    public synchronized void pausar(){
        pausado=true;
    }
    public synchronized void reanudar(){
        pausado=false;
        notifyAll();
    }
    public boolean estaPausado(){
        return pausado;
    }
    public void close() throws IOException{
        reanudar();
        wrapped.close();
    }
}
