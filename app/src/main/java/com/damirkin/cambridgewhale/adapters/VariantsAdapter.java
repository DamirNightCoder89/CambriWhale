package com.damirkin.cambridgewhale.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.damirkin.cambridgewhale.R;
import com.damirkin.cambridgewhale.word.WordVar;

import java.util.List;

public class VariantsAdapter extends ArrayAdapter<WordVar> {
    private LayoutInflater inflater;
    private int layout;
    private List<WordVar> varList;

    public VariantsAdapter(Context context, int resource, List<WordVar> varList) {
        super(context, resource, varList);
        this.varList = varList;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(this.layout, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        WordVar wordVar = varList.get(position);

        viewHolder.header_t.setText(wordVar.getType());
        viewHolder.example_t.setText(wordVar.getExample());
        viewHolder.body_t.setText(wordVar.getTranslation());

        return convertView;
    }

    private class ViewHolder {
        final TextView header_t, example_t, body_t;
        ViewHolder(View view) {
            header_t = (TextView) view.findViewById(R.id.head);
            example_t = (TextView) view.findViewById(R.id.example);
            body_t = (TextView) view.findViewById(R.id.translate);
        }
    }
}
