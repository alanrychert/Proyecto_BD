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
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;


@SuppressWarnings("serial")
public class VentanaInspector extends javax.swing.JInternalFrame 
{
   private JPanel pnlFiltroNombre;
   private JScrollPane scrTabla;
   private JTextField txtNombre;
   private JTable tabla;
   private JLabel lblNombre;
   private JPanel panelTablaFiltro;
   private JPanel panelSeleccionados;
   private JLabel lblCalleSelec;
   private JLabel lblAlturaSelec;
   private JLabel lblIdParqSelec;
   private JButton btnVerificar;
   private JPanel panelPatentes;
   private DefaultListModel<String> l1;
   
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
         crearPanelTablaFiltro();
         crearPanelPatentes();
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
   private void tablaMouseClicked(MouseEvent evt) 
   {
      if ((this.tabla.getSelectedRow() != -1) && (evt.getClickCount() == 2))
      {
         this.seleccionarFila();
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
      
      //ACA SE CAMBIARIA UN CUADRO DE TEXTO CON EL IDE DEL PARQUIMETRO 
      //this.txtNombre.setText(this.tabla.getValueAt(this.tabla.getSelectedRow(), 0).toString());
      //this.txtFecha.setText(Fechas.convertirDateAString((java.util.Date) this.tabla.getValueAt(this.tabla.getSelectedRow(), 1)));
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
         String sql = "SELECT calle,altura,id_parq FROM parquimetros WHERE LOWER(calle) LIKE '%"+ 
                 this.txtNombre.getText().trim().toLowerCase() + "%' ";

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
            this.tabla.setValueAt(rs.getString("id_parq"), i, 2);
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
	       GridLayout thisLayout = new GridLayout(1,2);
	       panelTablaFiltro= new JPanel(thisLayout);
           
           {  //se crea un JScrollPane para poder mostrar la tabla en su interior 
               scrTabla = new JScrollPane();
               panelTablaFiltro.add(scrTabla, BorderLayout.CENTER);
             
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
            getContentPane().add(panelTablaFiltro,BorderLayout.CENTER);
   }
   
   private void crearPanelSeleccionados() {
	   panelSeleccionados= new JPanel(new GridLayout(1,4));
       panelSeleccionados.setPreferredSize(new Dimension(getContentPane().getWidth(),50));
	   lblCalleSelec= new JLabel("Calle: ");
	   lblAlturaSelec= new JLabel("Altura: ");
	   lblIdParqSelec= new JLabel("Id parq: ");
	   btnVerificar= new JButton("Verificar");
	   btnVerificar.setMaximumSize(new Dimension(60,20));
	   panelSeleccionados.add(lblCalleSelec);
	   panelSeleccionados.add(lblAlturaSelec);
	   panelSeleccionados.add(lblIdParqSelec);
	   panelSeleccionados.add(btnVerificar);
	   
	   getContentPane().add(panelSeleccionados,BorderLayout.SOUTH);
	   
   }

   private void crearPanelPatentes() { 
	   panelPatentes= new JPanel(new BorderLayout());
	   
	   
	   //-----------------------------------------------------
	   //L1 TIENE QUE ESTAR EN UN SCROLLPANE
	   //-----------------------------------------------------
	   
	   
	   
	   l1 = new DefaultListModel<String>();
	   l1.addElement("Elemento de prueba");
	   JList<String> list = new JList<String>(l1);  
	   
	   JPanel agregarPanel = new JPanel();
	   JButton agregarPatenteBoton = new JButton();
	   final JTextField patenteTextField = new JTextField(20);
	   
	   agregarPatenteBoton.setText("Agregar");
	   agregarPatenteBoton.addActionListener(new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			l1.addElement(patenteTextField.getText().toString());
			patenteTextField.setText("");
		}
		   
	   });
	   
	   agregarPanel.setLayout(new GridLayout(1,0));
	   agregarPanel.add(patenteTextField);
	   agregarPanel.add(agregarPatenteBoton);
	   
	   JLabel tituloLabel = new JLabel("Patentes:");
	   tituloLabel.setFont(new Font("Arial", Font.PLAIN, 22));
	   tituloLabel.setAlignmentX(CENTER_ALIGNMENT);
	   
	   panelPatentes.add(tituloLabel,BorderLayout.NORTH);
	   panelPatentes.add(list,BorderLayout.CENTER);
	   panelPatentes.add(agregarPanel,BorderLayout.SOUTH);
	   panelTablaFiltro.add(panelPatentes);
	   
	   
   }
}
