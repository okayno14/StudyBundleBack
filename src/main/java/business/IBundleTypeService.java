package business;

import dataAccess.entity.BundleType;

import java.util.List;

public interface IBundleTypeService
{
	void add(BundleType client);
	BundleType get(long id);
	List<BundleType> get();
	void update(BundleType client, String name);
	void delete(BundleType client);
}
