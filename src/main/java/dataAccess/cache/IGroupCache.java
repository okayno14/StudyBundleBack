package dataAccess.cache;

import dataAccess.entity.Group;

public interface IGroupCache
{
	boolean contains(long id);
	boolean isEmpty();
	Group get(long id);
	void put(Group group);
	void delete(long id);
}
