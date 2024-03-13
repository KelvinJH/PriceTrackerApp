# Price Tracker Application: ECFX Project

## Summary
This is a simple Java application leveraging JSoup, Selenium, and Java Swing to monitor the price of the 10th generation iPad. The monitoring is done by scraping the Amazon product page for the price and storing that in a text file. 
The application will also produce an on-screen notification to alert the user that the current price is lower than the last scraped price. The project uses Maven to manage dependencies.


## Configuring and running
Running the application is pretty straightforward. You can run the application from either the command line or with the help of an IDE like VSCode or IntelliJ. It was easier for me to compile and run the application with VSCode; however, if there is a burning desire to run 
with the command line. Simply navigate to the project directory and run the following commands:

Compile the project.
```mvn compile```

If your environment variables are set correctly, then you can also run the following command to start the application:

```java -cp target/classes com.ecfx.PriceTrackerApp```

The thing to remember when running with the command line is that this project uses Selenium for some of the browser inputs and requires Chromedriver. Make sure it is also in the environmental variables of your machine.

When the application starts, it will prompt the user for two inputs. The first is if we want to run the application with test data. A simple 1 for yes or 2 for no will move you forward. The second input that is requested is the duration between each poll, in hours. 
For example, entering 1 would poll the Amazon page every hour, while 5 would do it every 5 hours. After that, there will sometimes be a prompt for captcha before scraping.


## Challenges and possible improvements
There were two challenges that were overcome through this project. 

The first challenge was building an application that adheres to Amazon's scraping policies. Since the objective of the project was to collect data by scraping, we couldn't leverage some of Amazon's public facing APIs. Another thing I had to keep in mind was to not get IP blocked from
excessively scraping the page. To work around this, I copied the html of the iPad product page and stored it locally to allow for testing without spamming the connection. This allowed me to proceed ethically without appearing like a bot.

The second was identifying a way to solve the captcha. Amazon, to defend against bots, redirects initial load of product pages to a captcha page that requires you to prove that you are not a robot. 
At the time of this discovery, the only dependency that this project had was JSoup for scraping data. After some research, I found that I can leverage Selenium Webdriver to perform actions like clicking, typing, and submitting forms to solve the captcha. Additionally, Selenium Webdriver provided
the redirected URL along with the cookies which was then passed through the JSoup connect method. Although this implementation works, the problem that comes along with this implementation is the requirement of human input. A possible improvement to this project would be 
leveraging some captcha solving services or machine learning algorithms that are trained to recognize patterns in captcha images. Adding either of those technologies would remove the need to solve a captcha every interval of scraping.

## Testing
As with any code testing was done to ensure that it met the requirements. As mentioned above, for early testing, I stored a copy of the iPad product page locally in form of an html file that will be included in the source code. Leveraging that html file, I familiarized myself 
with the content of the product page so identify the best way to grab the price. Another html file that can be found in the source code is a replica of the captcha page. For similar reasons, I leveraged the captcha page to test captcha solving with selenium. When that worked as expected,
I moved onto testing by connecting to the actual page on the web. Lastly, I had to test the pop-up notification by fiddling with the log file. This can be done by changing the last scraped price, which is stored in a local text file, to be greater than the price on the actual product page.
This had to be done because I had to simulate the price dropping so that a notification is produced.
