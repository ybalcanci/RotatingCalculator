package ybalcanci.rotatingcalculator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {

    private static final int FUNCTION_MAX_LENGTH = 30;
    private static final String DEFAULT_FUNCTION = "0";

    public MainThread thread;
    private boolean created;
    private CirclePlayer[] circlePlayers;
    private boolean justCalculated;
    private TextPlayer[] textPlayers;
    private TextPlayer function;
    private double rotatingSpeed;
    private int originX;
    private int originY;
    private int turnRadius1;
    private int turnRadius2;
    private static final String[] TEXTS = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "+", "-", "=", "x", "/", "C"};
    private int buttonCount = TEXTS.length;
    private Point[] playerPoints;
    private static final double[] angles1 = new double[]{0, 30, 60, 90, 120, 150, 180, 210, 240, 270, 300, 330};
    private static final double[] angles2 = new double[]{0, 45, 90, 135, 180, 225, 270, 315};

    public GamePanel(Context context, Point screenSize){
        super(context);
        getHolder().addCallback(this);
        thread = new MainThread(getHolder(), this);
        justCalculated = false;
        created = false;
        rotatingSpeed = 0.03;
        originX = screenSize.x / 2;
        originY = screenSize.y * 5 / 8;
        turnRadius1 = screenSize.x * 8 / 24;
        turnRadius2 = screenSize.x * 7 / 40;
        function = new TextPlayer(DEFAULT_FUNCTION, screenSize.x / 8, screenSize.y / 16, Color.WHITE);
        function.setTextSize(screenSize.x / 16);
        playerPoints = new Point[buttonCount + 1];
        circlePlayers = new CirclePlayer[buttonCount];
        textPlayers = new TextPlayer[buttonCount];
        for(int i = 0; i < buttonCount; i++) {
            if(TEXTS[i].equals("=")) {
                circlePlayers[i] = new CirclePlayer(originX, originY, screenSize.x / 12, "#FF9501");
                textPlayers[i] = new TextPlayer(TEXTS[i], originX, originY, Color.LTGRAY);
            }
            else if(TEXTS[i].equals("+") || TEXTS[i].equals("-") || TEXTS[i].equals("/") || TEXTS[i].equals("x")) {
                circlePlayers[i] = new CirclePlayer(originX, originY, screenSize.x / 12, "#A6A6A6");
                textPlayers[i] = new TextPlayer(TEXTS[i], originX, originY, Color.BLACK);
            }
            else {
                circlePlayers[i] = new CirclePlayer(originX, originY, screenSize.x / 12, "#333333");
                textPlayers[i] = new TextPlayer(TEXTS[i], originX, originY, Color.LTGRAY);
            }
            playerPoints[i] = new Point(originX, originY);
        }
        setFocusable(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.created = true;
        thread = new MainThread(getHolder(), this);

        thread.setRunning(true);
        if(!thread.isAlive())
            thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        created = false;
    }
    public void stopThread(){
        boolean retry = true;
        while(retry){
            try{
                thread.setRunning(false);
                thread.join();
                retry = false;
            } catch(Exception e){e.printStackTrace();}
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if(justCalculated) {
                    function.update(DEFAULT_FUNCTION);
                    justCalculated = false;
                }
                double x = event.getX();
                double y = event.getY();
                if (function.getText().length() < FUNCTION_MAX_LENGTH) {
                    for (int i = 0; i < buttonCount; i++) {
                        if (i != 12 && clickInsideCircle(circlePlayers[i], x, y)) {
                            if(i != 15){
                                if(!((function.getText().equals(DEFAULT_FUNCTION) || isOperator(function.getText().charAt(function.getText().length() - 1)))  && isOperator(textPlayers[i].getText().charAt(0)))) {
                                    if (function.getText().equals(DEFAULT_FUNCTION))
                                        function.update(textPlayers[i].getText());
                                    else
                                        function.update(function.getText() + textPlayers[i].getText());
                                }
                            }
                        }
                    }
                }
                if (clickInsideCircle(circlePlayers[12], x, y)) {
                    function.update(eval(function.getText()) + "");
                    justCalculated = true;
                }
                else if (clickInsideCircle(circlePlayers[15], x, y)) {
                    if(!function.getText().equals(DEFAULT_FUNCTION))
                        function.update(function.getText().substring(0, function.getText().length() - 1));
                    if(function.getText().length() == 0)
                        function.update(DEFAULT_FUNCTION);
                }
            }
        }catch(Exception e){e.printStackTrace();}
        return true;
    }

    public void update(){
        int tr; // turn radius
        int a; // temporary index for skipping '=' character because it is static in the screen.
        double[] angles;
        for(int i = 0; i < buttonCount; i++) {
            if(!textPlayers[i].getText().equals("=")) {
                a = i;
                if(i > 12)
                    a = i - 1;
                if(a < 9) {
                    tr = turnRadius1;
                    angles = angles1;
                    playerPoints[i].set(originX + (int) (Math.cos(angles[a % 9]) * tr), originY + (int) (Math.sin(angles[a % 9]) * tr));
                    if(a < 12)
                        angles[a % 12] += 0.03;
                    if (angles[a % 12] == 360)
                        angles[a % 12] = 0;
                }
                else {
                    tr = turnRadius2;
                    angles = angles2;
                    playerPoints[i].set(originX + (int) (Math.cos(angles[a % 9]) * tr), originY + (int) (Math.sin(angles[a % 9]) * tr));
                    if(a < buttonCount)
                        angles[a % 9] += rotatingSpeed;
                    if (angles[a % 9] == 360)
                        angles[a % 9] = 0;
                }
                circlePlayers[i].update(playerPoints[i]);
                textPlayers[i].update(playerPoints[i]);
            }
        }
    }
    @Override
    public void draw(Canvas canvas){
        if(canvas != null) {
            super.draw(canvas);

            canvas.drawColor(Color.parseColor("#010101"));
            function.draw(canvas);
            for (int i = 0; i < buttonCount; i++) {
                circlePlayers[i].draw(canvas);
                textPlayers[i].draw(canvas);
            }
        }
    }

    public boolean clickInsideCircle(CirclePlayer cp, double x, double y){
        return Math.sqrt(Math.pow((cp.getX() - x), 2) + Math.pow((cp.getY() - y), 2)) <= cp.getRadius();
    }

    // https://stackoverflow.com/questions/3422673/evaluating-a-math-expression-given-in-string-form
    public static double eval(final String str) {
        final String newstr = str.replace('x','*');
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < newstr.length()) ? newstr.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < newstr.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            //        | number | functionName factor | factor `^` factor

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(newstr.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = newstr.substring(startPos, this.pos);
                    x = parseFactor();
                    if (func.equals("sqrt")) x = Math.sqrt(x);
                    else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
                    else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
                    else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
                    else throw new RuntimeException("Unknown function: " + func);
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }

    public boolean isOperator(char c){
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '(' || c == ')' || c == '=' || c == 'C';
    }
}
