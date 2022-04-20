package controller;

import business.IBundleTypeService;
import dataAccess.entity.BundleType;

import java.util.List;

public class BundleTypeController implements IBundleTypeController
{
	private Controller         controller;
	private IBundleTypeService service;

	public BundleTypeController(Controller controller, IBundleTypeService bundleTypeService)
	{
		this.controller = controller;
		this.service    = bundleTypeService;
	}

	@Override
	public void add(BundleType client)
	{
		service.add(client);
	}

	@Override
	public List<BundleType> get()
	{
		return service.get();
	}

	@Override
	public BundleType get(long id)
	{
		return service.get(id);
	}

	@Override
	public void delete(BundleType client)
	{
		service.delete(client);
	}

	@Override
	public void update(BundleType client, String name)
	{
		service.update(client, name);
	}
}
