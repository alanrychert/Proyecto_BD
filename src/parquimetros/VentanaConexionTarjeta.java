package parquimetros;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.MaskFormatter;

import quick.dbtable.DBTable;


@SuppressWarnings("serial")
public class VentanaConexionTarjeta extends javax.swing.JInternalFrame 
{

   private JScrollPane scrTabla,scrTablaParq,scrTablaTarjeta;
   private JTable tabla,tablaParq,tablaTarjeta;
   private JPanel panelTablaFiltro,panelTablaParq,panelTablaTarjeta,panelTablas;
   private JPanel panelSeleccionados;
   private JLabel lblCalleSelec, lblAlturaSelec,lblIdParqSelec, lblTarjetaSelec;
   private JButton btnConectar;
   private DefaultListModel<String> l1;

   
   protected Connection conexionBD = null;

   public VentanaConexionTarjeta(Connection conexion) 
   {
      super();
      conexionBD = conexion;
      initGUI();
      refrescarTablaUbicacion();
      refrescarTablaTarjetas();
      
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
         panelTablas = new JPanel(new GridLayout(1,3));
         getContentPane().add(panelTablas,BorderLayout.CENTER);
         crearPanelTablaFiltro();
         crearPanelTablaParq();
         crearPanelTablaTarjeta();
         crearPanelSeleccionados();
         this.addComponentListener(new ComponentAdapter() {
            public void componentHidden(ComponentEvent evt) {
               thisComponentHidden(evt);
            }
            public void componentShown(ComponentEvent evt) {
               thisComponentShown(evt);
            }
         });
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   private void thisComponentShown(ComponentEvent evt) 
   {
      //this.refrescarTablaParquimetros("calle1","111");
   }
   
   private void thisComponentHidden(ComponentEvent evt) 
   {
      this.desconectarBD();
   }

   private void tablaMouseClicked(MouseEvent evt) 
   {
      if ((this.tabla.getSelectedRow() != -1) && (evt.getClickCount() == 2))
      {
         this.seleccionarFila();
      }
   }
   
   private void tablaParqMouseClicked(MouseEvent evt) 
   {
      if ((this.tablaParq.getSelectedRow() != -1) && (evt.getClickCount() == 2))
      {
    	 int seleccionado=tablaParq.getSelectedRow();
    	 String parq = this.tablaParq.getValueAt(seleccionado, 0).toString();
    	 
         this.lblIdParqSelec.setText(parq);
      }
   }
   
   private void tablaTarjetaMouseClicked(MouseEvent evt) 
   {
      if ((this.tablaTarjeta.getSelectedRow() != -1) && (evt.getClickCount() == 2))
      {
    	 int seleccionado=tablaTarjeta.getSelectedRow();
    	 String parq = this.tablaTarjeta.getValueAt(seleccionado, 0).toString();
    	 
         this.lblTarjetaSelec.setText(parq);
      }
   }
   
   
   private void tablaKeyTyped(KeyEvent evt) 
   {
      if ((this.tabla.getSelectedRow() != -1) && (evt.getKeyChar() == ' '))
      {
         this.seleccionarFila();
      }
   }
   
   private void seleccionarFila()
   {
      int seleccionado = this.tabla.getSelectedRow();
      String calle,altura;
      
      calle= this.tabla.getValueAt(seleccionado, 0).toString();
      altura= this.tabla.getValueAt(seleccionado, 1).toString();
      
      this.lblCalleSelec.setText(calle);
      this.lblAlturaSelec.setText(altura);
      
      refrescarTablaParquimetros(calle,altura);
      
      //ACA SE CAMBIARIA UN CUADRO DE TEXTO CON EL IDE DEL PARQUIMETRO 
      //this.txtNombre.setText(this.tabla.getValueAt(this.tabla.getSelectedRow(), 0).toString());
      //this.txtFecha.setText(Fechas.convertirDateAString((java.util.Date) this.tabla.getValueAt(this.tabla.getSelectedRow(), 1)));
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

   private void refrescarTablaUbicacion()
   {
      try
      {
         // se crea una sentencia o comando jdbc para realizar la consulta 
    	 // a partir de la conexi�n establecida (conexionBD)
         Statement stmt = this.conexionBD.createStatement();

         // se prepara el string SQL de la consulta
         String sql = "SELECT calle,altura FROM ubicaciones;";
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
   private void refrescarTablaParquimetros(String calle,String altura)
   {
      try
      {
         // se crea una sentencia o comando jdbc para realizar la consulta 
    	 // a partir de la conexi�n establecida (conexionBD)
         Statement stmt = this.conexionBD.createStatement();

         // se prepara el string SQL de la consulta
         String sql = "SELECT id_parq FROM parquimetros where calle=\""+calle+"\" and altura="+altura+";";
         // se ejecuta la sentencia y se recibe un resultset
         ResultSet rs = stmt.executeQuery(sql);
         // se recorre el resulset y se actualiza la tabla en pantalla
         ((DefaultTableModel) this.tablaParq.getModel()).setRowCount(0);
         int i = 0;
         while (rs.next())
         {
        	 // agrega una fila al modelo de la tabla
            ((DefaultTableModel) this.tablaParq.getModel()).setRowCount(i + 1);
            // se agregan a la tabla los datos correspondientes cada celda de la fila recuperada
            this.tablaParq.setValueAt(rs.getString("id_parq"), i, 0);    
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
   private void refrescarTablaTarjetas()
   {
      try
      {
         // se crea una sentencia o comando jdbc para realizar la consulta 
    	 // a partir de la conexi�n establecida (conexionBD)
         Statement stmt = this.conexionBD.createStatement();

         // se prepara el string SQL de la consulta
         String sql = "SELECT id_tarjeta FROM tarjetas;";
         // se ejecuta la sentencia y se recibe un resultset
         ResultSet rs = stmt.executeQuery(sql);
         // se recorre el resulset y se actualiza la tabla en pantalla
         ((DefaultTableModel) this.tablaTarjeta.getModel()).setRowCount(0);
         int i = 0;
         while (rs.next())
         {
        	 // agrega una fila al modelo de la tabla
            ((DefaultTableModel) this.tablaTarjeta.getModel()).setRowCount(i + 1);
            // se agregan a la tabla los datos correspondientes cada celda de la fila recuperada
            this.tablaTarjeta.setValueAt(rs.getString("id_tarjeta"), i, 0);    
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
   
   
   
   private void crearPanelTablaFiltro() {
	       GridLayout thisLayout = new GridLayout(1,1);
	       panelTablaFiltro= new JPanel(thisLayout);
	   
           
           {  //se crea un JScrollPane para poder mostrar la tabla en su interior 
               scrTabla = new JScrollPane();
               panelTablaFiltro.add(scrTabla);
             
               // se define el modelo de la tabla donde se almacenar�n las tuplas 
               // extendiendo DefaultTableModel 
               final class TablaEstacionadosModel extends DefaultTableModel{
               	// define la clase java asociada a cada columna de la tabla
          	        @SuppressWarnings("rawtypes")
					private Class[] types;
            	    // define si una columna es editable
                   private boolean[] canEdit;
                   
                   TablaEstacionadosModel(){
                   	super(new String[][] {},
                   		  new String[]{"Calle", "Altura"});
                   	types = new Class[] {java.lang.String.class,
                   	                     java.lang.Integer.class};
                   	canEdit= new boolean[] { false, false };
                   };             	
               		             
                   // recupera la clase java de cada columna de la tabla
                   @SuppressWarnings({ "unchecked", "rawtypes" })
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
                //Otra opci�n: definir el modelo de la tabla usando DefalutTableModel directamente 
                 TableModel BarcosModel =   
                    new DefaultTableModel  
                    (
                       new String[][] {},
                       new String[] {"Nombre", "Id", "Capit�n"}
                    );
               */
               
                  tabla = new JTable(); // Crea una tabla
                  tabla.setToolTipText("Doble-click o Espacio para seleccionar un parquimetro.");
                  tabla.addKeyListener(new KeyAdapter() {
                     public void keyTyped(KeyEvent evt) {
                        tablaKeyTyped(evt);
                     }
                  });
                  
                  tabla.addMouseListener(new MouseAdapter() {
                     public void mouseClicked(MouseEvent evt) {
                        tablaMouseClicked(evt);
                     }
              		});
                  scrTabla.setViewportView(tabla); //setea la tabla dentro del JScrollPane srcTabla               
                  tabla.setModel(EstacionadosModel); // setea el modelo de la tabla  
                  tabla.setAutoCreateRowSorter(true); // activa el ordenamiento por columnas, para
                                                      // que se ordene al hacer click en una columna
               
            }
        //    pack();
            panelTablas.add(panelTablaFiltro);
    
   }
   
   private void crearPanelTablaParq() {
       GridLayout thisLayout = new GridLayout(1,3);
       panelTablaParq= new JPanel(thisLayout);
   
       
       {  //se crea un JScrollPane para poder mostrar la tabla en su interior 
           scrTablaParq = new JScrollPane();
           panelTablaParq.add(scrTablaParq);
         
           // se define el modelo de la tabla donde se almacenar�n las tuplas 
           // extendiendo DefaultTableModel 
           final class TablaEstacionadosModel extends DefaultTableModel{
           	// define la clase java asociada a cada columna de la tabla
      	        @SuppressWarnings("rawtypes")
				private Class[] types;
        	    // define si una columna es editable
               private boolean[] canEdit;
               
               TablaEstacionadosModel(){
               	super(new String[][] {},
               		  new String[]{"Id Parquimetro"});
               	types = new Class[] {java.lang.String.class};
               	canEdit= new boolean[] { false};
               };             	
           		             
               // recupera la clase java de cada columna de la tabla
               @SuppressWarnings({ "unchecked", "rawtypes" })
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
            //Otra opci�n: definir el modelo de la tabla usando DefalutTableModel directamente 
             TableModel BarcosModel =   
                new DefaultTableModel  
                (
                   new String[][] {},
                   new String[] {"Nombre", "Id", "Capit�n"}
                );
           */
           
              tablaParq = new JTable(); // Crea una tabla
              tablaParq.setToolTipText("Doble-click o Espacio para seleccionar un parquimetro.");
              tablaParq.addKeyListener(new KeyAdapter() {
                 public void keyTyped(KeyEvent evt) {
                    tablaKeyTyped(evt);
                 }
              });
              
              tablaParq.addMouseListener(new MouseAdapter() {
                 public void mouseClicked(MouseEvent evt) {
                    tablaParqMouseClicked(evt);
                 }
          		});
              scrTablaParq.setViewportView(tablaParq); //setea la tabla dentro del JScrollPane srcTabla               
              tablaParq.setModel(EstacionadosModel); // setea el modelo de la tabla  
              tablaParq.setAutoCreateRowSorter(true); // activa el ordenamiento por columnas, para
                                                  // que se ordene al hacer click en una columna
           
        }
    //    pack();
        panelTablas.add(panelTablaParq);
}
   
   private void crearPanelTablaTarjeta() {
       GridLayout thisLayout = new GridLayout(1,1);
       panelTablaTarjeta= new JPanel(thisLayout);
   
       
       {  //se crea un JScrollPane para poder mostrar la tabla en su interior 
           scrTablaTarjeta = new JScrollPane();
           panelTablaTarjeta.add(scrTablaTarjeta);
         
           // se define el modelo de la tabla donde se almacenar�n las tuplas 
           // extendiendo DefaultTableModel 
           final class TablaEstacionadosModel extends DefaultTableModel{
           	// define la clase java asociada a cada columna de la tabla
      	        @SuppressWarnings("rawtypes")
				private Class[] types;
        	    // define si una columna es editable
               private boolean[] canEdit;
               
               TablaEstacionadosModel(){
               	super(new String[][] {},
               		  new String[]{"Tarjeta"});
               	types = new Class[] {java.lang.String.class,
               	                     java.lang.Integer.class};
               	canEdit= new boolean[] { false, false };
               };             	
           		             
               // recupera la clase java de cada columna de la tabla
               @SuppressWarnings({ "unchecked", "rawtypes" })
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
            //Otra opci�n: definir el modelo de la tabla usando DefalutTableModel directamente 
             TableModel BarcosModel =   
                new DefaultTableModel  
                (
                   new String[][] {},
                   new String[] {"Nombre", "Id", "Capit�n"}
                );
           */
           
              tablaTarjeta = new JTable(); // Crea una tabla
              tablaTarjeta.setToolTipText("Doble-click o Espacio para seleccionar un parquimetro.");
              tablaTarjeta.addKeyListener(new KeyAdapter() {
                 public void keyTyped(KeyEvent evt) {
                    tablaKeyTyped(evt);
                 }
              });
              
              tablaTarjeta.addMouseListener(new MouseAdapter() {
                 public void mouseClicked(MouseEvent evt) {
                    tablaTarjetaMouseClicked(evt);
                 }
          		});
              scrTablaTarjeta.setViewportView(tablaTarjeta); //setea la tabla dentro del JScrollPane srcTabla               
              tablaTarjeta.setModel(EstacionadosModel); // setea el modelo de la tabla  
              tablaTarjeta.setAutoCreateRowSorter(true); // activa el ordenamiento por columnas, para
                                                  // que se ordene al hacer click en una columna
           
        }
    //    pack();
        panelTablas.add(panelTablaTarjeta);

}
   
   private void crearPanelSeleccionados() {
	   panelSeleccionados= new JPanel(new GridLayout(1,5));
       panelSeleccionados.setPreferredSize(new Dimension(getContentPane().getWidth(),50));
	   lblCalleSelec= new JLabel("Calle: ");
	   lblAlturaSelec= new JLabel("Altura: ");
	   lblIdParqSelec= new JLabel("Id parq: ");
	   lblTarjetaSelec= new JLabel("Id tarjeta: ");
	   
	   btnConectar= new JButton("Conectar");
	   btnConectar.setMaximumSize(new Dimension(60,20));
	   panelSeleccionados.add(lblCalleSelec);
	   panelSeleccionados.add(lblAlturaSelec);
	   panelSeleccionados.add(lblIdParqSelec);
	   panelSeleccionados.add(lblTarjetaSelec);
	   panelSeleccionados.add(btnConectar);
	   
	   btnConectar.addActionListener(new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(lblCalleSelec.getText()=="Calle: " ||lblIdParqSelec.getText()=="Id parq: " || lblAlturaSelec.getText() == "Altura: " || lblTarjetaSelec.getText()=="Id tarjeta: ") {
				JOptionPane.showMessageDialog(null,"Debe Completar todos los campos");
			}
			else{
				conectar(lblTarjetaSelec.getText(),lblIdParqSelec.getText());
			}
		
		}
			   
		   });
		   
		   getContentPane().add(panelSeleccionados,BorderLayout.SOUTH);   
   }
   
   private void conectar(String id_tarjeta, String id_parquimetro) {
	    JPanel panelOperacion = new JPanel();
	    DBTable tablaOperacion;
	    tablaOperacion = new DBTable();
		tablaOperacion.setEditable(false);  
		tablaOperacion.setConnection(conexionBD);
		String sql = "call conectar("+id_tarjeta+","+id_parquimetro+");";
		System.out.println(sql);
		
		panelOperacion.add(tablaOperacion);
		refrescarTabla(tablaOperacion,sql);
		
		JOptionPane.showMessageDialog(null,panelOperacion, "Resultado de la Operacion",JOptionPane.INFORMATION_MESSAGE);
	      
	
   }
   private void refrescarTabla(DBTable tablaOperacion,String consulta)
   {
      try
      {    
    	  Statement stmt = this.conexionBD.createStatement();
          ResultSet rs = stmt.executeQuery(consulta);
          tablaOperacion.refresh(rs);

    	  // obtenemos el modelo de la tabla a partir de la consulta para 
    	  // modificar la forma en que se muestran algunas columnas  
    	  //tablaOperacion.createColumnModelFromQuery();    	    
    	  
	  
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


   
}

