package controller;

import business.bundle.IBundleService;
import dataAccess.entity.Bundle;

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
}
