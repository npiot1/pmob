package com.example.piotn.pmob_td;

import java.util.concurrent.ThreadFactory;

public class ImageThreadFactory implements ThreadFactory {
    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        t.setPriority(5); // ou tout autre acte de configuration
        return t;
    }
}
