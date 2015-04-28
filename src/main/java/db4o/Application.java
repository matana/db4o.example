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
		
		queryByPredicateAgeHigherThan(db, 25, true);
		
		queryWithContraints(db, true);
		
		queryWithContraintsSorted(db, 20, true);
		
		updateObject(db, true);
		
		queryPetByName(db, "Gizmo 2", true);
		
		db.close();

	}

	/**
	 * 
	 * @param db
	 * @param print
	 */
	private static void updateObject(ObjectContainer db, boolean print) {

		List<Student> result = queryByPredicateAgeHigherThan(db, 30, false);
		
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
			printResults(result, "UPDATED", "#Update object");
	}

	/**
	 * 
	 * @param result
	 * @param label
	 * @param header
	 */
	private static void printResults(List<Student> result, String label, String header) {
		String lines = "";
		for (int i = 0; i < header.length(); i++) {
			lines += "-";
		}
		System.out.println(lines);
		System.out.println(header);
		System.out.println(lines);
		if(result.isEmpty())
			System.out.println(" > Nothing found...");
		else
			for (Student student : result)
				System.out.printf(" > %s :: %s \n", label, student);
	}

	/**
	 * 
	 * @param db
	 * @param print
	 */
	private static void queryByTargetType(ObjectContainer db, boolean print) {
		ObjectSet<Student> result = db.query(Student.class);
		if(print)
			printResults(result, "DB.ENTRY", "#Query by TargetType");
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
	private static List<Student> queryByPredicateAgeHigherThan(ObjectContainer db, final int age, boolean print) {

		ObjectSet<Student> result = db.query(new Predicate<Student>() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public boolean match(Student student) {
				return student.getAge() > age;
			}
		});
		
		if(print)
			printResults(result, "FOUND", "#Native query with Predicate<ExtentType>");
		
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
			printResults(result, "FOUND", "#Native query with Predicate<ExtentType>");
		
		return new ArrayList<Student>(result);
	}

	/**
	 * 
	 * @param db
	 * @param print
	 */
	private static void queryWithContraints(ObjectContainer db, boolean print) {
		
		Query q = db.query();
		q.constrain(new Student());
		q.descend("age")
			.constrain(25)
			.greater()
			.equal()
			.and(q.descend("forename")
					.constrain("Stephi")
					.startsWith(true));
		
		ObjectSet<Student> result = q.execute();
		
		printResults(result, "FOUND", "#Query with constraints");
	}
	
	/**
	 * 
	 * @param db
	 * @param print
	 */
	private static void queryWithContraintsSorted(ObjectContainer db, int age, boolean print) {
		
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
		
		printResults(result, "FOUND", "#Query with constraints - sorted");
		
	}

}
