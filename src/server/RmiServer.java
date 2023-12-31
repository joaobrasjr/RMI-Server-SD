package server;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.net.*;
import java.util.*;

import interfaces.ReceiveMessageInterface;

public class RmiServer extends java.rmi.server.UnicastRemoteObject implements ReceiveMessageInterface {
    private int id;
    private int thisPort;
    private String thisAddress;
    Registry registry;
    private String database;
    Conexao conecta = new Conexao();
    ArrayList<RmiServer> servers = new ArrayList<RmiServer>();

    public RmiServer(Integer porta, int id, String dataBase) throws RemoteException {
        try {
            this.thisAddress = (InetAddress.getLocalHost()).toString();
            this.id = id;
            this.database = dataBase;
            this.thisPort = porta;
        } catch (Exception e) {
            throw new RemoteException("can't get inet address.");
        }

        try {
            registry = LocateRegistry.createRegistry(thisPort);
            registry.rebind("rmiServer", this); //Atualiza o registro do servidor
            System.out.println("Conectado address=" + this.thisAddress + "- port=" + this.thisPort);
        } catch (RemoteException e) {
            throw e;
        }
    }

    public int receiveMessage(String comandoSql) throws RemoteException {
        int status = this.sendCommand(comandoSql); // 208
        if (status != 200) {
            System.out.println("Erro maquina principal");
            System.out.println();
            return status;
        }

        for (int i = 0; i < servers.size(); i++) {
            status = servers.get(i).sendCommand(comandoSql);
            if (status == 200)
                continue;
            else {
                System.out.printf("Erro na maquina: %d", servers.get(i).id);
                System.out.println();
                servers.remove(i);
            }
        }

        for (int i = 0; i < servers.size(); i++) {
            System.out.printf("Maquina: %d", servers.get(i).id);
            System.out.println();
        }
        return 200;
    }

    private int sendCommand(String comandoSql) {
        try (Connection con = conecta.Conexao("jdbc:sqlserver://localhost:1433;", "sa", "joao1234", database)) {
            Statement stmt;
            try {
                // con.setAutoCommit(false);
                stmt = con.createStatement(); //permite a execução de instruções SQL
                stmt.execute(comandoSql);
                // stmt.executeUpdate(comandoSql);
                con.close();
                return 200;
            } catch (SQLException e) {
                System.out.printf("erro na maquina %d", this.id);
                System.out.println();
                return e.getErrorCode();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            return 400;
        }
    }

    public void addNewMember(RmiServer server) {
        this.servers.add(server);
    }

    public int getId() {
        return id;
    }

    public int getThisPort() {
        return thisPort;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLider() {
        RmiServer server = null;

        for (int i = 0; i < servers.size(); i++) {
            if (server == null) {
                server = servers.get(i);
            } else if (servers.get(i).id > server.id) {
                server = servers.get(i);
            }
        }
        this.id = 0;
        return server.thisPort;
    }

}