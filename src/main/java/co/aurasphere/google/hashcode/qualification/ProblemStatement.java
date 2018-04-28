package co.aurasphere.google.hashcode.qualification;
import java.util.LinkedList;

/**
 * Representation of the Google HashCode 2018 Qualification round's problem
 * statement.
 * 
 * @author Donato Rimenti
 *
 */
public class ProblemStatement {

	/**
	 * Number of rows in the grid.
	 */
	public static int gridRows;

	/**
	 * Number of columns in the grid.
	 */
	public static int gridColumns;

	/**
	 * Numbers of vehicles in the simulation.
	 */
	public static int numVehicles;

	/**
	 * Number of rides in the simulation.
	 */
	public static int numRides;

	/**
	 * Bonus points scored for a ride that starts on time (i.e. the current step
	 * is equal to {@link Ride#earliestStart}).
	 */
	public static int bonus;

	/**
	 * Number of steps in the simulation.
	 */
	public static int numSteps;

	/**
	 * Rides of the simulation.
	 */
	public final static LinkedList<Ride> rides = new LinkedList<Ride>();

}
