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
	public void add()
	{
		service.add();
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
	public void delete()
	{
		service.delete();
	}

	@Override
	public void update(String name)
	{
		service.update(name);
	}

	@Override
	public BundleType getClient()
	{
		return service.getClient();
	}

	@Override
	public void setClient(BundleType client)
	{
		service.setClient(client);
	}
}
