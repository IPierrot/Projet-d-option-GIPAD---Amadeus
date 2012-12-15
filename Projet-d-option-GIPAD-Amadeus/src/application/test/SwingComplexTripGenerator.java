package application.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EtchedBorder;

import model.Trip;

/**
 * Application de test avec une UI basique.
 * @author Dim
 *
 */
public class SwingComplexTripGenerator extends JFrame implements ActionListener{
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private Thread solvingThread;
    private JFileChooser fc;
    private JTextArea textArea;
    JScrollPane scroll;
    private JButton open, solve, clean, stop;
    private JTextArea statut;
    private static ComplexTripGenerator ctg;
    
    private static synchronized ComplexTripGenerator getCtg() {
        return ctg;
    }
    
    private static synchronized void setCtg(ComplexTripGenerator c) {
        ctg = c;
    }
    
    /**
     * Constructeur par défaut.
     */
    public SwingComplexTripGenerator() {
        super("Complex Trip Generator 0.1");
        setCtg(null);
      //Parametres de la fenêtre
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocation(50, 50);
        this.setSize(new Dimension(800, 490));
        try {
            this.setIconImage(ImageIO.read(
                    SwingComplexTripGenerator.class.getClassLoader().
                    getResource("ressources/plane.png")));
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        
        JPanel panel = new JPanel(new BorderLayout());
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        fc = new JFileChooser();
        
        
        try {
            UIManager.setLookAndFeel(
                    UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        textArea.setBackground(Color.WHITE);
        JTextAreaOutputStream outStream = new JTextAreaOutputStream(textArea); 
        System.setOut(new PrintStream(outStream)); 

        scroll = new JScrollPane(textArea);
        scroll.setPreferredSize(new Dimension(750, 350));
        JPanel log = new JPanel();
        log.add(scroll);
        
        open = new JButton("load a request");
        open.addActionListener(this);
        solve = new JButton("solve");
        solve.addActionListener(this);
        clean = new JButton("clean");
        clean.addActionListener(this);
        stop = new JButton("stop");
        stop.addActionListener(this);
        stop.setEnabled(false);

        JPanel buttonsPane = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 20, 30));
        JLabel label = new JLabel();
        JPanel labPane = new JPanel();
        Icon icon = new ImageIcon (
                SwingComplexTripGenerator.class.getClassLoader().
                getResource("ressources/plane.png"));
        label.setIcon(icon);
        label.setPreferredSize(new Dimension((int) (icon.getIconWidth()),
                (int) (icon.getIconHeight())));
        labPane.add(label);
        buttonsPane.add(open);
        buttonsPane.add(solve);
        buttonsPane.add(clean);
        buttonsPane.add(stop);
        buttonsPane.setPreferredSize(new Dimension(550, 80));
        buttonsPane.setAlignmentY(CENTER_ALIGNMENT);
      
        JPanel pane = new JPanel(new FlowLayout());
        pane.add(labPane);
        pane.add(buttonsPane);
        
        panel.add(pane, BorderLayout.NORTH);
        panel.add(log, BorderLayout.CENTER);
        
        this.add(panel);
        
       
    }


    @Override
    public void actionPerformed(final ActionEvent e) {
        
        if (e.getSource() == open) {
            int returnVal = fc.showOpenDialog(this);
    
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                new Thread() {
                    public void run() {
                        solve.setEnabled(false);
                        setCtg(new ComplexTripGenerator(fc.getSelectedFile()));
                        solve.setEnabled(true);
                    }
                }.start();
            }
            
        } else if (e.getSource() == solve) {
            if (getCtg() != null) {
                solvingThread = new Thread() {
                    public void run() {
                        open.setEnabled(false);
                        solve.setEnabled(false);
                        stop.setEnabled(true);
                        Trip t = getCtg().tryToSolve();
                        if (t != null) {
                            System.out.println(t);
                        } else {
                            System.out.println("Pas de solution trouvée");
                        }
                        open.setEnabled(true);
                        solve.setEnabled(true);
                        stop.setEnabled(false);
                    }
                };
                solvingThread.start();
            }
        } else if (e.getSource() == clean) {
            textArea.setText("");
        } else if (e.getSource() == stop) {
            solvingThread.interrupt();
            System.out.println("Résolution interrompue, veuillez recharger une requête");
            stop.setEnabled(false);
            solve.setEnabled(false);
            open.setEnabled(true);
        }
    }
    
    /**
     * Main
     * @param args arguments
     */
    public static void main(final String[] args) {
        SwingComplexTripGenerator sw = new SwingComplexTripGenerator();
        sw.setVisible(true);
    }
    
    /**
     * Un OutputStream vers un JTextArea. Utile pour redéfinir System.out
     * et consorts vers un JTextArea.
     * @see javax.swing.JTextArea
     * @see java.io.OutputStream
     * @author Glob
     * @version 0.2
     */
    private class JTextAreaOutputStream extends OutputStream {
       private JTextArea m_textArea = null;

       /**
        * Method JTextAreaOutputStream.
        * @param aTextArea le JTextArea qui recevra les caractères.
        */
       public JTextAreaOutputStream(JTextArea aTextArea) {
          m_textArea = aTextArea;
       }

       /**
        * Écrit un caractère dans le JTextArea.
        * Si le caractère est un retour chariot, scrolling.
        * @see java.io.OutputStream#write(int)
        */
       public void write(int b) throws IOException {
          byte[] bytes = new byte[1];
          bytes[0] = (byte)b;
          String newText = new String(bytes);
          m_textArea.append(newText);
          if (newText.indexOf('\n') > -1) {
             try {
                m_textArea.scrollRectToVisible(m_textArea.modelToView(
                            m_textArea.getDocument().getLength()));
             } catch (javax.swing.text.BadLocationException err) {
                err.printStackTrace();
             }
          }
       }

       /**
        * Écrit un tableau de bytes dans le JTextArea.
        * Scrolling du JTextArea à la fin du texte ajouté.
        * @see java.io.OutputStream#write(byte[])
        */
       public final void write(byte[] arg0) throws IOException {
          String txt = new String(arg0);
          m_textArea.append(txt);
          try {
             m_textArea.scrollRectToVisible(
                     m_textArea.modelToView(m_textArea.getDocument().getLength()));
          } catch (javax.swing.text.BadLocationException err) {
             err.printStackTrace();
          }
       }
    }
}
