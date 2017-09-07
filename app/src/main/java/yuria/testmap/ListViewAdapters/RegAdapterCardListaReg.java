package yuria.testmap.ListViewAdapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import yuria.testmap.DescAcquistoActivity;
import yuria.testmap.MapsActivity;
import yuria.testmap.R;
import yuria.testmap.models.Registrazione;

/**
 * Created by yuria on 07/09/2017.
 */

public class RegAdapterCardListaReg extends ArrayAdapter<Registrazione> {

    public RegAdapterCardListaReg(Context context, ArrayList<Registrazione> regs)
    {
        super(context,0,regs);
    }

    View current = null;

    public View getView(int position, View convertView, ViewGroup parent){
        Registrazione reg =getItem(position);
        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.card_items2,parent,false);
        }
        current=convertView;
        TextView tv1 = (TextView) convertView.findViewById(R.id.cardText21);
        TextView tv2 = (TextView) convertView.findViewById(R.id.cardText22);
        TextView tv3 = (TextView) convertView.findViewById(R.id.cardText23);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        tv1.setText("Nome: "+reg.getNome());
        tv2.setText("Tipo: "+reg.getTipo());
        tv3.setText("Data: "+sdf.format(reg.getData()));


        Button detailsBtn = (Button) convertView.findViewById(R.id.cardDettagli2Btn);

        detailsBtn.setTag(position);
        detailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetails(v);


            }
        });
        return convertView;


    }


    private void showDetails(View v) {
        int position = (Integer) v.getTag();
        Registrazione reg = getItem(position);
        Intent int1 = new Intent(current.getContext(), DescAcquistoActivity.class);
        int1.putExtra("activity","registrazione");
        int1.putExtra("reg",reg);
        current.getContext().startActivity(int1);
    }
}
