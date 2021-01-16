/*
    아래 방식은 블로킹 방식으로, 일정 수준의 동시 클라이언트를 지원할 수 있다.
    그러나 동시 접속자가 수만명까지 증가한다면 정상적으로 동작하지 않을 가능성이 높다.
 */

package plain.plainoioserver;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class PlainOioServer {

    public void serve(int port) throws IOException {
        final ServerSocket serverSocket = new ServerSocket(port);

        try {
            for (;;) {
                final Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connection from " + clientSocket);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OutputStream out;

                        try {
                            out = clientSocket.getOutputStream();

                            out.write("Hi!\r\n".getBytes(StandardCharsets.UTF_8));
                            out.flush();
                            clientSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                clientSocket.close();
                            } catch (IOException ex) {
                                // 종료 시 무시
                            }
                        }
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
