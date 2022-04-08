package dataAccess.repository;

import dataAccess.entity.BundleType;
import exception.DataAccess.NotUniqueException;

import java.util.List;

public interface IBundleTypeRepo
{
	List<BundleType> get();
	void save(BundleType bundleType) throws NotUniqueException;
	void delete(BundleType bundleType);
}
