package business.bundle;

import dataAccess.entity.Bundle;
import dataAccess.entity.Course;
import dataAccess.entity.User;

import java.util.List;

public interface IBundleService
{
	void add(List<Bundle> bundles);
	Bundle get(long id);
	List<Bundle> get(String courseName, String groupName, User authorFIO);
	List<Bundle> get(Course course, User author);
	List<Bundle> getAll(User author);
	void groupChanged(Bundle client);
	byte[] downloadReport(User initiator, Bundle client);
	Bundle uploadReport(User initiator, Bundle client, byte document[]);
	void cancel(User initiator, Bundle client);
	void delete(User initiator, Bundle client);
}
