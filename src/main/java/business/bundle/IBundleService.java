package business.bundle;

import dataAccess.entity.Bundle;
import dataAccess.entity.Course;
import dataAccess.entity.Group;
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
	//метод обработки события: группу отписывают от указанного перечня курсов
	//метод сработает в ситуации если инициатор состоит в редколлегии каждого из курсов
	//если полученный список групп не
	void groupMovedFromCourse(User initiator, List<Course> courseList, List<Group> groupList);
	byte[] downloadReport(User initiator, Bundle client);
	Bundle uploadReport(User initiator, Bundle client, byte document[]);
	void cancel(User initiator, Bundle client);
	void emptify(User initiator, Bundle client);
	//вызывается только в контексте удаления аккаунта
	void delete(User initiator, User target);
}
