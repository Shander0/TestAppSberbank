package shander.testappsberbank.processes;

public abstract class AbstractProcess {

    private Logger logger;

    public abstract void start();

    public AbstractProcess(Logger logger) {
        this.logger = logger;
    }

    public String getName() {
        return null;
    }

    protected void log(CharSequence msg) {
        logger.log(msg);
    }

    protected void postProgress(int progress) {
        logger.postProgress(progress);
    }

    protected void postProgressName(String name) {
        logger.postProgressName(name);
    }

    public interface Logger {
        void log(CharSequence msg);
        void postProgress(int progress);
        void postProgressName(String name);
    }


    protected int calculateProgress(long value, long max, int from, int to) {
        return (int) (((((float) value) / max) * (to - from)) + from);
    }
}
