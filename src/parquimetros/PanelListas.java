package parquimetros;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;

public class PanelListas extends JPanel {

	private JList<Object> listaTablas;
	private JList<Object> listaAtributos;
	private JScrollPane jspListaTablas;
	private JScrollPane jspListaAtributos;
	private JLabel labelTablas;
	private JLabel labelAtributos;
	protected Connection conexionBD = null;
	
	
	/**
	 * Create the panel.
	 */
	public PanelListas() {
		this.setPreferredSize(new Dimension(400,200));
		setLayout(new GridLayout(2, 4, 0, 0));
		
		conectarBD();
        initSCVPTablas();
        initSCVPAtributos();
	}
	
	void initSCVPTablas(){
		labelTablas = new JLabel("Lista de tablas");
		add(labelTablas);
		labelTablas.setHorizontalAlignment((int) CENTER_ALIGNMENT);
		ArrayList <String> listaT= new ArrayList<String>();
		
		try {
		Statement st= conexionBD.createStatement();
		ResultSet rs = st.executeQuery("show tables");
		
		while (rs.next()) {
			listaT.add( rs.getString(1));
		}
		st.close();
		rs.close();
		}catch (SQLException ex)
	      {
	         System.out.println("SQLException: " + ex.getMessage());
	         System.out.println("SQLState: " + ex.getSQLState());
	         System.out.println("VendorError: " + ex.getErrorCode());
	      }
		
		int i=0;
		String tablas[] = new String[listaT.size()];
		for (String t: listaT) {
			tablas[i]=t;
			i++;
		}
		
          
        jspListaTablas= new JScrollPane();
		//create list 
        listaTablas= new JList<Object>(tablas); 
        listaTablas.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				refrescarAtributos();
			}
        	
        }); 
        jspListaTablas.setViewportView(listaTablas);
        add(jspListaTablas);
        
		
	}
	
	void initSCVPAtributos() {
		labelAtributos = new JLabel("Lista de atributos");
		add(labelAtributos);
		labelAtributos.setHorizontalAlignment((int) CENTER_ALIGNMENT);
		
		//String array to store weekdays 
        String atributos[]= {}; 
        
        jspListaAtributos= new JScrollPane();
        //create list 
        listaAtributos= new JList<Object>(atributos); 
        jspListaAtributos.setViewportView(listaAtributos);
        add(jspListaAtributos);
	}

	private void conectarBD()
	   {
	      if (this.conexionBD == null)
	      {             
	         try
	         {  //se genera el string que define los datos de la conexión 
	            String servidor = "localhost:3306";
	            String baseDatos = "parquimetros";
	            String usuario = "admin";
	            String clave = "admin";
	            String uriConexion = "jdbc:mysql://" + servidor + "/" + baseDatos + 
	            		          "?serverTimezone=America/Argentina/Buenos_Aires";
	            //se intenta establecer la conexión
	            this.conexionBD = DriverManager.getConnection(uriConexion, usuario, clave);
	         }
	         catch (SQLException ex)
	         {
	            JOptionPane.showMessageDialog(this,
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
	   
	   private void refrescarAtributos() {
		   int i=0;
		   ArrayList<String> listaA= new ArrayList<String>();
		   try {
			   Statement st= conexionBD.createStatement();
			   ResultSet rs= st.executeQuery("Describe "+listaTablas.getSelectedValue());
			   
			   while (rs.next()) {
				   listaA.add(rs.getString(1));
			   }
		   }catch(SQLException ex)
			   {
		            System.out.println("SQLException: " + ex.getMessage());
		            System.out.println("SQLState: " + ex.getSQLState());
		            System.out.println("VendorError: " + ex.getErrorCode());
		        }
		   String listData[] = new String[listaA.size()];
		   for (String s:listaA) {
			   listData[i]=listaA.get(i);
			   i++;
		   }
		   listaAtributos.setListData(listData);
	   }
}
