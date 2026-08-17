package framework.support.elements;

import org.openqa.selenium.By;

public record Element(By locator, String elementName) {
}
