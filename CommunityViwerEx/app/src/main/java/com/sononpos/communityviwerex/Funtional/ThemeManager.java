package com.sononpos.communityviwerex.Funtional;

import java.util.ArrayList;

/**
 * Created by nnnyyy on 2017-03-09.
 */

public class ThemeManager {
    // Black Theme
    private static final String ThemeBlack_BasicFontColor = "#dddddd";
    private static final String ThemeBlack_SubFontColor = "#aaaaaa";
    private static final String ThemeBlack_BgListColor = "#333333";
    private static final String ThemeBlack_BgTitleColor = "#2a2a2a";
    private static final String ThemeBlack_LeftItemEnableColor = "#eeeeee";
    private static final String ThemeBlack_LeftItemDisableColor = "#555555";

    // White Theme
    private static final String ThemeWhite_BasicFontColor = "#000000";
    private static final String ThemeWhite_SubFontColor = "#727272";
    private static final String ThemeWhite_BgListColor = "#ffffff";
    private static final String ThemeWhite_BgTitleColor = "#fcfcfc";
    private static final String ThemeWhite_LeftItemEnableColor = "#111111";
    private static final String ThemeWhite_LeftItemDisableColor = "#999999";

    private static final String ThemeSakura_BasicFontColor = "#342831";
    private static final String ThemeSakura_SubFontColor = "#735A6E";
    private static final String ThemeSakura_BgListColor = "#F3D6E6";
    private static final String ThemeSakura_BgTitleColor = "#f3e5e2";
    private static final String ThemeSakura_LeftItemEnableColor = "#111111";
    private static final String ThemeSakura_LeftItemDisableColor = "#dec7ce";

    private static final String ThemeYellow_BasicFontColor = "#111111";
    private static final String ThemeYellow_SubFontColor = "#c23838";
    private static final String ThemeYellow_BgListColor = "#fee485";
    private static final String ThemeYellow_BgTitleColor = "#fee485";
    private static final String ThemeYellow_LeftItemEnableColor = "#111111";
    private static final String ThemeYellow_LeftItemDisableColor = "#cfa7a7";

    private static final String ThemeClien_BasicFontColor = "#374273";
    private static final String ThemeClien_SubFontColor = "#999ab4";
    private static final String ThemeClien_BgListColor = "#f0f3f5";
    private static final String ThemeClien_BgTitleColor = "#f0f3f5";
    private static final String ThemeClien_LeftItemEnableColor = "#374273";
    private static final String ThemeClien_LeftItemDisableColor = "#999ab4";

    private static final int TabFont_Normal = 16;
    private static final int TabFont_Small = 12;
    private static final int TabFont_Large = 20;

    private static final int TitleFont_Normal = 16;
    private static final int TitleFont_Small = 12;
    private static final int TitleFont_Large = 20;

    private static final int SubFont_Normal = 12;
    private static final int SubFont_Small = 10;
    private static final int SubFont_Large = 16;

    private static ArrayList<ThemeColorObject> aThemes = new ArrayList<ThemeColorObject>();
    private static ArrayList<ThemeFontObject> aThemeFonts = new ArrayList<ThemeFontObject>();

    static public class ThemeColorObject {

        public ThemeColorObject() {

        }

        public String BasicFont = ThemeManager.ThemeBlack_BasicFontColor;
        public String SubFont = ThemeManager.ThemeBlack_SubFontColor;
        public String BgList = ThemeManager.ThemeBlack_BgListColor;
        public String BgTitle = ThemeManager.ThemeBlack_BgTitleColor;
        public String LeftEnable = ThemeManager.ThemeBlack_LeftItemEnableColor;
        public String LeftDisable = ThemeManager.ThemeBlack_LeftItemDisableColor;
    }

    static public class ThemeFontObject {
        public ThemeFontObject() {

        }

        public int TabFont = ThemeManager.TabFont_Normal;
        public int TitleFont = ThemeManager.TitleFont_Normal;
        public int SubFont = ThemeManager.SubFont_Normal;
    }

    public static void Init() {
        ThemeColorObject temp = new ThemeColorObject();
        temp.BasicFont = ThemeBlack_BasicFontColor;
        temp.SubFont = ThemeBlack_SubFontColor;
        temp.BgList = ThemeBlack_BgListColor;
        temp.BgTitle = ThemeBlack_BgTitleColor;
        temp.LeftEnable = ThemeBlack_LeftItemEnableColor;
        temp.LeftDisable = ThemeBlack_LeftItemDisableColor;
        aThemes.add(temp);

        temp = new ThemeColorObject();
        temp.BasicFont = ThemeWhite_BasicFontColor;
        temp.SubFont = ThemeWhite_SubFontColor;
        temp.BgList = ThemeWhite_BgListColor;
        temp.BgTitle = ThemeWhite_BgTitleColor;
        temp.LeftEnable = ThemeWhite_LeftItemEnableColor;
        temp.LeftDisable = ThemeWhite_LeftItemDisableColor;
        aThemes.add(temp);

        temp = new ThemeColorObject();
        temp.BasicFont = ThemeSakura_BasicFontColor;
        temp.SubFont = ThemeSakura_SubFontColor;
        temp.BgList = ThemeSakura_BgListColor;
        temp.BgTitle = ThemeSakura_BgTitleColor;
        temp.LeftEnable = ThemeSakura_LeftItemEnableColor;
        temp.LeftDisable = ThemeSakura_LeftItemDisableColor;
        aThemes.add(temp);

        temp = new ThemeColorObject();
        temp.BasicFont = ThemeYellow_BasicFontColor;
        temp.SubFont = ThemeYellow_SubFontColor;
        temp.BgList = ThemeYellow_BgListColor;
        temp.BgTitle = ThemeYellow_BgTitleColor;
        temp.LeftEnable = ThemeYellow_LeftItemEnableColor;
        temp.LeftDisable = ThemeYellow_LeftItemDisableColor;
        aThemes.add(temp);

        temp = new ThemeColorObject();
        temp.BasicFont = ThemeClien_BasicFontColor;
        temp.SubFont = ThemeClien_SubFontColor;
        temp.BgList = ThemeClien_BgListColor;
        temp.BgTitle = ThemeClien_BgTitleColor;
        temp.LeftEnable = ThemeClien_LeftItemEnableColor;
        temp.LeftDisable = ThemeClien_LeftItemDisableColor;
        aThemes.add(temp);

        ThemeFontObject tempFont = new ThemeFontObject();
        tempFont.TitleFont = TitleFont_Small;
        tempFont.SubFont = SubFont_Small;
        tempFont.TabFont = TabFont_Small;
        aThemeFonts.add(tempFont);

        tempFont = new ThemeFontObject();
        tempFont.TitleFont = TitleFont_Normal;
        tempFont.SubFont = SubFont_Normal;
        tempFont.TabFont = TabFont_Normal;
        aThemeFonts.add(tempFont);

        tempFont = new ThemeFontObject();
        tempFont.TitleFont = TitleFont_Large;
        tempFont.SubFont = SubFont_Large;
        tempFont.TabFont = TabFont_Large;
        aThemeFonts.add(tempFont);
    }

    private static ThemeColorObject themeColor = new ThemeColorObject();
    private static ThemeFontObject themeFont = new ThemeFontObject();

    public static ThemeColorObject GetTheme() { return themeColor; }
    public static ThemeColorObject GetTheme(int themeNum ) { return aThemes.get(themeNum); }

    public static ThemeFontObject GetFont() { return themeFont; }
    public static ThemeFontObject GetFont(int themeNum) { return aThemeFonts.get(themeNum); }

    public static String GetName(int themeNum) {
        switch(themeNum) {
            case 0: return "검정색 테마";
            case 1: return "흰색 테마";
            case 2: return "사쿠라 테마";
            case 3: return "병아리 테마";
            case 4: return "Clien 테마";
            default: return "검정색 테마";
        }
    }

    public static String GetFontName(int themeNum) {
        switch(themeNum) {
            case 0: return "작게";
            case 1: return "보통";
            case 2: return "크게";
            default: return "보통";
        }
    }

    public static void SetTheme(int themeNum ) {
        if(aThemes.size() <= themeNum) {
            return;
        }

        themeColor = aThemes.get(themeNum);
    }

    public static void SetThemeFont(int themeNum ) {
        if(aThemeFonts.size() <= themeNum) {
            return;
        }

        themeFont = aThemeFonts.get(themeNum);
    }
}
