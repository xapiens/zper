package org.zeromq.zper;

import static org.junit.Assert.*;

import java.util.Properties;

import org.jeromq.ZLog;
import org.jeromq.ZLogManager;
import org.jeromq.ZMQ;
import org.jeromq.ZMQ.Context;
import org.jeromq.ZMQ.Socket;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestZPFront
{
    private static ZPFront server;
    private static String bind = "tcp://*:5555";
    private static String topic = "test";

    @BeforeClass
    public static void start () throws Exception {
        
        Properties conf = new Properties ();
        conf.setProperty ("front.bind", bind);
        conf.setProperty ("base_dir", "/tmp/zlogs/");
        
        server = new ZPFront (conf);
        server.start();
        
    }
    
    @AfterClass
    public static void tearDown () throws Exception {
        server.shutdown ();
    }
    
    @Test
    public void testSend () throws Exception {
        Context ctx = ZMQ.context (1);
        Socket sock = ctx.socket (ZMQ.DEALER);
        
        ZLog zlog = ZLogManager.instance().get(topic);
        long offset = zlog.offset();
        String data = "hello";
        
        System.out.println("previous offset " + zlog.path ().getAbsolutePath () + ":" + offset);

        sock.setIdentity (ZPUtils.genTopicIdentity (topic));
        sock.setLinger (100);

        boolean ret = sock.connect ("tcp://127.0.0.1:5555");
        assertTrue (ret);


        ret = sock.send (data);
        assertTrue (ret);

        // wait until flush
        Thread.sleep (1000);

        assertEquals (zlog.offset(), offset + data.length () + 2);
        
        sock.close ();
        ctx.term ();
    }
    
    @Test
    public void testSendPush () throws Exception {
        Context ctx = ZMQ.context (1);
        Socket sock = ctx.socket (ZMQ.PUSH);
        
        ZLog zlog = ZLogManager.instance().get(topic);
        long offset = zlog.offset();
        String data = "hello";
        
        System.out.println("previous offset " + zlog.path ().getAbsolutePath () + ":" + offset);

        sock.setIdentity (ZPUtils.genTopicIdentity (topic));
        sock.setLinger (100);

        boolean ret = sock.connect ("tcp://127.0.0.1:5555");
        assertTrue (ret);


        ret = sock.send (data);
        assertTrue (ret);

        // wait until flush
        Thread.sleep (1000);

        assertEquals (zlog.offset(), offset + data.length () + 2);
        
        sock.close ();
        ctx.term ();
    }

}
