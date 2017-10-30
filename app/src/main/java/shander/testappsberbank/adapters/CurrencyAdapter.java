package shander.testappsberbank.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import shander.testappsberbank.entities.Currency;

public class CurrencyAdapter extends ArrayAdapter {

    private List<Currency> currencies;
    private Context context;

    public CurrencyAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Currency> objects) {
        super(context, resource, objects);
        this.context = context;
        this.currencies = objects;
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View row = inflater.inflate(android.support.v7.appcompat.R.layout.support_simple_spinner_dropdown_item, parent,
                false);
        row.setContentDescription(String.valueOf(currencies.get(position).getNumCode()));
        TextView v = row.findViewById(android.R.id.text1);
        v.setText(currencies.get(position).getCharCode() + " " + currencies.get(position).getName());
        return row;
    }


    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View row = inflater.inflate(android.support.v7.appcompat.R.layout.support_simple_spinner_dropdown_item, parent,
                false);
        row.setContentDescription(String.valueOf(currencies.get(position).getNumCode()));
        TextView v = row.findViewById(android.R.id.text1);
        v.setText(currencies.get(position).getCharCode() + " " + currencies.get(position).getName());
        return row;
    }
}
