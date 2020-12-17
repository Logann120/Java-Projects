// COURSE: CSCI1620
// TERM: Fall 2020
// 
// NAME: Logan Noonan
// RESOURCES: I utilized help from the CSLC various times for help with web-cat errors.

package reports;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import analytics.Data;

/**
 * A report for a single company of Fortune 500 data.
 * Report includes the minimum, maximum, average, and standard deviation of revenues, profits, and rank
 *  for all years in which the company was ranked in the Fortune 500.
 * @author Logan Noonan
 *
 */
public class CompanyReport implements Report
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
	 * The companies minimum rank across all years.
	 */
	private Double minRank;
	
	/**
	 * The companies maximum rank across all years.
	 */
	private Double maxRank;
	
	/**
	 * The companies average rank across all years.
	 */
	private Double avgRank;
	
	/**
	 * The companies standard deviation rank across all years.
	 */
	private Double sDRank;
	
	/**
	 * The companies rank for any given year.
	 */
	private Integer yearlyRank;
	
	/**
	 * An array to hold the companies revenue values for each year it was listed.
	 */
	private Double[] totalRevenue;
	
	/**
	 * An array to hold the companies profit values for each year it was listed.
	 */
	private Double[] totalProfit;
	
	/**
	 * An array to hold the companies rank values for each year it was listed.
	 */
	private Double[] totalRanks;
	
	/**
	 * Tracks the current index in the revenue array.
	 */
	private int revenueIndex;
	
	/**
	 * Tracks the current index in the profit array.
	 */
	private int profitIndex;
	
	/**
	 * Tracks the current index in the rank array.
	 */
	private int rankIndex;
	
	/**
	 * The company to process a report on.
	 */
	private String theCompany;
	
	/**
	 * Creates new CompanyReport for given company; data to be read from given file.
	 * @param inputFileIn - File containing Fortune 500 data for this report.
	 * @param companyIn - Company to report Fortune 500 data.
	 */
	public CompanyReport(File inputFileIn, String companyIn)
	{
		this.setCompany(companyIn);
		this.fileExists = false;
		try 
		{
			this.setFile(inputFileIn);
			this.inputScanner = new Scanner(this.theFile);
			this.fileExists = true;
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		this.totalRevenue = new Double[Report.NUMCOMPANIES];
		this.totalProfit = new Double[Report.NUMCOMPANIES];
		this.totalRanks = new Double[Report.MAXYEAR - Report.MINYEAR];
		this.revenueIndex = 0;
		this.profitIndex = 0;
		this.rankIndex = 0;
		this.isProcessed = false;	
	}
	
	/**
	 * A method used to verify the file has been loaded properly.
	 * @param fileIn The file to load in.
	 * @throws FileNotFoundException Thrown if the file is not found.
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
	 * Calculates the minimum, maximum, average, and standard deviation of revenues, profits, and rank
	 *  for all years the company is ranked using tools in the Data class.
	 * @return true if processing successful, false if the input file does not exist.
	 */
	public boolean processReport() 
	{
		boolean result = false;
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
			this.minRank = Data.minimum(this.getRanksList());
			this.maxRank = Data.maximum(this.getRanksList());
			this.avgRank = Data.average(this.getRanksList());
			this.sDRank = Data.standardDeviation(this.getRanksList());
			result = true;
			this.isProcessed = true;
		}
		return result;
	}
	
	/**
	 * This method is responsible for retrieving all of the values from the file for the given company.
	 */
	private void extractNumbers()
	{
		if (this.inputScanner.hasNextLine())
		{
			this.nextLine = this.inputScanner.nextLine(); // Need to skip the first line since it
		}                                               // only contains column names.
		while (this.inputScanner.hasNextLine())
		{
			this.nextLine = this.inputScanner.nextLine();
			this.lineParser = new Scanner(this.nextLine); 
			this.lineParser.useDelimiter(","); 
			this.lineParser.next(); // Skip this value to get to the rank.
			yearlyRank = this.lineParser.nextInt();
			if (this.lineParser.next().equals(this.theCompany))
			{
				this.sumRank();
				this.sumRevenues();
				this.sumProfits();
			}
		}
		this.inputScanner.close();
		this.lineParser.close();
	}
	
	/**
	 * This method is called to add new rank values to the rank array.
	 */
	private void sumRank()
	{
		this.totalRanks[this.rankIndex] = (Double) (double) this.yearlyRank;
		this.rankIndex++;
	}
	
	/**
	 * This method is called to add new revenue values to the revenue array.
	 */
	private void sumRevenues()
	{
		this.totalRevenue[this.revenueIndex] = this.lineParser.nextDouble();
		this.revenueIndex++;
	}
	
	/**
	 * This method is called to add new profit values to the profit array.
	 */
	private void sumProfits()
	{
		this.totalProfit[this.profitIndex] = this.lineParser.nextDouble();
		this.profitIndex++;
	}
	
	/**
	 * This method retrieves the revenue array.
	 * @return The revenue array.
	 */
	private Double[] getRevenuesList()
	{
		return this.totalRevenue;
	}
	
	/**
	 * This method retrieves the profit array.
	 * @return The profits array.
	 */
	private Double[] getProfitsList()
	{
		return this.totalProfit;
	}
	
	/**
	 * This method retrieves the rank array.
	 * @return The ranks array.
	 */
	private Double[] getRanksList()
	{
		return this.totalRanks;
	}
	
	/**
	 * This method returns the minimum rank.
	 * @return The minimum rank
	 */
	private String getMinRank()
	{
		if (this.minRank == null)
		{
			return null;
		}
		else
		{
			String s = this.minRank.toString();
			// I must test the length of the string to make the correct slice.
			if (s.length() == 3)
			{
				s = s.substring(0, 1);
			}
			else if (s.length() == 4)
			{
				s = s.substring(0, 2);
			}
			else
			{
				s = s.substring(0, 3);
			}
			return s;
		}
	}
	
	/**
	 * This method returns the maximum rank.
	 * @return The maximum rank
	 */
	private String getMaxRank()
	{
		if (this.maxRank == null)
		{
			return null;
		}
		else
		{
			String s = this.maxRank.toString();
			// I must test the length of the string to make the correct slice.
			if (s.length() == 3)
			{
				s = s.substring(0, 1);
			}
			else if (s.length() == 4)
			{
				s = s.substring(0, 2);
			}
			else
			{
				s = s.substring(0, 3);
			}
			return s;
		}
	}
	
	/**
	 * Writes the processed report to the given file.
	 * The given file's contents will look like the result of calling CompanyReport's toString.
	 * @param outputFile - File to write report to.
	 * @throws DataNotProcessedException - Thrown if write attempted and report has not yet been processed.
	 * @return true if write successful, false if file cannot be created. 
	 */
	public boolean writeReport(File outputFile) throws DataNotProcessedException
	{
		if (!this.isProcessed)
		{
			throw new DataNotProcessedException();
		}
		boolean result = false;
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
	 * Fortune 500 Report for COMPANY ranked RANKED times
	 * Revenue
	 * Min: MINREV Max: MAXREV Avg: AVGREV StD: STDREV
	 * Profit
	 * Min: MINPRO Max: MAXPRO Avg: AVGPRO StD: STDPRO
	 * Rank
	 * Min: MINRANK Max: MAXRANK Avg: AVGRANK StD: STDRANK
	 * 
	 * Where COMPANY is the company, RANKED is the number of times the company has been ranked in the file,
	 *  MINREV, MAXREV, AVGREV, STDREV are the minimum, maximum, average, and standard deviation of revenues,
	 *  MINPRO, MAXPRO, AVGPRO, STDPRO are the minimum, maximum, average, and standard deviation of profits,
	 *  and MINRANK, MAXRANK, AVGRANK, STDRANK are the minimum, maximum, average, and standard deviation of rank.
	 * These are all floating point values formatted to exactly three decimals except for MINRANK and MAXRANK
	 *  which are whole number values.
	 * NOTE: There are no blank lines before, after, or between the lines, and the String
	 *  DOES NOT end in a new line.
	 *  If your toString is not formatted exactly most tests will fail.
	 *  A JUnit test for this method is provided in the tests package to ensure your formatting is correct.
	 *  Additionally, remember that all are formatted to exactly three decimal places,
	 *   which will explain any "nul"s you see in the provided test case.
	 */
	@Override
	public String toString()
	{
		String result;
		result = String.format("Fortune 500 Report for %s ranked %d times\n"
				+ "Revenue\n"
				+ "Min: %.3f Max: %.3f Avg: %.3f StD: %.3f\n"
				+ "Profit\n"
				+ "Min: %.3f Max: %.3f Avg: %.3f StD: %.3f\n"
				+ "Rank\n"
				+ "Min: %s Max: %s Avg: %.3f StD: %.3f", this.getCompany(), this.rankIndex,
				this.minRevenue, 
				this.maxRevenue, this.avgRevenue, this.sDRevenue, this.minProfit,
				this.maxProfit, this.avgProfit, this.sDProfit, this.getMinRank(),
				this.getMaxRank(), this.avgRank, this.sDRank);
		return result;
	}

	/**
	 * Returns the company of this report.
	 * @return Company of this report.
	 */
	public String getCompany()
	{
		return this.theCompany;
	}
	
	/**
	 * This method sets proper company names.
	 * @param companyIn The company name to be set.
	 */
	private void setCompany(String companyIn)
	{
		if (companyIn != null)
		{
			this.theCompany = companyIn;
		}
	}
}
