package wekb

import grails.gorm.transactions.Transactional
import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.ftp.FTPConnectionClosedException
import org.apache.commons.net.ftp.FTPFile
import org.apache.commons.net.ftp.FTPReply

import java.nio.file.Files
import java.nio.file.Path

@Transactional
class FtpConnectService {

    boolean ftpConnectAndSaveFile(String server, String username, String password, String remoteDir, String localDir) {
        boolean success = false
        int port = 0
        List<String> parts = server.split(":")
        if (parts.size() == 2) {
            server = parts[0]
            port = Integer.parseInt(parts[1])
        }
        FTPClient ftp = new FTPClient()

        try {
            final int reply
            if (port > 0) {
                ftp.connect(server, port)
            } else {
                ftp.connect(server)
            }
            log.info("Connected to " + server + " on " + (port > 0 ? port : ftp.getDefaultPort()))

            reply = ftp.getReplyCode()

            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect()
                log.error("FTP server refused connection.")
            }

        } catch (final IOException e) {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect()
                } catch (final IOException f) {
                    // do nothing
                }
            }
            log.error("Could not connect to server.")
            e.printStackTrace()
        }
        try {
            if (!ftp.login(username, password)) {
                ftp.logout()
                return null
            }

            log.info("Remote system is " + ftp.getSystemType())

            if (false) {
                ftp.setFileType(FTP.BINARY_FILE_TYPE)
            } else {
                ftp.setFileType(FTP.ASCII_FILE_TYPE)
            }

            //if (localActive) {
            //    ftp.enterLocalActiveMode()
            //} else {
            ftp.enterLocalPassiveMode()
            //}

            try (final OutputStream output = new FileOutputStream(localDir)) {
                success = ftp.retrieveFile(remoteDir, output)
            }

            ftp.noop() // check that control connection is working OK

            ftp.logout()
        } catch (final FTPConnectionClosedException e) {
            log.error("Server closed connection.")
            e.printStackTrace()
        } catch (final IOException e) {
            e.printStackTrace()
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect()
                } catch (final IOException f) {
                    // do nothing
                }
            }
        }

        log.info("ftpConnectAndSaveFile is "+ success ? 'success!': 'fail!' )

        return success

    }

    File ftpConnectAndGetFile(KbartSource kbartSource, UpdatePackageInfo updatePackageInfo) {
        File file
        String server = kbartSource.ftpServerUrl
        String username = kbartSource.ftpUsername
        String password = kbartSource.ftpPassword

        String remoteDir = kbartSource.ftpDirectory
        if(remoteDir.endsWith("/")){
            remoteDir =remoteDir+kbartSource.ftpFileName
        }else{
            remoteDir =remoteDir+"/"+kbartSource.ftpFileName
        }

        String localDir = "/tmp/wekb/kbartExportFTP"

        File folder = new File("${localDir}")
        if (!folder.exists()) {
            folder.mkdirs()
        }

        boolean success
        int port = 0
        List<String> parts = server.split(":")
        if (parts.size() == 2) {
            server = parts[0]
            port = Integer.parseInt(parts[1])
        }
        FTPClient ftp = new FTPClient()

        try {
            final int reply
            if (port > 0) {
                ftp.connect(server, port)
            } else {
                ftp.connect(server)
            }
            log.info("Connected to " + server + " on " + (port > 0 ? port : ftp.getDefaultPort()))

            reply = ftp.getReplyCode()

            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect()
                log.error("FTP server refused connection.")
            }

        } catch (final IOException e) {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect()
                } catch (final IOException f) {
                    // do nothing
                }
            }
            log.error("Could not connect to server.")
            e.printStackTrace()
        }
        try {
            if (!ftp.login(username, password)) {
                ftp.logout()
                return null
            }

            log.info("Remote system is " + ftp.getSystemType())

            if (false) {
                ftp.setFileType(FTP.BINARY_FILE_TYPE)
            } else {
                ftp.setFileType(FTP.ASCII_FILE_TYPE)
            }

            //if (localActive) {
            //    ftp.enterLocalActiveMode()
            //} else {
            ftp.enterLocalPassiveMode()
            //}

            try (final OutputStream output = new FileOutputStream("${localDir}/${kbartSource.uuid}")) {
                success = ftp.retrieveFile(remoteDir, output)

                if(success) {
                    file = new File("${localDir}/${kbartSource.uuid}")
                }
            }

            ftp.noop() // check that control connection is working OK

            ftp.logout()
        } catch (final FTPConnectionClosedException e) {
            log.error("Server closed connection.")
            e.printStackTrace()
        } catch (final IOException e) {
            e.printStackTrace()
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect()
                } catch (final IOException f) {
                    // do nothing
                }
            }
        }

        log.info("ftpConnectAndGetFile is "+ success ? 'success!': 'fail!' )

        return file

    }

    void ftpConnectionTest(String server, String username, String pw){

        ftpConnectAndSaveFile(server, username, pw, '/kbart/eon-kbart2.txt', '/tmp/test2.txt')

    }
}
