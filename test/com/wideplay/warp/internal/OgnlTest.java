package com.wideplay.warp.internal;

import ognl.Ognl;
import ognl.OgnlException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * On: 27/03/2007
 *
 * @author Dhanji R. Prasanna
 * @since 1.0
 */
public class OgnlTest {
    private List<String> strings = new ArrayList<String>();
    private String myString;
    private boolean primBool;
    private Boolean wrapBool;

    @BeforeClass
    public void startup() {
        strings.add("yahop");
        strings.add("goog");
        strings.add("asdasdp");
    }

    @Test
    public final void testSetValue() throws OgnlException {
        Object value = "goog".hashCode();

        Ognl.setValue("myString", this, value);
        System.out.println(myString);

        System.out.println(Ognl.getValue("myString = strings.{? #this.hashCode() == " + value + "}[0]", this));
        System.out.println(myString);
    }

    @Test
    public final void testSetPrimitiveBoolean() throws OgnlException {
        primBool = false;
        Ognl.setValue("primBool", this, "true");
        assert primBool;

        primBool = true;
        Ognl.setValue("primBool", this, "false");
        assert !primBool : "false was not set";
    }

    @Test
    public final void testSetWrapperBoolean() throws OgnlException {
        wrapBool = false;
        Ognl.setValue("wrapBool", this, "true");
        assert wrapBool;

        wrapBool = true;
        Ognl.setValue("wrapBool", this, "false");
        assert !wrapBool : "false was not set";
    }

    
    public String getMyString() {
        return myString;
    }

    public void setMyString(String myString) {
        this.myString = myString;
    }

    public List<String> getStrings() {
        return strings;
    }

    public void setStrings(List<String> strings) {
        this.strings = strings;
    }


    public boolean isPrimBool() {
        return primBool;
    }

    public void setPrimBool(boolean primBool) {
        this.primBool = primBool;
    }

    public Boolean getWrapBool() {
        return wrapBool;
    }

    public void setWrapBool(Boolean wrapBool) {
        this.wrapBool = wrapBool;
    }
}
