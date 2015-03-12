package hdgl.db.query.parser;

import static org.junit.Assert.*;

import java.util.Arrays;

import hdgl.db.query.condition.AbstractCondition;
import hdgl.db.query.condition.AbstractCondition.ConditionRelationship;
import hdgl.db.query.condition.EqualTo;
import hdgl.db.query.condition.IntNumberValue;
import hdgl.db.query.condition.LargerThanOrEqualTo;
import hdgl.db.query.condition.LessThan;
import hdgl.db.query.condition.NoRestriction;
import hdgl.db.query.condition.NotEqualTo;
import hdgl.db.query.condition.OfType;
import hdgl.db.query.convert.SortConditions;
import org.junit.Test;

public class ConditionTest {

	@Test
	public void test() {
		AbstractCondition c1 = new EqualTo("a", new IntNumberValue(1));
		AbstractCondition c11 = new EqualTo("a", new IntNumberValue(1));
		AbstractCondition c2 = new LessThan("a", new IntNumberValue(100));
		AbstractCondition c3 = new LargerThanOrEqualTo("a", new IntNumberValue(1));
		AbstractCondition c4 = new OfType("test");
		AbstractCondition c6 = new OfType("test2");
		AbstractCondition c5 = new NotEqualTo("a", new IntNumberValue(1));
		AbstractCondition c7 = new NoRestriction();
		assertEquals(ConditionRelationship.Equivalent, c1.relationship(c1));
		assertEquals(ConditionRelationship.Equivalent, c1.relationship(c11));
		assertEquals(ConditionRelationship.Require, c1.relationship(c2));
		assertEquals(ConditionRelationship.Sufficient, c2.relationship(c1));
		assertEquals(ConditionRelationship.Require, c1.relationship(c3));
		assertEquals(ConditionRelationship.Sufficient, c3.relationship(c1));
		assertEquals(ConditionRelationship.NotRelevant, c3.relationship(c4));
		
		AbstractCondition[] sorted = SortConditions.sortConditions(new AbstractCondition[]{c1,c11,c2,c3,c4,c5,c6,c7});
		System.out.println(Arrays.toString(sorted));
	}

}
