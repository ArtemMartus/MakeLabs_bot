/*
 * Copyright (c) 2019.  Artem Martus (upsage) All Rights Reserved
 */

package main.makelabs_bot.helper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InnerPathTest {

    @Test
    void getLast() {
        assertEquals("aqe", new InnerPath("/gd/ad/aqe").getLast());
        assertEquals("aqe", new InnerPath("/aqe").getLast());
        assertEquals("aqe", new InnerPath("aqe").getLast());
    }

    @Test
    void getPath() {
        assertEquals("/path/to/data", new InnerPath("/path/to/data").getPath());
    }

    @Test
    void isWorkData() {
        //todo make this test
    }

    @Test
    void goBack() {
        assertEquals("/hello", new InnerPath("/hello/world").goBack());
        assertEquals("/", new InnerPath("")); //todo finish this
    }

    @Test
    void goHome() {
    }

    @Test
    void isAbsolute() {
    }

    @Test
    void setData() {
    }
}