package testing;

import org.junit.Assert;
import org.junit.Test;
import testing.Calculator;

public class CalculatorTest {
    @Test
    public void testAdd() throws Exception {
        Calculator calculator = new Calculator();
        int sum = calculator.add(1, 2);
        Assert.assertEquals(2, sum);
    }

}
