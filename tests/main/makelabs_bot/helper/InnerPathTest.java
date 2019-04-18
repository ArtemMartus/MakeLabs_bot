/*
 * Copyright (c) 2019.  Artem Martus (upsage) All Rights Reserved
 */

package main.makelabs_bot.helper;

import main.makelabs_bot.model.DatabaseManager;
import main.makelabs_bot.model.data_pojo.PostWorkData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InnerPathTest {

    private static DatabaseManager databaseManager;

    @BeforeEach
    void setUp() {
        Log.setShowLevel(Log.EVERYTHING);
        DatabaseManager.databaseName = "testDatabase";
        databaseManager = DatabaseManager.getInstance();
    }

    @Test
    void getLast() {
        assertEquals("aqe", new InnerPath("/gd/ad/aqe").getLast());
        assertEquals("aqe", new InnerPath("/aqe").getLast());
        assertEquals("aqe", new InnerPath("aqe").getLast());
        assertEquals("", new InnerPath("").getLast());
        assertEquals("", new InnerPath("/").getLast());
    }

    @Test
    void getPath() {
        assertEquals("/path/to/data", new InnerPath("/path/to/data").getPath());
    }

    /// This test need DatabaseManager to work correctly
    @Test
    void isWorkData() {
        try {
            PostWorkData workData = new PostWorkData("[]", "test workData", 1L,
                    new InnerPath("/hello/test"));
            databaseManager.saveWorkData(workData);

            assertTrue(new InnerPath("/hello/test").isWorkData());
            assertFalse(new InnerPath("/test/hello/21dfwqr23").isWorkData());

            databaseManager.removeWorkData("/hello/test");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testCommandAdding() {
        assertEquals("/add", new InnerPath("/").addCommand("add"));
        assertEquals("/add", new InnerPath("/").addCommand("/add"));
        assertEquals("/add", new InnerPath("/add").addCommand(""));
        assertEquals("/add", new InnerPath("/add").addCommand("/"));
        assertEquals("/add/hello", new InnerPath("/add/").addCommand("/hello"));
        assertEquals("/", new InnerPath("/").addCommand(""));
        assertEquals("/", new InnerPath("/").addCommand("/"));
        assertEquals("/", new InnerPath("").addCommand(""));
    }


    @Test
    void goBack() {
        assertEquals("/hello", new InnerPath("/hello/world").goBack());
        assertEquals("hello", new InnerPath("hello/world").goBack());
        assertEquals("/", new InnerPath("/hello").goBack());
        assertEquals("", new InnerPath("hello").goBack());
        assertEquals("/", new InnerPath("/").goBack());
        assertEquals("", new InnerPath("").goBack());
    }

    @Test
    void goHome() {
        assertEquals("/", new InnerPath("/hello/world").goHome());
        assertEquals("/", new InnerPath("world").goHome());
    }

    @Test
    void isAbsolute() {
        assertTrue(new InnerPath("/this/is/absolute").isAbsolute());
        assertFalse(new InnerPath("not/actually/absolute").isAbsolute());
    }

    @Test
    void setData() {
        String data = "123de12g4f34g2";
        InnerPath path = new InnerPath(data);
        assertEquals(path.getPath(), data);
        data = "ergf431g314g1";
        path.setData(data);
        assertEquals(path.getPath(), data);
    }
}