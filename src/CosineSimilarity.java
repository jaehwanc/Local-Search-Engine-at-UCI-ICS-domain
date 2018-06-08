
public class CosineSimilarity {

	public static void computeCosineSimilarity(double docMat[][], double query[]) {
		// int[] a = {1,2,3,4,6,1,1};
		// int[] b = {2,3,3,4,6,1,2};
		// int[] c = {1,2,3,4,5,6,7};

		double[][] mat = docMat;

		// Cosine Similarity 1: 0.0884659586649982
		// Cosine Similarity 2: 0.08958440498683731
		//
		// Cosine Similarity 1: 0.8411784753765535
		// Cosine Similarity 2: 0.7071067811865476

		double[] q = query;

		double dotProduct = 0;
		double queryLength = 0.0;
		double docLength = 0.0;

		double[] cos = new double[mat.length];

		for (int i = 0; i < q.length; i++) {
			queryLength += Math.pow(q[i], 2);
		}//end of for

		queryLength = Math.sqrt(queryLength);

		for (int i = 0; i < mat.length; i++) {
			dotProduct = 0.0;
			docLength = 0.0;

			for (int j = 0; j < mat[0].length; j++) {
				dotProduct += mat[i][j] * q[j];
				docLength += Math.pow(mat[i][j], 2);
			}
			docLength = Math.sqrt(docLength);

			cos[i] = dotProduct / (queryLength * docLength);

		}//end of for

		for (int i = 0; i < cos.length; i++) {
			System.out.println("Cosine Similarity " + (i + 1) + ": " + cos[i]);
		}

	}//end of computeCosineSimilarity

	/*
	 * public static void main(String args[]) { double[][] mat =
	 * {{1,2,3,4,5,6},{1,2,5,1,2,6},{3,5,1,7,3,2}}; double[] query =
	 * {1,2,5,6,7,1}; computeCosineSimilarity(mat, query);
	 * 
	 * }
	 */
}//computeCosineSimilarity
