package controller;

import business.bundle.IBundleService;
import dataAccess.entity.Bundle;
import dataAccess.entity.Course;
import dataAccess.entity.User;

import java.util.List;

public class BundleController implements IBundleController
{
	private Controller controller;
	private IBundleService bundleService;

	public BundleController(Controller controller, IBundleService bundleService)
	{
		this.controller    = controller;
		this.bundleService = bundleService;
	}

	@Override
	public void add(List<Bundle> bundles)
	{
		bundleService.add(bundles);
	}

	@Override
	public Bundle get(long id)
	{
		return null;
	}

	@Override
	public List<Bundle> get(String courseName, String groupName, User authorFIO)
	{
		return null;
	}

	@Override
	public List<Bundle> get(Course course, User author)
	{
		return null;
	}

	@Override
	public List<Bundle> getAll(User author)
	{
		return bundleService.getAll(author);
	}

	@Override
	public void groupChanged(Bundle client)
	{
		bundleService.groupChanged(client);
	}

	@Override
	public byte[] downloadReport(Bundle client)
	{
		return new byte[0];
	}

	@Override
	public void uploadReport(Bundle client, byte[] document)
	{

	}

	@Override
	public void decline(Bundle client)
	{

	}

	@Override
	public void delete(Bundle client)
	{

	}
}
