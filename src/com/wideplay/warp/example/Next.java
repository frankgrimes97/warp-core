package com.wideplay.warp.example;

import com.google.inject.Inject;
import com.wideplay.warp.annotations.OnEvent;

/**
 * Created with IntelliJ IDEA.
 * On: 18/03/2007
 *
 * @author Dhanji R. Prasanna (dhanji at gmail com)
 * @since 1.0
 */
public class Next {
    private int number;
    
    @Inject 
    PageInjectDemo pageInjectDemo;


    @OnEvent @NextPage
    public Object onPageHandler() {
        System.out.println("Next.onPageHandler() invoked!");

        return pageInjectDemo;
    }


    //getters/setters---------
    public int getNumber() {
        return number++;
    }

    public void setNumber(int number) {
        this.number = number;
    }


    public PageInjectDemo getPageInjectDemo() {
        return pageInjectDemo;
    }
}
