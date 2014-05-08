/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kickupper;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author aleyase2-admin
 */
public class KSDocument {

    String id;
    String title;
    String shortDescription;
    String descripton;
    int funded;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public int getFunded() {
        return funded;
    }

    public void setFunded(int funded) {
        this.funded = funded;
    }

    public String getDescripton() {
        return descripton;
    }

    public void setDescripton(String descripton) {
        this.descripton = descripton;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public static KSDocument parse(String filepath, String id) {
        try {
            File input = new File(filepath);
            Document doc = Jsoup.parse(input, "UTF-8");
            KSDocument ksdoc = new KSDocument();
            ksdoc.setId(id);
            ksdoc.setTitle(doc.select("#title a").text());
            ksdoc.setShortDescription(doc.select(".short-blurb").text());
            ksdoc.setDescripton(doc.select(".full-description").text());
            final String success_fund = doc.select("body").attr("class");
//            System.out.println(success_fund);
            if (success_fund.contains("Project-failed")) {
                ksdoc.setFunded(0);
            } else {
                ksdoc.setFunded(1);
            }
            return ksdoc;
        } catch (IOException ex) {
            Logger.getLogger(OfflineJobs.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public String toString() {
        return id + "\n"
                + title + "\n"
                + shortDescription + "\n"
                + descripton + "\n"
                + funded + "\n";
    }

}
