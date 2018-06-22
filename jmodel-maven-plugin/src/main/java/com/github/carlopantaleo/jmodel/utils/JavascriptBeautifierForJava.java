package com.github.carlopantaleo.jmodel.utils;

import com.google.common.io.Resources;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Javascript Beautifier caller.
 *
 * See https://gist.github.com/fedochet/d41442e735eaa277937a094ab8e5fc8f
 *
 * @author fedochet
 */

public class JavascriptBeautifierForJava {

    // my javascript beautifier of choice
    private static final String BEAUTIFY_JS_RESOURCE = "beautify.js";

    // name of beautifier function
    private static final String BEAUTIFY_METHOD_NAME = "js_beautify";
    private final ScriptEngine engine;

    public JavascriptBeautifierForJava() throws ScriptException, FileNotFoundException {
        engine = new ScriptEngineManager().getEngineByName("nashorn");

        // this is needed to make self invoking function modules work
        // otherwise you won't be able to invoke your function
        engine.eval("var global = this;");

        URL resource = Resources.getResource(BEAUTIFY_JS_RESOURCE);
        if (resource == null) {
            throw new FileNotFoundException("Could not find " + BEAUTIFY_JS_RESOURCE + " script in classpath");
        }

        try (InputStreamReader isr = new InputStreamReader(resource.openStream())) {
            engine.eval(isr);
        } catch (IOException e) {
            throw new ScriptException("Could not execute beautify script: " + e.getMessage());
        }
    }

    public String beautify(String javascriptCode) throws ScriptException, NoSuchMethodException {
        return (String) ((Invocable) engine).invokeFunction(BEAUTIFY_METHOD_NAME, javascriptCode);
    }
}