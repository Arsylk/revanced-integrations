package app.revanced.integrations.patches;

import android.content.Intent;
import android.view.View;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Date;

public class JakDojadePremiumPatch {

    public static Object getMockPurchase(String className) {
        try {
            Class<?> gProductClass = Class.forName("com.citynav.jakdojade.pl.android.billing.output.GoogleProduct");
            Field enumField = gProductClass.getDeclaredField("PREMIUM_YEARLY_V4");
            enumField.setAccessible(true);

            Class<?> jClass = Class.forName(className);
            Constructor<?> jConst = jClass.getConstructor(
                gProductClass,
                long.class,
                String.class,
                Date.class,
                Date.class
            );

            return jConst.newInstance(
        enumField.get(null),
                777L,
                "fun",
                new Date(1900, 6, 7),
                new Date(1900, 6, 7)
            );

        }catch (Exception e) {
            e.printStackTrace();
        }
        throw new IllegalStateException();
    }
}
