package dataClasses;

public class Journey implements Comparable<Journey> {
	private int year;
	private Destination destination;
	private Taxi taxi;
	private int numberOfPassengers;
	
	public Journey(int year, Destination destination, Taxi taxi, int numberOfPassengers) {
		super();
		
		this.year = year;
		this.destination = destination;
		this.taxi = taxi;
		this.numberOfPassengers = numberOfPassengers;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public Destination getDestination() {
		return destination;
	}

	public void setDestination(Destination destination) {
		this.destination = destination;
	}

	public Taxi getTaxi() {
		return taxi;
	}

	public void setTaxi(Taxi taxi) {
		this.taxi = taxi;
	}

	public int getNumberOfPassengers() {
		return numberOfPassengers;
	}

	public void setNumberOfPassengers(int numberOfPassengers) {
		this.numberOfPassengers = numberOfPassengers;
	}

	@Override
	public String toString() {
		String s = "";
		s += "Year: " + year + "\n";
		s += "Taxi: " + taxi.getRegistrationNumber() + "\n";
		s += "Driver: " + taxi.getDriver() + "\n";
		s += "Destination: " + destination.getName() + "\n";
		s += "Distance: " + destination.getDistance() + "\n";
		s += "Passengers: " + numberOfPassengers + "\n";
		return s;
	}

	@Override
	public int compareTo(Journey other) {
//		return taxi.getDriver().compareTo(other.taxi.getDriver());
		return destination.getDistance() - other.destination.getDistance();
	}
}
