package db4o;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;
import com.db4o.query.Query;

import db4o.model.Pet;
import db4o.model.Student;

public class Application {

	public static void main(String[] args) {
		
		ObjectContainer db = initDb();
		
		queryByTargetType(db, true);
		
		queryStudentByAgeHigherThan(db, 25, true);
		
		queryStudentbyAgeAndForename(db, 25, "Stephi", true);
		
		queryStudentAgeSorted(db, 20, true);
		
		updateStudentByAgeHigherThan(db, 30, true);
		
		queryPetByName(db, "Gizmo 2", true);
		
		deleteStudentByPetName(db, "Gizmo 1", true);

		db.close();

	}

	private static void deleteStudentByPetName(ObjectContainer db, String name, boolean print) {
		
		List<Student> toDelete = queryPetByName(db, name, true);
		
		for (Student student : toDelete) 
			db.delete(student);
		db.commit();
		
		printResults(db.query(Student.class), "DELETE", "#Delete object (Student)");
	}

	/**
	 * 
	 * @param db
	 * @param print
	 */
	private static void updateStudentByAgeHigherThan(ObjectContainer db, int age, boolean print) {

		List<Student> result = queryStudentByAgeHigherThan(db, age, false);
		
		int i = 1;
		for (Student student : result) {
			Pet haustier = student.getPet();
			if(haustier == null) {
				student.setPet(new Pet("Gizmo " + i));
				i++;
			}
			db.commit();
		}
		if(print)
			printResults(result, "UPDATE", "#Update object (Student)");
	}

	/**
	 * 
	 * @param result
	 * @param label
	 * @param header
	 */
	private static void printResults(List<Student> result, String label, String header) {
		String line = "";
		for (int i = 0; i < header.length(); i++) { line += "-"; }
		System.out.printf("%s\n%s\n%s\n",line, header, line);
		
		if(result.isEmpty())
			System.out.println(" > Nothing found...");
		else
			for (Student student : result)
				System.out.printf(" > %s :: %s \n", label, student);
		System.out.println();
	}

	/**
	 * 
	 * @param db
	 * @param print
	 */
	private static void queryByTargetType(ObjectContainer db, boolean print) {
		ObjectSet<Student> result = db.query(Student.class);
		if(print)
			printResults(result, "DB.ENTRY", "#Query by TargetType - db.query(Student.class)");
	}
	
	/**
	 * 
	 * @return
	 */
	private static List<Student> populateObjetcs() {
		List<Student> list = new ArrayList<>();
		list.add(new Student("Polo","Mirko", 1, 21));
		list.add(new Student("Bingo", "Mingo", 2, 20));
		list.add(new Student("Schwielbärth", "Stephi", 3, 40));
		list.add(new Student("Bitte", "Stephi", 4, 34));
		list.add(new Student("Düster", "Nicole", 5, 24));
		list.add(new Student("Thomas", "Roman", 6, 27));
		return list;
	}

	/**
	 * 
	 * @return
	 */
	private static ObjectContainer initDb() {
		ObjectContainer db = Db4o.openFile(Db4o.newConfiguration(), "data.db");
		List<Student> list = populateObjetcs();
		ObjectSet<Student> result = db.query(Student.class);
		boolean removeAll = list.removeAll(new HashSet<>(result));
		if(removeAll || result.size() == 0)
			for (Student student : list) {
				db.store(student);
		}
		return db;
	}

	/**
	 * 
	 * @param db
	 * @param age
	 * @param print
	 * @return
	 */
	private static List<Student> queryStudentByAgeHigherThan(ObjectContainer db, final int age, boolean print) {

		ObjectSet<Student> result = db.query(new Predicate<Student>() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public boolean match(Student student) {
				return student.getAge() > age;
			}
		});
		
		if(print)
			printResults(result, "RESULT", "#Native query with Predicate<ExtentType> - db.query(new Predicate<Student>(){...});");
		
		return new ArrayList<Student>(result);
	}
	
	/**
	 * 
	 * @param db
	 * @param age
	 * @param print
	 * @return
	 */
	private static List<Student> queryPetByName(ObjectContainer db, final String name, boolean print) {

		ObjectSet<Student> result = db.query(new Predicate<Student>() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public boolean match(Student student) {
				Pet haustier = student.getPet();
				if(haustier != null)
					return haustier.getName().equalsIgnoreCase(name);
				return false;
			}
		});
		
		if(print)
			printResults(result, "RESULT", "#Native query - find nested object (Pet) by 'name' attribute");
		
		return new ArrayList<Student>(result);
	}

	/**
	 * 
	 * @param db
	 * @param print
	 */
	private static void queryStudentbyAgeAndForename(ObjectContainer db, int age, String forename, boolean print) {
		
		Query q = db.query();
		q.constrain(new Student());
		q.descend("age")
			.constrain(age)
			.greater()
			.equal()
			.and(q.descend("forename")
					.constrain(forename)
					.startsWith(true));
		
		ObjectSet<Student> result = q.execute();
		
		printResults(result, "RESULT", "#Query with constraints");
	}
	
	/**
	 * 
	 * @param db
	 * @param print
	 */
	private static void queryStudentAgeSorted(ObjectContainer db, int age, boolean print) {
		
		Query q = db.query();
		q.constrain(new Student());
		q.descend("age")
			.constrain(age)
			.greater()
			.equal();
		
		q.sortBy(new Comparator<Student>() {

			@Override
			public int compare(Student o1, Student o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		
		ObjectSet<Student> result = q.execute();
		
		printResults(result, "RESULT", "#Query with constraints - sorted by 'name' attribute");
		
	}

}
