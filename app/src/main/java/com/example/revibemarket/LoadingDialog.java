    package com.example.revibemarket;

    import android.app.Activity;
    import android.app.AlertDialog;
    import android.os.Handler;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.widget.TextView;

    import com.airbnb.lottie.LottieAnimationView;

    public class LoadingDialog {
        private Activity activity;
        private AlertDialog dialog;
        private LottieAnimationView lottie;
        private TextView textView;
        private String notification;


        public LoadingDialog(Activity activity,String notification) {
            this.activity = activity;
            this.notification = notification;
        }
        public void loadingDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);

            LayoutInflater inflater = activity.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.loading_dialog, null);
            builder.setView(dialogView);

            lottie = dialogView.findViewById(R.id.lottieAnimationView);
            lottie.playAnimation();

            textView = dialogView.findViewById(R.id.textViewDialog);
            textView.setText(notification);


            builder.setCancelable(true);
            dialog = builder.create();
            dialog.show();

        }

        public void dismissDialog() {
            lottie.setAnimation(R.raw.successful);
            lottie.playAnimation();
            textView.setText("Done!");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                }
            },1500);

        }
    }
