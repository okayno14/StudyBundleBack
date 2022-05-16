package view.HTTP.request;

import com.google.gson.annotations.Expose;
import dataAccess.entity.User;

import java.util.LinkedList;

public class IDReq
{
	@Expose
	private LinkedList<User> arr;

	public IDReq()
	{
	}

	public LinkedList<User> getArr()
	{
		return arr;
	}

	public void setArr(LinkedList<User> arr)
	{
		this.arr = arr;
	}
}

