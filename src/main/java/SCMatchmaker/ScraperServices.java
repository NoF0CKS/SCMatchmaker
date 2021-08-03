package SCMatchmaker;

import SCMatchmaker.Models.ProfileClass;
import discord4j.core.object.entity.Message;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class ScraperServices {
    //scrapes the battle royal leaderboard
    public static ProfileClass scrapeBattleRoyal(Message message, String handle, WebDriver driver, WebDriverWait wait){
        //this is going to be our return profileclass
        ProfileClass player = new ProfileClass();

        try{
            //navigate to a website
            driver.navigate().to("https://robertsspaceindustries.com/community/leaderboards/all?mode=BR");

            //save the xPath of the season dropdown menu
            String seasonButton = "//*[@id=\"leaderboard-filters\"]/div[3]/div[2]/fieldset[3]/a";

            //wait until the dropdown menu is loaded on the page before clicking it
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath(seasonButton)));

            //click the dropdown menu
            driver.findElement(By.xpath(seasonButton)).click();

            //get the xPath value of 3.11
            String onlyWorkingSeason = "//*[@id=\"leaderboard-filters\"]/div[3]/div[2]/fieldset[3]/a/ul/li[3]";

            //click 3.11
            driver.findElement(By.xpath(onlyWorkingSeason)).click();

            //get the Find Player textbox
            WebElement findPlayer = driver.findElement(By.xpath("//*[@id=\"leaderboard-filters\"]/div[3]/div[2]/fieldset[1]/div/div/input"));

            //look for the user
            findPlayer.sendKeys(handle);

            //save the xPath of the player popout
            String userPopOut = "//*[@id=\"leaderboard-filters\"]/div[3]/div[2]/fieldset[1]/div/div/div";

            //wait until the user is visible as it sometimes takes a moment
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath(userPopOut)));

            //click the user and wait for it to load
            driver.findElement(By.xpath(userPopOut)).click();

            try{
                //wait for the user panel to pop up
                wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#leaderboard-data > li.row.clearfix.trans-02s.animDone.active")));
            }catch(Exception e){
                Bot.sendMessage(message, ":small_orange_diamond: (2/5) Battle Royal Leaderboard: Player not found.");
                player.setMessage(message);
                player.setHandle(handle);
                player.setDiscordID(message.getUserData().id().toString());
                player.setBR_ELO(0.00);
                player.setBR_Playtime(0);
                player.setBR_DamageDealt(0);
                player.setBR_DamageTaken(0);
                player.setBR_Matches(0);
                player.setBR_Wins(0);
                player.setBR_Losses(0);
                player.setBR_Kills(0);
                player.setBR_Deaths(0);
                return player;
            }

            //populate the ProfileClass player
            player.setMessage(message);
            player.setHandle(handle);
            player.setDiscordID(message.getUserData().id().toString());
            player.setBR_ELO(EloManagerServices.calculateInitialElo()); //sets player's starting Elo to the median of all Elo scores.
            //going to convert the play time into minutes, first.
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:SSS");
            String timeString = driver.findElement(By.cssSelector("#leaderboard-data > li.row.clearfix.trans-02s.animDone.active > div:nth-child(8)")).getText();
            String[] hourMin = timeString.split(":");
            player.setBR_Playtime((Integer.parseInt((hourMin[0]))* 60) + (Integer.parseInt(hourMin[1]))); //+;
            //end playtime set
            player.setBR_DamageDealt(Long.parseLong(driver.findElement(By.cssSelector("#leaderboard-data > li.row.clearfix.trans-02s.animDone.active > div.stats-wrap.trans-05s.dogfighting.account.open > div:nth-child(2) > div.stats > li:nth-child(2)")).getText().replaceAll("DAMAGE DEALT:", "")));
            player.setBR_DamageTaken(Long.parseLong(driver.findElement(By.cssSelector("#leaderboard-data > li.row.clearfix.trans-02s.animDone.active > div.stats-wrap.trans-05s.dogfighting.account.open > div:nth-child(2) > div.stats > li:nth-child(1)")).getText().replaceAll("DAMAGE TAKEN:", "")));
            player.setBR_Matches(Integer.parseInt(driver.findElement(By.cssSelector("#leaderboard-data > li.row.clearfix.trans-02s.animDone.active > div.stats-wrap.trans-05s.dogfighting.account.open > div:nth-child(1) > div.stats > li:nth-child(1)")).getText().replaceAll("MATCHES:", "")));
            player.setBR_Wins(Integer.parseInt(driver.findElement(By.cssSelector("#leaderboard-data > li.row.clearfix.trans-02s.animDone.active > div.stats-wrap.trans-05s.dogfighting.account.open > div:nth-child(1) > div.stats > li:nth-child(3)")).getText().replaceAll("WINS:", "")));
            player.setBR_Losses(Integer.parseInt(driver.findElement(By.cssSelector("#leaderboard-data > li.row.clearfix.trans-02s.animDone.active > div.stats-wrap.trans-05s.dogfighting.account.open > div:nth-child(1) > div.stats > li:nth-child(4)")).getText().replaceAll("LOSSES:", "")));
            player.setBR_Kills(Integer.parseInt(driver.findElement(By.cssSelector("#leaderboard-data > li.row.clearfix.trans-02s.animDone.active > div.stats-wrap.trans-05s.dogfighting.account.open > div:nth-child(2) > div.stats > li:nth-child(3)")).getText().replaceAll("KILLS:", "")));
            player.setBR_Deaths(Integer.parseInt(driver.findElement(By.cssSelector("#leaderboard-data > li.row.clearfix.trans-02s.animDone.active > div.stats-wrap.trans-05s.dogfighting.account.open > div:nth-child(2) > div.stats > li:nth-child(4)")).getText().replaceAll("DEATHS:", "")));

            //Old code, but worth keeping
            /*
            String rating = driver.findElement(By.cssSelector("#leaderboard-data > li.row.clearfix.trans-02s.animDone.active > div:nth-child(3)")).getText();
            String scoreminute = driver.findElement(By.cssSelector("#leaderboard-data > li.row.clearfix.trans-02s.animDone.active > div:nth-child(4)")).getText();
            String score = driver.findElement(By.cssSelector("#leaderboard-data > li.row.clearfix.trans-02s.animDone.active > div:nth-child(5)")).getText();
            String damageratio = driver.findElement(By.cssSelector("#leaderboard-data > li.row.clearfix.trans-02s.animDone.active > div:nth-child(6)")).getText();
            String killdeath = driver.findElement(By.cssSelector("#leaderboard-data > li.row.clearfix.trans-02s.animDone.active > div:nth-child(7)")).getText();
            String playtime = String.valueOf((Integer.parseInt(hourMin[0])*60)+Integer.parseInt(hourMin[1]));
            //below are inside the expanded box. You have to remove some text before they can be stored.
            String matches = driver.findElement(By.cssSelector("#leaderboard-data > li.row.clearfix.trans-02s.animDone.active > div.stats-wrap.trans-05s.dogfighting.account.open > div:nth-child(1) > div.stats > li:nth-child(1)")).getText().replaceAll("MATCHES:", "");
            String avgMatchTimeString = driver.findElement(By.cssSelector("#leaderboard-data > li.row.clearfix.trans-02s.animDone.active > div.stats-wrap.trans-05s.dogfighting.account.open > div:nth-child(1) > div.stats > li:nth-child(2)")).getText().replaceAll("AVG MATCH:", "");
            hourMin = avgMatchTimeString.split(":");
            String avgmatch = String.valueOf((Integer.parseInt(hourMin[0])*60)+Integer.parseInt(hourMin[1]));
            String wins = driver.findElement(By.cssSelector("#leaderboard-data > li.row.clearfix.trans-02s.animDone.active > div.stats-wrap.trans-05s.dogfighting.account.open > div:nth-child(1) > div.stats > li:nth-child(3)")).getText().replaceAll("WINS:", "");
            String losses = driver.findElement(By.cssSelector("#leaderboard-data > li.row.clearfix.trans-02s.animDone.active > div.stats-wrap.trans-05s.dogfighting.account.open > div:nth-child(1) > div.stats > li:nth-child(4)")).getText().replaceAll("LOSSES:", "");
            String winloss = driver.findElement(By.cssSelector("#leaderboard-data > li.row.clearfix.trans-02s.animDone.active > div.stats-wrap.trans-05s.dogfighting.account.open > div:nth-child(1) > div.stats > li:nth-child(5)")).getText().replaceAll("WIN/LOSS:", "");
            String damagetaken = driver.findElement(By.cssSelector("#leaderboard-data > li.row.clearfix.trans-02s.animDone.active > div.stats-wrap.trans-05s.dogfighting.account.open > div:nth-child(2) > div.stats > li:nth-child(1)")).getText().replaceAll("DAMAGE TAKEN:", "");
            String damagedealt = driver.findElement(By.cssSelector("#leaderboard-data > li.row.clearfix.trans-02s.animDone.active > div.stats-wrap.trans-05s.dogfighting.account.open > div:nth-child(2) > div.stats > li:nth-child(2)")).getText().replaceAll("DAMAGE DEALT:", "");
            String kills = driver.findElement(By.cssSelector("#leaderboard-data > li.row.clearfix.trans-02s.animDone.active > div.stats-wrap.trans-05s.dogfighting.account.open > div:nth-child(2) > div.stats > li:nth-child(3)")).getText().replaceAll("KILLS:", "");
            String deaths = driver.findElement(By.cssSelector("#leaderboard-data > li.row.clearfix.trans-02s.animDone.active > div.stats-wrap.trans-05s.dogfighting.account.open > div:nth-child(2) > div.stats > li:nth-child(4)")).getText().replaceAll("DEATHS:", "");
             */

            //update user
            Bot.sendMessage(message, ":small_blue_diamond: (2/5) Battle Royal Leaderboard Verified...");

            //return the results as an array. There are 16 results total
            return player;
        }catch(Exception e){
            Bot.sendMessage(message, ":small_red_triangle: Battle Royal Leaderboard Error: " + e);
            player.setMessage(message);
            player.setHandle(handle);
            player.setDiscordID(message.getUserData().id().toString());
            player.setBR_ELO(0.00);
            player.setBR_Playtime(0);
            player.setBR_DamageDealt(0);
            player.setBR_DamageTaken(0);
            player.setBR_Matches(0);
            player.setBR_Wins(0);
            player.setBR_Losses(0);
            player.setBR_Kills(0);
            player.setBR_Deaths(0);
            return player;
        }
    }

    //check profile page
    public static List<String> checkPageNewUser(String url, Message message, WebDriver driver, WebDriverWait wait) {

        //place the results in an array list
        List<String> resultsArray = new ArrayList<>();

        //put it all in a try-catch
        try{
            //validating the URL
            boolean validURL;
            if(url.startsWith("http")){
                validURL = true;
            }else{
                validURL = false;
            }

            //navigate to a website
            driver.navigate().to(url);

            //checking if there's a 404 error and if the verified text is in the Bio
            Boolean isRealUser = driver.findElements(By.cssSelector("#public-profile > div.profile-content.overview-content.clearfix > p > span")).size() > 0;

            //does the person's discordID appear on page?
            //Boolean hasNumber = driver.getPageSource().toLowerCase(Locale.ROOT).contains(message.getId().toString());
            Boolean hasNumber;
            String userDiscordId = message.getUserData().id().toString();
            String bioSection = driver.findElement(By.cssSelector("#public-profile > div.profile-content.overview-content.clearfix > div.right-col > div > div > div")).getText();
            if (bioSection.contains(userDiscordId)){
                hasNumber = true;
            }else{
                hasNumber = false;
            }
            //hasNumber = (driver.findElement(By.cssSelector("#public-profile > div.profile-content.overview-content.clearfix > div.right-col > div > div > div")).getText()==message.getUserData().id().toString());

            if(validURL==true && isRealUser==true && hasNumber==true){
                //get their UEE record number
                String ueeCitRecord = driver.findElement(By.cssSelector("#public-profile > div.profile-content.overview-content.clearfix > p > strong")).getText().replaceAll("#", "");; //it literally pulls the hashtag with it, so you gotta dump it.

                //check if the citrecord exists in the database. If it does, return an empty results array
                if(SQLServices.existsUEENumber(ueeCitRecord, message) == true){
                    return resultsArray;
                }else{//if the citizenrecord doesn't exist, finish scraping and return the elements in an array
                    //get their handle
                    String handle = driver.findElement(By.cssSelector("#public-profile > div.profile-content.overview-content.clearfix > div.box-content.profile-wrapper.clearfix > div > div.profile.left-col > div > div.info > p:nth-child(2) > strong")).getText();

                    //get their Org SID
                    String orgSID = driver.findElement(By.cssSelector("#public-profile > div.profile-content.overview-content.clearfix > div.box-content.profile-wrapper.clearfix > div > div.main-org.right-col.visibility-V > div > div.info > p:nth-child(2) > strong")).getText();

                    //update user and close driver
                    Bot.sendMessage(message, ":small_blue_diamond: (1/5) Profile Page verified...");

                    //place the results in an array list
                    resultsArray.add(handle); //index: 0
                    resultsArray.add(ueeCitRecord); //index: 1
                    resultsArray.add(orgSID); //index: 2

                    return resultsArray;
                }
            }else{
                Bot.sendMessage(message, ":small_red_triangle: There was an error, please check your URL and ensure the following text " +
                        "is located inside your StarCitizen Bio:");
                Bot.sendMessage(message, message.getUserData().id().toString());
                return resultsArray;
            }
        }catch(Exception e){
            Bot.sendMessage(message, ":small_red_triangle: The URL was invalid, please double-check and try again. It should look like the following:" +
                    "\n`` !newuser https://robertsspaceindustries.com/citizens/YOUR_HANDLE_HERE ``");
            return resultsArray;
        }
    }

    //check profile page
    public static List<String> checkPageUpdateMe(String url, Message message, WebDriver driver, WebDriverWait wait) {

        //place the results in an array list
        List<String> resultsArray = new ArrayList<>();

        //put it all in a try-catch
        try{
            //validating the URL
            boolean validURL;
            if(url.startsWith("http")){
                validURL = true;
            }else{
                validURL = false;
            }

            //navigate to a website
            driver.navigate().to(url);

            //checking if there's a 404 error and if the verified text is in the Bio
            Boolean isRealUser = driver.findElements(By.cssSelector("#public-profile > div.profile-content.overview-content.clearfix > p > span")).size() > 0;

            //does the person's discordID appear on page?
            //Boolean hasNumber = driver.getPageSource().toLowerCase(Locale.ROOT).contains(message.getId().toString());
            Boolean hasNumber;
            String userDiscordId = message.getUserData().id().toString();
            String bioSection = driver.findElement(By.cssSelector("#public-profile > div.profile-content.overview-content.clearfix > div.right-col > div > div > div")).getText();
            if (bioSection.contains(userDiscordId)){
                hasNumber = true;
            }else{
                hasNumber = false;
            }
            //hasNumber = (driver.findElement(By.cssSelector("#public-profile > div.profile-content.overview-content.clearfix > div.right-col > div > div > div")).getText()==message.getUserData().id().toString());

            if(validURL==true && isRealUser==true && hasNumber==true){
                //get their UEE record number
                String ueeCitRecord = driver.findElement(By.cssSelector("#public-profile > div.profile-content.overview-content.clearfix > p > strong")).getText().replaceAll("#", "");; //it literally pulls the hashtag with it, so you gotta dump it.

                //get their handle
                String handle = driver.findElement(By.cssSelector("#public-profile > div.profile-content.overview-content.clearfix > div.box-content.profile-wrapper.clearfix > div > div.profile.left-col > div > div.info > p:nth-child(2) > strong")).getText();

                //get their Org SID
                String orgSID = driver.findElement(By.cssSelector("#public-profile > div.profile-content.overview-content.clearfix > div.box-content.profile-wrapper.clearfix > div > div.main-org.right-col.visibility-V > div > div.info > p:nth-child(2) > strong")).getText();

                //update user and close driver
                Bot.sendMessage(message, ":small_blue_diamond: (1/5) Profile Page verified...");

                //place the results in an array list
                resultsArray.add(handle); //index: 0
                resultsArray.add(ueeCitRecord); //index: 1
                resultsArray.add(orgSID); //index: 2

                return resultsArray;
            }else{
                Bot.sendMessage(message, ":small_red_triangle: There was an error, please check your URL and ensure the following text " +
                        "is located inside your StarCitizen Bio:");
                Bot.sendMessage(message, message.getUserData().id().toString());
                return resultsArray;
            }
        }catch(Exception e){
            Bot.sendMessage(message, ":small_red_triangle: The URL was invalid, please double-check and try again. It should look like the following:" +
                    "\n`` !newuser https://robertsspaceindustries.com/citizens/YOUR_HANDLE_HERE ``");
            return resultsArray;
        }
    }


}
