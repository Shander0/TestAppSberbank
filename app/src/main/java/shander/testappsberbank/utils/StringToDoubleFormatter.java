package shander.testappsberbank.utils;

        import org.simpleframework.xml.transform.Transform;

public class StringToDoubleFormatter implements Transform<Double> {
    @Override
    public Double read(String s) throws Exception {
        return Double.parseDouble(s.replace(",", "."));
    }

    @Override
    public String write(Double aDouble) throws Exception {
        return Double.toString(aDouble);
    }

}

