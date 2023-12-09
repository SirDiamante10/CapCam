package team.Grapes;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.videoio.VideoCapture;
import org.opencv.core.Core;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgcodecs.Imgcodecs;

public class CamaraCaptura extends javax.swing.JFrame {

    private DaemonThread myThread = null;
    int count = 0;

    Mat frame = new Mat();
    Mat area;
    Mat cara;
    Mat circulos = new Mat();

    MatOfByte mem = new MatOfByte();

    CascadeClassifier detectorCaras;  // Objeto reconocedor de imagenes
    CascadeClassifier detectorOjo;   // Objeto para reconocer ojos

    MatOfRect deteccionCaras;   // Matriz donde se alojarán las caras detectadas
    MatOfRect deteccionOjos;   // Matriz donde se alojarán los ojos detectadas

    String lugarFile = "";
    String base = "./data/haarcascades/";   // Carpeta donde se localizan los modelos de reconocimiento
    String caraFile = "haarcascade_frontalface_alt.xml";  // Archivo referencia para reconocimiento de rostros
    String ojoFile = "haarcascade_eye.xml";  // Archivo referencia para reconocimiento de rostros

    class DaemonThread implements Runnable {

        protected volatile boolean runnable = false;
        VideoCapture webSource = null;
        String source = "";
        
        DaemonThread(String _source){
            source = _source;
        }
        
        @Override
        public void run() {
            webSource = new VideoCapture();
            
            if (source.equals("Desde WebCam")) { 
                webSource.open(0);
            } else {
                webSource.open(lugarFile);
            }            
            
            detectorCaras = new CascadeClassifier(base + caraFile); // Se crea un objeto CascadeClassifier que reconocera caras
            deteccionCaras = new MatOfRect(); // Se inicializa el objeto donde se guardaran las caras detectadas

            detectorOjo = new CascadeClassifier(base + ojoFile); // Se crea un objeto CascadeClassifier que reconocera caras
            deteccionOjos = new MatOfRect(); // Se inicializa el objeto donde se guardaran las caras detectadas

            synchronized (this) {
                while (runnable) {
                    if (webSource.grab()) {
                        try {
                            webSource.retrieve(frame); // Se obtiene un recuadro de la camara

                            detectorCaras.detectMultiScale(frame, deteccionCaras); // Se buscan las caras dentro de la imagen y se guardan en faceDetections
                            
                            int i= 0;
                            
                            for (Rect rect : deteccionCaras.toArray()) {
                                
                                // Se crea un cuadrito verde por cada cara detectada
                                Imgproc.rectangle(frame, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                                        new Scalar(0, 255, 0));

                               
                                cara = frame.submat(deteccionCaras.toArray()[i]);
                                
                                
                                detectorOjo.detectMultiScale(cara, deteccionOjos);

                                if (deteccionOjos.toArray().length != 0) {

                                    System.out.println("Cara con " + deteccionOjos.toArray().length + " ojos");

                                    for (int j = 0; j < deteccionOjos.toArray().length; j++) {
                                        
                                        Rect rect2 = deteccionOjos.toArray()[j];

                                        Imgproc.rectangle(frame, new Point(rect2.x, rect2.y), new Point(rect2.x + rect2.width, rect2.y + rect2.height),
                                        new Scalar(0, 255, 0));
                                        
                                        Mat eye = cara.submat(deteccionOjos.toArray()[j]);

                                    }
                                }

                            }

                            // Se codifica la imagen frame a un arreglo de memoria
                            Imgcodecs.imencode(".bmp", frame, mem);

                            // Se convierte el arreglo de bytes de la imagen a un objeto de la clase Image
                            Image im = ImageIO.read(new ByteArrayInputStream(mem.toArray()));

                            // Se despliega en el Panel
                            BufferedImage buff = (BufferedImage) im;
                            Graphics g = panel1.getGraphics();

                            if (g.drawImage(buff, 0, 0, getWidth(), getHeight() - 150, 0, 0, buff.getWidth(), buff.getHeight(), null)) {
                                if (runnable == false) {
                                    System.out.println("En metodo wait()");
                                    this.wait();
                                }
                            }
                        } catch (Exception ex) {
                            System.out.println("Error");
                        }
                    }
                }
            }
            
            webSource.release();
        }
    }

    public CamaraCaptura() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        boton1 = new javax.swing.JButton();
        boton2 = new javax.swing.JButton();
        panel1 = new javax.swing.JPanel();
        panel2 = new javax.swing.JPanel();
        jComboBox1 = new javax.swing.JComboBox();
        boton3 = new javax.swing.JButton();
        etiqueta1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Captura de Video");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        boton1.setFont(new java.awt.Font("Cambria", 0, 18)); // NOI18N
        boton1.setText("Iniciar");
        boton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boton1ActionPerformed(evt);
            }
        });

        boton2.setFont(new java.awt.Font("Cambria", 0, 18)); // NOI18N
        boton2.setText("Detener");
        boton2.setEnabled(false);
        boton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boton2ActionPerformed(evt);
            }
        });

        panel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panel1.setPreferredSize(new java.awt.Dimension(320, 240));

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 369, Short.MAX_VALUE)
        );

        panel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panel2.setAlignmentX(0.1F);
        panel2.setAlignmentY(0.1F);

        jComboBox1.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Desde WebCam", "Desde Archivo" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        boton3.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        boton3.setText("...");
        boton3.setEnabled(false);
        boton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boton3ActionPerformed(evt);
            }
        });

        etiqueta1.setFont(new java.awt.Font("Calibri", 0, 16)); // NOI18N
        etiqueta1.setText("Metodo de Captura");

        javax.swing.GroupLayout panel2Layout = new javax.swing.GroupLayout(panel2);
        panel2.setLayout(panel2Layout);
        panel2Layout.setHorizontalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel2Layout.createSequentialGroup()
                .addContainerGap(356, Short.MAX_VALUE)
                .addComponent(etiqueta1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(boton3, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        panel2Layout.setVerticalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(etiqueta1)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(boton3))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(panel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(boton1, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(boton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(panel1, javax.swing.GroupLayout.DEFAULT_SIZE, 763, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(panel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel1, javax.swing.GroupLayout.DEFAULT_SIZE, 373, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(boton1)
                    .addComponent(boton2))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void boton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boton1ActionPerformed

        if ((boton1.getText()).equals("Iniciar")) {

            myThread = new DaemonThread(jComboBox1.getSelectedItem().toString());
            Thread t = new Thread(myThread);
            t.setDaemon(true);
            myThread.runnable = true;
            t.start();

            boton1.setEnabled(false);
            boton2.setEnabled(true);
            jComboBox1.setEnabled(false);
        }
    }//GEN-LAST:event_boton1ActionPerformed

    private void boton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boton2ActionPerformed

        if ((boton2.getText()).equals("Detener")) {
            myThread.runnable = false;
            boton2.setEnabled(false);
            boton1.setEnabled(true);
            jComboBox1.setEnabled(true);
        }
    }//GEN-LAST:event_boton2ActionPerformed

    private void boton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boton3ActionPerformed
        // TODO add your handling code here:
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("AVI", "avi");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            lugarFile = chooser.getSelectedFile().getPath();
            boton1.setEnabled(true);
        } else {
            lugarFile = "";
        }
    }//GEN-LAST:event_boton3ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
        if (jComboBox1.getSelectedItem().equals("Desde Archivo")) {
            boton3.setEnabled(true);
            boton1.setEnabled(false);
        } else {
            boton3.setEnabled(false);
            boton1.setEnabled(true);
        }
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:


    }//GEN-LAST:event_formWindowClosed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        if (myThread == null) {
        } else if (myThread.runnable) {
            myThread.runnable = false;
        }
    }//GEN-LAST:event_formWindowClosing

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        //System.loadLibrary("OpenCV");
        java.awt.EventQueue.invokeLater(() -> {
            new CamaraCaptura().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton boton1;
    private javax.swing.JButton boton2;
    private javax.swing.JButton boton3;
    private javax.swing.JLabel etiqueta1;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JPanel panel1;
    private javax.swing.JPanel panel2;
    // End of variables declaration//GEN-END:variables

}
