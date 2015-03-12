// $ANTLR 3.4 E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g 2012-10-30 21:27:15

package hdgl.db.query.parser;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class QueryLexer extends Lexer {
    public static final int EOF=-1;
    public static final int T__18=18;
    public static final int T__19=19;
    public static final int T__20=20;
    public static final int T__21=21;
    public static final int T__22=22;
    public static final int T__23=23;
    public static final int ESC_SEQ=4;
    public static final int EXPONENT=5;
    public static final int FLOAT=6;
    public static final int HEX_DIGIT=7;
    public static final int ID=8;
    public static final int INT=9;
    public static final int LQUOTE=10;
    public static final int OCTAL_ESC=11;
    public static final int OP=12;
    public static final int QUANTIFIER=13;
    public static final int RQUOTE=14;
    public static final int STRING=15;
    public static final int UNICODE_ESC=16;
    public static final int WS=17;

    // delegates
    // delegators
    public Lexer[] getDelegates() {
        return new Lexer[] {};
    }

    public QueryLexer() {} 
    public QueryLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public QueryLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);
    }
    public String getGrammarFileName() { return "E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g"; }

    // $ANTLR start "T__18"
    public final void mT__18() throws RecognitionException {
        try {
            int _type = T__18;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:11:7: ( '-' )
            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:11:9: '-'
            {
            match('-'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__18"

    // $ANTLR start "T__19"
    public final void mT__19() throws RecognitionException {
        try {
            int _type = T__19;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:12:7: ( '.' )
            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:12:9: '.'
            {
            match('.'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__19"

    // $ANTLR start "T__20"
    public final void mT__20() throws RecognitionException {
        try {
            int _type = T__20;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:13:7: ( ':' )
            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:13:9: ':'
            {
            match(':'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__20"

    // $ANTLR start "T__21"
    public final void mT__21() throws RecognitionException {
        try {
            int _type = T__21;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:14:7: ( '[' )
            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:14:9: '['
            {
            match('['); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__21"

    // $ANTLR start "T__22"
    public final void mT__22() throws RecognitionException {
        try {
            int _type = T__22;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:15:7: ( ']' )
            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:15:9: ']'
            {
            match(']'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__22"

    // $ANTLR start "T__23"
    public final void mT__23() throws RecognitionException {
        try {
            int _type = T__23;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:16:7: ( '|' )
            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:16:9: '|'
            {
            match('|'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__23"

    // $ANTLR start "OP"
    public final void mOP() throws RecognitionException {
        try {
            int _type = OP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:21:4: ( '<' | '>' | '<=' | '>=' | '<>' | '=' )
            int alt1=6;
            switch ( input.LA(1) ) {
            case '<':
                {
                switch ( input.LA(2) ) {
                case '=':
                    {
                    alt1=3;
                    }
                    break;
                case '>':
                    {
                    alt1=5;
                    }
                    break;
                default:
                    alt1=1;
                }

                }
                break;
            case '>':
                {
                int LA1_2 = input.LA(2);

                if ( (LA1_2=='=') ) {
                    alt1=4;
                }
                else {
                    alt1=2;
                }
                }
                break;
            case '=':
                {
                alt1=6;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 1, 0, input);

                throw nvae;

            }

            switch (alt1) {
                case 1 :
                    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:21:6: '<'
                    {
                    match('<'); 

                    }
                    break;
                case 2 :
                    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:21:10: '>'
                    {
                    match('>'); 

                    }
                    break;
                case 3 :
                    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:21:14: '<='
                    {
                    match("<="); 



                    }
                    break;
                case 4 :
                    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:21:19: '>='
                    {
                    match(">="); 



                    }
                    break;
                case 5 :
                    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:21:24: '<>'
                    {
                    match("<>"); 



                    }
                    break;
                case 6 :
                    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:21:29: '='
                    {
                    match('='); 

                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "OP"

    // $ANTLR start "QUANTIFIER"
    public final void mQUANTIFIER() throws RecognitionException {
        try {
            int _type = QUANTIFIER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:24:2: ( '*' | '+' | '?' )
            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:
            {
            if ( (input.LA(1) >= '*' && input.LA(1) <= '+')||input.LA(1)=='?' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "QUANTIFIER"

    // $ANTLR start "ID"
    public final void mID() throws RecognitionException {
        try {
            int _type = ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:27:5: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )* )
            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:27:7: ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
            {
            if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:27:31: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0 >= '0' && LA2_0 <= '9')||(LA2_0 >= 'A' && LA2_0 <= 'Z')||LA2_0=='_'||(LA2_0 >= 'a' && LA2_0 <= 'z')) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:
            	    {
            	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ID"

    // $ANTLR start "INT"
    public final void mINT() throws RecognitionException {
        try {
            int _type = INT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:30:5: ( ( '0' .. '9' )+ )
            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:30:7: ( '0' .. '9' )+
            {
            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:30:7: ( '0' .. '9' )+
            int cnt3=0;
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( ((LA3_0 >= '0' && LA3_0 <= '9')) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:
            	    {
            	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt3 >= 1 ) break loop3;
                        EarlyExitException eee =
                            new EarlyExitException(3, input);
                        throw eee;
                }
                cnt3++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "INT"

    // $ANTLR start "FLOAT"
    public final void mFLOAT() throws RecognitionException {
        try {
            int _type = FLOAT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:34:5: ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )? | '.' ( '0' .. '9' )+ ( EXPONENT )? | ( '0' .. '9' )+ EXPONENT )
            int alt10=3;
            alt10 = dfa10.predict(input);
            switch (alt10) {
                case 1 :
                    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:34:9: ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )?
                    {
                    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:34:9: ( '0' .. '9' )+
                    int cnt4=0;
                    loop4:
                    do {
                        int alt4=2;
                        int LA4_0 = input.LA(1);

                        if ( ((LA4_0 >= '0' && LA4_0 <= '9')) ) {
                            alt4=1;
                        }


                        switch (alt4) {
                    	case 1 :
                    	    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:
                    	    {
                    	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                    	        input.consume();
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt4 >= 1 ) break loop4;
                                EarlyExitException eee =
                                    new EarlyExitException(4, input);
                                throw eee;
                        }
                        cnt4++;
                    } while (true);


                    match('.'); 

                    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:34:25: ( '0' .. '9' )*
                    loop5:
                    do {
                        int alt5=2;
                        int LA5_0 = input.LA(1);

                        if ( ((LA5_0 >= '0' && LA5_0 <= '9')) ) {
                            alt5=1;
                        }


                        switch (alt5) {
                    	case 1 :
                    	    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:
                    	    {
                    	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                    	        input.consume();
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop5;
                        }
                    } while (true);


                    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:34:37: ( EXPONENT )?
                    int alt6=2;
                    int LA6_0 = input.LA(1);

                    if ( (LA6_0=='E'||LA6_0=='e') ) {
                        alt6=1;
                    }
                    switch (alt6) {
                        case 1 :
                            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:34:37: EXPONENT
                            {
                            mEXPONENT(); 


                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:35:9: '.' ( '0' .. '9' )+ ( EXPONENT )?
                    {
                    match('.'); 

                    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:35:13: ( '0' .. '9' )+
                    int cnt7=0;
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( ((LA7_0 >= '0' && LA7_0 <= '9')) ) {
                            alt7=1;
                        }


                        switch (alt7) {
                    	case 1 :
                    	    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:
                    	    {
                    	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                    	        input.consume();
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt7 >= 1 ) break loop7;
                                EarlyExitException eee =
                                    new EarlyExitException(7, input);
                                throw eee;
                        }
                        cnt7++;
                    } while (true);


                    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:35:25: ( EXPONENT )?
                    int alt8=2;
                    int LA8_0 = input.LA(1);

                    if ( (LA8_0=='E'||LA8_0=='e') ) {
                        alt8=1;
                    }
                    switch (alt8) {
                        case 1 :
                            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:35:25: EXPONENT
                            {
                            mEXPONENT(); 


                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:36:9: ( '0' .. '9' )+ EXPONENT
                    {
                    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:36:9: ( '0' .. '9' )+
                    int cnt9=0;
                    loop9:
                    do {
                        int alt9=2;
                        int LA9_0 = input.LA(1);

                        if ( ((LA9_0 >= '0' && LA9_0 <= '9')) ) {
                            alt9=1;
                        }


                        switch (alt9) {
                    	case 1 :
                    	    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:
                    	    {
                    	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                    	        input.consume();
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt9 >= 1 ) break loop9;
                                EarlyExitException eee =
                                    new EarlyExitException(9, input);
                                throw eee;
                        }
                        cnt9++;
                    } while (true);


                    mEXPONENT(); 


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "FLOAT"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:39:5: ( ( ' ' | '\\t' | '\\r' | '\\n' ) )
            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:39:9: ( ' ' | '\\t' | '\\r' | '\\n' )
            {
            if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            _channel=HIDDEN;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "WS"

    // $ANTLR start "STRING"
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:47:5: ( '\"' ( ESC_SEQ |~ ( '\\\\' | '\"' ) )* '\"' )
            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:47:8: '\"' ( ESC_SEQ |~ ( '\\\\' | '\"' ) )* '\"'
            {
            match('\"'); 

            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:47:12: ( ESC_SEQ |~ ( '\\\\' | '\"' ) )*
            loop11:
            do {
                int alt11=3;
                int LA11_0 = input.LA(1);

                if ( (LA11_0=='\\') ) {
                    alt11=1;
                }
                else if ( ((LA11_0 >= '\u0000' && LA11_0 <= '!')||(LA11_0 >= '#' && LA11_0 <= '[')||(LA11_0 >= ']' && LA11_0 <= '\uFFFF')) ) {
                    alt11=2;
                }


                switch (alt11) {
            	case 1 :
            	    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:47:14: ESC_SEQ
            	    {
            	    mESC_SEQ(); 


            	    }
            	    break;
            	case 2 :
            	    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:47:24: ~ ( '\\\\' | '\"' )
            	    {
            	    if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '!')||(input.LA(1) >= '#' && input.LA(1) <= '[')||(input.LA(1) >= ']' && input.LA(1) <= '\uFFFF') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop11;
                }
            } while (true);


            match('\"'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "STRING"

    // $ANTLR start "EXPONENT"
    public final void mEXPONENT() throws RecognitionException {
        try {
            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:52:10: ( ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+ )
            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:52:12: ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+
            {
            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:52:22: ( '+' | '-' )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0=='+'||LA12_0=='-') ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:
                    {
                    if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                        input.consume();
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;
                    }


                    }
                    break;

            }


            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:52:33: ( '0' .. '9' )+
            int cnt13=0;
            loop13:
            do {
                int alt13=2;
                int LA13_0 = input.LA(1);

                if ( ((LA13_0 >= '0' && LA13_0 <= '9')) ) {
                    alt13=1;
                }


                switch (alt13) {
            	case 1 :
            	    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:
            	    {
            	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt13 >= 1 ) break loop13;
                        EarlyExitException eee =
                            new EarlyExitException(13, input);
                        throw eee;
                }
                cnt13++;
            } while (true);


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "EXPONENT"

    // $ANTLR start "HEX_DIGIT"
    public final void mHEX_DIGIT() throws RecognitionException {
        try {
            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:55:11: ( ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' ) )
            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:
            {
            if ( (input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'F')||(input.LA(1) >= 'a' && input.LA(1) <= 'f') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "HEX_DIGIT"

    // $ANTLR start "ESC_SEQ"
    public final void mESC_SEQ() throws RecognitionException {
        try {
            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:59:5: ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' ) | UNICODE_ESC | OCTAL_ESC )
            int alt14=3;
            int LA14_0 = input.LA(1);

            if ( (LA14_0=='\\') ) {
                switch ( input.LA(2) ) {
                case '\"':
                case '\'':
                case '\\':
                case 'b':
                case 'f':
                case 'n':
                case 'r':
                case 't':
                    {
                    alt14=1;
                    }
                    break;
                case 'u':
                    {
                    alt14=2;
                    }
                    break;
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                    {
                    alt14=3;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 14, 1, input);

                    throw nvae;

                }

            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 14, 0, input);

                throw nvae;

            }
            switch (alt14) {
                case 1 :
                    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:59:9: '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' )
                    {
                    match('\\'); 

                    if ( input.LA(1)=='\"'||input.LA(1)=='\''||input.LA(1)=='\\'||input.LA(1)=='b'||input.LA(1)=='f'||input.LA(1)=='n'||input.LA(1)=='r'||input.LA(1)=='t' ) {
                        input.consume();
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;
                    }


                    }
                    break;
                case 2 :
                    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:60:9: UNICODE_ESC
                    {
                    mUNICODE_ESC(); 


                    }
                    break;
                case 3 :
                    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:61:9: OCTAL_ESC
                    {
                    mOCTAL_ESC(); 


                    }
                    break;

            }

        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ESC_SEQ"

    // $ANTLR start "OCTAL_ESC"
    public final void mOCTAL_ESC() throws RecognitionException {
        try {
            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:66:5: ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) )
            int alt15=3;
            int LA15_0 = input.LA(1);

            if ( (LA15_0=='\\') ) {
                int LA15_1 = input.LA(2);

                if ( ((LA15_1 >= '0' && LA15_1 <= '3')) ) {
                    int LA15_2 = input.LA(3);

                    if ( ((LA15_2 >= '0' && LA15_2 <= '7')) ) {
                        int LA15_4 = input.LA(4);

                        if ( ((LA15_4 >= '0' && LA15_4 <= '7')) ) {
                            alt15=1;
                        }
                        else {
                            alt15=2;
                        }
                    }
                    else {
                        alt15=3;
                    }
                }
                else if ( ((LA15_1 >= '4' && LA15_1 <= '7')) ) {
                    int LA15_3 = input.LA(3);

                    if ( ((LA15_3 >= '0' && LA15_3 <= '7')) ) {
                        alt15=2;
                    }
                    else {
                        alt15=3;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 15, 1, input);

                    throw nvae;

                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 15, 0, input);

                throw nvae;

            }
            switch (alt15) {
                case 1 :
                    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:66:9: '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); 

                    if ( (input.LA(1) >= '0' && input.LA(1) <= '3') ) {
                        input.consume();
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;
                    }


                    if ( (input.LA(1) >= '0' && input.LA(1) <= '7') ) {
                        input.consume();
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;
                    }


                    if ( (input.LA(1) >= '0' && input.LA(1) <= '7') ) {
                        input.consume();
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;
                    }


                    }
                    break;
                case 2 :
                    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:67:9: '\\\\' ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); 

                    if ( (input.LA(1) >= '0' && input.LA(1) <= '7') ) {
                        input.consume();
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;
                    }


                    if ( (input.LA(1) >= '0' && input.LA(1) <= '7') ) {
                        input.consume();
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;
                    }


                    }
                    break;
                case 3 :
                    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:68:9: '\\\\' ( '0' .. '7' )
                    {
                    match('\\'); 

                    if ( (input.LA(1) >= '0' && input.LA(1) <= '7') ) {
                        input.consume();
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;
                    }


                    }
                    break;

            }

        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "OCTAL_ESC"

    // $ANTLR start "UNICODE_ESC"
    public final void mUNICODE_ESC() throws RecognitionException {
        try {
            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:73:5: ( '\\\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT )
            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:73:9: '\\\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
            {
            match('\\'); 

            match('u'); 

            mHEX_DIGIT(); 


            mHEX_DIGIT(); 


            mHEX_DIGIT(); 


            mHEX_DIGIT(); 


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "UNICODE_ESC"

    // $ANTLR start "LQUOTE"
    public final void mLQUOTE() throws RecognitionException {
        try {
            int _type = LQUOTE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:75:8: ( '(' )
            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:75:10: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "LQUOTE"

    // $ANTLR start "RQUOTE"
    public final void mRQUOTE() throws RecognitionException {
        try {
            int _type = RQUOTE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:76:8: ( ')' )
            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:76:10: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "RQUOTE"

    public void mTokens() throws RecognitionException {
        // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:1:8: ( T__18 | T__19 | T__20 | T__21 | T__22 | T__23 | OP | QUANTIFIER | ID | INT | FLOAT | WS | STRING | LQUOTE | RQUOTE )
        int alt16=15;
        alt16 = dfa16.predict(input);
        switch (alt16) {
            case 1 :
                // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:1:10: T__18
                {
                mT__18(); 


                }
                break;
            case 2 :
                // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:1:16: T__19
                {
                mT__19(); 


                }
                break;
            case 3 :
                // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:1:22: T__20
                {
                mT__20(); 


                }
                break;
            case 4 :
                // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:1:28: T__21
                {
                mT__21(); 


                }
                break;
            case 5 :
                // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:1:34: T__22
                {
                mT__22(); 


                }
                break;
            case 6 :
                // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:1:40: T__23
                {
                mT__23(); 


                }
                break;
            case 7 :
                // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:1:46: OP
                {
                mOP(); 


                }
                break;
            case 8 :
                // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:1:49: QUANTIFIER
                {
                mQUANTIFIER(); 


                }
                break;
            case 9 :
                // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:1:60: ID
                {
                mID(); 


                }
                break;
            case 10 :
                // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:1:63: INT
                {
                mINT(); 


                }
                break;
            case 11 :
                // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:1:67: FLOAT
                {
                mFLOAT(); 


                }
                break;
            case 12 :
                // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:1:73: WS
                {
                mWS(); 


                }
                break;
            case 13 :
                // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:1:76: STRING
                {
                mSTRING(); 


                }
                break;
            case 14 :
                // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:1:83: LQUOTE
                {
                mLQUOTE(); 


                }
                break;
            case 15 :
                // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:1:90: RQUOTE
                {
                mRQUOTE(); 


                }
                break;

        }

    }


    protected DFA10 dfa10 = new DFA10(this);
    protected DFA16 dfa16 = new DFA16(this);
    static final String DFA10_eotS =
        "\5\uffff";
    static final String DFA10_eofS =
        "\5\uffff";
    static final String DFA10_minS =
        "\2\56\3\uffff";
    static final String DFA10_maxS =
        "\1\71\1\145\3\uffff";
    static final String DFA10_acceptS =
        "\2\uffff\1\2\1\1\1\3";
    static final String DFA10_specialS =
        "\5\uffff}>";
    static final String[] DFA10_transitionS = {
            "\1\2\1\uffff\12\1",
            "\1\3\1\uffff\12\1\13\uffff\1\4\37\uffff\1\4",
            "",
            "",
            ""
    };

    static final short[] DFA10_eot = DFA.unpackEncodedString(DFA10_eotS);
    static final short[] DFA10_eof = DFA.unpackEncodedString(DFA10_eofS);
    static final char[] DFA10_min = DFA.unpackEncodedStringToUnsignedChars(DFA10_minS);
    static final char[] DFA10_max = DFA.unpackEncodedStringToUnsignedChars(DFA10_maxS);
    static final short[] DFA10_accept = DFA.unpackEncodedString(DFA10_acceptS);
    static final short[] DFA10_special = DFA.unpackEncodedString(DFA10_specialS);
    static final short[][] DFA10_transition;

    static {
        int numStates = DFA10_transitionS.length;
        DFA10_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA10_transition[i] = DFA.unpackEncodedString(DFA10_transitionS[i]);
        }
    }

    class DFA10 extends DFA {

        public DFA10(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 10;
            this.eot = DFA10_eot;
            this.eof = DFA10_eof;
            this.min = DFA10_min;
            this.max = DFA10_max;
            this.accept = DFA10_accept;
            this.special = DFA10_special;
            this.transition = DFA10_transition;
        }
        public String getDescription() {
            return "33:1: FLOAT : ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )? | '.' ( '0' .. '9' )+ ( EXPONENT )? | ( '0' .. '9' )+ EXPONENT );";
        }
    }
    static final String DFA16_eotS =
        "\2\uffff\1\17\7\uffff\1\21\7\uffff";
    static final String DFA16_eofS =
        "\22\uffff";
    static final String DFA16_minS =
        "\1\11\1\uffff\1\60\7\uffff\1\56\7\uffff";
    static final String DFA16_maxS =
        "\1\174\1\uffff\1\71\7\uffff\1\145\7\uffff";
    static final String DFA16_acceptS =
        "\1\uffff\1\1\1\uffff\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\uffff\1\14"+
        "\1\15\1\16\1\17\1\2\1\13\1\12";
    static final String DFA16_specialS =
        "\22\uffff}>";
    static final String[] DFA16_transitionS = {
            "\2\13\2\uffff\1\13\22\uffff\1\13\1\uffff\1\14\5\uffff\1\15\1"+
            "\16\2\10\1\uffff\1\1\1\2\1\uffff\12\12\1\3\1\uffff\3\7\1\10"+
            "\1\uffff\32\11\1\4\1\uffff\1\5\1\uffff\1\11\1\uffff\32\11\1"+
            "\uffff\1\6",
            "",
            "\12\20",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\20\1\uffff\12\12\13\uffff\1\20\37\uffff\1\20",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA16_eot = DFA.unpackEncodedString(DFA16_eotS);
    static final short[] DFA16_eof = DFA.unpackEncodedString(DFA16_eofS);
    static final char[] DFA16_min = DFA.unpackEncodedStringToUnsignedChars(DFA16_minS);
    static final char[] DFA16_max = DFA.unpackEncodedStringToUnsignedChars(DFA16_maxS);
    static final short[] DFA16_accept = DFA.unpackEncodedString(DFA16_acceptS);
    static final short[] DFA16_special = DFA.unpackEncodedString(DFA16_specialS);
    static final short[][] DFA16_transition;

    static {
        int numStates = DFA16_transitionS.length;
        DFA16_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA16_transition[i] = DFA.unpackEncodedString(DFA16_transitionS[i]);
        }
    }

    class DFA16 extends DFA {

        public DFA16(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 16;
            this.eot = DFA16_eot;
            this.eof = DFA16_eof;
            this.min = DFA16_min;
            this.max = DFA16_max;
            this.accept = DFA16_accept;
            this.special = DFA16_special;
            this.transition = DFA16_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( T__18 | T__19 | T__20 | T__21 | T__22 | T__23 | OP | QUANTIFIER | ID | INT | FLOAT | WS | STRING | LQUOTE | RQUOTE );";
        }
    }
 

}