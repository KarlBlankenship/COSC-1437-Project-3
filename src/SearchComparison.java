/* 
// Karl Blankenship
// COSC 1437
// Project III - Search Algorithm Comparison
// July 2018
// Filename: SearchComparison.java
*/

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.io.*;

/**
 * Search Algorithm Comparison between linear and binary search times.
 * @author Karl Blankenship
 */
public class SearchComparison 
{
	static final long delayTime = 1;			// Delay time added to each process cycle for measurement.
	static final int numberOfKeys = 20;			// Number of keys to be generated.
	static final int numberOfData = 1000;		// Number of random numbers to be generated for data file.
	static final int maxValueOfRandom = 1000;	// Maximum value possible for each random number.
	
	/**
	 * The main method is the starting point for this java program. 
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws IOException, InterruptedException 
	{
		// Create a SearhCaomparison object.
		SearchComparison search = new SearchComparison();
				
		// Create a list of keys that will need to be compared to the data file for a matching value.
		search.createRandomDataFile("key.txt", numberOfKeys, maxValueOfRandom);
		
		// Create a list of random numbers that will be the data file to be searched.
		search.createRandomDataFile("data.txt", numberOfData, maxValueOfRandom);
				
		// Create a new output file object for recording matches and search times.
		PrintWriter outputFile = new PrintWriter("comparison_report.txt");
		
		// Create the header string for output file and output to display.
		String header = String.format("%-8s%-8s%-16s%-16s%-16s%-16s",
									"Index", "Value", "Found-Linear", "Linear Time(ms)",
									"Found Binary", "Binary Time(ms)");
		
		// Create display output header.
		System.out.println(header);
		
		// Write header to output file.
		outputFile.println(header);
		
		// Create the key array and write values from the key.txt input file into the array.
		int[] keyArray = returnArray("key.txt", numberOfKeys);
		
		// Create the data array and write values from the data.txt input file into the array.
		int[] dataArray =	returnArray("data.txt", numberOfData);
		
		// Sort data array into sequence from lowest to highest.
		int[] sortedDataArray = returnSortedDataArray(dataArray);
		
		// Cycle through each key in the array and search for a match in the data file.
		for (int index = 0; index < (numberOfKeys); index++)
		{
			// Find the linear search time.
			// timedLinearSearch returns the time in ms and sets time to negative if value was not found.
			long linearTime = timedLinearSearch(keyArray[index], dataArray);
			boolean foundLinear = true;	// Create boolean flag as true.
			// If linear time is negative then must be made positive and set boolean flag to false.
			if (linearTime < 0)
			{
				linearTime = linearTime * (-1);
				foundLinear = false;
			}
			
			// Find the binary search time.
			// timedBinarySearch returns the time in ms and sets time to negative if value was not found.
			long binaryTime = timedBinarySearch(keyArray[index], sortedDataArray);
			boolean foundBinary = true;	// Create boolean flag as true.
			// If linear time is negative then must be made positive and set boolean flag to false.
			if (binaryTime < 0)
			{
				binaryTime = binaryTime * (-1);
				foundBinary = false;
			}
		
			//Create output string.
			String outputString = String.format("%-8d%-8d%-16b%-16d%-16b%-16d", 
												index, keyArray[index], foundLinear, linearTime,
												foundBinary, binaryTime);
			
			// Write output string to display.
			System.out.println(outputString);
			
			// Write output string to output file.
			outputFile.println(outputString);
		}
	
		// Close output file to write buffer to file.
		outputFile.close();		
	}
	
	/**
	 * The returnArray method accepts an input text file as well as the number of 
	 * integers to process and returns an array. The array will contain the first
	 * corresponding number of integers from the text file.
	 * @param filename of the input text file as a string.
	 * @param numberOfIntegers this is the number of expected integers in the file.
	 * @return the array containing the specified number of integer values from the input file. 
	 * @throws IOException
	 */
	public static int[] returnArray(String filename, int numberOfIntegers) throws IOException 
	{
		// Create the return array reference variable.
		int[] intArray = new int[numberOfIntegers];
		
		// Create new File object.
		File file = new File(filename);
		
		// Create a new Scanner object to read contents of file.
		Scanner inputFile = new Scanner(file);
		
		// Write context of scanner object to return array.
		int index = 0;	// Create and initialize the array index counter.
		while (inputFile.hasNextInt())
		{
			intArray[index] = inputFile.nextInt();	// Write next integer to current array index.
			index++;
		}
				
		// Close the input file.
		inputFile.close();
		
		// Return the array of values.
		return intArray;
	}
	
	/**
	 * The returnSortedDataArray will accept an input array of integers and will 
	 * return an integer array containing the same values but sorted such that the
	 * lowest value is at the lowest index and the highest value is at the highest
	 * index.
	 * @param inputArray is the input array of integers.
	 * @return returns an array of input integers sorted from lowest to highest.
	 */
	public static int[] returnSortedDataArray(int[] inputArray) 
	{
		// Create the return array reference variable.
		int[] sortedArray = new int[inputArray.length];	
		
		// Copy values from the input array into the sorted array.
		for (int index = 0; index < inputArray.length; index++)
			sortedArray[index] = inputArray[index];
		
		// Bubble sort the array to sequence the numbers from smallest to largest.
		int lastPos;	// Index of the last element to compare.
		int index;			// Index of an element to compare.
		int temp;			// Used to swap elements.
		
		// Next for loop will always bubble the highest value to the last position so
		// each time the loop executes, the array only has to be sorted to one less
		// index.
		for (lastPos = sortedArray.length - 1; lastPos >= 0; lastPos--)
		{
			// This for loop will compare each index to the next higher index and swap
			// the values if needed. This will bubble the highest value up to the 
			// highest index.
			for (index = 0; index <= lastPos - 1; index++)
			{
				// This if statement swaps the two values being compared if required.
				if (sortedArray[index] > sortedArray[index + 1])
				{
					temp = sortedArray[index];
					sortedArray[index] = sortedArray[index + 1];
					sortedArray[index + 1] = temp;
				}
			}
		}
		
		// Returns the sorted integer array.
		return sortedArray;
	}
	
	/**
	 * The timedLinearSearch method will utilize the linear search methodology to search for
	 * the input item value within the data array. This method can only return a single value
	 * but will encode two pieces of information. Instead of creating and returning a custom
	 * object containing the search time and whether or not the value was located, this method
	 * will return the search time and will set that time to a negative value if the search value
	 * was not located within the data set. 
	 * @param item
	 * @param inputArray
	 * @return the total search time in milliseconds, negative if item value was never located.
	 * @throws InterruptedException
	 */
	public static long timedLinearSearch(int item, int[] inputArray) throws InterruptedException
	{
		boolean flag = false;		// Create return flag and default to false.
		int index = 0;				// Create counter for the while loop.
		long timeInMilliseconds;	// Total time in milliseconds.
		
		long start = System.currentTimeMillis();	// Start time in milliseconds for timer.
		
		// Loop through inputArray to see if item is present.
		while (!flag && index < inputArray.length)
		{
			// Delay time added to improve measurement.
			TimeUnit.MILLISECONDS.sleep(delayTime);
			
			// Check to see if item matches each index.
			if (item == inputArray[index])
				flag = true;
			index++;
		}
		
		long end = System.currentTimeMillis();	// End time in milliseconds for timer.
		
		// Calculate total time and set to negative value if item was not found.
		if (flag)
			timeInMilliseconds = end - start;
		else
			timeInMilliseconds = (-1) * (end - start);

		// Return the time in Milliseconds.
		return timeInMilliseconds;
	}
	
	/**
	 * The timedBinarySearch method will utilize the binary search methodology to search for
	 * the input item value within the data array. This method can only return a single value
	 * but will encode two pieces of information. Instead of creating and returning a custom
	 * object containing the search time and whether or not the value was located, this method
	 * will return the search time and will set that time to a negative value if the search value
	 * was not located within the data set.
	 * @param item
	 * @param inputArray
	 * @return
	 * @throws InterruptedException
	 */
	public static long timedBinarySearch(int item, int[] inputArray) throws InterruptedException
	{
		boolean flag = false;				// Create the return flag.
		int first = 0;						// Start position of search block.
		int middle;							// Middle location, test point.
		int last = inputArray.length - 1;	// Last position of search block.
		long timeInMilliseconds;			// Total time in milliseconds. 
		
		long start = System.currentTimeMillis();	// Start time in milliseconds for timer.
		
		// Loop through inputArray to see if item is present.
		while (!flag && first <= last)
		{
			// Delay time to improve measurement.
			TimeUnit.MILLISECONDS.sleep(delayTime);
			
			// Calculate the middle index of the current search range, test point.
			middle = (last + first) / 2;
			
			// Check to see if the search item value equals the middle value in the search range.
			if (item == inputArray[middle])
				flag = true;
			// Adjust the new search range to the lower half of the current range.
			else if (item < inputArray[middle])
				last = middle - 1;
			// Adjust the new search range to the upper half of the current range.
			else
				first = middle + 1;		
		}
		
		long end = System.currentTimeMillis();	// End time in milliseconds for timer.
		
		// Calculate total time and set to negative value if item was not found.
		if (flag)
			timeInMilliseconds = end - start;
		else
			timeInMilliseconds = (-1) * (end - start);
		
		// Return the time in Milliseconds.
		return timeInMilliseconds;
	}
	
	/**
	 * The createRandomDataFile method will create a text file of the specified 
	 * file name with the specified quantity of random numbers within the specified
	 * maximum random number value.
	 * @param fileName the name of the file created or over written.
	 * @param quatity the quantity of random numbers to generate.
	 * @param maxValue is the highest possible numerical value of each random number.
	 * @throws IOException
	 */
	public void createRandomDataFile(String fileName, int quantity, int maxValue) throws IOException {
		
		// Create a new random object.
		Random random = new Random();
		
		// Create a file object.
		File outFile = new File(fileName);
		
		// Create a file writer to object.
		FileWriter fw = new FileWriter(outFile);
		
		// Generate random data to serve as search keys.
		int count = quantity;
		for (int i = 1; i <= count; i++) 
		{			
			fw.write(random.nextInt(maxValue) + "\t");
			if (i % 10 == 0) {
				fw.write("\n");
			}			
		}
		
		// Close the output file.
		fw.close();
	}
}
