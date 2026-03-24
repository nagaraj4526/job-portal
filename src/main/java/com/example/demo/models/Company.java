package com.example.demo.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Entity
@Table(name="company")
public class Company {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private  int id;
	 private String companyName;
	 private String jobtitle;
	    private String location;
	    private int salary;

	    @ManyToOne
	    @JoinColumn(name = "admin_id")
	    
	    private Admin admin;

		
		

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getCompanyName() {
			return companyName;
		}

		public void setCompanyName(String companyName) {
			this.companyName = companyName;
		}

		public String getLocation() {
			return location;
		}

		public void setLocation(String location) {
			this.location = location;
		}

		

		public int getSalary() {
			return salary;
		}

		public void setSalary(int salary) {
			this.salary = salary;
		}

		public Admin getAdmin() {
			return admin;
		}

		public void setAdmin(Admin admin) {
			this.admin = admin;
		}

		public String getJobtitle() {
			return jobtitle;
		}

		public void setJobtitle(String jobtitle) {
			this.jobtitle = jobtitle;
		}

		
	    
		

	

}
