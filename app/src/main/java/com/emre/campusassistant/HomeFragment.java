package com.emre.campusassistant;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home,container,false);

        String username = getArguments().getString("username");
        int studentID = getArguments().getInt("id");

        WebView homeWebView = v.findViewById(R.id.homeWebView);
        homeWebView.setWebViewClient(new WebViewClient());
        homeWebView.getSettings().setJavaScriptEnabled(true);
        homeWebView.loadUrl("http://emreiot.baykalsarioglu.com/home.php?id="+studentID);
        homeWebView.setLayerType(homeWebView.LAYER_TYPE_HARDWARE, null);

        return v;
    }
}
