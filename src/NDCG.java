import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLEncoder;

public class NDCG {
	
	public static void computeNDCG (List<String> google, List<String> patient){
		double IDCG = 12.32347;
		double[] DC = new double[5];		
		double DCG = 0.0;
		
		for (int i = 0; i < patient.size(); i++) {
			for (int j = 0; j < google.size(); j++) {
				String pStr = patient.get(i).substring(patient.get(i).indexOf("/"));
				String gStr = google.get(j).substring(google.get(j).indexOf("/"));
				
				if(pStr.substring(pStr.lastIndexOf("/")).contains("index"))
					pStr = pStr.substring(pStr.indexOf("/"), pStr.lastIndexOf("/"));
				else if(pStr.substring(pStr.length()-1).equals("/"))
					pStr = pStr.substring(pStr.indexOf("/"), pStr.length()-1);

				if(gStr.substring(gStr.lastIndexOf("/")).contains("index"))
					gStr = gStr.substring(gStr.indexOf("/"), gStr.lastIndexOf("/"));
				else if(gStr.substring(gStr.length()-1).equals("/"))
					gStr = gStr.substring(gStr.indexOf("/"), gStr.length()-1);

					if( pStr.equals(gStr) ) {
						double l =  log2(i+1);
						if(l == 0)	l = 1;
						DC[i] = (5-j)/l;
						DCG += DC[i];
						break;
					}//end of if
					
			} //end of for j
		}//end of for i
		
		double NDCG = (DCG / IDCG);
		System.out.println();
		System.out.println("NDCG: " + NDCG);
	}//end of computeNDCG

	public static double log2(double d) {
		return (double) (Math.log(d) / Math.log(2.0));
	}//end of log2
	
	
	public ArrayList<String> getGoogleResult(String query){
		ArrayList<String> googleResultList = new ArrayList<String>();
		String google = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&start=0&rsz=5&q=";
	    String search = "site:ics.uci.edu";
	    String charset = "UTF-8";
	    try  {
	    	URL url = new URL(google + URLEncoder.encode(search+" "+query, charset));
		    Reader reader = new InputStreamReader(url.openStream(), charset);
		    GoogleResults results = new Gson().fromJson(reader, GoogleResults.class);
		    int size = results.getResponseData().getResults().size();
		    for(int i = 0; i < size; i++){
		    //	if()
		    	googleResultList.add(results.getResponseData().getResults().get(i).getUrl());
		    }
		    	//System.out.println(googleResultList);
	    }catch(Exception e)
	    {
	    	e.printStackTrace();
	    }
	    
	    return googleResultList;
	}//end of getTop5 from google
	public static void pause() {
		try {
			System.in.read();
		} catch (IOException e) {
		}
	}// end of pause


}//end of NDCG
