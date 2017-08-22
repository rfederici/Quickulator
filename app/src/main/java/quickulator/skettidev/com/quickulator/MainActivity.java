package quickulator.skettidev.com.quickulator;

import android.app.Activity;
import android.support.v4.view.GestureDetectorCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity implements View.OnTouchListener{

    private GestureDetectorCompat mDetector;

    private TextView history, display;
    private ArrayList<Button> numBtns = new ArrayList<Button>();
    private Button decBtn, eqBtn;
    private char operand;

    double firstNum, secondNum;
    boolean hasFirst;


    private void addButtonsToGlobals () {

        history = (TextView) findViewById(R.id.history);
        display = (TextView) findViewById(R.id.display);

        int ids[] = {
                R.id.button_0, R.id.button_1, R.id.button_2, R.id.button_3, R.id.button_4,
                R.id.button_5, R.id.button_6, R.id.button_7, R.id.button_8, R.id.button_9
        };
        for (int id: ids) {
            numBtns.add((Button)findViewById(id));
        }

        decBtn = (Button) findViewById(R.id.button_dec);
        eqBtn = (Button) findViewById(R.id.button_eq);
    }

    private void addListenerToButtons () {
        for (Button btn: numBtns) {
            btn.setOnTouchListener(this);
        }
        decBtn.setOnTouchListener(this);
        eqBtn.setOnTouchListener(this);
    }


    private void setOperand(char o) {

        operand = o;
        if (!hasFirst) {
            firstNum = Double.parseDouble(display.getText().toString());
            display.setText("0");
            hasFirst = true;
        }

        history.setText(firstNum + " " + o);

    }

    private double add (double a, double b) {
        return a + b;
    }

    private double subtract (double a, double b) {
        return a - b;
    }

    private double multiply (double a, double b) {
        return a * b;
    }

    private double divide (double a, double b) {
        return a / b;
    }

    private void solve() {
        secondNum = Double.parseDouble(display.getText().toString());
        double result = 0;
        switch (operand) {
            case '+':
                result = add(firstNum, secondNum);
                break;
            case '-':
                result = subtract(firstNum, secondNum);
                break;
            case '*':
                result = multiply(firstNum, secondNum);
                break;
            case '/':
                result = divide(firstNum, secondNum);
                break;
        }
        history.setText("");
        display.setText(String.valueOf(result));
        hasFirst = false;
    }

    private void clear () {
        display.setText("0");
        history.setText("");
        hasFirst = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        onResume();

    }

    @Override
    protected void onResume() {

        super.onResume();
        mDetector = new GestureDetectorCompat(this, new MyGestureListener());
        addButtonsToGlobals();
        addListenerToButtons();


    }

    @Override
    public void onBackPressed() {
        String d = display.getText().toString();
        if (d.length() == 1)
            display.setText("0");
        else
            display.setText(
                    d.substring(0,d.length()-1)
            );
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            clear();
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    public boolean onTouch(View source, MotionEvent e) {

        boolean x = mDetector.onTouchEvent(e);
        if (e.getAction() == MotionEvent.ACTION_UP && !x) {
            //display = findViewById(R.id.display);
            if (numBtns.contains(source)) {
                String n = String.valueOf(numBtns.indexOf(source));
                Log.d("TEST", "Display is: " + display.getText());
                if (!display.getText().toString().equals("0"))
                    display.append(n);
                else
                    display.setText(n);
            }
            else if (decBtn.equals((Button) source)) {
                if (!display.getText().toString().contains("."))
                    display.append(".");
            }
            else if (source == eqBtn) {
                solve();
            }
        }

        return true;
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Gestures";

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            final int SWIPE_MIN_DISTANCE = 50;

            Log.d(DEBUG_TAG,"onFling();");
            Log.d(DEBUG_TAG,e1.toString() + e2.toString());
            Map<Character, Float> directions = new HashMap<Character, Float>();
            directions.put('*', e1.getY() - e2.getY());
            directions.put('/', e2.getY() - e1.getY());
            directions.put('-', e1.getX() - e2.getX());
            directions.put('+', e2.getX() - e1.getX());

            // Get value of max direction
            Map.Entry<Character, Float> max = new AbstractMap.SimpleEntry<Character, Float>('?', (float)0);
            for (Map.Entry<Character, Float> e: directions.entrySet()) {
                if (e.getValue() > max.getValue())
                    max = e;
            }


            if (Math.abs(max.getValue()) > SWIPE_MIN_DISTANCE) {
                setOperand(max.getKey());
                return true;
            }

            return false;
        }
    }

}