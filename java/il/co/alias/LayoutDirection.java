package il.co.alias;

import android.view.View;
import android.view.Window;

/**
 * Created by igapo on 15.09.2018.
 */

public class LayoutDirection {

    private static Window window;

    public static void setLayoutDirection(String i_language, Window i_window)
    {
        window = i_window;
        if(i_language.equals("en"))
        {
            setDirectionLtr();
        }
        else if(i_language.equals("iw"))
        {
            setDirectionRtl();
        }
    }

    private static void setDirectionRtl()
    {
        window.getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
    }

    private static void setDirectionLtr()
    {
        window.getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
    }
}
