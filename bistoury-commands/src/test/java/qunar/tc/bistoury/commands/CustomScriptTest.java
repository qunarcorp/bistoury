package qunar.tc.bistoury.commands;

import org.junit.Test;

/**
 * Created by cai.wen on 18-10-11.
 *
 * @author cai.wen.
 */
public class CustomScriptTest {

    @Test
    public void CustomScriptMap() {
        System.out.println(CustomScript.scriptPaths());
    }

    @Test
    public void testScriptPath() {

        assert !CustomScript.containsCustomScript("test  ");
        assert !CustomScript.containsCustomScript("test");
        assert CustomScript.containsCustomScript("qjdump");
        assert !CustomScript.containsCustomScript(null);


        System.out.println(CustomScript.customScriptPath("test  "));
        System.out.println(CustomScript.customScriptPath("test"));
        System.out.println(CustomScript.customScriptPath("qjdump"));
        System.out.println(CustomScript.customScriptPath("qjdump test"));
        System.out.println(CustomScript.customScriptPath(null));


        System.out.println(CustomScript.replaceScriptPath("qjdump   test"));
        System.out.println(CustomScript.replaceScriptPath("qjdump test"));
        System.out.println(CustomScript.replaceScriptPath("qjdump   test"));
        System.out.println(CustomScript.replaceScriptPath("qjdump"));
        System.out.println(CustomScript.replaceScriptPath("qjdum "));
    }
}
