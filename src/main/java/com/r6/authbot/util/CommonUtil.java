package com.r6.authbot.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

import javax.imageio.ImageIO;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.r6.authbot.domain.VerifiedUser;

import io.github.bonigarcia.wdm.managers.ChromeDriverManager;

/**
 * Html to image 클래스
 * <hr/>
 * 
 * @author 세민
 * @version 1.0
 * @since 2024.01.17
 */
public class CommonUtil {

    public static InputStream createLeaderboardImg(String tbody) {
        String htmlStyle = "<style>@import url(https://fonts.googleapis.com/css?family=Rubik:300,400,500);*{box-sizing:border-box;margin:0;padding:0}body{border-top:15px solid #dc0000;background:#15151e;color:#fff;font-family:Rubik,sans-serif;display:flex;flex-direction:column;align-items:center}table{padding-left:40px;margin:0 1rem;position:relative;width:calc(90vw - 2rem);max-width:800px;border-spacing:0 1rem}table:before{padding-bottom:20px;position:absolute;content:'';right:calc(100% + 1rem);top:0;height:100%;width:1.5rem;border-radius:5px;border:1px solid #38383f;background:repeating-linear-gradient(-45deg,#15151e 0,#15151e 4px,#38383f 4px,#38383f 8px)}table tr>*{text-align:center;padding:.5rem}table tr>:nth-child(2){text-align:left;display:flex;justify-content: center;align-items:center}table tr>:nth-child(2) img{margin-right:.75rem}table th{font-weight:300;letter-spacing:.04rem;font-size:1.2rem;color:#eee;text-transform:uppercase}table td.position{font-weight:500}table td.driver{padding-left:1rem;font-size:1.5rem;letter-spacing:.05rem}table td.driver strong{text-transform:uppercase;font-weight:500}table td.gap span{background:#38383f;border-radius:30px;padding:.5rem .75rem;font-size:1.2rem;text-transform:uppercase}@media (max-width:500px){table tr>:nth-child(3){display:none}}</style>";
        String baseHtml = String.format(
                "<html><head>%s</head><body><table><tr><th>Rank</th><th style='margin-left: 10px;'>User</th><th>MMR</th></tr><tbody>%s</tbody></table></body></html>",
                htmlStyle, tbody);

        ChromeDriver driver = null;
        try {
            ChromeDriverManager.chromedriver().setup();

            ChromeOptions options = new ChromeOptions();
            options.addArguments("--single-process");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--headless");
            options.addArguments("--start-fullscreen");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-extensions");
            options.addArguments("--disable-gpu");
            options.addArguments("window-size=800x700");

            driver = new ChromeDriver(options);

            driver.get("data:text/html;base64,"
                    + Base64.getEncoder().encodeToString(baseHtml.getBytes(StandardCharsets.UTF_8)));

            WebElement body = driver.findElement(By.tagName("body"));
            WebElement table = driver.findElement(By.tagName("table"));

            File screenShot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            BufferedImage fullImg = ImageIO.read(screenShot);

            Point point = body.getLocation();

            Integer bodyWidth = body.getSize().getWidth();
            Integer tableHeight = table.getSize().getHeight();

            BufferedImage bodyScreenshot = fullImg.getSubimage(point.getX(), point.getY(), bodyWidth, tableHeight + 30);
            ImageIO.write(bodyScreenshot, "png", screenShot);

            driver.close();
            return new FileInputStream(screenShot);
        } catch (Exception ex) {
            ex.printStackTrace();
            driver.close();
            return null;
        }
    }

    public static String createTbodyElement(VerifiedUser userInfo, Integer rank) {
        String rankIconStyle = "";
        if (rank <= 3) {
            ArrayList<String> styleList = new ArrayList<>(Arrays.asList("filter: hue-rotate(300deg) sepia(1);",
                    "filter: grayscale(100%) sepia(20%);", "filter: hue-rotate(30deg) sepia(50%);"));
            rankIconStyle = styleList.get(rank - 1);
        }

        String element = "<tr class='driver'>\r\n" + //
                "                <td>\r\n" + //
                "                    <img style='" + rankIconStyle
                + "' width='70' src='https://trackercdn.com/cdn/r6.tracker.network/ranks/champion-ranks/" + rank.toString() + ".png?v=3'>\r\n" + //
                "                </td>\r\n" + //
                "                <td class='driver'>\r\n" + //
                "                    <img style='border-radius: 50%;' width='70' src='https://ubisoft-avatars.akamaized.net/"
                + userInfo.getUbisoftUid() + "/default_256_256.png'>\r\n" + //
                "                    <span>\r\n" + //
                userInfo.getUbisoftUname() + "\r\n" +
                "                    </span>\r\n" + //
                "                </td>\r\n" + //
                "                <td class='gap'><span>" + userInfo.getCurrentMMR().toString() + " MMR</span></td>\r\n"
                + //
                "            </tr>";

        return element;
    }
}
