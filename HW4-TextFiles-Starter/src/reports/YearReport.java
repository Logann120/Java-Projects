// NAME: Logan Noonan

package reports;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import analytics.Data;

/**
 * A report for a single year of Fortune 500 data.
 * Report includes the minimum, maximum, average, and standard deviation of revenues and profits
 *  for all ranked companies of the report's year.
 * @author YOUR NAME
 *
 */
public class YearReport implements Report
{	
	/**
	 * This is the cvs file to read data from.
	 */
	private File theFile;
	
	/**
	 * A boolean value to test if the file was processed or not.
	 */
	private boolean isProcessed;

	/**
	 * A boolean to check if the file was created properly or not.
	 */
	private boolean fileExists;
	
	/**
	 * A scanner to read the file.
	 */
	private Scanner inputScanner;
	
	/**
	 * A string of the files next line.
	 */
	private String nextLine;
	
	/**
	 * A scanner to parse the next line in the file.
	 */
	private Scanner lineParser;
	
	/**
	 * The company minimum revenue.
	 */
	private Double minRevenue;
	
	/**
	 * The company maximum revenue.
	 */
	private Double maxRevenue;
	
	/**
	 * The company average revenue.
	 */
	private Double avgRevenue;
	
	/**
	 * The company's standard deviation of revenue.
	 */
	private Double sDRevenue;
	
	/**
	 * The company minimum profit.
	 */
	private Double minProfit;
	
	/**
	 * The company maximum profit.
	 */
	private Double maxProfit;
	
	/**
	 * The company average profit.
	 */
	private Double avgProfit;
	
	/**
	 * The company's standard deviation of revenue.
	 */
	private Double sDProfit;
	
	/**
	 * An array to hold the companies revenue values for each year it was listed.
	 */
	private Double[] totalRevenue;
	
	/**
	 * An array to hold the companies profit values for each year it was listed.
	 */
	private Double[] totalProfit;
	
	/**
	 * Tracks the current index in the revenue array.
	 */
	private int revenueIndex;
	
	/**
	 * Tracks the current index in the profit array.
	 */
	private int profitIndex;
	
	/**
	 * The year to run a report for, only to be set after processing.
	 */
	private int theYear; 
	
	/**
	 * Creates new YearReport for given year; data to be read from given file.
	 * @param inputFileIn - File containing Fortune 500 data for this report.
	 * @param yearIn - Year to report Fortune 500 data.
	 */
	public YearReport(File inputFileIn, int yearIn)
	{
		this.fileExists = false;
		try 
		{
			this.setFile(inputFileIn);
			this.inputScanner = new Scanner(this.theFile);
			this.fileExists = true;
		} 
		catch (FileNotFoundException e) 
		{
			e.toString();
		}
		this.theYear = yearIn;
		this.totalRevenue = new Double[Report.NUMCOMPANIES];
		this.totalProfit = new Double[Report.NUMCOMPANIES];
		this.revenueIndex = 0;
		this.profitIndex = 0;
		this.isProcessed = false;
	}
	
	/**
	 * A method to verify the file is loaded properly.
	 * @param fileIn The file to load in.
	 * @throws FileNotFoundException Thrown if the file does not exist.
	 */
	private void setFile(File fileIn) throws FileNotFoundException
	{
		if (fileIn != null)
		{
			this.theFile = fileIn;
		}
		else
		{
			throw new FileNotFoundException();
		}
	}

	/**
	 * Reads data from Fortune 500 data file; processes the data.
	 * The file is a csv file and can be assumed is formatted correctly.
	 * See supplemental document for details on reading from csv files.
	 * Calculates the minimum, maximum, average, and standard deviation of revenues and profits
	 *  for all ranked companies of the report's year using tools in the Data class.
	 * @throws YearNotFoundException - Thrown if the report's year is not present in the data file.
	 * @return true if processing successful, false if the input file does not exist.
	 */
	public boolean processReport()
	{
		boolean result = false;
		if (!(Report.MINYEAR <= this.getYear() && this.getYear() <= Report.MAXYEAR))
		{
			throw new YearNotFoundException();
		}
		else
		{
			if (this.fileExists)
			{
				this.extractNumbers();
				this.minRevenue = Data.minimum(this.getRevenuesList());
				this.maxRevenue = Data.maximum(this.getRevenuesList());
				this.avgRevenue = Data.average(this.getRevenuesList());
				this.sDRevenue = Data.standardDeviation(this.getRevenuesList());
				this.minProfit = Data.minimum(this.getProfitsList());
				this.maxProfit = Data.maximum(this.getProfitsList());
				this.avgProfit = Data.average(this.getProfitsList());
				this.sDProfit = Data.standardDeviation(this.getProfitsList());
				result = true;
				this.isProcessed = true;
			}
		}
		return result;
	}
	
	/**
	 * A method used to extract all the data for the given year.
	 */
	private void extractNumbers()
	{
		if (this.inputScanner.hasNextLine())
		{
			this.nextLine = this.inputScanner.nextLine(); // Skip the first line since it is column names.
		}
		while (this.inputScanner.hasNextLine())
		{
			this.nextLine = this.inputScanner.nextLine();
			this.lineParser = new Scanner(this.nextLine); 
			this.lineParser.useDelimiter(","); 
			if (this.lineParser.nextInt() == this.theYear)
			{
				do
				{
					this.sumRevenues();
					this.sumProfits();
					if (this.inputScanner.hasNextLine())
					{
						this.nextLine = this.inputScanner.nextLine();
						this.lineParser = new Scanner(this.nextLine);
						this.lineParser.useDelimiter(",");
					}
					else
					{
						break;
					}
				} while (this.lineParser.nextInt() == this.getYear());
				
				this.inputScanner.close();
				this.lineParser.close();
				break;
			}
		}
	}
	
	/**
	 * A method to extract all revenue values for a given year.
	 */
	private void sumRevenues()
	{
		this.lineParser.next(); // Skip this line to get to the revenue.
		this.lineParser.next(); // Skip this line as well.
		this.totalRevenue[this.revenueIndex] = this.lineParser.nextDouble();
		this.revenueIndex++;
	}
	
	/**
	 * A method to extract all profit values for a given year.
	 */
	private void sumProfits()
	{
		this.totalProfit[this.profitIndex] = this.lineParser.nextDouble();
		this.profitIndex++;
	}
	
	/**
	 * A method to return the array of the years revenues.
	 * @return The revenue array.
	 */
	private Double[] getRevenuesList()
	{
		return this.totalRevenue;
	}
	
	/**
	 * A method to return the array of the years profits.
	 * @return The profit array.
	 */
	private Double[] getProfitsList()
	{
		return this.totalProfit;
	}

	/**
	 * Writes the processed report to the given file.
	 * The given file's contents will look like the result of calling YearReport's toString.
	 * @param outputFile - File to write report to.
	 * @return true if write successful, false if file cannot be created. 
	 * @throws DataNotProcessedException \\
	 */
	public boolean writeReport(File outputFile) throws DataNotProcessedException
	{
		if (!this.isProcessed)
		{
			throw new DataNotProcessedException();
		}
		boolean result;
		try
		{
			FileOutputStream fos = new FileOutputStream(outputFile);
			PrintWriter writer =  new PrintWriter(fos);
			writer.print(this.toString());
			writer.close();
			fos.close();
			result = true;
		} 
		catch (IOException e) 
		{
			result = false;
		}
		return result;
	}

	/**
	 * Returns a formatted String of this report suitable for writing to an output file. String is of the form:
	 * 
	 * Fortune 500 Report for YEAR
	 * Revenue
	 * Min: MINREV Max: MAXREV Avg: AVGREV StD: STDREV
	 * Profit
	 * Min: MINPRO Max: MAXPRO Avg: AVGPRO StD: STDPRO
	 * 
	 * Where YEAR is the year of the report, MINREV, MAXREV, AVGREV, STDREV are the
	 *  minimum, maximum, average, and standard deviation of revenues, and
	 *  MINPRO, MAXPRO, AVGPRO, STDPRO are the minimum, maximum, average, and standard deviation of profits.
	 * These are all floating point values formatted to exactly three decimals
	 *  except for YEAR which is a whole number value.
	 * NOTE: There are no blank lines before, after, or between the 
	 * lines, and the String DOES NOT end in a new line.
	 *  If your toString is not formatted exactly most tests will fail.
	 *  A JUnit test for this method is provided in the tests package to ensure your formatting is correct.
	 *  Additionally, remember that all are formatted to exactly three decimal places,
	 *   which will explain any "nul"s you see in the provided test case.
	 */
	@Override
	public String toString()
	{
		String result;
		result = String.format("Fortune 500 Report for %d\n"
				+ "Revenue\n"
				+ "Min: %.3f Max: %.3f Avg: %.3f StD: %.3f\n"
				+ "Profit\n"
				+ "Min: %.3f Max: %.3f Avg: %.3f StD: %.3f", this.getYear(), this.minRevenue, 
				this.maxRevenue, this.avgRevenue, this.sDRevenue, this.minProfit,
				this.maxProfit, this.avgProfit, this.sDProfit);
		return result;
	}

	/**
	 * Returns the year of this report.
	 * @return Year of this report.
	 */
	public int getYear()
	{
		return this.theYear;
	}
}
