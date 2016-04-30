package org.hpar;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by sxf on 4/30/16.
 */
public class Worker {
    ExecutorService threadPool = Executors.newCachedThreadPool();


}

class Job implements Runnable {

    @Override
    public void run() {

    }
}