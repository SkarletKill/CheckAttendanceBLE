package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import entity.Pupil;
import networks.neo.ble.checkattendanceble.R;

public class PupilModelAdapter extends BaseAdapter {
    private List<Pupil> pupils;
    private LayoutInflater inflater;

    public PupilModelAdapter(Context context, List<Pupil> pupils) {
        this.pupils = pupils;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return pupils.size();
    }

    @Override
    public Object getItem(int i) {
        return pupils.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        if (v == null) {
            v = inflater.inflate(R.layout.pupils, viewGroup, false);
        }

        Pupil pupil = getPupil(i);

        TextView textpupil = (TextView) v.findViewById(R.id.textView);
        textpupil.setText(pupil.getName());

        return v;
    }

    private Pupil getPupil(int i) {
        return (Pupil) getItem(i);
    }
}
