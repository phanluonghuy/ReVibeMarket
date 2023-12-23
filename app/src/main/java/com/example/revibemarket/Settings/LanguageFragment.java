    package com.example.revibemarket.Settings;

    import android.content.SharedPreferences;
    import android.content.res.Configuration;
    import android.content.res.Resources;
    import android.os.Bundle;

    import androidx.annotation.CallSuper;
    import androidx.annotation.NonNull;
    import androidx.fragment.app.Fragment;

    import android.util.DisplayMetrics;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.ImageView;
    import android.widget.TextView;
    import android.widget.Toast;

    import com.bumptech.glide.load.engine.Resource;
    import com.example.revibemarket.R;

    import java.util.Locale;

    public class LanguageFragment extends Fragment {

        private static final String PREFS_NAME = "SaveLauguage";
        private static final String LANG_KEY = "language";

        private TextView tv_english, tv_vietnamese;
        private ImageView img_english, img_vietnamese, img_close_language;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_language, container, false);

            tv_english = view.findViewById(R.id.tv_english);
            tv_vietnamese = view.findViewById(R.id.tv_vietnamese);
            img_english = view.findViewById(R.id.img_english);
            img_vietnamese = view.findViewById(R.id.img_vietnamese);
            img_close_language = view.findViewById(R.id.img_close_language);



            setInitialLanguage();
            setDisplay();

            img_close_language.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();
                }
            });
            tv_english.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String language = "en";
                    setLocale(language);
                }
            });

            tv_vietnamese.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String language = "vi";
                    setLocale(language);
                }
            });
            return view;
        }

        private void setLocale(String language) {
            Resources resources = getResources();
            DisplayMetrics metrics = resources.getDisplayMetrics();
            Configuration configuration = resources.getConfiguration();
            configuration.setLocale(new Locale(language));
            resources.updateConfiguration(configuration, metrics);
            saveLanguage(language);
            setDisplay();
        }

        private void saveLanguage(String language) {
            SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(LANG_KEY, language);
            editor.apply();
        }

        private void setDisplay() {
            String lg = getResources().getConfiguration().locale.toString();

            if (lg.equals("en")) {
                img_english.setVisibility(View.VISIBLE);
                img_vietnamese.setVisibility(View.INVISIBLE);
            } else {
                img_english.setVisibility(View.INVISIBLE);
                img_vietnamese.setVisibility(View.VISIBLE);
            }
        }
        private String getCurrentLanguage() {
            SharedPreferences prefs = getContext().getSharedPreferences(PREFS_NAME, getContext().MODE_PRIVATE);
            return prefs.getString(LANG_KEY, "en");
        }
        private void setInitialLanguage() {
            String savedLanguage = getCurrentLanguage();
            setLocale(savedLanguage);
        }

    }