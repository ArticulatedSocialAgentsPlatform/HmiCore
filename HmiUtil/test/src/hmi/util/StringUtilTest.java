/*******************************************************************************
 * The MIT License (MIT)
 * Copyright (c) 2015 University of Twente
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
/*
 * StringUtil JUnit test
 */

package hmi.util;

import static org.junit.Assert.*;
import org.junit.*;

/**
 * JUnit test for StringUtil
 */
public class StringUtilTest {
    
    public StringUtilTest() {
    }

    @Before
    public void setUp()  { // common initialization, executed for every test.
    }

    @After
    public void tearDown() {
    }

    @Test
    /* test the diff method */
    public void diffTest() {     
      // null strings: diff pos not defined, return -1
       String s1 = null;
       String s2 = null;
       int pos = StringUtil.diff(s1, s2);
       assertTrue(pos == -1);  
       StringUtil.TextPos textPos = StringUtil.diffPos(s1, s2);
       assertTrue(textPos.equals(new StringUtil.TextPos()));
       
       // empty strings: diff pos not defined, return -1
       s1 = "";
       s2 = "";
       pos = StringUtil.diff(s1, s2);
       assertTrue(pos == -1);
       textPos = StringUtil.diffPos(s1, s2);
       assertTrue(textPos.equals(new StringUtil.TextPos()));
       
       // one empty strings: diff pos is first char of non-empty string = 0
       s1 = "";
       s2 = "aap";
       pos = StringUtil.diff(s1, s2);
       assertTrue(pos == 0);
       textPos = StringUtil.diffPos(s1, s2);
       assertTrue(textPos.equals(new StringUtil.TextPos(0, 0)));
       
       // one string is prefix of the other: dif pos = length of shortest string = 2
       s1 = "aa";
       s2 = "aap";
       pos = StringUtil.diff(s1, s2);
       assertTrue(pos == 2);
       textPos = StringUtil.diffPos(s1, s2);
       assertTrue(textPos.equals(new StringUtil.TextPos(0,2)));
       
       // same situation, with arguments reversed
       s1 = "aap";
       s2 = "aa";
       pos = StringUtil.diff(s1, s2);
       assertTrue(pos == 2);
       textPos = StringUtil.diffPos(s1, s2);
       assertTrue(textPos.equals(new StringUtil.TextPos(0,2)));
       
       // equal length strings, differing at pos 0
       s1 = "abc";
       s2 = "xap";
       pos = StringUtil.diff(s1, s2);
       assertTrue(pos == 0);
       textPos = StringUtil.diffPos(s1, s2);
       assertTrue(textPos.equals(new StringUtil.TextPos(0,0)));
       
       // equal length strings, differing at pos 1
       s1 = "abc";
       s2 = "aap";
       pos = StringUtil.diff(s1, s2);
       assertTrue(pos == 1);
       textPos = StringUtil.diffPos(s1, s2);
       assertTrue(textPos.equals(new StringUtil.TextPos(0,1)));
       
       // equal length strings, differing at pos 2
       s1 = "abc";
       s2 = "abx";
       pos = StringUtil.diff(s1, s2);
       assertTrue(pos == 2);
       textPos = StringUtil.diffPos(s1, s2);
       assertTrue(textPos.equals(new StringUtil.TextPos(0,2)));
       
       // unequal length strings, differing at pos 1
       s1 = "abc";
       s2 = "aapnoot";
       pos = StringUtil.diff(s1, s2);
       assertTrue(pos == 1);
       textPos = StringUtil.diffPos(s1, s2);
       assertTrue(textPos.equals(new StringUtil.TextPos(0,1)));
       
       // unequal length strings, differing at pos 1
       s1 = "abcd";
       s2 = "aap";
       pos = StringUtil.diff(s1, s2);
       assertTrue(pos == 1);
       textPos = StringUtil.diffPos(s1, s2);
       assertTrue(textPos.equals(new StringUtil.TextPos(0,1)));
       
       // unequal length strings, differing at pos 0
       s1 = "abcd";
       s2 = "xap";
       pos = StringUtil.diff(s1, s2);
       assertTrue(pos == 0);
       textPos = StringUtil.diffPos(s1, s2);
       assertTrue(textPos.equals(new StringUtil.TextPos(0,0)));
       
       // equal strings: return -1
       s1 = "abc";
       s2 = "abc";
       pos = StringUtil.diff(s1, s2);
       assertTrue(pos == -1);
       textPos = StringUtil.diffPos(s1, s2);
       assertTrue(textPos.equals(new StringUtil.TextPos()));
       
       
       // unequal strings, containing newline chars
       s1 = "abc\nxyz\n123\naap";
       s2 = "abc\nxyz\n156\naap";
       pos = StringUtil.diff(s1, s2);
       assertTrue(pos == 9);
       textPos = StringUtil.diffPos(s1, s2);
       assertTrue(textPos.equals(new StringUtil.TextPos(2, 1))); // line 2, position 1
       
    } 

    @Test
    /* test the getLineNumber method */
    public void getLineNumberTest() {     
       assertTrue(StringUtil.getLineNumber(null, 0) == -1);  // line number of null string not defined
       assertTrue(StringUtil.getLineNumber("", 0) == -1);    // line number of empty string not defined
       assertTrue(StringUtil.getLineNumber("x", 0) == 0);    // line number of pos 0 = 0
       assertTrue(StringUtil.getLineNumber("xyx\nabc\naap", 0) == 0);   // first character, line 0
       assertTrue(StringUtil.getLineNumber("xyx\nabc\naap", 1) == 0); 
       assertTrue(StringUtil.getLineNumber("xyx\nabc\naap", 2) == 0);
       assertTrue(StringUtil.getLineNumber("xyx\nabc\naap", 3) == 0); // the \n still belongs to line 0
       assertTrue(StringUtil.getLineNumber("xyx\nabc\naap", 4) == 1);
       assertTrue(StringUtil.getLineNumber("xyx\nabc\naap", 5) == 1);
       assertTrue(StringUtil.getLineNumber("xyx\nabc\naap", 6) == 1);  
       assertTrue(StringUtil.getLineNumber("xyx\nabc\naap", 7) == 1);  // second \n char, line 1
       assertTrue(StringUtil.getLineNumber("xyx\nabc\naap", 8) == 2); 
       assertTrue(StringUtil.getLineNumber("xyx\nabc\naap", 9) == 2); 
       assertTrue(StringUtil.getLineNumber("xyx\nabc\naap", 10) == 2);  // last character,  line 2
       assertTrue(StringUtil.getLineNumber("xyx\nabc\naap", 11) == -1);  // position out of range    
    }
    
    @Test
    /* test the getLinePos method */
    public void getLinePosTest() {     
       assertTrue(StringUtil.getLinePos(null, 0) == -1);  // line number of null string not defined
       assertTrue(StringUtil.getLinePos("", 0) == -1);    // line number of empty string not defined
       assertTrue(StringUtil.getLinePos("x", 0) == 0);    // line number of pos 0 = 0
       assertTrue(StringUtil.getLinePos("xyx\nabc\naap", 0) == 0);   // first character, line 0
       assertTrue(StringUtil.getLinePos("xyx\nabc\naap", 1) == 1); 
       assertTrue(StringUtil.getLinePos("xyx\nabc\naap", 2) == 2);
       assertTrue(StringUtil.getLinePos("xyx\nabc\naap", 3) == 3); // the \n still belongs to line 0
       assertTrue(StringUtil.getLinePos("xyx\nabc\naap", 4) == 0);
       assertTrue(StringUtil.getLinePos("xyx\nabc\naap", 5) == 1);
       assertTrue(StringUtil.getLinePos("xyx\nabc\naap", 6) == 2);  
       assertTrue(StringUtil.getLinePos("xyx\nabc\naap", 7) == 3);  
       assertTrue(StringUtil.getLinePos("xyx\nabc\naap", 8) == 0); 
       assertTrue(StringUtil.getLinePos("xyx\nabc\naap", 9) == 1); 
       assertTrue(StringUtil.getLinePos("xyx\nabc\naap", 10) == 2);  // last character,  
       assertTrue(StringUtil.getLinePos("xyx\nabc\naap", 11) == -1);  // position out of range    
    }

    @Test
    /* test the getLinePos method */
    public void getTextPosTest() {     
//       assertTrue(StringUtil.getLinePos(null, 0) == -1);  // line number of null string not defined
//       assertTrue(StringUtil.getLinePos("", 0) == -1);    // line number of empty string not defined
//       assertTrue(StringUtil.getLinePos("x", 0) == 0);    // line number of pos 0 = 0

       assertTrue(StringUtil.getTextPos(null, 0).equals(new StringUtil.TextPos())); // undefined position
       assertTrue(StringUtil.getTextPos("", 0).equals(new StringUtil.TextPos()));   // undefined position
              
       assertTrue(StringUtil.getTextPos("x", -1).equals(new StringUtil.TextPos()));   // undefined position    
       assertTrue(StringUtil.getTextPos("x", 0).equals(new StringUtil.TextPos(0, 0))); 
       assertTrue(StringUtil.getTextPos("x", 1).equals(new StringUtil.TextPos()));    // undefined position

       assertTrue(StringUtil.getTextPos("xyx\nabc\naap", 0).equals(new StringUtil.TextPos(0, 0)));   // first character, line 0
       assertTrue(StringUtil.getTextPos("xyx\nabc\naap", 1).equals(new StringUtil.TextPos(0, 1)));
       assertTrue(StringUtil.getTextPos("xyx\nabc\naap", 2).equals(new StringUtil.TextPos(0, 2)));
       assertTrue(StringUtil.getTextPos("xyx\nabc\naap", 3).equals(new StringUtil.TextPos(0, 3))); // \n still belongs to line 0
       assertTrue(StringUtil.getTextPos("xyx\nabc\naap", 4).equals(new StringUtil.TextPos(1, 0)));
       assertTrue(StringUtil.getTextPos("xyx\nabc\naap", 5).equals(new StringUtil.TextPos(1, 1)));
       assertTrue(StringUtil.getTextPos("xyx\nabc\naap", 6).equals(new StringUtil.TextPos(1, 2)));
       assertTrue(StringUtil.getTextPos("xyx\nabc\naap", 7).equals(new StringUtil.TextPos(1, 3)));  // second \n, line 1
       assertTrue(StringUtil.getTextPos("xyx\nabc\naap", 8).equals(new StringUtil.TextPos(2, 0)));
       assertTrue(StringUtil.getTextPos("xyx\nabc\naap", 9).equals(new StringUtil.TextPos(2, 1)));
       assertTrue(StringUtil.getTextPos("xyx\nabc\naap", 10).equals(new StringUtil.TextPos(2, 2)));
       assertTrue(StringUtil.getTextPos("xyx\nabc\naap", 11).equals(new StringUtil.TextPos(-1, -1))); // position out of range
    }


  
}



