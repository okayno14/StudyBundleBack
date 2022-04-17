package dataAccess.repository;

import dataAccess.entity.BundleType;
import dataAccess.entity.Requirement;

import java.util.List;

public interface IRequirementRepo
{
	void save(Requirement req);
	List<Requirement> get();
	void delete(Requirement req);
	long countReferences(Requirement req);
}
