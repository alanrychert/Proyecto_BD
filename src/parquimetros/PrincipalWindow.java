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

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
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

@SuppressWarnings("serial")
public class PrincipalWindow {

	private JFrame frame;
	private JDesktopPane desktopPane;
	private JInternalFrame ventanaPrincipal;
	
	protected Connection conexionBD = null;
	private JMenuBar jMenuBar1;
	private JMenu mnuOpciones;
	private JMenuItem mniLogin;
	private JMenuItem mniLogout;
	
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
		

		jMenuBar1 = new JMenuBar();
        frame.setJMenuBar(jMenuBar1);
        
        mnuOpciones = new JMenu();
        jMenuBar1.add(mnuOpciones);
        mnuOpciones.setText("Opciones");
        
        mniLogin = new JMenuItem();
        mnuOpciones.add(mniLogin);
        mniLogin.setText("Iniciar Sesion");
        mniLogin.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
        	mostrarPanelLogin(frame);
        	}
        });
        
        mniLogout = new JMenuItem();
        mnuOpciones.add(mniLogout);
        mniLogout.setText("Salir");
        mniLogout.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
        		mniSalirActionPerformed(evt);
        	}
        });
	}
	
	private void mniSalirActionPerformed(ActionEvent evt) 
	   {
		  frame.setVisible(false);
	      frame.dispose();
	   }
	
	
	
	private void cargarVentanaPrincipal(JInternalFrame ventana){
		this.ventanaPrincipal = ventana;
	    this.desktopPane.add(this.ventanaPrincipal);
	    try{
	        this.ventanaPrincipal.setMaximum(true);
	    }
	    catch (PropertyVetoException e) {}
	    this.ventanaPrincipal.setVisible(true);      
	   }
	
	public void mostrarPanelLogin(JFrame frame) {
	    final JLabel usuarioLabel =new JLabel("Legajo", SwingConstants.RIGHT);
	    
	    
	    JPanel controls = new JPanel(new GridLayout(0, 1, 2, 2));
	    final JTextField username = new JTextField();
	    controls.add(username);
	    JPasswordField password = new JPasswordField();
	    controls.add(password);
	    
	    JPanel panel = new JPanel(new BorderLayout(5, 5));
	    JPanel tipoUsuario = new JPanel(new GridLayout(0, 1, 2, 2));
	    
	    JRadioButton botonAdmin = new JRadioButton("Administrador");
	    
	    JRadioButton botonUsuario = new JRadioButton("Inspector");
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
	    
	  
	    
	    int resultado= JOptionPane.showOptionDialog(frame, panel, "Iniciar Sesion", 
	    		JOptionPane.YES_NO_OPTION,
	    		JOptionPane.QUESTION_MESSAGE,
	    		null,	//icono predeterminado
	    		new Object[] {"Login","Cancelar"},
	    		"Login"
	    		);

	    if(resultado == JOptionPane.YES_OPTION) {	    
	    	if(botonAdmin.isSelected()) {
	    		conectarBD(username.getText(),new String(password.getPassword()));
	    		if(conexionBD!=null) {
	    			cargarVentanaPrincipal(new VentanaConsultas(conexionBD));
	    		}
	    	}
	    	else {
	    		conectarBD("inspector","inspector");
	    		//PREGUNTAR SI ESTA BIEN QUE ESTO ESTE HARDCODEADO ACA
	    		
	    		try {
					if(conexionBD.isValid(5)) {
						Statement st= conexionBD.createStatement();
						ResultSet rs = st.executeQuery(
								" select * from inspectores where legajo="+username.getText()+
								" and password=md5('"+new String(password.getPassword())+"');");
						if(rs.next()) {
							//ACA IRIA LA VENTANA 
							int legajo = Integer.parseInt(username.getText());
							cargarVentanaPrincipal(new VentanaInspector(conexionBD,legajo));
				   			JOptionPane.showMessageDialog(frame, "se ingreso correctamente");					
						}
						else
						{
							JOptionPane.showMessageDialog(frame,
			                       "Usuario o contraseña incorrecto",
					                        "Error",
					                        JOptionPane.ERROR_MESSAGE);
						}
					}				
				} catch (SQLException ex) {
					JOptionPane.showMessageDialog(frame,
		                       "Usuario o contraseña incorrecto" + 
		                        ex.getMessage(),
		                        "Error",
		                        JOptionPane.ERROR_MESSAGE);
				}
	    		
	   		}
	    	conexionBD=null; //pongo la conexión en nulo porque no la va a usar hasta que intente iniciar denuevo, donde crea una nueva.
	   	}
  
	    
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

	  
	   
}
