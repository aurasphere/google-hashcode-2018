package co.aurasphere.google.hashcode.qualification;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a vehicle which can perform a {@link Ride} of the problem.
 * 
 * @author Donato Rimenti
 *
 */
public class Vehicle {

	/**
	 * The current row of the vehicle.
	 */
	public int currentRow;

	/**
	 * The current column of the vehicle.
	 */
	public int currentColumn;

	/**
	 * The rides completed by the vehicle, used to print the final solution.
	 */
	public final List<Integer> completedRides = new ArrayList<Integer>();

	/**
	 * The score of the rides completed by this vehicle so far, used to compute
	 * the final score.
	 */
	public int score;

	/**
	 * The next step at which the vehicle will be able to start a new ride.
	 */
	public int nextAvailableStep;

}
