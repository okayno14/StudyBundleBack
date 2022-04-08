package business;

import dataAccess.entity.BundleType;

import java.util.List;

public interface IBundleTypeService
{
	void add();
	BundleType get(long id);
	List<BundleType> get();
	void update(String name);
	void delete();

	BundleType getClient();
	void setClient(BundleType client);
}
