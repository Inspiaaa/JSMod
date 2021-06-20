package com.la.jsmod;

import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.exceptions.JavetCompilationException;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.exceptions.JavetExecutionException;
import com.caoccao.javet.exceptions.JavetScriptingError;
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.V8ScriptOrigin;
import com.caoccao.javet.values.reference.V8ValueObject;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

public class JavetTest {
    public static JavetTest instance;

    public V8Runtime runtime;

    public JavetTest() {
        MinecraftForge.EVENT_BUS.register(this);
        instance = this;
    }

    public void testEvalOnePlusOne() {
        try {
            Integer onePlusOne = runtime.getExecutor("1 + 1").executeInteger();
            JSMod.logger.info("1 + 1 = ");
            JSMod.logger.info(onePlusOne);
        }
        catch (JavetException e) {
            e.printStackTrace();
        }
    }

    public void testFunction() {
        try {
            runtime.execute("function fun() { return 5; }", new V8ScriptOrigin("My script"), false);
            Integer result = runtime.getGlobalObject().invokeInteger("fun");
            JSMod.logger.info("Running a function that returns 5:");
            JSMod.logger.info(result);
        }
        catch (JavetException e) {
            e.printStackTrace();
        }
    }

    public static class SayInterceptor {
        @V8Function()
        public void say(String message) {
            JSMod.logger.info("From JS: " + message);
        }
    }

    public void testJsToJava() {
        try {
            V8ValueObject obj = runtime.createV8ValueObject();
            obj.bind(new SayInterceptor());
            runtime.getGlobalObject().set("Java", obj);
            runtime.getExecutor("Java.say('Hello world!')").executeVoid();
            runtime.getGlobalObject().delete(obj);
            obj.close();
        }
        catch (JavetException e) {
            e.printStackTrace();
        }
    }

    public void testJsError() {
        try {
            JSMod.logger.info("Intentionally producing an error");
            runtime.getExecutor("null.a").executeVoid();
        }
        catch (JavetException e) {
            e.printStackTrace();
        }
        try {
            runtime.execute("null.a", new V8ScriptOrigin("long_path/test_file.js"), false);
        }
        catch (JavetExecutionException e) {
            JSMod.logger.info(e.getScriptingError().getMessage());
        }
        catch (JavetException e) {
            e.printStackTrace();
        }
    }

    public void testLocalGcObjects(int n) {
        for (int i = 0; i < n; i ++) {
            try {
                V8ValueObject obj = runtime.createV8ValueObject();
                obj.set("field", "hello");
                obj.close();
            }
            catch (JavetException e) {
                e.printStackTrace();
            }
        }
    }

    public void testReturnedGcObjects(int n) {
        for (int i = 0; i < n; i ++) {
            try {
                V8ValueObject obj = runtime.createV8ValueObject();
                obj.setWeak();

                runtime.getGlobalObject().set("Obj", obj);
                runtime.getGlobalObject().delete("Obj");
            }
            catch (JavetException e) {
                e.printStackTrace();
            }
        }
    }

    public class GetObjectInterceptor {
        @V8Function()
        public V8ValueObject getMessage() {
            try {
                V8ValueObject obj = runtime.createV8ValueObject();
                obj.set("message", "Hello world!");
                obj.setWeak();
                return obj;
            }
            catch (JavetException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void testJsToJavaToJs() {
        try {
            V8ValueObject obj = runtime.createV8ValueObject();
            obj.bind(new GetObjectInterceptor());
            runtime.getGlobalObject().set("Java", obj);
            JSMod.logger.info("Js -> Java -> Js: Getting an obj from Java via a call");
            JSMod.logger.info(runtime.getExecutor("JSON.stringify(Java.getMessage())").executeString());
            runtime.getGlobalObject().delete(obj);
            obj.close();
        }
        catch (JavetException e) {
            e.printStackTrace();
        }
    }

    public void createRuntime() {
        JSMod.logger.info("Creating V8 Runtime");
        try {
            runtime = V8Host.getV8Instance().createV8Runtime();

            testEvalOnePlusOne();
            testFunction();
            testJsToJava();
            testJsToJavaToJs();
            testJsError();

        } catch (JavetException e) {
            e.printStackTrace();
        }
    }

    public void releaseRuntime() {
        try {
            runtime.close();
        } catch (JavetException e) {
            e.printStackTrace();
        }
        JSMod.logger.info("Releasing V8 Runtime");
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (event.side != Side.CLIENT)
            return;

        if (event.phase != TickEvent.Phase.END)
            return;

        // Run something
        // testGCObjects(1000);
        // testReturnedGcObjects(1000);
    }
}
