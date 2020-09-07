/**@TODO: Marked for deletion, pending Markus' success implementing NodeJVM∂ **/
package io.windflow.eternalengine.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@Deprecated
public class CssReducer {

    public Collection<String> getMatches() {
        Collection<String> c = new ArrayList<>(Arrays.asList("Mark", "Jo", "Onno", "Andrea", "Dante"));
        Collection<String> c2 = new ArrayList<>(Arrays.asList("Sian", "Sine", "Rush", "Zani", "Andrea", "Dante"));
        c2.retainAll(c);
        return c2;
    }

    public static void main (String[] args) {

        System.out.println(new CssReducer().getMatches());

    }

}
