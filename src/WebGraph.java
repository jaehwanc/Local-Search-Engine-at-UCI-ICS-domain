
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class WebGraph {
	
	public static final String DB = "jdbc:sqlite:/Users/yoonie/Documents/workspace/Milestone3_improved/storage/Data.db";

	private Map<Integer, List<Integer>> InLinks = new HashMap<Integer, List<Integer>>();
//	private Map<Integer, Integer[]> In = new HashMap<Integer, Integer[]>();
	private Map<Integer,  List<Integer>> OutLinks = new HashMap<Integer, List<Integer>>();
	private Map<String, Integer > matchURL = new HashMap<String, Integer>();
	
	public void getLinks(){
		try {
			Class.forName("org.sqlite.JDBC");
			Connection connection = null;
			try {
				// create a database connection
				connection = DriverManager.getConnection(DB);
				connection.setAutoCommit(false);
				Statement stmt_in = connection.createStatement(); 
				stmt_in.setQueryTimeout(30); // set timeout to 30 sec.
				Statement stmt_out = connection.createStatement(); 
				stmt_out.setQueryTimeout(30); // set timeout to 30 sec.

				ResultSet in_set= stmt_in.executeQuery("select * from InLinks");
				while (in_set.next()) {
					InLinks.put(in_set.getInt("fromlink"), new ArrayList<Integer>());
					StringTokenizer st = new StringTokenizer(in_set.getString("tolink"), "{}@#$%!^/&*():;',.\n\t\f\r\"'`~=-_+?|][ ");
					while(st.hasMoreTokens())
						InLinks.get(in_set.getInt("fromlink")).add(Integer.parseInt(st.nextToken()));
				}//end of while
	
				ResultSet out_set = stmt_out.executeQuery("select * from OutLinks");
				while (out_set.next()) { 
					OutLinks.put(out_set.getInt("fromlink"), new ArrayList<Integer>());
					StringTokenizer st2 = new StringTokenizer(out_set.getString("tolink"), "{}@#$%!^/&*():;',.\n\t\f\r\"'`~=-_+?|][ ");
					while (st2.hasMoreTokens())		
						OutLinks.get(out_set.getInt("fromlink")).add(Integer.parseInt(st2.nextToken()));	
				}//end of while
			} // end of try

			catch (SQLException e) {
				System.err.println(e.getMessage());
			} // end of catch
			finally {
				try {
					if (connection != null) {
						connection.setAutoCommit(true);
						connection.close();
					}
				} // end of try
				catch (SQLException e) { // connection close failed.
					System.err.println(e);
				} // end of catch
			} // end of finally
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}//end of getLink
	
	public void buildWebGraph(){
		try {
			Class.forName("org.sqlite.JDBC");
			Connection connection = null;

			try {
				// create a database connection
				connection = DriverManager.getConnection(DB);
				connection.setAutoCommit(false);
				Statement statementURL = connection.createStatement(); 
				statementURL.setQueryTimeout(30); // set timeout to 30 sec.
				Statement statementKey = connection.createStatement(); 
				statementKey.setQueryTimeout(30); // set timeout to 30 sec.
				Statement statementIn = connection.createStatement(); 
				statementIn.setQueryTimeout(30); // set timeout to 30 sec.
				Statement statementOut = connection.createStatement(); 
				statementOut.setQueryTimeout(30); // set timeout to 30 sec.
				
				int cnt =0;
				ResultSet urlSet = statementURL.executeQuery("select * from PageLinks");
				while (urlSet.next()) { // different document
					int docid =  urlSet.getInt("docid");
					String str = urlSet.getString("url").trim();
					matchURL.put(str, docid);
				//	System.out.println("id: "+id+", docid : "+ docid+ "	urlSet: "+ str);
					cnt++;
					System.out.println("cnt " + cnt);
				//	pause();
				}
				urlSet.close();
				ResultSet keySet = statementKey.executeQuery("select * from PageLinks");
				cnt = 0;
				while (keySet.next()) { // different document
					cnt++;
					System.out.println("cnt"+  cnt);
					String url = keySet.getString("url").trim();
					int docid = keySet.getInt("docid");
					addOutLink(docid, matchURL.get(url));
					addInLink(matchURL.get(url), docid);
				} // end of while

				for (Integer key : OutLinks.keySet()) {
					List<Integer> list = OutLinks.get(key);
					for (int i = 0; i < list.size(); i++)
						addInLink(list.get(i), key);
				} // end of for
				
			
				System.out.println("writing in" );
				for(Integer key: InLinks.keySet()){
					String str="";
					for(int p = 0; p<InLinks.get(key).size() ; p++){
						System.out.println("key"+ key+ "	" +p+"/"+InLinks.get(key).size() );
						str += InLinks.get(key).get(p);
						str += " ";
					//	statementIn.executeUpdate("insert into InLinks values(null, '"+ key +"','"+InLinks.get(key)+ "')");				
					}
					statementIn.executeUpdate("insert into InLinks values(null, '"+ key +"','"+str+ "')");
				}
				pause();
				
				System.out.println("writing out");
				for(Integer kkey: OutLinks.keySet()){
					String str="";
					for(int p = 0; p<OutLinks.get(kkey).size() ; p++){
						str += OutLinks.get(kkey).get(p);
						str += " ";
						System.out.println("kkey"+ kkey+ "	" +p+"/"+OutLinks.get(kkey).size() );	
						//statementOut.executeUpdate("insert into OutLinks values(null, '"+ kkey+"','"+OutLinks.get(kkey)+ "')");			
					}
					statementOut.executeUpdate("insert into OutLinks values(null, '"+ kkey+"','"+str+ "')");
				}
				pause();
			} // end of try

			catch (SQLException e) {

				System.err.println(e.getMessage());
			} // end of catch
			finally {
				try {
					if (connection != null) {
						connection.setAutoCommit(true);
						connection.close();
					}
				} // end of try
				catch (SQLException e) { // connection close failed.
					System.err.println(e);
				} // end of catch
			} // end of finally
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}//end of buildWebGraph
	

	public void addInLink (Integer fromLink, Integer toLink) {
		if(!InLinks.containsKey(fromLink)) 
			InLinks.put(fromLink,  new ArrayList<Integer>());
		if(!InLinks.get(fromLink).contains(toLink))
			InLinks.get(fromLink).add(toLink);	

	}//end of inLink

	public void addOutLink(Integer fromLink, Integer toLink) {
		if(!OutLinks.containsKey(fromLink)) 
			OutLinks.put(fromLink,  new ArrayList<Integer>());
		if(!OutLinks.get(fromLink).contains(toLink))
			OutLinks.get(fromLink).add(toLink);			
	}//end of outLink
	
	public Map<Integer, List<Integer>> getInLink(){
		return InLinks;
	}
	public Map<Integer, List<Integer>> getOutLink(){
		return OutLinks;
	}
	private void pause() {
	    try {
	      System.in.read();
	    } catch (IOException e) { }
 }//end of pause
}//end of WebGraph
