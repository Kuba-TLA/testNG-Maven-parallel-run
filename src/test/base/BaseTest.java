package base;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;
import util.ConfigReader;
import util.Screenshot;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

public class BaseTest {
    private static final ThreadLocal<WebDriver> allDrivers = new ThreadLocal<>();
    private static final ThreadLocal<ExtentTest> allExtentTests = new ThreadLocal<>();
    private ExtentReports extentReports;

    private final String configFilePath = "src/test/data/config/config.properties";

    @BeforeMethod()
    public void setDriver(Method method) {
        initializeDriver(ConfigReader.readProperty(configFilePath, "browser"));

        ExtentTest extentTest = extentReports.createTest(getCustomTestName(method));
        allExtentTests.set(extentTest);

        logTestGroups(method);

//        driver.get(ConfigReader.readProperty(configFilePath, "url"));
    }

    @AfterMethod
    public void closeDriver(ITestResult result){
        //This will capture the result of the Test and log to extentTest report
        if(result.getStatus() == ITestResult.SUCCESS){
            getExtentTest().pass("Test PASSED");
        }else if(result.getStatus() == ITestResult.FAILURE){
            getExtentTest().fail("Test FAILED");
            getExtentTest().fail(result.getThrowable());
        }else if(result.getStatus() == ITestResult.SKIP){
            getExtentTest().skip("Test SKIPPED");
            getExtentTest().fail(result.getThrowable());
        }

        getDriver().quit();
    }

    @BeforeSuite
    public void startReporter() {
        //initiating extent report
        extentReports = new ExtentReports();
        ExtentSparkReporter spark = new ExtentSparkReporter("reports.html");
        //adding some configurations
        spark.config().setTheme(Theme.STANDARD);
        spark.config().setDocumentTitle("Orange HRM");
        spark.config().setReportName("Orange HRM Tests");
        extentReports.attachReporter(spark);
    }

    @AfterSuite
    public void closeReporter() {
        //closing the extent reporter
        extentReports.flush();
    }

    public void initializeDriver(String browser) {
        WebDriver driver = null;
        switch (browser) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver();
                break;
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver();
                break;
            case "ie":
                WebDriverManager.iedriver().setup();
                driver = new InternetExplorerDriver();
        }
        allDrivers.set(driver);
//        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
    }

    public WebDriver getDriver(){
        return allDrivers.get();
    }

    public ExtentTest getExtentTest(){
        return allExtentTests.get();
    }

    public String getCustomTestName(Method method){
        Test testClass = method.getAnnotation(Test.class);

        if(testClass.testName().length() > 0)
            return testClass.testName();
        return method.getName();
    }

    public void logTestGroups(Method method){
        Test testClass = method.getAnnotation(Test.class);

        for(String e: testClass.groups()){
            getExtentTest().assignCategory(e);
        }
    }

    public void logScreenshot(String title){
        getExtentTest().info(title,
                MediaEntityBuilder.createScreenCaptureFromBase64String(Screenshot.takeScreenshot(getDriver())).build());
    }

    public void logScreenshot(){
        getExtentTest().info(MediaEntityBuilder.createScreenCaptureFromBase64String(Screenshot.takeScreenshot(getDriver())).build());
    }
}
