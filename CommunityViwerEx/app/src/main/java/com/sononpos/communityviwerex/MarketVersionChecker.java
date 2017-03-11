package com.sononpos.communityviwerex;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by nnnyy on 2017-03-10.
 */
public class MarketVersionChecker {

    public static String getMarketVersion(String packageName) {
        try {
            Document doc = Jsoup.connect("https://play.google.com/store/apps/details?id=" + packageName).get();
            Elements Version = doc.select(".content");

            for(Element mElement : Version) {
                if (mElement.attr("itemprop").equals("softwareVersion")) {
                    return mElement.text().trim();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static int getVersionState(String sDeviceVer) {
        String sMarketVer = getMarketVersion("com.sononpos.communityviwerex");
        if(sMarketVer == null ) return -1;

        ArrayList<String> aMarketVers = new ArrayList<>(Arrays.asList(sMarketVer.split("\\.")));
        ArrayList<String> aDeviceVers = new ArrayList<>(Arrays.asList(sDeviceVer.split("\\.")));
        if( Integer.parseInt(aMarketVers.get(0)) > Integer.parseInt(aDeviceVers.get(0)) ) {
            //  초 메이저 업데이트
            return 1;
        }

        if( Integer.parseInt(aMarketVers.get(1)) > Integer.parseInt(aDeviceVers.get(1)) ) {
            //  메이저 업데이트
            return 2;
        }

        if( Integer.parseInt(aMarketVers.get(2)) > Integer.parseInt(aDeviceVers.get(2)) ) {
            //  메이저 업데이트
            return 3;
        }

        return 0;
    }
}
