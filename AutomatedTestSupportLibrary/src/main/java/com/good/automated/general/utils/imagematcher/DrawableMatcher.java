package com.good.automated.general.utils.imagematcher;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class DrawableMatcher extends TypeSafeMatcher<View> {

    private final int expectedId;
    String resourceName;

    public DrawableMatcher(int expectedId) {
        super(View.class);
        this.expectedId = expectedId;
    }

    @Override
    protected boolean matchesSafely(View target) {
        if (!(target instanceof ImageView)){
            return false;
        }
        ImageView imageView = (ImageView) target;
        if (expectedId < 0){
            return imageView.getDrawable() == null;
        }
        Context context = target.getContext();
        Drawable expectedDrawable = ContextCompat.getDrawable(context, expectedId);
        resourceName = context.getResources().getResourceEntryName(expectedId);

        if (expectedDrawable == null) {
            return false;
        }

        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        Bitmap otherBitmap = ((BitmapDrawable) expectedDrawable).getBitmap();
        return bitmap.sameAs(otherBitmap);
    }

    /**
    * This method is called in case of Failure
    */
    @Override
    public void describeTo(Description description) {
        description.appendText(" Drawable with resource id: ");
        description.appendValue(expectedId);
        description.appendText(" doesn't match to: ");
        if (resourceName != null) {
            description.appendText("[");
            description.appendText(resourceName);
            description.appendText("]");
        }
    }
}