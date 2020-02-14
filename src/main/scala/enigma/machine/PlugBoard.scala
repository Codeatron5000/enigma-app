package enigma.machine

/**
 * The plug board contains up to 10 connections that scramble any letter with
 * any other letter.
 */
class PlugBoard(private var connectionSeq: Seq[(Char, Char)]) {
    /**
     * The plug board cannot have more than 10 connections and no connections
     * can overlap with other connections
     */
    private def validateConnections(connections: Seq[(Char, Char)]): Unit = {
        if (connections.size > 10) {
            throw new PlugBoardOverloadException
        }

        val connectionSet = connections.flatMap(c => Seq(c._1, c._2)).toSet
        if (connectionSet.size != connections.length * 2) {
            throw new PlugBoardConflictException
        }
    }

    def connections: Seq[(Char, Char)] = connectionSeq

    /**
     * Build a map of all the substitutions going in one direction through each
     * connection.
     */
    private def forwardSubstitutions = connections.toMap

    /**
     * Build the reverse map of the forward substitutions.
     */
    private def reverseSubstitutions = {
        for ((k, v) <- forwardSubstitutions) yield (v, k)
    }

    /**
     * Combine the two maps to give a full substitution map of all the possible
     * connections.
     */
    private def subMap: Map[Char, Char] = {
        forwardSubstitutions ++ reverseSubstitutions
    }

    /**
     * Add a connection to the list of connections, validating before it is
     * added.
     */
    def addConnection(connection: (Char, Char)): Unit = {
        val newConnections = connections :+ connection
        validateConnections(newConnections)
        connectionSeq = newConnections
    }

    /**
     * Remove a connection between two letters if it exists.
     * There is no need to validate as any connections can be removed.
     */
    def removeConnection(connection: (Char, Char)): Unit = {
        connectionSeq = connectionSeq.filterNot(_ == connection)
    }

    /**
     * Encode a letter through the plug board by sending it through the
     * substitution map. If there is no substitution for the letter it encodes
     * to itself.
     */
    def encode(letter: Char): Char = subMap.getOrElse(letter, letter)

    /**
     * Validate the connections on first initialising the plug board.
     */
    validateConnections(connectionSeq)
}
