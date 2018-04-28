package co.aurasphere.google.hashcode.qualification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Solver for the Google HashCode 2018 Qualification round problem.
 * 
 * @author Donato Rimenti
 *
 */
public class ProblemSolver {

	/**
	 * Private constructor for static class.
	 */
	private ProblemSolver() {
	}

	/**
	 * Solution method of the problem. Returns a list of vehicles, each with a
	 * list of rides assigned. The steps performed are the following:
	 * 
	 * <pre>
	 * 1. Sort the rides from earliest to latest 
	 * 2. Init the vehicle fleet and give a starting ride to each vehicle 
	 * 3. For each ride, compute the fitness score of each vehicle
	 * 4. Assign the ride to the vehicle with the maximum fitness score
	 * </pre>
	 * 
	 * @return a list of vehicles, each with its own rides assigned
	 */
	public static List<Vehicle> solveProblem() {
		List<Vehicle> solution = new ArrayList<Vehicle>();

		// Sorts the rides in order of availability. This is important
		// because we then assign the rides in order so if they are not sorted,
		// a lot of them will be skipped.
		Collections
				.sort(ProblemStatement.rides, (r1, r2) -> Integer.compare(
						r1.earliestStart, r2.earliestStart));

		// Initiates the solution.
		for (int i = 0; i < ProblemStatement.numVehicles; i++) {
			Vehicle newVehicle = new Vehicle();
			solution.add(newVehicle);

			// Assigns the first available ride to each vehicle. This helps to
			// spread to fleet.
			while (!assignRide(newVehicle, ProblemStatement.rides.poll()))
				;
		}

		// Iterates the rides and assigns each one to the fittest vehicle.
		for (Ride currentRide : ProblemStatement.rides) {

			// Finds the fittest vehicle of the fleet for this ride.
			Vehicle bestVehicleForRide = solution
					.stream()
					.max((v1, v2) -> Double.compare(
							currentRide.fitnessScore(v1),
							currentRide.fitnessScore(v2))).get();

			// Assigns this ride to the vehicle found.
			assignRide(bestVehicleForRide, currentRide);
		}

		return solution;
	}

	/**
	 * Assigns a ride to a vehicle if it can be completed on time.
	 * 
	 * @param vehicle
	 *            the vehicle whose ride needs to be assigned
	 * @param ride
	 *            the ride to assign
	 * @return true if the ride has been assigned, false otherwise
	 */
	private static boolean assignRide(Vehicle vehicle, Ride ride) {
		// This ride is not available anymore, it can't be assigned.
		if (ride.fitnessScore(vehicle) == Double.NEGATIVE_INFINITY) {
			return false;
		}

		// Assigns the ride and the score to the vehicle.
		vehicle.completedRides.add(ride.rideNumber);
		vehicle.score += ride.realScore(vehicle);

		// The following operations needs to be performed in strict order.

		// Computes the next step at which the vehicle will be available again.
		// This is the sum of the idle time (if any) and the steps needed to
		// complete the ride.
		int idleTime = ride.idleTime(vehicle);
		if (idleTime > 0) {
			vehicle.nextAvailableStep += idleTime;
		}
		vehicle.nextAvailableStep += ride
				.stepsToCompleteFromCurrentLocation(vehicle);

		// Moves the vehicle to the new location.
		vehicle.currentColumn = ride.endColumn;
		vehicle.currentRow = ride.endRow;

		// Ride assigned.
		return true;
	}
}