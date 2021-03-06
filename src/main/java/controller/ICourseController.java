package controller;

import business.ICourseService;
import dataAccess.entity.Bundle;
import dataAccess.entity.Course;
import dataAccess.entity.Group;
import dataAccess.entity.User;

import java.util.List;

public interface ICourseController extends ICourseService
{
	void GroupChanged(Group g, List<User> userList);
}
