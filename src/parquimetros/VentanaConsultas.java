package parquimetros;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import java.sql.Types;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import quick.dbtable.*; 


@SuppressWarnings("serial")
public class VentanaConsultas extends javax.swing.JInternalFrame 
{
   private JPanel pnlConsulta;
   private JTextArea txtInput;
   private JButton botonBorrar;
   private JButton btnEjecutar;
   private DBTable tabla;    
   private JScrollPane scrConsulta;
   private PanelListas panelListas;
   protected Connection conexionBD = null;

   
   
   public VentanaConsultas(Connection conexion) 
   {
      super();
      conexionBD = conexion;
      initGUI();
      
   }
   
   private void initGUI() 
   {
      try {
      	// crea la tabla  
      	tabla = new DBTable();   
      	tabla.setConnection(conexionBD);
    	  
         setPreferredSize(new Dimension(800, 600));
         this.setBounds(0, 0, 800, 600);
         setVisible(true);
         BorderLayout thisLayout = new BorderLayout();
         this.setTitle("Consultas (Utilizando DBTable)");
         getContentPane().setLayout(thisLayout);
         this.setClosable(true);
         this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
         this.setMaximizable(true);
         this.addComponentListener(new ComponentAdapter() {
            public void componentHidden(ComponentEvent evt) {
               thisComponentHidden(evt);
            }
            public void componentShown(ComponentEvent evt) {
               thisComponentShown(evt);
            }
         });
         
         
         {
            pnlConsulta = new JPanel();
            getContentPane().add(pnlConsulta, BorderLayout.NORTH);
            {
               scrConsulta = new JScrollPane();
               pnlConsulta.add(scrConsulta);
               {
                  txtInput = new JTextArea();
                  scrConsulta.setViewportView(txtInput);
                  txtInput.setTabSize(3);
                  txtInput.setColumns(80);
                  txtInput.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
                  txtInput.setText("Cualquier consulta");
                  txtInput.setFont(new java.awt.Font("Monospaced",0,12));
                  txtInput.setRows(10);
               }
            }
            {
               btnEjecutar = new JButton();
               pnlConsulta.add(btnEjecutar);
               btnEjecutar.setText("Ejecutar");
               btnEjecutar.addActionListener(new ActionListener() {
                  public void actionPerformed(ActionEvent evt) {
                     btnEjecutarActionPerformed(evt);
                  }
               });
            }
            {
            	botonBorrar = new JButton();
            	pnlConsulta.add(botonBorrar);
            	botonBorrar.setText("Borrar");            
            	botonBorrar.addActionListener(new ActionListener() {
            		public void actionPerformed(ActionEvent arg0) {
            		  txtInput.setText("");            			
            		}
            	});
            }	
         }
         {
        	
        	
        	// Agrega la tabla al frame (no necesita JScrollPane como Jtable)
            getContentPane().add(tabla, BorderLayout.CENTER);           
                      
           // setea la tabla para sólo lectura (no se puede editar su contenido)  
           tabla.setEditable(false);   
           
           panelListas= new PanelListas(conexionBD);
           getContentPane().add(panelListas,BorderLayout.SOUTH);
                      
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   private void thisComponentShown(ComponentEvent evt) 
   {
	   tabla.setConnection(conexionBD);
   }
   
   private void thisComponentHidden(ComponentEvent evt) 
   {
	   try
       {
		  desconectarBD();
          tabla.close();            
       }
       catch (SQLException ex)
       {
          System.out.println("SQLException: " + ex.getMessage());
          System.out.println("SQLState: " + ex.getSQLState());
          System.out.println("VendorError: " + ex.getErrorCode());
       } 
   }

   private void btnEjecutarActionPerformed(ActionEvent evt) 
   {
	  String[] palabras= this.txtInput.getText().split(" ");
	  String primerP = palabras[0].toUpperCase();
	  if (primerP.contentEquals("UPDATE") || primerP.contentEquals("CREATE") || primerP.contentEquals("INSERT")|| primerP.contentEquals("DELETE")
			  || primerP.contentEquals("DROP")) 
	  {
		  this.ejecutar();
	  }
	  else {
		  this.refrescarTabla();  
	  }
   }
   

   private void refrescarTabla()
   {
      try
      {    
    	 // seteamos la consulta a partir de la cual se obtendrán los datos para llenar la tabla
    	 tabla.setSelectSql(this.txtInput.getText().trim());

    	  // obtenemos el modelo de la tabla a partir de la consulta para 
    	  // modificar la forma en que se muestran algunas columnas  
    	  tabla.createColumnModelFromQuery();    	    
    	  for (int i = 0; i < tabla.getColumnCount(); i++)
    	  { // para que muestre correctamente los valores de tipo TIME (hora)  		   		  
    		 if	 (tabla.getColumn(i).getType()==Types.TIME)  
    		 {    		 
    		    tabla.getColumn(i).setType(Types.CHAR);  
  	       	 }
    		 // cambiar el formato en que se muestran los valores de tipo DATE
    		 if	 (tabla.getColumn(i).getType()==Types.DATE)
    		 {
    		    tabla.getColumn(i).setDateFormat("dd/MM/YYYY");
    		 }
          }  
    	  // actualizamos el contenido de la tabla.   	     	  
    	  tabla.refresh();
    	  // No es necesario establecer  una conexión, crear una sentencia y recuperar el 
    	  // resultado en un resultSet, esto lo hace automáticamente la tabla (DBTable) a 
    	  // patir de la conexión y la consulta seteadas con connectDatabase() y setSelectSql() respectivamente.
          
    	  
    	  
       }
      catch (SQLException ex)
      {
         // en caso de error, se muestra la causa en la consola
         System.out.println("SQLException: " + ex.getMessage());
         System.out.println("SQLState: " + ex.getSQLState());
         System.out.println("VendorError: " + ex.getErrorCode());
         JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this),
                                       ex.getMessage() + "\n", 
                                       "Error al ejecutar la consulta.",
                                       JOptionPane.ERROR_MESSAGE);
         
      }
      
   }
   
   private void ejecutar() {
      try
      {
         Statement stmt = this.conexionBD.createStatement();
         String sql = txtInput.getText().trim();
         stmt.execute(sql);
         stmt.close();
         panelListas.refrescarListaTablas();
      }
      catch (SQLException ex)
      {
         JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this),
                                       ex.getMessage()+"\n",
                                       "Error",
                                       JOptionPane.ERROR_MESSAGE);
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
