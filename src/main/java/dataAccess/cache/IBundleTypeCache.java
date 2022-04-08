package dataAccess.cache;

import dataAccess.entity.BundleType;

import java.util.List;

public interface IBundleTypeCache
{
	boolean contains(long id);
	boolean isEmpty();
	BundleType get(long id);
	List<BundleType> get();
	void put(BundleType bundleType);
	void delete(long id);
}
