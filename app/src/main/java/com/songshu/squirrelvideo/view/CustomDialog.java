package com.songshu.squirrelvideo.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.songshu.squirrelvideo.R;


/**
 * Created by yb on 15-5-27.
 */
public class CustomDialog extends Dialog {
    private static final String TAG = CustomDialog.class.getSimpleName() + ":";

    public CustomDialog(Context context, int theme) {
        super(context, theme);
    }

    public CustomDialog(Context context) {
        super(context);
    }


    public static class Builder {

        private Context context;
        private String title;
        private String message;
        private String positiveButtonText;
        private String negativeButtonText;
        private View contentView;

        private OnClickListener positiveButtonClickListener;
        private OnClickListener negativeButtonClickListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }

        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        public Builder setPositiveButton(int positiveButtonText, OnClickListener listener) {
            this.positiveButtonText = (String) context.getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setPositiveButton(String positiveButtonText, OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(int negativeButtonText, OnClickListener listener) {
            this.negativeButtonText = (String) context.getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText, OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        public CustomDialog create(int layoutResource) {
            return create(layoutResource, 0);
        }

        public CustomDialog create(int layoutResource, int theme) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final CustomDialog dialog;
            if (theme != 0) {
                dialog = new CustomDialog(context, theme);
            } else {
                dialog = new CustomDialog(context, R.style.custom_dialog);
            }

            View layout = inflater.inflate(layoutResource, null);
            dialog.addContentView(layout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            if (!TextUtils.isEmpty(title)) {
                ((TextView) layout.findViewById(R.id.title)).setText(title);
            }

            if (!TextUtils.isEmpty(positiveButtonText)) {
                ((TextView) layout.findViewById(R.id.positiveButton)).setText(positiveButtonText);
                if (positiveButtonClickListener != null) {
                    ((TextView) layout.findViewById(R.id.positiveButton)).setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            positiveButtonClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                        }
                    });
                }
            } else {
                if (layout.findViewById(R.id.positiveButton) != null) {
                    layout.findViewById(R.id.positiveButton).setVisibility(View.GONE);
                }
            }

            if (!TextUtils.isEmpty(negativeButtonText)) {
                ((TextView) layout.findViewById(R.id.negativeButton)).setText(negativeButtonText);
                if (negativeButtonClickListener != null) {
                    ((TextView) layout.findViewById(R.id.negativeButton)).setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            negativeButtonClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
                        }
                    });
                }
            } else {
                if (layout.findViewById(R.id.negativeButton) != null) {
                    layout.findViewById(R.id.negativeButton).setVisibility(View.GONE);
                }
            }

            if ((layout.findViewById(R.id.negativeButton) != null && layout.findViewById(R.id.negativeButton).getVisibility() == View.GONE)
                    && (layout.findViewById(R.id.positiveButton) != null && layout.findViewById(R.id.positiveButton).getVisibility() == View.GONE)) {
                layout.findViewById(R.id.ll_btns).setVisibility(View.GONE);
                layout.findViewById(R.id.divider_port).setVisibility(View.GONE);
                layout.findViewById(R.id.divider_hori).setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(message)) {
                ((TextView) layout.findViewById(R.id.message)).setText(message);
            } else if (contentView != null) {
                // if no message set add the contentView to the dialog body
                ((RelativeLayout) layout.findViewById(R.id.content)).removeAllViews();
                ((RelativeLayout) layout.findViewById(R.id.content)).addView(contentView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }

            dialog.setContentView(layout);

            return dialog;
        }

    }
}
