package au.id.tmm.senatedb.core.rawdata.download

import au.id.tmm.utilities.hashing.Digest

class DataIntegrityException(message: String, expectedDigest: Digest, actualDigest: Digest)
  extends RuntimeException(s"$message. expected=${expectedDigest.asHexString}, actual=${actualDigest.asHexString}") {
}
