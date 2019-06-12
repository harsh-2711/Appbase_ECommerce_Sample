package com.harsh.appbase;

import android.content.Context;
import android.content.Intent;


public class EasyMagicPrintFeedback {

    private Context context;
    private String emailId;
    private boolean withSystemInfo;


    public EasyMagicPrintFeedback(Builder builder) {

        this.emailId = builder.emailId;
        this.context = builder.context;
        this.withSystemInfo = builder.withSystemInfo;

    }

    public static class Builder {

        private Context context;
        private String emailId;
        private boolean withSystemInfo;


        public Builder(Context context) {
            this.context = context;
        }

        public Builder withEmail(String email) {
            this.emailId = email;
            return this;
        }

        public Builder withSystemInfo() {
            withSystemInfo = true;
            return this;
        }


        public EasyMagicPrintFeedback build() {
            return new EasyMagicPrintFeedback(this);
        }

    }

    public void start() {

        Intent intent = new Intent(context, MagicPrintFeedback.class);
        intent.putExtra("email", emailId);
        intent.putExtra("with_info", withSystemInfo);
        context.startActivity(intent);


    }

}
