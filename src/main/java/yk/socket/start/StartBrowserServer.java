package yk.socket.start;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.stereotype.Service;

import yk.socket.browserServer.BrowserServerThread;

@Service
public class StartBrowserServer {
	BrowserServerThread server = null;
	ExecutorService es = null;

	@PostConstruct
	private void start() {
		server = new BrowserServerThread(8888);
		es = Executors.newCachedThreadPool();
		es.execute(server);
		System.out.println("StartBrowserServer...");
	}

	@PreDestroy
	private void destroy() {
		if (server != null) {
			server.shutdown();
			server = null;
		}
		if (es != null) {
			es.shutdownNow();
		}
		System.out.println("netty server is shutdown..");
	}
}
