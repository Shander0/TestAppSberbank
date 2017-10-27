package shander.testappsberbank.entities;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "Valute")
public class Currency {
    @Attribute(name = "ID")
    private String id;

    @Element(name = "Name")
    private String name;

    @Element(name = "NumCode")
    private int numCode;

    @Element(name = "CharCode")
    private String charCode;

    @Element(name = "Nominal")
    private Float nominal;

    @Element(name = "Value")
    private Float value;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getNumCode() {
        return numCode;
    }

    public String getCharCode() {
        return charCode;
    }

    public Float getNominal() {
        return nominal;
    }

    public Float getValue() {
        return value;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumCode(int numCode) {
        this.numCode = numCode;
    }

    public void setCharCode(String charCode) {
        this.charCode = charCode;
    }

    public void setNominal(Float nominal) {
        this.nominal = nominal;
    }

    public void setValue(Float value) {
        this.value = value;
    }
}
