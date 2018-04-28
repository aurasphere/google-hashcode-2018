package co.aurasphere.google.hashcode.qualification;

/**
 * Represents a ride which can be assigned to a {@link Vehicle} of the problem.
 * 
 * @author Donato Rimenti
 *
 */
public class Ride {

	/**
	 * Weight used by {@link #fitnessScore(Vehicle)} to encourage/discourage a
	 * ride with some idle involved.
	 */
	private static final double IDLE_RIDE_WEIGHT = 1;

	/**
	 * Weight used by {@link #fitnessScore(Vehicle)} to encourage/discourage a
	 * ride already started which won't get any bonus but that doesn't involve
	 * idle.
	 */
	private static final double STARTED_RIDE_WEIGHT = 1;

	/**
	 * Weight used by {@link #fitnessScore(Vehicle)} to encourage/discourage a
	 * ride which starts on time and gets the bonus without any idle.
	 */
	private static final double PERFECT_RIDE_WEIGHT = 1;

	/**
	 * Weight used by {@link #weightedIdleTime(Vehicle)} to give a weight to an
	 * idle step.
	 */
	private static final double IDLE_WEIGHT = 0;

	/**
	 * Weight used by {@link #weightedStepsToStartTheRide(Vehicle)} to give a
	 * weight to a step while not performing any ride.
	 */
	private static final double STEP_WEIGHT = 1;

	/**
	 * ID of the ride.
	 */
	public final int rideNumber;

	/**
	 * Row at which the ride starts.
	 */
	public final int startRow;

	/**
	 * Column at which the ride starts.
	 */
	public final int startColumn;

	/**
	 * Row at which the ride ends.
	 */
	public final int endRow;

	/**
	 * Column at which the ride ends.
	 */
	public final int endColumn;

	/**
	 * Step from which the ride will be available. Furthermore, if the ride
	 * starts exactly at this step, a {@link ProblemStatement#bonus} will be
	 * awarded to the final score.
	 */
	public final int earliestStart;

	/**
	 * Last step at which the ride can be completed. If the ride is not
	 * completed on time, no points are awarded.
	 */
	public final int latestFinish;

	/**
	 * Steps needed to complete the ride.
	 */
	public final int stepsToEndRide;

	/**
	 * Creates a new Ride.
	 * 
	 * @param rideNumber
	 *            the {@link #rideNumber}
	 * @param startRow
	 *            the {@link #startRow}
	 * @param startColumn
	 *            the {@link #startColumn}
	 * @param endRow
	 *            the {@link #endRow}
	 * @param endColumn
	 *            the {@link #endColumn}
	 * @param earliestStart
	 *            the {@link #earliestStart}
	 * @param latestFinish
	 *            the {@link #latestFinish}
	 */
	public Ride(int rideNumber, int startRow, int startColumn, int endRow,
			int endColumn, int earliestStart, int latestFinish) {
		this.rideNumber = rideNumber;
		this.startRow = startRow;
		this.startColumn = startColumn;
		this.endRow = endRow;
		this.endColumn = endColumn;
		this.earliestStart = earliestStart;
		this.latestFinish = latestFinish;

		// Pre-computes the steps to end the ride.
		this.stepsToEndRide = pointsDistance(startRow, startColumn, endRow,
				endColumn);
	}

	/**
	 * Returns the Manhattan distance between two points.
	 * 
	 * @param x1
	 *            the x coordinate of the first point
	 * @param y1
	 *            the y coordinate of the first point
	 * @param x2
	 *            the x coordinate of the second point
	 * @param y2
	 *            the y coordinate of the second point
	 * @return the distance between the points
	 */
	public int pointsDistance(int x1, int y1, int x2, int y2) {
		return Math.abs(x1 - x2) + Math.abs(y1 - y2);
	}

	/**
	 * Returns the steps to complete a ride from the vehicle's current location.
	 * 
	 * @param vehicle
	 *            the vehicle which will complete the ride
	 * @return the steps to complete the ride for the current vehicle
	 */
	public int stepsToCompleteFromCurrentLocation(Vehicle vehicle) {
		return stepsToStartTheRide(vehicle) + stepsToEndRide;
	}

	/**
	 * Returns the minimum score of this ride.
	 * 
	 * @return the minimum score of this ride
	 */
	public int minScore() {
		return stepsToEndRide;
	}

	/**
	 * Returns the maximum score of this ride.
	 * 
	 * @return the maximum score of this ride
	 */
	public int maxScore() {
		return stepsToEndRide + ProblemStatement.bonus;
	}

	/**
	 * Returns the steps needed to start the ride from the current location for
	 * a vehicle.
	 * 
	 * @param vehicle
	 *            the vehicle which will start the ride
	 * @return the steps needed to start the ride for the vehicle
	 */
	public int stepsToStartTheRide(Vehicle vehicle) {
		return pointsDistance(startRow, startColumn, vehicle.currentRow,
				vehicle.currentColumn);
	}

	/**
	 * Computes the real score of a ride for a vehicle. Used only for debugging
	 * purposes when printing the actual score.
	 * 
	 * @param vehicle
	 *            the vehicle whose score needs to be computed
	 * @return the score of this ride for the vehicle
	 */
	public int realScore(Vehicle vehicle) {
		// Step at which we'll be at the starting point.
		int stepStartReached = vehicle.nextAvailableStep
				+ stepsToStartTheRide(vehicle);

		// An expired ride is worth 0 points.
		if (stepStartReached + stepsToEndRide >= latestFinish) {
			return 0;
		}

		// If we are starting at the earliest start or before (by idling) we'll
		// get the maximum score, otherwise we get the minimum since we can
		// still complete the ride but we didn't start on time.
		if (stepStartReached <= earliestStart) {
			return maxScore();
		} else {
			return minScore();
		}
	}

	/**
	 * Computes the fitness score of a ride for a vehicle. Rides with higher
	 * fitness values for a vehicle are more likely to be chosen next. The
	 * fitness score is only an approximation. It can be a negative number and
	 * it's sole purpose is to give an index for determining how good a ride is.
	 * It doesn't need to be the real score.
	 * 
	 * @param the
	 *            vehicle whose score needs to be computed
	 * @return the score of this ride for the vehicle
	 */
	public double fitnessScore(Vehicle vehicle) {
		// Step at which we'll be at the starting point.
		int stepStartReached = vehicle.nextAvailableStep
				+ stepsToStartTheRide(vehicle);

		// Ride expired, we return NEGATIVE_INFINITY in order to avoid it.
		if (stepStartReached + stepsToEndRide >= latestFinish) {
			return Double.NEGATIVE_INFINITY;
		}

		// Perfect ride, no idles and bonus awarded.
		if (stepStartReached == earliestStart) {
			return (maxScore() - weightedStepsToStartTheRide(vehicle))
					* PERFECT_RIDE_WEIGHT;
		}

		// Ride is started already but we can still finish it without the bonus.
		if (stepStartReached > earliestStart) {
			return (minScore() - weightedStepsToStartTheRide(vehicle))
					* STARTED_RIDE_WEIGHT;
		}

		// Ride not yet available, penalized by idle time but will have max
		// score.
		return (maxScore() - weightedIdleTime(vehicle) - weightedStepsToStartTheRide(vehicle))
				* IDLE_RIDE_WEIGHT;
	}

	/**
	 * Applies the {@link #IDLE_WEIGHT} to the {@link #idleTime(Vehicle)}
	 * function.
	 * 
	 * @param vehicle
	 *            the vehicle whose weighted idle needs to be computed
	 * @return a weighted idle
	 */
	private double weightedIdleTime(Vehicle vehicle) {
		return idleTime(vehicle) * IDLE_WEIGHT;
	}

	/**
	 * Applies the {@link #STEP_WEIGHT} to the
	 * {@link #stepsToStartTheRide(Vehicle)} function.
	 * 
	 * @param vehicle
	 *            the vehicle whose weighted steps to start the ride needs to be
	 *            computed
	 * @return a weighted steps to start the ride
	 */
	private double weightedStepsToStartTheRide(Vehicle vehicle) {
		return stepsToStartTheRide(vehicle) * STEP_WEIGHT;
	}

	/**
	 * Returns the time a vehicle needs to wait before it can start this ride.
	 * 
	 * @param vehicle
	 *            the vehicle whose idle time needs to be computed
	 * @return the idle time of the vehicle for the current ride
	 */
	public int idleTime(Vehicle vehicle) {
		// Step at which we'll be at the starting point.
		int stepStartReached = vehicle.nextAvailableStep
				+ stepsToStartTheRide(vehicle);

		// The idle time is the difference between the step at which we can
		// start the ride and the step at which we reach the start of the ride.
		return earliestStart - stepStartReached;
	}

}