package vdq.com.main;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Formmain {

//    static WebDriver driver;

    public static void main(String[] args) throws InterruptedException {
        WebDriver driver;
        System.out.print("Nhập hashtag #");
        Scanner sc = new Scanner(System.in);
        String hashtag = sc.nextLine().trim();

        List<DataInfo> rs = new ArrayList<DataInfo>();
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();

        driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        driver.manage().window().maximize();
//salesup_erp
        driver.navigate().to("https://vi-vn.facebook.com/hashtag/"+hashtag);
        Thread.sleep(2000);

        boolean scroll = true;
        int start = 0;
        do {
            try {
                start = getPost(start, rs, driver);

                for (int i = 0; i < 100; i++) {
                    driver.findElement(By.tagName("body")).sendKeys(Keys.ARROW_DOWN);
                }
            } catch (Exception e) {
                System.out.println("không thể truy cập vào phần tử");
                scroll = false;
            }
            if (start == -1) {
                scroll = false;
            }
        } while (scroll);
        driver.quit();
        SaveDb(rs);
    }

    private static void SaveDb(List<DataInfo> lstPost) {

        System.out.print("Đang Lưu Thông Tin ");
        DAO dao = new DAO();
        Long idnew = null;
        String queryPost = "INSERT INTO Posts(author,detail, content) VALUES (?, ?, ?)";
        String queryImage = "INSERT INTO Images (id, image) values (?,?)";
        String queryCount = "SELECT COUNT(*) FROM Posts WHERE detail like CONCAT(? ,'%')";
        for (DataInfo i : lstPost) {
            if(dao.count(queryCount, i.getLinkDetail().split("\\?")[0]) > 0){
                continue;
            }
            idnew = dao.Insert(queryPost, i.getAuthor(), i.getLinkDetail(), i.getContent() == null ? "": i.getContent());
            for (String ig : i.getLinkImg()) {
                dao.Insert(queryImage, idnew, ig);
            }
            System.out.print(".");
        }
        System.out.println("Hoàn Tất");
    }

    private static int getPost(int start, List<DataInfo> lstPost, WebDriver driver) throws InterruptedException {
        By divSelector = By.xpath("//*[@role=\"article\"]");
        List<WebElement> lstDivElements = driver.findElements(divSelector);
        int end = lstDivElements.size() > (5 + start) ? (5 + start) : lstDivElements.size() ;

        for (int i = start; i < end; i++) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", lstDivElements.get(i));
            System.out.println("Đang Lấy Thông Tin Bài Đăng " + (i + 1) + " ...");
            lstPost.add(getInfo(lstDivElements.get(i),  driver));
        }
        if (lstDivElements.size() == end) {
            return -1;
        }
        return end;
    }

    private static DataInfo getInfo(WebElement Element, WebDriver driver) {
        Actions action = new Actions(driver);
        DataInfo data = new DataInfo();
        String a = Element.getAttribute("aria-describedby");
        String[] arrayA = a.split(" ");

        /*author*/
        String author = Element.getAttribute("aria-labelledby");
        WebElement actor;
        actor = driver.findElement(By.xpath("(//*[@id=\"" + author + "\"]//child::span)[1]"));
//        System.out.println("authorName: " + actor.getText());
        data.setAuthor(actor.getText());

        /*link detail*/
        WebElement link = driver.findElement(By.xpath("(//*[@id=\"" + arrayA[0] + "\"]//child::a)[1]"));
        action.moveToElement(link).perform();
        String hrelid = link.getAttribute("href");
        data.setLinkDetail(hrelid);
//        System.out.println("Link: " + hrelid);

        /*content*/
        WebElement message = null;
        if (!driver.findElements(By.xpath("//*[@id=\"" + arrayA[1] + "\"]")).isEmpty()) {
            if (!driver.findElements(By.xpath("(//*[@id=\"" + arrayA[1] + "\"]//child::div[@role])[1]")).isEmpty()) {
                driver.findElement(By.xpath("(//*[@id=\"" + arrayA[1] + "\"]//child::div[@role])[1]")).click();
            }
            message = driver.findElement(By.xpath("//*[@id=\"" + arrayA[1] + "\"]"));
//            System.out.println(message.getText());
            data.setContent(message.getText());
        }
        /*image url*/
        List<WebElement> urlImage = null;
        if (!driver.findElements(By.xpath("//*[@id=\"" + arrayA[2] + "\"]")).isEmpty()) {
            urlImage = driver.findElements(By.xpath("//*[@id=\"" + arrayA[2] + "\"]//child::a"));
//            System.out.println("Link Image: ");
            for (WebElement img : urlImage) {
                data.addImage(img.getAttribute("href"));
//                System.out.println(img.getAttribute("href"));
            }
        }
        return data;
    }
}
