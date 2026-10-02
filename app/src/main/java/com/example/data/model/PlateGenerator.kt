package com.example.data.model

object PlateGenerator {
    private val letters = ('A'..'Z').toList()

    /**
     * Génère une plaque d'immatriculation de véhicule ivoirienne réaliste (ex: 7421-HJ-01)
     * Format officiel : 4 chiffres - 2 lettres - 01 (Région des Lagunes / Dabou)
     */
    fun generateVehiclePlate(): String {
        val num = (1000..9999).random()
        val l1 = letters.random()
        val l2 = letters.random()
        return "$num-$l1$l2-01"
    }

    /**
     * Génère une plaque d'immatriculation chauffeur officielle Allô Dabou (ex: DABOU-5832)
     * ou au format national préfectoral (ex: CI-DAB-8412)
     */
    fun generateDriverBadge(prefix: String = "DABOU"): String {
        val num = (1000..9999).random()
        return "$prefix-$num"
    }

    fun generateDriverPlateCI(): String {
        val num = (1000..9999).random()
        return "CI-DAB-$num"
    }
}
