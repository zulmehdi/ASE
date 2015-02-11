package applicationLogic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import dataClasses.Destination;
import dataClasses.Journey;
import dataClasses.Taxi;

public class JourneyFileOps extends FileOps {
	private List<Taxi> taxis;
	private List<Destination> destinations;
	private List<Journey> journeys;
	
	private int year;
	
	public JourneyFileOps(String fileName) {
		super(fileName);
		
		this.year = fileName == Helpers.JOURNEYS_2015_FILE_NAME ? 2015 : 2014;
		
		taxis = getTaxis();
		destinations = getDestinations();
		journeys = new ArrayList<Journey>();
	}
	
	public List<Journey> getJourneys() {
		List<String> lines = readLinesFromFile();
		
		return year == 2015 ? get2015Journeys(lines) : get2014Journeys(lines);
	}
	
	private List<Journey> get2015Journeys(List<String> lines) {
		for(String line : lines) {
			String[] words = line.split(";");
			
			Taxi taxi = new Taxi(getDriverName(words[0]), words[0]);
			Destination destination = new Destination(words[1], getDistance(words[1]));
			
			Journey journey = new Journey(year, destination, taxi, Integer.parseInt(words[2]));
			
			journeys.add(journey);
		}
		
		return journeys;
	}

	private List<Journey> get2014Journeys(List<String> lines) {
		for(String line : lines) {
			String[] words = line.split(";");
			
			Destination destination = new Destination(words[0], getDistance(words[0]));
			
			Journey journey = new Journey(year, destination, null, 0);
			
			journeys.add(journey);
		}
		
		return journeys;
	}

	public List<Journey> getMostExpensiveJourneys() {
		List<Journey> journeys = this.journeys;
		Comparator<Journey> comparator = new Comparator<Journey>() {

			@Override
			public int compare(Journey journey1, Journey journey2) {
				if(journey2.getDestination().getDistance() > journey1.getDestination().getDistance()) {
					return 1;
				} else if(journey2.getDestination().getDistance() < journey1.getDestination().getDistance()) {
					return -1;
				} else {
					return 0;
				}
			}
			
		};
		
		Collections.sort(journeys, comparator);
		
		return journeys;
	}
	
	public List<Journey> getLeastExpensiveJourneys() {
		List<Journey> journeys = this.journeys;
		Comparator<Journey> comparator = new Comparator<Journey>() {

			@Override
			public int compare(Journey journey1, Journey journey2) {
				if(journey2.getDestination().getDistance() < journey1.getDestination().getDistance()) {
					return 1;
				} else if(journey2.getDestination().getDistance() > journey1.getDestination().getDistance()) {
					return -1;
				} else {
					return 0;
				}
			}
			
		};
		
		Collections.sort(journeys, comparator);
		
		return journeys;
	}
	
	public TreeSet<Taxi> getDriversAndVisitedPlaces() {
		TreeSet<Taxi> taxis = getUniqueTaxiSet();
		
		for(Taxi taxi : taxis) {
			for(Journey journey : journeys) {
				if(taxi.getDriver().equals(journey.getTaxi().getDriver())) {
					taxi.setDestinations(journey.getDestination());
				}
			}
		}
		
		return taxis;
	}
	
	private TreeSet<Taxi> getUniqueTaxiSet() {
		TreeSet<Taxi> taxis = new TreeSet<Taxi>();
		
		for(Taxi taxi : this.taxis) {
			taxis.add(taxi);
		}
		
		return taxis;
	}
	
	public TreeSet<Journey> getUniqueJourneySet() {
		TreeSet<Journey> journeys = new TreeSet<Journey>();
		
		for(Journey journey : this.journeys) {
			journeys.add(journey);
		}
		
		return journeys;
	}

	private double getDistance(String name) {
		double distance = -1;
		
		for(Destination destination : destinations) {
			if(destination.getName().equals(name)) {
				distance = destination.getDistance();
			}
		}
		
		return distance;
	}

	private String getDriverName(String regNumber) {
		String name = "NOT FOUND";
		
		for(Taxi taxi : taxis) {
			if(taxi.getRegistrationNumber().equals(regNumber)) {
				name = taxi.getDriver();
			}
		}
		
		return name;
	}

	private List<Taxi> getTaxis() {
		return new TaxiFileOps(Helpers.TAXIS_FILE_NAME).getTaxis();
	}
	
	private List<Destination> getDestinations() {
		return new DestinationFileOps(Helpers.DESTINATIONS_FILE_NAME).getDestinations();
	}
	
	//
	//
	// GET UNIQUE AND COMMON JOURNEYS, AND WRITE THEM TO A FILE
	//
	//
	
	private static TreeSet<Journey> getCommonJourneys(TreeSet<Journey> journeys1, TreeSet<Journey> journeys2) {
		TreeSet<Journey> commonJourneys = journeys1;
		commonJourneys.retainAll(journeys2);
		
		return commonJourneys;
	}

	private static TreeSet<Journey> getUniqueJourneys(TreeSet<Journey> journeys1, TreeSet<Journey> journeys2) {
		TreeSet<Journey> journeys1Only = new TreeSet<Journey>();
		for(Journey journey : journeys1) {
			journeys1Only.add(journey);
		}
		
		journeys1Only.removeAll(journeys2);
		
		return journeys1Only;
	}
	
	private static String getUniqueAndCommonDestinations(
			TreeSet<Journey> uniqueJourneys1, int uniqueJourney1Count,
			TreeSet<Journey> uniqueJourneys2, int uniqueJourney2Count,
			TreeSet<Journey> commonJourneys, int commonJourneyCount) {
		String s = "";
		s += uniqueJourney1Count + " NEW PLACES IN 2015\n";
		for(Journey journey : uniqueJourneys1) {
			s += journey.getDestination().getName() + "\n";
		}
		s += "\n";
		
		s += uniqueJourney2Count + " PLACES VISITED IN 2014 ONLY\n";
		for(Journey journey : uniqueJourneys2) {
			s += journey.getDestination().getName() + "\n";
		}
		s += "\n";
		
		s += commonJourneyCount + " PLACES VISITED IN BOTH 2014 AND 2015\n";
		for(Journey journey : commonJourneys) {
			s += journey.getDestination().getName() + "\n";
		}
		s += "\n";
		
		return s;
	}
	
	public static void writeUniqueAndCommonDestinations(TreeSet<Journey> journeys1, TreeSet<Journey> journeys2) {
		TreeSet<Journey> uniqueJourneys1 = getUniqueJourneys(journeys1, journeys2);
		TreeSet<Journey> uniqueJourneys2 = getUniqueJourneys(journeys2, journeys1);
		TreeSet<Journey> commonJourneys = getCommonJourneys(journeys1, journeys2);
		
		String s = "";
		s += getUniqueAndCommonDestinations(
				uniqueJourneys1, uniqueJourneys1.size(),
				uniqueJourneys2, uniqueJourneys2.size(),
				commonJourneys, commonJourneys.size()
				);
		
		writeToFile("UniqueAndCommonJourneys", s);
	}

	//
	//
	// GET FIRST FIVE JOURNEYS FROM LISTS, SORT THEM IN ASC DESC ORDER, AND WRITE THEM TO A FILE
	//
	//
	
	public static void writeTopFiveAndCheapestJourneysToFile(String top5Journeys, String cheapest5Journeys) {
		String s = "";
		s += top5Journeys;
		s += cheapest5Journeys;
		
		writeToFile("TopFiveAndCheapestFiveJourneys", s);
	}

	public static String getFirstFiveJourneys(List<Journey> journeys, String title) {
		String s = "";
		s += title;
		s += ":\n";
		
		for(Journey journey : journeys.subList(0, 5)) {
			s += journey.toString();
		}
		
		s += "\n";
		
		return s;
	}
}