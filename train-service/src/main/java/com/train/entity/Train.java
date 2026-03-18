package com.train.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="train")
public class Train {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, unique = true, length = 20)
	private String trainNumber;
	
	@Column(nullable = false, length = 100)
	private String name;
	
	@Column(nullable = false, length = 100)
	private String source;
	
	@Column(nullable = false, length = 100)
	private String destination;
	
	@OneToMany(mappedBy="train", cascade=CascadeType.ALL)
	private List<Journey> journeys;
	
	public Train() {
		super();
	}
	
	public Train(Long id, String trainNumber, String name, String source, String destination, List<Journey> journeys) {
		super();
		this.id = id;
		this.trainNumber = trainNumber;
		this.name = name;
		this.source = source;
		this.destination = destination;
		this.journeys = journeys;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTrainNumber() {
		return trainNumber;
	}
	public void setTrainNumber(String trainNumber) {
		this.trainNumber = trainNumber;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public List<Journey> getJourneys() {
		return journeys;
	}
	public void setJourneys(List<Journey> journeys) {
		this.journeys = journeys;
	}
}
