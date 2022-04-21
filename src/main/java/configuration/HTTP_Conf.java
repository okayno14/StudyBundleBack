package configuration;

import com.google.gson.annotations.Expose;

public class HTTP_Conf
{
	@Expose
	int port;

	public HTTP_Conf(int port)
	{
		this.port = port;
	}

	public int getPort()
	{
		return port;
	}
}
