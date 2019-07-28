package com.sononpos.communityviwerex;

/**
 * Created by nnnyy on 2017-10-17.
 */

public class Global {
    private static Global instance;
    private Global(){}
    public static Global obj() {
        if(instance == null) {
            synchronized (Global.class) {
                if( instance == null ) {
                    instance = new Global();
                }
            }
        }

        return instance;
    }

    private TabItemManager timan;
    public boolean isKor = true;
    public TabItemManager getTabItemManager() {
        if(timan == null) {
            timan = new TabItemManager();
        }
        return timan;
    }

    private ArticleListManager alman;
    public ArticleListManager getArticleListManager() {
        if(alman == null) {
            alman = new ArticleListManager();
        }
        return alman;
    }
}
