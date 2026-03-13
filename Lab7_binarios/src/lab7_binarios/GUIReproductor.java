/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab7_binarios;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author andres
 */
public class GUIReproductor extends JFrame {
    private JTable tablaCanciones;
    private DefaultTableModel modeloTabla;
    private JLabel lblImagen;
    private JLabel lblNombre;
    private JLabel lblArtista;
    private JLabel lblDuracion;
    private JButton btnPlay;
    private JButton btnPause;
    private JButton btnStop;
    private JButton btnAdd;
    private JButton btnSelect;
    private JButton btnRemove;
    private Cancion cancionSeleccionada;
    private ArchivoCanciones archivoCanciones;
    private ReproductorMP3 reproductor;
    private ImageIcon imagenDefault;
    private static final String CARPETA_CANCIONES="canciones";
    public GUIReproductor() {
        archivoCanciones=new ArchivoCanciones();
        reproductor=new ReproductorMP3();
        imagenDefault=cargarImagenDefault();
        crearCarpetaCanciones();
        migrarRutasAbsolutas();
        initComponents();
        refrescarTabla();
    }
    private void crearCarpetaCanciones() {
        File carpeta=new File(CARPETA_CANCIONES);
        if(!carpeta.exists()) {
            carpeta.mkdirs();
        }
    }
    private void migrarRutasAbsolutas() {
        try {
            Cancion[] canciones=archivoCanciones.leerTodos();
            for(Cancion c : canciones) {
                String ruta=c.getRutaMP3();
                if(ruta==null||ruta.isEmpty()) continue;
                boolean esAbsoluta=ruta.contains(":\\") || ruta.startsWith("/Users/") || ruta.startsWith("/home/");
                if(esAbsoluta) {
                    String nombreArchivo=new File(ruta).getName();
                    String rutaRelativa=CARPETA_CANCIONES+"/"+nombreArchivo;
                    archivoCanciones.actualizarRutaMP3(c.getId(),rutaRelativa);
                }
            }
        } catch(IOException e) {}
    }
    private String copiarMP3AlProyecto(String rutaOriginal) throws IOException {
        File origen=new File(rutaOriginal);
        File carpeta=new File(CARPETA_CANCIONES);
        File destino=new File(carpeta,origen.getName());
        if(!destino.exists()) {
            Files.copy(origen.toPath(),destino.toPath(),StandardCopyOption.REPLACE_EXISTING);
        }
        return CARPETA_CANCIONES+"/"+origen.getName();
    }
    private File resolverRutaMP3(String rutaGuardada) {
        if(rutaGuardada==null||rutaGuardada.isEmpty()) return new File("");
        File f=new File(rutaGuardada);
        if(f.isAbsolute()&&f.exists()) return f;
        File relativa=new File(System.getProperty("user.dir"),rutaGuardada);
        if(relativa.exists()) return relativa;
        String nombreArchivo=new File(rutaGuardada).getName();
        File enCanciones=new File(System.getProperty("user.dir"),CARPETA_CANCIONES+"/"+nombreArchivo);
        if(enCanciones.exists()) return enCanciones;
        return f;
    }
    private ImageIcon cargarImagenDefault() {
        try {
            java.net.URL url=getClass().getResource("/lab7_binarios/recursos/imagen_default.png");
            if(url!=null) {
                java.awt.image.BufferedImage buf=ImageIO.read(url);
                if(buf!=null) {
                    Image scaled=buf.getScaledInstance(160,160,Image.SCALE_SMOOTH);
                    return new ImageIcon(scaled);
                }
            }
        } catch(Exception e) {}
        java.awt.image.BufferedImage buf=new java.awt.image.BufferedImage(160,160,java.awt.image.BufferedImage.TYPE_INT_RGB);
        Graphics2D g=buf.createGraphics();
        g.setColor(new Color(210,210,210));
        g.fillRect(0,0,160,160);
        g.setColor(new Color(150,150,150));
        g.setFont(new Font("Arial",Font.BOLD,48));
        FontMetrics fm=g.getFontMetrics();
        String sym="♪";
        int x=(160-fm.stringWidth(sym))/2;
        int y=(160+fm.getAscent())/2-8;
        g.drawString(sym,x,y);
        g.dispose();
        return new ImageIcon(buf);
    }
    private void initComponents() {
        setTitle("Reproductor de Música");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(780,500);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout(8,8));
        getContentPane().setBackground(new Color(230,230,230));
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        JPanel panelIzquierdo=new JPanel();
        panelIzquierdo.setLayout(new BoxLayout(panelIzquierdo,BoxLayout.Y_AXIS));
        panelIzquierdo.setBackground(new Color(230,230,230));
        panelIzquierdo.setPreferredSize(new Dimension(180,0));
        panelIzquierdo.setBorder(BorderFactory.createTitledBorder("Portada"));
        lblImagen=new JLabel();
        lblImagen.setIcon(imagenDefault);
        lblImagen.setText("");
        lblImagen.setPreferredSize(new Dimension(160,160));
        lblImagen.setMinimumSize(new Dimension(160,160));
        lblImagen.setMaximumSize(new Dimension(160,160));
        lblImagen.setHorizontalAlignment(JLabel.CENTER);
        lblImagen.setVerticalAlignment(JLabel.CENTER);
        lblImagen.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblImagen.setBorder(BorderFactory.createLineBorder(Color.GRAY,1));
        lblImagen.setBackground(Color.WHITE);
        lblImagen.setOpaque(true);
        lblNombre=new JLabel("Sin selección",JLabel.CENTER);
        lblNombre.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblNombre.setFont(new Font("Arial",Font.BOLD,12));
        lblNombre.setBorder(BorderFactory.createEmptyBorder(6,0,2,0));
        lblArtista=new JLabel(" ",JLabel.CENTER);
        lblArtista.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblArtista.setFont(new Font("Arial",Font.PLAIN,11));
        lblDuracion=new JLabel(" ",JLabel.CENTER);
        lblDuracion.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblDuracion.setFont(new Font("Arial",Font.PLAIN,11));
        lblDuracion.setForeground(new Color(80,80,80));
        panelIzquierdo.add(Box.createVerticalStrut(6));
        panelIzquierdo.add(lblImagen);
        panelIzquierdo.add(lblNombre);
        panelIzquierdo.add(lblArtista);
        panelIzquierdo.add(lblDuracion);
        String[] columnas={"ID","Nombre","Artista","Género","Duración"};
        modeloTabla=new DefaultTableModel(columnas,0) {
            public boolean isCellEditable(int row,int col) { return false; }
        };
        tablaCanciones=new JTable(modeloTabla);
        tablaCanciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaCanciones.setFont(new Font("Arial",Font.PLAIN,12));
        tablaCanciones.setRowHeight(24);
        tablaCanciones.getTableHeader().setFont(new Font("Arial",Font.BOLD,12));
        tablaCanciones.getColumnModel().getColumn(0).setMinWidth(0);
        tablaCanciones.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaCanciones.getColumnModel().getColumn(0).setWidth(0);
        tablaCanciones.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if(e.getClickCount()==2) {
                    btnSelectActionPerformed();
                }
            }
        });
        JLabel lblHint=new JLabel("Doble clic en una fila = Select",JLabel.CENTER);
        lblHint.setFont(new Font("Arial",Font.ITALIC,10));
        lblHint.setForeground(new Color(120,120,120));
        JScrollPane scroll=new JScrollPane(tablaCanciones);
        JPanel panelBotones=new JPanel(new FlowLayout(FlowLayout.CENTER,6,6));
        panelBotones.setBackground(new Color(230,230,230));
        btnAdd=new JButton("Add");
        btnSelect=new JButton("Select");
        btnPlay=new JButton("Play");
        btnPause=new JButton("Pause");
        btnStop=new JButton("Stop");
        btnRemove=new JButton("Remove");
        Font fBtn=new Font("Arial",Font.PLAIN,12);
        for(JButton b : new JButton[]{btnAdd,btnSelect,btnPlay,btnPause,btnStop,btnRemove}) {
            b.setFont(fBtn);
        }
        btnAdd.addActionListener(e -> btnAddActionPerformed());
        btnSelect.addActionListener(e -> btnSelectActionPerformed());
        btnPlay.addActionListener(e -> btnPlayActionPerformed());
        btnPause.addActionListener(e -> btnPauseActionPerformed());
        btnStop.addActionListener(e -> btnStopActionPerformed());
        btnRemove.addActionListener(e -> btnRemoveActionPerformed());
        panelBotones.add(btnAdd);
        panelBotones.add(btnSelect);
        panelBotones.add(btnPlay);
        panelBotones.add(btnPause);
        panelBotones.add(btnStop);
        panelBotones.add(btnRemove);
        JPanel panelCentro=new JPanel(new BorderLayout(4,4));
        panelCentro.setBackground(new Color(230,230,230));
        panelCentro.add(lblHint,BorderLayout.NORTH);
        panelCentro.add(scroll,BorderLayout.CENTER);
        panelCentro.add(panelBotones,BorderLayout.SOUTH);
        add(panelIzquierdo,BorderLayout.WEST);
        add(panelCentro,BorderLayout.CENTER);
    }
    private void btnSelectActionPerformed() {
        int fila=tablaCanciones.getSelectedRow();
        if(fila<0) {
            JOptionPane.showMessageDialog(this,"Primero haz clic en una fila de la lista para marcarla.","Sin selección",JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int id=(int)modeloTabla.getValueAt(fila,0);
        try {
            Cancion c=archivoCanciones.buscarPorId(id);
            if(c==null) {
                JOptionPane.showMessageDialog(this,"No se encontró la canción en el archivo.","Error",JOptionPane.ERROR_MESSAGE);
                return;
            }
            cancionSeleccionada=c;
            lblNombre.setText(c.getNombre());
            lblArtista.setText(c.getArtista());
            lblDuracion.setText(c.getDuracionFormateada());
            mostrarImagen(c.getRutaImagen());
            JOptionPane.showMessageDialog(this,
                "Canción seleccionada:\n"+c.getNombre()+" - "+c.getArtista()+"\nDuración: "+c.getDuracionFormateada()+"\nPresiona Play para reproducir.",
                "Canción cargada",
                JOptionPane.INFORMATION_MESSAGE);
        } catch(IOException ex) {
            JOptionPane.showMessageDialog(this,"Error al leer la canción: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
        }
    }
    private void btnPlayActionPerformed() {
        if(cancionSeleccionada==null) {
            JOptionPane.showMessageDialog(this,"Selecciona una canción primero con el botón Select.");
            return;
        }
        File f=resolverRutaMP3(cancionSeleccionada.getRutaMP3());
        if(!f.exists()) {
            JOptionPane.showMessageDialog(this,"Archivo MP3 no encontrado:\n"+f.getAbsolutePath()+"\n\nAsegúrate de que el archivo esté en la carpeta 'canciones' del proyecto.");
            return;
        }
        reproductor.play(f.getAbsolutePath());
        btnPause.setText("Pause");
    }
    private void btnPauseActionPerformed() {
        reproductor.pausarReanudar();
        btnPause.setText(reproductor.estaPausado()?"Resume":"Pause");
    }
    private void btnStopActionPerformed() {
        reproductor.stop();
        btnPause.setText("Pause");
    }
    private void btnAddActionPerformed() {
        JFileChooser chooserMP3=new JFileChooser();
        chooserMP3.setDialogTitle("Seleccionar archivo MP3");
        chooserMP3.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivos MP3","mp3"));
        if(chooserMP3.showOpenDialog(this)!=JFileChooser.APPROVE_OPTION) return;
        String rutaOriginal=chooserMP3.getSelectedFile().getAbsolutePath();
        String rutaRelativa;
        try {
            rutaRelativa=copiarMP3AlProyecto(rutaOriginal);
        } catch(IOException ex) {
            JOptionPane.showMessageDialog(this,"No se pudo copiar el MP3 al proyecto:\n"+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            return;
        }
        mostrarFormularioAgregar(rutaOriginal,rutaRelativa);
    }
    private void mostrarFormularioAgregar(String rutaOriginal,String rutaRelativa) {
        int duracion=UtilMP3.obtenerDuracion(rutaOriginal);
        String nombreTag=UtilMP3.obtenerNombreSugerido(rutaOriginal);
        String artistaTag=UtilMP3.obtenerArtista(rutaOriginal);
        String generoTag=UtilMP3.obtenerGenero(rutaOriginal);
        String[] desdArchivo=UtilMP3.extraerDesdNombreArchivo(rutaOriginal);
        String nombreInicial=!nombreTag.isEmpty()?nombreTag:(!desdArchivo[1].isEmpty()?desdArchivo[1]:"");
        String artistaInicial=!artistaTag.isEmpty()?artistaTag:desdArchivo[0];
        String generoInicial=!generoTag.isEmpty()?generoTag:desdArchivo[2];
        JDialog dialogo=new JDialog(this,"Agregar Canción",true);
        dialogo.setSize(430,400);
        dialogo.setLocationRelativeTo(this);
        dialogo.setResizable(false);
        dialogo.setLayout(new BorderLayout(8,8));
        JPanel panelCampos=new JPanel(new GridBagLayout());
        panelCampos.setBorder(BorderFactory.createEmptyBorder(10,12,4,12));
        panelCampos.setBackground(new Color(240,240,240));
        GridBagConstraints gbc=new GridBagConstraints();
        gbc.insets=new Insets(4,4,4,4);
        gbc.fill=GridBagConstraints.HORIZONTAL;
        Font fLabel=new Font("Arial",Font.PLAIN,12);
        JLabel lArchivo=new JLabel("Archivo MP3:");
        lArchivo.setFont(fLabel);
        JLabel valArchivo=new JLabel(new File(rutaOriginal).getName());
        valArchivo.setFont(new Font("Arial",Font.ITALIC,11));
        valArchivo.setForeground(new Color(80,80,80));
        JLabel lCopiar=new JLabel("Guardado en:");
        lCopiar.setFont(fLabel);
        JLabel valCopiar=new JLabel(rutaRelativa);
        valCopiar.setFont(new Font("Arial",Font.ITALIC,11));
        valCopiar.setForeground(new Color(60,130,60));
        JLabel lDuracion=new JLabel("Duración:");
        lDuracion.setFont(fLabel);
        JLabel valDuracion=new JLabel(formatearDuracion(duracion));
        valDuracion.setFont(new Font("Arial",Font.ITALIC,11));
        valDuracion.setForeground(new Color(80,80,80));
        JLabel lNombre=new JLabel("Nombre:");
        lNombre.setFont(fLabel);
        JTextField txtNombre=new JTextField(nombreInicial,20);
        txtNombre.setFont(fLabel);
        JLabel lArtista=new JLabel("Artista:");
        lArtista.setFont(fLabel);
        JTextField txtArtista=new JTextField(artistaInicial,20);
        txtArtista.setFont(fLabel);
        JLabel lGenero=new JLabel("Género:");
        lGenero.setFont(fLabel);
        JTextField txtGenero=new JTextField(generoInicial,20);
        txtGenero.setFont(fLabel);
        JLabel lImagen=new JLabel("Imagen (opcional):");
        lImagen.setFont(fLabel);
        JTextField txtImagen=new JTextField("",20);
        txtImagen.setFont(new Font("Arial",Font.ITALIC,11));
        txtImagen.setEditable(false);
        txtImagen.setForeground(new Color(100,100,100));
        JButton btnElegirImagen=new JButton("Elegir...");
        btnElegirImagen.setFont(new Font("Arial",Font.PLAIN,11));
        final String[] rutaImagenFinal={""};
        btnElegirImagen.addActionListener(e -> {
            JFileChooser chooserImg=new JFileChooser();
            chooserImg.setDialogTitle("Seleccionar imagen");
            chooserImg.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Imágenes (jpg, jpeg, png, gif)","jpg","jpeg","png","gif"));
            if(chooserImg.showOpenDialog(dialogo)==JFileChooser.APPROVE_OPTION) {
                String ruta=chooserImg.getSelectedFile().getAbsolutePath();
                if(esImagenValida(ruta)) {
                    rutaImagenFinal[0]=ruta;
                    txtImagen.setText(chooserImg.getSelectedFile().getName());
                } else {
                    JOptionPane.showMessageDialog(dialogo,"El archivo no es una imagen válida.\nSolo se permiten jpg, jpeg, png, gif.","Imagen inválida",JOptionPane.WARNING_MESSAGE);
                    rutaImagenFinal[0]="";
                    txtImagen.setText("");
                }
            }
        });
        JPanel panelImagen=new JPanel(new BorderLayout(4,0));
        panelImagen.setBackground(new Color(240,240,240));
        panelImagen.add(txtImagen,BorderLayout.CENTER);
        panelImagen.add(btnElegirImagen,BorderLayout.EAST);
        gbc.gridx=0; gbc.gridy=0; gbc.weightx=0.35;
        panelCampos.add(lArchivo,gbc);
        gbc.gridx=1; gbc.weightx=0.65;
        panelCampos.add(valArchivo,gbc);
        gbc.gridx=0; gbc.gridy=1; gbc.weightx=0.35;
        panelCampos.add(lCopiar,gbc);
        gbc.gridx=1; gbc.weightx=0.65;
        panelCampos.add(valCopiar,gbc);
        gbc.gridx=0; gbc.gridy=2; gbc.weightx=0.35;
        panelCampos.add(lDuracion,gbc);
        gbc.gridx=1; gbc.weightx=0.65;
        panelCampos.add(valDuracion,gbc);
        gbc.gridx=0; gbc.gridy=3; gbc.weightx=0.35;
        panelCampos.add(lNombre,gbc);
        gbc.gridx=1; gbc.weightx=0.65;
        panelCampos.add(txtNombre,gbc);
        gbc.gridx=0; gbc.gridy=4; gbc.weightx=0.35;
        panelCampos.add(lArtista,gbc);
        gbc.gridx=1; gbc.weightx=0.65;
        panelCampos.add(txtArtista,gbc);
        gbc.gridx=0; gbc.gridy=5; gbc.weightx=0.35;
        panelCampos.add(lGenero,gbc);
        gbc.gridx=1; gbc.weightx=0.65;
        panelCampos.add(txtGenero,gbc);
        gbc.gridx=0; gbc.gridy=6; gbc.weightx=0.35;
        panelCampos.add(lImagen,gbc);
        gbc.gridx=1; gbc.weightx=0.65;
        panelCampos.add(panelImagen,gbc);
        JPanel panelBotonesD=new JPanel(new FlowLayout(FlowLayout.RIGHT,8,8));
        panelBotonesD.setBackground(new Color(240,240,240));
        JButton btnAceptar=new JButton("Aceptar");
        JButton btnCancelar=new JButton("Cancelar");
        btnAceptar.setFont(new Font("Arial",Font.PLAIN,12));
        btnCancelar.setFont(new Font("Arial",Font.PLAIN,12));
        panelBotonesD.add(btnAceptar);
        panelBotonesD.add(btnCancelar);
        btnCancelar.addActionListener(e -> dialogo.dispose());
        btnAceptar.addActionListener(e -> {
            String nombre=txtNombre.getText().trim();
            String artista=txtArtista.getText().trim();
            String genero=txtGenero.getText().trim();
            if(nombre.isEmpty()) {
                JOptionPane.showMessageDialog(dialogo,"El nombre de la canción es obligatorio.","Campo requerido",JOptionPane.WARNING_MESSAGE);
                return;
            }
            if(artista.isEmpty()) artista="Desconocido";
            if(genero.isEmpty()) genero="Sin género";
            Cancion nueva=new Cancion(0,nombre,artista,genero,duracion,rutaRelativa,rutaImagenFinal[0],true);
            try {
                archivoCanciones.guardar(nueva);
                refrescarTabla();
                dialogo.dispose();
            } catch(IOException ex) {
                JOptionPane.showMessageDialog(dialogo,"Error al guardar: "+ex.getMessage());
            }
        });
        dialogo.add(panelCampos,BorderLayout.CENTER);
        dialogo.add(panelBotonesD,BorderLayout.SOUTH);
        dialogo.setVisible(true);
    }
    private void btnRemoveActionPerformed() {
        int fila=tablaCanciones.getSelectedRow();
        if(fila<0) {
            JOptionPane.showMessageDialog(this,"Selecciona una canción para eliminar.");
            return;
        }
        int id=(int)modeloTabla.getValueAt(fila,0);
        int confirm=JOptionPane.showConfirmDialog(this,"¿Eliminar esta canción de la lista?","Confirmar",JOptionPane.YES_NO_OPTION);
        if(confirm!=JOptionPane.YES_OPTION) return;
        if(cancionSeleccionada!=null&&cancionSeleccionada.getId()==id) {
            reproductor.stop();
            btnPause.setText("Pause");
            cancionSeleccionada=null;
            lblNombre.setText("Sin selección");
            lblArtista.setText(" ");
            lblDuracion.setText(" ");
            lblImagen.setIcon(imagenDefault);
            lblImagen.setText("");
        }
        try {
            archivoCanciones.eliminarPorId(id);
            refrescarTabla();
        } catch(IOException ex) {
            JOptionPane.showMessageDialog(this,"Error al eliminar: "+ex.getMessage());
        }
    }
    private void refrescarTabla() {
        modeloTabla.setRowCount(0);
        try {
            Cancion[] canciones=archivoCanciones.leerTodos();
            for(Cancion c : canciones) {
                modeloTabla.addRow(new Object[]{
                    c.getId(),
                    c.getNombre(),
                    c.getArtista(),
                    c.getGenero(),
                    c.getDuracionFormateada()
                });
            }
        } catch(IOException ex) {}
    }
    private void mostrarImagen(String ruta) {
        if(ruta!=null&&!ruta.isEmpty()) {
            File f=new File(ruta);
            if(f.exists()) {
                try {
                    java.awt.image.BufferedImage buf=ImageIO.read(f);
                    if(buf!=null) {
                        Image scaled=buf.getScaledInstance(160,160,Image.SCALE_SMOOTH);
                        lblImagen.setIcon(new ImageIcon(scaled));
                        lblImagen.setText("");
                        return;
                    }
                } catch(Exception e) {}
            }
        }
        lblImagen.setIcon(imagenDefault);
        lblImagen.setText("");
    }
    private boolean esImagenValida(String ruta) {
        String lower=ruta.toLowerCase();
        if(!lower.endsWith(".jpg")&&!lower.endsWith(".jpeg")&&!lower.endsWith(".png")&&!lower.endsWith(".gif")) {
            return false;
        }
        try {
            java.awt.image.BufferedImage img=ImageIO.read(new File(ruta));
            return img!=null;
        } catch(IOException e) {
            return false;
        }
    }
    private String formatearDuracion(int segundos) {
        int min=segundos/60;
        int seg=segundos%60;
        return String.format("%d:%02d",min,seg);
    }
}
