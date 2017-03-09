package com.sononpos.communityviwerex.Funtional;

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
    private static final String ThemeWhite_SubFontColor = "#111111";
    private static final String ThemeWhite_BgListColor = "#ffffff";
    private static final String ThemeWhite_BgTitleColor = "#fcfcfc";
    private static final String ThemeWhite_LeftItemEnableColor = "#111111";
    private static final String ThemeWhite_LeftItemDisableColor = "#999999";

    private static final String ThemeSakura_BasicFontColor = "#f25e8d";
    private static final String ThemeSakura_SubFontColor = "#fb9bb9";
    private static final String ThemeSakura_BgListColor = "#f3e5e2";
    private static final String ThemeSakura_BgTitleColor = "#f3e5e2";
    private static final String ThemeSakura_LeftItemEnableColor = "#f25e8d";
    private static final String ThemeSakura_LeftItemDisableColor = "#dec7ce";

    private static final String ThemeYellow_BasicFontColor = "#ff4a4a";
    private static final String ThemeYellow_SubFontColor = "#c23838";
    private static final String ThemeYellow_BgListColor = "#fee485";
    private static final String ThemeYellow_BgTitleColor = "#fee485";
    private static final String ThemeYellow_LeftItemEnableColor = "#ff4a4a";
    private static final String ThemeYellow_LeftItemDisableColor = "#cfa7a7";

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

    private static final ThemeColorObject themeColor = new ThemeColorObject();

    public static String GetBasicFontColor() { return themeColor.BasicFont; }
    public static String GetSubFontColor() { return themeColor.SubFont; }
    public static String GetBgListColor() { return themeColor.BgList; }
    public static String GetBgTitleColor() { return themeColor.BgTitle; }

    public static ThemeColorObject GetTheme() { return themeColor; }

    public static String GetName(int themeNum) {
        switch(themeNum) {
            case 0: return "검정색 테마";
            case 1: return "흰색 테마";
            case 2: return "사쿠라 테마";
            case 3: return "병아리 테마";
            default: return "검정색 테마";
        }
    }

    public static void SetTheme(int themeNum ) {
        switch(themeNum) {
            case 0:
                themeColor.BasicFont = ThemeBlack_BasicFontColor;
                themeColor.SubFont = ThemeBlack_SubFontColor;
                themeColor.BgList = ThemeBlack_BgListColor;
                themeColor.BgTitle = ThemeBlack_BgTitleColor;
                themeColor.LeftEnable = ThemeBlack_LeftItemEnableColor;
                themeColor.LeftDisable = ThemeBlack_LeftItemDisableColor;
                break;

            case 1:
                themeColor.BasicFont = ThemeWhite_BasicFontColor;
                themeColor.SubFont = ThemeWhite_SubFontColor;
                themeColor.BgList = ThemeWhite_BgListColor;
                themeColor.BgTitle = ThemeWhite_BgTitleColor;
                themeColor.LeftEnable = ThemeWhite_LeftItemEnableColor;
                themeColor.LeftDisable = ThemeWhite_LeftItemDisableColor;
                break;

            case 2:
                themeColor.BasicFont = ThemeSakura_BasicFontColor;
                themeColor.SubFont = ThemeSakura_SubFontColor;
                themeColor.BgList = ThemeSakura_BgListColor;
                themeColor.BgTitle = ThemeSakura_BgTitleColor;
                themeColor.LeftEnable = ThemeSakura_LeftItemEnableColor;
                themeColor.LeftDisable = ThemeSakura_LeftItemDisableColor;
                break;

            case 3:
                themeColor.BasicFont = ThemeYellow_BasicFontColor;
                themeColor.SubFont = ThemeYellow_SubFontColor;
                themeColor.BgList = ThemeYellow_BgListColor;
                themeColor.BgTitle = ThemeYellow_BgTitleColor;
                themeColor.LeftEnable = ThemeYellow_LeftItemEnableColor;
                themeColor.LeftDisable = ThemeYellow_LeftItemDisableColor;
                break;

            default:
                themeColor.BasicFont = ThemeBlack_BasicFontColor;
                themeColor.SubFont = ThemeBlack_SubFontColor;
                themeColor.BgList = ThemeBlack_BgListColor;
                themeColor.BgTitle = ThemeBlack_BgTitleColor;
                themeColor.LeftEnable = ThemeBlack_LeftItemEnableColor;
                themeColor.LeftDisable = ThemeBlack_LeftItemDisableColor;
                break;
        }
    }
}
