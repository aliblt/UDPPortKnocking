import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class Client {

    public static void main(String[] args) throws IOException {
        //First args is Server port number

        if (args.length < 1) {
            System.out.println("Insufficient # of args");
            return;
        }

        InetAddress address = InetAddress.getLocalHost();
        int serverPortNum = Integer.parseInt(args[0]);

        DatagramSocket socket = new DatagramSocket();
        int clientPortNum = socket.getLocalPort();


        //Send 3 packets to server in order to activate
        for (int i=0 ; i<1 ; i++ ) {

            byte[] queryBuff = String.valueOf(i).getBytes();
            DatagramPacket query = new DatagramPacket(queryBuff, queryBuff.length, address, serverPortNum);

            socket.send(query);
        }

        System.out.println("Initial Messages has sent ");


        byte[] buff = new byte[UDP.MAX_DATAGRAM_SIZE];
        DatagramPacket packet = new DatagramPacket(buff, buff.length);

        socket.receive(packet);

        String str = new String(packet.getData(), 0, packet.getLength()).trim();
        int serverDataPort = Integer.parseInt(str);

        System.out.println("Received Port Number: "  + serverDataPort);

        Socket dataSocket = new Socket("localhost", serverDataPort);
        InputStream is = dataSocket.getInputStream();
        BufferedReader incomingData = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));

        String fileName = incomingData.readLine();
        int dataSize = Integer.parseInt(incomingData.readLine());

        System.out.println("Incoming data Name: " + fileName);
        System.out.println("Data Size: " + dataSize);

        FileOutputStream fos = new FileOutputStream("ClientData/"+fileName);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        byte[] fileContent = is.readNBytes(dataSize);
        System.out.println(fileContent + " - " + fileContent.length);
        bos.write(fileContent,0,fileContent.length);
        bos.flush();
        fos.close();
        bos.close();

        dataSocket.close();
        socket.close();
    }

}