package th.co.imake.rtsp;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

//import java.io.File;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

import com.innovatrics.commons.geom.Point;
import com.innovatrics.commons.geom.PointF;
import com.innovatrics.commons.geom.Rectangle;
import com.innovatrics.commons.pc.RawImageUtils;
import com.innovatrics.iface.Face;
import com.innovatrics.iface.FaceFeature;
import com.innovatrics.iface.FaceHandler;
import com.innovatrics.iface.IFace;
import com.innovatrics.iface.VisualObject;
import com.innovatrics.iface.VisualObjectHandler;
import com.innovatrics.iface.enums.FaceCropMethod;
import com.innovatrics.iface.enums.FaceFeatureId;
import com.innovatrics.iface.enums.ImageSaveType;
import com.innovatrics.iface.enums.Parameter;
import com.innovatrics.iface.enums.TrackedObjectFaceType;
import com.innovatrics.iface.enums.TrackedObjectState;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;
import th.co.imake.rtsp.model.FaceBlacklist;
import th.co.imake.rtsp.model.FaceMatching;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;

public class IFaceTech extends JFrame implements th.co.imake.rtsp.IPCameraCapture.IPCameraCaptureEvents
{
    private static final long serialVersionUID = 1L;
    public static final int FRAMES_TO_REFRESH = 25;
    public static final int MIN_EYE_DISTANCE = 40;
    public static final int MAX_EYE_DISTANCE = 200;
    public static final int MAX_FACES = 5;
    
    private JPanel canvas;
    private JLabel statusBar;
    
    private JFrame frame;
	private JTable table;
	private static final int width = 1320;
    private static final int height = 750;
    private  JLabel label =null;
    private JLabel lblNewLabel_3 =null;
   // private JLabel lblNewLabel_4 =null;
    private JLabel lblNewLabel_5 = null ; 
    private JLabel lblNewLabel_1 = null; 
    private JLabel lblNewLabel_2 =null;
    List<FaceBlacklist> faceBlacklists = null;
    byte[] template0 =null;
   // private int video_width = 935;
    //private int video_height = 350;
    
    private IPCameraCapture ipCapture;            
    private final SharedData model = new SharedData();
    
    private final FPSIPCounter fpsCounter = new FPSIPCounter();
    
    private IFace iface = IFace.getInstance();
    private FaceHandler faceHandler = null;
    private VisualObjectHandler objectHandler = null;
    private VisualObject[] objects = null;
    private long startTime;
    
    static Map<String, String> paramconfig = new HashMap<String, String>();
    
    

    String pathCropped ="";
    String db_driver = "org.gjt.mm.mysql.Driver";
    String db_url = "";
    String db_user = "";
    String db_password = ""; 
    String client_name = ""; 
    String ip_tomcat = ""; 
    String net_control_ip = "";
    String net_control_password = "";
    String lpr_db_url = ""; 
    String lpr_db_user  = "";
    String lpr_db_password  = "";
    int delay = 0;
    String gate_name  = "";
    
    public static String media_url = "";
    public IFaceTech() {
        super("IFace Realtime Demo");
    }
    
    public void start() {
    	/* */

    System.out.println("start>>>>");
    
  
    
	//delete directory ./matching_picture
    try {
		deleteDirectoryRecursionJava(new File(System.getProperty("user.dir")+"\\matching_picture"));
	} catch (IOException e) {
		// TODO Auto-generated catch block // e.printStackTrace();
	}
    
	//delete directory ./profile_picture
    try {
  		deleteDirectoryRecursionJava(new File(System.getProperty("user.dir")+"\\profile_picture"));
  	} catch (IOException e) {
  		// TODO Auto-generated catch block // e.printStackTrace();
  	}
	
    // Set parameter from file config.ini
    SystemConfig systemConfig = new SystemConfig();
	systemConfig.setConfig();
	System.out.println("--------- system config ---------");
	System.out.println("db_url: "+SystemConfig.db_url);
	System.out.println("db_user: "+SystemConfig.db_user);
	System.out.println("db_password: "+SystemConfig.db_password);
	System.out.println("camera_ip: "+SystemConfig.camera_ip); 
	System.out.println("client_name: "+SystemConfig.client_name);
	System.out.println("ip_tomcat: "+SystemConfig.ip_tomcat);
	
	System.out.println("net_control_ip: "+SystemConfig.net_control_ip);
	System.out.println("net_control_password: "+SystemConfig.net_control_password);
	
	System.out.println("lpr_db_url: "+SystemConfig.lpr_db_url);
	System.out.println("lpr_db_user : "+SystemConfig.lpr_db_user );
	System.out.println("lpr_db_password : "+SystemConfig.lpr_db_password );
	
	System.out.println("delay : "+SystemConfig.delay );
	System.out.println("gate_name : "+SystemConfig.gate_name );
	
	System.out.println("----------------------------------");
	
	db_url  = SystemConfig.db_url;
	db_user = SystemConfig.db_user;
	db_password = SystemConfig.db_password;
	media_url = SystemConfig.camera_ip;
	client_name = SystemConfig.client_name;
	ip_tomcat = SystemConfig.ip_tomcat;
	net_control_ip = SystemConfig.net_control_ip;
	net_control_password = SystemConfig.net_control_password;
	lpr_db_url = SystemConfig.lpr_db_url;
	lpr_db_user = SystemConfig.lpr_db_user;
	lpr_db_password = SystemConfig.lpr_db_password;
	delay = SystemConfig.delay;
	gate_name = SystemConfig.gate_name;
	
    paramConfigs();
	pathCropped=(String) getParam("2");
//	media_url=(String) getParam("4");

	
	
        if( initIFace() == false ) {
            setError("Failed to init IFace library!");
            
            System.exit(-1);
        }
        /* */
    	
    	initFaceBlacklists();
    	
    	
    	
    	ipCapture = new IPCameraCapture(this);        
        if( ipCapture.init() == false ) {
            setError("Failed to init Frame Graber!");
            
            System.exit(-1);            
        }
        
        // Set the size as determined by the grabber.
        initComponents(new Dimension(ipCapture.getWidth(), ipCapture.getHeight()));
        
        ipCapture.execute();
    }
    private void initFaceBlacklists(){
    	Connection conn  = null;
    	Statement st =null;
    	ResultSet rs = null;
    	faceBlacklists = new ArrayList<FaceBlacklist>();
    	try {
		      Class.forName(db_driver);
		      conn = DriverManager.getConnection(db_url, db_user, db_password);

		      String query = "SELECT * FROM blacklist_picture ";
		      
		      st = conn.createStatement();
		      rs = st.executeQuery(query);

			// new directory with files
			new File(System.getProperty("user.dir")+"\\profile_picture").mkdirs();
				
			while((rs!=null) && (rs.next()))
			{			
				FaceBlacklist faceBlacklist = new FaceBlacklist(); 
				faceBlacklist.setPictureId(rs.getInt("PICTURE_ID"));
				faceBlacklist.setProfileId(rs.getInt("PROFILE_ID"));
				String path = rs.getString("PATH")+"/";
				String fileName = rs.getString("FILE_NAME");

				// download image from serve
				URL url = new URL(path+fileName);
				InputStream in = new BufferedInputStream(url.openStream());
				OutputStream out = new BufferedOutputStream(new FileOutputStream(System.getProperty("user.dir")+"\\profile_picture\\"+fileName));

				for ( int i; (i = in.read()) != -1; ) {
				    out.write(i);
				}
				in.close();
				out.close();
				
				System.out.println("download image: "+path+fileName);

				Face[] faces = faceHandler.detectFaces(System.getProperty("user.dir")+"\\profile_picture\\"+fileName, 40, 200, 1);
				if(faces!=null && faces.length>0){
					faceBlacklist.setTempate(faces[0].createTemplate());
				}
		       
				faceBlacklists.add(faceBlacklist);
           }
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, e.getMessage());
			e.printStackTrace();
		}finally{
			try {
				rs.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				st.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    	
    	
    	
    }
	
    private  List<FaceMatching> fetchFaceMatching(){
    	Connection conn  = null;
    	Statement st =null;
    	ResultSet rs = null;
    	List<FaceMatching> faceMatchings = new ArrayList<FaceMatching>();
    	try {
    		 Class.forName(db_driver);
		     conn = DriverManager.getConnection(db_url, db_user, db_password);

		      String query = "SELECT * FROM face_matching fm LEFT JOIN blacklist_picture bpic "
		      		+ " ON fm.picture_id = bpic.PICTURE_ID "
		      		+ " LEFT JOIN blacklist_profile bpro ON bpro.PROFILE_ID = bpic.PROFILE_ID "
		      		+ " ORDER BY time_matching desc LIMIT 6 ";
		      
		      st = conn.createStatement();
		      rs = st.executeQuery(query);
		      SimpleDateFormat dt = new SimpleDateFormat("dd/mm/yyyyy hh:mm:ss"); 
		      
			while((rs!=null) && (rs.next()))
           {			
				FaceMatching faceMatching = new FaceMatching(); 
				faceMatching.setPictureId(rs.getInt("PICTURE_ID"));
				faceMatching.setPercent(rs.getString("PERCENT"));
				faceMatching.setTimeMatching(rs.getTimestamp("TIME_MATCHING"));
				faceMatching.setTimeMatchingStr(dt.format(rs.getTimestamp("TIME_MATCHING")));
				faceMatching.setPathSource(rs.getString("PATH_SOURCE"));
				faceMatching.setFileSource(rs.getString("FILE_SOURCE"));
				faceMatching.setTitle(rs.getString("TITLE"));
				faceMatching.setFirstName(rs.getString("FIRST_NAME"));
				faceMatching.setLastName(rs.getString("LAST_NAME"));
				
				faceMatching.setPathTarget(rs.getString("PATH"));
				faceMatching.setFileTarget(rs.getString("FILE_NAME"));
			
				
				faceMatchings.add(faceMatching);
           }
			
            
		} catch (Exception e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, e.getMessage());
			e.printStackTrace();
		}finally{
			try {
				rs.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				st.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    	return faceMatchings;
    }
    private void saveToTable(FaceMatching faceMatching){
    	Connection conn  = null;
    	PreparedStatement preparedStatement = null;

		java.util.Date today = new java.util.Date();
    	try {
    		 Class.forName(db_driver);
		     conn = DriverManager.getConnection(db_url, db_user, db_password);

		      String query = "INSERT INTO face_matching (matching_id,picture_id,time_matching,path_source,file_source,percent,detail,client_name) "
		      		+ "values (?,?,?,?,?,?,?,?) ";
		      preparedStatement  = conn.prepareStatement(query);
		      preparedStatement.setString(1, faceMatching.getMatchingId());
		      preparedStatement.setInt(2, faceMatching.getPictureId());
		      preparedStatement.setTimestamp(3,new java.sql.Timestamp(today.getTime()));
		      preparedStatement.setString(4, faceMatching.getPathSource());
		      preparedStatement.setString(5, faceMatching.getFileSource());
		      preparedStatement.setString(6, faceMatching.getPercent());
		      preparedStatement.setString(7, faceMatching.getDetail());
		      preparedStatement.setString(8, faceMatching.getClientName());
		      // execute insert SQL stetement
		      preparedStatement .executeUpdate();
		     
            
		} catch (Exception e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, e.getMessage());
			e.printStackTrace();
		}finally{
			try {
				preparedStatement.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
   }
    
    private void refreshTable(){
    	 List<FaceMatching> faceMatchings = fetchFaceMatching();
				  DefaultTableModel model = (DefaultTableModel)table.getModel();
				  int rowCount = model.getRowCount();
				//Remove rows one by one from the end of the table
				  for (int i = rowCount - 1; i >= 0; i--) {
					model.removeRow(i);
				  }
			      int row = 0;
				if(faceMatchings!=null && faceMatchings.size()>0){
					for(FaceMatching faceMatching:faceMatchings){
						model.addRow(new Object[0]);
						model.setValueAt(faceMatching.getTitle()+" "+faceMatching.getFirstName()+" "+faceMatching.getLastName(),row, 0);
						model.setValueAt(faceMatching.getPercent(), row, 1);
						model.setValueAt(faceMatching.getTimeMatchingStr(), row, 2);
						model.setValueAt(faceMatching.getPathSource()+"//"+faceMatching.getFileSource(), row, 3);
						model.setValueAt(faceMatching.getPathTarget()+"//"+faceMatching.getFileTarget(), row, 4);
						row++;
					}
					table.setRowSelectionInterval(0, 0);
					// set lasted matched
					int indexMathed = 0;
					String name = model.getValueAt(indexMathed, 0).toString();
					String persent = model.getValueAt(indexMathed, 1).toString();
					String datetime = model.getValueAt(indexMathed, 2).toString();
					//String datetime = model.getValueAt(selectRowIndex, 2).toString();
					String pic = model.getValueAt(indexMathed, 3).toString();
					
					String target = model.getValueAt(indexMathed, 4).toString();
					
					label.setText(name); 
					lblNewLabel_3.setText(persent);
					//lblNewLabel_4.setText(persent);
					lblNewLabel_5.setText(datetime);
					lblNewLabel_1.setIcon(new ImageIcon(scaleImage(pic, lblNewLabel_1.getWidth(), lblNewLabel_1.getWidth())));
					lblNewLabel_2.setIcon(new ImageIcon(scaleImage(target, lblNewLabel_2.getWidth(), lblNewLabel_2.getHeight())));
					//lblNewLabel_1.setIcon(new ImageIcon(pic));
					//lblNewLabel_2.setIcon(new ImageIcon(target));
				
					
				}
    }
   
// function upload image to server
	private String uploadImageFn(String fileName) throws ClientProtocolException, IOException{ // marker daris
    	HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

        
        HttpPost httppost = new HttpPost(ip_tomcat+"/IFACETech/admin/upload_match_jsp.jsp");
        File file = new File(System.getProperty("user.dir")+"\\matching_picture\\"+fileName);

        MultipartEntity mpEntity = new MultipartEntity();
        ContentBody cbFile = new FileBody(file, "image/jpeg");
        mpEntity.addPart("file", cbFile);
        httppost.setEntity(mpEntity);
       
        HttpResponse response = httpclient.execute(httppost);

        HttpEntity resEntity = response.getEntity();

        if (resEntity != null) {
        //  System.out.println(EntityUtils.toString(resEntity));
          fileName = EntityUtils.toString(resEntity);
        }
        //System.out.println("file name :"+fileName);
        httpclient.getConnectionManager().shutdown();
        return fileName.trim();
    }


// function delete directory
	void deleteDirectoryRecursionJava(File file) throws IOException {
    	  if (file.isDirectory()) {
    	    File[] entries = file.listFiles();
    	    if (entries != null) {
    	      for (File entry : entries) {
    	        deleteDirectoryRecursionJava(entry);
    	      }
    	    }
    	  }
    	  if (!file.delete()) {
    	    throw new IOException("Failed to delete " + file);
    	  }
    }

    
    private  void paramConfigs(){
    	Connection conn  = null;
    	Statement st =null;
    	ResultSet rs = null;
    	try {
    		 Class.forName(db_driver);
		     conn = DriverManager.getConnection(db_url, db_user, db_password);

		      String query = "SELECT * FROM parameter_config ";
		      
		      st = conn.createStatement();
		      rs = st.executeQuery(query);
		      while((rs!=null) && (rs.next()))
	           {	
		    	  paramconfig.put(rs.getString("PARAM_KEY"), rs.getString("PARAM_VALUE"));
		    	  
	           }
					
					
    	} catch (Exception e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, e.getMessage());
			e.printStackTrace();
		}finally{
			try {
				rs.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				st.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    	//return paramConfigx;
    }
    
    public static Object getParam(String key){
    	return paramconfig.get(key);
    }
    
    

    private void initComponents(Dimension canvasSize) 
    {
    	frame = new JFrame("iFACE Tech");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, width, height);
		frame.getContentPane().setLayout(null);
		
		
	       frame.setVisible(true);
	       
			
			lblNewLabel_1 = new JLabel("");
			lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
			Border border1 = BorderFactory.createLineBorder(Color.BLUE, 2);
			lblNewLabel_1.setBorder(border1);
			//lblNewLabel_1.setBounds(41, 325, 206, 200);
			//lblNewLabel_1.setBounds(41, 425, 206, 100);
			lblNewLabel_1.setBounds(1000, 30, 206, 220);
			frame.getContentPane().add(lblNewLabel_1);
			
			lblNewLabel_2 = new JLabel("");
			lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
			Border border2 = BorderFactory.createLineBorder(Color.BLUE, 2);
			lblNewLabel_2.setIcon(new ImageIcon(""));///Users/imake/im1.jpg"));
			lblNewLabel_2.setBorder(border2);
			//lblNewLabel_2.setBounds(770, 325, 206, 200);
			//lblNewLabel_2.setBounds(770, 425, 206, 100);
			lblNewLabel_2.setBounds(1000, 460, 206, 220);
			frame.getContentPane().add(lblNewLabel_2);
			
			lblNewLabel_3 = new JLabel("");
			lblNewLabel_3.setFont(new Font("Tahoma", Font.BOLD, 17));
			lblNewLabel_3.setHorizontalAlignment(SwingConstants.CENTER);
			Border border3 = BorderFactory.createLineBorder(Color.BLUE, 2);
			lblNewLabel_3.setBorder(border3);
			//lblNewLabel_3.setBounds(433, 345, 106, 36);
			lblNewLabel_3.setBounds(1080, 310, 106, 36);
			lblNewLabel_3.addMouseListener(new java.awt.event.MouseAdapter() {
				@Override
				public void mouseClicked(java.awt.event.MouseEvent arg0) {
					refreshTable();
					System.out.println("refresh");
				}
			});
			frame.getContentPane().add(lblNewLabel_3);
			
			JLabel lblNewLabel_6 = new JLabel("Capture Picture from Camera");
			lblNewLabel_6.setFont(new Font("Tahoma", Font.BOLD, 15));
			lblNewLabel_6.setBounds(1000, 5, 300, 20);
			frame.getContentPane().add(lblNewLabel_6);
			
			JLabel lblNewLabel_7 = new JLabel("Pictue from Watch List");
			lblNewLabel_7.setFont(new Font("Tahoma", Font.BOLD, 15));
			lblNewLabel_7.setBounds(1000, 430, 300, 20);
			frame.getContentPane().add(lblNewLabel_7);
			
			label = new JLabel("...");
			label.setFont(new Font("Tahoma", Font.BOLD, 14));
			label.setForeground(new Color(204, 0, 51));
			label.setAlignmentX(JLabel.CENTER);
			label.setBounds(1050, 245, 200, 55);
			frame.getContentPane().add(label);
			
			JLabel lblNewLabel_4 = new JLabel("ชื่อสกุล :");
			lblNewLabel_4.setFont(new Font("Tahoma", Font.BOLD, 12));
			lblNewLabel_4.setBounds(950, 245, 50, 55);
			frame.getContentPane().add(lblNewLabel_4);
			
			lblNewLabel_5 = new JLabel("...");
			lblNewLabel_5.setForeground(new Color(165, 42, 42));
			lblNewLabel_5.setFont(new Font("Tahoma", Font.BOLD, 14));
			lblNewLabel_5.setBounds(1050, 360, 200, 55);
			frame.getContentPane().add(lblNewLabel_5);
			
			JLabel label_1 = new JLabel("เปอร์เซ็นปรียบเทียบ :");
			label_1.setBounds(950, 310, 117, 52);
			label_1.setFont(new Font("Tahoma", Font.BOLD, 12));
			frame.getContentPane().add(label_1);
			
			JLabel label_2 = new JLabel("วันที่ตรวจจับ :");
			label_2.setBounds(950, 360, 106, 52);
			label_2.setFont(new Font("Tahoma", Font.BOLD, 12));
			frame.getContentPane().add(label_2);
			
			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setEnabled(false);
			scrollPane.setBounds(41, 550, 850, 130);//41, 550, 935, 100
			frame.getContentPane().add(scrollPane);
			
			/* */
			table = new JTable();
			 List<FaceMatching> faceMatchings = fetchFaceMatching();
			try {
				  DefaultTableModel model = (DefaultTableModel)table.getModel();
				  
				  DefaultTableCellRenderer header = new DefaultTableCellRenderer();
				  header.setFont(new Font("Tahoma", Font.BOLD, 12));
				  
				  
				  JLabel xx = new JLabel("ชื่อ-สกุล");
				  xx.setFont(new Font("Tahoma", Font.PLAIN, 12));
			      model.addColumn("ชื่อ-สกุล");
			      model.addColumn("เปอร์เซ็นการเปรียบเทียบ");
			      model.addColumn("วันที่ตรวจจับ");
			      model.addColumn("PATH_SOURCE");
			      model.addColumn("PATH_TARGET");
			      TableColumnModel columnModel = table.getColumnModel();
			      columnModel.getColumn(0).setHeaderRenderer(header);
			      columnModel.getColumn(1).setHeaderRenderer(header);
			      columnModel.getColumn(2).setHeaderRenderer(header);
			      columnModel.getColumn(3).setHeaderRenderer(header);
			      columnModel.getColumn(4).setHeaderRenderer(header);
			      int row = 0;
				if(faceMatchings!=null && faceMatchings.size()>0){
					for(FaceMatching faceMatching:faceMatchings){
						model.addRow(new Object[0]);
						model.setValueAt(faceMatching.getTitle()+" "+faceMatching.getFirstName()+" "+faceMatching.getLastName(),row, 0);
						model.setValueAt(faceMatching.getPercent(), row, 1);
						model.setValueAt(faceMatching.getTimeMatchingStr(), row, 2);
						model.setValueAt(faceMatching.getPathSource()+"//"+faceMatching.getFileSource(), row, 3);
						model.setValueAt(faceMatching.getPathTarget()+"//"+faceMatching.getFileTarget(), row, 4);
						row++;
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				JOptionPane.showMessageDialog(null, e.getMessage());
				e.printStackTrace();
			}
			
			
			table.addMouseListener(new java.awt.event.MouseAdapter() {
				@Override
				public void mouseClicked(java.awt.event.MouseEvent arg0) {
					TableModel model =table.getModel();
					int selectRowIndex = table.getSelectedRow();
					
					String name = model.getValueAt(selectRowIndex, 0).toString();
					String persent = model.getValueAt(selectRowIndex, 1).toString();
					String datetime = model.getValueAt(selectRowIndex, 2).toString();
					//String datetime = model.getValueAt(selectRowIndex, 2).toString();
					String pic = model.getValueAt(selectRowIndex, 3).toString();
					
					String target = model.getValueAt(selectRowIndex, 4).toString();
					
					label.setText(name);
					lblNewLabel_3.setText(persent);
					//lblNewLabel_4.setText(persent);
					lblNewLabel_5.setText(datetime);
					lblNewLabel_1.setIcon(new ImageIcon(scaleImage(pic, lblNewLabel_1.getWidth(), lblNewLabel_1.getWidth())));
					lblNewLabel_2.setIcon(new ImageIcon(scaleImage(target, lblNewLabel_2.getWidth(), lblNewLabel_2.getHeight())));
				}
			});
			table.setCellSelectionEnabled(false);
			table.setRowSelectionAllowed(true);
			table.setFont(new Font("Tahoma", Font.PLAIN, 12));
			scrollPane.setViewportView(table);
			/* */
			
		//	JPanel lblNewLabel = new JPanel();
			 canvas = new JPanel() {
		            private static final long serialVersionUID = 1L;

		            @Override public void paintComponent(Graphics g) {
		                super.paintComponent(g);
		                draw(g);
		            }
		        };
		
			Border border = BorderFactory.createLineBorder(Color.BLUE, 2);
			canvas.setBounds(41, 11, IPCameraCapture.video_width, IPCameraCapture.video_height);
			canvas.setBorder(border);
		    canvas.setPreferredSize(canvasSize);
			//frame.add(canvas, BorderLayout.CENTER);
			 
			frame.getContentPane().add(canvas) ;
			statusBar = new JLabel();        
			setVisible(true);
		
    }

    private boolean initIFace() {
        try {
            // if the licence was deployed using LicenceManager.exe
            iface.init();           
        //    IFace.initWithLicence(Files.readAllBytes(Paths.get("path_to_licence_file\\iengine.lic")));            
            
        } catch(Exception ex) {
            setError("IFace licence error! : " + ex.getMessage());

            return false;
        }

        try {
            startTime = System.currentTimeMillis();
            faceHandler = new FaceHandler();
            objectHandler = new VisualObjectHandler(faceHandler);
            objectHandler.setParam(Parameter.TRACK_MIN_EYE_DISTANCE, "50");
            objectHandler.setParam(Parameter.TRACK_MAX_EYE_DISTANCE, "100");
            objectHandler.setParam(Parameter.TRACK_FACE_DISCOVERY_FREQUENCE_MS, "2000");
            objects = new VisualObject[10];
            for (int i = 0; i < objects.length; i++) {
                objects[i] = new VisualObject(objectHandler);
            }
        } catch(Exception ex) {
            setError("IFace initialization error! : " + ex.getMessage());
            return false ;
        }

        setStatus("IFace engine intialized successfully");

        return true;
    }
    
    @Override
    public void onNewFrame(final BufferedImage image) {
        synchronized( model ) {
            // drop frame if busy
            if( model.processing == false ) {
                model.processing = true;
                final Thread processor = new Thread(new Runnable(){
                    @Override public void run() {
                        processImage(image);
                    }
                });
                processor.start();
            }
            
            // video preview
            fpsCounter.tick();
            model.updateModel(image);
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    canvas.repaint();
                }
            });
        }
    }
    
    private static boolean isWithinBounds(PointF p, int width, int height) {
        return (p.getX() >= 0) && (p.getX() <= width) && (p.getY() >= 0) && (p.getY() <= height);
    }
    
    private static boolean isWithinBounds(PointF[] points, int width, int height) {
        for (PointF p : points) {
            if (!isWithinBounds(p, width, height)) {
                return false;
            }
        }
        return true;
    }

    public void processImage(BufferedImage originalImage) {
        if (iface == null) { // IFace not yet inited
            return;
        }
        objectHandler.trackObjects(RawImageUtils.toRawBGRImage(originalImage), System.currentTimeMillis() - startTime, objects);
        List<Face> faceList = new ArrayList<Face>();
        List<VisualObject> trackedObjectList = new ArrayList<VisualObject>();
        
        for (VisualObject object : objects) {
            if (object.getState() == TrackedObjectState.TRACKED ) {
                faceList.add(object.getFace(TrackedObjectFaceType.LAST));
                trackedObjectList.add(object);
            } else if (object.getState() == TrackedObjectState.LOST) {
                object.clean();
            }
        }
        Face[] faces = faceList.toArray(new Face[faceList.size()]);
        VisualObject[] trackedObjects = trackedObjectList.toArray(new VisualObject[trackedObjectList.size()]);
    
        synchronized (model) { 
            model.updateModel(faces, trackedObjects);
            model.processing = false;
        }
    }
    
    public void draw(Graphics g)
    {
        final Graphics2D g2d = (Graphics2D) g;
        //System.out.println(model.getImage());
        g.drawImage(model.getImage(), 0, 0, null);
        
        Face[] faces = model.getFaces();
        VisualObject[] trackedObjects = model.getTrackedObjects();
        String persentConfig ="";
        persentConfig = (String) getParam("3");
        if( (faces != null) && (faces.length != 0) )
        {
            Color primary = Color.yellow;
            Color secondary = Color.lightGray;
            Color primaryErr = Color.red;
        
            for (int i = 0; i < faces.length; i++) {

                Rectangle boundingBox = trackedObjects[i].getBoundingBox();
                //if icao not outside scene                
                
                Point topLeft = boundingBox.getTopLeft();
                Point topRight= boundingBox.getTopRight();
                Point bottomLeft = boundingBox.getBottomLeft();
                Point bottomRight =  boundingBox.getBottomRight();
                
                boolean icaoInside = isWithinBounds(new PointF[] {topLeft.toPointF(), topRight.toPointF(), bottomLeft.toPointF(), bottomRight.toPointF()} , getWidth(), getHeight());
                

                g.setColor((icaoInside) ? primary : primaryErr);
                g2d.setStroke(new BasicStroke((i == 0) ? 2 : 1));                
                
                g.drawLine(topLeft.getX(), topLeft.getY(), topRight.getX(), topRight.getY());
                g.drawLine(topRight.getX(), topRight.getY(), bottomRight.getX(), bottomRight.getY());
                g.drawLine(bottomLeft.getX(), bottomLeft.getY(), bottomRight.getX(), bottomRight.getY());
                g.drawLine(bottomLeft.getX(), bottomLeft.getY(), topLeft.getX(), topLeft.getY());
                
                g.setFont(new Font("Dialog", Font.PLAIN, 20));
                g.drawString(Integer.toString(trackedObjects[i].getId()), topLeft.getX()-5, topLeft.getY()-5);
                
                Face face = faces[i];
                if ( face != null) {
                    FaceFeature[] features = face.getFeatures(
                            FaceFeatureId.NOSE_TIP, 
                            FaceFeatureId.MOUTH_LEFT_CORNER, 
                            FaceFeatureId.MOUTH_RIGHT_CORNER, 
                            FaceFeatureId.LEFT_EYE_INNER_CORNER,
                            FaceFeatureId.LEFT_EYE_OUTER_CORNER,
                            FaceFeatureId.RIGHT_EYE_INNER_CORNER,
                            FaceFeatureId.RIGHT_EYE_OUTER_CORNER);
                    
                    Point noseTip = features[0].getPos().toPoint();
                    Point mouthLeftCorner = features[1].getPos().toPoint();
                    Point mouthRightCorner = features[2].getPos().toPoint();
                    

                    Point leftEyeInnerCorner = features[3].getPos().toPoint();
                    Point leftEyeOuterCorner = features[4].getPos().toPoint();
                    
                    Point rightEyeInnerCorner = features[5].getPos().toPoint();
                    Point rightEyeOuterCorner = features[6].getPos().toPoint();
                    
                    
                    
                    Point leftEye = face.getBasicInfo().getLeftEye().toPoint();
                    Point rightEye = face.getBasicInfo().getRightEye().toPoint();
                    
                    int leftEyeWidth = (int)leftEyeInnerCorner.getDistanceTo(leftEyeOuterCorner);
                    int rightEyeWidth = (int)rightEyeInnerCorner.getDistanceTo(rightEyeOuterCorner);
                    
                    g2d.setStroke(new BasicStroke(1));
                    g.setColor(secondary);
                    g.drawOval(leftEye.getX() - leftEyeWidth / 2, leftEye.getY()- leftEyeWidth / 2, leftEyeWidth, leftEyeWidth);
                    g.drawOval(rightEye.getX()- rightEyeWidth / 2, rightEye.getY()- rightEyeWidth / 2, rightEyeWidth, rightEyeWidth);

                    drawCrossair(g, secondary, noseTip);
                    drawCrossair(g, secondary, mouthLeftCorner);
                    drawCrossair(g, secondary, mouthRightCorner);
                  //  byte[] template0 = getFaceTemplate(faceHandler, cfg.image0Path, cfg);
                    byte[] template1 = face.createTemplate();
                  
                    if(faceBlacklists!=null && faceBlacklists.size()>0){
                    	for(FaceBlacklist faceBlacklist : faceBlacklists){
                    		if(faceBlacklist.getTempate()!=null){
                    		try {
                    			 float matchingConfidence = faceHandler.matchTemplate(faceBlacklist.getTempate(), template1);
                    			 
                    			 if(matchingConfidence>100){
                    				 matchingConfidence = 100;
                    			 }
                    			 
                    			 System.out.println("+++++++++++++ prfile id["+faceBlacklist.getProfileId()+"] picture id["+faceBlacklist.getPictureId()+"] "+String.format("Matching confidence: %.2f", matchingConfidence));
                            	if(matchingConfidence > Integer.parseInt(persentConfig) ){
                    
                            		// open the sound file as a Java input stream
                            	    String gongFile = "lib/Warning.wav";
                            	    InputStream in = new FileInputStream(gongFile);

                            	    // create an audiostream from the inputstream
                            	    AudioStream audioStream = new AudioStream(in);

                            	    // play the audio clip with the audioplayer class
                            	    AudioPlayer.player.start(audioStream);
                            		
                            		Date date = new Date() ;
                                	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm") ;
                                	String fileName = dateFormat.format(date)+".jpg";
                                	byte[] cropped = face.getCropImage(FaceCropMethod.TOKEN_FRONTAL, ImageSaveType.JPG);
                                	
                                	// new directory matching_picture
                                	new File(System.getProperty("user.dir")+"\\matching_picture").mkdirs();
                                	
									Files.write(new File(System.getProperty("user.dir")+"\\matching_picture\\"+fileName).toPath(), cropped); // marker daris

									fileName = uploadImageFn(fileName);
									System.out.println("file name:"+fileName);
                            			// save to Table
                            			FaceMatching faceMatching = new FaceMatching();
                            			faceMatching.setMatchingId(genToken());
                            			faceMatching.setPictureId(faceBlacklist.getPictureId());
                            			faceMatching.setPathSource(pathCropped);
                            			faceMatching.setFileSource(fileName);
                            			faceMatching.setPercent(String.format("%.2f", matchingConfidence)+"%");
                            			faceMatching.setDetail("Detail");
                            			faceMatching.setClientName(client_name);
                            			saveToTable(faceMatching);
                            			refreshTable();
                            			
                                  		// daris
                        			Connection conn  = null;
                        	    	Statement st =null;
                        	    	ResultSet rs = null;
                        			
                  		      	try {
									   conn = DriverManager.getConnection(lpr_db_url, lpr_db_user, lpr_db_password);
									   String query = "SELECT * FROM lprtable l "
											   +"WHERE l.LogDate BETWEEN (SYSDATE() - INTERVAL "+delay+" SECOND) AND SYSDATE()"
									   		   +"AND l.suspect NOT LIKE '%WATCHLIST%' AND l.suspect LIKE '%MEMBER%'"
											   +"AND (l.lane = '"+gate_name+"'  OR '' = '"+gate_name+"')"
									   		   +"ORDER BY l.LogDate DESC LIMIT 1";
	                          		    st = conn.createStatement();
	                          		    rs = st.executeQuery(query);
	                          		    
	                          		  if((rs!=null) && (rs.next()))
	                              			{
	                                		    System.out.println(rs);
	                                      		netControl(1,1); // channel 1 open
	                                              System.out.println("DELAY :"+delay+" SEC.");
	                                              try {
	                                                  for (int loop=0; loop<delay ; loop++) {
	                                                      Thread.sleep(1000);
	                                                      System.out.print(".");
	                                                  }
	                                                  System.out.println("");
	                                              } catch (InterruptedException ie)
	                                              {
	                                                  Thread.currentThread().interrupt();
	                                              }
	                                              netControl(0,1); // channel 1 close
	                              			}
	                          		  
								} catch (SQLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
                            			break;
                            		}
                            } catch (IOException e) {
                            	e.printStackTrace();
                            }
                    	  }
                    	}
                    }
                }
            }
            
        } else {
            setStatus("No face detected");
        }

        drawFPS(g2d);
    }
  
    private void netControl(int digit,int channel) {
    	try
		{
      	    		  
      		String command = "C:\\udpcom_win\\udpcom_winxp -p 80 "+net_control_password+",s="+digit+channel+" "+net_control_ip;
      		System.out.println("cmd :"+command);
      		
    		@SuppressWarnings("unused")
    		Process process = Runtime.getRuntime().exec(command);
		
		} catch (IOException e)
		{
		    e.printStackTrace();
		}
        
    }
    
    
    private void drawCrossair(Graphics g, Color color, Point p) {
        g.setColor(color);
        g.drawLine(p.getX() - 6, p.getY(), p.getX() + 6, p.getY());
        g.drawLine(p.getX(), p.getY() - 6, p.getX(), p.getY() + 6);
    }
    
    private void drawFPS(Graphics2D g2d) {
        g2d.setColor(Color.red);
        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(2);

        g2d.drawString("FPS = " + format.format(fpsCounter.getFPS()), 10, 10);
    }
    
    private void setError(final String msg) {
        System.err.println(msg);
        setStatus(msg);
    }
    private void setStatus(final String msg) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {
                statusBar.setText(msg);
            }
        });
    }
    private String genToken(){
  		StringBuffer sb = new StringBuffer();
  	    for (int i = 36; i > 0; i -= 12) {
  	      int n = Math.min(12, Math.abs(i));
  	      sb.append(org.apache.commons.lang.StringUtils.leftPad(Long.toString(Math.round(Math.random() * Math.pow(36, n)), 36), n, '0'));
  	    }
  	    return sb.toString();
   }
    
    public static void main(String[] args) throws IOException {
    	new NativeDiscovery().discover();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
            	
                new IFaceTech().start();
            }
        });
    }
    
    
    private Image scaleImage(String filePath, int file_width,int file_height){
        BufferedImage img = null;
        Image dimg = null;
     try {
         img = ImageIO.read(new URL(filePath));     // img = ImageIO.read(new File(filePath));
         dimg = img.getScaledInstance(file_width, file_height,
              Image.SCALE_SMOOTH);
     } catch (IOException e) {
         e.printStackTrace();
     }
     
     return dimg;
       }
    
}





class FPSIPCounter {
    protected long timePrev = 0;
    protected double fps = 0;

    public void reset() {
        timePrev = 0;
        fps = 0;
    }
    
    public double tickAndCount() {
        tick();        
        
        return getFPS();
    }

    public double getFPS() { return fps; }
    
    public void tick() {
        if(timePrev == 0) {
            timePrev = System.currentTimeMillis();
            return;
        }
        
        double dT = ((double) (System.currentTimeMillis() - timePrev)) / 1000;
        double fpsNow = 1 / dT;
        timePrev = System.currentTimeMillis();
        
        if(fps == 0) {
            fps = fpsNow;
            return;
        }
        
        double increment = 1;
        
        fps = fps*(1-increment) + fpsNow*increment;
    }
   
    

}
