package org.zper.perf;

import java.util.Properties;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;
import org.zper.ZPUtils;

public class Producer
{
    private ZContext ctx;
    private Socket sock;

    public Producer(String topic, Properties props)
    {
        ctx = new ZContext();
        ctx.setLinger(-1);
        sock = ctx.createSocket(ZMQ.DEALER);
        sock.setSndHWM(0);
        sock.setIdentity(ZPUtils.genTopicIdentity(topic, 0));
        for (String addr : props.getProperty("writer.list").split(",")) {
            sock.connect("tcp://" + addr);
        }
    }

    public void send(Message message)
    {
        boolean rc;
        rc = sock.sendMore(message.getHeader());
        if (!rc)
            assert (false);
        rc = sock.send(message.getPayload());
        if (!rc)
            assert (false);

    }

    public void close()
    {
        ctx.destroy();
    }

}
