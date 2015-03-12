grammar Query;

options {
	language=Java;
}

@header{
package hdgl.db.query.parser;

import hdgl.db.query.expression.*;
import hdgl.db.query.parser.*;
import hdgl.db.query.condition.*;
import java.util.ArrayList;

}

@lexer::header {
package hdgl.db.query.parser.output;
}

OP	:	'<'|'>'|'<='|'>='|'<>'|'='
	;
QUANTIFIER
	:	'*'|'+'|'?'
	;

ID  :	('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*
    ;

INT :	'0'..'9'+
    ;

FLOAT
    :   ('0'..'9')+ '.' ('0'..'9')* EXPONENT?
    |   '.' ('0'..'9')+ EXPONENT?
    |   ('0'..'9')+ EXPONENT
    ;

WS  :   ( ' '
        | '\t'
        | '\r'
        | '\n'
        ) {$channel=HIDDEN;}
    ;

STRING
    :  '"' ( ESC_SEQ | ~('\\'|'"') )* '"'
    ;

fragment
EXPONENT : ('e'|'E') ('+'|'-')? ('0'..'9')+ ;

fragment
HEX_DIGIT : ('0'..'9'|'a'..'f'|'A'..'F') ;

fragment
ESC_SEQ
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
    |   UNICODE_ESC
    |   OCTAL_ESC
    ;

fragment
OCTAL_ESC
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

fragment
UNICODE_ESC
    :   '\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
    ;

LQUOTE	:	'('	;
RQUOTE	:	')'	;

order	returns [String val]
	:	ID	{$val = $ID.text;}
	|	OP	{$val = $OP.text;}
	;

value	returns [AbstractValue val]
	:	STRING		{$val = new StringValue($STRING.text);}
	|	INT		{$val = new IntNumberValue($INT.text);}
	|	FLOAT		{$val = new FloatNumberValue($FLOAT.text);}
	|	ID		{$val = new StringValue($ID.text);}
	;

entity	returns [Expression val]
	:	{ ArrayList<Util.OrderAndCondition> list = new ArrayList<Util.OrderAndCondition>();}
		'.'	ID?	(entityRestriction	{list.add($entityRestriction.val);})*
		{ 
			Util.OrderAndConditions ocs = Util.combineOrderAndConditions(list);
			$val = Expression.buildEntity(".", ocs.getOrder(), ocs.getConditions(), $ID.text);
		}
		(QUANTIFIER	{$val = Expression.buildQuantifier($QUANTIFIER.text, $val);})?
	|	{ ArrayList<Util.OrderAndCondition> list = new ArrayList<Util.OrderAndCondition>();}
		'-'	ID?	(entityRestriction	{list.add($entityRestriction.val);})*	
		{ 
			Util.OrderAndConditions ocs = Util.combineOrderAndConditions(list);
			$val = Expression.buildEntity("-", ocs.getOrder(), ocs.getConditions(), $ID.text);
		}
		(QUANTIFIER	{$val = Expression.buildQuantifier($QUANTIFIER.text, $val);})?
	;
	
entityRestriction returns[Util.OrderAndCondition val]
	:	'['	ID	OP	value	']'	
		{ $val = new Util.OrderAndCondition( null, Expression.buildCondition($ID.text, $OP.text, $value.val)); }
	|	'['	order	':'	ID	']'
		{ $val = new Util.OrderAndCondition( Expression.buildOrder($ID.text, $order.val), null); }
	|	'['	order	':'	ID	OP	value	']'
		{ $val = new Util.OrderAndCondition( Expression.buildOrder($ID.text, $order.val), Expression.buildCondition($ID.text, $OP.text, $value.val)); }
	;

group	returns [Expression val]
	:	{ ArrayList<Expression> list = new ArrayList<Expression>(); }
		LQUOTE (p1=parallel 
		{ list.add($p1.val); }
		)+ RQUOTE
		{ $val = Expression.buildConcat(list.toArray(new Expression[0])); }
		(QUANTIFIER
		{ $val = Expression.buildQuantifier($QUANTIFIER.text, $val); }
		)?
	;

fragment
atom	returns [Expression val]
	:	entity	{ $val = $entity.val; }
	|	group	{ $val = $group.val; }
	;

parallel returns [Expression val]
	:	{ ArrayList<Expression> list = new ArrayList<Expression>(); }
		e1=atom 
		{ list.add($e1.val); }
		('|' e2=atom
		{ list.add($e2.val); }
		)*
		{ $val = Expression.buildParallel(list.toArray(new Expression[0])); }
	;	

expression returns [Query val]
	: 	{ ArrayList<Expression> list = new ArrayList<Expression>(); }
		(p1=parallel 
		{ list.add($p1.val); }
		)+	
		{ $val = Expression.buildQuery(Expression.buildConcat(list.toArray(new Expression[0]))); }
		EOF
	;	