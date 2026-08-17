package application.demo_web_shop.pages;

import application.BasePage;
import application.demo_web_shop.components.Header;
import org.openqa.selenium.WebDriver;

public class LandingPage extends BasePage {


        private final Header header;

        public LandingPage(WebDriver driver) {
            super(driver);
            this.header = new Header(driver);
        }

        public Header header() {
            return header;
        }

}
