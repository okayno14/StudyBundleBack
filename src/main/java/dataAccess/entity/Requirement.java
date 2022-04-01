package dataAccess.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Requirement
{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private long        id = -1;

}
