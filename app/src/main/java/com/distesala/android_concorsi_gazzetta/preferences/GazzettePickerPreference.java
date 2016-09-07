package com.distesala.android_concorsi_gazzetta.preferences;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

/**
 * Created by Marco on 06/09/16.
 */
public class GazzettePickerPreference extends DialogPreference
{
    private static final String NAMESPACE_ATTRS = "http://schemas.android.com/apk/res-auto";

    private final int minValue;
    private final int maxValue;
    private final int step;

    private int valueIndex;
    private NumberPicker picker = null;


    public GazzettePickerPreference(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        this.minValue = attrs.getAttributeIntValue(NAMESPACE_ATTRS, "min_value", 15);
        this.maxValue = attrs.getAttributeIntValue(NAMESPACE_ATTRS, "max_value", 60);
        this.step = attrs.getAttributeIntValue(NAMESPACE_ATTRS, "step", 1);
    }

    @Override
    protected View onCreateDialogView()
    {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;

        picker = new NumberPicker(getContext());
        picker.setLayoutParams(layoutParams);

        FrameLayout dialogView = new FrameLayout(getContext());
        dialogView.addView(picker);

        return dialogView;
    }

    @Override
    protected void onBindDialogView(View view)
    {
        super.onBindDialogView(view);

        picker.setMinValue(0);
        picker.setMaxValue((maxValue - minValue)/step);
        picker.setDisplayedValues(getValuesToDisplay());
        picker.setWrapSelectorWheel(true);

        picker.setValue(getValueIndex());
    }

    @Override
    protected void onDialogClosed(boolean positiveResult)
    {
        if (positiveResult)
        {
            picker.clearFocus();
            int newValue = picker.getValue();
            if (callChangeListener(newValue))
            {
                setValueIndex(newValue);
            }
        }

    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index)
    {
        return a.getInt(index, minValue);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue)
    {
        setValueIndex(restorePersistedValue ? (getPersistedInt(0)-minValue)/step : (((Integer) defaultValue) - minValue)/step);
    }

    private void setValueIndex(int valueIndex)
    {
        this.valueIndex = valueIndex;
        persistInt(minValue + valueIndex*step);
    }

    private int getValueIndex()
    {
        return this.valueIndex;
    }

    private String[] getValuesToDisplay()
    {
        String[] values = new String[((maxValue - minValue)/step) +1];

        for (int i = 0; i < values.length; i++)
        {
            values[i] = String.valueOf(minValue + step*i);
        }

        return values;
    }
}
