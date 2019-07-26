package com.enjoyshop.fragment;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.enjoyshop.R;

import butterknife.BindView;

@SuppressLint("NewApi")
public class CustomDialog extends Dialog {

    public CustomDialog(Context context) {
        super(context);
    }


    public CustomDialog(Context context, int theme) {
        super(context, theme);
    }

    @SuppressLint("InlinedApi")
    public static class Builder {
        private Context context;

        private DialogInterface.OnClickListener positiveButtonClickListener;


        public Builder(Context context) {
            this.context = context;
        }


        /**
         * Set the positive button resource and it's listener
         * 
         * @param positiveButtonText
         * @return
         */
        public Builder setPositiveButton(int positiveButtonText,
                                         DialogInterface.OnClickListener listener) {

            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setPositiveButton(String positiveButtonText,
                                         DialogInterface.OnClickListener listener) {
            this.positiveButtonClickListener = listener;
            return this;
        }

        public CustomDialog create(int resource) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
// instantiate the dialog with the custom Theme
            final CustomDialog dialog = new CustomDialog(context,
                    R.style.Dialog);

            View layout = inflater.inflate(resource, null);

            if (positiveButtonClickListener != null) {
                ((ImageButton) layout.findViewById(R.id.dialog_close_btn))
                        .setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                positiveButtonClickListener.onClick(dialog,
                                        DialogInterface.BUTTON_POSITIVE);
                            }
                        });
            }

            dialog.addContentView(layout, new LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));


            dialog.setContentView(layout);
            return dialog;
        }

    }

}
