import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageRank {
	public static final String STOP_WORD = "/Users/yoonie/Documents/workspace/Milestone3_improved/storage/stopword.txt";
	public static final String DB = "jdbc:sqlite:/Users/yoonie/Documents/workspace/Milestone3_improved/storage/Data.db";

	private double dampening = 0.85;
	//private Map<Integer, Double> scores =  new HashMap<Integer, Double>();
	private Map<Integer, List<Integer>> InLinks = new HashMap<Integer, List<Integer>>();
	private Map<Integer,  List<Integer>> OutLinks = new HashMap<Integer, List<Integer>>();
	
	public Map<Integer, Double> CalculatePageRank(){
		// PR(A) = (1-d) + d (PR(T1)/C(T1) + ... + PR(Tn)/C(Tn))
		//
		// Where
		// A is a page
		// d is a damping factor (usually 0.85)
		// T1...Tn are pages that link to A
		// PR(Ti) is the PageRank of Ti
		// C(Ti) is the number of outgoing links from Ti
		System.out.println("Link Graph making start!!");
		WebGraph graph = new WebGraph();
	//	graph.buildWebGraph();
		graph.getLinks();
		InLinks = graph.getInLink();
		OutLinks = graph.getOutLink();
		System.out.println("Link Graph finished!!" );// pausing..");
		//pause();
		
		Map<Integer, Double> pgr = new HashMap<Integer, Double>(); // docid,pageRank											
		
			try {
				Class.forName("org.sqlite.JDBC");
				Connection connection = null;

				try {
					// create a database connection
					connection = DriverManager.getConnection(DB);
					connection.setAutoCommit(false);
					Statement statementData = connection.createStatement(); // read
					statementData.setQueryTimeout(30); // set timeout to 30 sec.
					Statement statement = connection.createStatement(); // read
					statement.setQueryTimeout(30); // set timeout to 30 sec.
					System.out.println("Page Rank Calculation start!!");
					
				for (int iter = 0; iter < 500; iter++) {// iteration
					System.out.println("iteration count: " +iter);
					ResultSet db = statementData.executeQuery("select * from CrawledData");
					int pagecnt = 0;
					while (db.next()) { // different document
						pagecnt++;
					//	System.out.println("page cnt " + pagecnt);
						int docid = db.getInt("docid");	
						if (InLinks.get(docid) != null) {
							double prc = 0.0;
							for (int i = 0; i < InLinks.get(docid).size(); i++) { // incoming link개수만큼
								if (pgr.containsKey(InLinks.get(docid).get(i)))
									prc += pgr.get(InLinks.get(docid).get(i))/(OutLinks.get(docid).size());
								else
									prc += 1 / (OutLinks.get(docid).size());
							} // end of for
							pgr.put(docid, ((1 - dampening) + dampening * prc));
						//	System.out.println("pgr " + pgr.get(docid));
						} else
							pgr.put(docid, 1.0);

					} // end of while db.next()
				} // end of iteration
				for (Integer key : pgr.keySet())
					statement.executeUpdate("insert into PageRank values('" + key + "','" + pgr.get(key) + "')");

				System.out.println("Page Rank Calculation finished!!");// pausing..");
				//pause();
			} // end of try

				catch (SQLException e) {
					// if the error message is "out of memory", it probably
					// means no
					// database file is found
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
			return pgr;
	}// end of CalculatePageRank
	
	public Map<Integer, Double> setPageRank(){
		Map<Integer, Double> pgr = new HashMap<Integer, Double>(); // docid,pageRank											
		
		try {
			Class.forName("org.sqlite.JDBC");
			Connection connection = null;
			try {
				// create a database connection
				connection = DriverManager.getConnection(DB);
				connection.setAutoCommit(false);
				Statement stmt = connection.createStatement(); // read
				stmt.setQueryTimeout(30); // set timeout to 30 sec.
				ResultSet pagedb= stmt.executeQuery("select * from PageRank");
				while(pagedb.next())
					pgr.put(pagedb.getInt("docid"), pagedb.getDouble("score"));
			} // end of try

			catch (SQLException e) {
				// if the error message is "out of memory", it probably
				// means no
				// database file is found
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
		return pgr;
	}//end of setPageRank
	
	public double getPageRank(int doc){
		double rankScore = 0.0;
		try {
			Class.forName("org.sqlite.JDBC");
			Connection connection = null;

			try {
				// create a database connection
				connection = DriverManager.getConnection(DB);
				connection.setAutoCommit(false);
				Statement st = connection.createStatement(); // read
				st.setQueryTimeout(30); // set timeout to 30 sec.			
				ResultSet db = st.executeQuery("select * from PageRank where docid = '" + doc + "' ");
				while (db.next()) { // different document
					rankScore = db.getDouble("score");
				} // end of while db.next()
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
		return rankScore;
	}
	
	private void pause() {
	    try {
	      System.in.read();
	    } catch (IOException e) { }
 }//end of pause
}//end of PageRank
