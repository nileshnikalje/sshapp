package ssh;

import ch.ethz.ssh2.*;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.TimeLimiter;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Hello world!
 *
 */
public class App 
{
    private static Logger logger = Logger.getLogger(App.class.getName());
//    private String currentServerHostname = "13.126.113.214";
//    private String currentServerUser = "root";
//    private String currentServerPass = "test1";
    private String currentServerHostname = "localhost";
    private String currentServerUser = "root";
    private String currentServerPass = "IDM10gin$$";
    public static void main(String[] arg) throws Exception {
        String lastCommandOutput = null;


        // Setup ssh session with endpoint



        String tempCommand = "cat /etc/passwd";

        logger.info("sending command: " + tempCommand);

        Map outputMessages;



        outputMessages = new App().executeCommand("cat /etc/passwd");

        System.out.println("stdout for read :" + outputMessages.get("stdout"));
        System.out.println("stderr for read :" + outputMessages.get("stderr"));

        System.out.println("Creating user");
//       outputMessages = new App().createUser(new User("sshuser2","pass", "/home/xyz", "/bin/sh"));
        outputMessages = new App().changePassword("sshuser", "test8");

//        System.out.println("stdout for create :" + outputMessages.get("stdout"));
//        System.out.println("stderr for create :" + outputMessages.get("stderr"));
//
//        outputMessages = new App().executeCommand("pidksdfsd");
//
        System.out.println("stdout for createuser command :" + outputMessages.get("stdout"));
        System.out.println("stderr for createuser command :" + outputMessages.get("stderr"));
//
//
//
//        outputMessages = new App().changePassword("sshuser", "test1", "test2" );
//      //  outputMessages = new App().executeInteractive("passwd centos");
//
//        System.out.println("stdout for passwd command :" + outputMessages.get("stdout"));
//        System.out.println("stderr for passwd command :" + outputMessages.get("stderr"));


        outputMessages = new App().executeShellScript("shellScript.sh");

    }

    private Map executeShellScript(String s) throws Exception {

        String shellScriptFileName = this.getClass().getClassLoader().getResource(s).getPath();
        String commandToSend = new String(Files.readAllBytes(Paths.get(shellScriptFileName)));
        return executeCommand(commandToSend);
    }

    private Map executeInteractive(String command) throws Exception{

        StringBuilder stdout = new StringBuilder();
        StringBuilder stderr = new StringBuilder();
        Connection connection = new Connection(currentServerHostname);
        try {

            logger.info("starting connection with " + currentServerHostname);

            logger.info("connection object created..");

            connection.connect();

            connection.authenticateWithPassword(currentServerUser, currentServerPass);

            Session session = connection.openSession();
            session.execCommand("/bin/sh");
            session.close();

            session = connection.openSession();
            session.execCommand(command);

            InputStream stderrIs = new StreamGobbler(session.getStderr());

            BufferedReader stderrReader = new BufferedReader(new InputStreamReader(stderrIs));

            while (true) {
                String line = stderrReader.readLine();
                if (line == null)
                    break;
                stderr.append(line + "\n");
            }


//            session = connection.openSession();
//            session.requestDumbPTY();
//            session.startShell();
//
//            OutputStream stdin = session.getStdin();
//
//            InputStream stdoutIs = new StreamGobbler(session.getStdout());
//            BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(stdoutIs));
//
//            //stdin.write(("passwd sshuser" + '\n').getBytes());
//            stdin.write(command.getBytes());
//            while (true) {
//                String line = stdoutReader.readLine();
//                if (line == null || line.trim().equals("passwd: all authentication tokens updated successfully."))
//                    break;
//                if(line.trim().equals("passwd sshuser")) {
//                    stdin.write(("test5"+'\n').getBytes());
//                    stdin.write(("test5"+'\n').getBytes());
//                }
////                if(line.trim().startsWith("#")) {
////                    stdin.write(("passwd centos" + '\n').getBytes());
////                }
//
//                if(line.trim().equals("Changing password for user centos.")) {
//                    System.out.println("df");
////                    stdin.write(("test5"+'\n').getBytes());
////                    stdin.write(("test5"+'\n').getBytes());
//                }
//
//                if(line.trim().equals("New password:") || line.trim().equals("Retype new password:"))
//                    stdin.write(("test5" + '\n').getBytes());
//                stdout.append(line + "\n");
//            }
//
//            session.getExitSignal();
//            session.close();



        }
        finally {
            connection.close();
        }

        Map<String,String> outputMessages= new HashMap<>();
        outputMessages.put("stdout",stdout.toString());
        outputMessages.put("stderr",stderr.toString());

        return outputMessages;

    }

    int x, y;

    private void addText(byte[] data, int len)
    {
        char[][] lines = new char[y][];
        int posy = 0;
        int posx = 0;
        for (int i = 0; i < len; i++)
        {
            char c = (char) (data[i] & 0xff);

            if (c == 8) // Backspace, VERASE
            {
                if (posx < 0)
                    continue;
                posx--;
                continue;
            }

            if (c == '\r')
            {
                posx = 0;
                continue;
            }

            if (c == '\n')
            {
                posy++;
                if (posy >= y)
                {
                    for (int k = 1; k < y; k++)
                        lines[k - 1] = lines[k];
                    posy--;
                    lines[y - 1] = new char[x];
                    for (int k = 0; k < x; k++)
                        lines[y - 1][k] = ' ';
                }
                continue;
            }

            if (c < 32)
            {
                continue;
            }

            if (posx >= x)
            {
                posx = 0;
                posy++;
                if (posy >= y)
                {
                    posy--;
                    for (int k = 1; k < y; k++)
                        lines[k - 1] = lines[k];
                    lines[y - 1] = new char[x];
                    for (int k = 0; k < x; k++)
                        lines[y - 1][k] = ' ';
                }
            }

            if (lines[posy] == null)
            {
                lines[posy] = new char[x];
                for (int k = 0; k < x; k++)
                    lines[posy][k] = ' ';
            }

            lines[posy][posx] = c;
            posx++;
        }

        StringBuffer sb = new StringBuffer(x * y);

        for (int i = 0; i < lines.length; i++)
        {
            if (i != 0)
                sb.append('\n');

            if (lines[i] != null)
            {
                sb.append(lines[i]);
            }

        }

    }


    private Map executeCommand (String command) throws Exception {

        StringBuilder stdout = new StringBuilder();
        StringBuilder stderr = new StringBuilder();
        Connection connection = new Connection(currentServerHostname);
        try {

            logger.info("starting connection with " + currentServerHostname);

            logger.info("connection object created..");

            connection.connect();

            connection.authenticateWithPassword(currentServerUser, currentServerPass);

            Session session = connection.openSession();

            InputStream stdoutIs = new StreamGobbler(session.getStdout());

            BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(stdoutIs));

            logger.info("connected");
            System.out.println("Connected. Sending command " + command);
            session.execCommand(command);// + " && sleep 5");

            InputStream stderrIs = new StreamGobbler(session.getStderr());

            BufferedReader stderrReader = new BufferedReader(new InputStreamReader(stderrIs));

            TimeLimiter limiter = SimpleTimeLimiter.create(Executors.newSingleThreadExecutor());
            while (true) {
                //limiter.callWithTimeout(stderrReader::readLine,20,TimeUnit.SECONDS);
                String line = stderrReader.readLine();
                if (line == null)
                    break;
                stderr.append(line + "\n");
            }

            while (true) {
                String line = stdoutReader.readLine();
                if (line == null)
                    break;
                stdout.append(line + "\n");
            }



        }
        finally {
            connection.close();
        }

        Map<String,String> outputMessages= new HashMap<>();
        outputMessages.put("stdout",stdout.toString());
        outputMessages.put("stderr",stderr.toString());

        return outputMessages;

    }




    private Map createUser( User user) throws Exception{
        MustacheFactory mf = new DefaultMustacheFactory();
        String fileName = "createUser.mst" ;
        fileName = this.getClass().getClassLoader().getResource("createUser.mst").getFile();
        Mustache m = mf.compile(fileName);

        StringWriter writer = new StringWriter();
        m.execute(writer,user).flush();
        String command = writer.toString();
        System.out.println("Command to create user:" + command);


        return executeCommandWithSCP(command);

    }

    private Map changePassword(String userId, String newPassword) throws Exception{
        Credentials creds = new Credentials(userId, newPassword);
        MustacheFactory mf = new DefaultMustacheFactory();
        String fileName = "changePasswd.mst" ;
        fileName = this.getClass().getClassLoader().getResource("changePasswd.mst").getFile();
        Mustache m = mf.compile(fileName);

        StringWriter writer = new StringWriter();
        m.execute(writer,creds).flush();
        String command = writer.toString();
        System.out.println("Command to create user:" + command);

        return executeCommand(command);
//        return executeCommandWithSCP(command);
    }


    private Map executeCommandWithSCP (String command) throws IOException {

        StringBuilder stdout = new StringBuilder();
        StringBuilder stderr = new StringBuilder();
        Connection connection = new Connection(currentServerHostname);
        SCPOutputStream outputStream = null;
        try {

            logger.info("starting connection with " + currentServerHostname);

            logger.info("connection object created..");

            connection.connect();

            connection.authenticateWithPassword(currentServerUser, currentServerPass);

            SCPClient scpClient = new SCPClient(connection);

            outputStream = scpClient.put("command.sh",command.length(),"/tmp/abcd", "0700");

            outputStream.write(command.getBytes());

            outputStream.close();

            String commandToBeExecuted = "/tmp/abcd/command.sh";

            Session session = connection.openSession();

            InputStream stdoutIs = new StreamGobbler(session.getStdout());

            BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(stdoutIs));

            logger.info("connected");
            System.out.println("Connected. Sending command " + commandToBeExecuted);
            session.execCommand(commandToBeExecuted);// + " && sleep 5");

            while (true) {
                String line = stdoutReader.readLine();
                if (line == null)
                    break;
                stdout.append(line + "\n");
            }

            InputStream stderrIs = new StreamGobbler(session.getStderr());

            BufferedReader stderrReader = new BufferedReader(new InputStreamReader(stderrIs));

            while (true) {
                String line = stderrReader.readLine();
                if (line == null)
                    break;
                stderr.append(line + "\n");
            }

        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            connection.close();
        }

        Map<String,String> outputMessages= new HashMap<>();
        outputMessages.put("stdout",stdout.toString());
        outputMessages.put("stderr",stderr.toString());

        return outputMessages;

    }


}
