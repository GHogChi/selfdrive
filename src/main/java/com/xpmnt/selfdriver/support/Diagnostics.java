package com.xpmnt.selfdriver.support;

import static java.lang.System.*;

public class Diagnostics {
    /**
     * IDEA gives an irrelevant warning here; apparently identical warning
     * was fixed in NetBeans. Same warning in some other classes.
     * @see <a href="https://netbeans.org/bugzilla/show_bug.cgi?id=242627">
     *     Bug 242627 - Invalid "Confusing primitive array passed to vararg method" hint</a>
     * @param format
     * @param args
     */
    public static void dump(String format, Object... args) {
        out.println(String.format("[" + format + "]", args));
    }
}
