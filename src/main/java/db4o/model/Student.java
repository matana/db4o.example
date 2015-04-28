package db4o.model;

import java.io.Serializable;

public class Student implements Serializable {

	private static final long serialVersionUID = 7836132392288847209L;
	private String name;
	private String forename;
	private Integer id;
	private Integer age;
	private Pet pet;
	
	public Student() {
	}
	
	public Student(String name, String forename, Integer id, Integer age) {
		setName(name);
		setForename(forename);
		setId(id);
		setAge(age);
	}
	

	public void setAge(Integer age) {
		this.age = age;
	}
	
	public void setForename(String forename) {
		this.forename = forename;
	}
	
	public String getForename() {
		return forename;
	}
	
	public Integer getAge() {
		return age;
	}

	public Integer getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	public Pet getPet() {
		return pet;
		
	}

	public void setPet(Pet pet) {
		this.pet = pet;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((age == null) ? 0 : age.hashCode());
		result = prime * result
				+ ((forename == null) ? 0 : forename.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((pet == null) ? 0 : pet.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Student other = (Student) obj;
		if (age == null) {
			if (other.age != null)
				return false;
		} else if (!age.equals(other.age))
			return false;
		if (forename == null) {
			if (other.forename != null)
				return false;
		} else if (!forename.equals(other.forename))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (pet == null) {
			if (other.pet != null)
				return false;
		} else if (!pet.equals(other.pet))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Student [name=" + name + ", forename=" + forename + ", id="
				+ id + ", age=" + age + ", pet=" + pet + "]";
	}
	
	
	
}
