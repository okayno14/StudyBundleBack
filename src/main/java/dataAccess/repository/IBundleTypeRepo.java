package dataAccess.repository;

import dataAccess.entity.BundleType;
import exception.DataAccess.NotUniqueException;

import java.util.List;

public interface IBundleTypeRepo
{
	List<BundleType> get();
	BundleType get(long id);
	void save(BundleType bundleType) throws NotUniqueException;
	long countReferences(BundleType bundleType);
	void delete(BundleType bundleType);
}
