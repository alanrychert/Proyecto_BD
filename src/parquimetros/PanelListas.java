package parquimetros;

import javax.swing.JPanel;

import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JList;

@SuppressWarnings("serial")
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
	public PanelListas(Connection conexion) {
		this.setPreferredSize(new Dimension(400,200));
		setLayout(new GridLayout(2, 4, 0, 0));
		
		conexionBD = conexion;
        initSCVPTablas();
        initSCVPAtributos();
	}
	
	void initSCVPTablas(){
		labelTablas = new JLabel("Lista de tablas");
		add(labelTablas);
		labelTablas.setHorizontalAlignment((int) CENTER_ALIGNMENT);
		ArrayList <String> listaT= new ArrayList<String>();
		
		try {
		while(!conexionBD.isValid(1)) {}
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
		
          
		
        jspListaTablas= new JScrollPane();
		//create list 
        listaTablas= new JList<Object>(listaT.toArray()); 
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

	
	   
	private void refrescarAtributos() {
	   ArrayList<String> listaA= new ArrayList<String>();
	   try {
		   if(conexionBD.isValid(1)) {
		   Statement st= conexionBD.createStatement();
		   ResultSet rs= st.executeQuery("Describe "+listaTablas.getSelectedValue());
		   
		   while (rs.next()) {
			   listaA.add(rs.getString(1));
		   }
		   listaAtributos.setListData(listaA.toArray());
		   }
	   }catch(SQLException ex)
		   {
	            System.out.println("SQLException: " + ex.getMessage());
	            System.out.println("SQLState: " + ex.getSQLState());
	            System.out.println("VendorError: " + ex.getErrorCode());
	        }
	   
   }
}
