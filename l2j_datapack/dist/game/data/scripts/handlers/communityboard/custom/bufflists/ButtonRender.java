package handlers.communityboard.custom.bufflists;

public class ButtonRender {

    public static String render(String name, String action) {
        return "<button value=\"" + name + "\"" +
                "action=\"" + action + "\"" +
                "width=150 height=22 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">";
    }

}
