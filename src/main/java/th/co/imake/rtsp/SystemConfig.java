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
                }
                
            }
//            System.out.println("1 -> "+db_url);
//            System.out.println("2 -> "+db_user);
//            System.out.println("3 -> "+db_password);
            
            reader.close();
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
        
    }
}
