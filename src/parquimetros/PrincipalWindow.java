package parquimetros;

import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JDesktopPane;
import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.ButtonGroup;
import javax.swing.JButton;

public class PrincipalWindow {

	private JFrame frame;
	private JDesktopPane desktopPane;
	private VentanaConsultas consultasAdmin;
	private final String usuarioAdmin="ADMIN";
	private final String usuarioInspector="INSPECTOR";
	protected Connection conexionBD = null;

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
		
				
		
		JButton btnInspector = new JButton("LOGIN");
		btnInspector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mostrarPanelLogin(frame);
			}
		});
		btnInspector.setBounds(454, 236, 149, 67);
		desktopPane.add(btnInspector);
		
	
	}
	
	
	private void mniConsultasActionPerformed(){
		this.consultasAdmin = new VentanaConsultas(conexionBD);
	    this.consultasAdmin.setVisible(false);
	    this.desktopPane.add(this.consultasAdmin);
	    try{
	        this.consultasAdmin.setMaximum(true);
	    }
	    catch (PropertyVetoException e) {}
	    this.consultasAdmin.setVisible(true);      
	   }
	
	public void mostrarPanelLogin(JFrame frame) {
	    Hashtable<String, String> logininformation = new Hashtable<String, String>();
	    final JLabel usuarioLabel =new JLabel("Legajo", SwingConstants.RIGHT);
	    
	    
	    JPanel controls = new JPanel(new GridLayout(0, 1, 2, 2));
	    final JTextField username = new JTextField();
	    controls.add(username);
	    JPasswordField password = new JPasswordField();
	    controls.add(password);
	    
	    JPanel panel = new JPanel(new BorderLayout(5, 5));
	    JPanel tipoUsuario = new JPanel(new GridLayout(0, 1, 2, 2));
	    
	    JRadioButton botonAdmin = new JRadioButton(usuarioAdmin);
	    botonAdmin.setActionCommand(usuarioAdmin);
	    

	    JRadioButton botonUsuario = new JRadioButton(usuarioInspector);
	    botonUsuario.setActionCommand(usuarioInspector);
	    botonUsuario.setSelected(true);

	    //Group the radio buttons.
	    ButtonGroup group = new ButtonGroup();
	    group.add(botonUsuario);
	    group.add(botonAdmin);

	    //Register a listener for the radio buttons.
	    botonUsuario.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				username.setText("");
				username.setEditable(true);
				usuarioLabel.setText("Legajo");
			}
	    	
	    });
	    botonAdmin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				username.setText("admin");
				username.setEditable(false);
				usuarioLabel.setText("");
			}	
	    	
	    });
	    
	    tipoUsuario.add(botonUsuario);
	    tipoUsuario.add(botonAdmin);

	    JPanel label = new JPanel(new GridLayout(0, 1, 2, 2));
	    label.add(usuarioLabel);
	    label.add(new JLabel("Password", SwingConstants.RIGHT));
	   

	  
	    panel.add(tipoUsuario,BorderLayout.NORTH);
	    panel.add(label, BorderLayout.WEST);
	    panel.add(controls, BorderLayout.CENTER);
	    
	  
	    
	    int resultado= JOptionPane.showOptionDialog(frame, panel, "login", 
	    		JOptionPane.YES_NO_OPTION,
	    		JOptionPane.QUESTION_MESSAGE,
	    		null,	//icono predeterminado
	    		new Object[] {"Login","Cancelar"},
	    		"Login"
	    		);

	    if(resultado == JOptionPane.YES_OPTION) {
	    	conectarBD(username.getText(),new String(password.getPassword()));
	    	if(conexionBD!=null) {
	    		if(botonAdmin.isSelected()) {
	    			mniConsultasActionPerformed();
	    		}
	    		else {
	    			//ACA IRIA LA VENTANA 
	    			JOptionPane.showMessageDialog(frame, "se ingreso correctamente");
	    		}
	    	}
	    }
	    
	    logininformation.put("user", username.getText());
	    logininformation.put("pass", new String(password.getPassword()));
	    
	    
	    
	}
	
	private void conectarBD(String usuario,String clave)
	   {
	      if (this.conexionBD == null)
	      {             
	         try
	         {  //se genera el string que define los datos de la conexión 
	            String servidor = "localhost:3306";
	            String baseDatos = "parquimetros";
	            String uriConexion = "jdbc:mysql://" + servidor + "/" + baseDatos + 
	            		          "?serverTimezone=America/Argentina/Buenos_Aires";
	            //se intenta establecer la conexión
	            this.conexionBD = DriverManager.getConnection(uriConexion, usuario, clave);
	         }
	         catch (SQLException ex)
	         {
	            JOptionPane.showMessageDialog(frame,
	                        "Se produjo un error al intentar conectarse a la base de datos.\n" + 
	                         ex.getMessage(),
	                         "Error",
	                         JOptionPane.ERROR_MESSAGE);
	            System.out.println("SQLException: " + ex.getMessage());
	            System.out.println("SQLState: " + ex.getSQLState());
	            System.out.println("VendorError: " + ex.getErrorCode());
	         }
	      }
	   }

	   private void desconectarBD()
	   {
	      if (this.conexionBD != null)
	      {
	         try
	         {
	            this.conexionBD.close();
	            this.conexionBD = null;
	         }
	         catch (SQLException ex)
	         {
	            System.out.println("SQLException: " + ex.getMessage());
	            System.out.println("SQLState: " + ex.getSQLState());
	            System.out.println("VendorError: " + ex.getErrorCode());
	         }
	      }
	   }
	   
}
