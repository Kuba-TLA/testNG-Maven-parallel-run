package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.LoginPage;

public class LoginTest extends BaseTest {
    LoginPage page;

    @BeforeMethod
    public void setUp(){
        page = new LoginPage(getDriver());
    }

    @Test(testName = "Google Test", groups = {"regression"})
    public void test1(){
        getDriver().get("https://google.com");
        getExtentTest().info("Opening google.com");
        logScreenshot();
        Assert.assertEquals(getDriver().getTitle(), "Google");
        page.sleep(2000);
    }

    @Test
    public void amazonTest(){
        getDriver().get("https://amazon.com");
        getExtentTest().pass("Opened amazon web page");
        logScreenshot("Home Page");
        Assert.assertTrue(getDriver().getTitle().toLowerCase().contains("amazon"));
        page.sleep(2000);
    }

    @Test(testName = "Ebay Test")
    public void test3(){
        getDriver().get("https://ebay.com");
        getExtentTest().pass("Opened Ebay web page");
        logScreenshot();
        Assert.assertTrue(getDriver().getTitle().toLowerCase().contains("ebay"));
        page.sleep(2000);
    }

}
