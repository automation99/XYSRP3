package FlyModules;

import java.text.DecimalFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.lang3.ArrayUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import flows.XYSRP_Flow;
import pageObjects.Database;


public class Flynas extends XYSRP_Flow {
	
	static String NoFlights;
	static String result;
	static String F3Dummy;
	static String From;
	static String To;
	static String Flights;
	static String Depdate=null;
	private static boolean isNoThanksClicked = false;
	
	public static void search(WebDriver driver) throws Exception
	{

		driver.get("https://accounts.google.com/v3/signin/identifier?dsh=S873427101%3A1670174877878096&continue=https%3A%2F%2Fmail.google.com%2Fmail%2F&flowEntry=ServiceLogin&flowName=GlifWebSignIn&rip=1&sacu=1&service=mail&ifkv=ARgdvAv7qIg9j-X7zxwLWrETGRTaquhiB_tbb7YW19ONpQZ-z4IHi9LknQfITZIbwMLY0zXURVL5jg");
	    Thread.sleep(1500);
		
		
	}
	
    public static void FlightDetails(WebDriver driver,Database PnrDetails) throws Exception
	{
        
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(25)); // Set the maximum wait time to 60 seconds
		boolean isPageLoaded = false;
		int maxAttempts = 2;
		int attempt = 1;
		if (!isNoThanksClicked) {
            List<WebElement> iframes = driver.findElements(By.tagName("iframe"));
            if (iframes.size() > 6) { // Ensure the iframe exists
                WebElement iframeElement = iframes.get(6); 
                driver.switchTo().frame(iframeElement);

                try {
                    // Wait for the "No Thanks" button to be clickable
                    System.out.println("Waiting for 'No Thanks' button...");
                    WebElement noThanksButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("deny")));
                    System.out.println("'No Thanks' button found!");

                    // Click the button
                    noThanksButton.click();
                    System.out.println("'No Thanks' button clicked!");

                    // Mark as clicked
                    isNoThanksClicked = true;
                } catch (Exception e) {
                    System.out.println("Failed to interact with 'No Thanks' button: " + e.getMessage());
                } finally {
                    // Switch back to the main content
                    driver.switchTo().defaultContent();
                    System.out.println("Switched back to main content!");
                }
            } else {
                System.out.println("Iframe not found or fewer than 7 iframes present on the page.");
            }
        }

		while (!isPageLoaded && attempt <= maxAttempts) {
		    try {
		        // Wait for the page to load completely
		        isPageLoaded = wait.until(ExpectedConditions.urlContains("https://booking.flynas.com/#/booking/flights"));
		    } catch (Exception e) {
		   
		    	try {
		    	driver.manage().deleteAllCookies();
		    	search(driver);
		    	driver.manage().deleteAllCookies();
		        // Refresh the page
		        driver.get(FlynasURL);
		        Thread.sleep(10000);
		        System.out.println("Cookies deleted. Page refreshed.");
		        }
		    	catch (Exception e1) {
		    		try {
		    		 isPageLoaded = wait.until(ExpectedConditions.urlContains("https://booking.flynas.com/#/booking/flights"));
		    		}
		    		catch (Exception e2) {
				    	driver.manage().deleteAllCookies();
				    	search(driver);
				    	driver.manage().deleteAllCookies();
				        // Refresh the page
				        driver.get(FlynasURL);
				        Thread.sleep(10000);
				        System.out.println("Cookies deleted. Page refreshed.");
				        }
		    	}
		    	
		    }

		    attempt++;
		}

		Actions actions = new Actions(driver);
			
		try{
		boolean displayed = false;
		do{
		  try{
		    displayed = driver.findElement(By.xpath("//button[contains(text(),'Back')]")).isDisplayed();
		  } 
		  catch (Exception e){
			
			   driver.get(FlynasURL); 
			   Thread.sleep(4000);
		 }
		} while(!displayed);
		
		String departureAirport = PnrDetails.From;
		String arrivalAirport = PnrDetails.To;

		//String[] airportsToHandle = {"CAI", "SAW", "IST"};
		String[] airportsToHandle = {"CAI"};

		if (ArrayUtils.contains(airportsToHandle, departureAirport) || ArrayUtils.contains(airportsToHandle, arrivalAirport)) {
		    try {
		        Thread.sleep(2000);
		        WebElement NoThanks = driver.findElement(By.xpath("//*/text()[normalize-space(.)='No Thanks!']/parent::*"));
		        NoThanks.click();
		        Thread.sleep(2000);
		    } catch (Exception e) {
		       
		    }
		} else {
		    
		}
		try{
    		WebElement elementToMoveAndClick = driver.findElement(By.xpath("//div[@class='pers-modal__btn-close-icon']"));
            actions.moveToElement(elementToMoveAndClick).perform();
            elementToMoveAndClick.click();
	     }
    	catch(Exception e){
    		
    	}
		WebElement FromCity=driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Modify your Search'])[1]/preceding::span[7]"));
		From =FromCity.getText();
		WebElement ToCity=driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Modify your Search'])[1]/preceding::span[5]"));
		To =ToCity.getText();
		
		if ("TR1".equals(From)) {
			From = "SAW";
			PnrDetails.From="SAW";
		} else if ("TR1".equals(To)) {
			To = "SAW";
			PnrDetails.To="SAW";
		} else {

		}
		if ("EG1".equals(From)) {
		    From = "CAI";
		    PnrDetails.From="CAI";
		}
		else if ("EG1".equals(To)) {
		    To = "CAI";
		    PnrDetails.To="CAI";
		} else {

		}
		if ("AE1".equals(From)) {
		    From = "DXB";
		    PnrDetails.From="DXB";
		}
		else if ("AE1".equals(To)) {
		    To = "DXB";
		    PnrDetails.To="DXB";
		} else {

		}
		
		try {
	           
       	    driver.findElement(By.xpath("//a[contains(@class, 'btn_prev')]")).click();
            //Thread.sleep(2000);
       	 int dayCounter = 1; // Initialize a counter for day offset

       	for (int weekOffset = 0; weekOffset < 7; weekOffset++) {
       	    for (int dayOffset = 1; dayOffset <= 5; dayOffset++) {
       	        int totalOffset = weekOffset * 5 + dayOffset;

       	        if (totalOffset > 35) {
       	            break; // Exit the loop if the total days processed exceed 35
       	        }
       	        driver.findElement(By.xpath("//a[@class='btn-refresh']")).click();
       	        String DepartDate = driver.findElement(By.xpath("//*[@id='select_departure']/div/ul/li[" + dayCounter + "]/a/span[2]")).getText();
       	        String[] dateParts = DepartDate.split("\\W+");
       	        int dayInt = Integer.parseInt(dateParts[1]);
       	        String day = String.format("%02d", dayInt);
       	        String monthAbbreviation = dateParts[2];
       	        String Year = "2024";
       	        if (monthAbbreviation.equals("Nov") || monthAbbreviation.equals("Dec")) {
       	            Year = "2024";
       	        } else {
       	            Year = "2025";
       	        }
       	        Depdate = String.format("%s %s %s", day, monthAbbreviation, Year);
 
       	        System.out.println("SRP Date: " + Depdate);

       	        String FlightsAvailable = driver.findElement(By.xpath("//*[@id='select_departure']/div/ul/li[" + dayCounter + "]/a/span[1]/span/span")).getText().replaceAll("[\r\n]+", " ");
       	        //System.out.println(FlightsAvailable); 
       	        boolean isFlightsAvailable = !FlightsAvailable.contains("Sold") && !FlightsAvailable.contains("No");
       	        if (isFlightsAvailable) {
       	            //int dayInt = Integer.parseInt(day);
       	            driver.findElement(By.xpath("//*[@id='select_departure']/div/ul/li[" + dayCounter + "]/a/span[1]/span/span")).click();
       	            Thread.sleep(2000);

       	            try {
       	                boolean isDisplayed = driver.findElement(By.xpath("//span[contains(text(),'" + dayInt + " " + monthAbbreviation + " " + Year + "')]")).isDisplayed();
       	                if (isDisplayed) {
       	                	SRP_Flights(driver, PnrDetails);
       	                } else {
       	                	Thread.sleep(5000);
       	                	driver.findElement(By.xpath("//*[@id='select_departure']/div/ul/li[" + dayCounter + "]/a/span[1]/span/span")).click();
       	                    Thread.sleep(2000);
       	                    driver.findElement(By.xpath("//span[contains(text(),'" + dayInt + " " + monthAbbreviation + " " + Year + "')]")).isDisplayed();
       	                    SRP_Flights(driver, PnrDetails);
       	                }
       	            } catch (NoSuchElementException e) {
       	            	Thread.sleep(5000);
       	            	driver.findElement(By.xpath("//*[@id='select_departure']/div/ul/li[" + dayCounter + "]/a/span[1]/span/span")).click();
   	                    Thread.sleep(2000);
   	                    driver.findElement(By.xpath("//span[contains(text(),'" + dayInt + " " + monthAbbreviation + " " + Year + "')]")).isDisplayed();
   	                    SRP_Flights(driver, PnrDetails);
       	            }
       	        } else {
       	            System.out.println("No Flights");
       	            String From = PnrDetails.From;
       	            String To = PnrDetails.To;
       	            List<FadFlightDetails> finalList = new ArrayList<FadFlightDetails>();
       	            ApiMethods.sendResults(From, To, Depdate, finalList);
       	        }

       	        dayCounter++; // Increment the day counter

       	        // If it's the last iteration of the inner loop and not the last week, click on the "Next" button
       	        if (dayOffset == 5 && weekOffset < 6) {
       	        	driver.findElement(By.xpath("//a[@class='btn-refresh']")).click();
       	            driver.findElement(By.xpath("//a[contains(@class, 'btn_next')]")).click();
       	            Thread.sleep(1000);
       	        }
       	    }
       	}

       } catch (Exception e) {
           // Handle exceptions
           
       }
		
		}	
	   catch (Exception e){
		driver.manage().deleteAllCookies();
		driver.get("https://www.iana.org/domains");
	}
}
	
	
	public static void SRP_Flights(WebDriver driver,Database PnrDetails) throws Exception
	{
		
		/*WebElement FlightText=driver.findElement(By.xpath("//div[2]/div/div[2]/div"));
		Flights =FlightText.getText().replace(")Direct", ") Direct");		
		System.out.println(Flights);
		NoFlights= Flights.split(" ")[5];*/
		
		
		if(From.equals(PnrDetails.From))
		 { 
			System.out.println("From City Matched");
			
			if(To.equals(PnrDetails.To))
			 {
				System.out.println("To City Matched");
				
				if("EG1".equals(PnrDetails.From))
				 {
					PnrDetails.From="CAI";
				 }
				else if("EG1".equals(PnrDetails.To))
				 {
					PnrDetails.To="CAI";
				 }
			     else{
			    	
			     }
				if("AE1".equals(PnrDetails.From))
				 {
					PnrDetails.From="DXB";
				 }
				else if("AE1".equals(PnrDetails.To))
				 {
					PnrDetails.To="DXB";
				 }
			     else{
			    	
			     }
				
				XY_FlightDetailsSending_Economy(driver, PnrDetails);
				
				/*if("a".equals(NoFlights))
				 { 
					//driver.manage().deleteAllCookies();
					System.out.println("No Flights Available");
					No_Flights(driver, PnrDetails);
					NoFlights ="null";
					Flynas.search(driver);
				 }
				else if("(0)".equals(NoFlights))
				{
					//driver.manage().deleteAllCookies();
					System.out.println("No Direct Flights");
					No_Flights(driver, PnrDetails);
					NoFlights =" ";
				}
				
				
				else {
					//System.out.println("ECONOMY AND PREMIUM FLIGHT");
					XY_FlightDetailsSending_Economy(driver, PnrDetails);
					//XY_FlightDetailsSending_Economy_Premium(driver, PnrDetails);
				}*/
			 }
			else {
				System.out.println("To City Mismatch");
				Thread.sleep(3000);
				driver.get(FlynasURL); 
				Thread.sleep(1000);
				FlightDetails(driver, PnrDetails);
				
		        }
		       }
			else {
				
				System.out.println("From City Mismatch");
				Thread.sleep(3000);
				driver.get(FlynasURL); 
				Thread.sleep(1000);
				FlightDetails(driver, PnrDetails);
				
		 }
	}
		
public static void XY_FlightDetailsSending_Economy(WebDriver driver,Database PnrDetails) throws Exception
{
	//flyadealPage.flight_Details();
	String DataChanege=null;
	String date = null;
	String month = null;
	String year = null;
	String FlightNum = null;
	String JournyTimeHours = null;
	 String JournyTimeMin=null;
	 String EndTime=null;
	 String From=PnrDetails.From;
	 String To=PnrDetails.To;
	 String flySeatNum="99";
	 String economy=null;
	 String premium=null;
	 String StartTerminal=null;
	 String EndTerminal=null;

	 String flyPlusSeatNum="99";
	 String Sold=null;
	 List<FadFlightDetails> finalList =  new ArrayList<FadFlightDetails>();
	 //driver.findElement(By.xpath("//*[@id='collapseOne0']/div/div[2]/div[1]/button[2]")).click();
	 //Thread.sleep(1500);
	 //WebDriverWait wait = new WebDriverWait(driver, 10);
     //WebElement directFlightsButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'Direct Flights')]")));

     // Click on the button
     //directFlightsButton.click();
		
	try {
		String ele = null;
		List<WebElement> element = driver.findElements(By.xpath("//div[@class='flight_table ng-star-inserted']/div[@class='card px-0']"));
		 for (WebElement e1 : element) {
				 ele = e1.getText();
				 //System.out.println(ele);
				 FadFlightDetails currentFlightFly = new FadFlightDetails();
				 FadFlightDetails currentFlightFlyPlus = new FadFlightDetails();
				 
              
				 String str1=ele.replaceAll("[\r\n]+", " ").replace(",", "");
				 String s=str1.replaceAll("Non-stop ","").replaceAll("Flight Details ", "").replaceAll("XY ", "").replaceAll("SAR", "").replaceAll("hr ", "").replaceAll("min ", "")
						 .replaceAll("from ", "").replaceAll("Sold out", "Soldout").replaceAll("New Delhi", "NewDelhi").replaceAll("XY", "").replaceAll("Doha Hamad International Airport", "DohaHamadInternationalAirport").replaceAll("Istanbul Sabiha Gokcen Airport", "IstanbulSabiha").replaceAll("Airbus 320 | Operated by flynas ", "").replaceAll("Boeing 739-739 ", "").replaceAll("Cairo International Airport ", "").replaceAll("Airbus 320 ", "").replaceAll("Airbus 330 ", "").replaceAll("Sharm el Sheikh", "SharmelSheikh").replaceAll("Al Jouf", "AlJouf").replaceAll("Cairo Sphinx Airport", "CairoSphinxAirport").replaceAll("Doha Hamad international Airport", "DohaHamadinternationalAirport").replaceAll("Istanbul Sabiha Gokcen International Airport", "IstanbulSabihaGokcenInternationalAirport").replaceAll("King Abdulaziz International Airport", "KingAbdulazizInternationalAirport").replaceAll("King Khalid International Airport", "KingKhalidInternationalAirport").replaceAll("Hamad International Airport", "HamadInternationalAirport").replaceAll("Sharm El-Sheikh", "SharmElSheikh").replaceAll("Istanbul Sabiha", "IstanbulSabiha").replaceAll("Al Baha", "AlBaha").replaceAll("Abu Dhabi", "AbuDhabi").replaceAll("332 Jeddah", "Jeddah").replaceAll("Promo ", "");
				 
				 
				 String Str = new String(s);
			      
				 String[] flightDetails = s.split("\n");
		            
		            for (String flightDetail : flightDetails) {
					if (flightDetail.contains("Sphinx") || flightDetail.contains("change aircraft") || flightDetail.contains("588") || flightDetail.contains("587") || flightDetail.contains("286") || flightDetail.contains("285")) {
				        continue; // Skip this flight and go to the next iteration
				    }
				
			     
			   //894 Dammam 19:50 3 25 Cairo 22:15 369.00 899.00	  
				//57 Riyadh 22:50 2 0 Jeddah 00:50 (+1) 219.01 414.00 Only 4 seats left
				// 57 Riyadh 22:50 2 0 Jeddah 00:50 (+1) Soldout 1554.00 Only 1 seats left
				// 3 Riyadh 05:25 2 0 Jeddah 07:25 409.00 Only 2 seats left 788.00 Only 2 seats left
				 System.out.println(s);
				 String StartTime= s.split(" ")[2];
				 FlightNum= s.split(" ")[0];
				 
				 
				 if (FlightNum.length() == 1) {
			            result = "11" + FlightNum;
			            int resultInt = Integer.parseInt(result) * 3;
			            F3Dummy = "4" + Integer.toString(resultInt);
			            //System.out.println("Dummy Flight:"+F3Dummy);
			            
			        } else if (FlightNum.length() == 2) {
			            result = "1" + FlightNum;
			            int resultInt = Integer.parseInt(result) * 3;
			            F3Dummy = "4" + Integer.toString(resultInt);
			            //System.out.println("Dummy Flight:"+F3Dummy);
			        }
			        else{
			        	result = "1" + FlightNum;
			        	int resultInt = Integer.parseInt(result) * 3;
			        	String resultString = Integer.toString(resultInt);
			        	String lastThreeChars = resultString.substring(resultString.length() - 3);
			        	F3Dummy = "4" + lastThreeChars;
			        	//System.out.println("Dummy Flight: " + F3Dummy);
				         
			        }
				 
				 
				 //System.out.println("FLIGHT NOOO:"+FlightNum);
				 EndTime= s.split(" ")[6];
				 JournyTimeHours=s.split(" ")[3];
				 JournyTimeMin=s.split(" ")[4];
				String PlusOne= s.split(" ")[7];
				String Seats = "9";	
				
				String[] splitArr = s.split(" ");
				if (splitArr.length >= 10) {
				    //Seats = splitArr[9];
					for (int i = 0; i < splitArr.length; i++) {
					    if ("Only".equals(splitArr[i]) && i + 1 < splitArr.length) {
					        Seats = splitArr[i + 1];
					        break; // Exit the loop once seats count is found
					    }
					}
				}

				//System.out.println("Seats count: " + Seats);
				
				if(PlusOne.equals("(+1)"))
				{
					
				 DataChanege="1";
				 economy=s.split(" ")[8];
				 if (splitArr.length >= 10) {
					    //Seats = splitArr[10];
					  for (int i = 0; i < splitArr.length; i++) {
					     if ("Only".equals(splitArr[i]) && i + 1 < splitArr.length) {
					         Seats = splitArr[i + 1];
					         break; // Exit the loop once seats count is found
					     }
					 }
					}

					//System.out.println("Seats count: " + Seats);
				
				// 57 Riyadh 22:50 2 0 Jeddah 00:50 (+1) Soldout 1554.00 Only 1 seats left
				 if("Soldout".equals(economy))
				 {
					  economy="00.0";
				 }
				 else{
					 
				 }
				//57 Riyadh 22:50 2 0 Jeddah 00:50 (+1) 529.00 Only 5 seats left 788.00 Only 1 seats left
					
				
				 
				
				}
				else{
					
					 economy=s.split(" ")[7];
					
					// 57 Riyadh 22:50 2 0 Jeddah 00:50 Soldout 1554.00 Only 1 seats left
					 if("Soldout".equals(economy))
					 {
						  economy="00.0";
					 }
					 else{
						 
					 }
					// 57 Riyadh 22:50 2 0 Jeddah 00:50 1554.00 Soldout Only 1 seats left
					
					 
					 
				}
				
				if ("00.0".equals(economy)) {
				    economy = "00.0";
				} else {
				    // Convert economy to a double
				    double economyValue = Double.parseDouble(economy);

				    // Check if economy price is more than 1000
				    if (economyValue > 1000) {
				    	 // Subtract a dynamic value between 57 and 64 for each flight
				    	double dynamicSubtract = Math.min(Math.random() * 5 + 5, economyValue); // Ensure subtraction doesn't make the fare negative
				    	economyValue -= dynamicSubtract;
				    }
				    // Check if economy price is more than 500
				    else if (economyValue > 500) {
				    	 // Subtract a dynamic value between 27 and 34 for each flight
				    	double dynamicSubtract = Math.min(Math.random() * 5 + 1, economyValue); // Ensure subtraction doesn't make the fare negative
				    	economyValue -= dynamicSubtract;
				       
				    } else {
				    	// Subtract a dynamic value between 16 and 23 for each flight
		                double dynamicSubtract = Math.min(Math.random() * 5 + 1, economyValue); // Ensure subtraction doesn't make the fare negative
		                economyValue -= dynamicSubtract;
				    }

				 // Ensure the value does not go below 0
				    economyValue = Math.max(economyValue, 0);
				    // Convert it back to a string
				 // Format it back to a string with 2 decimal places
		            DecimalFormat decimalFormat = new DecimalFormat("#0.00");
		            economy = decimalFormat.format(economyValue);

				    
				}
				
				
			    
				     
				/*System.out.println("From:"+From);
				System.out.println("To:"+To);
				System.out.println("economy:"+economy);
				System.out.println("DepartureDate:"+Depdate);
				System.out.println("Currency:SAR");
				
				
				System.out.println("FlightNumber:"+FlightNum);
				System.out.println("Class :Economy");*/
				int Hours = Integer.parseInt(JournyTimeHours);	
				int TotalMin=Hours * 60;
				
				int Min = Integer.parseInt(JournyTimeMin);	
				int Total=TotalMin+Min;
				String JournyTimeTotal=Integer.toString(Total);
				/*System.out.println("JourneyTime:"+Total);
				
				
				
				System.out.println("StartTime :"+StartTime);
				System.out.println("EndTime :"+EndTime);
				System.out.println("StartDate:"+Depdate);
				System.out.println("EndDate:Null");
				System.out.println("Start Airport:"+From);
				System.out.println("End Airport:"+To);
				System.out.println("Fly Fare:"+economy);*/
				
				//System.out.println("----------------------------------------");
				
				currentFlightFlyPlus.FareType=currentFlightFly.FareType="Economy";
				currentFlightFlyPlus.Class=currentFlightFly.Class="Economy";
				currentFlightFlyPlus.StartAirp = currentFlightFly.StartAirp =From;
				currentFlightFlyPlus.EndAirp=currentFlightFly.EndAirp=To;
				currentFlightFlyPlus.StartDt=currentFlightFly.StartDt=Depdate;
				currentFlightFlyPlus.ADTBG=currentFlightFly.ADTBG="";
				currentFlightFlyPlus.CHDBG=currentFlightFly.CHDBG="";
				currentFlightFlyPlus.INFBG=currentFlightFly.INFBG="";
				currentFlightFlyPlus.DayChg=currentFlightFly.DayChg = DataChanege;
				currentFlightFlyPlus.Fltnum=currentFlightFly.Fltnum="XY"+FlightNum;
								
				/*if (element.size() >= 1) {
				    if (From.equals("TIF")) { 
				        if (To.equals("RUH")) {
				            currentFlightFlyPlus.Fltnum = currentFlightFly.AltFltnum = "F3" + F3Dummy;
				        }
				    } else if (From.equals("RUH")) {
				        if (To.equals("TIF")) {
				            currentFlightFlyPlus.Fltnum = currentFlightFly.AltFltnum = "F3" + F3Dummy;
				        } else if (To.equals("JED")) {
				            currentFlightFlyPlus.Fltnum = currentFlightFly.AltFltnum = "F3" + F3Dummy;
				        }
				    } else if (From.equals("JED")) {
				        if (To.equals("RUH")) {
				            currentFlightFlyPlus.Fltnum = currentFlightFly.AltFltnum = "F3" + F3Dummy;
				        } else if (To.equals("AHB")) {
				            currentFlightFlyPlus.Fltnum = currentFlightFly.AltFltnum = "F3" + F3Dummy;
				        } else if (To.equals("DMM")) {
				            currentFlightFlyPlus.Fltnum = currentFlightFly.AltFltnum = "F3" + F3Dummy;
				        } else if (To.equals("ELQ")) {
				            currentFlightFlyPlus.Fltnum = currentFlightFly.AltFltnum = "F3" + F3Dummy;
				        }
				    } else if (From.equals("AHB")) {
				        if (To.equals("JED")) {
				            currentFlightFlyPlus.Fltnum = currentFlightFly.AltFltnum = "F3" + F3Dummy;
				        }
				    } else if (From.equals("DMM")) {
				        if (To.equals("JED")) {
				            currentFlightFlyPlus.Fltnum = currentFlightFly.AltFltnum = "F3" + F3Dummy;
				        }
				    } else if (From.equals("ELQ")) {
				        if (To.equals("JED")) {
				            currentFlightFlyPlus.Fltnum = currentFlightFly.AltFltnum = "F3" + F3Dummy;
				        }
				    }
				}

				else{
					
				}*/
				
				currentFlightFlyPlus.JrnyTm=currentFlightFly.JrnyTm=JournyTimeTotal;
				currentFlightFlyPlus.StartTm=currentFlightFly.StartTm=StartTime;
				currentFlightFlyPlus.EndTm=currentFlightFly.EndTm=EndTime;
				currentFlightFlyPlus.NoOfSeats=currentFlightFly.NoOfSeats=Seats;
				//currentFlightFlyPlus.StartTerminal=currentFlightFly.StartTerminal="";
				currentFlightFlyPlus.EndTerminal=currentFlightFly.EndTerminal="";
				currentFlightFlyPlus.AdultBasePrice=currentFlightFly.AdultBasePrice=economy.replace(",", "");
				currentFlightFlyPlus.AdultTaxes=currentFlightFly.AdultTaxes ="";
				currentFlightFlyPlus.ChildBasePrice=currentFlightFly.ChildBasePrice=economy.replace(",", "");
				currentFlightFlyPlus.ChildTaxes=currentFlightFly.ChildTaxes="";
				//currentFlightFlyPlus.InfantBasePrice=currentFlightFly.InfantBasePrice ="60";
				
				if (From.equals("AMM")) {
				    if (To.equals("RUH")) {
				        currentFlightFlyPlus.InfantBasePrice=currentFlightFly.InfantBasePrice ="413";
				    } else {
				        currentFlightFlyPlus.InfantBasePrice=currentFlightFly.InfantBasePrice ="75";
				    }
				} else if (From.equals("CAI")) {
				    if (To.equals("DMM") || To.equals("JED") || To.equals("RUH")) {
				        currentFlightFlyPlus.InfantBasePrice=currentFlightFly.InfantBasePrice ="320";
				    } else {
				        currentFlightFlyPlus.InfantBasePrice=currentFlightFly.InfantBasePrice ="75";
				    }
				} else if (From.equals("DEL")) {
				    if (To.equals("RUH")) {
				        currentFlightFlyPlus.InfantBasePrice=currentFlightFly.InfantBasePrice ="161";
				    } else {
				        currentFlightFlyPlus.InfantBasePrice=currentFlightFly.InfantBasePrice ="75";
				    }
				} else if (From.equals("DMM")) {
				    if (To.equals("CAI") || To.equals("DXB")) {
				        currentFlightFlyPlus.InfantBasePrice=currentFlightFly.InfantBasePrice ="140";
				    } else {
				        currentFlightFlyPlus.InfantBasePrice=currentFlightFly.InfantBasePrice ="75";
				    }
				} else if (From.equals("DOH")) {
				    if (To.equals("JED") || To.equals("RUH")) {
				        currentFlightFlyPlus.InfantBasePrice=currentFlightFly.InfantBasePrice ="130";
				    } else {
				        currentFlightFlyPlus.InfantBasePrice=currentFlightFly.InfantBasePrice ="75";
				    }
				} else if (From.equals("DXB")) {
				    if (To.equals("DMM") || To.equals("JED") || To.equals("RUH")) {
				        currentFlightFlyPlus.InfantBasePrice=currentFlightFly.InfantBasePrice ="140";
				    } else {
				        currentFlightFlyPlus.InfantBasePrice=currentFlightFly.InfantBasePrice ="75";
				    }
				} else if (From.equals("JED")) {
				    if (To.equals("CAI") || To.equals("DOH") || To.equals("AUH") || To.equals("DXB")) {
				        currentFlightFlyPlus.InfantBasePrice=currentFlightFly.InfantBasePrice ="140";
				    } else {
				        currentFlightFlyPlus.InfantBasePrice=currentFlightFly.InfantBasePrice ="75";
				    }
				} else if (From.equals("KWI")) {
				    if (To.equals("RUH")) {
				        currentFlightFlyPlus.InfantBasePrice=currentFlightFly.InfantBasePrice ="151";
				    } else {
				        currentFlightFlyPlus.InfantBasePrice=currentFlightFly.InfantBasePrice ="75";
				    }
				} else if (From.equals("LKO")) {
				    if (To.equals("RUH")) {
				        currentFlightFlyPlus.InfantBasePrice=currentFlightFly.InfantBasePrice ="161";
				    } else {
				        currentFlightFlyPlus.InfantBasePrice=currentFlightFly.InfantBasePrice ="75";
				    }
				} else if (From.equals("RUH")) {
				    if (To.equals("AMM") || To.equals("CAI") || To.equals("DEL") || To.equals("DOH") || To.equals("DXB") || To.equals("KWI") || To.equals("LKO") || To.equals("TBS")) {
				        currentFlightFlyPlus.InfantBasePrice=currentFlightFly.InfantBasePrice ="140";
				    } else {
				        currentFlightFlyPlus.InfantBasePrice=currentFlightFly.InfantBasePrice ="75";
				   }  
				  } 
				else {
			        currentFlightFlyPlus.InfantBasePrice=currentFlightFly.InfantBasePrice ="75";
			   }  
				
				currentFlightFlyPlus.InfantTaxes=currentFlightFly.InfantTaxes="";
				currentFlightFlyPlus.TotalApiFare=currentFlightFly.TotalApiFare="";
				finalList.add(currentFlightFly);
		        
				WebElement FlightText=driver.findElement(By.xpath("//div[2]/div/div[2]/div"));
				ele =FlightText.getText();
				
		 }
		} 
	}
	
		
		 catch (Exception e) {
		
	}
		
	    System.out.println("----------------------------------------");
	    //driver.manage().deleteAllCookies();
		ApiMethods.sendResults(From, To,Depdate, finalList);
	
}

}





