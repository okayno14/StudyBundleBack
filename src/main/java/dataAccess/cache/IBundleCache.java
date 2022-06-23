package dataAccess.cache;

import business.bundle.BundlePredicate;
import dataAccess.entity.Bundle;

import java.util.List;

public interface IBundleCache
{
	boolean contains(long id);
	boolean isEmpty();
	Bundle get(long id);
	void put(Bundle bundle);
	void delete(long id);
	void clean();
	List<Bundle> filter(BundlePredicate bundlePredicate, Bundle sample);
}
