package parquimetros;

import java.awt.EventQueue; 
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JDesktopPane;
import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JButton;

public class PrincipalWindow {

	private JFrame frame;
	private JDesktopPane desktopPane;
	private VentanaConsultas consultasAdmin;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PrincipalWindow window = new PrincipalWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public PrincipalWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 850, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		desktopPane = new JDesktopPane();
		desktopPane.setPreferredSize(new Dimension(800,600));
		frame.getContentPane().add(desktopPane,BorderLayout.CENTER);
		
		JButton btnAdmin = new JButton("Admin");
		btnAdmin.setBounds(259, 236, 149, 67);
		desktopPane.add(btnAdmin);
		
		this.consultasAdmin = new VentanaConsultas();
	    this.consultasAdmin.setVisible(false);
	    this.desktopPane.add(this.consultasAdmin);
		
		btnAdmin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                                
            	mniConsultasActionPerformed(evt);
                /*JFrame insertpass= new JFrame();
                insertpass.setBounds(500, 250, 200, 200);
                insertpass.setPreferredSize(new Dimension(300,300));
                insertpass.setVisible(true);
                
                javax.swing.JLabel firstText= new javax.swing.JLabel("Ingrese su contraseña");
                insertpass.add(firstText,BorderLayout.NORTH);
                
                javax.swing.JTextField texto= new javax.swing.JTextField();
                texto.setText("Contraseña");
                texto.setVisible(true);
                insertpass.add(texto,BorderLayout.CENTER);
                
                
                JButton confirmar = new JButton("Confirmar");
                insertpass.add(confirmar,BorderLayout.SOUTH);
                */
             }
          });
		
		
		
		JButton btnInspector = new JButton("Inspector");
		btnInspector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnInspector.setBounds(454, 236, 149, 67);
		desktopPane.add(btnInspector);
		
	
	}
	
	
	private void mniConsultasActionPerformed(ActionEvent evt) 
	   {
	      try
	      {
	         this.consultasAdmin.setMaximum(true);
	      }
	      catch (PropertyVetoException e) {}
	      this.consultasAdmin.setVisible(true);      
	   }
}
