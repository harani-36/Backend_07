package com.train.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TrainResponse {
	private Long id;
    private String trainNumber;
    private String name;
    private String source;
    private String destination;
    
    // Frontend compatibility fields
    @JsonProperty("trainName")
    public String getTrainName() {
        return name;
    }
    
    @JsonProperty("sourceStation")
    public String getSourceStation() {
        return source;
    }
    
    @JsonProperty("destinationStation")
    public String getDestinationStation() {
        return destination;
    }
    
	public TrainResponse(Long id, String trainNumber, String name, String source, String destination) {
		super();
		this.id = id;
		this.trainNumber = trainNumber;
		this.name = name;
		this.source = source;
		this.destination = destination;
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
    

}
