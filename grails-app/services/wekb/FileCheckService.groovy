package wekb

import grails.gorm.transactions.Transactional

import java.security.MessageDigest

@Transactional
class FileCheckService {

    boolean hasFileChanged(KbartSource kbartSource, File file) {
        String currentHash = computeHash(file)
        String lastHash = kbartSource.kbartFileHash

        if (lastHash == null) {
            lastHash = currentHash
            return true
        }

        boolean changed = (currentHash != lastHash)
        lastHash = currentHash
        return changed
    }

    String computeHash(File file) {

        MessageDigest digest = MessageDigest.getInstance("SHA-512")

        file.withInputStream { is ->
            byte[] buffer = new byte[16384]
            int bytesRead
            while ((bytesRead = is.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead)
            }
        }

        return digest.digest().encodeHex().toString()
    }
}
