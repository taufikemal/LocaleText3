/*
 * Copyright (C) 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.localetext;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * This app demonstrates how to localize an app with text, an image,
 * a floating action button, an options menu, and the app bar.
 */
public class MainActivity extends AppCompatActivity {

    // Default quantity is 1.
    private int mInputQuantity = 1;

    // TODO: Get the number format for this locale.

    // Fixed price in U.S. dollars and cents: ten cents.
    private double mPrice = 0.10;
    double mIdExchangeRate = 14000;

    // Exchange rates for France (FR) and Israel (IW).
    double mFrExchangeRate = 0.93; // 0.93 euros = $1.
    double mIwExchangeRate = 3.61; // 3.61 new shekels = $1.


    private NumberFormat mNumberFormat=NumberFormat.getInstance();
    private NumberFormat mCurrencyFormat=NumberFormat.getCurrencyInstance();

    /**
     * Creates the view with a toolbar for the options menu
     * and a floating action button, and initialize the
     * app data.
     *
     * @param savedInstanceState Bundle with activity's previously saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHelp();
            }
        });

        // Get the current date.
        final Date myDate = new Date();
        // Add 5 days in milliseconds to create the expiration date.
        final long expirationDate = myDate.getTime() + TimeUnit.DAYS.toMillis(5);
        // Set the expiration date as the date to display.
        myDate.setTime(expirationDate);

        String myFormattedDate= DateFormat.getDateInstance().format(myDate);
        TextView expDateView=findViewById(R.id.date);
        expDateView.setText(myFormattedDate);

        String myFormattedPrice;
        String deviceLocale= Locale.getDefault().getCountry();
        if (deviceLocale.equals("ID")){
            mPrice*=mIdExchangeRate;
            myFormattedPrice=mCurrencyFormat.format(mPrice);
        } else {
            mCurrencyFormat=NumberFormat.getCurrencyInstance(Locale.US);
            myFormattedPrice=mCurrencyFormat.format(mPrice);
        }

        TextView localePrice=findViewById(R.id.price);
        localePrice.setText(myFormattedPrice);

        final TextView totalPrice=findViewById(R.id.total);

        // Get the EditText view for the entered quantity.
        final EditText enteredQuantity = (EditText) findViewById(R.id.quantity);
        // Add an OnEditorActionListener to the EditText view.
        enteredQuantity.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // String myFormattedTotal;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Close the keyboard.
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService
                            (Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    // Check if view v is empty.
                    if (v.toString().equals("")) {
                        // Don't format, leave alone.
                    } else {

                        try {
                            mInputQuantity=mNumberFormat.parse(v.getText().toString()).intValue();
                            v.setError(null);
                            String myFormattedQuantity=mNumberFormat.format(mInputQuantity);
                            v.setText(myFormattedQuantity);
                            double total=mInputQuantity*mPrice;
                            String myFormattedTotalPrice=mCurrencyFormat.format(total);
                            totalPrice.setText(myFormattedTotalPrice);

                        } catch (ParseException e) {
                            v.setError(getText(R.string.enter_number));
                            e.printStackTrace();
                        }

                        return true;
                    }
                }
                return false;
            }
        });
    }

    /**
     * Shows the Help screen.
     */
    private void showHelp() {
        // Create the intent.
        Intent helpIntent = new Intent(this, HelpActivity.class);
        // Start the HelpActivity.
        startActivity(helpIntent);
    }

    /**
     * Clears the quantity when resuming the app after language is changed.
     */
    @Override
    protected void onResume() {
        super.onResume();
        ((EditText) findViewById(R.id.quantity)).getText().clear();
    }

     // @param menu       Options menu
     // @return boolean   True after creating options menu.

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Handles options menu item clicks.
     *
     * @param item      Menu item
     * @return boolean  True if menu item is selected.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle options menu item clicks here.
        switch (item.getItemId()) {
            case R.id.action_help:
                Intent helpIntent = new Intent(this, HelpActivity.class);
                startActivity(helpIntent);
                return true;
            case R.id.action_language:
                Intent languageIntent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
                startActivity(languageIntent);
                return true;
            default:
                // Do nothing
        }
        return super.onOptionsItemSelected(item);
    }
}