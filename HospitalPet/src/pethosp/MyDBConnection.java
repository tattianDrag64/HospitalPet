package pethosp;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;


public class MyDBConnection {
static Connection conn=null;
	
	static Connection getConnection() {
		
		try {
			Class.forName("org.h2.Driver");
			conn=DriverManager.getConnection("jdbc:h2:tcp://localhost/C:/Users/Public/Databases", "sa", "1234");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return conn;
	}
}