package dataAccess.cache;

import dataAccess.entity.Requirement;

import java.util.List;

public interface IRequirementCache
{
	boolean contains(long id);
	boolean contains(Requirement req);
	boolean isEmpty();
	Requirement get(long id);
	List<Requirement> get();
	void put(Requirement req);
	void delete(long id);
}
