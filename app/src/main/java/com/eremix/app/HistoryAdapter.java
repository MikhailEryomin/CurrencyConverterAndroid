package com.eremix.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.LinkedList;

public class HistoryAdapter extends ArrayAdapter<HistoryUnit> {

    private final LinkedList<HistoryUnit> historyUnits;
    private final int layout;
    private final LayoutInflater inflater;

    public HistoryAdapter(@NonNull Context context, int resource, @NonNull LinkedList<HistoryUnit> historyUnits) {
        super(context, resource, historyUnits);
        this.inflater = LayoutInflater.from(context);
        this.layout = resource;
        this.historyUnits = historyUnits;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = inflater.inflate(this.layout, parent, false);

        HistoryUnit historyUnit = historyUnits.get(position);

        Country from = historyUnit.getFromCountry();
        Country to = historyUnit.getToCountry();

        TextView fromCharCodeText = view.findViewById(R.id.h_from_charCode);
        TextView fromValueText = view.findViewById(R.id.h_from_value);
        ImageView fromFlag = view.findViewById(R.id.h_from_flagView);

        TextView toCharCodeText = view.findViewById(R.id.h_to_charCode);
        TextView toValueText = view.findViewById(R.id.h_to_value);
        ImageView toFlag = view.findViewById(R.id.h_to_flagView);

        if (from != null && to != null) {
            fromCharCodeText.setText(from.getCharCode());
            fromValueText.setText(String.valueOf(historyUnit.getFromValue()));
            String fromFlagLink = from.getImageResourceLink();
            if (fromFlagLink != null) Picasso.get().load(fromFlagLink).into(fromFlag);

            toCharCodeText.setText(to.getCharCode());
            toValueText.setText(String.valueOf(historyUnit.getToValue()));
            String toFlagLink = to.getImageResourceLink();
            if (toFlagLink != null) Picasso.get().load(toFlagLink).into(toFlag);
        }

        return view;
    }
}
