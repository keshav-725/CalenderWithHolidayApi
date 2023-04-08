package org.keshav.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "holidays")
public class Holiday {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
    private String name;
    private LocalDate date;
    private String type;
	public Holiday() {
		super();
		//TODO Auto-generated constructor stub
	}
	public Holiday(String name, LocalDate date, String type) {
		super();
		this.name = name;
		this.date = date;
		this.type = type;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
