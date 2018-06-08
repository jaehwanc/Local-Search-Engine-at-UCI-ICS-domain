
import java.util.List;

public class GoogleResults{
	 
	private ResponseData responseData;

	public ResponseData getResponseData() {
		return responseData;
	}//end of getResponseData

	public void setResponseData(ResponseData responseData) {
		this.responseData = responseData;
	}//end of setResponseData

	public String toString() {
		return "ResponseData[" + responseData + "]";
	}//end of toString

	public static class ResponseData {
		private List<Result> results;

		public List<Result> getResults() {
			return results;
		}

		public void setResults(List<Result> results) {
			this.results = results;
		}

		public String toString() {
			return "Results[" + results + "]";
		}
	}//end of class responseData

	public static class Result {
		private String url;
		private String title;

		public String getUrl() {
			return url;
		}

		public String getTitle() {
			return title;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String toString() {
			return "Result[url:" + url + ",title:" + title + "]";
		}
	}//end of class result
}//end of googleResult