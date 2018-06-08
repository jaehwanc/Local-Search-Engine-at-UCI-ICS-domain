import java.io.FileNotFoundException;
import java.io.FileReader;
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
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.TreeMap;

public class SearchEngine {
	public static final String DB = "jdbc:sqlite:/Users/yoonie/Documents/workspace/Milestone3_improved/storage/Data.db";
	private HashMap<Integer, Double> tfidfMap = new HashMap<Integer, Double>();    //docid, tfidf
	private HashMap<Integer, List<Integer>> positionMap = new HashMap<Integer, List<Integer>>();    //docid, position
	private HashMap<Integer, HashMap<Integer, Double>> score = new HashMap<Integer, HashMap<Integer, Double>>(); //nth, doc, result
	
	public void findQueryMatch(String input) throws FileNotFoundException, IOException {//find top 5 URLs for each of the 10 queries
		
		PageRank pg = new PageRank();
		//	Map<Integer, Double> pgr = pg.CalculatePageRank();
		Map<Integer, Double> pgr = pg.setPageRank();
		
		String[] query = input.split(" ");		
		try {
			Class.forName("org.sqlite.JDBC");
			Connection connection = null;		
			try {// create a database connection
				connection = DriverManager.getConnection(DB);
				connection.setAutoCommit(false);
				Statement statementData = connection.createStatement(); //read CrawledData
				Statement statementIndex = connection.createStatement(); //read IndexedResult
				statementData.setQueryTimeout(30); // set timeout to 30 sec.
				statementIndex.setQueryTimeout(30); // set timeout to 30 sec.
				double tfidf = 0.0;
				int docid = 0;				
				for(int cnt = 0 ; cnt< query.length ; cnt++){ //different query	
					System.out.println("query: " + query[cnt] );
					ResultSet index = statementIndex.executeQuery("select * from IndexedResult where uniqueword = '" + query[cnt] + "' ");			
					while(index.next()){ //different document
						tfidf = index.getDouble("tfidf");
						docid = index.getInt("docid");
						String temp = index.getString("position");				
						if(tfidfMap.containsKey(docid)){ //position 비교
							StringTokenizer st = new StringTokenizer(temp, "\n\t\f\r ");
							while (st.hasMoreTokens()){ //same document, different position
								int query_pos = Integer.parseInt(st.nextToken()) -1 ;
								if(positionMap.containsKey(docid) && positionMap.get(docid).contains(query_pos)){
									tfidf = (tfidf + tfidfMap.get(docid)) *100;
								}//end of if
								tfidfMap.put(docid, tfidf);		
							}//end of while		
						}//end of if
						
						else {//그냥 넣기  		
							List<Integer> lt = new ArrayList<Integer>();
							StringTokenizer st = new StringTokenizer(temp, "\n\t\f\r ");
							while (st.hasMoreTokens()){ //same document, different position
								int query_pos = Integer.parseInt(st.nextToken());	
								lt.add(query_pos);
							}//end of while
							positionMap.put(docid, lt);
							tfidfMap.put(docid, tfidf);
						}//end of else
					}//end of while  //different document		
			//		System.out.println("mapsize: "+ tfidfMap.size());
				}//end of for cnt
			//	System.out.println("final mapsize: "+ tfidfMap.size());				
				computeScore(pgr, input);
			} // end of try
			catch (SQLException e) {
				System.err.println(e.getMessage());
			} // end of catch
			finally {
				try {
					if (connection != null){
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

	}// end of function
	public void computeScore(Map<Integer, Double> pgr, String input){
		TreeMap<Double, Integer> sort = new TreeMap<Double, Integer>();
		Double result = 0.0;
		for(Integer key: tfidfMap.keySet()){
			if(pgr.containsKey(key)){
				result = tfidfMap.get(key) + pgr.get(key)*1000;
				sort.put(result, key);
			}
		}//end of for
		int countsort = 0;
		int countscore = 5;
		for(Double dkey: sort.keySet()){
			if(sort.size()-5 <= countsort){
				score.put(countscore, new HashMap<Integer,Double>());
				score.get(countscore).put(sort.get(dkey), dkey);
				countscore--;
			}//end of if
			countsort++;
		} // end of for
/*		
	 for(int i : score.keySet()){ 
		 for(int j : score.get(i).keySet())
		 System.out.println("score: "+ i + "	/docid: "+ j + "	/double: "+score.get(i).get(j)); 
	 }
	*/	
	 List<String> temp_urllist = getMatchData();
	 CompareWithGoogle(temp_urllist, input);
	}// end of computeScore

	
	public List<String> getMatchData() { // data 가지고 db에서 거기 url이랑 text내용들 읽어오는 거
		List<String> urllist = new ArrayList<String>();
		try {
			Class.forName("org.sqlite.JDBC");
			Connection connection = null;
			try {// create a database connection
				connection = DriverManager.getConnection(DB);
				connection.setAutoCommit(false);
				Statement statement = connection.createStatement();
				statement.setQueryTimeout(30); // set timeout to 30 sec.
				for (int sc : score.keySet()) {
					for (int doc : score.get(sc).keySet()) {
						ResultSet db = statement.executeQuery("select * from CrawledData where docid = '" + doc + "' ");
						System.out.println();
						String url = "";
						String text = "";
						while (db.next()) { // unique word 찾아내서 저장
							url = db.getString("url");
							text = db.getString("text");
							System.out.print("Score: " + sc + "	" + url);
							urllist.add(url);
						//	System.out.println(text.substring(0, 100));
						} // end of while
					} // end of for
				} // end of for	
				
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
		return urllist;
	}// end of getMatchData


	@SuppressWarnings("static-access")
	public void CompareWithGoogle(List<String> patient, String input){//NDCG
		NDCG ndcg = new NDCG();
		List<String> google = ndcg.getGoogleResult(input);
		System.out.println();
		System.out.println("Google search result");
		for(int i=0; i<google.size(); i++)
			System.out.println("Score: " + (i+1) + "	" +google.get(i));
	
			ndcg.computeNDCG(google, patient);
			
	}//end of compareWithGoogle
	
	
	public static void pause() {
		try {
			System.in.read();
		} catch (IOException e) {
		}
	}// end of pause

}//end of Parser