package com.tableboy.crazytipcalc;

import android.os.SystemClock;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;


public class CrazyTipCalcActivity extends AppCompatActivity {

    private static final String TOTAL_BILL = "TOTAL_BILL";
    private static final String CURRENT_TIP = "CURRENT_TIP";
    private static final String BILL_WITHOUT_TIP = "BILL_WITHOUT_TIP";

    private double billBeforeTip;
    private double tipAmount;


    private double finalBill;

    EditText billBeforeTipEditText;
    EditText tipAmountEditText;
    EditText finalBillEditText;

    SeekBar tipChangeSeekBar;

    private int[] checkListValues = new int[12];

    CheckBox friendlyCheckBox, specialsCheckBox, opinionCheckBox;

    RadioGroup availableRadioGroup;
    RadioButton badRadioButton, okRadioButton, goodRadioButton;

    Button startChronometerButton, pauseChronometerButton, resetChronometerButton;

    Chronometer timeWaitingChronometer;
    Spinner problemSpinner;

    long secondsYouWaited = 0;

    TextView timeWaitingTextView;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putDouble(TOTAL_BILL, finalBill);
        outState.putDouble(CURRENT_TIP, tipAmount);
        outState.putDouble(BILL_WITHOUT_TIP, billBeforeTip);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crazy_tip_calc);

        if (savedInstanceState == null) {
            billBeforeTip = 0.0;
            tipAmount = 0.15;
            finalBill = 0.0;
        } else {
            billBeforeTip = savedInstanceState.getDouble(BILL_WITHOUT_TIP);
            tipAmount = savedInstanceState.getDouble(CURRENT_TIP);
            finalBill = savedInstanceState.getDouble(TOTAL_BILL);
        }

        billBeforeTipEditText = (EditText) findViewById(R.id.edit_text_bill);
        tipAmountEditText = (EditText) findViewById(R.id.edit_text_tip);
        finalBillEditText = (EditText) findViewById(R.id.edit_text_final_bill);

        billBeforeTipEditText.addTextChangedListener(billBeforeTipListener);

        tipChangeSeekBar = (SeekBar) findViewById(R.id.seek_bar_change_tip);

        tipChangeSeekBar.setOnSeekBarChangeListener(tipChangeSeekBarListener);

        friendlyCheckBox = (CheckBox) findViewById(R.id.check_box_friendly);
        specialsCheckBox = (CheckBox) findViewById(R.id.check_box_specials);
        opinionCheckBox = (CheckBox) findViewById(R.id.check_box_opinion);

        setUpIntroCheckBoxes();

        availableRadioGroup = (RadioGroup) findViewById(R.id.radio_group_availability);
        badRadioButton = (RadioButton) findViewById(R.id.radio_button_bad);
        goodRadioButton = (RadioButton) findViewById(R.id.radio_button_good);
        okRadioButton = (RadioButton) findViewById(R.id.radio_button_ok);

        addChangeListenerRadio();

        problemSpinner = (Spinner) findViewById(R.id.spinner_problem_solve);

        addItemSelectedListenerToSpinner();

        startChronometerButton = (Button) findViewById(R.id.button_start);
        pauseChronometerButton = (Button) findViewById(R.id.button_pause);
        resetChronometerButton = (Button) findViewById(R.id.button_reset);

        setButtonOnClickListeners();

        timeWaitingChronometer = (Chronometer) findViewById(R.id.chronometer_time_waiting);

        timeWaitingTextView = (TextView) findViewById(R.id.text_view_time_waiting);

    }

    private TextWatcher billBeforeTipListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                billBeforeTip = Double.parseDouble(s.toString());
            } catch (NumberFormatException e) {
                billBeforeTip = 0.0;
            }
            updateTipAndFinalBill();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }


    };


    private void updateTipAndFinalBill() {
        tipAmount = Double.parseDouble(tipAmountEditText.getText().toString());

        finalBill = billBeforeTip + (tipAmount * billBeforeTip);

        finalBillEditText.setText(String.format("%.02f", finalBill));

    }

    private OnSeekBarChangeListener tipChangeSeekBarListener = new OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            tipAmount = (tipChangeSeekBar.getProgress()) * 0.01;

            tipAmountEditText.setText(String.format("%.02f", tipAmount));

            updateTipAndFinalBill();

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private void setUpIntroCheckBoxes() {
        friendlyCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkListValues[0] = (friendlyCheckBox.isChecked()) ? 4 : 0;

                setTipFromWaitressCheckList();

                updateTipAndFinalBill();

            }
        });

        specialsCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkListValues[1] = (specialsCheckBox.isChecked()) ? 1 : 0;

                setTipFromWaitressCheckList();

                updateTipAndFinalBill();

            }
        });

        opinionCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkListValues[2] = (opinionCheckBox.isChecked()) ? 2 : 0;

                setTipFromWaitressCheckList();

                updateTipAndFinalBill();

            }
        });
    }

    private void setTipFromWaitressCheckList() {
        int checkListTotal = 0;

        for (int item : checkListValues) {
            checkListTotal += item;
        }

        tipAmountEditText.setText(""+checkListTotal);
    }

    private void addChangeListenerRadio() {
        availableRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                checkListValues[3] = (badRadioButton.isChecked()) ? -1 : 0;
                checkListValues[4] = (okRadioButton.isChecked()) ? 2 : 0;
                checkListValues[5] = (goodRadioButton.isChecked()) ? 4 : 0;

                setTipFromWaitressCheckList();

                updateTipAndFinalBill();
            }
        });
    }

    private void addItemSelectedListenerToSpinner() {
        problemSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                checkListValues[6] = (problemSpinner.getSelectedItem().equals("Bad")) ? -1 : 0;
                checkListValues[7] = (problemSpinner.getSelectedItem().equals("Ok")) ? 3 : 0;
                checkListValues[8] = (problemSpinner.getSelectedItem().equals("Good")) ? 6 : 0;

                setTipFromWaitressCheckList();

                updateTipAndFinalBill();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setButtonOnClickListeners() {
        startChronometerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int stopMilliSeconds = 0;

                String chronoText = timeWaitingChronometer.getText().toString();
                String array[] = chronoText.split(":");

                if (array.length==2) {
                    stopMilliSeconds=Integer.parseInt(array[0])*60*1000 +
                            Integer.parseInt(array[1])*1000;
                }
                else if (array.length == 3) {
                    stopMilliSeconds=Integer.parseInt(array[0])*60*60*1000 +
                            Integer.parseInt(array[1])*60*1000+
                            Integer.parseInt(array[2])*1000;
                }
                timeWaitingChronometer.setBase(SystemClock.elapsedRealtime() - stopMilliSeconds);
                secondsYouWaited = Long.parseLong(array[1]);

                updateTipBasedOnTimeYouWaited(secondsYouWaited);

                timeWaitingChronometer.start();
            }
        });

        pauseChronometerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeWaitingChronometer.stop();
            }
        });
        resetChronometerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeWaitingChronometer.setBase(SystemClock.elapsedRealtime());

                secondsYouWaited = 0;
            }
        });
    }

    private void updateTipBasedOnTimeYouWaited(long secondsYouWaited) {
        checkListValues[9] = (secondsYouWaited>10)?-2:0;

        setTipFromWaitressCheckList();

        updateTipAndFinalBill();
    }


}
