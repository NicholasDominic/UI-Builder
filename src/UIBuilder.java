import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.*;

public class UIBuilder {
	private static final int NONE = -1, BORDER = 3;
	private JFrame frame = new JFrame("Final Project - GUI Editor Builder");
	private ImageIcon icon = new ImageIcon("logoBINUS.png");
	private JMenuBar menu_bar = new JMenuBar();
	private JMenu menu = new JMenu("MENU");
	private JMenuItem open = new JMenuItem("OPEN"), save = new JMenuItem("SAVE");
	private JToolBar toolbar = new JToolBar();
	private JPanel main = new JPanel();
	private int startX = NONE, startY = NONE, prevX = NONE, prevY = NONE;
	private boolean resize = false;
	private int butIdx = 0, rbutIdx = 0, mitemIdx = 0, cboxIdx = 0;

	public class Compo {
		public String Idx;
		public JComponent comp;
		
		public Compo(String Idx, JComponent comp) {
			this.Idx = Idx;
			this.comp = comp;
		}		
	}
	
    public static Statement st;
    public static ResultSet rs;
    public PreparedStatement pst;
    private Connection con;
    private String driver = "com.mysql.jdbc.Driver";
    private String url = "jdbc:mysql://localhost:3306";
    private String dbname = "test", user = "root", pass = "";
    
    Vector<JButton> buttons = new Vector<>();
    Vector<JRadioButton> rbuttons = new Vector<>(); 
    Vector<JCheckBox> cboxes = new Vector<>();
    Vector<JMenuItem> mitems = new Vector<>();
    Vector<Compo> compos = new Vector<>();
    Vector<String>project_name = new Vector<>();
    
	public boolean openConnection() {
		try { Class.forName(driver); }
		catch(Exception e) {
			System.out.println("Driver not found");
			return false;
		}

		try { con = DriverManager.getConnection(url + "/" + dbname, user, pass);
		} catch (Exception e) {
			System.out.println("Cannot get connection to database..");
			return false;
		}
		
		try { st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);	
		} catch (Exception exc) { System.out.println("Cannot create statement"); }
		
		return true;
	}

	public void closeConnection() {
		try {
			if(st!=null) {
				st.close();
				st=null;
			}

			if(con!=null) {
				con.close();
				con=null;
			}
		}
		catch(Exception exc) { }
	}
    
	public UIBuilder() {
		frame.setBounds(100, 100, 1000, 350);
		frame.setIconImage(icon.getImage());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		menu_bar.add(menu);
		menu.add(open);
		menu.addSeparator();
		menu.add(save);
		frame.setJMenuBar(menu_bar);
		
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(toolbar, BorderLayout.SOUTH);
		frame.getContentPane().add(main, BorderLayout.CENTER);
		frame.setVisible(true);
		
		open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { load(); }
		});
		
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { save(); }
		});
		
		buildToolbox();
		buildMainPanel();
		openConnection();
	}

	private void buildMainPanel() { main.setLayout(null); }
	
	public boolean isFileNameValid(String filename) {
		char [] c;
		
		if(filename==null) { return false; }
		else if(filename.trim().equals("")) {
			JOptionPane.showMessageDialog(null,"File name must be inputted!", "New File", JOptionPane.ERROR_MESSAGE);
			return false;
		} else {
			c = filename.toCharArray();
			for(char k : c) {
				if(!((k>='a'&&k<='z')||(k>='A'&&k<='Z')||(k>='0'&&k<='9'))) {
					JOptionPane.showMessageDialog(null,"Wrong file name format!","New File",JOptionPane.ERROR_MESSAGE);
					return false;
				}
			}
		}
		try {
			rs = st.executeQuery("SELECT * FROM UITABLE");
			rs.last();
			if(rs.getRow()==0){ return false; }
			
			rs = st.executeQuery("SELECT * FROM UITABLE");
			rs.beforeFirst();
			
			while(rs.next()) {
				project_name.add(""+rs.getString(1));
			}
			rs.first();
			
			for(int i=0; i<project_name.size(); i++){
				if(filename.equals(project_name.get(i))){
					JOptionPane.showMessageDialog(null,"File name already exists!", "Input another name", JOptionPane.ERROR_MESSAGE);
					return false;
				}
			}
		} catch (SQLException e) { e.printStackTrace(); }
		return true;
	}
	
	private void buildToolbox() {
	    JButton button = new JButton("Button");
	    button.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
	    	String id = "B" + butIdx;
	    	JComponent comp = new JButton("Button");
	    	compos.add(new Compo(id, comp));
	        addComponent(compos.lastElement().comp, 10, 10, 80, 24);
	        butIdx++;
	      }
	    });
	    toolbar.add(button);
	    
	    JCheckBox checkbox = new JCheckBox("Checkbox");
	    checkbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		    	String id = "C" + cboxIdx;
		    	JComponent comp = new JCheckBox("Checkbox");
		    	compos.add(new Compo(id, comp));;
		        addComponent(compos.lastElement().comp, 10, 10, 80, 24);
		        cboxIdx++;
			}
		});
	    toolbar.add(checkbox);
	    
	    JRadioButton radiobtn = new JRadioButton("Radio Button");
	    radiobtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		    	String id = "R" + rbutIdx;
		    	JComponent comp = new JRadioButton("Radio Button");
		    	compos.add(new Compo(id, comp));
		        addComponent(compos.lastElement().comp, 10, 10, 80, 24);
		        rbutIdx++;
			}
		});
	    toolbar.add(radiobtn);
	    
	    JMenuItem menuitem = new JMenuItem("Menu Item");
	    menuitem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		    	String id = "M" + mitemIdx;
		    	JComponent comp = new JMenuItem("Menu Item");
		    	compos.add(new Compo(id, comp));
		        addComponent(compos.lastElement().comp, 10, 10, 80, 24);
		        mitemIdx++;
			}
		});
	    toolbar.add(menuitem);
	}
	
	private void addComponent(JComponent comp, int x, int y, int width, int height) {
	    comp.setBounds(x, y, width, height);
	
	    comp.addMouseListener(new MouseAdapter() {
	    	public void mouseReleased(MouseEvent e) {
		    	startX = NONE;
		    	startY = NONE;
		    	((JComponent) e.getSource()).setCursor(Cursor.getDefaultCursor());
	      }
	
	    	public void mousePressed(MouseEvent e) {
		    	startX = e.getX();
		    	startY = e.getY();
	      }
	    });
	
	    comp.addMouseMotionListener(new MouseMotionAdapter() {
	    	public void mouseMoved(MouseEvent e) {
		        JComponent source = (JComponent) e.getSource();
		        int x = e.getX();
		        int y = e.getY();
	        
		        // Change into resized mouse
		        Rectangle bounds = source.getBounds();
		        resize = x < BORDER || y < BORDER || Math.abs(bounds.width - x) < BORDER || Math.abs(bounds.height - y) < BORDER;
		        
		        if (resize) source.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR)); // there are a lot of resize cursors here, this is just of proof of concept
		        else source.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
		}
	
	    	public void mouseDragged(MouseEvent e) {
		        int x = e.getX();
		        int y = e.getY();
		        
		        if (startX != NONE && startY != NONE) { // if component still on cursor hold
		        	JComponent source = (JComponent) e.getSource();
					Rectangle bounds = source.getBounds();
					int deltaX = x - startX;
					int deltaY = y - startY;
					
					if (resize) source.setSize(Math.max(10, bounds.width + x - prevX), Math.max(10, bounds.height + y - prevY)); // handle all resize cases, left, right,..
					else source.setLocation(bounds.x + deltaX, bounds.y + deltaY);	     
			        
		        } 

		        prevX = x;
		        prevY = y;
		      }
		    });
	    
	    main.add(comp);
	    main.validate();
	    main.repaint();
	}
	
	private void save() {
		String newfilename;
	
		newfilename = JOptionPane.showInputDialog(null, "Type new file name (a-z, A-Z, 0-9 only)", "Edit File", JOptionPane.INFORMATION_MESSAGE);
		
		if(isFileNameValid(newfilename)) {
			for (Compo compo : compos) {
				try {
					pst = con.prepareStatement("INSERT INTO UITABLE (PROJECTID, COMPID, x, y, width, height) VALUES(?,?,?,?,?,?)");
					pst.setString(1, newfilename);
					pst.setString(2, compo.Idx);
					pst.setInt(3, compo.comp.getBounds().x);
					pst.setInt(4, compo.comp.getBounds().y);
					pst.setInt(5, compo.comp.getBounds().width);
					pst.setInt(6, compo.comp.getBounds().height);
					pst.executeUpdate();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			compos.clear();
			frame.getContentPane().removeAll();
			main = new JPanel();
			frame.getContentPane().setLayout(new BorderLayout());
			frame.getContentPane().add(toolbar, BorderLayout.SOUTH);
			frame.getContentPane().add(main, BorderLayout.CENTER);
			frame.setVisible(true);
			main.setLayout(null);
		}
	}
	
	private boolean isLoad(String newfilename){
		int flag = 0;
		try {
			rs = st.executeQuery("SELECT * FROM UITABLE");
			rs.last();
			if(rs.getRow()==0) { return false; }
			
			rs = st.executeQuery("SELECT * FROM UITABLE");
			rs.beforeFirst();
			while(rs.next()) {
				project_name.add(""+rs.getString(1));
			}
			rs.first();
			
			for(int i=0; i<project_name.size(); i++){
				if(newfilename.equals(project_name.get(i))){
					return true;
				}
			}			
		} catch (SQLException e) { e.printStackTrace(); }
		return false;
	}
	
	private void load() {
		butIdx = 0;
		mitemIdx = 0;
		rbutIdx = 0;
		cboxIdx = 0;
		compos.clear();
		
		String newfilename = JOptionPane.showInputDialog(null, "Type file name", "Load File", JOptionPane.INFORMATION_MESSAGE);
		
		if(isLoad(newfilename) == false){
			JOptionPane.showMessageDialog(null,"Name not exists in database!", "Error", JOptionPane.ERROR_MESSAGE);
		} else {
			compos.clear();
			frame.getContentPane().removeAll();
			main = new JPanel();
			frame.getContentPane().setLayout(new BorderLayout());
			frame.getContentPane().add(toolbar, BorderLayout.SOUTH);
			frame.getContentPane().add(main, BorderLayout.CENTER);
			frame.setVisible(true);
			main.setLayout(null);
			
			try { 
				rs = st.executeQuery("SELECT * FROM UITABLE WHERE PROJECTID = '"+ newfilename +"'");
			} catch (SQLException e) { e.printStackTrace(); }
			
			try {
				String id;
				JComponent comp = null;
				while(rs.next()) {
					id = rs.getString("COMPID");
					int x = rs.getInt("x");
					int y = rs.getInt("y");
					int width = rs.getInt("width");
					int height = rs.getInt("height");
					
					switch (id.charAt(0)) {
					case 'B':
						comp = new JButton("Button");
						butIdx++;
						break;
					case 'C':
						comp = new JCheckBox("Check Box");
						cboxIdx++;
						break;
					case 'R':
						comp = new JRadioButton("Radio Button");
						rbutIdx++;
						break;		
					case 'M':
						comp = new JMenuItem("Menu Item");
						mitemIdx++;
						break;
					}
					compos.add(new Compo(id, comp));
					addComponent(compos.lastElement().comp, x, y, width, height);
				}
			} catch (SQLException e) { e.printStackTrace(); }
		}	
	}

	public static void main(String[] args) { new UIBuilder(); }
}