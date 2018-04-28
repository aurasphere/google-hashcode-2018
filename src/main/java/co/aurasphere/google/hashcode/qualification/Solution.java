package co.aurasphere.google.hashcode.qualification;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Main class of the Google HashCode 2018 Qualification round. The main method
 * reads the input into a {@link ProblemStatement} object, delegates the
 * solution to the {@link ProblemSolver} class and prints the output to console
 * or to a file.<br>
 * <br>
 * The scores achieved by this class are the following:
 * 
 * <pre>
 * 	<table border="1">
 * 		<tr><th>Input</th><th>Score</th></tr>
 * 		<tr><td>a_example.in</td><td>4</td></tr>
 * 		<tr><td>b_should_be_easy.in</td><td>176.677</td></tr>
 * 		<tr><td>c_no_hurry.in</td><td>13.789.773</td></tr>
 * 		<tr><td>d_metropolis.in</td><td>10.914.293</td></tr>
 * 		<tr><td>e_high_bonus.in</td><td>21.460.945</td></tr>
 * 		<tr><th>Total:</th><th>46.341.692</th></tr>
 * 	</table>
 * </pre>
 * 
 * <a href="https://github.com/PicoJr/2018-hashcode-score">The scores have been
 * validated using this Python snippet not written by me</a>
 * 
 * @author Donato Rimenti
 *
 */
public class Solution {

	/**
	 * The input dataset.
	 */
	private static final String INPUT_FILE = "a_example.in";

	/**
	 * An optional output file. If present, the output will be redirected in a
	 * valid format to this file, otherwise if null, the output will be printed
	 * in console along with the score of the solution.
	 */
	private static final String OUTPUT_FILE = null;

	/**
	 * The main method of this class. Reads an input file into a
	 * {@link ProblemStatement}, solves it and prints the solution.
	 *
	 * @param args
	 *            null
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// Loads the problem statement.
		parseFile(INPUT_FILE);

		// Solves the problem.
		List<Vehicle> vehicles = ProblemSolver.solveProblem();

		// Redirects the system output to a file.
		if (OUTPUT_FILE != null) {
			System.setOut(new PrintStream(new BufferedOutputStream(
					new FileOutputStream(OUTPUT_FILE)), true));
		}
		// Prints the solution.
		printSolution(vehicles);

		// If an output file has not been specified, prints also the score for
		// debugging/profiling purposes.
		if (OUTPUT_FILE == null) {
			printScore(vehicles);
		}
	}

	/**
	 * Computes and prints the score of a given solution.
	 * 
	 * @param solution
	 *            the solution whose score needs to be printed
	 */
	private static void printScore(List<Vehicle> solution) {
		int score = solution.stream().collect(
				Collectors.summingInt(v -> v.score));
		System.out.println("Score: " + String.format("%,d", score));
	}

	/**
	 * Prints the solution for each vehicle.
	 * 
	 * @param vehicles
	 *            the vehicles of the solution
	 */
	private static void printSolution(List<Vehicle> vehicles) {
		for (Vehicle v : vehicles) {
			// Prints the number of completed rides.
			System.out.print(v.completedRides.size());

			// Prints each ride.
			v.completedRides.forEach(i -> System.out.print(" " + i));

			// Prints a new line.
			System.out.println();
		}
	}

	/**
	 * Loads an input file from classpath into the {@link ProblemStatement}
	 * class.
	 * 
	 * @param fileName
	 *            the input file resource name
	 * @throws Exception
	 */
	public static void parseFile(String fileName) throws Exception {
		// Reads all the file lines.
		URI filePath = Solution.class.getClassLoader().getResource(fileName)
				.toURI();
		List<String> fileLines = Files.readAllLines(Paths.get(filePath),
				StandardCharsets.UTF_8);

		// Processes each line.
		Iterator<String> it = fileLines.iterator();

		// Reads the first row.
		String[] firstLine = it.next().split(" ");
		ProblemStatement.gridRows = Integer.valueOf(firstLine[0]);
		ProblemStatement.gridColumns = Integer.valueOf(firstLine[1]);
		ProblemStatement.numVehicles = Integer.valueOf(firstLine[2]);
		ProblemStatement.numRides = Integer.valueOf(firstLine[3]);
		ProblemStatement.bonus = Integer.valueOf(firstLine[4]);
		ProblemStatement.numSteps = Integer.valueOf(firstLine[5]);

		// Reads the rides.
		List<Ride> rides = new ArrayList<Ride>();
		int rideNumber = 0;
		while (it.hasNext()) {
			String[] currentLine = it.next().split(" ");

			Ride ride = new Ride(rideNumber++, Integer.valueOf(currentLine[0]),
					Integer.valueOf(currentLine[1]),
					Integer.valueOf(currentLine[2]),
					Integer.valueOf(currentLine[3]),
					Integer.valueOf(currentLine[4]),
					Integer.valueOf(currentLine[5]));
			rides.add(ride);
		}

		ProblemStatement.rides.addAll(rides);
	}

}