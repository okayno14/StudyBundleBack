package dataAccess.repository;

import dataAccess.entity.Bundle;

import java.io.OutputStream;
import java.util.List;

public interface IBundleRepoFile
{
	void save(Bundle bundle, byte array[]);
	byte[] get(Bundle bundle);
	void fillTextVector(Bundle bundle);
	void fillTextVector(List<Bundle> bundleList);
	void moveGroupChanged(Bundle client, String destinationFolder);
	void delete(Bundle bundle);
}
