package controller;

import business.bundle.IBundleService;
import dataAccess.entity.*;
import exception.Business.BusinessException;
import exception.Business.NoRightException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BundleController implements IBundleController
{
	private Controller     controller;
	private IBundleService bundleService;

	public BundleController(Controller controller, IBundleService bundleService)
	{
		this.controller    = controller;
		this.bundleService = bundleService;
	}

	@Override
	public void add(List<Bundle> bundles)
	{
		bundleService.add(bundles);
	}

	@Override
	public Bundle get(long id)
	{
		return bundleService.get(id);
	}

	@Override
	public List<Bundle> get(String courseName, String groupName, User authorFIO)
	{
		return null;
	}

	@Override
	public List<Bundle> get(Course course, User author)
	{
		return bundleService.get(course,author);
	}

	@Override
	public List<Bundle> getAll(User author)
	{
		return bundleService.getAll(author);
	}

	@Override
	public void groupChanged(Bundle client)
	{
		bundleService.groupChanged(client);
	}

	@Override
	public void groupMovedFromCourse(User initiator, List<Course> courseList, List<Group> groupList)
	{
		//Пересечение членов редколлегии каждого из курсов
		HashSet<User>  users    = new HashSet<>();
		Set<CourseACL> firstACL = courseList.iterator().next().getACL();
		for (CourseACL courseACL : firstACL)
		{
			users.add(courseACL.getUser());
		}

		for (Course c : courseList)
		{
			HashSet<User> buf = new HashSet<>();
			for (CourseACL courseACL : c.getACL())
			{
				buf.add(courseACL.getUser());
			}
			users.retainAll(buf);
		}

		if(users.size()==0)
		{
			throw new BusinessException(new NoRightException());
		}
		//составили список для подмены админа,
		// но можем заранее проверить исключение
		if (!(initiator.getRole().getId() == controller.roleController.getAdmin().getId()) &&
				!users.contains(initiator))
		{
			throw new BusinessException(new NoRightException());
		}

		if(initiator.getRole().getId()==controller.roleController.getAdmin().getId())
		{
			initiator = users.iterator().next();
		}

		bundleService.groupMovedFromCourse(initiator, courseList, groupList);
	}

	@Override
	public byte[] downloadReport(User initiator, Bundle client)
	{
		if (initiator.getRole().getId() == controller.roleController.getAdmin().getId())
		{
			initiator = client.getAuthor();
		}
		return bundleService.downloadReport(initiator, client);
	}

	@Override
	public Bundle uploadReport(User initiator, Bundle client, byte[] document)
	{
		if (initiator.getRole().getId() == controller.roleController.getAdmin().getId())
		{
			initiator = client.getAuthor();
		}
		return bundleService.uploadReport(initiator, client, document);
	}

	@Override
	public void accept(User initiator, Bundle client)
	{
		if (initiator.getRole().getId() == controller.roleController.getAdmin().getId())
		{
			initiator = client.getCourse().getAuthor();
		}
		bundleService.accept(initiator, client);
	}

	@Override
	public void cancel(User initiator, Bundle client)
	{
		if (initiator.getRole().getId() == controller.roleController.getAdmin().getId())
		{
			initiator = client.getCourse().getAuthor();
		}
		bundleService.cancel(initiator, client);
	}

	@Override
	public void emptify(User initiator, Bundle client)
	{
		if (initiator.getRole().getId() == controller.roleController.getAdmin().getId())
		{
			initiator = client.getCourse().getAuthor();
		}
		bundleService.emptify(initiator, client);
	}

	@Override
	public void delete(User initiator, User target)
	{
		if(initiator.getRole().getId()==controller.roleController.getAdmin().getId())
		{
			initiator=target;
		}
		bundleService.delete(initiator, target);
	}
}
