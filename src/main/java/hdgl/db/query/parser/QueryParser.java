// $ANTLR 3.4 E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g 2012-10-30 21:27:14

package hdgl.db.query.parser;

import hdgl.db.query.expression.*;
import hdgl.db.query.parser.*;
import hdgl.db.query.condition.*;
import java.util.ArrayList;



import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class QueryParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ESC_SEQ", "EXPONENT", "FLOAT", "HEX_DIGIT", "ID", "INT", "LQUOTE", "OCTAL_ESC", "OP", "QUANTIFIER", "RQUOTE", "STRING", "UNICODE_ESC", "WS", "'-'", "'.'", "':'", "'['", "']'", "'|'"
    };

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
    public Parser[] getDelegates() {
        return new Parser[] {};
    }

    // delegators


    public QueryParser(TokenStream input) {
        this(input, new RecognizerSharedState());
    }
    public QueryParser(TokenStream input, RecognizerSharedState state) {
        super(input, state);
    }

    public String[] getTokenNames() { return QueryParser.tokenNames; }
    public String getGrammarFileName() { return "E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g"; }



    // $ANTLR start "order"
    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:78:1: order returns [String val] : ( ID | OP );
    public final String order() throws RecognitionException {
        String val = null;


        Token ID1=null;
        Token OP2=null;

        try {
            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:79:2: ( ID | OP )
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==ID) ) {
                alt1=1;
            }
            else if ( (LA1_0==OP) ) {
                alt1=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 1, 0, input);

                throw nvae;

            }
            switch (alt1) {
                case 1 :
                    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:79:4: ID
                    {
                    ID1=(Token)match(input,ID,FOLLOW_ID_in_order542); 

                    val = (ID1!=null?ID1.getText():null);

                    }
                    break;
                case 2 :
                    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:80:4: OP
                    {
                    OP2=(Token)match(input,OP,FOLLOW_OP_in_order549); 

                    val = (OP2!=null?OP2.getText():null);

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return val;
    }
    // $ANTLR end "order"



    // $ANTLR start "value"
    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:83:1: value returns [AbstractValue val] : ( STRING | INT | FLOAT | ID );
    public final AbstractValue value() throws RecognitionException {
        AbstractValue val = null;


        Token STRING3=null;
        Token INT4=null;
        Token FLOAT5=null;
        Token ID6=null;

        try {
            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:84:2: ( STRING | INT | FLOAT | ID )
            int alt2=4;
            switch ( input.LA(1) ) {
            case STRING:
                {
                alt2=1;
                }
                break;
            case INT:
                {
                alt2=2;
                }
                break;
            case FLOAT:
                {
                alt2=3;
                }
                break;
            case ID:
                {
                alt2=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 2, 0, input);

                throw nvae;

            }

            switch (alt2) {
                case 1 :
                    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:84:4: STRING
                    {
                    STRING3=(Token)match(input,STRING,FOLLOW_STRING_in_value566); 

                    val = new StringValue((STRING3!=null?STRING3.getText():null));

                    }
                    break;
                case 2 :
                    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:85:4: INT
                    {
                    INT4=(Token)match(input,INT,FOLLOW_INT_in_value574); 

                    val = new IntNumberValue((INT4!=null?INT4.getText():null));

                    }
                    break;
                case 3 :
                    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:86:4: FLOAT
                    {
                    FLOAT5=(Token)match(input,FLOAT,FOLLOW_FLOAT_in_value582); 

                    val = new FloatNumberValue((FLOAT5!=null?FLOAT5.getText():null));

                    }
                    break;
                case 4 :
                    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:87:4: ID
                    {
                    ID6=(Token)match(input,ID,FOLLOW_ID_in_value590); 

                    val = new StringValue((ID6!=null?ID6.getText():null));

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return val;
    }
    // $ANTLR end "value"



    // $ANTLR start "entity"
    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:90:1: entity returns [Expression val] : ( '.' ( ID )? ( entityRestriction )* ( QUANTIFIER )? | '-' ( ID )? ( entityRestriction )* ( QUANTIFIER )? );
    public final Expression entity() throws RecognitionException {
        Expression val = null;


        Token ID8=null;
        Token QUANTIFIER9=null;
        Token ID11=null;
        Token QUANTIFIER12=null;
        Util.OrderAndCondition entityRestriction7 =null;

        Util.OrderAndCondition entityRestriction10 =null;


        try {
            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:91:2: ( '.' ( ID )? ( entityRestriction )* ( QUANTIFIER )? | '-' ( ID )? ( entityRestriction )* ( QUANTIFIER )? )
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==19) ) {
                alt9=1;
            }
            else if ( (LA9_0==18) ) {
                alt9=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 9, 0, input);

                throw nvae;

            }
            switch (alt9) {
                case 1 :
                    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:91:4: '.' ( ID )? ( entityRestriction )* ( QUANTIFIER )?
                    {
                     ArrayList<Util.OrderAndCondition> list = new ArrayList<Util.OrderAndCondition>();

                    match(input,19,FOLLOW_19_in_entity612); 

                    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:92:7: ( ID )?
                    int alt3=2;
                    int LA3_0 = input.LA(1);

                    if ( (LA3_0==ID) ) {
                        alt3=1;
                    }
                    switch (alt3) {
                        case 1 :
                            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:92:7: ID
                            {
                            ID8=(Token)match(input,ID,FOLLOW_ID_in_entity614); 

                            }
                            break;

                    }


                    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:92:11: ( entityRestriction )*
                    loop4:
                    do {
                        int alt4=2;
                        int LA4_0 = input.LA(1);

                        if ( (LA4_0==21) ) {
                            alt4=1;
                        }


                        switch (alt4) {
                    	case 1 :
                    	    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:92:12: entityRestriction
                    	    {
                    	    pushFollow(FOLLOW_entityRestriction_in_entity618);
                    	    entityRestriction7=entityRestriction();

                    	    state._fsp--;


                    	    list.add(entityRestriction7);

                    	    }
                    	    break;

                    	default :
                    	    break loop4;
                        }
                    } while (true);


                     
                    			Util.OrderAndConditions ocs = Util.combineOrderAndConditions(list);
                    			val = Expression.buildEntity(".", ocs.getOrder(), ocs.getConditions(), (ID8!=null?ID8.getText():null));
                    		

                    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:97:3: ( QUANTIFIER )?
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0==QUANTIFIER) ) {
                        alt5=1;
                    }
                    switch (alt5) {
                        case 1 :
                            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:97:4: QUANTIFIER
                            {
                            QUANTIFIER9=(Token)match(input,QUANTIFIER,FOLLOW_QUANTIFIER_in_entity631); 

                            val = Expression.buildQuantifier((QUANTIFIER9!=null?QUANTIFIER9.getText():null), val);

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:98:4: '-' ( ID )? ( entityRestriction )* ( QUANTIFIER )?
                    {
                     ArrayList<Util.OrderAndCondition> list = new ArrayList<Util.OrderAndCondition>();

                    match(input,18,FOLLOW_18_in_entity644); 

                    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:99:7: ( ID )?
                    int alt6=2;
                    int LA6_0 = input.LA(1);

                    if ( (LA6_0==ID) ) {
                        alt6=1;
                    }
                    switch (alt6) {
                        case 1 :
                            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:99:7: ID
                            {
                            ID11=(Token)match(input,ID,FOLLOW_ID_in_entity646); 

                            }
                            break;

                    }


                    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:99:11: ( entityRestriction )*
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( (LA7_0==21) ) {
                            alt7=1;
                        }


                        switch (alt7) {
                    	case 1 :
                    	    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:99:12: entityRestriction
                    	    {
                    	    pushFollow(FOLLOW_entityRestriction_in_entity650);
                    	    entityRestriction10=entityRestriction();

                    	    state._fsp--;


                    	    list.add(entityRestriction10);

                    	    }
                    	    break;

                    	default :
                    	    break loop7;
                        }
                    } while (true);


                     
                    			Util.OrderAndConditions ocs = Util.combineOrderAndConditions(list);
                    			val = Expression.buildEntity("-", ocs.getOrder(), ocs.getConditions(), (ID11!=null?ID11.getText():null));
                    		

                    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:104:3: ( QUANTIFIER )?
                    int alt8=2;
                    int LA8_0 = input.LA(1);

                    if ( (LA8_0==QUANTIFIER) ) {
                        alt8=1;
                    }
                    switch (alt8) {
                        case 1 :
                            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:104:4: QUANTIFIER
                            {
                            QUANTIFIER12=(Token)match(input,QUANTIFIER,FOLLOW_QUANTIFIER_in_entity664); 

                            val = Expression.buildQuantifier((QUANTIFIER12!=null?QUANTIFIER12.getText():null), val);

                            }
                            break;

                    }


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return val;
    }
    // $ANTLR end "entity"



    // $ANTLR start "entityRestriction"
    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:107:1: entityRestriction returns [Util.OrderAndCondition val] : ( '[' ID OP value ']' | '[' order ':' ID ']' | '[' order ':' ID OP value ']' );
    public final Util.OrderAndCondition entityRestriction() throws RecognitionException {
        Util.OrderAndCondition val = null;


        Token ID13=null;
        Token OP14=null;
        Token ID16=null;
        Token ID18=null;
        Token OP20=null;
        AbstractValue value15 =null;

        String order17 =null;

        String order19 =null;

        AbstractValue value21 =null;


        try {
            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:108:2: ( '[' ID OP value ']' | '[' order ':' ID ']' | '[' order ':' ID OP value ']' )
            int alt10=3;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==21) ) {
                int LA10_1 = input.LA(2);

                if ( (LA10_1==ID) ) {
                    int LA10_2 = input.LA(3);

                    if ( (LA10_2==OP) ) {
                        alt10=1;
                    }
                    else if ( (LA10_2==20) ) {
                        int LA10_5 = input.LA(4);

                        if ( (LA10_5==ID) ) {
                            int LA10_6 = input.LA(5);

                            if ( (LA10_6==22) ) {
                                alt10=2;
                            }
                            else if ( (LA10_6==OP) ) {
                                alt10=3;
                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("", 10, 6, input);

                                throw nvae;

                            }
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 10, 5, input);

                            throw nvae;

                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 10, 2, input);

                        throw nvae;

                    }
                }
                else if ( (LA10_1==OP) ) {
                    int LA10_3 = input.LA(3);

                    if ( (LA10_3==20) ) {
                        int LA10_5 = input.LA(4);

                        if ( (LA10_5==ID) ) {
                            int LA10_6 = input.LA(5);

                            if ( (LA10_6==22) ) {
                                alt10=2;
                            }
                            else if ( (LA10_6==OP) ) {
                                alt10=3;
                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("", 10, 6, input);

                                throw nvae;

                            }
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 10, 5, input);

                            throw nvae;

                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 10, 3, input);

                        throw nvae;

                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 10, 1, input);

                    throw nvae;

                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                throw nvae;

            }
            switch (alt10) {
                case 1 :
                    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:108:4: '[' ID OP value ']'
                    {
                    match(input,21,FOLLOW_21_in_entityRestriction683); 

                    ID13=(Token)match(input,ID,FOLLOW_ID_in_entityRestriction685); 

                    OP14=(Token)match(input,OP,FOLLOW_OP_in_entityRestriction687); 

                    pushFollow(FOLLOW_value_in_entityRestriction689);
                    value15=value();

                    state._fsp--;


                    match(input,22,FOLLOW_22_in_entityRestriction691); 

                     val = new Util.OrderAndCondition( null, Expression.buildCondition((ID13!=null?ID13.getText():null), (OP14!=null?OP14.getText():null), value15)); 

                    }
                    break;
                case 2 :
                    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:110:4: '[' order ':' ID ']'
                    {
                    match(input,21,FOLLOW_21_in_entityRestriction701); 

                    pushFollow(FOLLOW_order_in_entityRestriction703);
                    order17=order();

                    state._fsp--;


                    match(input,20,FOLLOW_20_in_entityRestriction705); 

                    ID16=(Token)match(input,ID,FOLLOW_ID_in_entityRestriction707); 

                    match(input,22,FOLLOW_22_in_entityRestriction709); 

                     val = new Util.OrderAndCondition( Expression.buildOrder((ID16!=null?ID16.getText():null), order17), null); 

                    }
                    break;
                case 3 :
                    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:112:4: '[' order ':' ID OP value ']'
                    {
                    match(input,21,FOLLOW_21_in_entityRestriction718); 

                    pushFollow(FOLLOW_order_in_entityRestriction720);
                    order19=order();

                    state._fsp--;


                    match(input,20,FOLLOW_20_in_entityRestriction722); 

                    ID18=(Token)match(input,ID,FOLLOW_ID_in_entityRestriction724); 

                    OP20=(Token)match(input,OP,FOLLOW_OP_in_entityRestriction726); 

                    pushFollow(FOLLOW_value_in_entityRestriction728);
                    value21=value();

                    state._fsp--;


                    match(input,22,FOLLOW_22_in_entityRestriction730); 

                     val = new Util.OrderAndCondition( Expression.buildOrder((ID18!=null?ID18.getText():null), order19), Expression.buildCondition((ID18!=null?ID18.getText():null), (OP20!=null?OP20.getText():null), value21)); 

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return val;
    }
    // $ANTLR end "entityRestriction"



    // $ANTLR start "group"
    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:116:1: group returns [Expression val] : LQUOTE (p1= parallel )+ RQUOTE ( QUANTIFIER )? ;
    public final Expression group() throws RecognitionException {
        Expression val = null;


        Token QUANTIFIER22=null;
        Expression p1 =null;


        try {
            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:117:2: ( LQUOTE (p1= parallel )+ RQUOTE ( QUANTIFIER )? )
            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:117:4: LQUOTE (p1= parallel )+ RQUOTE ( QUANTIFIER )?
            {
             ArrayList<Expression> list = new ArrayList<Expression>(); 

            match(input,LQUOTE,FOLLOW_LQUOTE_in_group753); 

            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:118:10: (p1= parallel )+
            int cnt11=0;
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);

                if ( (LA11_0==LQUOTE||(LA11_0 >= 18 && LA11_0 <= 19)) ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:118:11: p1= parallel
            	    {
            	    pushFollow(FOLLOW_parallel_in_group758);
            	    p1=parallel();

            	    state._fsp--;


            	     list.add(p1); 

            	    }
            	    break;

            	default :
            	    if ( cnt11 >= 1 ) break loop11;
                        EarlyExitException eee =
                            new EarlyExitException(11, input);
                        throw eee;
                }
                cnt11++;
            } while (true);


            match(input,RQUOTE,FOLLOW_RQUOTE_in_group770); 

             val = Expression.buildConcat(list.toArray(new Expression[0])); 

            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:122:3: ( QUANTIFIER )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==QUANTIFIER) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:122:4: QUANTIFIER
                    {
                    QUANTIFIER22=(Token)match(input,QUANTIFIER,FOLLOW_QUANTIFIER_in_group779); 

                     val = Expression.buildQuantifier((QUANTIFIER22!=null?QUANTIFIER22.getText():null), val); 

                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return val;
    }
    // $ANTLR end "group"



    // $ANTLR start "atom"
    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:128:1: fragment atom returns [Expression val] : ( entity | group );
    public final Expression atom() throws RecognitionException {
        Expression val = null;


        Expression entity23 =null;

        Expression group24 =null;


        try {
            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:129:2: ( entity | group )
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( ((LA13_0 >= 18 && LA13_0 <= 19)) ) {
                alt13=1;
            }
            else if ( (LA13_0==LQUOTE) ) {
                alt13=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 13, 0, input);

                throw nvae;

            }
            switch (alt13) {
                case 1 :
                    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:129:4: entity
                    {
                    pushFollow(FOLLOW_entity_in_atom805);
                    entity23=entity();

                    state._fsp--;


                     val = entity23; 

                    }
                    break;
                case 2 :
                    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:130:4: group
                    {
                    pushFollow(FOLLOW_group_in_atom812);
                    group24=group();

                    state._fsp--;


                     val = group24; 

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return val;
    }
    // $ANTLR end "atom"



    // $ANTLR start "parallel"
    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:133:1: parallel returns [Expression val] :e1= atom ( '|' e2= atom )* ;
    public final Expression parallel() throws RecognitionException {
        Expression val = null;


        Expression e1 =null;

        Expression e2 =null;


        try {
            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:134:2: (e1= atom ( '|' e2= atom )* )
            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:134:4: e1= atom ( '|' e2= atom )*
            {
             ArrayList<Expression> list = new ArrayList<Expression>(); 

            pushFollow(FOLLOW_atom_in_parallel835);
            e1=atom();

            state._fsp--;


             list.add(e1); 

            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:137:3: ( '|' e2= atom )*
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);

                if ( (LA14_0==23) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:137:4: '|' e2= atom
            	    {
            	    match(input,23,FOLLOW_23_in_parallel845); 

            	    pushFollow(FOLLOW_atom_in_parallel849);
            	    e2=atom();

            	    state._fsp--;


            	     list.add(e2); 

            	    }
            	    break;

            	default :
            	    break loop14;
                }
            } while (true);


             val = Expression.buildParallel(list.toArray(new Expression[0])); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return val;
    }
    // $ANTLR end "parallel"



    // $ANTLR start "expression"
    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:143:1: expression returns [Query val] : (p1= parallel )+ EOF ;
    public final Query expression() throws RecognitionException {
        Query val = null;


        Expression p1 =null;


        try {
            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:144:2: ( (p1= parallel )+ EOF )
            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:144:5: (p1= parallel )+ EOF
            {
             ArrayList<Expression> list = new ArrayList<Expression>(); 

            // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:145:3: (p1= parallel )+
            int cnt15=0;
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( (LA15_0==LQUOTE||(LA15_0 >= 18 && LA15_0 <= 19)) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // E:\\Project\\hdgl\\hdgl.db.query\\src\\hdgl\\db\\query\\parser\\Query.g:145:4: p1= parallel
            	    {
            	    pushFollow(FOLLOW_parallel_in_expression886);
            	    p1=parallel();

            	    state._fsp--;


            	     list.add(p1); 

            	    }
            	    break;

            	default :
            	    if ( cnt15 >= 1 ) break loop15;
                        EarlyExitException eee =
                            new EarlyExitException(15, input);
                        throw eee;
                }
                cnt15++;
            } while (true);


             val = Expression.buildQuery(Expression.buildConcat(list.toArray(new Expression[0]))); 

            match(input,EOF,FOLLOW_EOF_in_expression905); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return val;
    }
    // $ANTLR end "expression"

    // Delegated rules


 

    public static final BitSet FOLLOW_ID_in_order542 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OP_in_order549 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_value566 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_value574 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_value582 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_value590 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_19_in_entity612 = new BitSet(new long[]{0x0000000000202102L});
    public static final BitSet FOLLOW_ID_in_entity614 = new BitSet(new long[]{0x0000000000202002L});
    public static final BitSet FOLLOW_entityRestriction_in_entity618 = new BitSet(new long[]{0x0000000000202002L});
    public static final BitSet FOLLOW_QUANTIFIER_in_entity631 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_18_in_entity644 = new BitSet(new long[]{0x0000000000202102L});
    public static final BitSet FOLLOW_ID_in_entity646 = new BitSet(new long[]{0x0000000000202002L});
    public static final BitSet FOLLOW_entityRestriction_in_entity650 = new BitSet(new long[]{0x0000000000202002L});
    public static final BitSet FOLLOW_QUANTIFIER_in_entity664 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_21_in_entityRestriction683 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_ID_in_entityRestriction685 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_OP_in_entityRestriction687 = new BitSet(new long[]{0x0000000000008340L});
    public static final BitSet FOLLOW_value_in_entityRestriction689 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_22_in_entityRestriction691 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_21_in_entityRestriction701 = new BitSet(new long[]{0x0000000000001100L});
    public static final BitSet FOLLOW_order_in_entityRestriction703 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_entityRestriction705 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_ID_in_entityRestriction707 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_22_in_entityRestriction709 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_21_in_entityRestriction718 = new BitSet(new long[]{0x0000000000001100L});
    public static final BitSet FOLLOW_order_in_entityRestriction720 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_entityRestriction722 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_ID_in_entityRestriction724 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_OP_in_entityRestriction726 = new BitSet(new long[]{0x0000000000008340L});
    public static final BitSet FOLLOW_value_in_entityRestriction728 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_22_in_entityRestriction730 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LQUOTE_in_group753 = new BitSet(new long[]{0x00000000000C0400L});
    public static final BitSet FOLLOW_parallel_in_group758 = new BitSet(new long[]{0x00000000000C4400L});
    public static final BitSet FOLLOW_RQUOTE_in_group770 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_QUANTIFIER_in_group779 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_entity_in_atom805 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_group_in_atom812 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_atom_in_parallel835 = new BitSet(new long[]{0x0000000000800002L});
    public static final BitSet FOLLOW_23_in_parallel845 = new BitSet(new long[]{0x00000000000C0400L});
    public static final BitSet FOLLOW_atom_in_parallel849 = new BitSet(new long[]{0x0000000000800002L});
    public static final BitSet FOLLOW_parallel_in_expression886 = new BitSet(new long[]{0x00000000000C0400L});
    public static final BitSet FOLLOW_EOF_in_expression905 = new BitSet(new long[]{0x0000000000000002L});

}