import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class Start {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		// TODO Auto-generated method stub
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("start");

			for (int i = 1; i <= 10; i++) {	
				System.out.println();
				System.out.print("Input "+i+"st query: ");
				String input = br.readLine();
				SearchEngine query = new SearchEngine();
				System.out.println();
				//System.out.println("start query match");
				query.findQueryMatch(input);
				//query.computeScore(pgr, input);	
			}//end of for
			
			System.out.println("end");
	}//end of main

}//end of Query
