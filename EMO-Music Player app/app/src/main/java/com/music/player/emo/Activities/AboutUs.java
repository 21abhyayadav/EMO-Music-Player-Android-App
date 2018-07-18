package com.music.player.emo.Activities;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.music.player.emo.R;

public class AboutUs extends Fragment {
    private TextView TextView1;
    View view;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("About Us");
        view = inflater.inflate(R.layout.activity_about_us, container, false);
        TextView1 = (TextView) view.findViewById(R.id.textView);
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append("Made with ")
                .append(" ", new ImageSpan(getActivity(), R.drawable.hrt), 0)
                .append(" by Abhya, Mansi, Naman and Piyush under the guidance of Ms. Priyanka Kataria, Ms. Sharanjeet Kaur and Mr. Mohd. Nadeem Uddin.");
        TextView1.setText(builder);
        return view;
    }
}