package th.co.imake.rtsp;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SystemConfig {
	public static String db_url = "";
	public static String db_user = "";
	public static String db_password = "";  
	public static String camera_ip = ""; 
	public static String client_name = ""; 
	public static String ip_tomcat = "";
	public static String net_control_ip = "";
	public static String net_control_password = "";
	public static String lpr_db_url= "";
	public static String lpr_db_user  = "";
	public static String lpr_db_password  = "";
	public static int delay  = 10;
	public static String gate_name  = "";
 
	public void setConfig() {
        try {
//        	System.out.println(System.getProperty("user.dir"));
            Path file = Paths.get("config.ini");
            BufferedReader reader = Files.newBufferedReader(file , 
                    StandardCharsets.UTF_8);
            String line = null;
            String headTxT = null;
            String detailTxT = null;
            String[] readLine ;

            while ((line = reader.readLine()) != null) {
            	
//              System.out.println(line);
                readLine = line.split("=");
               
                headTxT = readLine[0].trim().toString();
                detailTxT = readLine[1].trim().toString();

                if(headTxT.equals("db_url")){
                	db_url = detailTxT;
                }else if(headTxT.equals("db_user")){
                	db_user = detailTxT;
                }else if(headTxT.equals("db_password")){
                	db_password = detailTxT;
                }else if(headTxT.equals("camera_ip")){
                	camera_ip = detailTxT;
                }else if(headTxT.equals("client_name")){
                	client_name = detailTxT;
                }else if(headTxT.equals("ip_tomcat")){
                	ip_tomcat = detailTxT;
                }else if(headTxT.equals("net_control_ip")){
                	net_control_ip = detailTxT;
                }else if(headTxT.equals("net_control_password")){
                	net_control_password = detailTxT;
                }else if(headTxT.equals("lpr_db_url")){
                	lpr_db_url = detailTxT;
                }else if(headTxT.equals("lpr_db_user")){
                	lpr_db_user = detailTxT;
                }else if(headTxT.equals("lpr_db_password")){
                	lpr_db_password = detailTxT;
                }else if(headTxT.equals("delay")){
                	delay = Integer.parseInt(detailTxT) ;
                }else if(headTxT.equals("gate_name")){
                	gate_name = detailTxT;
                }
            }
            
            reader.close();
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
        
    }
}
