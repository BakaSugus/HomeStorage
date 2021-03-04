package cn.j.netstorage.tool;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;

import com.rometools.rome.io.XmlReader;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class RssParser {

    public static List<RssParser.Seed> parse(String url) {
        List<RssParser.Seed> seeds = new ArrayList<>();
        try (XmlReader reader = new XmlReader(new URL(url))) {
            SyndFeed feed = new SyndFeedInput().build(reader);
            for (SyndEntry entry : feed.getEntries()) {
                seeds.add(new Seed(entry.getTitleEx().getValue(),entry.getEnclosures().get(0).getUrl(),entry.getPublishedDate().toLocaleString()));
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FeedException e) {
            e.printStackTrace();
        }
        return seeds;
    }

    public static class Seed {
        private String name;
        private String url;
        private String date;

        public Seed(String name, String url, String date) {
            this.name = name;
            this.url = url;
            this.date = date;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        @Override
        public String toString() {
            return "Seed{" +
                    "name='" + name + '\'' +
                    ", url='" + url + '\'' +
                    ", date='" + date + '\'' +
                    '}';
        }
    }

}
