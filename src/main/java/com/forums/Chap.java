package forums;



import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class Chap {

    private String url;
    private String chap_number;

    public Chap(String url) {
        this.url = url;
        chap_number = chapNumber(url);
    }

    public String getUrl() {
        return url;
    }

    public String getChap_number() {
        return chap_number;
    }

    private String chapNumber(String url){
        int idx = 0;
        for (int i = 0; i < url.length(); i++) {
            if(Character.isDigit(url.charAt(i))){
                idx = i;
                break;
            }
        }
        return url.substring(idx,url.length()-1);
    }

    private ArrayList<Chap> getAllChapInPage(String urls) throws IOException {
        ArrayList<Chap> list_chap = new ArrayList<>();
        Document document = Jsoup.connect(urls).get();
        org.jsoup.select.Elements elms = document.getElementsByClass("row");
        for (int i = 0; i < elms.size(); i++) {
            Elements elm_row = elms.get(i).getElementsByTag("a");
            for (int j = 0; j < elm_row.size(); j++) {
                String link_chap = elm_row.first().absUrl("href");
                list_chap.add(new Chap(link_chap));
            }
        }
        return list_chap;
    }

    private ArrayList<String> listImgOnPage(String pageURL) throws IOException {
        Document document = Jsoup.connect(pageURL).get();
        ArrayList<String> list_img = new ArrayList<>();
        Elements elms = document.getElementsByClass("grab-content-chap");
        Elements e = document.getElementsByTag("img");
        for (int i = 0; i < e.size(); i++) {
            String url = e.get(i).absUrl("src");
            if (url.equals("")) {
                continue;
            }
            list_img.add(url);
        }
        return list_img;
    }
}
