package com.fun.frame.thread;

import com.fun.base.constaint.ThreadLimitTimesCount;
import com.fun.base.interfaces.MarkThread;
import com.fun.frame.httpclient.FanLibrary;
import com.fun.frame.httpclient.FunRequest;
import com.fun.frame.httpclient.GCThread;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.http.client.methods.HttpRequestBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * http请求多线程类
 */
@SuppressFBWarnings("CN_IMPLEMENTS_CLONE_BUT_NOT_CLONEABLE")
public class RequestThreadTimes<T extends HttpRequestBase> extends ThreadLimitTimesCount<HttpRequestBase> {

    static Logger logger = LoggerFactory.getLogger(RequestThreadTimes.class);

    /**
     * 单请求多线程多次任务构造方法
     *
     * @param request 被执行的请求
     * @param times   每个线程运行的次数
     */
    public RequestThreadTimes(HttpRequestBase request, int times) {
        super(request, times, null);
    }

    /**
     * 应对对每个请求进行标记的情况
     *
     * @param request
     * @param times
     * @param mark
     */
    public RequestThreadTimes(HttpRequestBase request, int times, MarkThread mark) {
        super(request, times, mark);
    }

    protected RequestThreadTimes() {
        super();
    }

    @Override
    public void before() {
        super.before();
        GCThread.starts();
    }

    /**
     * @throws Exception
     */
    @Override
    protected void doing() throws Exception {
        FanLibrary.executeSimlple(t);
    }

    @Override
    public RequestThreadTimes clone() {
        RequestThreadTimes threadTimes = new RequestThreadTimes();
        threadTimes.times = this.times;
        threadTimes.t = FunRequest.cloneRequest(t);
        threadTimes.mark = mark == null ? null : mark.clone();
        return threadTimes;
    }


}
