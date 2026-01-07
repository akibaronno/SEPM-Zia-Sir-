package interfacetest;

/**
 *
 * @author rpbarbat
 */
public class InterfaceTest
{
    static public interface ITest
    {
        public int getFirstValue();
        public int getSecondValue();
    }

    static abstract public class ATest implements ITest
    {
        int first = 0;

        @Override
        public int getFirstValue()
        {
            return first++;
        }
    }

    static public class TestImpl extends ATest
    {
        int second = 0;

        @Override
        public int getSecondValue()
        {
            return second++;
        }
    }

    static public class Test
    {
        int value = 0;

        public int getConcreteValue()
        {
            return value++;
        }
    }

    static int loops = 1000000;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        // Get some various pointers to the test classes
        // To Interface
        ITest iTest = new TestImpl();

        // To abstract base
        ATest aTest = new TestImpl();

        // To impl
        TestImpl testImpl = new TestImpl();

        // To concrete
        Test test = new Test();

        System.out.println("Method call timings - " + loops + " loops");


        StopWatch stopWatch = new StopWatch();

        // Call interface method via interface reference
        stopWatch.start();

        for (int i = 0; i < loops; i++)
        {
            iTest.getFirstValue();
        }

        stopWatch.stop();

        System.out.println("interface method via interface reference: (nanos, millis)" + stopWatch.getElapsedNanos() + ", " + stopWatch.getElapsedMillis());


        // Call interface method via abstract reference
        stopWatch.start();

        for (int i = 0; i < loops; i++)
        {
            aTest.getFirstValue();
        }

        stopWatch.stop();

        System.out.println("interface method via abstract reference: (nanos, millis)" + stopWatch.getElapsedNanos() + ", " + stopWatch.getElapsedMillis());


        // Call derived interface via derived reference
        stopWatch.start();

        for (int i = 0; i < loops; i++)
        {
            testImpl.getSecondValue();
        }

        stopWatch.stop();

        System.out.println("interface via toplevel derived reference: (nanos, millis)" + stopWatch.getElapsedNanos() + ", " + stopWatch.getElapsedMillis());


        // Call concrete method in concrete class
        stopWatch.start();

        for (int i = 0; i < loops; i++)
        {
            test.getConcreteValue();
        }

        stopWatch.stop();

        System.out.println("Concrete method via concrete class reference: (nanos, millis)" + stopWatch.getElapsedNanos() + ", " + stopWatch.getElapsedMillis());
    }
}


package interfacetest;

/**
 *
 * @author rpbarbat
 */
public class StopWatch
{
    private long start;
    private long stop;

    public StopWatch()
    {
        start = 0;
        stop = 0;
    }

    public void start()
    {
        stop = 0;
        start = System.nanoTime();
    }

    public void stop()
    {
        stop = System.nanoTime();
    }

    public float getElapsedNanos()
    {
        return (stop - start);
    }

    public float getElapsedMillis()
    {
        return (stop - start) / 1000;
    }

    public float getElapsedSeconds()
    {
        return (stop - start) / 1000000000;
    }
}

