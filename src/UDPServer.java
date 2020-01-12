import javax.xml.crypto.Data;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;


public class UDPServer {
    private List<DatagramSocket> serverPorts = new ArrayList<DatagramSocket>();

    public UDPServer(String[] ports) throws SocketException  {
        initializeServer(ports);
    }

    private void initializeServer(String[] ports) throws SocketException {
        for (int i=0 ; i<ports.length ; i++ ) {
            serverPorts.add(new DatagramSocket(Integer.parseInt(ports[i])));
            System.out.println("Server started to listens on: " + serverPorts.get(i).getLocalPort());
        }
    }

    private void service(DatagramSocket ds) {

        while (true) {
            try {
                portListen(ds);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void portListen(DatagramSocket ds ) throws IOException{
        byte[] buff = new byte[UDP.MAX_DATAGRAM_SIZE];
        final DatagramPacket datagram = new DatagramPacket(buff, buff.length);

        ds.receive(datagram);

        new Thread(() ->  {
            try {
                int n = Integer.parseInt(new String(datagram.getData(), 0, datagram.getLength()));
                System.out.println("I've got " + n);

                ServerSocket dataSocket = new ServerSocket(0);

                int dataPort = dataSocket.getLocalPort();

                System.out.println("New Data Port: " + dataPort);

                byte[] respBuff = String.valueOf(dataPort).getBytes();
                int clientPort = datagram.getPort();
                InetAddress clientAddress = datagram.getAddress();
                DatagramPacket resp = new DatagramPacket(respBuff, respBuff.length, clientAddress, clientPort);
                try {
                    ds.send(resp);
                    System.out.println("I've sent " + dataPort);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                File file = new File("ServerData/Lenna.png");
                String fileName = file.getName();
                int fileSize = (int) file.length();
                byte[] dataFileByte = new byte[fileSize];
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));

                Socket s = dataSocket.accept();
                PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                bis.read(dataFileByte, 0, fileSize);
                System.out.println("Sending Data Name and Size: " + fileName + " " + fileSize);
                out.println(fileName);
                out.println(fileSize);
                System.out.println(dataFileByte + "--" + dataFileByte.length);
                s.getOutputStream().write(dataFileByte,0,fileSize);
                s.close();
                bis.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void listen() {
        for( DatagramSocket ds: serverPorts ) {
            new Thread(() -> service(ds)).start();
        }
    }


    public static void main(String[] args) {
        try {
            new UDPServer(args).listen();
        } catch (SocketException e) {
            System.out.println("Could not set up the server");
        }
    }

}