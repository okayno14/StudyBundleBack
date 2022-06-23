package view.HTTP.response.data;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

public class BundleAnalysisResult
{
	@Expose
	private JsonObject arr[] = new JsonObject[2];
	@Expose
	private int        percent;

	public void setBundle(JsonObject b)
	{
		arr[0] = b;
	}

	public void setBestMatch(JsonObject b)
	{
		arr[1] = b;
	}

	public void setPercent(float val)
	{
		percent = (int)(val*100);
	}
}
