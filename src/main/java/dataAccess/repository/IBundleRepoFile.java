package dataAccess.repository;

import dataAccess.entity.Bundle;

import java.io.OutputStream;
import java.util.List;

public interface IBundleRepoFile
{
	void save(Bundle bundle, byte array[]);
	byte[] get(Bundle bundle);
	void fillReport(Bundle bundle);
	void fillReport(List<Bundle> bundleList);
	void delete(Bundle bundle);
}
