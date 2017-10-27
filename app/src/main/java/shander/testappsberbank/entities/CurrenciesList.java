package shander.testappsberbank.entities;

import android.support.annotation.Nullable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

@Root(name = "ValCurs")
public class CurrenciesList implements Serializable {

    @Attribute(name = "Date")
    private String date;

    @Attribute(name = "name")
    private String name;

    @ElementList(entry = "Valute", inline = true)
    private List<Currency> currencies;

    @Nullable
    public String getDate() {
        return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date);
    }

    public String getName() {
        return name;
    }

    public List<Currency> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List<Currency> currencies) {
        this.currencies = currencies;
    }
}
