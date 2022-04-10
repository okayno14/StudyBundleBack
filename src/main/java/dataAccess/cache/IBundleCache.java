package dataAccess.cache;

import dataAccess.entity.Bundle;

public interface IBundleCache
{
	boolean contains(long id);
	boolean isEmpty();
	Bundle get(long id);
	void put(Bundle bundle);
	void delete(long id);
}
