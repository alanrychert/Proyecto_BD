package parquimetros;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import quick.dbtable.DBTable;

@SuppressWarnings("serial")
public class VentanaInspector extends javax.swing.JInternalFrame 
{
   private JPanel pnlFiltroNombre;
   private JScrollPane scrTabla;
   private JTextField txtNombre;
   private JTable tabla;
   private JLabel lblNombre;
   
   protected Connection conexionBD = null;

   public VentanaInspector(Connection conexion) 
   {
      super();
      conexionBD = conexion;
      initGUI();
   }
   
   private void initGUI() 
   {
      try {
         this.setPreferredSize(new java.awt.Dimension(640, 480));
         this.setBounds(0, 0, 640, 480);
         this.setTitle("Inspector");
         this.setClosable(true);
         this.setResizable(true);
         this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
         this.setMaximizable(true);
         BorderLayout thisLayout = new BorderLayout();
         this.setVisible(true);
         getContentPane().setLayout(thisLayout);
         this.addComponentListener(new ComponentAdapter() {
            public void componentHidden(ComponentEvent evt) {
               thisComponentHidden(evt);
            }
            public void componentShown(ComponentEvent evt) {
               thisComponentShown(evt);
            }
         });
         {
            pnlFiltroNombre = new JPanel();
            getContentPane().add(pnlFiltroNombre, BorderLayout.NORTH);
            {
               lblNombre = new JLabel();
               pnlFiltroNombre.add(lblNombre);
               lblNombre.setText("Búsqueda incremental del nombre");
            }
            {
               txtNombre = new JTextField();               
               pnlFiltroNombre.add(txtNombre);
               txtNombre.setColumns(30);
               txtNombre.addCaretListener(new CaretListener() {
                  public void caretUpdate(CaretEvent evt) {
                     txtNombreCaretUpdate(evt);
                  }
               });
            }
         }
         {  //se crea un JScrollPane para poder mostrar la tabla en su interior 
            scrTabla = new JScrollPane();
            getContentPane().add(scrTabla, BorderLayout.CENTER);
            
            
          
            // se define el modelo de la tabla donde se almacenarán las tuplas 
            // extendiendo DefaultTableModel 
            final class TablaEstacionadosModel extends DefaultTableModel{
            	// define la clase java asociada a cada columna de la tabla
       	        private Class[] types;
         	    // define si una columna es editable
                private boolean[] canEdit;
                
                TablaEstacionadosModel(){
                	super(new String[][] {},
                		  new String[]{"Calle", "Altura", "Id Parquimetro"});
                	types = new Class[] {java.lang.String.class,
                	                     java.lang.Integer.class, java.lang.String.class };
                	canEdit= new boolean[] { false, false, false };
                };             	
            		             
                // recupera la clase java de cada columna de la tabla
                public Class getColumnClass(int columnIndex) 
                {
                   return types[columnIndex];
                }
                // determina si una celda es editable
                public boolean isCellEditable(int rowIndex, int columnIndex) 
                {
                   return canEdit[columnIndex];
                }         	          	            	
            };
            
            TableModel EstacionadosModel = new TablaEstacionadosModel();
                               
            /*           
             //Otra opción: definir el modelo de la tabla usando DefalutTableModel directamente 
              TableModel BarcosModel =   
                 new DefaultTableModel  
                 (
                    new String[][] {},
                    new String[] {"Nombre", "Id", "Capitán"}
                 );
            */
            
               tabla = new JTable(); // Crea una tabla
               scrTabla.setViewportView(tabla); //setea la tabla dentro del JScrollPane srcTabla               
               tabla.setModel(EstacionadosModel); // setea el modelo de la tabla  
               tabla.setAutoCreateRowSorter(true); // activa el ordenamiento por columnas, para
                                                   // que se ordene al hacer click en una columna
            
         }
         pack();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   private void thisComponentShown(ComponentEvent evt) 
   {
      //this.conectarBD();
	  //PREGUNTAR SI ES NECESARIO QUE SE CONECTE Y DESCONECTE CUANDO SE MUESTRA Y SE OCULTA
      this.refrescarTabla();
   }
   
   private void thisComponentHidden(ComponentEvent evt) 
   {
      //this.desconectarBD();
   }

   private void txtNombreCaretUpdate(CaretEvent evt) 
   {
      this.refrescarTabla();
   }


   /*private void desconectarBD()
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
   }*/

   private void refrescarTabla()
   {
      try
      {
         // se crea una sentencia o comando jdbc para realizar la consulta 
    	 // a partir de la conexión establecida (conexionBD)
         Statement stmt = this.conexionBD.createStatement();

         // se prepara el string SQL de la consulta
         String sql = "SELECT * FROM estacionados;";

         // se ejecuta la sentencia y se recibe un resultset
         ResultSet rs = stmt.executeQuery(sql);
         // se recorre el resulset y se actualiza la tabla en pantalla
         ((DefaultTableModel) this.tabla.getModel()).setRowCount(0);
         int i = 0;
         while (rs.next())
         {
        	 // agrega una fila al modelo de la tabla
            ((DefaultTableModel) this.tabla.getModel()).setRowCount(i + 1);
            // se agregan a la tabla los datos correspondientes cada celda de la fila recuperada
            this.tabla.setValueAt(rs.getString("calle"), i, 0);
            this.tabla.setValueAt(rs.getInt("altura"), i, 1);            
            this.tabla.setValueAt(rs.getString("patente"), i, 2);
            i++;
         }
         // se cierran los recursos utilizados 
         rs.close();
         stmt.close();
      }
      catch (SQLException ex)
      {
         // en caso de error, se muestra la causa en la consola
         System.out.println("SQLException: " + ex.getMessage());
         System.out.println("SQLState: " + ex.getSQLState());
         System.out.println("VendorError: " + ex.getErrorCode());
      }
   }

}
