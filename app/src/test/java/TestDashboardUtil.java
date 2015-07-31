import com.gmo.buckwise.util.Util;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created by GMO on 5/28/2015.
 */
public class TestDashboardUtil {

    Util du = new Util();

    @Test
    public void testGetCurrentMonth() {
        String expectedString = "May";
        String actualString = du.getCurrentMonth();
        assertEquals(expectedString, actualString);
    }

    @Test
    public void testGetDayOfMonth(){
        String expected = "29";
        String actual = du.getDayOfMonth();
        assertEquals(expected, actual);
    }
}
