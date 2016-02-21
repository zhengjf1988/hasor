package test.net.hasor.rsf._04_protocol;
import java.io.IOException;
import java.util.Date;
import org.junit.Test;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import net.hasor.core.Settings;
import net.hasor.core.setting.StandardContextSettings;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.domain.RSFConstants;
import net.hasor.rsf.rpc.context.DefaultRsfSettings;
import net.hasor.rsf.serialize.SerializeCoder;
import net.hasor.rsf.serialize.SerializeFactory;
import net.hasor.rsf.transform.codec.ProtocolUtils;
import net.hasor.rsf.transform.protocol.RequestInfo;
/**
 * 
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class TransformTest {
    @Test
    public void request() throws Throwable {
        Settings setting = new StandardContextSettings();//create Settings
        setting.refresh();
        RsfSettings rsfSetting = new DefaultRsfSettings(setting);//create RsfSettings
        SerializeFactory factory = SerializeFactory.createFactory(rsfSetting);
        SerializeCoder coder = factory.getSerializeCoder("java");
        //
        //--------------------------------------------------------------------
        byte[] rsfData = this.write(coder);
        RequestInfo request = read(rsfData);
        //
        System.out.println(request.getServiceName());
    }
    //
    public byte[] write(SerializeCoder coder) throws Throwable {
        RequestInfo request = new RequestInfo();
        request.setRequestID(1234567890L);
        request.setServiceGroup("RSF");
        request.setServiceName("test.net.hasor.rsf.services.EchoService");
        request.setServiceVersion("1.0.0");
        request.setSerializeType("serializeType");
        request.setTargetMethod("targetMethod");
        request.setClientTimeout(6000);
        request.setReceiveTime(System.currentTimeMillis());
        //
        request.addParameter("java.lang.String", coder.encode("say Hello."));
        request.addParameter("java.lang.Long", coder.encode(111222333444555L));
        request.addParameter("java.util.Date", coder.encode(new Date()));
        request.addParameter("java.lang.Object", coder.encode(null));
        //
        request.addOption("auth", "yes");
        request.addOption("user", "guest");
        request.addOption("password", null);
        //
        ByteBuf dataBuf = ByteBufAllocator.DEFAULT.heapBuffer();
        ProtocolUtils.wirteRequestInfo(RSFConstants.Version_1, request, dataBuf);
        byte[] rsfData = dataBuf.array();
        return rsfData;
    }
    public RequestInfo read(byte[] rsfData) throws IOException {
        //
        ByteBuf dataBuf = ByteBufAllocator.DEFAULT.heapBuffer();
        dataBuf.writeBytes(rsfData);
        RequestInfo request = ProtocolUtils.buildRequestInfo(RSFConstants.Version_1, dataBuf);
        return request;
    }
}